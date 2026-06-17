/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SUPPLIER_PROFILE")
public class SupplierProfile {

    @Id
    @Column(name = "supplier_id", length = 50)
    private String supplierId;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier")
    private SupplierTier tier;

    @Column(name = "onboarded_date")
    private LocalDate onboardedDate;

    @Column(name = "quality_score")
    private Double qualityScore;   // defect rate % — manual input, lower is better

    @Column(name = "responsiveness_score")
    private Double responsivenessScore;  // 1-10 manual rating

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupplierDelivery> deliveries = new ArrayList<>();

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String v) { this.supplierId = v; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String v) { this.supplierName = v; }
    public String getCountry() { return country; }
    public void setCountry(String v) { this.country = v; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String v) { this.contactEmail = v; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String v) { this.contactPhone = v; }
    public SupplierTier getTier() { return tier; }
    public void setTier(SupplierTier v) { this.tier = v; }
    public LocalDate getOnboardedDate() { return onboardedDate; }
    public void setOnboardedDate(LocalDate v) { this.onboardedDate = v; }
    public Double getQualityScore() { return qualityScore; }
    public void setQualityScore(Double v) { this.qualityScore = v; }
    public Double getResponsivenessScore() { return responsivenessScore; }
    public void setResponsivenessScore(Double v) { this.responsivenessScore = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
    public List<SupplierDelivery> getDeliveries() { return deliveries; }
    public void setDeliveries(List<SupplierDelivery> v) { this.deliveries = v; }
}
