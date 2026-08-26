/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3cost;

import com.scplatform.pcm.common.response.ApiResponse;
import com.scplatform.pcm.common.response.BaseApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/costs")
@Tag(name = "Cost Records", description = "Cost change workflow — draft, submit, approve, reject")
public class CostRecordController extends BaseApiController {

    @Autowired private CostRecordService costService;

    @GetMapping
    @Operation(summary = "Search cost records with pagination")
    public ResponseEntity<ApiResponse<Page<CostDto>>> search(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ok(costService.search(search, page, size).map(CostRecordController::toDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cost record detail")
    public ResponseEntity<ApiResponse<CostDto>> getById(@PathVariable Long id) {
        return ok(toDto(costService.getById(id)));
    }

    @GetMapping("/item/{itemCode}")
    @Operation(summary = "All cost records for a specific item")
    public ResponseEntity<ApiResponse<List<CostDto>>> getByItem(@PathVariable String itemCode) {
        return ok(costService.getByItemCode(itemCode).stream().map(CostRecordController::toDto).toList());
    }

    @PostMapping
    @Operation(summary = "Create a new cost record (DRAFT)")
    public ResponseEntity<ApiResponse<CostDto>> create(
            @Valid @RequestBody CostRecordRequest req, Authentication auth) {
        return created(toDto(costService.create(req, auth.getName())));
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "Submit DRAFT for approval")
    public ResponseEntity<ApiResponse<CostDto>> submit(
            @PathVariable Long id, Authentication auth) {
        return ok(toDto(costService.submit(id, auth.getName())));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve cost record — updates item unit cost")
    public ResponseEntity<ApiResponse<CostDto>> approve(
            @PathVariable Long id, Authentication auth) {
        return ok(toDto(costService.approve(id, auth.getName())));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject cost record with mandatory reason")
    public ResponseEntity<ApiResponse<CostDto>> reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectionRequest req, Authentication auth) {
        return ok(toDto(costService.reject(id, req.getReason(), auth.getName())));
    }

    @GetMapping("/pending")
    @Operation(summary = "Pending approval queue for managers")
    public ResponseEntity<ApiResponse<List<CostDto>>> getPending() {
        return ok(costService.getPendingApprovals().stream().map(CostRecordController::toDto).toList());
    }

    @GetMapping("/stats")
    @Operation(summary = "Cost record counts by status for dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ok(costService.getStats());
    }

    // ── DTO mapping ──────────────────────────────────────────────────────────
    // CostRecord.item is a lazy @ManyToOne into the full Item entity graph
    // (Boms, Amls, Avls, ...). Serializing the entity directly recurses through
    // that graph's bidirectional relationships without end, so the list
    // endpoint must map to a flat DTO instead of returning JPA entities as-is.

    private static CostDto toDto(CostRecord c) {
        ItemDto item = c.getItem() != null
                ? new ItemDto(c.getItem().getItemNumber(), c.getItem().getDescription())
                : null;
        return new CostDto(
                c.getId(), item, c.getVersionNumber(),
                c.getPreviousCost(), c.getProposedCost(), c.getChangePercent(),
                c.getStatus() != null ? c.getStatus().name() : null,
                c.getJustification(), c.getRejectionReason(),
                c.getCreatedBy(), c.getCreatedDate(), c.getSubmittedDate(),
                c.getApprovedBy(), c.getApprovedDate()
        );
    }

    record ItemDto(String itemCode, String description) {}

    record CostDto(
            Long id,
            ItemDto item,
            Integer versionNumber,
            BigDecimal previousCost,
            BigDecimal proposedCost,
            BigDecimal changePercent,
            String status,
            String justification,
            String rejectionReason,
            String createdBy,
            LocalDateTime createdDate,
            LocalDateTime submittedDate,
            String approvedBy,
            LocalDateTime approvedDate
    ) {}
}
