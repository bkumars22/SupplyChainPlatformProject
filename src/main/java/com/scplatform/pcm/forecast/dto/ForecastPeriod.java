/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import java.util.Date;

public class ForecastPeriod
{

    protected String label;
    protected Date startDate;
    protected Date endDate;
    protected ForecastTimeline.ForecastPeriodState state;

    public ForecastPeriod(String label, Date startDate, Date endDate, ForecastTimeline.ForecastPeriodState state)
    {
        super();
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
    }

    public String getLabel()
    {
        return label;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public ForecastTimeline.ForecastPeriodState getState()
    {
        return state;
    }

    public String toString()
    {
        return label + " start=" + startDate + " end=" + endDate +" state=" + state;
    }
}