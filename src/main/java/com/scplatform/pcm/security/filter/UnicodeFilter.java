/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Unicode/Character Encoding Filter
 * 
 * This filter ensures all requests use a consistent character encoding (default: UTF-8).
 * It sets the character encoding on both the request and response.
 * 
 * Only applies to specific URL patterns:
 * - /saveExceptionRequest.do
 * - /downloadODMEmail.do
 * - /processExceptionRequest
 * - /submitExceptionRequest
 * 
 * Configuration via application.properties:
 * <pre>
 * security.encoding.charset=UTF-8
 * security.encoding.enabled=true
 * </pre>
 * 
 * @author PCM Security Team
 * @version 1.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class UnicodeFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(UnicodeFilter.class);

    private static final Set<String> URL_PATTERNS = Set.of(
            "/saveExceptionRequest.do",
            "/downloadODMEmail.do",
            "/processExceptionRequest",
            "/submitExceptionRequest"
    );

    @Value("${security.encoding.charset:UTF-8}")
    private String encoding;

    @Value("${security.encoding.enabled:true}")
    private boolean enabled;

    public UnicodeFilter() {
        logger.info("Unicode Filter initialized");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Set character encoding on request
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(encoding);
            logger.trace("Set request character encoding to: {}", encoding);
        }

        // Set character encoding on response
        response.setCharacterEncoding(encoding);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!enabled) {
            return true;
        }
        // Only filter requests matching specified URL patterns
        String requestUri = request.getRequestURI();
        if (requestUri == null) {
            return true;
        }
        // Check if URI ends with any of the patterns
        return URL_PATTERNS.stream().noneMatch(requestUri::endsWith);
    }

    // Getters and setters for testing
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
