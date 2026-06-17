/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.alert.entity.AlertDetail;
import com.scplatform.pcm.alert.enums.AlertDetailState;
import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alerts", description = "Real-time supply chain alerts for mobile app")
@SecurityRequirement(name = "Bearer Auth")
public class AlertRestController {

    private final AlertDetailRepository alertDetailRepository;

    public AlertRestController(AlertDetailRepository alertDetailRepository) {
        this.alertDetailRepository = alertDetailRepository;
    }

    @GetMapping
    @Operation(summary = "Get all alerts for the logged-in user")
    public ResponseEntity<List<AlertResponse>> getMyAlerts(@AuthenticationPrincipal String userId) {
        List<AlertDetail> alerts = alertDetailRepository.findByUserLoginIdOrderByCreatedDesc(userId);
        List<AlertResponse> response = alerts.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get only active (unread) alerts for the logged-in user")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts(@AuthenticationPrincipal String userId) {
        List<AlertDetail> alerts = alertDetailRepository
                .findByUserLoginIdOrderByCreatedDesc(userId)
                .stream()
                .filter(a -> a.getState() == AlertDetailState.ACTIVE)
                .toList();
        return ResponseEntity.ok(alerts.stream().map(this::toResponse).toList());
    }

    @GetMapping("/count")
    @Operation(summary = "Get count of unread alerts for badge display on mobile")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal String userId) {
        long count = alertDetailRepository.countByUserLoginIdAndState(userId, AlertDetailState.ACTIVE);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/dismiss")
    @Transactional
    @Operation(summary = "Dismiss a single alert")
    public ResponseEntity<?> dismissAlert(@PathVariable Long id,
                                          @AuthenticationPrincipal String userId) {
        int updated = alertDetailRepository.markAsDismissed(id, userId);
        if (updated == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", "Alert dismissed"));
    }

    @PutMapping("/dismiss-all")
    @Transactional
    @Operation(summary = "Dismiss all alerts for the logged-in user")
    public ResponseEntity<?> dismissAll(@AuthenticationPrincipal String userId) {
        int count = alertDetailRepository.dismissAllForUser(userId);
        return ResponseEntity.ok(Map.of("dismissed", count));
    }

    private AlertResponse toResponse(AlertDetail a) {
        return new AlertResponse(
                a.getId(),
                a.getAlertType(),
                a.getAlertLabel(),
                a.getShortSummary(),
                a.getLongSummary(),
                a.getState() != null ? a.getState().name() : null,
                a.getCreated() != null ? a.getCreated().toString() : null,
                a.getExpirationDate() != null ? a.getExpirationDate().toString() : null,
                a.getStringAttribute1(),   // Item
                a.getStringAttribute2(),   // Supplier
                a.getStringAttribute3(),   // SourceSite
                a.getStringAttribute8(),   // BusinessState
                a.getPunchoutUrl()
        );
    }

    record AlertResponse(
            Long id,
            String alertType,
            String label,
            String shortSummary,
            String longSummary,
            String state,
            String created,
            String expiresOn,
            String item,
            String supplier,
            String sourceSite,
            String businessState,
            String actionUrl
    ) {}
}
