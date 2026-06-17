/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Intercepts all exceptions from /api/** controllers.
 * Returns generic messages to callers — never exposes stack traces,
 * internal class names, or package paths to the outside world.
 */
@RestControllerAdvice(basePackages = "com.scplatform.api.controller")
public class ApiErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiErrorHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        // Log full detail internally only
        logger.error("API error: {}", ex.getMessage(), ex);
        // Return nothing useful to attacker
        return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadInput(IllegalArgumentException ex) {
        logger.warn("Bad API input: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
    }
}
