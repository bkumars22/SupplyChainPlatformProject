/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForecastTimelineTest {

    private ForecastTimeline timeline;

    @BeforeEach
    void setUp() {
        timeline = new ForecastTimeline("FISCAL", "MONTHLY");
    }

    @Test
    void testGetCalendarNameAndPeriodType() {
        assertEquals("FISCAL", timeline.getCalendarName());
        assertEquals("MONTHLY", timeline.getPeriodType());
    }

    @Test
    void testEmptyTimelineHasNoPeriods() {
        assertTrue(timeline.getPeriods().isEmpty());
        assertEquals(-1, timeline.getCurrentPeriodIndex());
        assertNull(timeline.getCurrentPeriod());
        assertEquals(0, timeline.getFuturePeriodCount());
        assertEquals(0, timeline.getPastPeriodCount());
    }

    @Test
    void testAddPeriodAndGetPeriods() {
        Date start = new Date(1000L);
        Date end   = new Date(2000L);
        timeline.addPeriod("Q1", start, end, ForecastTimeline.ForecastPeriodState.CURRENT);

        List<ForecastPeriod> periods = timeline.getPeriods();
        assertEquals(1, periods.size());
        assertEquals("Q1", periods.get(0).getLabel());
    }

    @Test
    void testClearPeriods() {
        timeline.addPeriod("Q1", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST);
        timeline.clearPeriods();
        assertTrue(timeline.getPeriods().isEmpty());
    }

    @Test
    void testGetCurrentPeriodIndex() {
        timeline.addPeriod("P1", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST);
        timeline.addPeriod("P2", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.CURRENT);
        timeline.addPeriod("P3", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.FUTURE);
        assertEquals(1, timeline.getCurrentPeriodIndex());
    }

    @Test
    void testGetCurrentPeriodIndex_NoCurrent() {
        timeline.addPeriod("P1", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST);
        assertEquals(-1, timeline.getCurrentPeriodIndex());
    }

    @Test
    void testGetCurrentPeriod() {
        Date start = new Date(5000L);
        Date end   = new Date(6000L);
        timeline.addPeriod("PAST",   new Date(0L), new Date(1000L), ForecastTimeline.ForecastPeriodState.PAST);
        timeline.addPeriod("CURR",   start,        end,             ForecastTimeline.ForecastPeriodState.CURRENT);
        timeline.addPeriod("FUTURE", new Date(),   new Date(),      ForecastTimeline.ForecastPeriodState.FUTURE);

        ForecastPeriod curr = timeline.getCurrentPeriod();
        assertNotNull(curr);
        assertEquals("CURR", curr.getLabel());
    }

    @Test
    void testGetCurrentPeriod_NoCurrent() {
        timeline.addPeriod("FUTURE", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.FUTURE);
        assertNull(timeline.getCurrentPeriod());
    }

    @Test
    void testGetFuturePeriodCount() {
        timeline.addPeriod("P1", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST);
        timeline.addPeriod("P2", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.CURRENT);
        timeline.addPeriod("P3", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.FUTURE);
        timeline.addPeriod("P4", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.FUTURE);
        assertEquals(2, timeline.getFuturePeriodCount());
    }

    @Test
    void testGetPastPeriodCount() {
        timeline.addPeriod("P1", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST);
        timeline.addPeriod("P2", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST);
        timeline.addPeriod("P3", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.CURRENT);
        assertEquals(2, timeline.getPastPeriodCount());
    }

    @Test
    void testGetPeriod_ByStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();
        Date end   = DateUtils.addMonths(start, 1);
        timeline.addPeriod("JAN 2026", start, end, ForecastTimeline.ForecastPeriodState.FUTURE);

        ForecastPeriod found = timeline.getPeriod(start);
        assertNotNull(found);
        assertEquals("JAN 2026", found.getLabel());
    }

    @Test
    void testGetPeriod_NoMatch() {
        timeline.addPeriod("P1", new Date(10000L), new Date(20000L),
                ForecastTimeline.ForecastPeriodState.FUTURE);
        assertNull(timeline.getPeriod(new Date(99999999L)));
    }

    @Test
    void testForecastPeriodStateValues() {
        ForecastTimeline.ForecastPeriodState[] states = ForecastTimeline.ForecastPeriodState.values();
        assertEquals(3, states.length);
    }
}
