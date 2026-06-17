/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Global exception handler for all /api/** controllers.
 * Production responses never expose stack traces, class names, SQL, or line numbers.
 * Full exception detail is always logged server-side.
 */
@RestControllerAdvice(basePackages = "com.scplatform.api.controller")
public class ApiErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiErrorHandler.class);

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    // ── Generic fallback ────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        log.error("[API-ERROR] Unexpected error: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "INTERNAL_ERROR", null, ex);
    }

    // ── Bad input ───────────────────────────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadInput(IllegalArgumentException ex) {
        log.warn("[API-ERROR] Invalid input: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Invalid request", "INVALID_INPUT", null, ex);
    }

    // ── Resource not found ──────────────────────────────────────────────────
    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        log.warn("[API-ERROR] Resource not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Resource not found", "NOT_FOUND", null, ex);
    }

    // ── Access denied ───────────────────────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("[API-ERROR] Access denied: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "Access denied", "ACCESS_DENIED", null, ex);
    }

    // ── Validation errors ───────────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.toList());
        log.warn("[API-ERROR] Validation failed: {}", fieldErrors);
        return build(HttpStatus.BAD_REQUEST, "Validation failed", "VALIDATION_ERROR", fieldErrors, ex);
    }

    // ── Wrong HTTP method ───────────────────────────────────────────────────
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        log.warn("[API-ERROR] Method not allowed: {}", ex.getMessage());
        return build(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not supported", "METHOD_NOT_ALLOWED", null, ex);
    }

    // ── Payload too large ───────────────────────────────────────────────────
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handlePayloadTooLarge(MaxUploadSizeExceededException ex) {
        log.warn("[API-ERROR] Upload size exceeded: {}", ex.getMessage());
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "Request body too large — maximum allowed size exceeded",
                     "PAYLOAD_TOO_LARGE", null, ex);
    }

    // ── DB constraint violation ─────────────────────────────────────────────
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("[API-ERROR] Data integrity violation: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Data conflict — resource already exists or constraint violated",
                     "DATA_CONFLICT", null, ex);
    }

    // ── Builder ─────────────────────────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, String code,
                                                       List<String> details, Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error",     message);
        body.put("code",      code);
        body.put("status",    status.value());
        body.put("timestamp", Instant.now().toString());
        if (details != null && !details.isEmpty()) {
            body.put("details", details);
        }
        // In dev profile only: include exception message (never the stack trace)
        if (isDev() && ex != null && ex.getMessage() != null) {
            body.put("_dev_hint", ex.getMessage());
        }
        return ResponseEntity.status(status).body(body);
    }

    private boolean isDev() {
        return activeProfile != null &&
               (activeProfile.contains("dev") || activeProfile.contains("local"));
    }
}
