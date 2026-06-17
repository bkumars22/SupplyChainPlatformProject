/**
 *	PcmSimpleForecastValue.java
 *	Created on May 11, 2012
 *
 *	Copyright (c) 2012 E2open, Inc.
 *	All Rights Reserved.
 *
 *	THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *	The copyright notice above does not evidence any
 *	actual or intended publication of such source code.
 *
 *	Author: manderson
 */
package com.scplatform.pcm.forecast.entity;

import com.scplatform.pcm.user.entity.Users;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@DiscriminatorValue("S")
public class PcmSimpleForecastValue extends PcmForecastValue {

    @Column(name = "FORECAST_VALUE", nullable = true)
    @Basic(optional = false)
    private BigDecimal forecastValue;

    public BigDecimal getForecastValue() {
        return forecastValue;
    }

    public void setForecastValue(BigDecimal forecastValue) {
        this.forecastValue = forecastValue;
    }

    public PcmSimpleForecastValue() {
        super();
    }

    public PcmSimpleForecastValue(PcmSimpleForecastValue copyValue) {
        super(copyValue);
        this.forecastValue = copyValue.getForecastValue();
    }

    public PcmSimpleForecastValue(Date effectiveFromDt, Date effectiveToDt, String forecastMeasureKey,
                                  BigDecimal forecastValue, String forecastValueUOM) {
        super(effectiveFromDt, effectiveToDt, forecastMeasureKey, forecastValueUOM);
        this.forecastValue = forecastValue;
    }

    @Override
    @Transient
    public BigDecimal getPitValue() {
        return this.getForecastValue();
    }

    @Override
    @Transient
    public BigDecimal getCalculatedForecastValue() {
        return this.getForecastValue();
    }

    @Override
    @Transient
    public boolean isValueUnset() {
        return (this.getForecastValue() == null);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.domain.pcm.PcmForecastValue#writeFieldsToJson(com.fasterxml.jackson.databind.node.ObjectNode)
     */
    @Override
    protected void writeFieldsToJSON(ObjectNode jn) {
        super.writeFieldsToJSON(jn);
        jn.put("fv", forecastValue);
    }

}
