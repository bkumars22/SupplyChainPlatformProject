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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * XSS (Cross-Site Scripting) Protection Filter
 * 
 * This filter intercepts all HTTP requests and sanitizes input parameters,
 * headers, and cookies to prevent XSS attacks.
 * 
 * The filter uses {@link OncePerRequestFilter} to ensure it executes exactly once per request,
 * even in cases of request dispatching (forwards, includes, etc.).
 * 
 * Features:
 * - Sanitizes all request parameters
 * - Optionally sanitizes request headers
 * - Optionally sanitizes cookie values
 * - Configurable via application properties
 * - Supports URL pattern exclusions
 * - Supports parameter exclusions
 * - High priority filter execution (runs early in filter chain)
 * 
 * @author PCM Security Team
 * @version 1.0
 * @see XssSanitizer
 * @see XssRequestWrapper
 * @see XssFilterProperties
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XssFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);

    private final XssSanitizer sanitizer;
    private final XssFilterProperties properties;

    // Cache of excluded URL patterns for performance
    private Set<String> excludedUrlPatterns;
    private Set<String> excludedParameters;
    private Set<String> excludedHeaders;

    public XssFilter(XssSanitizer sanitizer, XssFilterProperties properties) {
        this.sanitizer = sanitizer;
        this.properties = properties;
        initializeExclusions();
        logger.info("XSS Filter initialized. Enabled: {}, Sanitize Headers: {}, Sanitize Cookies: {}",
                properties.isEnabled(), properties.isSanitizeHeaders(), properties.isSanitizeCookies());
    }

    /**
     * Initializes the exclusion sets from properties.
     */
    private void initializeExclusions() {
        this.excludedUrlPatterns = properties.getExcludedUrlPatterns() != null 
                ? new HashSet<>(properties.getExcludedUrlPatterns()) 
                : new HashSet<>();
        this.excludedParameters = properties.getExcludedParameters() != null 
                ? new HashSet<>(properties.getExcludedParameters()) 
                : new HashSet<>();
        this.excludedHeaders = properties.getExcludedHeaders() != null 
                ? new HashSet<>(properties.getExcludedHeaders()) 
                : new HashSet<>();
                
        logger.debug("XSS Filter exclusions - URLs: {}, Parameters: {}, Headers: {}",
                excludedUrlPatterns.size(), excludedParameters.size(), excludedHeaders.size());
    }

    /**
     * Main filter logic that wraps the request with XSS sanitization capabilities.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Check if filter is enabled
        if (!properties.isEnabled()) {
            logger.trace("XSS Filter is disabled, passing request through");
            filterChain.doFilter(request, response);
            return;
        }

        // Check if URL should be excluded
        String requestUri = request.getRequestURI();
        if (isUrlExcluded(requestUri)) {
            logger.debug("URL '{}' is excluded from XSS filtering", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        // Check HTTP method exclusions
        String method = request.getMethod();
        if (isMethodExcluded(method)) {
            logger.trace("HTTP method '{}' is excluded from XSS filtering", method);
            filterChain.doFilter(request, response);
            return;
        }

        // Log request details at debug level
        if (logger.isDebugEnabled()) {
            logger.debug("Applying XSS filter to request: {} {}", method, requestUri);
        }

        // Wrap the request with XSS sanitization
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(
                request,
                sanitizer,
                properties.isSanitizeHeaders(),
                properties.isSanitizeCookies(),
                excludedParameters,
                excludedHeaders
        );

        // Add security headers to response
        addSecurityHeaders(response);

        // Continue with the filter chain using the wrapped request
        filterChain.doFilter(wrappedRequest, response);
    }

    /**
     * Determines if this filter should be skipped for the given request.
     * 
     * @param request the HTTP request
     * @return true if the filter should be skipped
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Additional check - can be used for async dispatch handling if needed
        return !properties.isEnabled();
    }

    /**
     * Checks if the given URL should be excluded from XSS filtering.
     * 
     * @param requestUri the request URI to check
     * @return true if the URL should be excluded
     */
    private boolean isUrlExcluded(String requestUri) {
        if (requestUri == null || excludedUrlPatterns.isEmpty()) {
            return false;
        }

        for (String pattern : excludedUrlPatterns) {
            if (matchesPattern(requestUri, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given HTTP method should be excluded from XSS filtering.
     * Default implementation filters GET, POST, PUT, PATCH requests.
     * 
     * @param method the HTTP method to check
     * @return true if the method should be excluded
     */
    private boolean isMethodExcluded(String method) {
        if (method == null) {
            return false;
        }
        
        Set<String> excludedMethods = properties.getExcludedMethods();
        if (excludedMethods != null && excludedMethods.contains(method.toUpperCase())) {
            return true;
        }
        
        // By default, only filter methods that typically contain user input
        // OPTIONS, HEAD, TRACE typically don't need XSS filtering
        return false;
    }

    /**
     * Matches a URL against a pattern.
     * Supports simple wildcard patterns with '*' and '**'.
     * 
     * @param url the URL to match
     * @param pattern the pattern to match against
     * @return true if the URL matches the pattern
     */
    private boolean matchesPattern(String url, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return false;
        }

        // Exact match
        if (pattern.equals(url)) {
            return true;
        }

        // Convert pattern to regex
        String regex = pattern
                .replace(".", "\\.")
                .replace("**", ".*")
                .replace("*", "[^/]*");

        try {
            return url.matches(regex);
        } catch (Exception e) {
            logger.warn("Invalid URL pattern: {}", pattern);
            return false;
        }
    }

    /**
     * Adds security-related headers to the response to provide additional XSS protection.
     * 
     * @param response the HTTP response
     */
    private void addSecurityHeaders(HttpServletResponse response) {
        if (!properties.isAddSecurityHeaders()) {
            return;
        }

        // X-XSS-Protection header (for older browsers)
        // Note: This header is deprecated in modern browsers but still useful for legacy support
        if (!response.containsHeader("X-XSS-Protection")) {
            response.setHeader("X-XSS-Protection", "1; mode=block");
        }

        // X-Content-Type-Options to prevent MIME type sniffing
        if (!response.containsHeader("X-Content-Type-Options")) {
            response.setHeader("X-Content-Type-Options", "nosniff");
        }

        // Content-Security-Policy for XSS prevention (basic policy)
        if (properties.isAddContentSecurityPolicy() && !response.containsHeader("Content-Security-Policy")) {
            String csp = properties.getContentSecurityPolicy();
            if (csp != null && !csp.isEmpty()) {
                response.setHeader("Content-Security-Policy", csp);
            }
        }

        // Referrer-Policy
        if (!response.containsHeader("Referrer-Policy")) {
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        }
    }

    /**
     * Returns the name of this filter for logging purposes.
     * 
     * @return the filter name
     */
    @Override
    protected String getFilterName() {
        return "XssProtectionFilter";
    }
}
