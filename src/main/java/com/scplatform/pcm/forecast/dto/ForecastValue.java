/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.forecast.entity.PcmForecastValue;
public abstract class ForecastValue {

    abstract void validate(String prop) throws Exception;

    abstract boolean isValueSet();

    abstract void updatePcmForecastValue(PcmForecastValue pfv, ForecastChange changeRecord, ForecastTimeline timeline);

    /**
     * @return
     */
    abstract PcmForecastValue createPcmForecastValue();
}