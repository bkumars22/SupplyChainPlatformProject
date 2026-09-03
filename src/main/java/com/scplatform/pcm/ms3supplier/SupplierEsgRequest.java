/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import java.util.List;

public class SupplierEsgRequest {
    private List<String> esgCertifications;
    private Double carbonIntensityScore;
    private Integer complianceViolations12mo;
    private Double laborAuditScore;

    public List<String> getEsgCertifications() { return esgCertifications; }
    public void setEsgCertifications(List<String> v) { this.esgCertifications = v; }
    public Double getCarbonIntensityScore() { return carbonIntensityScore; }
    public void setCarbonIntensityScore(Double v) { this.carbonIntensityScore = v; }
    public Integer getComplianceViolations12mo() { return complianceViolations12mo; }
    public void setComplianceViolations12mo(Integer v) { this.complianceViolations12mo = v; }
    public Double getLaborAuditScore() { return laborAuditScore; }
    public void setLaborAuditScore(Double v) { this.laborAuditScore = v; }
}
