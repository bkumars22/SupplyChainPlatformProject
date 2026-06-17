/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ForecastPeriodTest {

    @Test
    void testConstructorAndGetters() {
        Date start = new Date(1000L);
        Date end   = new Date(2000L);
        ForecastPeriod period = new ForecastPeriod(
                "Q1 2026", start, end, ForecastTimeline.ForecastPeriodState.CURRENT);

        assertEquals("Q1 2026", period.getLabel());
        assertEquals(start, period.getStartDate());
        assertEquals(end, period.getEndDate());
        assertEquals(ForecastTimeline.ForecastPeriodState.CURRENT, period.getState());
    }

    @Test
    void testToString_ContainsLabel() {
        ForecastPeriod period = new ForecastPeriod(
                "Q2", new Date(0L), new Date(999L), ForecastTimeline.ForecastPeriodState.PAST);
        String s = period.toString();
        assertTrue(s.contains("Q2"), "toString should contain label");
        assertTrue(s.contains("PAST"), "toString should contain state");
    }

    @Test
    void testConstructor_NullValues() {
        ForecastPeriod period = new ForecastPeriod(null, null, null,
                ForecastTimeline.ForecastPeriodState.FUTURE);
        assertNull(period.getLabel());
        assertNull(period.getStartDate());
        assertNull(period.getEndDate());
        assertEquals(ForecastTimeline.ForecastPeriodState.FUTURE, period.getState());
    }

    @Test
    void testAllStates() {
        for (ForecastTimeline.ForecastPeriodState state : ForecastTimeline.ForecastPeriodState.values()) {
            ForecastPeriod p = new ForecastPeriod("lbl", new Date(), new Date(), state);
            assertEquals(state, p.getState());
        }
    }
}
