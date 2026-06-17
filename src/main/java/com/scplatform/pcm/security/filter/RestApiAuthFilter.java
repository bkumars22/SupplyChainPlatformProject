/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * REST API Authentication Filter
 * 
 * This filter handles authentication for external REST API calls.
 * It validates the iv-user header for requests coming through a proxy (x-forwarded-host).
 * 
 * Configuration via PCM Configuration:
 * <pre>
 * scplatform.restapi.auth.enabled=true
 * </pre>
 * 
 * @author PCM Security Team
 * @version 1.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class RestApiAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RestApiAuthFilter.class);

    private static final String HEADER_X_FORWARDED_HOST = "x-forwarded-host";
    private static final String HEADER_IV_USER = "iv-user";
    private static final String CONFIG_KEY_AUTH_ENABLED = "scplatform.restapi.auth.enabled";
    private static final String URL_PATTERN = "/mcm/api/";

    private final PcmConfigUtil configUtil;
    private final UsersRepository usersRepository;

    public RestApiAuthFilter(PcmConfigUtil configUtil, UsersRepository usersRepository) {
        this.configUtil = configUtil;
        this.usersRepository = usersRepository;
        logger.info("REST API Auth Filter initialized");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Log all request headers
        request.getHeaderNames().asIterator()
                .forEachRemaining(header -> logger.info("{}: {}", header, request.getHeader(header)));

        String proxyHost = request.getHeader(HEADER_X_FORWARDED_HOST);
        boolean isExternalCall = false;
        if (proxyHost != null) {
            isExternalCall = true;
        }

        boolean isRestAuthEnabled = configUtil.getBooleanValue(CONFIG_KEY_AUTH_ENABLED, true);

        if (isExternalCall && isRestAuthEnabled) {
            String userName = request.getHeader(HEADER_IV_USER);
            String requestURI = request.getRequestURI();

            if (userName != null && !userName.isEmpty()) {
                Optional<Users> user = usersRepository.findByUserId(userName);

                if (user.isEmpty()) {
                    logger.error("Invalid iv-user {} for the requestURI={}", userName, requestURI);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                } else {
                    logger.info("IV-User logged in by User Name: {}", userName);
                    filterChain.doFilter(request, response);
                }
            } else {
                logger.error("Request header param iv-user not found for the requestURI={}", requestURI);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only filter requests matching /mcm/api/*
        String requestUri = request.getRequestURI();
        return requestUri == null || !requestUri.startsWith(URL_PATTERN);
    }
}
