/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PcmCostRecordRangeTest {

    private static PcmCostElement element(String key) {
        PcmCostElementId id = new PcmCostElementId(key, "CT");
        PcmCostElement el = new PcmCostElement();
        el.setId(id);
        el.setCostElementValueType("S");
        return el;
    }

    @Test
    void testFromRangeConstructor() {
        PcmCostRecordRange r = new PcmCostRecordRange(new BigDecimal("1.5"));
        assertEquals(new BigDecimal("1.5"), r.getFromRange());
    }

    @Test
    void testAddCostRecordValue_CreatesNewWhenAbsent() {
        PcmCostRecordRange r = new PcmCostRecordRange();
        PcmCostElement el = element("E1");
        PcmCostRecordValue val = r.addCostRecordValue(el, new BigDecimal("12"), "EA");
        assertNotNull(val);
        assertSame(r, val.getCostRecordRange());
        assertSame(el, val.getCostElement());
        assertEquals(new BigDecimal("12"), val.getCostValue());
        assertEquals("EA", val.getCostUom());
        assertEquals("S", val.getCostValueType());
        assertEquals(1, r.getCostRecordValues().size());
    }

    @Test
    void testAddCostRecordValue_UpdatesExisting() {
        PcmCostRecordRange r = new PcmCostRecordRange();
        PcmCostElement el = element("E1");
        PcmCostRecordValue first = r.addCostRecordValue(el, new BigDecimal("12"), "EA");
        PcmCostRecordValue second = r.addCostRecordValue(el, new BigDecimal("99"), "KG");
        assertSame(first, second, "should mutate the existing record value, not create another");
        assertEquals(new BigDecimal("99"), second.getCostValue());
        assertEquals("KG", second.getCostUom());
        assertEquals(1, r.getCostRecordValues().size());
    }

    @Test
    void testRemoveCostRecordValue() {
        PcmCostRecordRange r = new PcmCostRecordRange();
        PcmCostElement el = element("E1");
        r.addCostRecordValue(el, new BigDecimal("12"), "EA");
        r.removeCostRecordValue(el);
        assertTrue(r.getCostRecordValues().isEmpty());
    }

    @Test
    void testAddCostRecordValueObject_StoresAndBacklinks() {
        PcmCostRecordRange r = new PcmCostRecordRange();
        PcmCostElement el = element("E2");
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.setCostElement(el);
        PcmCostRecordValue ret = r.addCostRecordValue(v);
        assertSame(v, ret);
        assertSame(r, v.getCostRecordRange());
        assertSame(v, r.getCostRecordValues().get("E2"));
    }

    @Test
    void testGetCostRecordValueByElementAndKey() {
        PcmCostRecordRange r = new PcmCostRecordRange();
        PcmCostElement el = element("E3");
        PcmCostRecordValue v = r.addCostRecordValue(el, BigDecimal.ONE, "EA");
        assertSame(v, r.getCostRecordValue(el));
        assertSame(v, r.getCostRecordValue("E3"));
        assertNull(r.getCostRecordValue("MISSING"));
    }

    @Test
    void testCopyClonesValuesAndIsIndependent() {
        PcmCostRecordRange r = new PcmCostRecordRange();
        r.setActive(Boolean.TRUE);
        r.setFromRange(new BigDecimal("0"));
        r.setToRange(new BigDecimal("10"));
        r.setCostRecord(mock(PcmCostRecord.class));
        PcmCostElement el = element("E1");
        r.addCostRecordValue(el, new BigDecimal("5"), "EA");

        PcmCostRecordRange copy = r.copy();
        assertNotSame(r, copy);
        assertEquals(r.getActive(), copy.getActive());
        assertEquals(r.getFromRange(), copy.getFromRange());
        assertEquals(r.getToRange(), copy.getToRange());
        assertSame(r.getCostRecord(), copy.getCostRecord());
        assertEquals(1, copy.getCostRecordValues().size());
        PcmCostRecordValue copiedVal = copy.getCostRecordValues().get("E1");
        assertNotNull(copiedVal);
        assertSame(copy, copiedVal.getCostRecordRange());
    }

    @Test
    void testCompareToByFromRange() {
        PcmCostRecordRange a = new PcmCostRecordRange(new BigDecimal("0"));
        PcmCostRecordRange b = new PcmCostRecordRange(new BigDecimal("10"));
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(new PcmCostRecordRange(new BigDecimal("0"))));
    }
}
