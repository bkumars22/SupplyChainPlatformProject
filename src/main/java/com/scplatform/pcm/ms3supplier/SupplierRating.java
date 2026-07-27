/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SUPPLIER_QUALITY_RATING")
public class SupplierRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false, length = 50)
    private String supplierId;

    @Column(name = "rated_by", nullable = false, length = 100)
    private String ratedBy;

    @Column(name = "rating_date", nullable = false)
    private LocalDate ratingDate;

    @Column(name = "quality_score", nullable = false, precision = 4, scale = 1)
    private BigDecimal qualityScore;

    @Column(name = "delivery_score", nullable = false, precision = 4, scale = 1)
    private BigDecimal deliveryScore;

    @Column(name = "responsiveness", nullable = false, precision = 4, scale = 1)
    private BigDecimal responsiveness;

    @Column(name = "overall_score", precision = 4, scale = 1, insertable = false, updatable = false)
    private BigDecimal overallScore;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getRatedBy() { return ratedBy; }
    public void setRatedBy(String ratedBy) { this.ratedBy = ratedBy; }

    public LocalDate getRatingDate() { return ratingDate; }
    public void setRatingDate(LocalDate ratingDate) { this.ratingDate = ratingDate; }

    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }

    public BigDecimal getDeliveryScore() { return deliveryScore; }
    public void setDeliveryScore(BigDecimal deliveryScore) { this.deliveryScore = deliveryScore; }

    public BigDecimal getResponsiveness() { return responsiveness; }
    public void setResponsiveness(BigDecimal responsiveness) { this.responsiveness = responsiveness; }

    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
