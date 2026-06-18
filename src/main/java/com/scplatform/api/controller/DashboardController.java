/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.alert.enums.AlertDetailState;
import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import com.scplatform.pcm.bom.repository.BomRepository;
import com.scplatform.pcm.inventory.InventoryItemRepository;
import com.scplatform.pcm.purchaseorder.PurchaseOrderRepository;
import com.scplatform.pcm.ms3supplier.SupplierRatingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController("mobileDashboardController")
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Single call to load mobile home screen KPIs")
@SecurityRequirement(name = "Bearer Auth")
public class DashboardController {

    private final AlertDetailRepository alertDetailRepository;
    private final BomRepository bomRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRatingRepository supplierRatingRepository;

    public DashboardController(AlertDetailRepository alertDetailRepository,
                               BomRepository bomRepository,
                               InventoryItemRepository inventoryItemRepository,
                               PurchaseOrderRepository purchaseOrderRepository,
                               SupplierRatingRepository supplierRatingRepository) {
        this.alertDetailRepository    = alertDetailRepository;
        this.bomRepository            = bomRepository;
        this.inventoryItemRepository  = inventoryItemRepository;
        this.purchaseOrderRepository  = purchaseOrderRepository;
        this.supplierRatingRepository = supplierRatingRepository;
    }

    @GetMapping("/summary")
    @Operation(summary = "Load all KPI counts for home screen in one API call")
    public ResponseEntity<Map<String, Object>> getSummary(@AuthenticationPrincipal String userId) {

        long unreadAlerts = alertDetailRepository.countByUserLoginIdAndState(userId, AlertDetailState.ACTIVE);

        // Low-stock items: currentStock <= reorderPoint
        long lowStockCount = inventoryItemRepository.findLowStockItems().size();

        // Open POs: SUBMITTED or CONFIRMED (in-flight)
        long openPOCount = purchaseOrderRepository.findByStatus("SUBMITTED").size()
                         + purchaseOrderRepository.findByStatus("CONFIRMED").size();

        // Avg supplier rating last 90 days (scale 0-100)
        LocalDate since = LocalDate.now().minusDays(90);
        double avgRating = supplierRatingRepository.findAll().stream()
                .filter(r -> r.getRatingDate() != null && !r.getRatingDate().isBefore(since))
                .mapToDouble(r -> {
                    if (r.getQualityScore() == null || r.getDeliveryScore() == null || r.getResponsiveness() == null) return 0;
                    return r.getQualityScore().add(r.getDeliveryScore()).add(r.getResponsiveness())
                            .divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP)
                            .divide(new BigDecimal("20"), 4, RoundingMode.HALF_UP)
                            .doubleValue();
                })
                .average().orElse(0.0);
        double avgRatingRounded = BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP).doubleValue();

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("unreadAlerts",    unreadAlerts);
        kpis.put("totalBoms",       bomRepository.count());
        kpis.put("activeBoms",      bomRepository.countByStatus("Active"));
        kpis.put("pendingBoms",     bomRepository.countByStatus("Pending"));
        kpis.put("approvedBoms",    bomRepository.countByStatus("Approved"));
        kpis.put("lowStockCount",   lowStockCount);
        kpis.put("openPOCount",     openPOCount);
        kpis.put("avgSupplierRating", avgRatingRounded);
        kpis.put("totalInventorySkus", inventoryItemRepository.count());
        return ResponseEntity.ok(kpis);
    }
}
