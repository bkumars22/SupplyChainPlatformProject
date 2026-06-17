/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.security.filter;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Populates SLF4J MDC with per-request context so every log line automatically
 * carries tracing information without changing any existing log statements.
 *
 * <p>MDC keys populated:</p>
 * <ul>
 *   <li>{@code requestId} — correlation UUID from {@code X-Request-ID} header or auto-generated</li>
 *   <li>{@code userId} — from session's ApplicationContext</li>
 *   <li>{@code roleId} — from session's ApplicationContext</li>
 *   <li>{@code method} — HTTP method (GET, POST, etc.)</li>
 *   <li>{@code uri} — request URI</li>
 * </ul>
 *
 * <p>Runs after security filters (order = HIGHEST_PRECEDENCE + 5) so the session
 * is already established by SessionHandlerFilter. All controller/service/repository
 * logs will have MDC populated. Cleared in {@code finally} to prevent thread leaks.</p>
 *
 * <p>Configuration (application.properties):</p>
 * <pre>
 *   logging.mdc.enabled=true              # master switch (default: true)
 *   logging.mdc.correlation-header=X-Request-ID  # header to read (default: X-Request-ID)
 * </pre>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class MdcLoggingFilter extends OncePerRequestFilter {

    @Value("${logging.mdc.enabled:true}")
    private boolean enabled;

    @Value("${logging.mdc.correlation-header:X-Request-ID}")
    private String correlationHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            populateMdc(request, response);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private void populateMdc(HttpServletRequest request, HttpServletResponse response) {
        // 1. Request ID — from header or generate
        String requestId = request.getHeader(correlationHeader);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put("requestId", requestId);
        response.setHeader(correlationHeader, requestId);

        // 2. HTTP method & URI
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        // 3. User context from session (safe — no exception on anonymous requests)
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object attr = session.getAttribute(ApplicationContext.SESSION_ATTR_NAME);
            if (attr instanceof ApplicationContext ac) {
                if (ac.getCurrentUser() != null && ac.getCurrentUser().getUserId() != null) {
                    MDC.put("userId", ac.getCurrentUser().getUserId());
                }
                if (ac.getCurrentRole() != null && ac.getCurrentRole().getRoleId() != null) {
                    MDC.put("roleId", ac.getCurrentRole().getRoleId());
                }
            }
        }
    }
}
