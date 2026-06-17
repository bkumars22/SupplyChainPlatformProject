/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.alert.enums.AlertDetailState;
import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import com.scplatform.pcm.bom.repository.BomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("mobileDashboardController")
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Single call to load mobile home screen KPIs")
@SecurityRequirement(name = "Bearer Auth")
public class DashboardController {

    private final AlertDetailRepository alertDetailRepository;
    private final BomRepository bomRepository;

    public DashboardController(AlertDetailRepository alertDetailRepository,
                               BomRepository bomRepository) {
        this.alertDetailRepository = alertDetailRepository;
        this.bomRepository = bomRepository;
    }

    @GetMapping("/summary")
    @Operation(summary = "Load all KPI counts for home screen in one API call")
    public ResponseEntity<Map<String, Object>> getSummary(@AuthenticationPrincipal String userId) {

        long unreadAlerts = alertDetailRepository.countByUserLoginIdAndState(userId, AlertDetailState.ACTIVE);

        return ResponseEntity.ok(Map.of(
                "unreadAlerts", unreadAlerts,
                "totalBoms",    bomRepository.count(),
                "activeBoms",   bomRepository.countByStatus("Active"),
                "pendingBoms",  bomRepository.countByStatus("Pending"),
                "approvedBoms", bomRepository.countByStatus("Approved")
        ));
    }
}
