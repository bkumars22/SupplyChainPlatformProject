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

@Service
@Transactional
public class SupplierEsgService {

    // Same "no data yet" neutral default the plan's own feature-vector sketch
    // uses -- applied here per missing field, not as a blanket stand-in for
    // "no ESG data at all" (that case returns esgDataAvailable=false instead).
    private static final double NEUTRAL_DEFAULT = 50.0;
    private static final double VIOLATION_PENALTY = 20.0;

    @Autowired private SupplierRepository supplierRepo;
    @Autowired private SupplierEsgProfileRepository esgRepo;

    public SupplierEsgProfile upsertEsgProfile(String supplierId, SupplierEsgRequest req) {
        if (!supplierRepo.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found: " + supplierId);
        }
        SupplierEsgProfile profile = esgRepo.findById(supplierId).orElseGet(() -> {
            SupplierEsgProfile p = new SupplierEsgProfile();
            p.setSupplierId(supplierId);
            return p;
        });
        profile.setEsgCertifications(req.getEsgCertifications() == null ? null : String.join(",", req.getEsgCertifications()));
        profile.setCarbonIntensityScore(req.getCarbonIntensityScore());
        profile.setComplianceViolations12mo(req.getComplianceViolations12mo());
        profile.setLaborAuditScore(req.getLaborAuditScore());
        return esgRepo.save(profile);
    }

    public EsgRiskDto getEsgRisk(String supplierId) {
        if (!supplierRepo.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found: " + supplierId);
        }
        return esgRepo.findById(supplierId)
            .map(SupplierEsgService::score)
            .orElseGet(() -> EsgRiskDto.noDataAvailable(supplierId));
    }

    /**
     * Pure scoring logic, no repository access -- directly testable with
     * synthetic profiles (full data, partial data, all-null fields).
     * Missing individual fields get a neutral default AND are listed in
     * missingFields, so the score is real but its gaps stay visible rather
     * than being silently absorbed into a falsely confident number.
     */
    static EsgRiskDto score(SupplierEsgProfile profile) {
        EsgRiskDto dto = new EsgRiskDto();
        dto.setSupplierId(profile.getSupplierId());
        dto.setEsgDataAvailable(true);
        dto.setEsgCertifications(profile.getEsgCertifications() == null || profile.getEsgCertifications().isBlank()
            ? List.of() : Arrays.asList(profile.getEsgCertifications().split(",")));
        dto.setCarbonIntensityScore(profile.getCarbonIntensityScore());
        dto.setComplianceViolations12mo(profile.getComplianceViolations12mo());
        dto.setLaborAuditScore(profile.getLaborAuditScore());

        List<String> missing = new ArrayList<>();

        double carbonComponent;
        if (profile.getCarbonIntensityScore() != null) {
            carbonComponent = clamp(100.0 - profile.getCarbonIntensityScore());
        } else {
            carbonComponent = NEUTRAL_DEFAULT;
            missing.add("carbonIntensityScore");
        }

        double complianceComponent;
        if (profile.getComplianceViolations12mo() != null) {
            complianceComponent = clamp(100.0 - profile.getComplianceViolations12mo() * VIOLATION_PENALTY);
        } else {
            complianceComponent = NEUTRAL_DEFAULT;
            missing.add("complianceViolations12mo");
        }

        double laborComponent;
        if (profile.getLaborAuditScore() != null) {
            laborComponent = clamp(profile.getLaborAuditScore());
        } else {
            laborComponent = NEUTRAL_DEFAULT;
            missing.add("laborAuditScore");
        }

        dto.setMissingFields(missing);
        double esgScore = (carbonComponent + complianceComponent + laborComponent) / 3.0;
        dto.setEsgRiskScore(Math.round(esgScore * 10.0) / 10.0);
        dto.setMessage(missing.isEmpty()
            ? "ESG score based on complete data"
            : "ESG score partially estimated -- missing: " + String.join(", ", missing));
        return dto;
    }

    private static double clamp(double v) { return Math.max(0.0, Math.min(100.0, v)); }
}
