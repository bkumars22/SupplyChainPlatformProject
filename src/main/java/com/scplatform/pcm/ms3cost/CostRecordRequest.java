/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3cost;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CostRecordRequest {
    @NotBlank(message = "Item code is required")
    private String itemCode;
    @NotNull(message = "Proposed cost is required")
    @DecimalMin(value = "0.0001", message = "Cost must be greater than zero")
    private BigDecimal proposedCost;
    @NotBlank(message = "Justification is required")
    private String justification;

    public String getItemCode() { return itemCode; }
    public void setItemCode(String v) { this.itemCode = v; }
    public BigDecimal getProposedCost() { return proposedCost; }
    public void setProposedCost(BigDecimal v) { this.proposedCost = v; }
    public String getJustification() { return justification; }
    public void setJustification(String v) { this.justification = v; }
}