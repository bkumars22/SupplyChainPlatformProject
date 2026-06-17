/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.repo.PcmForecastRepository;
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
@RequestMapping("/api/forecasts")
@Tag(name = "Forecasts", description = "Demand and cost forecasts for supply chain planning")
@SecurityRequirement(name = "Bearer Auth")
public class ForecastRestController {

    private final PcmForecastRepository forecastRepository;

    public ForecastRestController(PcmForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    @GetMapping
    @Operation(summary = "Get paginated forecasts")
    public ResponseEntity<List<ForecastSummary>> listForecasts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(size, 100);
        List<ForecastSummary> result = forecastRepository
                .findAll(PageRequest.of(page, safeSize, Sort.by("forecastKey").descending()))
                .getContent()
                .stream()
                .map(this::toSummary)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get forecast by ID")
    public ResponseEntity<?> getForecast(@PathVariable Long id) {
        return forecastRepository.findById(id)
                .map(f -> ResponseEntity.ok(toSummary(f)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    @Operation(summary = "Forecast count statistics by status")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total = forecastRepository.count();
        List<PcmForecast> all = forecastRepository.findAll();
        long approved = all.stream().filter(f -> "APPROVED".equals(f.getStatus())).count();
        long submitted = all.stream().filter(f -> "SUBMITTED".equals(f.getStatus())).count();
        long draft = all.stream().filter(f -> "DRAFT".equals(f.getStatus())).count();
        return ResponseEntity.ok(Map.of(
                "total", total,
                "approved", approved,
                "submitted", submitted,
                "draft", draft
        ));
    }

    @PostMapping
    @Operation(summary = "Create a new forecast (DRAFT)")
    public ResponseEntity<?> createForecast(@RequestBody ForecastCreateRequest req) {
        if (req.forecastType() == null || req.forecastType().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "forecastType is required"));
        if (req.calendarName() == null || req.calendarName().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "calendarName is required"));

        PcmForecast f = new PcmForecast();
        f.setForecastType(req.forecastType());
        f.setCalendarName(req.calendarName());
        f.setDescription(req.description());
        f.setStatus("DRAFT");
        f.setCurrentFlag(true);
        f.setDeleteFlag(false);
        if (req.forecastModel() != null) {
            try {
                f.setForecastModel(com.scplatform.pcm.forecast.enums.ForecastModel.valueOf(req.forecastModel().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid forecastModel: " + req.forecastModel()));
            }
        }
        PcmForecast saved = forecastRepository.save(f);
        return ResponseEntity.status(201).body(toSummary(saved));
    }

    private ForecastSummary toSummary(PcmForecast f) {
        return new ForecastSummary(
                f.getForecastKey(),
                f.getForecastExternalId(),
                f.getForecastType(),
                f.getCalendarName(),
                f.getForecastModel() != null ? f.getForecastModel().name() : null,
                f.getStatus(),
                f.getDescription(),
                f.getItem() != null ? f.getItem().getItemNumber() : null,
                f.getEffectiveFromDt() != null ? f.getEffectiveFromDt().toString() : null,
                f.getEffectiveToDt() != null ? f.getEffectiveToDt().toString() : null,
                f.getCurrentFlag()
        );
    }

    record ForecastSummary(
            Long forecastKey,
            String externalId,
            String forecastType,
            String calendarName,
            String forecastModel,
            String status,
            String description,
            String itemNumber,
            String effectiveFrom,
            String effectiveTo,
            boolean isActive
    ) {}

    record ForecastCreateRequest(
            String forecastType,
            String calendarName,
            String forecastModel,
            String description
    ) {}
}
