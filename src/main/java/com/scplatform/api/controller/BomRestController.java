/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.bom.repository.BomRepository;
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
@RequestMapping("/api/bom")
@Tag(name = "Bill of Materials", description = "BOM data for mobile app")
@SecurityRequirement(name = "Bearer Auth")
public class BomRestController {

    private final BomRepository bomRepository;

    public BomRestController(BomRepository bomRepository) {
        this.bomRepository = bomRepository;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of BOMs")
    public ResponseEntity<List<BomSummary>> listBoms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        int safeSize = Math.min(size, 100);
        List<BomSummary> result = bomRepository
                .findAll(PageRequest.of(page, safeSize, Sort.by("bomKey").descending()))
                .getContent()
                .stream()
                .map(this::toSummary)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{bomKey}")
    @Operation(summary = "Get full BOM detail including all BOM lines")
    public ResponseEntity<?> getBomDetail(@PathVariable Long bomKey) {
        return bomRepository.findById(bomKey)
                .map(bom -> ResponseEntity.ok(toDetail(bom)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get BOMs filtered by status (Active, Pending, Approved)")
    public ResponseEntity<List<BomSummary>> getByStatus(@PathVariable String status) {
        List<BomSummary> result = bomRepository.findByStatus(status)
                .stream().map(this::toSummary).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get BOM statistics for dashboard widget")
    public ResponseEntity<Map<String, Object>> getBomStats() {
        return ResponseEntity.ok(Map.of(
                "total",    bomRepository.count(),
                "active",   bomRepository.countByStatus("Active"),
                "pending",  bomRepository.countByStatus("Pending"),
                "approved", bomRepository.countByStatus("Approved"),
                "topLevel", bomRepository.findByIsTopLevel(true).size()
        ));
    }

    // ── Private mappers ──────────────────────────────────────────────────────

    private BomSummary toSummary(Bom bom) {
        return new BomSummary(
                bom.getBomKey(),
                bom.getBomName(),
                bom.getBomDesc(),
                bom.getStatus(),
                bom.getItem() != null ? bom.getItem().getItemNumber() : null,
                bom.getBomVersion() != null ? bom.getBomVersion().getVersion() : null,
                bom.getIsTopLevel(),
                bom.getSortedBomLines() != null ? bom.getSortedBomLines().size() : 0,
                bom.getEffectiveFrom() != null ? bom.getEffectiveFrom().toString() : null,
                bom.getEffectiveTo() != null ? bom.getEffectiveTo().toString() : null
        );
    }

    private BomDetail toDetail(Bom bom) {
        List<BomLineItem> lines = bom.getSortedBomLines() != null
                ? bom.getSortedBomLines().stream().map(this::toLineItem).toList()
                : List.of();

        return new BomDetail(
                bom.getBomKey(),
                bom.getBomName(),
                bom.getBomDesc(),
                bom.getStatus(),
                bom.getItem() != null ? bom.getItem().getItemNumber() : null,
                bom.getBomVersion() != null ? bom.getBomVersion().getVersion() : null,
                bom.getBomVersion() != null ? bom.getBomVersion().getRevision() : null,
                bom.getIsTopLevel(),
                bom.getLeadTime(),
                bom.getBusinessEntity() != null ? bom.getBusinessEntity().getBusinessEntityName() : null,
                bom.getEffectiveFrom() != null ? bom.getEffectiveFrom().toString() : null,
                bom.getEffectiveTo() != null ? bom.getEffectiveTo().toString() : null,
                lines
        );
    }

    private BomLineItem toLineItem(BomLine line) {
        return new BomLineItem(
                line.getBomLineKey(),
                line.getItem() != null ? line.getItem().getItemNumber() : null,
                line.getDescription(),
                line.getItemQty() != null ? line.getItemQty().toString() : null,
                line.getLeadTime(),
                line.getManagedFlag(),
                line.getEffectiveFrom() != null ? line.getEffectiveFrom().toString() : null,
                line.getEffectiveTo() != null ? line.getEffectiveTo().toString() : null
        );
    }

    // ── Response records ─────────────────────────────────────────────────────

    record BomSummary(
            Long bomKey, String bomName, String description, String status,
            String itemNumber, String version, Boolean isTopLevel,
            int lineCount, String effectiveFrom, String effectiveTo
    ) {}

    record BomDetail(
            Long bomKey, String bomName, String description, String status,
            String itemNumber, String version, String revision,
            Boolean isTopLevel, Float leadTime, String supplier,
            String effectiveFrom, String effectiveTo,
            List<BomLineItem> lines
    ) {}

    record BomLineItem(
            Long bomLineKey, String itemNumber, String description,
            String quantity, Float leadTime, String managedFlag,
            String effectiveFrom, String effectiveTo
    ) {}
}
