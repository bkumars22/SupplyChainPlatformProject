/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.forecast.dto;


import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.service.ForecastFormRecordDataService;

import com.scplatform.pcm.searchframework.dto.SearchForm;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class ForecastFormRecordData extends SearchForm
{
    protected Long siteKey;
    protected String extendPeriods;
    protected String rowAdjustmentType;
    protected Map<Date,ForecastValues> periodValues = new HashMap<Date,ForecastValues>();
    private final NumberFormat numberFormat;

    public ForecastFormRecordData()
    {
        numberFormat = DecimalFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(6);
        numberFormat.setMinimumFractionDigits(2);
    }

    public ForecastFormRecordData(PcmForecast forecast)
    {
        this();
        copy(forecast);
    }

    public void copy(PcmForecast forecast) {
        SpringContextHolder.getBean(ForecastFormRecordDataService.class).copy(this, forecast);
    }


    public NumberFormat getNumberFormat() {
        return numberFormat;
    }


    public Long getSiteKey()
    {
        return siteKey;
    }
    public void setSiteKey(Long siteKey)
    {
        this.siteKey = siteKey;
    }

    public String getRowAdjustmentType()
    {
        return rowAdjustmentType;
    }

    public void setRowAdjustmentType(String rowAdjustmentType)
    {
        this.rowAdjustmentType = StringUtils.trimToNull(rowAdjustmentType);
    }

    public String getExtendPeriods()
    {
        return extendPeriods;
    }

    public void setExtendPeriods(String extendPeriods)
    {
        this.extendPeriods = StringUtils.trimToNull(extendPeriods);
    }


    public ForecastValues getForecastValues(String time)
    {
        Date period = new Date(Long.valueOf(time));
        return getForecastValuesForDate(period);
    }

    public ForecastValues getForecastValuesForDate(Date period)
    {
        ForecastValues values = periodValues.get(period);
        if (values == null)
        {
            values = new ForecastValues();
            periodValues.put(period, values);
        }
        return values;
    }


    public Map<Date,ForecastValues> getForecastValuesMap()
    {
        return periodValues;
    }

    public abstract static class ForecastValue {
        // Pure data holder; no behavior.
    }

    public static class SimpleForecastValue extends ForecastValue {
        String forecastValue;
        BigDecimal value;

        /**
         * @return the forecastValue
         */
        public String getForecastValue() {
            return this.forecastValue;
        }

        /**
         * @param forecastValue the forecastValue to set
         */
        public void setForecastValue(String forecastValue) {
            this.forecastValue = StringUtils.trimToNull(forecastValue);
        }

        /** Parsed numeric value populated by the validation step in the service. */
        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }

}

