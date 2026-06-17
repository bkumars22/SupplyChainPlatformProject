/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SupplierDeliveryRequest {
    @NotBlank private String poNumber;
    private String itemCode;
    @NotNull private LocalDate promisedDate;
    private LocalDate actualDate;
    @NotNull private Integer qtyOrdered;
    private Integer qtyReceived;
    private String notes;

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
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
}
