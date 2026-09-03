/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import jakarta.persistence.*;

/**
 * ESG/sustainability data for a supplier, kept in its own table (rather than
 * added as columns on SupplierProfile) so this feature never touches that
 * entity. Most suppliers won't have this data initially -- the complete
 * absence of a row (not just null fields) is what SupplierEsgService uses
 * to report "no ESG data on file" rather than fabricating a score.
 */
@Entity
@Table(name = "SUPPLIER_ESG_PROFILE")
public class SupplierEsgProfile {

    @Id
    @Column(name = "supplier_id", length = 50)
    private String supplierId;

    @Column(name = "esg_certifications")
    private String esgCertifications;   // comma-separated, e.g. "ISO14001,SA8000"

    @Column(name = "carbon_intensity_score")
    private Double carbonIntensityScore;   // normalized 0-100, lower is better

    @Column(name = "compliance_violations_12mo")
    private Integer complianceViolations12mo;

    @Column(name = "labor_audit_score")
    private Double laborAuditScore;   // 0-100, higher is better; null = no third-party audit on file

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String v) { this.supplierId = v; }
    public String getEsgCertifications() { return esgCertifications; }
    public void setEsgCertifications(String v) { this.esgCertifications = v; }
    public Double getCarbonIntensityScore() { return carbonIntensityScore; }
    public void setCarbonIntensityScore(Double v) { this.carbonIntensityScore = v; }
    public Integer getComplianceViolations12mo() { return complianceViolations12mo; }
    public void setComplianceViolations12mo(Integer v) { this.complianceViolations12mo = v; }
    public Double getLaborAuditScore() { return laborAuditScore; }
    public void setLaborAuditScore(Double v) { this.laborAuditScore = v; }
}
