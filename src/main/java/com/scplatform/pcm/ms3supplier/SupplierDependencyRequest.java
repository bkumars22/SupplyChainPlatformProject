/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class SupplierDependencyRequest {
    @NotBlank private String dependentSupplierId;
    @NotBlank private String upstreamSupplierId;
    private String componentOrMaterial;
    @DecimalMin("0.0") @DecimalMax("1.0") private Double dependencyCriticality = 0.5;
    private Boolean isSoleSource = false;

    public String getDependentSupplierId() { return dependentSupplierId; }
    public void setDependentSupplierId(String v) { this.dependentSupplierId = v; }
    public String getUpstreamSupplierId() { return upstreamSupplierId; }
    public void setUpstreamSupplierId(String v) { this.upstreamSupplierId = v; }
    public String getComponentOrMaterial() { return componentOrMaterial; }
    public void setComponentOrMaterial(String v) { this.componentOrMaterial = v; }
    public Double getDependencyCriticality() { return dependencyCriticality; }
    public void setDependencyCriticality(Double v) { this.dependencyCriticality = v; }
    public Boolean getIsSoleSource() { return isSoleSource; }
    public void setIsSoleSource(Boolean v) { this.isSoleSource = v; }
}
