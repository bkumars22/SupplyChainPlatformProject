/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Custom HttpServletRequestWrapper that sanitizes request parameters, headers, and cookies
 * to prevent XSS (Cross-Site Scripting) attacks.
 * 
 * This wrapper intercepts all parameter access methods and sanitizes the values
 * before returning them to the application.
 * 
 * @author PCM Security Team
 * @version 1.0
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger logger = LoggerFactory.getLogger(XssRequestWrapper.class);

    private final XssSanitizer sanitizer;
    private final boolean sanitizeHeaders;
    private final boolean sanitizeCookies;
    private final Set<String> excludedParameters;
    private final Set<String> excludedHeaders;

    /**
     * Creates a new XssRequestWrapper with the specified sanitizer.
     * 
     * @param request the original HttpServletRequest to wrap
     * @param sanitizer the XSS sanitizer to use
     */
    public XssRequestWrapper(HttpServletRequest request, XssSanitizer sanitizer) {
        this(request, sanitizer, true, true, Collections.emptySet(), Collections.emptySet());
    }

    /**
     * Creates a new XssRequestWrapper with full configuration options.
     * 
     * @param request the original HttpServletRequest to wrap
     * @param sanitizer the XSS sanitizer to use
     * @param sanitizeHeaders whether to sanitize header values
     * @param sanitizeCookies whether to sanitize cookie values
     * @param excludedParameters parameter names to exclude from sanitization
     * @param excludedHeaders header names to exclude from sanitization
     */
    public XssRequestWrapper(HttpServletRequest request, XssSanitizer sanitizer,
                             boolean sanitizeHeaders, boolean sanitizeCookies,
                             Set<String> excludedParameters, Set<String> excludedHeaders) {
        super(request);
        this.sanitizer = sanitizer;
        this.sanitizeHeaders = sanitizeHeaders;
        this.sanitizeCookies = sanitizeCookies;
        this.excludedParameters = excludedParameters != null ? excludedParameters : Collections.emptySet();
        this.excludedHeaders = excludedHeaders != null ? excludedHeaders : Collections.emptySet();
    }

    /**
     * Returns a sanitized parameter value for the given parameter name.
     * 
     * @param name the parameter name
     * @return the sanitized parameter value, or null if the parameter doesn't exist
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        
        if (isParameterExcluded(name)) {
            logger.debug("Parameter '{}' is excluded from XSS sanitization", name);
            return value;
        }
        
        return sanitizer.sanitize(value);
    }

    /**
     * Returns sanitized parameter values for the given parameter name.
     * 
     * @param name the parameter name
     * @return array of sanitized parameter values, or null if the parameter doesn't exist
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }

        if (isParameterExcluded(name)) {
            logger.debug("Parameter '{}' is excluded from XSS sanitization", name);
            return values;
        }

        String[] sanitizedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            sanitizedValues[i] = sanitizer.sanitize(values[i]);
        }
        return sanitizedValues;
    }

    /**
     * Returns a map of all parameters with sanitized values.
     * 
     * @return map of parameter names to sanitized values
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> originalMap = super.getParameterMap();
        if (originalMap == null || originalMap.isEmpty()) {
            return originalMap;
        }

        Map<String, String[]> sanitizedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : originalMap.entrySet()) {
            String paramName = entry.getKey();
            String[] values = entry.getValue();
            
            if (isParameterExcluded(paramName)) {
                sanitizedMap.put(paramName, values);
            } else {
                String[] sanitizedValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    sanitizedValues[i] = sanitizer.sanitize(values[i]);
                }
                sanitizedMap.put(paramName, sanitizedValues);
            }
        }
        return Collections.unmodifiableMap(sanitizedMap);
    }

    /**
     * Returns a sanitized header value for the given header name.
     * 
     * @param name the header name
     * @return the sanitized header value, or null if the header doesn't exist
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (!sanitizeHeaders || value == null) {
            return value;
        }

        if (isHeaderExcluded(name)) {
            logger.debug("Header '{}' is excluded from XSS sanitization", name);
            return value;
        }

        return sanitizer.sanitize(value);
    }

    /**
     * Returns sanitized header values for the given header name.
     * 
     * @param name the header name
     * @return enumeration of sanitized header values
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headers = super.getHeaders(name);
        if (!sanitizeHeaders || headers == null) {
            return headers;
        }

        if (isHeaderExcluded(name)) {
            return headers;
        }

        List<String> sanitizedHeaders = new ArrayList<>();
        while (headers.hasMoreElements()) {
            sanitizedHeaders.add(sanitizer.sanitize(headers.nextElement()));
        }
        return Collections.enumeration(sanitizedHeaders);
    }

    /**
     * Returns cookies with sanitized values.
     * 
     * @return array of cookies with sanitized values, or null if no cookies exist
     */
    @Override
    public Cookie[] getCookies() {
        Cookie[] cookies = super.getCookies();
        if (!sanitizeCookies || cookies == null || cookies.length == 0) {
            return cookies;
        }

        Cookie[] sanitizedCookies = new Cookie[cookies.length];
        for (int i = 0; i < cookies.length; i++) {
            Cookie original = cookies[i];
            String sanitizedValue = sanitizer.sanitize(original.getValue());
            
            // Create a new cookie with the sanitized value
            Cookie sanitizedCookie = new Cookie(original.getName(), sanitizedValue);
            sanitizedCookie.setDomain(original.getDomain());
            sanitizedCookie.setMaxAge(original.getMaxAge());
            sanitizedCookie.setPath(original.getPath());
            sanitizedCookie.setSecure(original.getSecure());
            sanitizedCookie.setHttpOnly(original.isHttpOnly());
            
            sanitizedCookies[i] = sanitizedCookie;
        }
        return sanitizedCookies;
    }

    /**
     * Returns the sanitized query string.
     * 
     * @return the sanitized query string, or null if there is no query string
     */
    @Override
    public String getQueryString() {
        String queryString = super.getQueryString();
        if (queryString == null) {
            return null;
        }
        return sanitizer.sanitize(queryString);
    }

    /**
     * Checks if a parameter should be excluded from sanitization.
     * 
     * @param parameterName the parameter name to check
     * @return true if the parameter should be excluded
     */
    private boolean isParameterExcluded(String parameterName) {
        return parameterName != null && excludedParameters.contains(parameterName);
    }

    /**
     * Checks if a header should be excluded from sanitization.
     * 
     * @param headerName the header name to check
     * @return true if the header should be excluded
     */
    private boolean isHeaderExcluded(String headerName) {
        if (headerName == null) {
            return false;
        }
        // Check case-insensitively for headers
        String lowerHeaderName = headerName.toLowerCase();
        return excludedHeaders.stream()
                .anyMatch(excluded -> excluded.equalsIgnoreCase(lowerHeaderName));
    }
}
