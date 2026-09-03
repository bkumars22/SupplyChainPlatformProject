/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import java.util.List;

/**
 * directRisk is this supplier's own risk in isolation; effectiveRisk adds
 * decayed risk cascading up from its dependency chain. directRisk is never
 * overwritten by effectiveRisk -- a supplier can look fine on its own
 * metrics (low directRisk) while depending on a struggling upstream source
 * (high effectiveRisk), and that gap is the whole point of this endpoint.
 */
public class CascadedRiskDto {
    private String supplierId;
    private String supplierName;
    private double directRisk;
    private double cascadedContribution;
    private double effectiveRisk;
    private List<SoleSourceFlag> soleSourceRiskFlags;
    private String summary;

    public CascadedRiskDto(String supplierId, String supplierName, double directRisk,
                            double cascadedContribution, double effectiveRisk,
                            List<SoleSourceFlag> soleSourceRiskFlags) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.directRisk = directRisk;
        this.cascadedContribution = cascadedContribution;
        this.effectiveRisk = effectiveRisk;
        this.soleSourceRiskFlags = soleSourceRiskFlags;
        this.summary = soleSourceRiskFlags.isEmpty()
            ? (effectiveRisk < 0.3
                ? "Low risk, no critical single-source dependencies detected"
                : "Elevated effective risk from cascading dependency exposure")
            : String.format("Depends on %d sole-source supplier(s) currently showing elevated risk",
                soleSourceRiskFlags.size());
    }

    public String getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public double getDirectRisk() { return directRisk; }
    public double getCascadedContribution() { return cascadedContribution; }
    public double getEffectiveRisk() { return effectiveRisk; }
    public List<SoleSourceFlag> getSoleSourceRiskFlags() { return soleSourceRiskFlags; }
    public String getSummary() { return summary; }
}
