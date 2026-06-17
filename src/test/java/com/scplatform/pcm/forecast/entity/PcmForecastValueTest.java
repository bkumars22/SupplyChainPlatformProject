/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PcmForecastValueTest {

    /** Minimal concrete subclass to exercise the abstract base. */
    private static class TestValue extends PcmForecastValue {
        BigDecimal pit;
        TestValue() { super(); }
        TestValue(Date from, Date to, String mk, String uom) { super(from, to, mk, uom); }
        TestValue(PcmForecastValue copy) { super(copy); }
        @Override public BigDecimal getPitValue() { return pit; }
        @Override public BigDecimal getCalculatedForecastValue() { return pit; }
        @Override public boolean isValueUnset() { return pit == null; }
    }

    @Test
    void noArgsConstructor_initialises() {
        TestValue v = new TestValue();
        assertNull(v.getForecastMeasureKey());
        assertNull(v.getEffectiveFromDt());
        assertNull(v.getForecast());
    }

    @Test
    void fullConstructor_setsFields() {
        Date from = new Date(1L);
        Date to = new Date(2L);
        TestValue v = new TestValue(from, to, "Q", "EA");
        assertEquals("Q", v.getForecastMeasureKey());
        assertSame(from, v.getEffectiveFromDt());
        assertSame(to, v.getEffectiveToDt());
        assertEquals("EA", v.getForecastValueUOM());
    }

    @Test
    void copyConstructor_copiesAllFields() {
        Date from = new Date(10L);
        Date to = new Date(20L);
        TestValue src = new TestValue(from, to, "Q", "EA");
        TestValue copy = new TestValue(src);
        assertEquals("Q", copy.getForecastMeasureKey());
        assertSame(from, copy.getEffectiveFromDt());
        assertSame(to, copy.getEffectiveToDt());
        assertEquals("EA", copy.getForecastValueUOM());
    }

    @Test
    void setters_updateFields() {
        TestValue v = new TestValue();
        v.setForecastMeasureKey("MK");
        assertEquals("MK", v.getForecastMeasureKey());
        Date d = new Date();
        v.setEffectiveFromDt(d);
        assertSame(d, v.getEffectiveFromDt());
        v.setEffectiveToDt(d);
        assertSame(d, v.getEffectiveToDt());
        v.setForecastValueKey(99L);
        assertEquals(Long.valueOf(99L), v.getForecastValueKey());
        PcmForecast f = new PcmForecast();
        v.setForecast(f);
        assertSame(f, v.getForecast());
        v.setSavedStates("{}");
        assertEquals("{}", v.getSavedStates());
    }

    @Test
    void getPath_combinesEffectiveFromAndMeasure() {
        TestValue v = new TestValue(new Date(123L), null, "Q", null);
        assertEquals("123.Q", v.getPath());
    }

    @Test
    void getPath_handlesNullFromDate() {
        TestValue v = new TestValue();
        v.setForecastMeasureKey("Q");
        assertEquals(".Q", v.getPath());
    }

    @Test
    void equalsHashCode_byForecastAndMeasureCaseInsensitive() {
        PcmForecast forecast = new PcmForecast();
        TestValue a = new TestValue(new Date(1L), null, "QTY", null);
        a.setForecast(forecast);
        TestValue b = new TestValue(new Date(1L), null, "qty", null);
        b.setForecast(forecast);

        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void compareTo_isCaseInsensitive() {
        TestValue a = new TestValue(new Date(1L), new Date(2L), "QTY", null);
        TestValue b = new TestValue(new Date(1L), new Date(2L), "qty", null);
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void compareTo_ordersByMeasureThenFromDate() {
        TestValue a = new TestValue(new Date(1L), new Date(2L), "A", null);
        TestValue b = new TestValue(new Date(1L), new Date(2L), "B", null);
        assertTrue(a.compareTo(b) < 0);

        TestValue c = new TestValue(new Date(2L), new Date(3L), "A", null);
        assertTrue(a.compareTo(c) < 0);
    }

    @Test
    void toString_containsMeasureAndDate() {
        TestValue v = new TestValue(new Date(123L), null, "MK", null);
        String s = v.toString();
        assertTrue(s.contains("MK"));
    }

    @Test
    void savedState_roundTrip() throws Exception {
        TestValue v = new TestValue(new Date(1L), new Date(2L), "Q", "EA");
        v.saveState("k1");
        JsonNode node = v.getSavedStateAsJSON("k1");
        assertNotNull(node);
        String s = v.getSavedStateAsString("k1");
        assertNotNull(s);
        assertTrue(s.contains("Q"));

        // null when missing
        assertNull(v.getSavedStateAsJSON("missing"));
    }

    @Test
    void getSavedStateAsString_nullWhenNoStates() throws Exception {
        TestValue v = new TestValue();
        assertNull(v.getSavedStateAsString("anything"));
    }

    @Test
    void addSavedState_persistsCustomJson() throws Exception {
        TestValue v = new TestValue(new Date(1L), null, "Q", null);
        ObjectNode custom = new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode();
        custom.put("foo", "bar");
        v.addSavedState("custom", custom);
        assertEquals("bar", v.getSavedStateAsJSON("custom").get("foo").asText());
    }

    @Test
    void getCurrentStateAsJSON_populatesBaseFields() {
        TestValue v = new TestValue(new Date(0L), new Date(1L), "MK", "EA");
        ObjectNode jn = v.getCurrentStateAsJSON();
        assertEquals("MK", jn.get("mk").asText());
        assertEquals("EA", jn.get("uom").asText());
    }

    @Test
    void stateManipulationException_constructorsWork() {
        TestValue v = new TestValue();
        PcmForecastValue.StateManipulationException e1 = v.new StateManipulationException();
        PcmForecastValue.StateManipulationException e2 = v.new StateManipulationException("m");
        PcmForecastValue.StateManipulationException e3 = v.new StateManipulationException("m", new RuntimeException());
        PcmForecastValue.StateManipulationException e4 = v.new StateManipulationException(new RuntimeException("x"));
        assertNotNull(e1);
        assertEquals("m", e2.getMessage());
        assertEquals("m", e3.getMessage());
        assertNotNull(e4.getCause());
    }
}
