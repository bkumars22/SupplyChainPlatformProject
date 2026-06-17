/**
 *	PcmAdjustableForecastValue.java
 *	Created on Apr 24, 2012
 *
 *	Copyright (c) 2012 E2open, Inc.
 *	All Rights Reserved.
 *
 *	THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *	The copyright notice above does not evidence any
 *	actual or intended publication of such source code.
 *
 *	Author: dillo
 */
package com.scplatform.pcm.forecast.service;

import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.enums.AdjustmentType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ResourceBundle;


@Entity
@DiscriminatorValue("A")
@SecondaryTable(name = "PCM_FORECAST_VALUE_ADJUSTMENT")
public class PcmAdjustableForecastValue extends PcmForecastValue  {



    @Column(name = "ADJUSTABLE_VALUE", nullable = true)
    @Basic(optional = false)
    private BigDecimal adjustableValue;

    @Column(table = "PCM_FORECAST_VALUE_ADJUSTMENT", name = "ADJUSTMENT_AMOUNT", nullable = true)
    private BigDecimal adjustmentAmount;

    @Column(table = "PCM_FORECAST_VALUE_ADJUSTMENT", name = "ADJUSTMENT_TYPE", nullable = true)
    @Enumerated(EnumType.STRING)
    private AdjustmentType adjustmentType;
    protected static ResourceBundle messages = ResourceBundle.getBundle("sc-messages");


    public BigDecimal getAdjustableValue() {
        return adjustableValue;
    }

    public void setAdjustableValue(BigDecimal adjustValue) {
        this.adjustableValue = adjustValue;
    }

    public BigDecimal getAdjustmentAmount() {
        return adjustmentAmount;
    }

    public void setAdjustmentAmount(BigDecimal adjustAmount) {
        this.adjustmentAmount = adjustAmount;
    }

    public AdjustmentType getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(AdjustmentType adjustType) {
        this.adjustmentType = adjustType;
    }

    @Override
    @Transient
    public BigDecimal getPitValue() {
        return this.getAdjustableValue();
    }

    @Override
    @Transient
    public BigDecimal getCalculatedForecastValue() {
        AdjustmentType adjType = getAdjustmentType();
        BigDecimal adjVal = getAdjustableValue();
        if (adjType == null || adjVal == null) {
            // Mirror PcmSimpleForecastValue: return the raw value.
            // Scale/rounding normalisation is a presentation concern that
            // callers handle via the injected BigDecimalHelper service.
            return adjVal;
        }
        AdjustedValueCalculator calculator = adjType.getCalculator();
        return calculator.calculate(this.getAdjustableValue(), this.getAdjustmentAmount());
    }

    @Override
    @Transient
    public boolean isValueUnset() {
        return (this.getAdjustableValue() == null);
    }



    @Override
    protected void writeFieldsToJSON(ObjectNode jn){
        super.writeFieldsToJSON(jn);
        jn.put("av", adjustableValue);
        jn.put("amt", adjustmentAmount);
        jn.put("at", adjustmentType == null ? null : adjustmentType.toString());
    }

}
