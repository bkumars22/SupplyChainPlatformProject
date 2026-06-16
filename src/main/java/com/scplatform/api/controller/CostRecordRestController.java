/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.test.pcm.cost.entity.PcmCostRecord;
import com.test.pcm.cost.repository.PcmCostRecordRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cost-records")
@Tag(name = "Cost Records", description = "Product cost records management")
@SecurityRequirement(name = "Bearer Auth")
public class CostRecordRestController {

    private final PcmCostRecordRepository costRecordRepository;

    public CostRecordRestController(PcmCostRecordRepository costRecordRepository) {
        this.costRecordRepository = costRecordRepository;
    }

    @GetMapping
    @Operation(summary = "Get paginated cost records")
    public ResponseEntity<List<CostRecordSummary>> listCostRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(size, 100);
        List<CostRecordSummary> result = costRecordRepository
                .findAll(PageRequest.of(page, safeSize, Sort.by("costRecordKey").descending()))
                .getContent()
                .stream()
                .map(this::toSummary)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get cost records filtered by status")
    public ResponseEntity<List<CostRecordSummary>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(
            costRecordRepository.findByStatus(status).stream().map(this::toSummary).toList()
        );
    }

    @GetMapping("/stats")
    @Operation(summary = "Get cost record statistics for dashboard")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total    = costRecordRepository.count();
        long pending  = costRecordRepository.findByStatus("SUBMITTED").size();
        long approved = costRecordRepository.findByStatus("APPROVED").size();
        long active   = costRecordRepository.findByCurrentFlagTrue().size();
        return ResponseEntity.ok(Map.of(
                "total", total,
                "pending", pending,
                "approved", approved,
                "active", active
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cost record detail by ID")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return costRecordRepository.findById(id)
                .map(cr -> ResponseEntity.ok(toDetail(cr)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private CostRecordSummary toSummary(PcmCostRecord cr) {
        String item     = null;
        String supplier = null;
        String fromSite = null;
        String toSite   = null;
        String currency = null;

        if (cr.getSourcingLane() != null) {
            var lane = cr.getSourcingLane();
            if (lane.getItem() != null)     item     = lane.getItem().getItemNumber();
            if (lane.getSupplier() != null) supplier = lane.getSupplier().getBusinessEntityName();
            if (lane.getFromSite() != null) fromSite = lane.getFromSite().getSiteName();
            if (lane.getToSite() != null)   toSite   = lane.getToSite().getSiteName();
            currency = lane.getCurrencyCode();
        }

        return new CostRecordSummary(
                cr.getCostRecordKey(),
                cr.getCostType() != null ? cr.getCostType().getCostTypeName() : null,
                cr.getStatus(),
                item,
                supplier,
                fromSite,
                toSite,
                currency,
                cr.getEffectiveFromDt() != null ? cr.getEffectiveFromDt().toString() : null,
                cr.getEffectiveToDt()   != null ? cr.getEffectiveToDt().toString()   : null,
                cr.getCostRecordExternalId(),
                cr.getCurrentFlag()
        );
    }

    private CostRecordDetail toDetail(PcmCostRecord cr) {
        var summary = toSummary(cr);
        int rangeCount = cr.getCostRecordRanges() != null ? cr.getCostRecordRanges().size() : 0;
        return new CostRecordDetail(summary, cr.getDescription(), cr.getReasonCode(), rangeCount);
    }

    // ── Response records ─────────────────────────────────────────────────────

    record CostRecordSummary(
            Long id, String costType, String status,
            String itemNumber, String supplier,
            String fromSite, String toSite, String currency,
            String effectiveFrom, String effectiveTo,
            String externalId, boolean isActive
    ) {}

    record CostRecordDetail(
            CostRecordSummary summary,
            String description, String reasonCode, int rangeCount
    ) {}
}
