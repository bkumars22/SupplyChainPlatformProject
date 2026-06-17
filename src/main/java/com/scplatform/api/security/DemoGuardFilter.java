/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Blocks write (POST/PUT/DELETE/PATCH) operations for users with ROLE_DEMO.
 * Also blocks access to /api/admin/** entirely for demo users.
 * Applied only to /api/** requests (shouldNotFilter guards the rest).
 */
@Component
public class DemoGuardFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(DemoGuardFilter.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "DELETE", "PATCH");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().contains("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (isDemoUser()) {
            String method = request.getMethod().toUpperCase();
            String uri    = request.getRequestURI();

            boolean isWriteMethod = WRITE_METHODS.contains(method);
            boolean isAdminPath   = uri.contains("/api/admin/");
            boolean isUserWrite   = uri.contains("/api/users/") && !method.equals("GET");
            // Password change endpoint is blocked for demo user
            boolean isPasswordChange = uri.contains("/api/auth/password") || uri.contains("/api/users/password");

            if (isWriteMethod || isAdminPath || isUserWrite || isPasswordChange) {
                log.info("[DEMO] Blocked {} {} for demo user", method, uri);
                response.setStatus(403);
                response.setContentType("application/json");
                mapper.writeValue(response.getWriter(), Map.of(
                    "error",     "Not available in demo mode",
                    "code",      "DEMO_RESTRICTED",
                    "timestamp", Instant.now().toString()
                ));
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isDemoUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(a -> a.equals("ROLE_DEMO"));
    }
}
