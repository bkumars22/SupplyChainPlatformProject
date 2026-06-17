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
import com.scplatform.pcm.forecast.service.ChangeRecordService;

public class ForecastChange extends ChangeRecord
{
    private PcmForecast forecast;
    public ForecastChange(PcmForecast target)
    {
        this.forecast = target;
        recordId = generateRecordId(forecast);
    }

    public void setForecast(PcmForecast target)
    {
        this.forecast = target;
    }

    public PcmForecast getForecast()
    {
        return forecast;
    }

    public static String generateRecordId(PcmForecast target)
    {
        return SpringContextHolder.getBean(ChangeRecordService.class).generateRecordId(target);
    }

    public void setRecordId(String newRecordId) {
        recordId = newRecordId;
    }

}