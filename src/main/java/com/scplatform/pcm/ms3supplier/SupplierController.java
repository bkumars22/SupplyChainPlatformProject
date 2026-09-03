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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Scorecard", description = "Supplier performance scoring — OTD, quality, delivery history")
public class SupplierController extends BaseApiController {

    @Autowired private SupplierService supplierService;
    @Autowired private SupplierRiskCascadeService cascadeService;

    @GetMapping
    @Operation(summary = "All suppliers with live scorecards — paginated")
    public ResponseEntity<ApiResponse<List<SupplierScorecardDto>>> getAll(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<SupplierScorecardDto> all = supplierService.getAllScorecards(search);
        int total = all.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<SupplierScorecardDto> paged = all.subList(fromIndex, toIndex);
        ApiResponse<List<SupplierScorecardDto>> response = ApiResponse.ok(paged, "total:" + total);
        return ResponseEntity.ok(response);
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

    @PostMapping("/recalculate-tiers")
    @Operation(summary = "Recalculate every supplier's tier from its real OTD delivery history — call after any bulk data load (see DB_SETUP.md)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> recalculateTiers() {
        return ok(supplierService.recalculateTiers());
    }

    @GetMapping("/stats")
    @Operation(summary = "Supplier stats — avg scores, tier breakdown, at-risk count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ok(supplierService.getStats());
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export all suppliers as CSV")
    public ResponseEntity<byte[]> exportCsv() {
        List<SupplierScorecardDto> suppliers = supplierService.getAllScorecards(null);

        StringBuilder csv = new StringBuilder();
        csv.append("Supplier ID,Name,Country,Tier,OTD Score,Quality Score,At Risk\n");

        for (SupplierScorecardDto s : suppliers) {
            csv.append(escapeCsv(s.getSupplierId())).append(",")
               .append(escapeCsv(s.getSupplierName())).append(",")
               .append(escapeCsv(s.getCountry())).append(",")
               .append(escapeCsv(s.getTier())).append(",")
               .append(s.getOtdScore()).append(",")
               .append(s.getQualityScore()).append(",")
               .append(s.isAtRisk())
               .append("\n");
        }

        byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=suppliers.csv");
        headers.setContentLength(csvBytes.length);

        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    @PostMapping("/dependencies")
    @Operation(summary = "Record that one supplier sources a component/material from another (dependency graph edge)")
    public ResponseEntity<ApiResponse<SupplierDependency>> addDependency(
            @Valid @RequestBody SupplierDependencyRequest req) {
        return created(cascadeService.addDependency(req));
    }

    @GetMapping("/dependencies")
    @Operation(summary = "All supplier dependency edges")
    public ResponseEntity<ApiResponse<List<SupplierDependency>>> getDependencies() {
        return ok(cascadeService.getAllDependencies());
    }

    @GetMapping("/{supplierId}/cascaded-risk")
    @Operation(summary = "Effective risk including decayed risk cascading up from this supplier's dependency chain")
    public ResponseEntity<ApiResponse<CascadedRiskDto>> getCascadedRisk(@PathVariable String supplierId) {
        return ok(cascadeService.calculateCascadedRisk(supplierId));
    }

    @GetMapping("/{supplierId}/structural-risk")
    @Operation(summary = "Sole-source dependency mapping for this supplier, independent of current upstream risk levels")
    public ResponseEntity<ApiResponse<StructuralRiskDto>> getStructuralRisk(@PathVariable String supplierId) {
        return ok(cascadeService.getStructuralRisk(supplierId));
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
