/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3cost;

import com.scplatform.pcm.item.entity.Item;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MS3_COST_RECORD")
public class CostRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_code", nullable = false)
    private Item item;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "proposed_cost", precision = 18, scale = 4, nullable = false)
    private BigDecimal proposedCost;

    @Column(name = "previous_cost", precision = 18, scale = 4)
    private BigDecimal previousCost;

    @Column(name = "change_percent", precision = 8, scale = 2)
    private BigDecimal changePercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CostStatus status;

    @Column(name = "justification", nullable = false)
    private String justification;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public Long getId() { return id; }
    public Item getItem() { return item; }
    public void setItem(Item v) { this.item = v; }
    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer v) { this.versionNumber = v; }
    public BigDecimal getProposedCost() { return proposedCost; }
    public void setProposedCost(BigDecimal v) { this.proposedCost = v; }
    public BigDecimal getPreviousCost() { return previousCost; }
    public void setPreviousCost(BigDecimal v) { this.previousCost = v; }
    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal v) { this.changePercent = v; }
    public CostStatus getStatus() { return status; }
    public void setStatus(CostStatus v) { this.status = v; }
    public String getJustification() { return justification; }
    public void setJustification(String v) { this.justification = v; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String v) { this.rejectionReason = v; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String v) { this.createdBy = v; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime v) { this.createdDate = v; }
    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDateTime v) { this.submittedDate = v; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String v) { this.approvedBy = v; }
    public LocalDateTime getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDateTime v) { this.approvedDate = v; }
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime v) { this.lastModifiedDate = v; }
}