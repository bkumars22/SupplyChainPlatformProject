/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Redirects HTTP to HTTPS in production (Railway) by inspecting X-Forwarded-Proto.
 * Railway terminates SSL and forwards requests with X-Forwarded-Proto: http for plain HTTP.
 * Only active when server.railway.https-redirect=true is set (prod profile).
 * Never applies in local dev (no Railway proxy → header is absent).
 */
@Component
public class HttpsRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String proto = request.getHeader("X-Forwarded-Proto");
        if ("http".equalsIgnoreCase(proto)) {
            String httpsUrl = "https://" + request.getServerName() + request.getRequestURI();
            String query = request.getQueryString();
            if (query != null) httpsUrl += "?" + query;
            response.sendRedirect(httpsUrl);
            return;
        }
        chain.doFilter(request, response);
    }
}
