/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.util;

import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.fiscalPeriod.service.FiscalPeriodService;
import com.scplatform.pcm.forecast.dto.ForecastPeriod;
import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ForecastTimelineUtilTest {

    private FiscalPeriod period(String name, Date startDate, Date endDate) {
        return new FiscalPeriod("M", 1, name, startDate, endDate);
    }

    @Test
    void testUtilityClassIsFinal() {
        assertTrue(Modifier.isFinal(ForecastTimelineUtil.class.getModifiers()));
    }

    @Test
    void testUtilityClassHasPrivateConstructor() throws Exception {
        Constructor<ForecastTimelineUtil> ctor = ForecastTimelineUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);
        // Constructor is reachable via reflection — sanity check it runs without exception
        assertNotNull(ctor.newInstance());
    }

    @Test
    void testGenerateTimeline_FromService_DelegatesAndPopulatesPeriods() {
        FiscalPeriodService svc = mock(FiscalPeriodService.class);
        Calendar startAt = Calendar.getInstance();
        startAt.set(2026, Calendar.JUNE, 15, 0, 0, 0);
        startAt.set(Calendar.MILLISECOND, 0);

        Date past   = DateUtils.addMonths(startAt.getTime(), -2);
        Date pastEnd= DateUtils.addDays(past, 30);
        Date curr   = DateUtils.addDays(startAt.getTime(), -2);
        Date currEnd= DateUtils.addMonths(curr, 1);
        Date future = DateUtils.addMonths(startAt.getTime(), 1);
        Date futEnd = DateUtils.addMonths(future, 1);

        List<FiscalPeriod> fps = Arrays.asList(
                period("PAST", past, pastEnd),
                period("CURR", curr, currEnd),
                period("FUTURE", future, futEnd));
        when(svc.getFiscalPeriods(eq(startAt), eq(FiscalPeriod.PeriodType.MONTH), eq(1), eq(1)))
                .thenReturn(fps);

        ForecastTimeline tl = ForecastTimelineUtil.generateTimeline(svc, "FISCAL", "MONTH", startAt, 1, 1);
        assertNotNull(tl);
        assertEquals("FISCAL", tl.getCalendarName());
        assertEquals("MONTH", tl.getPeriodType());
        assertEquals(3, tl.getPeriods().size());
        assertEquals(ForecastTimeline.ForecastPeriodState.PAST,   tl.getPeriods().get(0).getState());
        assertEquals(ForecastTimeline.ForecastPeriodState.CURRENT, tl.getPeriods().get(1).getState());
        assertEquals(ForecastTimeline.ForecastPeriodState.FUTURE, tl.getPeriods().get(2).getState());
    }

    @Test
    void testGenerateTimeline_FromList_PopulatesAllPeriods() {
        Calendar startAt = Calendar.getInstance();
        startAt.set(2026, Calendar.JUNE, 15, 0, 0, 0);
        startAt.set(Calendar.MILLISECOND, 0);

        Date past   = DateUtils.addMonths(startAt.getTime(), -2);
        Date pastEnd= DateUtils.addDays(past, 30);
        List<FiscalPeriod> fps = Collections.singletonList(period("ONLY", past, pastEnd));

        ForecastTimeline tl = ForecastTimelineUtil.generateTimeline("FISCAL", "MONTH", startAt, 0, 0, fps);
        assertEquals(1, tl.getPeriods().size());
        ForecastPeriod fp = tl.getPeriods().get(0);
        assertEquals("ONLY", fp.getLabel());
        assertEquals(ForecastTimeline.ForecastPeriodState.PAST, fp.getState());
    }

    @Test
    void testGenerateTimeline_LowercasePeriodType() {
        FiscalPeriodService svc = mock(FiscalPeriodService.class);
        Calendar startAt = Calendar.getInstance();
        when(svc.getFiscalPeriods(any(), eq(FiscalPeriod.PeriodType.WEEK), any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.emptyList());

        ForecastTimeline tl = ForecastTimelineUtil.generateTimeline(svc, "FISCAL", "week", startAt, 0, 0);
        assertNotNull(tl);
        assertEquals(0, tl.getPeriods().size());
        verify(svc).getFiscalPeriods(any(), eq(FiscalPeriod.PeriodType.WEEK), eq(0), eq(0));
    }

    @Test
    void testGenerateTimeline_EmptyPeriodList() {
        Calendar startAt = Calendar.getInstance();
        ForecastTimeline tl = ForecastTimelineUtil.generateTimeline("X", "MONTH", startAt, 0, 0,
                Collections.emptyList());
        assertNotNull(tl);
        assertTrue(tl.getPeriods().isEmpty());
    }

    @Test
    void testGenerateTimeline_InvalidPeriodTypeThrows() {
        FiscalPeriodService svc = mock(FiscalPeriodService.class);
        assertThrows(IllegalArgumentException.class,
                () -> ForecastTimelineUtil.generateTimeline(svc, "X", "INVALID",
                        Calendar.getInstance(), 1, 1));
    }
}
