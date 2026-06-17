/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PcmCostRecordRangeExceptionTest {

    private static PcmCostElement element(String key) {
        PcmCostElement el = new PcmCostElement();
        el.setId(new PcmCostElementId(key, "CT"));
        el.setCostElementValueType("S");
        return el;
    }

    @Test
    void testNoArgsConstructorEmptyMap() {
        PcmCostRecordRangeException r = new PcmCostRecordRangeException();
        assertNotNull(r.getCostRecordValues());
        assertTrue(r.getCostRecordValues().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        PcmCostRecordException cre = mock(PcmCostRecordException.class);
        PcmCostRecordRangeException r = new PcmCostRecordRangeException(
                3L, cre, new BigDecimal("1"), new BigDecimal("2"), Boolean.FALSE,
                new java.util.LinkedHashMap<>());
        assertEquals(3L, r.getCostRecordRangeKey());
        assertSame(cre, r.getCostRecord());
        assertEquals(new BigDecimal("1"), r.getFromRange());
        assertEquals(new BigDecimal("2"), r.getToRange());
        assertEquals(Boolean.FALSE, r.getActive());
    }

    @Test
    void testAddNewValue() {
        PcmCostRecordRangeException r = new PcmCostRecordRangeException();
        PcmCostElement el = element("E1");
        PcmCostRecordValueException v = r.addCostRecordValue(el, new BigDecimal("5"), "EA");
        assertNotNull(v);
        assertSame(r, v.getCostRecordRange());
        assertEquals(new BigDecimal("5"), v.getCostValue());
        assertEquals("EA", v.getCostUom());
        assertEquals("S", v.getCostValueType());
    }

    @Test
    void testAddUpdatesExisting() {
        PcmCostRecordRangeException r = new PcmCostRecordRangeException();
        PcmCostElement el = element("E1");
        PcmCostRecordValueException first = r.addCostRecordValue(el, new BigDecimal("5"), "EA");
        PcmCostRecordValueException second = r.addCostRecordValue(el, new BigDecimal("9"), "KG");
        assertSame(first, second);
        assertEquals(new BigDecimal("9"), second.getCostValue());
        assertEquals("KG", second.getCostUom());
    }

    @Test
    void testRemoveCostRecordValue() {
        PcmCostRecordRangeException r = new PcmCostRecordRangeException();
        PcmCostElement el = element("E1");
        r.addCostRecordValue(el, new BigDecimal("1"), "EA");
        r.removeCostRecordValue(el);
        assertTrue(r.getCostRecordValues().isEmpty());
    }

    @Test
    void testAddObjectAndLookup() {
        PcmCostRecordRangeException r = new PcmCostRecordRangeException();
        PcmCostElement el = element("E2");
        PcmCostRecordValueException v = new PcmCostRecordValueException();
        v.setCostElement(el);
        PcmCostRecordValueException ret = r.addCostRecordValue(v);
        assertSame(v, ret);
        assertSame(r, v.getCostRecordRange());
        assertSame(v, r.getCostRecordValue(el));
        assertSame(v, r.getCostRecordValue("E2"));
        assertNull(r.getCostRecordValue("OTHER"));
    }

    @Test
    void testCopy() {
        PcmCostRecordRangeException r = new PcmCostRecordRangeException();
        r.setActive(Boolean.TRUE);
        r.setFromRange(new BigDecimal("0"));
        r.setToRange(new BigDecimal("100"));
        r.setCostRecord(mock(PcmCostRecordException.class));
        PcmCostElement el = element("E1");
        r.addCostRecordValue(el, new BigDecimal("9"), "EA");

        PcmCostRecordRangeException copy = r.copy();
        assertNotSame(r, copy);
        assertEquals(r.getFromRange(), copy.getFromRange());
        assertEquals(r.getToRange(), copy.getToRange());
        assertEquals(1, copy.getCostRecordValues().size());
    }

    @Test
    void testCompareToByFromRange() {
        PcmCostRecordRangeException a = new PcmCostRecordRangeException();
        a.setFromRange(new BigDecimal("0"));
        PcmCostRecordRangeException b = new PcmCostRecordRangeException();
        b.setFromRange(new BigDecimal("10"));
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }
}
