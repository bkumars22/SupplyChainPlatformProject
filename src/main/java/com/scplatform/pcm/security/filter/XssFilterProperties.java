/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Configuration properties for the XSS Protection Filter.
 * 
 * All properties are prefixed with "security.xss" in application.properties or application.yml.
 * 
 * Example configuration:
 * <pre>
 * security.xss.enabled=true
 * security.xss.sanitize-headers=true
 * security.xss.sanitize-cookies=true
 * security.xss.excluded-url-patterns=/api/webhook/**,/public/**
 * security.xss.excluded-parameters=signature,hash
 * security.xss.add-security-headers=true
 * </pre>
 * 
 * @author PCM Security Team
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "security.xss")
@Getter
@Setter
@ToString(of = {"enabled", "sanitizeHeaders", "sanitizeCookies", "excludedUrlPatterns", 
                "excludedParameters", "excludedHeaders", "addSecurityHeaders"})
public class XssFilterProperties {

    /**
     * Enable or disable the XSS filter.
     * Default: true
     */
    private boolean enabled = true;

    /**
     * Whether to sanitize HTTP request headers.
     * Default: true
     */
    private boolean sanitizeHeaders = true;

    /**
     * Whether to sanitize cookie values.
     * Default: true
     */
    private boolean sanitizeCookies = true;

    /**
     * URL patterns to exclude from XSS filtering.
     * Supports wildcards: * for single path segment, ** for multiple segments.
     * Example: /api/webhook/**, /public/*, /static/**
     */
    private Set<String> excludedUrlPatterns = new HashSet<>();

    /**
     * Parameter names to exclude from sanitization.
     * Useful for parameters that legitimately contain HTML or special characters.
     * Example: signature, hash, encoded_data
     */
    private Set<String> excludedParameters = new HashSet<>();

    /**
     * Header names to exclude from sanitization.
     * Example: Authorization, X-Request-ID
     */
    private Set<String> excludedHeaders = new HashSet<>();

    /**
     * HTTP methods to exclude from XSS filtering.
     * Example: OPTIONS, HEAD
     */
    private Set<String> excludedMethods = new HashSet<>();

    /**
     * Whether to add security headers to responses.
     * Includes: X-XSS-Protection, X-Content-Type-Options, X-Frame-Options
     * Default: true
     */
    private boolean addSecurityHeaders = true;

    /**
     * Whether to add Content-Security-Policy header.
     * Default: false (as CSP can break applications if not configured properly)
     */
    private boolean addContentSecurityPolicy = false;

    /**
     * Custom Content-Security-Policy header value.
     * Only used if addContentSecurityPolicy is true.
     * Default: "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'"
     */
    private String contentSecurityPolicy = "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'";

    /**
     * Whether to log sanitization events.
     * When true, logs when XSS patterns are detected and sanitized.
     * Default: true
     */
    private boolean logSanitization = true;
}
