/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.authentication.controller;

import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.user.service.UserSessionService;
import com.scplatform.pcm.user.util.ClientInfoUtil;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class AuthenticateController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticateController.class);

    private static final String VIEW_MAIN = "main";
    private static final String VIEW_TERMS = "terms";
    private static final String VIEW_LOGIN = "login";
    private static final String VIEW_ASK_RELOGIN = "askUserReLogin";
    private static final String REDIRECT_PORTAL = "redirect:../common/exit";

    private static final String TERM_ACK_SESSION_ATTR = "com.scplatform.pcm.web.termAck";

    private final UsersRepository usersRepository;
    private final UserSessionService userSessionService;
    private final PcmConfigUtil configUtil;

    public AuthenticateController(UsersRepository usersRepository,
                                  UserSessionService userSessionService,
                                  PcmConfigUtil configUtil) {
        this.usersRepository = usersRepository;
        this.userSessionService = userSessionService;
        this.configUtil = configUtil;
    }

    /**
     * Main authenticate action.
     * Validates user, sets up session context, handles concurrent login, and forwards to main page.
     */
    @RequestMapping("/authenticate")
    @Transactional
    public String authenticate(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value = "username", required = false) String usernameParam,
                               @RequestParam(value = "url", required = false) String paramURL,
                               @RequestParam(value = "exceptionId", required = false) String exceptionId,
                               @RequestParam(value = "actionType", required = false) String actionType) throws Exception {

        boolean viaPortal = (request.getHeader("iv-user") != null);
        HttpSession session = request.getSession(true);

        // Set session timeout from DB config
        int sessionExpireLimit = getSessionTimeoutMinutes();
        session.setMaxInactiveInterval(sessionExpireLimit * 60);

        // Set environment attribute if not already set
        if (session.getAttribute("environment") == null) {
            session.setAttribute("environment", System.getProperty("e2.env.subtype"));
        }


        Users user = null;
        UserSessionService.SessionCheckResult sessionCheckResult = null;

        try {
            // Try to get existing valid context
            ApplicationContext ac = AppContextHelper.getValidContext(request);
            user = usersRepository.findById(ac.getCurrentUser().getUserKey()).orElse(null);
            if (user != null) {
                AppContextHelper.setupSessionContext(user, request);
                sessionCheckResult = checkSessionState(user.getUserId(), request);
            }
            logger.debug("Session ID: {}, isNew: {}", session.getId(), session.isNew());
        } catch (InvalidUserContext iuc) {
            // No valid context - attempt to identify user
            String userid = request.getHeader("iv-user");
            if (userid == null) {
                userid = usernameParam;
            }
            if (userid == null) {
                logger.info("Login attempted without passing a userid from host {} address: {}",
                        request.getRemoteHost(), request.getRemoteAddr());
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, "error.invaliduser");
                return viaPortal ? REDIRECT_PORTAL : VIEW_LOGIN;
            }

            logger.info("Login attempted by user {} from host {} address: {}",
                    userid, request.getRemoteHost(), request.getRemoteAddr());

            List<Users> users = usersRepository.findAllByUserId(userid);
            if (users.size() == 1) {
                user = users.get(0);
            }
            if (user == null) {
                logger.info("Login attempted by unknown user: {} from host {} address: {}",
                        userid, request.getRemoteHost(), request.getRemoteAddr());
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, "error.invaliduser");
                return viaPortal ? REDIRECT_PORTAL : VIEW_LOGIN;
            }
            if (!user.getIsEnabled()) {
                logger.info("Login attempted by disabled user: {} from host {} address: {}",
                        userid, request.getRemoteHost(), request.getRemoteAddr());
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, "error.invaliduser");
                return viaPortal ? REDIRECT_PORTAL : VIEW_LOGIN;
            }

            // Update last access and setup session
            usersRepository.updateLastAccessDate(user.getUserKey(), new Timestamp(System.currentTimeMillis()));
            sessionCheckResult = checkSessionState(user.getUserId(), request);
            AppContextHelper.setupSessionContext(user, request);
            logger.info("Concurrent login check completed for user: {}", userid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Set homepage from user favorites
        if (session.getAttribute(ApplicationContext.SESSION_ATTR_NAME) != null) {
            ApplicationContext context = (ApplicationContext) session.getAttribute(ApplicationContext.SESSION_ATTR_NAME);
            user = context.getCurrentUser();
            String favString = user.getFavorites();
            if (favString != null) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> favWrap = objectMapper.readValue(favString, Map.class);
                    Object favList = favWrap.get("favorites");
                    if (favList instanceof List<?> list) {
                        List<String> accessibleFavorites = new ArrayList<>();
                        for (Object item : list) {
                            if (item instanceof Map<?, ?> fav) {
                                Object url = fav.get("url");
                                if (url instanceof String workflowUrl && !workflowUrl.isBlank()) {
                                    accessibleFavorites.add(workflowUrl);
                                }
                                Object isHome = fav.get("isHome");
                                if (Boolean.TRUE.equals(isHome)) {
                                    String homeUrl = (String) url;
                                    session.setAttribute("homepage", homeUrl);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Error parsing favorites for user: {}", user.getUserId(), e);
                }
            }
        }

        // Override homepage with URL parameter if provided
        if (paramURL != null) {
            String finalUrl = paramURL;
            if (exceptionId != null) {
                finalUrl = paramURL + "?exceptionId=" + exceptionId;
            }
            session.setAttribute("homepage", finalUrl);
        }

        // Handle session info creation / concurrent login
        if (user != null && sessionCheckResult != null) {
            switch (sessionCheckResult) {
                case CONCURRENT_LOGIN_DETECTED:
                    // Auto-continue: replace stale session with the current client's session
                    userSessionService.deleteSession(user.getUserId());
                    userSessionService.createSession(user.getUserId(), ClientInfoUtil.getClientUniqueId(request));
                    break;
                case CONCURRENT_LOGIN_CANCELLED:
                    return viaPortal ? REDIRECT_PORTAL : VIEW_LOGIN;
                default:
                    break;
            }
        }

        // Show terms page if configured
        if (showTermPage(request)) {
            return VIEW_TERMS;
        }

        return VIEW_MAIN;
    }

    /**
     * Acknowledge terms and conditions.
     */
    @RequestMapping("/ackTerms")
    public String ackTerms(HttpServletRequest request,
                           @RequestParam(value = "ackValue", required = false) String ackValue) {

        boolean viaPortal = (request.getHeader("iv-user") != null);

        try {
            ApplicationContext ac = AppContextHelper.getValidContext(request);
            Boolean termAck = (Boolean) request.getSession().getAttribute(TERM_ACK_SESSION_ATTR);

            // If ackValue is false, return to login
            if (ackValue != null && !Boolean.parseBoolean(ackValue)) {
                logger.info("User: {} responded to Terms Page with: {}", ac.getCurrentUser().getUserId(), ackValue);
                return viaPortal ? REDIRECT_PORTAL : VIEW_LOGIN;
            }

            // If ackValue not set and termAck not set, return to login
            if (ackValue == null && termAck == null) {
                logger.info("Unknown ackValue and termAck");
                return viaPortal ? REDIRECT_PORTAL : VIEW_LOGIN;
            }

            // Set terms acknowledgement if not already set
            if (termAck == null) {
                logger.info("User: {} responded to Terms Page with: {}", ac.getCurrentUser().getUserId(), ackValue);
                request.getSession().setAttribute(TERM_ACK_SESSION_ATTR, Boolean.TRUE);
            }
        } catch (InvalidUserContext iuc) {
            return viaPortal ? REDIRECT_PORTAL : VIEW_LOGIN;
        }

        HttpSession session = request.getSession();
        if (session != null) {
            // Check if a punch-in was in progress; resume it
            String path = (String) session.getAttribute("punchInPath");
            if (path != null) {
                String queryString = (String) session.getAttribute("punchInQuery");
                if (queryString != null) {
                    String punchInUrl = path + "?" + queryString;
                    logger.debug("punchIn forward path: {}", punchInUrl);
                    return "redirect:" + punchInUrl;
                }
            }
        }

        return VIEW_MAIN;
    }

    /**
     * Logout - invalidate session and cleanup.
     */
    @RequestMapping("/logout")
    @Transactional
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        boolean viaPortal = (request.getHeader("iv-user") != null);
        HttpSession session = request.getSession(false);

        if (session != null) {
            ApplicationContext context = (ApplicationContext) session.getAttribute(ApplicationContext.SESSION_ATTR_NAME);
            if (context != null && context.getCurrentUser() != null) {
                userSessionService.deleteSession(context.getCurrentUser().getUserId());
            }
            session.removeAttribute(ApplicationContext.SESSION_ATTR_NAME);
            session.invalidate();
        }

        if (viaPortal) {
            boolean launchpadEnabled = Boolean.parseBoolean(System.getProperty("mtcm.launchpad.enabled"));
            if (launchpadEnabled) {
                String clpUrl = System.getProperty("env.clp.url");
                return "redirect:" + clpUrl;
            }
            return REDIRECT_PORTAL;
        }
        return VIEW_LOGIN;
    }

    /**
     * Cancel re-login - clear session context and return to login.
     */
    @RequestMapping("/cancleRelogin")
    public String cancleRelogin(HttpServletRequest request) {
        boolean viaPortal = (request.getHeader("iv-user") != null);
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute(ApplicationContext.SESSION_ATTR_NAME);
            session.removeAttribute("userName");
        }

        if (viaPortal) {
            boolean launchpadEnabled = Boolean.parseBoolean(System.getProperty("mtcm.launchpad.enabled"));
            if (launchpadEnabled) {
                String clpUrl = System.getProperty("env.clp.url");
                return "redirect:" + clpUrl;
            }
            return REDIRECT_PORTAL;
        }
        return VIEW_LOGIN;
    }

    /**
     * Session expired handler - forward to ask re-login page.
     */
    @RequestMapping("/sessionInvalidate")
    public String doSessionExpired() {
        return VIEW_ASK_RELOGIN;
    }

    /**
     * Welcome page handler.
     * @return view name resolved to welcome.jsp
     */
    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    /**
     * Root path handler - redirects to authenticate if session exists, otherwise to login.
     */
    @RequestMapping({"/", "/index"})
    public String index(HttpServletRequest request) {
        try {
            AppContextHelper.getValidContext(request);
            return "redirect:authenticate";
        } catch (InvalidUserContext iuc) {
            return VIEW_LOGIN;
        }
    }

    // ======================== Helper methods ========================

    /**
     * Check session state using UserSessionService.
     */
    private UserSessionService.SessionCheckResult checkSessionState(String userId, HttpServletRequest request) {
        String clientUniqueId = ClientInfoUtil.getClientUniqueId(request);
        String actionType = request.getParameter("actionType");
        return userSessionService.checkAndManageSession(userId, clientUniqueId, actionType);
    }

    /**
     * Determine if Terms &amp; Conditions page should be shown.
     */
    private boolean showTermPage(HttpServletRequest request) {
        if (!configUtil.getBooleanValue("pcm.web.showTermPage", false)) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            Boolean ackValue = (Boolean) session.getAttribute(TERM_ACK_SESSION_ATTR);
            if (ackValue != null && ackValue) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get session timeout from DB config.
     */
    private int getSessionTimeoutMinutes() {
        try {
            String value = configUtil.getString("pcm.session.active.check.limit");
            if (value != null && !value.isEmpty()) {
                return Integer.parseInt(value);
            }
        } catch (Exception e) {
            logger.debug("Error reading session timeout config, using default: 10");
        }
        return 10;
    }
    
    
}
