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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/costs")
@Tag(name = "Cost Records", description = "Cost change workflow — draft, submit, approve, reject")
public class CostRecordController extends BaseApiController {

    @Autowired private CostRecordService costService;

    @GetMapping
    @Operation(summary = "Search cost records with pagination")
    public ResponseEntity<ApiResponse<Page<CostRecord>>> search(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ok(costService.search(search, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cost record detail")
    public ResponseEntity<ApiResponse<CostRecord>> getById(@PathVariable Long id) {
        return ok(costService.getById(id));
    }

    @GetMapping("/item/{itemCode}")
    @Operation(summary = "All cost records for a specific item")
    public ResponseEntity<ApiResponse<List<CostRecord>>> getByItem(@PathVariable String itemCode) {
        return ok(costService.getByItemCode(itemCode));
    }

    @PostMapping
    @Operation(summary = "Create a new cost record (DRAFT)")
    public ResponseEntity<ApiResponse<CostRecord>> create(
            @Valid @RequestBody CostRecordRequest req, Authentication auth) {
        return created(costService.create(req, auth.getName()));
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "Submit DRAFT for approval")
    public ResponseEntity<ApiResponse<CostRecord>> submit(
            @PathVariable Long id, Authentication auth) {
        return ok(costService.submit(id, auth.getName()));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve cost record — updates item unit cost")
    public ResponseEntity<ApiResponse<CostRecord>> approve(
            @PathVariable Long id, Authentication auth) {
        return ok(costService.approve(id, auth.getName()));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject cost record with mandatory reason")
    public ResponseEntity<ApiResponse<CostRecord>> reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectionRequest req, Authentication auth) {
        return ok(costService.reject(id, req.getReason(), auth.getName()));
    }

    @GetMapping("/pending")
    @Operation(summary = "Pending approval queue for managers")
    public ResponseEntity<ApiResponse<List<CostRecord>>> getPending() {
        return ok(costService.getPendingApprovals());
    }

    @GetMapping("/stats")
    @Operation(summary = "Cost record counts by status for dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ok(costService.getStats());
    }
}
