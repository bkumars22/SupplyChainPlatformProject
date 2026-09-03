/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import java.util.List;

/**
 * Structural single-point-of-failure mapping, independent of current risk
 * levels: lists every upstream dependency this supplier has no alternate
 * source for, even when that upstream source looks healthy right now. A
 * sole-source relationship is worth knowing about before it becomes an
 * active risk, not only after.
 */
public class StructuralRiskDto {
    private String supplierId;
    private List<SoleSourceDependency> soleSourceDependencies;

    public StructuralRiskDto(String supplierId, List<SoleSourceDependency> soleSourceDependencies) {
        this.supplierId = supplierId;
        this.soleSourceDependencies = soleSourceDependencies;
    }

    public String getSupplierId() { return supplierId; }
    public List<SoleSourceDependency> getSoleSourceDependencies() { return soleSourceDependencies; }
    public boolean isHasStructuralRisk() { return !soleSourceDependencies.isEmpty(); }

    public static class SoleSourceDependency {
        private String upstreamSupplierId;
        private String upstreamSupplierName;
        private String componentOrMaterial;

        public SoleSourceDependency(String upstreamSupplierId, String upstreamSupplierName, String componentOrMaterial) {
            this.upstreamSupplierId = upstreamSupplierId;
            this.upstreamSupplierName = upstreamSupplierName;
            this.componentOrMaterial = componentOrMaterial;
        }

        public String getUpstreamSupplierId() { return upstreamSupplierId; }
        public String getUpstreamSupplierName() { return upstreamSupplierName; }
        public String getComponentOrMaterial() { return componentOrMaterial; }
    }
}
