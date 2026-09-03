/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the com.scplatform.pcm.* controllers
 * (suppliers, cost records, purchase orders, inventory, BOM, ...).
 *
 * com.scplatform.api.security.ApiErrorHandler already does this for
 * com.scplatform.api.controller and com.scplatform.dataview, but its
 * basePackages never covered com.scplatform.pcm -- every exception thrown
 * there (e.g. CostRecordService.create()'s "Item not found") fell through
 * to Spring's default handling as a raw 500, discarding the actual message.
 * A second @RestControllerAdvice bean, scoped separately, is the correct
 * fix here rather than editing ApiErrorHandler's basePackages, which
 * already carries an unrelated pending change on that exact line.
 *
 * Response shape mirrors ApiErrorHandler's (error/code/status/timestamp),
 * plus `message` -- several pages already read err.response.data.message.
 */
@RestControllerAdvice(basePackages = "com.scplatform.pcm")
public class PcmApiErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(PcmApiErrorHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("[PCM-API-ERROR] Not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), "NOT_FOUND", null);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        log.warn("[PCM-API-ERROR] Validation failed: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "VALIDATION_ERROR", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadInput(IllegalArgumentException ex) {
        log.warn("[PCM-API-ERROR] Invalid input: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_INPUT", null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleBadState(IllegalStateException ex) {
        log.warn("[PCM-API-ERROR] Invalid state: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), "INVALID_STATE", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBeanValidation(MethodArgumentNotValidException ex) {
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.toList());
        log.warn("[PCM-API-ERROR] Request validation failed: {}", fieldErrors);
        return build(HttpStatus.BAD_REQUEST, "Validation failed", "VALIDATION_ERROR", fieldErrors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("[PCM-API-ERROR] Data integrity violation: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Data conflict — resource already exists or constraint violated", "DATA_CONFLICT", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        log.error("[PCM-API-ERROR] Unexpected error: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "INTERNAL_ERROR", null);
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, String code, List<String> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", message);
        body.put("message", message);
        body.put("code", code);
        body.put("status", status.value());
        body.put("timestamp", Instant.now().toString());
        if (details != null && !details.isEmpty()) {
            body.put("details", details);
        }
        return ResponseEntity.status(status).body(body);
    }
}
