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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Rejects JSON request bodies over 1 MB on write endpoints.
 * Returns HTTP 413 with a clean JSON error — no stack trace.
 */
@Component
public class ContentSizeLimitFilter extends OncePerRequestFilter {

    private static final long MAX_JSON_BYTES = 1024 * 1024L; // 1 MB
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH");
    private static final Set<String> SIZE_LIMITED_PREFIXES = Set.of(
        "/api/items", "/api/suppliers", "/api/users",
        "/api/cost-records", "/api/bom", "/api/auth"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase();
        if (!WRITE_METHODS.contains(method)) return true;
        String uri = request.getRequestURI();
        return SIZE_LIMITED_PREFIXES.stream().noneMatch(uri::contains);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long contentLength = request.getContentLengthLong();
        if (contentLength > MAX_JSON_BYTES) {
            response.setStatus(413);
            response.setContentType("application/json");
            mapper.writeValue(response.getWriter(), Map.of(
                "error",     "Request body too large — maximum 1 MB allowed",
                "code",      "PAYLOAD_TOO_LARGE",
                "timestamp", Instant.now().toString()
            ));
            return;
        }
        chain.doFilter(request, response);
    }
}
