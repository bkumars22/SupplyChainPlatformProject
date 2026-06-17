/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.service;

import org.springframework.stereotype.Service;

import com.scplatform.pcm.forecast.dto.ForecastFormRecordData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class PcmSimpleForecastValue {
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
}
