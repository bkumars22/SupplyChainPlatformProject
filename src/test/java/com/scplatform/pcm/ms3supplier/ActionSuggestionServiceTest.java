/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionSuggestionServiceTest {

    @Mock private SupplierRiskCascadeService cascadeService;
    @Mock private SupplierEsgService esgService;
    @InjectMocks private ActionSuggestionService service;

    private static EsgRiskDto esgNoData(String id) {
        return EsgRiskDto.noDataAvailable(id);
    }

    private static EsgRiskDto esgWithViolations(String id, int violations) {
        EsgRiskDto dto = new EsgRiskDto();
        dto.setSupplierId(id);
        dto.setEsgDataAvailable(true);
        dto.setComplianceViolations12mo(violations);
        return dto;
    }

    private static CascadedRiskDto noRisk(String id) {
        return new CascadedRiskDto(id, "Supplier " + id, 0.1, 0.0, 0.1, List.of());
    }

    private static CascadedRiskDto withSoleSourceFlag(String id) {
        var flag = new SoleSourceFlag("UP-1", "Upstream Co", "Widget", 0.8, 1);
        return new CascadedRiskDto(id, "Supplier " + id, 0.1, 0.48, 0.58, List.of(flag));
    }

    @Test
    void noRiskFactors_producesNoSuggestions() {
        when(cascadeService.calculateCascadedRisk("A")).thenReturn(noRisk("A"));
        when(esgService.getEsgRisk("A")).thenReturn(esgNoData("A"));

        assertTrue(service.generateSuggestions("A").isEmpty());
    }

    @Test
    void soleSourceFlag_producesDiversifySourcingSuggestionCitingTheUpstreamSupplier() {
        when(cascadeService.calculateCascadedRisk("A")).thenReturn(withSoleSourceFlag("A"));
        when(esgService.getEsgRisk("A")).thenReturn(esgNoData("A"));

        List<ActionSuggestion> suggestions = service.generateSuggestions("A");

        assertEquals(1, suggestions.size());
        ActionSuggestion s = suggestions.get(0);
        assertEquals("diversify_sourcing", s.getType());
        assertEquals("high", s.getPriority());
        assertTrue(s.isRequiresHumanApproval());
        assertTrue(s.getDescription().contains("Upstream Co"), "must cite the specific upstream supplier");
        assertTrue(s.getDescription().contains("Widget"), "must cite the specific component");
    }

    @Test
    void esgViolationsAboveThreshold_producesComplianceReviewSuggestion() {
        when(cascadeService.calculateCascadedRisk("B")).thenReturn(noRisk("B"));
        when(esgService.getEsgRisk("B")).thenReturn(esgWithViolations("B", 3));

        List<ActionSuggestion> suggestions = service.generateSuggestions("B");

        assertEquals(1, suggestions.size());
        assertEquals("esg_compliance_review", suggestions.get(0).getType());
        assertTrue(suggestions.get(0).getDescription().contains("3"), "must cite the specific violation count");
        assertTrue(suggestions.get(0).isRequiresHumanApproval());
    }

    @Test
    void esgViolationsAtOrBelowThreshold_noSuggestion() {
        when(cascadeService.calculateCascadedRisk("B")).thenReturn(noRisk("B"));
        when(esgService.getEsgRisk("B")).thenReturn(esgWithViolations("B", 2));

        assertTrue(service.generateSuggestions("B").isEmpty());
    }

    @Test
    void combinedSoleSourceAndEsgRisk_producesBothSuggestions() {
        // Per the source plan's Important Requirement #4: a supplier with both
        // a sole-source dependency AND genuine ESG concerns must reflect both.
        when(cascadeService.calculateCascadedRisk("C")).thenReturn(withSoleSourceFlag("C"));
        when(esgService.getEsgRisk("C")).thenReturn(esgWithViolations("C", 5));

        List<ActionSuggestion> suggestions = service.generateSuggestions("C");

        assertEquals(2, suggestions.size());
        assertTrue(suggestions.stream().anyMatch(s -> s.getType().equals("diversify_sourcing")));
        assertTrue(suggestions.stream().anyMatch(s -> s.getType().equals("esg_compliance_review")));
        assertTrue(suggestions.stream().allMatch(ActionSuggestion::isRequiresHumanApproval));
    }
}
