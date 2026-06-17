/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.forecast.dto.ForecastFormRecordData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ForecastValues {
    Map values = new HashMap();

    public Set getMeasureKeys() {
        return values.keySet();
    }

    public ForecastFormRecordData.ForecastValue getMeasureValue(String measureKey) {
        return (ForecastFormRecordData.ForecastValue) values.get(measureKey);
    }

    public ForecastFormRecordData.SimpleForecastValue getSimpleMeasureValue(String measureKey) {
        ForecastFormRecordData.SimpleForecastValue sfv = (ForecastFormRecordData.SimpleForecastValue) values.get(measureKey);
        if (sfv == null) {
            sfv = new ForecastFormRecordData.SimpleForecastValue();
            values.put(measureKey, sfv);
        }
        return sfv;

    }

    public void setSimpleMeasureValue(String measureKey, ForecastFormRecordData.ForecastValue measureValue) {
        values.put(measureKey, measureValue);
    }

    public AdjustableForecastValue getAdjustableMeasureValue(String measureKey) {
        AdjustableForecastValue afv =  (AdjustableForecastValue) values.get(measureKey);
        if (afv == null) {
            afv = new AdjustableForecastValue();
            values.put(measureKey, afv);
        }
        return afv;
    }

    public void setAdjustableMeasureValue(String measureKey, AdjustableForecastValue measureValue) {
        values.put(measureKey, measureValue);
    }

}
