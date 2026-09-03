/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import com.scplatform.pcm.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Supplier risk scoring (SupplierScorecardDto) looks at each supplier in
 * isolation. That misses a real gap: a Tier-1 supplier can look healthy on
 * its own metrics while depending entirely on a struggling Tier-2 source
 * for a critical component. This walks the supplier dependency graph
 * (SUPPLIER_DEPENDENCY) to compute an "effective risk" that accounts for
 * that -- without ever replacing the original direct risk, since both
 * numbers mean different things operationally.
 */
@Service
@Transactional
public class SupplierRiskCascadeService {

    // Placeholder defaults, as the source plan for this feature explicitly
    // notes -- real tuning needs real historical incident data (did a
    // Tier-2 disruption actually predict a later Tier-1 problem here?),
    // which doesn't exist yet for this product.
    public static final double DEFAULT_DECAY_FACTOR = 0.6;
    public static final int DEFAULT_MAX_HOPS = 3;

    @Autowired private SupplierRepository supplierRepo;
    @Autowired private DeliveryRepository deliveryRepo;
    @Autowired private SupplierDependencyRepository dependencyRepo;

    public SupplierDependency addDependency(SupplierDependencyRequest req) {
        if (req.getDependentSupplierId().equals(req.getUpstreamSupplierId())) {
            throw new IllegalArgumentException("A supplier cannot depend on itself");
        }
        requireSupplier(req.getDependentSupplierId());
        requireSupplier(req.getUpstreamSupplierId());

        SupplierDependency dep = new SupplierDependency();
        dep.setDependentSupplierId(req.getDependentSupplierId());
        dep.setUpstreamSupplierId(req.getUpstreamSupplierId());
        dep.setComponentOrMaterial(req.getComponentOrMaterial());
        dep.setDependencyCriticality(req.getDependencyCriticality());
        dep.setIsSoleSource(req.getIsSoleSource());
        return dependencyRepo.save(dep);
    }

    public List<SupplierDependency> getAllDependencies() {
        return dependencyRepo.findAll();
    }

    public CascadedRiskDto calculateCascadedRisk(String supplierId) {
        SupplierProfile target = requireSupplier(supplierId);
        Map<String, Double> directRisk = getDirectRiskScores();
        Map<String, String> names = nameLookup();
        List<SupplierDependency> edges = dependencyRepo.findAll();

        CascadeResult result = computeCascade(
            supplierId, directRisk, names, edges, DEFAULT_DECAY_FACTOR, DEFAULT_MAX_HOPS);

        return new CascadedRiskDto(supplierId, target.getSupplierName(), result.directRisk,
            result.cascadedContribution, result.effectiveRisk, result.soleSourceFlags);
    }

    /**
     * Structural sole-source mapping, independent of current risk levels
     * (per the source plan: "sole-source detection should be flagged even
     * when upstream risk is currently low" -- a separate, lower-urgency
     * concern from the active-risk flags in calculateCascadedRisk).
     */
    public StructuralRiskDto getStructuralRisk(String supplierId) {
        requireSupplier(supplierId);
        Map<String, String> names = nameLookup();
        List<StructuralRiskDto.SoleSourceDependency> deps = dependencyRepo.findByDependentSupplierId(supplierId).stream()
            .filter(e -> Boolean.TRUE.equals(e.getIsSoleSource()))
            .map(e -> new StructuralRiskDto.SoleSourceDependency(
                e.getUpstreamSupplierId(),
                names.getOrDefault(e.getUpstreamSupplierId(), e.getUpstreamSupplierId()),
                e.getComponentOrMaterial()))
            .collect(Collectors.toList());
        return new StructuralRiskDto(supplierId, deps);
    }

    private SupplierProfile requireSupplier(String supplierId) {
        return supplierRepo.findById(supplierId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId));
    }

    private Map<String, String> nameLookup() {
        return supplierRepo.findAll().stream()
            .collect(Collectors.toMap(SupplierProfile::getSupplierId, SupplierProfile::getSupplierName));
    }

    /**
     * Converts each supplier's compositeScore (0-100, higher is better --
     * see SupplierScorecardDto) into the 0-1 "higher is worse" risk value
     * the cascade algorithm operates on.
     */
    Map<String, Double> getDirectRiskScores() {
        Map<String, Double> risks = new HashMap<>();
        for (SupplierProfile p : supplierRepo.findAll()) {
            Long total = deliveryRepo.countBySupplierId(p.getSupplierId());
            Long onTime = deliveryRepo.countOnTimeBySupplierId(p.getSupplierId());
            double compositeScore = new SupplierScorecardDto(p, total, onTime).getCompositeScore();
            risks.put(p.getSupplierId(), Math.max(0.0, Math.min(1.0, (100.0 - compositeScore) / 100.0)));
        }
        return risks;
    }

    /**
     * Pure graph traversal, no repository access -- this is what
     * SupplierRiskCascadeServiceTest exercises directly with synthetic
     * dependency graphs, per the source plan's own instruction to verify
     * the decay math by hand before trusting it against real data.
     *
     * BFS outward from supplierId's upstream dependencies, hop by hop, up
     * to maxHops. A node is only ever expanded once (the `visited` guard)
     * -- this is also what makes circular dependencies terminate instead
     * of looping forever, since a node already visited is never re-queued.
     */
    static CascadeResult computeCascade(String supplierId, Map<String, Double> directRiskById,
                                         Map<String, String> namesById, List<SupplierDependency> edges,
                                         double decayFactor, int maxHops) {
        Map<String, List<SupplierDependency>> upstreamOf = edges.stream()
            .collect(Collectors.groupingBy(SupplierDependency::getDependentSupplierId));

        Set<String> visited = new HashSet<>();
        visited.add(supplierId);
        List<String> currentLevel = new ArrayList<>(List.of(supplierId));
        double cascadedContribution = 0.0;
        List<SoleSourceFlag> flags = new ArrayList<>();

        for (int hop = 1; hop <= maxHops && !currentLevel.isEmpty(); hop++) {
            List<String> nextLevel = new ArrayList<>();
            for (String node : currentLevel) {
                for (SupplierDependency edge : upstreamOf.getOrDefault(node, List.of())) {
                    String upstreamId = edge.getUpstreamSupplierId();
                    if (!visited.add(upstreamId)) continue; // already visited -- skip (cycle guard)

                    double upstreamRisk = directRiskById.getOrDefault(upstreamId, 0.0);
                    double criticality = edge.getDependencyCriticality() != null ? edge.getDependencyCriticality() : 0.5;
                    cascadedContribution += upstreamRisk * Math.pow(decayFactor, hop) * criticality;

                    if (Boolean.TRUE.equals(edge.getIsSoleSource()) && upstreamRisk > 0.5) {
                        flags.add(new SoleSourceFlag(upstreamId, namesById.getOrDefault(upstreamId, upstreamId),
                            edge.getComponentOrMaterial(), upstreamRisk, hop));
                    }
                    nextLevel.add(upstreamId);
                }
            }
            currentLevel = nextLevel;
        }

        double directRisk = directRiskById.getOrDefault(supplierId, 0.0);
        double effectiveRisk = Math.min(1.0, directRisk + cascadedContribution);
        return new CascadeResult(round3(directRisk), round3(cascadedContribution), round3(effectiveRisk), flags);
    }

    private static double round3(double v) { return Math.round(v * 1000.0) / 1000.0; }

    static class CascadeResult {
        final double directRisk;
        final double cascadedContribution;
        final double effectiveRisk;
        final List<SoleSourceFlag> soleSourceFlags;

        CascadeResult(double directRisk, double cascadedContribution, double effectiveRisk, List<SoleSourceFlag> soleSourceFlags) {
            this.directRisk = directRisk;
            this.cascadedContribution = cascadedContribution;
            this.effectiveRisk = effectiveRisk;
            this.soleSourceFlags = soleSourceFlags;
        }
    }
}
