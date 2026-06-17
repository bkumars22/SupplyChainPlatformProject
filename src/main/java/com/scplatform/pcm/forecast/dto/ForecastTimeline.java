/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;


public class ForecastTimeline {

    /** Lifecycle state of a single period relative to "now". */
    public enum ForecastPeriodState { PAST, CURRENT, FUTURE }

    private final List<ForecastPeriod> periods = new ArrayList<>();
    private final String calendarName;
    private final String periodType;

    public ForecastTimeline(String calendarName, String periodType) {
        this.calendarName = calendarName;
        this.periodType = periodType;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public String getPeriodType() {
        return periodType;
    }

    public List<ForecastPeriod> getPeriods() {
        return periods;
    }

    public void clearPeriods() {
        periods.clear();
    }

    public void addPeriod(String label, Date startDate, Date endDate, ForecastPeriodState state) {
        periods.add(new ForecastPeriod(label, startDate, endDate, state));
    }

    public int getCurrentPeriodIndex() {
        for (int idx = 0; idx < periods.size(); idx++) {
            if (periods.get(idx).getState() == ForecastPeriodState.CURRENT) {
                return idx;
            }
        }
        return -1;
    }

    public ForecastPeriod getCurrentPeriod() {
        for (ForecastPeriod fp : periods) {
            if (fp.getState() == ForecastPeriodState.CURRENT) {
                return fp;
            }
        }
        return null;
    }

    public int getFuturePeriodCount() {
        int cnt = 0;
        for (ForecastPeriod fp : periods) {
            if (fp.getState() == ForecastPeriodState.FUTURE) {
                cnt++;
            }
        }
        return cnt;
    }

    public ForecastPeriod getPeriod(Date startDate) {
        for (ForecastPeriod fp : periods) {
            if (DateUtils.isSameDay(fp.getStartDate(), startDate)) {
                return fp;
            }
        }
        return null;
    }

    public int getPastPeriodCount() {
        int cnt = 0;
        for (ForecastPeriod fp : periods) {
            if (fp.getState() == ForecastPeriodState.PAST) {
                cnt++;
            }
        }
        return cnt;
    }
}

