/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.auditlog.AuditLog;
import com.scplatform.pcm.auditlog.AuditLogRepository;
import com.scplatform.pcm.common.response.ApiResponse;
import com.scplatform.pcm.common.response.BaseApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Logs", description = "System-wide audit trail for all tracked operations")
public class AuditLogController extends BaseApiController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    @Operation(summary = "Get audit logs with optional filters by entity type or user")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String userId) {

        List<AuditLog> logs;

        if (userId != null && !userId.isBlank()) {
            logs = auditLogRepository.findByPerformedByOrderByLoggedAtDesc(userId);
        } else if (entityType != null && !entityType.isBlank()) {
            // Return all logs for the entity type (no entityId filter)
            logs = auditLogRepository.findTop100ByOrderByLoggedAtDesc().stream()
                    .filter(l -> entityType.equalsIgnoreCase(l.getEntityType()))
                    .toList();
        } else {
            logs = auditLogRepository.findTop100ByOrderByLoggedAtDesc();
        }

        return ok(logs);
    }

    @GetMapping("/{entityType}/{entityId}")
    @Operation(summary = "Get audit logs for a specific entity by type and ID")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        return ok(auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all audit logs for a specific user")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getByUser(
            @PathVariable String userId) {
        return ok(auditLogRepository.findByPerformedByOrderByLoggedAtDesc(userId));
    }
}
