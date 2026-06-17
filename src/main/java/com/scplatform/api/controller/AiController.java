/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.alert.entity.AlertDetail;
import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import com.scplatform.api.ai.AnomalyExplanationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Engine", description = "Claude AI powered supply chain anomaly analysis")
@SecurityRequirement(name = "Bearer Auth")
public class AiController {

    private final AnomalyExplanationService aiService;
    private final AlertDetailRepository alertRepo;

    public AiController(AnomalyExplanationService aiService, AlertDetailRepository alertRepo) {
        this.aiService = aiService;
        this.alertRepo = alertRepo;
    }

    @GetMapping("/explain-alert/{alertId}")
    @Operation(summary = "Get Claude AI explanation for a specific alert")
    public ResponseEntity<?> explainAlert(@PathVariable Long alertId) {
        return alertRepo.findById(alertId).map(alert -> {
            var ctx = new AnomalyExplanationService.AlertContext(
                    alert.getAlertType(),
                    alert.getShortSummary(),
                    alert.getStringAttribute1(),   // item
                    alert.getStringAttribute2(),   // supplier
                    alert.getStringAttribute3(),   // site
                    alert.getStringAttribute8(),   // business state
                    alert.getCreated() != null ? alert.getCreated().toString() : null
            );
            var explanation = aiService.explain(ctx);
            return ResponseEntity.ok(explanation);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/explain")
    @Operation(summary = "Get Claude AI explanation for any supply chain anomaly")
    public ResponseEntity<?> explainCustom(@RequestBody ExplainRequest req) {
        if (req.summary() == null || req.summary().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "summary is required"));
        }
        var ctx = new AnomalyExplanationService.AlertContext(
                req.alertType() != null ? req.alertType() : "ANOMALY",
                req.summary(),
                req.item(), req.supplier(), req.site(), req.state(), null
        );
        return ResponseEntity.ok(aiService.explain(ctx));
    }

    record ExplainRequest(
            String alertType, String summary,
            String item, String supplier, String site, String state
    ) {}

    // ── NEW: Python ML risk scoring endpoints ─────────────────────────────

    @Value("${ai.service.url:http://localhost:8001}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/health")
    @Operation(summary = "AI service health check")
    public ResponseEntity<Map<String,String>> health() {
        return ResponseEntity.ok(Map.of("status","ok","module","AI"));
    }

    @GetMapping("/supplier/{supplierId}/risk")
    @Operation(summary = "Get ML risk score for a supplier")
    public ResponseEntity<?> getSupplierRisk(@PathVariable Long supplierId) {
        try {
            // Build sample deliveries — later replace with real DB query
            List<Map<String,Object>> deliveries = new ArrayList<>();
            Map<String,Object> d1 = new HashMap<>();
            d1.put("supplierId", supplierId);
            d1.put("leadTimeDays", 5.0);
            d1.put("onTime", true);
            d1.put("defectRate", 0.01);
            deliveries.add(d1);
            Map<String,Object> d2 = new HashMap<>();
            d2.put("supplierId", supplierId);
            d2.put("leadTimeDays", 15.0);
            d2.put("onTime", false);
            d2.put("defectRate", 0.05);
            deliveries.add(d2);
            Map<String,Object> d3 = new HashMap<>();
            d3.put("supplierId", supplierId);
            d3.put("leadTimeDays", 3.0);
            d3.put("onTime", true);
            d3.put("defectRate", 0.0);
            deliveries.add(d3);

            Map<String,Object> payload = new HashMap<>();
            payload.put("supplierId", supplierId);
            payload.put("deliveries", deliveries);

            org.springframework.http.HttpHeaders headers =
                new org.springframework.http.HttpHeaders();
            headers.setContentType(
                org.springframework.http.MediaType.APPLICATION_JSON);
            var request = new org.springframework.http.HttpEntity<>(
                payload, headers);

            var response = restTemplate.postForEntity(
                aiServiceUrl + "/analyze-supplier", request, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "supplierId", supplierId,
                "riskScore", 0,
                "riskLevel", "Unknown",
                "explanation", "AI service unavailable: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/insights")
    @Operation(summary = "Get ML risk scores for all suppliers")
    public ResponseEntity<?> getAllInsights() {
        try {
            var response = restTemplate.getForEntity(
                aiServiceUrl + "/all-supplier-risks", List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/forecast/{itemId}")
    @Operation(summary = "Get demand forecast for an item")
    public ResponseEntity<?> getForecast(
            @PathVariable String itemId,
            @RequestParam(defaultValue="4") int weeks) {
        try {
            String url = aiServiceUrl + "/forecast/"
                + itemId + "?weeks=" + weeks;
            return ResponseEntity.ok(
                restTemplate.getForObject(url, Map.class));
        } catch (Exception e) {
            return ResponseEntity.ok(
                Map.of("error", "Forecast unavailable"));
        }
    }

    @GetMapping("/anomalies")
    @Operation(summary = "Get AI-detected anomalies derived from active alerts")
    public ResponseEntity<?> getAnomalies() {
        List<AlertDetail> alerts = alertRepo.findAll();
        List<Map<String,Object>> anomalies = alerts.stream().map(a -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("itemCode", a.getStringAttribute1());
            m.put("description", a.getShortSummary() != null ? a.getShortSummary() : a.getLongSummary());
            m.put("category", mapCategory(a.getAlertType()));
            m.put("severity", mapSeverity(a.getAlertType(), a.getStringAttribute8()));
            m.put("detectedAt", a.getCreated() != null ? a.getCreated().toString() : null);
            m.put("confidenceScore", deriveConfidence(a));
            m.put("recommendation", buildRecommendation(a));
            List<Map<String,Object>> affected = new ArrayList<>();
            if (a.getStringAttribute1() != null) {
                Map<String,Object> item = new HashMap<>();
                item.put("itemCode", a.getStringAttribute1());
                item.put("itemName", a.getStringAttribute2() != null ? a.getStringAttribute2() : a.getStringAttribute1());
                affected.add(item);
            }
            m.put("affectedItems", affected);
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("anomalies", anomalies, "total", anomalies.size()));
    }

    private String mapCategory(String alertType) {
        if (alertType == null) return "General";
        if (alertType.toLowerCase().contains("cost")) return "Cost";
        if (alertType.toLowerCase().contains("supply") || alertType.toLowerCase().contains("alloc")) return "Supply";
        if (alertType.toLowerCase().contains("forecast") || alertType.toLowerCase().contains("demand")) return "Demand";
        if (alertType.toLowerCase().contains("bom") || alertType.toLowerCase().contains("component")) return "BOM";
        return "Operational";
    }

    private String mapSeverity(String alertType, String state) {
        if ("ESCALATED".equalsIgnoreCase(state) || "CRITICAL".equalsIgnoreCase(state)) return "CRITICAL";
        if (alertType != null && (alertType.toLowerCase().contains("shortage") || alertType.toLowerCase().contains("overdue"))) return "CRITICAL";
        if ("PENDING".equalsIgnoreCase(state) || "ACTIVE".equalsIgnoreCase(state)) return "WARNING";
        return "INFO";
    }

    private int deriveConfidence(AlertDetail a) {
        int base = 70;
        if (a.getStringAttribute1() != null) base += 10;
        if (a.getStringAttribute2() != null) base += 5;
        if (a.getLongSummary() != null && a.getLongSummary().length() > 50) base += 10;
        return Math.min(base, 98);
    }

    private String buildRecommendation(AlertDetail a) {
        String type = a.getAlertType();
        if (type == null) return "Review alert and take appropriate action.";
        if (type.toLowerCase().contains("cost")) return "Review proposed cost change and validate against market benchmarks before approval.";
        if (type.toLowerCase().contains("supply")) return "Check supplier lead times and consider alternate sourcing to prevent stockout.";
        if (type.toLowerCase().contains("forecast")) return "Recalibrate demand forecast using latest sales data and seasonal adjustments.";
        return "Investigate root cause and update business object state accordingly.";
    }
}
