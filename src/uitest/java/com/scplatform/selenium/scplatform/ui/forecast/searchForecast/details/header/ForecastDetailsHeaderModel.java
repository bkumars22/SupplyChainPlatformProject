/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast.details.header;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class ForecastDetailsHeaderModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Forecast Type")
    private String forecastType;

    @DisplayName("Calendar Type")
    private String calendarType;

    @DisplayName("Period Type")
    private String periodType;

    /**
     * @return the forecastType
     */
    public String getForecastType() {
        return forecastType;
    }

    /**
     * @param forecastType
     *            the forecastType to set
     */
    public void setForecastType(String forecastType) {
        this.forecastType = forecastType;
    }

    /**
     * @return the calendarType
     */
    public String getCalendarType() {
        return calendarType;
    }

    /**
     * @param calendarType
     *            the calendarType to set
     */
    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    /**
     * @return the periodType
     */
    public String getPeriodType() {
        return periodType;
    }

    /**
     * @param periodType
     *            the periodType to set
     */
    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

}
