/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

public class SupplierScorecardDto {
    private String supplierId;
    private String supplierName;
    private String country;
    private String tier;
    private double otdScore;          // on-time delivery %
    private double qualityScore;      // defect rate (lower=better), normalised to 0-100
    private double responsivenessScore;
    private double compositeScore;    // weighted average
    private long totalDeliveries;
    private long onTimeDeliveries;
    private boolean atRisk;           // OTD < 70%

    public SupplierScorecardDto(SupplierProfile p, long total, long onTime) {
        this.supplierId = p.getSupplierId();
        this.supplierName = p.getSupplierName();
        this.country = p.getCountry();
        this.tier = p.getTier() != null ? p.getTier().name() : "UNKNOWN";
        this.totalDeliveries = total;
        this.onTimeDeliveries = onTime;
        this.otdScore = total > 0 ? Math.round((onTime * 100.0 / total) * 100.0) / 100.0 : 0;
        this.qualityScore = p.getQualityScore() != null
            ? Math.max(0, 100 - p.getQualityScore()) : 75.0;
        this.responsivenessScore = p.getResponsivenessScore() != null
            ? p.getResponsivenessScore() * 10 : 70.0;
        this.compositeScore = Math.round(
            (this.otdScore * 0.5 + this.qualityScore * 0.3 + this.responsivenessScore * 0.2) * 100.0) / 100.0;
        this.atRisk = this.otdScore < 70;
    }

    public String getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getCountry() { return country; }
    public String getTier() { return tier; }
    public double getOtdScore() { return otdScore; }
    public double getQualityScore() { return qualityScore; }
    public double getResponsivenessScore() { return responsivenessScore; }
    public double getCompositeScore() { return compositeScore; }
    public long getTotalDeliveries() { return totalDeliveries; }
    public long getOnTimeDeliveries() { return onTimeDeliveries; }
    public boolean isAtRisk() { return atRisk; }
}
