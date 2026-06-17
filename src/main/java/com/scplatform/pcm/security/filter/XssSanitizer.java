/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * XSS Sanitizer Utility Class
 * 
 * Provides comprehensive XSS attack prevention by sanitizing input strings.
 * Uses regex patterns to detect and neutralize potential XSS attack vectors.
 * 
 * Implements OWASP recommended patterns for XSS prevention.
 * 
 * @author PCM Security Team
 * @version 1.0
 */
@Component
public class XssSanitizer {

    private static final Logger logger = LoggerFactory.getLogger(XssSanitizer.class);

    // Precompiled regex patterns for performance (case-insensitive, multiline, dotall)
    private static final int PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL;

    /**
     * Pattern to match complete script tags with content: <script>...</script>
     */
    private static final Pattern SCRIPT_TAG_PATTERN = 
            Pattern.compile("<script[^>]*>.*?</script>", PATTERN_FLAGS);

    /**
     * Pattern to match standalone opening script tags: <script> or <script ...>
     */
    private static final Pattern SCRIPT_OPEN_TAG_PATTERN = 
            Pattern.compile("<script[^>]*>", PATTERN_FLAGS);

    /**
     * Pattern to match standalone closing script tags: </script>
     */
    private static final Pattern SCRIPT_CLOSE_TAG_PATTERN = 
            Pattern.compile("</script>", PATTERN_FLAGS);

    /**
     * Pattern to match src attribute with single quotes: src='...'
     */
    private static final Pattern SRC_SINGLE_QUOTE_PATTERN = 
            Pattern.compile("src\\s*=\\s*'[^']*'", PATTERN_FLAGS);

    /**
     * Pattern to match src attribute with double quotes: src="..."
     */
    private static final Pattern SRC_DOUBLE_QUOTE_PATTERN = 
            Pattern.compile("src\\s*=\\s*\"[^\"]*\"", PATTERN_FLAGS);

    /**
     * Pattern to match eval() expressions: eval(...) including nested parentheses
     */
    private static final Pattern EVAL_PATTERN = 
            Pattern.compile("eval\\s*\\([^)]*\\)", PATTERN_FLAGS);

    /**
     * Pattern to match CSS expression() expressions (IE specific)
     */
    private static final Pattern EXPRESSION_PATTERN = 
            Pattern.compile("expression\\s*\\([^)]*\\)", PATTERN_FLAGS);

    /**
     * Pattern to match javascript: protocol handler
     */
    private static final Pattern JAVASCRIPT_PATTERN = 
            Pattern.compile("javascript\\s*:", PATTERN_FLAGS);

    /**
     * Pattern to match vbscript: protocol handler
     */
    private static final Pattern VBSCRIPT_PATTERN = 
            Pattern.compile("vbscript\\s*:", PATTERN_FLAGS);

    /**
     * Pattern to match onload= event handler
     */
    private static final Pattern ONLOAD_PATTERN = 
            Pattern.compile("onload\\s*=", PATTERN_FLAGS);

    /**
     * Pattern to match all common event handlers (on* attributes)
     * Includes: onclick, onmouseover, onerror, onfocus, onblur, etc.
     */
    private static final Pattern EVENT_HANDLER_PATTERN = 
            Pattern.compile("on(click|dblclick|mousedown|mouseup|mouseover|mousemove|mouseout|mouseenter|mouseleave|"
                    + "keydown|keyup|keypress|focus|blur|change|submit|reset|select|input|"
                    + "load|unload|error|resize|scroll|contextmenu|drag|dragstart|dragend|"
                    + "dragover|dragenter|dragleave|drop|copy|cut|paste|beforeunload|hashchange|"
                    + "popstate|storage|message|online|offline|animationstart|animationend|"
                    + "animationiteration|transitionend|touchstart|touchend|touchmove|touchcancel)\\s*=",
                    PATTERN_FLAGS);

    /**
     * Pattern to match alert() expressions
     */
    private static final Pattern ALERT_PATTERN = 
            Pattern.compile("alert\\s*\\([^)]*\\)", PATTERN_FLAGS);

    /**
     * Pattern to match confirm() expressions (additional security)
     */
    private static final Pattern CONFIRM_PATTERN = 
            Pattern.compile("confirm\\s*\\([^)]*\\)", PATTERN_FLAGS);

    /**
     * Pattern to match prompt() expressions (additional security)
     */
    private static final Pattern PROMPT_PATTERN = 
            Pattern.compile("prompt\\s*\\([^)]*\\)", PATTERN_FLAGS);

    /**
     * Pattern to match document.cookie access (additional security)
     */
    private static final Pattern DOCUMENT_COOKIE_PATTERN = 
            Pattern.compile("document\\.cookie", PATTERN_FLAGS);

    /**
     * Pattern to match document.location manipulation (additional security)
     */
    private static final Pattern DOCUMENT_LOCATION_PATTERN = 
            Pattern.compile("document\\.location", PATTERN_FLAGS);

    /**
     * Pattern to match window.location manipulation (additional security)
     */
    private static final Pattern WINDOW_LOCATION_PATTERN = 
            Pattern.compile("window\\.location", PATTERN_FLAGS);

    /**
     * Pattern to match innerHTML assignment (additional security)
     */
    private static final Pattern INNER_HTML_PATTERN = 
            Pattern.compile("innerHTML\\s*=", PATTERN_FLAGS);

    /**
     * Pattern to match outerHTML assignment (additional security)
     */
    private static final Pattern OUTER_HTML_PATTERN = 
            Pattern.compile("outerHTML\\s*=", PATTERN_FLAGS);

    /**
     * Pattern to match data: URI scheme which can contain executable content
     */
    private static final Pattern DATA_URI_PATTERN = 
            Pattern.compile("data\\s*:", PATTERN_FLAGS);

    /**
     * Pattern to match iframe tags
     */
    private static final Pattern IFRAME_PATTERN = 
            Pattern.compile("<iframe[^>]*>.*?</iframe>|<iframe[^>]*>|</iframe>", PATTERN_FLAGS);

    /**
     * Pattern to match object tags (can load malicious content)
     */
    private static final Pattern OBJECT_PATTERN = 
            Pattern.compile("<object[^>]*>.*?</object>|<object[^>]*>|</object>", PATTERN_FLAGS);

    /**
     * Pattern to match embed tags
     */
    private static final Pattern EMBED_PATTERN = 
            Pattern.compile("<embed[^>]*>|</embed>", PATTERN_FLAGS);

    /**
     * Pattern to match form tags (can be used for form hijacking)
     */
    private static final Pattern FORM_PATTERN = 
            Pattern.compile("<form[^>]*>.*?</form>|<form[^>]*>|</form>", PATTERN_FLAGS);

    /**
     * Pattern to match base tag (can redirect all relative URLs)
     */
    private static final Pattern BASE_PATTERN = 
            Pattern.compile("<base[^>]*>", PATTERN_FLAGS);

    /**
     * Pattern to match link tags with potentially malicious rel attributes
     */
    private static final Pattern LINK_IMPORT_PATTERN = 
            Pattern.compile("<link[^>]*rel\\s*=\\s*['\"]?import['\"]?[^>]*>", PATTERN_FLAGS);

    /**
     * Pattern to match meta refresh (can redirect to malicious sites)
     */
    private static final Pattern META_REFRESH_PATTERN = 
            Pattern.compile("<meta[^>]*http-equiv\\s*=\\s*['\"]?refresh['\"]?[^>]*>", PATTERN_FLAGS);

    /**
     * Sanitizes the input string by removing or neutralizing all known XSS attack patterns.
     * 
     * @param input the string to sanitize
     * @return sanitized string with XSS patterns removed, or null if input is null
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }

        if (input.isEmpty()) {
            return input;
        }

        String original = input;
        String sanitized = input;

        // Remove null characters first (can be used to bypass filters)
        sanitized = removeNullCharacters(sanitized);

        // Remove script tags and content
        sanitized = SCRIPT_TAG_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = SCRIPT_OPEN_TAG_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = SCRIPT_CLOSE_TAG_PATTERN.matcher(sanitized).replaceAll("");

        // Remove src attributes that could load malicious content
        sanitized = SRC_SINGLE_QUOTE_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = SRC_DOUBLE_QUOTE_PATTERN.matcher(sanitized).replaceAll("");

        // Remove JavaScript execution patterns
        sanitized = EVAL_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = EXPRESSION_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = JAVASCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = VBSCRIPT_PATTERN.matcher(sanitized).replaceAll("");

        // Remove event handlers
        sanitized = ONLOAD_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = EVENT_HANDLER_PATTERN.matcher(sanitized).replaceAll("");

        // Remove dialog functions
        sanitized = ALERT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = CONFIRM_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = PROMPT_PATTERN.matcher(sanitized).replaceAll("");

        // Remove dangerous DOM access patterns
        sanitized = DOCUMENT_COOKIE_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = DOCUMENT_LOCATION_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = WINDOW_LOCATION_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = INNER_HTML_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = OUTER_HTML_PATTERN.matcher(sanitized).replaceAll("");

        // Remove dangerous URI schemes
        sanitized = DATA_URI_PATTERN.matcher(sanitized).replaceAll("");

        // Remove potentially dangerous HTML tags
        sanitized = IFRAME_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = OBJECT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = EMBED_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = FORM_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = BASE_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = LINK_IMPORT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = META_REFRESH_PATTERN.matcher(sanitized).replaceAll("");

        // Log if sanitization occurred
        if (!original.equals(sanitized)) {
            logger.warn("XSS attack pattern detected and sanitized. Original length: {}, Sanitized length: {}", 
                    original.length(), sanitized.length());
            if (logger.isDebugEnabled()) {
                logger.debug("Original input (truncated): {}", truncateForLog(original));
                logger.debug("Sanitized output (truncated): {}", truncateForLog(sanitized));
            }
        }

        return sanitized;
    }

    /**
     * Sanitizes the input and also HTML-encodes the result using OWASP Encoder.
     * Use this method when the output will be rendered in an HTML context.
     * 
     * @param input the string to sanitize and encode
     * @return sanitized and HTML-encoded string, or null if input is null
     */
    public String sanitizeAndEncode(String input) {
        String sanitized = sanitize(input);
        if (sanitized == null) {
            return null;
        }
        return Encode.forHtml(sanitized);
    }

    /**
     * Sanitizes the input for use in a JavaScript context.
     * Use this method when the output will be embedded in JavaScript code.
     * 
     * @param input the string to sanitize and encode
     * @return sanitized and JavaScript-encoded string, or null if input is null
     */
    public String sanitizeForJavaScript(String input) {
        String sanitized = sanitize(input);
        if (sanitized == null) {
            return null;
        }
        return Encode.forJavaScript(sanitized);
    }

    /**
     * Sanitizes the input for use in a URL context.
     * Use this method when the output will be used in a URL.
     * 
     * @param input the string to sanitize and encode
     * @return sanitized and URL-encoded string, or null if input is null
     */
    public String sanitizeForUrl(String input) {
        String sanitized = sanitize(input);
        if (sanitized == null) {
            return null;
        }
        return Encode.forUriComponent(sanitized);
    }

    /**
     * Sanitizes the input for use in a CSS context.
     * Use this method when the output will be embedded in CSS.
     * 
     * @param input the string to sanitize and encode
     * @return sanitized and CSS-encoded string, or null if input is null
     */
    public String sanitizeForCss(String input) {
        String sanitized = sanitize(input);
        if (sanitized == null) {
            return null;
        }
        return Encode.forCssString(sanitized);
    }

    /**
     * Checks if the input contains any XSS attack patterns without modifying it.
     * 
     * @param input the string to check
     * @return true if potential XSS patterns are detected, false otherwise
     */
    public boolean containsXssPatterns(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return SCRIPT_TAG_PATTERN.matcher(input).find()
                || SCRIPT_OPEN_TAG_PATTERN.matcher(input).find()
                || EVAL_PATTERN.matcher(input).find()
                || EXPRESSION_PATTERN.matcher(input).find()
                || JAVASCRIPT_PATTERN.matcher(input).find()
                || VBSCRIPT_PATTERN.matcher(input).find()
                || EVENT_HANDLER_PATTERN.matcher(input).find()
                || ALERT_PATTERN.matcher(input).find()
                || input.contains("\0");
    }

    /**
     * Removes null characters which can be used to bypass security filters.
     * 
     * @param input the string to process
     * @return string with null characters removed
     */
    private String removeNullCharacters(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\0", "")
                .replace("\\0", "")
                .replace("%00", "");
    }

    /**
     * Truncates a string for safe logging to prevent log injection attacks.
     * 
     * @param input the string to truncate
     * @return truncated string safe for logging
     */
    private String truncateForLog(String input) {
        if (input == null) {
            return "null";
        }
        int maxLength = 200;
        if (input.length() <= maxLength) {
            return sanitizeForLog(input);
        }
        return sanitizeForLog(input.substring(0, maxLength)) + "... [truncated]";
    }

    /**
     * Sanitizes a string for safe logging by removing newlines and control characters.
     * 
     * @param input the string to sanitize
     * @return string safe for logging
     */
    private String sanitizeForLog(String input) {
        if (input == null) {
            return "null";
        }
        return input.replaceAll("[\r\n\t]", " ")
                .replaceAll("[\\x00-\\x1F\\x7F]", "");
    }
}
