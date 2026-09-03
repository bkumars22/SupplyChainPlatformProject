/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SUPPLIER_DELIVERY")
public class SupplierDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @JsonIgnore breaks the bidirectional cycle with SupplierProfile.deliveries
    // (supplier -> deliveries[] -> each delivery's supplier -> deliveries[] -> ...).
    // Nothing reads d.supplier.* from the /deliveries response anyway -- the
    // caller already knows which supplier it asked for.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnore
    private SupplierProfile supplier;

    @Column(name = "po_number", length = 100)
    private String poNumber;

    @Column(name = "item_code", length = 50)
    private String itemCode;

    @Column(name = "promised_date")
    private LocalDate promisedDate;

    @Column(name = "actual_date")
    private LocalDate actualDate;

    @Column(name = "qty_ordered")
    private Integer qtyOrdered;

    @Column(name = "qty_received")
    private Integer qtyReceived;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryStatus status;

    @Column(name = "delay_days")
    private Integer delayDays;   // auto-calculated: actualDate - promisedDate

    @Column(name = "notes")
    private String notes;

    public Long getId() { return id; }
    public SupplierProfile getSupplier() { return supplier; }
    public void setSupplier(SupplierProfile v) { this.supplier = v; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String v) { this.poNumber = v; }
    public String getItemCode() { return itemCode; }
    public void setItemCode(String v) { this.itemCode = v; }
    public LocalDate getPromisedDate() { return promisedDate; }
    public void setPromisedDate(LocalDate v) { this.promisedDate = v; }
    public LocalDate getActualDate() { return actualDate; }
    public void setActualDate(LocalDate v) { this.actualDate = v; }
    public Integer getQtyOrdered() { return qtyOrdered; }
    public void setQtyOrdered(Integer v) { this.qtyOrdered = v; }
    public Integer getQtyReceived() { return qtyReceived; }
    public void setQtyReceived(Integer v) { this.qtyReceived = v; }
    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus v) { this.status = v; }
    public Integer getDelayDays() { return delayDays; }
    public void setDelayDays(Integer v) { this.delayDays = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
}
