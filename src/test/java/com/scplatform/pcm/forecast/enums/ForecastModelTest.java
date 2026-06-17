/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForecastModelTest {

    @Test
    void testEnumValues() {
        ForecastModel[] values = ForecastModel.values();
        assertEquals(2, values.length);
        assertEquals(ForecastModel.CURRENT, values[0]);
        assertEquals(ForecastModel.ADJUSTABLE, values[1]);
    }

    @Test
    void testGetAlias_Current() {
        assertEquals("CURRENT", ForecastModel.CURRENT.getAlias());
    }

    @Test
    void testGetAlias_Adjustable() {
        assertEquals("ADJUSTABLE", ForecastModel.ADJUSTABLE.getAlias());
    }

    @Test
    void testValueOfFromAlias_Current() {
        assertEquals(ForecastModel.CURRENT, ForecastModel.valueOfFromAlias("CURRENT"));
    }

    @Test
    void testValueOfFromAlias_Adjustable() {
        assertEquals(ForecastModel.ADJUSTABLE, ForecastModel.valueOfFromAlias("ADJUSTABLE"));
    }

    @Test
    void testValueOfFromAlias_CaseInsensitive() {
        assertEquals(ForecastModel.CURRENT, ForecastModel.valueOfFromAlias("current"));
        assertEquals(ForecastModel.ADJUSTABLE, ForecastModel.valueOfFromAlias("adjustable"));
    }

    @Test
    void testValueOfFromAlias_NoMatch() {
        assertNull(ForecastModel.valueOfFromAlias("UNKNOWN"));
    }

    @Test
    void testValueOfFromAlias_NullThrows() {
        assertThrows(NullPointerException.class, () -> ForecastModel.valueOfFromAlias(null));
    }

    @Test
    void testValueOf() {
        assertEquals(ForecastModel.CURRENT, ForecastModel.valueOf("CURRENT"));
        assertEquals(ForecastModel.ADJUSTABLE, ForecastModel.valueOf("ADJUSTABLE"));
    }
}
