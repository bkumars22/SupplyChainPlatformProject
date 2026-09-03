/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Suggestions only as good as the risk analysis behind them (cascading
 * risk + ESG, both built in earlier stages of this phase) -- every
 * suggestion here cites the specific flag or figure driving it, never a
 * generic "consider reviewing this supplier".
 */
@Service
public class ActionSuggestionService {

    private static final int ESG_VIOLATION_THRESHOLD = 2;

    @Autowired private SupplierRiskCascadeService cascadeService;
    @Autowired private SupplierEsgService esgService;

    public List<ActionSuggestion> generateSuggestions(String supplierId) {
        List<ActionSuggestion> suggestions = new ArrayList<>();

        CascadedRiskDto cascadedRisk = cascadeService.calculateCascadedRisk(supplierId);
        for (SoleSourceFlag flag : cascadedRisk.getSoleSourceRiskFlags()) {
            suggestions.add(new ActionSuggestion(
                "diversify_sourcing",
                "high",
                String.format(
                    "Depends solely on %s for %s, currently showing elevated risk (%.0f%%). Consider qualifying an alternate source.",
                    flag.getUpstreamSupplierName(),
                    flag.getComponentOrMaterial() != null ? flag.getComponentOrMaterial() : "a critical input",
                    flag.getUpstreamRisk() * 100)
            ));
        }

        EsgRiskDto esgRisk = esgService.getEsgRisk(supplierId);
        if (esgRisk.isEsgDataAvailable() && esgRisk.getComplianceViolations12mo() != null
                && esgRisk.getComplianceViolations12mo() > ESG_VIOLATION_THRESHOLD) {
            suggestions.add(new ActionSuggestion(
                "esg_compliance_review",
                "medium",
                String.format(
                    "%d compliance violations logged in the past 12 months -- recommend a compliance audit before renewal.",
                    esgRisk.getComplianceViolations12mo())
            ));
        }

        return suggestions;
    }
}
