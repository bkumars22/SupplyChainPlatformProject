/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import com.scplatform.pcm.common.response.ApiResponse;
import com.scplatform.pcm.common.response.BaseApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Scorecard", description = "Supplier performance scoring — OTD, quality, delivery history")
public class SupplierController extends BaseApiController {

    @Autowired private SupplierService supplierService;

    @GetMapping
    @Operation(summary = "All suppliers with live scorecards")
    public ResponseEntity<ApiResponse<List<SupplierScorecardDto>>> getAll(
            @RequestParam(defaultValue = "") String search) {
        return ok(supplierService.getAllScorecards(search));
    }

    @GetMapping("/{supplierId}")
    @Operation(summary = "Full scorecard for a single supplier")
    public ResponseEntity<ApiResponse<SupplierScorecardDto>> getById(@PathVariable String supplierId) {
        return ok(supplierService.getScorecard(supplierId));
    }

    @GetMapping("/{supplierId}/deliveries")
    @Operation(summary = "Full delivery history for a supplier")
    public ResponseEntity<ApiResponse<List<SupplierDelivery>>> getDeliveries(@PathVariable String supplierId) {
        return ok(supplierService.getDeliveries(supplierId));
    }

    @PostMapping("/{supplierId}/deliveries")
    @Operation(summary = "Log a new delivery for a supplier")
    public ResponseEntity<ApiResponse<SupplierDelivery>> logDelivery(
            @PathVariable String supplierId,
            @Valid @RequestBody SupplierDeliveryRequest req) {
        return created(supplierService.logDelivery(supplierId, req));
    }

    @GetMapping("/at-risk")
    @Operation(summary = "Suppliers with OTD score below 70% — critical risk")
    public ResponseEntity<ApiResponse<List<SupplierScorecardDto>>> getAtRisk() {
        return ok(supplierService.getAtRiskSuppliers());
    }

    @GetMapping("/top")
    @Operation(summary = "Top 5 suppliers by composite score")
    public ResponseEntity<ApiResponse<List<SupplierScorecardDto>>> getTop(
            @RequestParam(defaultValue = "5") int limit) {
        return ok(supplierService.getTopSuppliers(limit));
    }

    @GetMapping("/stats")
    @Operation(summary = "Supplier stats — avg scores, tier breakdown, at-risk count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ok(supplierService.getStats());
    }
}
