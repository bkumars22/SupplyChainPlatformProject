/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.util;

import java.util.Calendar;
import java.util.List;

import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.fiscalPeriod.service.FiscalPeriodService;
import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import com.scplatform.pcm.forecast.dto.ForecastTimeline.ForecastPeriodState;
import com.scplatform.pcm.util.datetime.DateAndTimeUtils;


public final class ForecastTimelineUtil {

    private ForecastTimelineUtil() {
        // utility class — no instances
    }

    public static ForecastTimeline generateTimeline(FiscalPeriodService fiscalPeriodService,
                                                    String calendarType,
                                                    String periodType,
                                                    Calendar startAt,
                                                    int numHistoryPeriods,
                                                    int numFuturePeriods) {
        FiscalPeriod.PeriodType pt = FiscalPeriod.PeriodType.valueOf(periodType.toUpperCase());
        List<FiscalPeriod> periods = fiscalPeriodService.getFiscalPeriods(
                startAt, pt, numHistoryPeriods, numFuturePeriods);
        return populate(new ForecastTimeline(calendarType, periodType), periods, startAt);
    }

    public static ForecastTimeline generateTimeline(String calendarType,
                                                    String periodType,
                                                    Calendar startAt,
                                                    int numHistoryPeriods,
                                                    int numFuturePeriods,
                                                    List<FiscalPeriod> fiscalPeriodsList) {
        return populate(new ForecastTimeline(calendarType, periodType), fiscalPeriodsList, startAt);
    }

    private static ForecastTimeline populate(ForecastTimeline timeline,
                                             List<FiscalPeriod> periods,
                                             Calendar startAt) {
        for (FiscalPeriod period : periods) {
            ForecastPeriodState state = ForecastPeriodState.CURRENT;
            if (DateAndTimeUtils.before(period.getFiscalPeriodEndDate(), startAt.getTime(), false)) {
                state = ForecastPeriodState.PAST;
            } else if (DateAndTimeUtils.after(period.getFiscalPeriodStartDate(), startAt.getTime(), false)) {
                state = ForecastPeriodState.FUTURE;
            }
            timeline.addPeriod(
                    period.getFiscalPeriodName(),
                    period.getFiscalPeriodStartDate(),
                    period.getFiscalPeriodEndDate(),
                    state);
        }
        return timeline;
    }
}

