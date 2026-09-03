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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScenarioSimulationServiceTest {

    @Mock private SupplierRepository supplierRepo;
    @Mock private SupplierDependencyRepository dependencyRepo;
    @Mock private SupplierRiskCascadeService cascadeService;
    @InjectMocks private ScenarioSimulationService service;

    private static SupplierDependency edge(String dependent, String upstream) {
        SupplierDependency d = new SupplierDependency();
        d.setDependentSupplierId(dependent);
        d.setUpstreamSupplierId(upstream);
        d.setDependencyCriticality(1.0);
        d.setIsSoleSource(false);
        return d;
    }

    // ── findDownstreamAffected: pure function, no mocks ─────────────────────

    @Test
    void findDownstreamAffected_includesTheChangedSupplierItself() {
        var affected = ScenarioSimulationService.findDownstreamAffected(Set.of("A"), List.of(), 3);
        assertEquals(Set.of("A"), affected);
    }

    @Test
    void findDownstreamAffected_walksMultipleHopsDownstream() {
        // A <- B <- C (B depends on A, C depends on B). Changing A affects B and C.
        var edges = List.of(edge("B", "A"), edge("C", "B"));
        var affected = ScenarioSimulationService.findDownstreamAffected(Set.of("A"), edges, 3);
        assertEquals(Set.of("A", "B", "C"), affected);
    }

    @Test
    void findDownstreamAffected_respectsMaxHops() {
        var edges = List.of(edge("B", "A"), edge("C", "B"), edge("D", "C"));
        var affected = ScenarioSimulationService.findDownstreamAffected(Set.of("A"), edges, 2);
        assertEquals(Set.of("A", "B", "C"), affected, "D is 3 hops downstream and must not be included when maxHops=2");
    }

    // ── simulate(): verifiably read-only ─────────────────────────────────────

    @Test
    void simulate_neverWritesToEitherRepository() {
        when(cascadeService.getDirectRiskScores()).thenReturn(Map.of("A", 0.1));
        when(dependencyRepo.findAll()).thenReturn(List.of());
        when(supplierRepo.findAll()).thenReturn(List.of());

        service.simulate(Map.of("A", 0.95));

        verify(supplierRepo, never()).save(any());
        verify(supplierRepo, never()).saveAll(any());
        verify(supplierRepo, never()).delete(any());
        verify(supplierRepo, never()).deleteAll();
        verify(dependencyRepo, never()).save(any());
        verify(dependencyRepo, never()).saveAll(any());
        verify(dependencyRepo, never()).delete(any());
        verify(dependencyRepo, never()).deleteAll();
    }

    @Test
    void simulate_alwaysFlagsResultAsSimulation() {
        when(cascadeService.getDirectRiskScores()).thenReturn(Map.of("A", 0.1));
        when(dependencyRepo.findAll()).thenReturn(List.of());
        when(supplierRepo.findAll()).thenReturn(List.of());

        ScenarioResultDto result = service.simulate(Map.of("A", 0.9));

        assertTrue(result.isSimulation());
        assertEquals(Map.of("A", 0.9), result.getHypotheticalChanges());
    }
}
