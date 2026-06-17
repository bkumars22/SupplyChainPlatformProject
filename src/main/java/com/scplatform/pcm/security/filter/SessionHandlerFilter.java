/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.security.filter;

import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.service.UserSessionService;
import com.scplatform.pcm.user.util.ClientInfoUtil;
import com.scplatform.pcm.authentication.service.AppContextService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Session Handler Filter
 * 
 * <p>This filter handles session-related operations for each request:
 * <ul>
 *   <li>Concurrent login detection and handling</li>
 *   <li>Session tracking and last access time updates</li>
 *   <li>Punch-in path handling (saving redirect paths in session)</li>
 *   <li>Request logging and monitoring</li>
 * </ul>
 * 
 * <p>Configuration via database (using PcmConfigUtil):
 * <ul>
 *   <li>{@code security.session.handler.enabled} - enable/disable filter (default: true)</li>
 *   <li>{@code security.session.handler.ignore-patterns} - semicolon-separated regex patterns to ignore</li>
 *   <li>{@code security.session.concurrent-login.enabled} - enable concurrent login check (default: true)</li>
 * </ul>
 * 
 * @author PCM Security Team
 * @version 1.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
public class SessionHandlerFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SessionHandlerFilter.class);

    private static final String PUNCHIN_REFERENCE = "=pin";
    private static final String SESSION_ATTR_PUNCHIN_PATH = "punchInPath";
    private static final String SESSION_ATTR_PUNCHIN_QUERY = "punchInQuery";
    private static final String PARAM_ACTION_TYPE = "actionType";

    private static final String CONFIG_ENABLED = "security.session.handler.enabled";
    private static final String CONFIG_CONCURRENT_LOGIN_ENABLED = "security.session.concurrent-login.enabled";
    
    private static final String REDIRECT_AUTHENTICATE = "authenticate";
    private static final String FORWARD_SESSION_INVALIDATE = "sessionInvalidate";
    private static final String FORWARD_CANCEL_RELOGIN = "cancleRelogin";

    /**
     * URL patterns to ignore (static resources, etc.)
     */
    private static final Set<String> DEFAULT_IGNORE_EXTENSIONS = Set.of(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".woff", ".woff2", ".ttf", ".svg"
    );

    private final UserSessionService userSessionService;
    private final PcmConfigUtil configUtil;

    // These can be overridden via setters for testing
    private Boolean enabledOverride;
    private Boolean concurrentLoginCheckEnabledOverride;
    private String ignorePatternConfigOverride;

    @Value("${security.session.handler.ignore-patterns:}")
    private String ignorePatternConfigProperty;

    private Pattern[] ignorePaths;

    public SessionHandlerFilter(UserSessionService userSessionService, PcmConfigUtil configUtil) {
        this.userSessionService = userSessionService;
        this.configUtil = configUtil;
        logger.info("Session Handler Filter initialized");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        logger.debug("Processing request: {} on thread: {}", uri, Thread.currentThread().getName());

        // Handle punch-in path storage (always runs, even for ignored paths)
        handlePunchInPath(request, uri, queryString);

        // Skip session check for ignored paths
        if (ignorePath(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Handle session validation and concurrent login check
        if (isConcurrentLoginCheckEnabled()) {
            try {
                if (!handleSessionCheck(request, response)) {
                    // Session check failed (redirect/forward already handled)
                    return;
                }
            } catch (InvalidUserContext e) {
                logger.debug("Invalid user context: {}, redirecting to authenticate", e.getMessage());
                response.sendRedirect(REDIRECT_AUTHENTICATE);
                return;
            }
        }

        try {
            // Continue with filter chain
            filterChain.doFilter(request, response);
        } finally {
            logger.debug("Completed request: {} on thread: {}", uri, Thread.currentThread().getName());
        }
    }

    /**
     * Handle session check for concurrent login detection.
     * 
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return true if request should continue, false if redirected/forwarded
     * @throws IOException         if redirect/forward fails
     * @throws ServletException    if forward fails
     * @throws InvalidUserContext  if no valid user context in session
     */
    private boolean handleSessionCheck(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, InvalidUserContext {

        // Get valid application context (throws InvalidUserContext if not found)
        ApplicationContext context = AppContextHelper.getValidContext(request);
        String currentUserId = context.getCurrentUser().getUserId();

        String clientUniqueId = ClientInfoUtil.getClientUniqueId(request);
        String actionType = request.getParameter(PARAM_ACTION_TYPE);

        UserSessionService.SessionCheckResult result = 
                userSessionService.checkAndManageSession(currentUserId, clientUniqueId, actionType);

        switch (result) {
            case VALID:
            case NEW_SESSION_CREATED:
            case SESSION_EXPIRED_RECREATED:
            case CONCURRENT_LOGIN_CONTINUED:
                // Session is valid, continue processing
                return true;

            case CONCURRENT_LOGIN_CANCELLED:
                // User cancelled - forward to cancel page
                request.getRequestDispatcher(FORWARD_CANCEL_RELOGIN).forward(request, response);
                return false;

            case CONCURRENT_LOGIN_DETECTED:
                // Concurrent login detected - forward to session invalidate page
                request.getRequestDispatcher(FORWARD_SESSION_INVALIDATE).forward(request, response);
                return false;

            default:
                return true;
        }
    }

    /**
     * Handle punch-in path saving for authentication redirects.
     * 
     * <p>When a request contains a punch-in reference (=pin), the path and query
     * are saved in the session so they can be restored after authentication.
     * 
     * @param request     the HTTP request
     * @param uri         the request URI
     * @param queryString the query string
     */
    private void handlePunchInPath(HttpServletRequest request, String uri, String queryString) {
        if (queryString != null && queryString.contains(PUNCHIN_REFERENCE)) {
            String contextPath = request.getContextPath();
            String relativePath = uri;
            if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
                relativePath = uri.substring(contextPath.length());
            }

            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_ATTR_PUNCHIN_PATH, relativePath);
            session.setAttribute(SESSION_ATTR_PUNCHIN_QUERY, queryString);

            logger.debug("Saved punch-in path: {} with query: {}", relativePath, queryString);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!isEnabled()) {
            return true;
        }

        String uri = request.getRequestURI();
        if (uri == null || uri.isEmpty()) {
            return true;
        }

        // Check default ignore extensions (static resources)
        for (String ext : DEFAULT_IGNORE_EXTENSIONS) {
            if (uri.endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Initialize ignore patterns from configuration.
     * Called after properties are set.
     */
    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        String patterns = getIgnorePatternConfig();
        if (patterns != null && !patterns.isBlank()) {
            initializePatterns(patterns);
        }
    }

    /**
     * Initialize regex patterns from semicolon-separated config string.
     * 
     * @param filter semicolon-separated regex patterns
     */
    private void initializePatterns(String filter) {
        if (filter == null || filter.isBlank()) {
            return;
        }

        String[] paths = filter.split(";");
        ignorePaths = new Pattern[paths.length];

        for (int idx = 0; idx < paths.length; idx++) {
            String pattern = paths[idx].trim();
            if (!pattern.isEmpty()) {
                try {
                    ignorePaths[idx] = Pattern.compile(pattern);
                    logger.debug("Added ignore pattern: {}", pattern);
                } catch (PatternSyntaxException pse) {
                    logger.error("ignorePattern contains invalid expression: {}", pattern, pse);
                }
            }
        }
    }

    /**
     * Check if the URL should be ignored based on configured patterns.
     * 
     * @param url the URL to check
     * @return true if the URL matches any ignore pattern
     */
    protected boolean ignorePath(String url) {
        if (ignorePaths != null) {
            for (Pattern pattern : ignorePaths) {
                if (pattern == null) {
                    continue;
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Testing URL: {} against pattern: {}", url, pattern.pattern());
                }
                if (pattern.matcher(url).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    // Configuration getters (from database or override)

    private boolean isEnabled() {
        if (enabledOverride != null) {
            return enabledOverride;
        }
        return configUtil.getBooleanValue(CONFIG_ENABLED, true);
    }

    private boolean isConcurrentLoginCheckEnabled() {
        if (concurrentLoginCheckEnabledOverride != null) {
            return concurrentLoginCheckEnabledOverride;
        }
        return configUtil.getBooleanValue(CONFIG_CONCURRENT_LOGIN_ENABLED, true);
    }

    private String getIgnorePatternConfig() {
        if (ignorePatternConfigOverride != null) {
            return ignorePatternConfigOverride;
        }
        return ignorePatternConfigProperty;
    }

    // Setters for testing
    public void setEnabled(boolean enabled) {
        this.enabledOverride = enabled;
    }

    public void setConcurrentLoginCheckEnabled(boolean enabled) {
        this.concurrentLoginCheckEnabledOverride = enabled;
    }

    public void setIgnorePatternConfig(String ignorePatternConfig) {
        this.ignorePatternConfigOverride = ignorePatternConfig;
        initializePatterns(ignorePatternConfig);
    }
}
