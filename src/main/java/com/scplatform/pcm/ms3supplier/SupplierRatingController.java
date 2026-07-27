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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers/{supplierId}/ratings")
@Tag(name = "Supplier Quality Rating", description = "Supplier quality, delivery, and responsiveness ratings")
public class SupplierRatingController extends BaseApiController {

    @Autowired
    private SupplierRatingService ratingService;

    @GetMapping
    @Operation(summary = "Get all ratings for a supplier")
    public ResponseEntity<ApiResponse<List<SupplierRating>>> getRatings(
            @PathVariable String supplierId) {
        return ok(ratingService.getRatings(supplierId));
    }

    @PostMapping
    @Operation(summary = "Add a new rating for a supplier")
    public ResponseEntity<ApiResponse<SupplierRating>> addRating(
            @PathVariable String supplierId,
            @RequestBody SupplierRatingService.RatingRequest req) {
        return created(ratingService.addRating(supplierId, req));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get the most recent 10 ratings for a supplier")
    public ResponseEntity<ApiResponse<List<SupplierRating>>> getLatest(
            @PathVariable String supplierId) {
        return ok(ratingService.getLatestRatings(supplierId));
    }
}
