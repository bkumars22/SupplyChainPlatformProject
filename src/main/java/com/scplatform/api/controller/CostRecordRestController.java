/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.ms3cost.CostRecord;
import com.scplatform.pcm.ms3cost.CostRecordRequest;
import com.scplatform.pcm.ms3cost.CostRecordService;
import com.scplatform.pcm.ms3cost.RejectionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cost-records")
@Tag(name = "Cost Records", description = "Cost record management — draft, submit, approve, reject")
@SecurityRequirement(name = "Bearer Auth")
public class CostRecordRestController {

    private final CostRecordService costService;

    public CostRecordRestController(CostRecordService costService) {
        this.costService = costService;
    }

    @GetMapping
    @Operation(summary = "List cost records with optional search")
    public ResponseEntity<List<CostDto>> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<CostDto> result = costService.search(search, page, Math.min(size, 100))
                .getContent().stream().map(this::toDto).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cost record by ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(costService.getById(id)));
    }

    @GetMapping("/stats")
    @Operation(summary = "Cost record counts by status")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(costService.getStats());
    }

    @PostMapping
    @Operation(summary = "Create a new cost record as DRAFT")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body, Authentication auth) {
        CostRecordRequest req = new CostRecordRequest();
        String itemCode = body.containsKey("itemKey")
                ? String.valueOf(body.get("itemKey"))
                : String.valueOf(body.getOrDefault("itemCode", ""));
        req.setItemCode(itemCode);
        req.setProposedCost(new BigDecimal(String.valueOf(body.get("proposedCost"))));
        req.setJustification(String.valueOf(body.getOrDefault("justification", "")));
        String createdBy = auth != null ? auth.getName() : String.valueOf(body.getOrDefault("createdBy", "system"));
        try {
            return ResponseEntity.ok(toDto(costService.create(req, createdBy)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a DRAFT cost record")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    Authentication auth) {
        CostRecord existing = costService.getById(id);
        if (body.containsKey("proposedCost"))
            existing.setProposedCost(new BigDecimal(String.valueOf(body.get("proposedCost"))));
        if (body.containsKey("justification"))
            existing.setJustification(String.valueOf(body.get("justification")));
        return ResponseEntity.ok(toDto(costService.submit(id, auth != null ? auth.getName() : "system")));
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "Submit DRAFT for approval")
    public ResponseEntity<?> submit(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(toDto(costService.submit(id, auth != null ? auth.getName() : "system")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a PENDING_APPROVAL cost record")
    public ResponseEntity<?> approve(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(toDto(costService.approve(id, auth != null ? auth.getName() : "system")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a PENDING_APPROVAL cost record")
    public ResponseEntity<?> reject(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    Authentication auth) {
        String reason = String.valueOf(body.getOrDefault("reason", ""));
        try {
            return ResponseEntity.ok(toDto(costService.reject(id, reason, auth != null ? auth.getName() : "system")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DTO mapper ────────────────────────────────────────────────────────────

    private CostDto toDto(CostRecord c) {
        return new CostDto(
                c.getId(),
                c.getItem() != null ? c.getItem().getItemNumber() : null,
                c.getVersionNumber(),
                c.getProposedCost(),
                c.getPreviousCost(),
                c.getChangePercent(),
                c.getStatus() != null ? c.getStatus().name() : null,
                c.getJustification(),
                c.getRejectionReason(),
                c.getCreatedBy(),
                c.getCreatedDate() != null ? c.getCreatedDate().toString() : null,
                c.getApprovedBy(),
                c.getApprovedDate() != null ? c.getApprovedDate().toString() : null
        );
    }

    record CostDto(
            Long id,
            String itemCode,
            Integer versionNumber,
            BigDecimal proposedCost,
            BigDecimal previousCost,
            BigDecimal changePercent,
            String status,
            String justification,
            String rejectionReason,
            String createdBy,
            String createdDate,
            String approvedBy,
            String approvedDate
    ) {}
}
