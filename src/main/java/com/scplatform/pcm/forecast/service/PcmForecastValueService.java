/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;


import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import com.scplatform.pcm.common.dto.ChangeTracker;
import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.fiscalPeriod.service.FiscalPeriodService;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue;
import com.scplatform.pcm.util.common.SCPlatformConstant;
import com.scplatform.pcm.util.datetime.DateAndTimeUtils;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


@Service
@RequiredArgsConstructor
public class PcmForecastValueService {

    private final FiscalPeriodService fiscalPeriodService;

    public SortedMap<Date, Map<String, PcmForecastValue>> getForecastValuesByPeriod(PcmForecast forecast) {
        SortedMap<Date, Map<String, PcmForecastValue>> results = new TreeMap<>();
        for (PcmForecastValue value : forecast.getForecastValues()) {
            results.computeIfAbsent(value.getEffectiveFromDt(),
                            k -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER))
                   .put(value.getForecastMeasureKey(), value);
        }
        return Collections.unmodifiableSortedMap(results);
    }

    public Map<String, PcmForecastValue> getCurrentForecastValues(PcmForecast forecast) {
        return getForecastValuesForDate(forecast, new Date());
    }

    public Map<String, PcmForecastValue> getForecastValuesForDate(PcmForecast forecast, Date startDate) {
        Map<String, PcmForecastValue> results = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (PcmForecastValue value : forecast.getForecastValues()) {
            if (DateAndTimeUtils.between(startDate, value.getEffectiveFromDt(), value.getEffectiveToDt())) {
                results.put(value.getForecastMeasureKey(), value);
            }
        }
        return Collections.unmodifiableMap(results);
    }

    public PcmForecastValue getForecastValueByPath(PcmForecast forecast, String path) {
        for (PcmForecastValue value : forecast.getForecastValues()) {
            if (value.getPath().equalsIgnoreCase(path)) {
                return value;
            }
        }
        return null;
    }

    public SortedSet<PcmForecastValue> getForecastValuesForMeasure(PcmForecast forecast, String measureName) {
        SortedSet<PcmForecastValue> results = new TreeSet<>();
        for (PcmForecastValue value : forecast.getForecastValues()) {
            if (value.getForecastMeasureKey().equalsIgnoreCase(measureName)) {
                results.add(value);
            }
        }
        return Collections.unmodifiableSortedSet(results);
    }

    /** Returns the values mapped by the measure name. */
    public Map<String, SortedSet<PcmForecastValue>> getForecastValuesByMeasure(PcmForecast forecast) {
        Map<String, SortedSet<PcmForecastValue>> results = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (PcmForecastValue value : forecast.getForecastValues()) {
            results.computeIfAbsent(value.getForecastMeasureKey(), k -> new TreeSet<>())
                   .add(value);
        }
        return Collections.unmodifiableMap(results);
    }

    public PcmForecastValue getForecastValue(PcmForecast forecast, String measureName, Date startDate) {
        for (PcmForecastValue value : forecast.getForecastValues()) {
            if (value.getForecastMeasureKey().equalsIgnoreCase(measureName)
                    && DateUtils.isSameDay(value.getEffectiveFromDt(), startDate)) {
                return value;
            }
        }
        return null;
    }

    public boolean addForecastValue(PcmForecast forecast, PcmForecastValue value) {
        value.setForecast(forecast);
        return forecast.getForecastValues().add(value);
    }

    public boolean removeForecastValue(PcmForecast forecast, PcmForecastValue value) {
        if (forecast.getForecastValues().remove(value)) {
            value.setForecast(null);
            return true;
        }
        return false;
    }

    public SortedSet<PcmForecastValue> removeForecastValuesBeyondDate(PcmForecast forecast, Date startDate) {
        SortedSet<PcmForecastValue> removedValues = new TreeSet<>();
        Iterator<PcmForecastValue> itr = forecast.getForecastValues().iterator();
        while (itr.hasNext()) {
            PcmForecastValue fv = itr.next();
            if (DateAndTimeUtils.after(fv.getEffectiveFromDt(), startDate, false)) {
                itr.remove();
                removedValues.add(fv);
            }
        }
        return removedValues;
    }

    public SortedSet<PcmForecastValue> getForecastValuesBeyondDate(PcmForecast forecast, Date startDate) {
        SortedSet<PcmForecastValue> result = new TreeSet<>();
        for (PcmForecastValue fv : forecast.getForecastValues()) {
            if (DateAndTimeUtils.after(fv.getEffectiveFromDt(), startDate, false)) {
                result.add(fv);
            }
        }
        return result;
    }

    public ForecastTimeline generateTimeline(String calendarType, String periodType,
                                             Calendar startAt, int numHistoryPeriods, int numFuturePeriods) {
        FiscalPeriod.PeriodType pt = FiscalPeriod.PeriodType.valueOf(periodType.toUpperCase());
        List<FiscalPeriod> periods = fiscalPeriodService.getFiscalPeriods(
                startAt, pt, numHistoryPeriods, numFuturePeriods);
        return buildTimeline(calendarType, periodType, startAt, periods);
    }

    public ForecastTimeline generateTimeline(String calendarType, String periodType,
                                             Calendar startAt, int numHistoryPeriods, int numFuturePeriods,
                                             List<FiscalPeriod> fiscalPeriodsList) {
        return buildTimeline(calendarType, periodType, startAt, fiscalPeriodsList);
    }

    private ForecastTimeline buildTimeline(String calendarType, String periodType,
                                           Calendar startAt, List<FiscalPeriod> periods) {
        ForecastTimeline timeline = new ForecastTimeline(calendarType, periodType);
        for (FiscalPeriod period : periods) {
            ForecastTimeline.ForecastPeriodState state = ForecastTimeline.ForecastPeriodState.CURRENT;
            if (DateAndTimeUtils.before(period.getFiscalPeriodEndDate(), startAt.getTime(), false)) {
                state = ForecastTimeline.ForecastPeriodState.PAST;
            } else if (DateAndTimeUtils.after(period.getFiscalPeriodStartDate(), startAt.getTime(), false)) {
                state = ForecastTimeline.ForecastPeriodState.FUTURE;
            }
            timeline.addPeriod(period.getFiscalPeriodName(), period.getFiscalPeriodStartDate(),
                    period.getFiscalPeriodEndDate(), state);
        }
        return timeline;
    }

    public String getTitle(PcmForecastValue value, boolean includeValue) {
        if (value instanceof PcmSimpleForecastValue) {
            return getSimpleTitle((PcmSimpleForecastValue) value, includeValue);
        }
        if (value instanceof PcmAdjustableForecastValue) {
            return getAdjustableTitle((PcmAdjustableForecastValue) value, includeValue);
        }
        return defaultTitle(value);
    }

    private String getSimpleTitle(PcmSimpleForecastValue value, boolean includeValue) {
        SimpleDateFormat sdf = new SimpleDateFormat(resolveDateFormat());
        List<Object> args = baseTitleArgs(value, sdf);
        if (!includeValue) {
            return SCPlatformMessages.INSTANCE.getAuditMessage("audit.currentForecastTitle", args.toArray(), null);
        }
        args.add(value.getForecastValue());
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.currentForecastTitleWithValue", args.toArray(), null);
    }

    private String getAdjustableTitle(PcmAdjustableForecastValue value, boolean includeValue) {
        SimpleDateFormat sdf = new SimpleDateFormat(resolveDateFormat());
        List<Object> args = baseTitleArgs(value, sdf);
        if (!includeValue) {
            return SCPlatformMessages.INSTANCE.getAuditMessage("audit.adjustableForecastTitle", args.toArray(), null);
        }
        args.add(value.getAdjustableValue());
        args.add(value.getAdjustmentType());
        args.add(value.getAdjustmentAmount());
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.adjustableForecastTitleWithValue", args.toArray(), null);
    }

    private String resolveDateFormat() {
        String df = SCPlatformMessages.INSTANCE.getAuditMessage("audit.dateFormat", null, null);
        if (df == null) {
            df = SCPlatformConstant.DEFAULT_DATE_FORMAT;
        }
        return df;
    }

    private List<Object> baseTitleArgs(PcmForecastValue value, SimpleDateFormat sdf) {
        List<Object> args = new ArrayList<>();
        args.add(value.getForecastMeasureKey());
        args.add(sdf.format(value.getEffectiveFromDt()));
        args.add(value.getEffectiveToDt() != null ? sdf.format(value.getEffectiveToDt()) : "");
        return args;
    }

    private String defaultTitle(PcmForecastValue value) {
        SimpleDateFormat sdf = new SimpleDateFormat(resolveDateFormat());
        StringBuilder sb = new StringBuilder();
        sb.append(value.getForecastMeasureKey()).append(' ');
        sb.append(sdf.format(value.getEffectiveFromDt()));
        if (value.getEffectiveToDt() != null) {
            sb.append(" - ").append(sdf.format(value.getEffectiveToDt()));
        }
        return sb.toString();
    }

    public ChangeTracker<PcmForecastValue> newChangeTracker(PcmForecastValue observed) {
        ChangeTracker<PcmForecastValue> tracker = new ChangeTracker<PcmForecastValue>() {

            @Override
            public void firePropertyChangeEvent(String propName, Object oldVal, Object newVal) {
                super.firePropertyChangeEvent(propName, oldVal, newVal);
                propagateChangeToForecast(this);
            }

            @Override
            @SuppressWarnings("rawtypes")
            public void firePropertyChangeEventUsingCompare(String propName, Comparable oldVal, Comparable newVal) {
                super.firePropertyChangeEventUsingCompare(propName, oldVal, newVal);
                propagateChangeToForecast(this);
            }
        };
        tracker.setObservedObject(observed);
        return tracker;
    }

    private  void propagateChangeToForecast(ChangeTracker<PcmForecastValue> tracker) {
        if (!tracker.hasChanged()) {
            return;
        }
        PcmForecastValue value = tracker.getObservedObject();
        if (value == null) {
            return;
        }
        PcmForecast forecast = value.getForecast();
        if (forecast != null) {
            forecast.getForecastChangeTracker().markChanged();
        }
    }
}
