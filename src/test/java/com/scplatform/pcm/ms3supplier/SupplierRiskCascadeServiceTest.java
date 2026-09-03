/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises SupplierRiskCascadeService.computeCascade() directly with
 * synthetic dependency graphs -- no Spring context or database, since the
 * method takes plain maps/lists. Per the source plan for this feature:
 * "manually verify a few calculated examples by hand match the code's
 * output, before trusting it against real data."
 */
class SupplierRiskCascadeServiceTest {

    private static final double DECAY = 0.6;
    private static final int MAX_HOPS = 3;

    private static SupplierDependency edge(String dependent, String upstream, double criticality, boolean soleSource) {
        SupplierDependency d = new SupplierDependency();
        d.setDependentSupplierId(dependent);
        d.setUpstreamSupplierId(upstream);
        d.setDependencyCriticality(criticality);
        d.setIsSoleSource(soleSource);
        d.setComponentOrMaterial("Widget");
        return d;
    }

    @Test
    void noDependencies_effectiveRiskEqualsDirectRisk() {
        var result = SupplierRiskCascadeService.computeCascade(
            "A", Map.of("A", 0.42), Map.of("A", "Alpha"), List.of(), DECAY, MAX_HOPS);

        assertEquals(0.42, result.directRisk, 1e-9);
        assertEquals(0.0, result.cascadedContribution, 1e-9);
        assertEquals(0.42, result.effectiveRisk, 1e-9);
        assertTrue(result.soleSourceFlags.isEmpty());
    }

    @Test
    void singleHop_soleSourceAboveThreshold_flagsAndAddsDecayedRisk() {
        // A depends solely on B for its input; B is currently high-risk.
        var edges = List.of(edge("A", "B", 1.0, true));
        var directRisk = Map.of("A", 0.1, "B", 0.8);

        var result = SupplierRiskCascadeService.computeCascade(
            "A", directRisk, Map.of("A", "Alpha", "B", "Beta"), edges, DECAY, MAX_HOPS);

        // hand-computed: 0.8 * 0.6^1 * 1.0 = 0.48
        assertEquals(0.48, result.cascadedContribution, 1e-9);
        assertEquals(0.1, result.directRisk, 1e-9);
        assertEquals(0.58, result.effectiveRisk, 1e-9);

        assertEquals(1, result.soleSourceFlags.size());
        SoleSourceFlag flag = result.soleSourceFlags.get(0);
        assertEquals("B", flag.getUpstreamSupplierId());
        assertEquals(1, flag.getHopDistance());
        assertEquals(0.8, flag.getUpstreamRisk(), 1e-9);
    }

    @Test
    void soleSourceBelowRiskThreshold_doesNotFlag() {
        // Sole-sourced, but the upstream supplier isn't currently risky (<= 0.5)
        // -- this is exactly what the separate structural-risk endpoint is for.
        var edges = List.of(edge("A", "B", 1.0, true));
        var directRisk = Map.of("A", 0.1, "B", 0.5);

        var result = SupplierRiskCascadeService.computeCascade(
            "A", directRisk, Map.of("A", "Alpha", "B", "Beta"), edges, DECAY, MAX_HOPS);

        assertTrue(result.soleSourceFlags.isEmpty());
    }

    @Test
    void twoHopChain_decaysFurtherForTheSecondHop() {
        // A <- B <- C. A and B look fine directly; C (2 hops away) is risky.
        var edges = List.of(
            edge("A", "B", 0.5, false),
            edge("B", "C", 1.0, false)
        );
        var directRisk = Map.of("A", 0.0, "B", 0.0, "C", 0.9);

        var result = SupplierRiskCascadeService.computeCascade(
            "A", directRisk, Map.of("A", "A", "B", "B", "C", "C"), edges, DECAY, MAX_HOPS);

        // hop 1 (B): risk 0 contributes nothing.
        // hop 2 (C): 0.9 * 0.6^2 * 1.0 = 0.324
        assertEquals(0.324, result.cascadedContribution, 1e-9);
        assertEquals(0.324, result.effectiveRisk, 1e-9);
    }

    @Test
    void maxHopsCap_ignoresRiskBeyondTheLimit() {
        // A <- B <- C <- D, but maxHops = 2 -- D (3 hops away) must never be reached.
        var edges = List.of(
            edge("A", "B", 1.0, false),
            edge("B", "C", 1.0, false),
            edge("C", "D", 1.0, false)
        );
        var directRisk = Map.of("A", 0.0, "B", 0.0, "C", 0.0, "D", 1.0);

        var result = SupplierRiskCascadeService.computeCascade(
            "A", directRisk, Map.of(), edges, DECAY, 2);

        assertEquals(0.0, result.cascadedContribution, 1e-9,
            "D is 3 hops away and must not contribute when maxHops=2");
    }

    @Test
    void circularDependency_terminatesInsteadOfLooping() {
        // A <- B <- A (circular). Must terminate, not hang or throw.
        var edges = List.of(
            edge("A", "B", 1.0, false),
            edge("B", "A", 1.0, false)
        );
        var directRisk = Map.of("A", 0.2, "B", 0.6);

        SupplierRiskCascadeService.CascadeResult result = assertTimeoutPreemptively(
            java.time.Duration.ofSeconds(2),
            () -> SupplierRiskCascadeService.computeCascade(
                "A", directRisk, Map.of("A", "A", "B", "B"), edges, DECAY, MAX_HOPS));

        // A's upstream is B (visited once, hop 1); B's upstream is A, but A is
        // already visited so the cycle stops there instead of re-expanding.
        assertEquals(0.6 * Math.pow(DECAY, 1) * 1.0, result.cascadedContribution, 1e-9);
    }

    @Test
    void missingDirectRiskEntry_defaultsToZero() {
        var result = SupplierRiskCascadeService.computeCascade(
            "unknown-supplier", Map.of(), Map.of(), List.of(), DECAY, MAX_HOPS);

        assertEquals(0.0, result.directRisk, 1e-9);
        assertEquals(0.0, result.effectiveRisk, 1e-9);
    }
}
