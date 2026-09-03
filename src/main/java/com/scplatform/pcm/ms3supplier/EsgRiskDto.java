/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import java.util.List;

/**
 * esgDataAvailable is false only when there is no ESG profile row at all
 * for this supplier -- esgRiskScore is null in that case rather than a
 * fabricated neutral value, per the explicit requirement that missing ESG
 * data must be reported honestly, not silently treated as "good".
 *
 * When a profile row exists but some individual fields are missing,
 * esgDataAvailable is true, missingFields lists exactly which inputs were
 * defaulted, and esgRiskScore is computed from whatever real data exists
 * plus neutral defaults for the rest -- so the score is real, and the
 * gaps in it are visible rather than hidden.
 */
public class EsgRiskDto {
    private String supplierId;
    private boolean esgDataAvailable;
    private Double esgRiskScore;   // 0-100, higher is better; null if esgDataAvailable is false
    private List<String> esgCertifications;
    private Double carbonIntensityScore;
    private Integer complianceViolations12mo;
    private Double laborAuditScore;
    private List<String> missingFields;
    private String message;

    public static EsgRiskDto noDataAvailable(String supplierId) {
        EsgRiskDto dto = new EsgRiskDto();
        dto.supplierId = supplierId;
        dto.esgDataAvailable = false;
        dto.message = "No ESG data on file for this supplier yet";
        return dto;
    }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String v) { this.supplierId = v; }
    public boolean isEsgDataAvailable() { return esgDataAvailable; }
    public void setEsgDataAvailable(boolean v) { this.esgDataAvailable = v; }
    public Double getEsgRiskScore() { return esgRiskScore; }
    public void setEsgRiskScore(Double v) { this.esgRiskScore = v; }
    public List<String> getEsgCertifications() { return esgCertifications; }
    public void setEsgCertifications(List<String> v) { this.esgCertifications = v; }
    public Double getCarbonIntensityScore() { return carbonIntensityScore; }
    public void setCarbonIntensityScore(Double v) { this.carbonIntensityScore = v; }
    public Integer getComplianceViolations12mo() { return complianceViolations12mo; }
    public void setComplianceViolations12mo(Integer v) { this.complianceViolations12mo = v; }
    public Double getLaborAuditScore() { return laborAuditScore; }
    public void setLaborAuditScore(Double v) { this.laborAuditScore = v; }
    public List<String> getMissingFields() { return missingFields; }
    public void setMissingFields(List<String> v) { this.missingFields = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
}
