/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.forecast.enums.AdjustmentType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class AdjustableForecastValue extends ForecastFormRecordData.ForecastValue {

    private String adjustmentAmount;
    private String adjustmentType;

     private BigDecimal adjAmnt;
      private AdjustmentType type;

    public AdjustableForecastValue() {
        // default constructor
    }

    public String getAdjustmentAmount() {
        return this.adjustmentAmount;
    }

    public void setAdjustmentAmount(String adjustmentAmount) {
        this.adjustmentAmount = StringUtils.trimToNull(adjustmentAmount);
    }

    public String getAdjustmentType() {
        return this.adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = StringUtils.trimToNull(adjustmentType);
    }

    public BigDecimal getAdjAmnt() {
        return adjAmnt;
    }

    public void setAdjAmnt(BigDecimal adjAmnt) {
        this.adjAmnt = adjAmnt;
    }

    public AdjustmentType getType() {
        return type;
    }

    public void setType(AdjustmentType type) {
        this.type = type;
    }
}
