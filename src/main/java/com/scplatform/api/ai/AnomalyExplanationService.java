/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AnomalyExplanationService {

    private static final Logger logger = LoggerFactory.getLogger(AnomalyExplanationService.class);

    @Value("${anthropic.api.key:#{null}}")
    private String apiKey;

    private static final String SYSTEM_PROMPT = """
        You are an expert supply chain analyst AI assistant for the Supply Chain Intelligence Platform.

        Your job is to analyze supply chain alerts and anomalies and provide:
        1. A clear, concise explanation of WHY the issue likely happened (root cause analysis)
        2. The potential business impact if not addressed
        3. 2-3 specific, actionable recommendations to resolve it

        Keep your response focused and practical. Use supply chain domain knowledge.
        Format your response in 3 clear sections: ROOT CAUSE, BUSINESS IMPACT, RECOMMENDATIONS.
        Be specific - reference actual alert data provided. Keep total response under 250 words.
        """;

    /**
     * Explain a supply chain alert anomaly using Claude AI.
     */
    public AnomalyExplanation explain(AlertContext alert) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("#{null}")) {
            logger.warn("Anthropic API key not configured — returning demo explanation");
            return demoExplanation(alert);
        }

        try {
            AnthropicClient client = AnthropicOkHttpClient.builder()
                    .apiKey(apiKey)
                    .build();

            String userMessage = buildPrompt(alert);

            Message response = client.messages().create(
                    MessageCreateParams.builder()
                            .model(Model.CLAUDE_3_5_SONNET_LATEST)
                            .maxTokens(500)
                            .system(SYSTEM_PROMPT)
                            .addUserMessage(userMessage)
                            .build()
            );

            String text = response.content().get(0).asText().text();

            logger.info("AI explanation generated for alert type: {}", alert.alertType());
            return new AnomalyExplanation(
                    alert.alertType(),
                    alert.summary(),
                    text,
                    "claude-3-5-sonnet",
                    false
            );

        } catch (Exception e) {
            logger.error("Claude API error: {}", e.getMessage());
            return demoExplanation(alert);
        }
    }

    private String buildPrompt(AlertContext alert) {
        return String.format("""
                Please analyze this supply chain alert:

                Alert Type: %s
                Summary: %s
                Item: %s
                Supplier: %s
                Source Site: %s
                Business State: %s
                Created: %s

                Provide root cause analysis, business impact, and recommendations.
                """,
                alert.alertType(),
                alert.summary(),
                alert.item()    != null ? alert.item()    : "N/A",
                alert.supplier()!= null ? alert.supplier(): "N/A",
                alert.site()    != null ? alert.site()    : "N/A",
                alert.state()   != null ? alert.state()   : "N/A",
                alert.created() != null ? alert.created() : "N/A"
        );
    }

    private AnomalyExplanation demoExplanation(AlertContext alert) {
        String text = String.format("""
                ROOT CAUSE
                The %s alert indicates a disruption in the supply chain workflow. Based on the alert pattern, \
                this is likely caused by a mismatch between planned and actual supplier delivery schedules, \
                or a workflow approval bottleneck that has exceeded standard processing time.

                BUSINESS IMPACT
                If unresolved, this could delay downstream production by 3-7 days, affect inventory levels \
                for dependent components, and potentially trigger cascade delays across related BOMs. \
                Financial exposure is estimated at moderate risk level.

                RECOMMENDATIONS
                1. Contact the supplier directly to confirm delivery schedule and get updated ETAs
                2. Evaluate activating a backup supplier from the Approved Vendor List (AVL)
                3. Escalate to supply chain manager if not resolved within 24 hours
                """, alert.alertType());

        return new AnomalyExplanation(
                alert.alertType(),
                alert.summary(),
                text,
                "demo-mode",
                true
        );
    }

    // ── Data records ─────────────────────────────────────────────────────────

    public record AlertContext(
            String alertType, String summary, String item,
            String supplier, String site, String state, String created
    ) {}

    public record AnomalyExplanation(
            String alertType, String alertSummary,
            String explanation, String model, boolean isDemoMode
    ) {}

    // ── ADD THESE METHODS at the bottom, before the last } ──────────────

@org.springframework.beans.factory.annotation.Autowired(required = false)
private org.springframework.web.client.RestTemplate restTemplate =
    new org.springframework.web.client.RestTemplate();

@org.springframework.beans.factory.annotation.Value("${ai.service.url:http://localhost:8001}")
private String aiServiceUrl;

public java.util.Map<String, Object> analyzeSupplierRisk(
        Long supplierId,
        java.util.List<java.util.Map<String,Object>> deliveries) {

    java.util.Map<String, Object> payload = new java.util.HashMap<>();
    payload.put("supplierId", supplierId);
    payload.put("deliveries", deliveries);

    org.springframework.http.HttpHeaders headers =
        new org.springframework.http.HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    org.springframework.http.HttpEntity<java.util.Map<String,Object>> request =
        new org.springframework.http.HttpEntity<>(payload, headers);

    try {
        org.springframework.http.ResponseEntity<java.util.Map> response =
            restTemplate.postForEntity(
                aiServiceUrl + "/analyze-supplier", request, java.util.Map.class);
        return response.getBody();
    } catch (Exception e) {
        logger.error("Python AI service error: {}", e.getMessage());
        java.util.Map<String, Object> fallback = new java.util.HashMap<>();
        fallback.put("supplierId", supplierId);
        fallback.put("riskScore", 0);
        fallback.put("riskLevel", "Unknown");
        fallback.put("explanation", "AI service unavailable");
        return fallback;
    }
}

public java.util.List getAllSupplierRisks() {
    try {
        org.springframework.http.ResponseEntity<java.util.List> res =
            restTemplate.getForEntity(
                aiServiceUrl + "/all-supplier-risks", java.util.List.class);
        return res.getBody();
    } catch (Exception e) {
        logger.error("Could not fetch supplier risks: {}", e.getMessage());
        return java.util.Collections.emptyList();
    }
}

public java.util.Map getForecast(String itemId, int weeks) {
    try {
        String url = aiServiceUrl + "/forecast/" + itemId + "?weeks=" + weeks;
        return restTemplate.getForObject(url, java.util.Map.class);
    } catch (Exception e) {
        return java.util.Map.of("error", "Forecast unavailable");
    }
}
}

