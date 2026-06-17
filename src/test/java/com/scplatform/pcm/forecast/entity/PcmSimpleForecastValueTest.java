/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PcmSimpleForecastValueTest {

    @Test
    void noArgsConstructor_initialisesEmpty() {
        PcmSimpleForecastValue v = new PcmSimpleForecastValue();
        assertNull(v.getForecastValue());
        assertNull(v.getForecastMeasureKey());
        assertTrue(v.isValueUnset());
    }

    @Test
    void fullConstructor_setsAllFields() {
        Date from = new Date(0L);
        Date to = new Date(1_000L);
        PcmSimpleForecastValue v = new PcmSimpleForecastValue(
                from, to, "QTY", new BigDecimal("42.0"), "EA");
        assertEquals(0, new BigDecimal("42.0").compareTo(v.getForecastValue()));
        assertEquals("QTY", v.getForecastMeasureKey());
        assertSame(from, v.getEffectiveFromDt());
        assertSame(to, v.getEffectiveToDt());
        assertEquals("EA", v.getForecastValueUOM());
        assertFalse(v.isValueUnset());
    }

    @Test
    void copyConstructor_copiesValueAndBaseFields() {
        PcmSimpleForecastValue src = new PcmSimpleForecastValue(
                new Date(1L), new Date(2L), "Q", new BigDecimal("5"), "EA");
        PcmSimpleForecastValue copy = new PcmSimpleForecastValue(src);
        assertEquals(0, new BigDecimal("5").compareTo(copy.getForecastValue()));
        assertEquals("Q", copy.getForecastMeasureKey());
        assertEquals("EA", copy.getForecastValueUOM());
    }

    @Test
    void setForecastValue_updates() {
        PcmSimpleForecastValue v = new PcmSimpleForecastValue();
        v.setForecastValue(new BigDecimal("7"));
        assertEquals(0, new BigDecimal("7").compareTo(v.getForecastValue()));
    }

    @Test
    void getPitValueAndCalculatedValue_returnForecastValue() {
        PcmSimpleForecastValue v = new PcmSimpleForecastValue();
        v.setForecastValue(new BigDecimal("3.14"));
        assertEquals(0, new BigDecimal("3.14").compareTo(v.getPitValue()));
        assertEquals(0, new BigDecimal("3.14").compareTo(v.getCalculatedForecastValue()));
    }

    @Test
    void isValueUnset_trueWhenNull() {
        PcmSimpleForecastValue v = new PcmSimpleForecastValue();
        assertTrue(v.isValueUnset());
        v.setForecastValue(BigDecimal.ZERO);
        assertFalse(v.isValueUnset());
    }

    @Test
    void getCurrentStateAsJSON_includesForecastValueField() {
        PcmSimpleForecastValue v = new PcmSimpleForecastValue(
                new Date(0L), new Date(1L), "Q", new BigDecimal("12.5"), "EA");
        ObjectNode jn = v.getCurrentStateAsJSON();
        assertNotNull(jn.get("fv"));
        assertEquals("Q", jn.get("mk").asText());
    }
}
