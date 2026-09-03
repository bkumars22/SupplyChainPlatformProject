/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * "What if Supplier X's risk rose to 0.9 -- which other suppliers would be
 * affected, and how badly?" Reuses SupplierRiskCascadeService's real
 * cascade algorithm (computeCascade, getDirectRiskScores) unmodified, but
 * overlays hypothetical risk values instead of only using current real
 * data. Every repository call this service makes is a read
 * (findAll/existsById) -- see ScenarioSimulationServiceTest, which
 * verifies no save/delete ever happens on either repository here.
 */
@Service
public class ScenarioSimulationService {

    @Autowired private SupplierRepository supplierRepo;
    @Autowired private SupplierDependencyRepository dependencyRepo;
    @Autowired private SupplierRiskCascadeService cascadeService;

    public ScenarioResultDto simulate(Map<String, Double> hypotheticalChanges) {
        Map<String, Double> simulatedScores = new HashMap<>(cascadeService.getDirectRiskScores());
        for (Map.Entry<String, Double> e : hypotheticalChanges.entrySet()) {
            simulatedScores.put(e.getKey(), Math.max(0.0, Math.min(1.0, e.getValue())));
        }

        List<SupplierDependency> edges = dependencyRepo.findAll();
        Map<String, String> names = supplierRepo.findAll().stream()
            .collect(Collectors.toMap(SupplierProfile::getSupplierId, SupplierProfile::getSupplierName));

        Set<String> affected = findDownstreamAffected(
            hypotheticalChanges.keySet(), edges, SupplierRiskCascadeService.DEFAULT_MAX_HOPS);

        Map<String, CascadedRiskDto> projectedImpact = new LinkedHashMap<>();
        for (String supplierId : affected) {
            SupplierRiskCascadeService.CascadeResult result = SupplierRiskCascadeService.computeCascade(
                supplierId, simulatedScores, names, edges,
                SupplierRiskCascadeService.DEFAULT_DECAY_FACTOR, SupplierRiskCascadeService.DEFAULT_MAX_HOPS);
            projectedImpact.put(supplierId, new CascadedRiskDto(
                supplierId, names.getOrDefault(supplierId, supplierId),
                result.directRisk, result.cascadedContribution, result.effectiveRisk, result.soleSourceFlags));
        }

        ScenarioResultDto dto = new ScenarioResultDto();
        dto.setHypotheticalChanges(hypotheticalChanges);
        dto.setProjectedImpact(projectedImpact);
        return dto;
    }

    /**
     * Every supplier reachable by walking DOWNSTREAM (dependent-ward) from
     * any of the changed supplier IDs, up to maxHops -- these are exactly
     * the suppliers whose cascaded risk could possibly change as a result
     * of the hypothetical inputs. Pure function, no repository access, so
     * it's directly testable with a synthetic edge list.
     */
    static Set<String> findDownstreamAffected(Set<String> changedIds, List<SupplierDependency> edges, int maxHops) {
        Map<String, List<SupplierDependency>> dependentsOf = edges.stream()
            .collect(Collectors.groupingBy(SupplierDependency::getUpstreamSupplierId));

        Set<String> affected = new HashSet<>(changedIds);
        Set<String> visited = new HashSet<>(changedIds);
        List<String> currentLevel = new ArrayList<>(changedIds);

        for (int hop = 1; hop <= maxHops && !currentLevel.isEmpty(); hop++) {
            List<String> nextLevel = new ArrayList<>();
            for (String node : currentLevel) {
                for (SupplierDependency edge : dependentsOf.getOrDefault(node, List.of())) {
                    String dependentId = edge.getDependentSupplierId();
                    if (!visited.add(dependentId)) continue;
                    affected.add(dependentId);
                    nextLevel.add(dependentId);
                }
            }
            currentLevel = nextLevel;
        }
        return affected;
    }
}
