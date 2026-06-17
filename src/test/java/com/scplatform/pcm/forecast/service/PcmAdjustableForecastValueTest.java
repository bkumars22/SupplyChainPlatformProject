/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PcmAdjustableForecastValueTest {

    @Test
    void noArgsConstructor_initialisesNulls() {
        PcmAdjustableForecastValue v = new PcmAdjustableForecastValue();
        assertNull(v.getAdjustableValue());
        assertNull(v.getAdjustmentAmount());
        assertNull(v.getAdjustmentType());
        assertTrue(v.isValueUnset());
    }

    @Test
    void settersAndGetters() {
        PcmAdjustableForecastValue v = new PcmAdjustableForecastValue();
        v.setAdjustableValue(new BigDecimal("10"));
        v.setAdjustmentAmount(new BigDecimal("2"));
        v.setAdjustmentType(com.scplatform.pcm.forecast.enums.AdjustmentType.FIXED);
        assertEquals(0, new BigDecimal("10").compareTo(v.getAdjustableValue()));
        assertEquals(0, new BigDecimal("2").compareTo(v.getAdjustmentAmount()));
        assertEquals(com.scplatform.pcm.forecast.enums.AdjustmentType.FIXED, v.getAdjustmentType());
        assertFalse(v.isValueUnset());
    }

    @Test
    void getPitValue_returnsAdjustableValue() {
        PcmAdjustableForecastValue v = new PcmAdjustableForecastValue();
        v.setAdjustableValue(new BigDecimal("42"));
        assertEquals(0, new BigDecimal("42").compareTo(v.getPitValue()));
    }

    @Test
    void getCalculatedForecastValue_returnsBaseWhenNoTypeOrAmount() {
        PcmAdjustableForecastValue v = new PcmAdjustableForecastValue();
        v.setAdjustableValue(new BigDecimal("10"));
        assertEquals(0, new BigDecimal("10").compareTo(v.getCalculatedForecastValue()));
    }

    @Test
    void getCalculatedForecastValue_appliesFixedAdjustment() {
        PcmAdjustableForecastValue v = new PcmAdjustableForecastValue();
        v.setAdjustableValue(new BigDecimal("10"));
        v.setAdjustmentAmount(new BigDecimal("3"));
        v.setAdjustmentType(com.scplatform.pcm.forecast.enums.AdjustmentType.FIXED);
        assertEquals(0, new BigDecimal("13").compareTo(v.getCalculatedForecastValue()));
    }

    @Test
    void getCalculatedForecastValue_appliesPercentAdjustment() {
        PcmAdjustableForecastValue v = new PcmAdjustableForecastValue();
        v.setAdjustableValue(new BigDecimal("100"));
        v.setAdjustmentAmount(new BigDecimal("10"));
        v.setAdjustmentType(com.scplatform.pcm.forecast.enums.AdjustmentType.PERCENT);
        // 100 + 10% = 110
        assertEquals(0, new BigDecimal("110")
                .compareTo(v.getCalculatedForecastValue().setScale(0, java.math.RoundingMode.HALF_UP)));
    }

    @Test
    void getCurrentStateAsJSON_includesAdjustableFields() {
        PcmAdjustableForecastValue v = new PcmAdjustableForecastValue();
        v.setForecastMeasureKey("Q");
        v.setEffectiveFromDt(new java.util.Date(0L));
        v.setAdjustableValue(new BigDecimal("5"));
        v.setAdjustmentAmount(new BigDecimal("1"));
        v.setAdjustmentType(com.scplatform.pcm.forecast.enums.AdjustmentType.FIXED);
        ObjectNode jn = v.getCurrentStateAsJSON();
        assertNotNull(jn.get("av"));
        assertNotNull(jn.get("amt"));
        assertEquals("FIXED", jn.get("at").asText());
    }

    @Test
    void getCurrentStateAsJSON_nullAdjustmentTypePrintsNull() {
        PcmAdjustableForecastValue v = new PcmAdjustableForecastValue();
        v.setForecastMeasureKey("Q");
        v.setEffectiveFromDt(new java.util.Date(0L));
        ObjectNode jn = v.getCurrentStateAsJSON();
        assertTrue(jn.get("at").isNull());
    }
}
