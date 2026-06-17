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

class PcmCostRecordValueTest {

    @Test
    void testNoArgsConstructorDefaults() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        assertNull(v.getCostRecordValueKey());
        assertNull(v.getCostValue());
        assertNotNull(v.getCostValueDetails());
        assertTrue(v.getCostValueDetails().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.setCostRecordValueKey(11L);
        v.setCostValue(new BigDecimal("5"));
        v.setCostUom("EA");
        v.setCostValueType("S");
        PcmCostRecordRange r = mock(PcmCostRecordRange.class);
        PcmCostElement el = mock(PcmCostElement.class);
        v.setCostRecordRange(r);
        v.setCostElement(el);

        assertEquals(11L, v.getCostRecordValueKey());
        assertEquals(new BigDecimal("5"), v.getCostValue());
        assertEquals("EA", v.getCostUom());
        assertEquals("S", v.getCostValueType());
        assertSame(r, v.getCostRecordRange());
        assertSame(el, v.getCostElement());
    }

    @Test
    void testAddCostValue_InitializesWhenNull() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.addCostValue(new BigDecimal("3"));
        assertEquals(new BigDecimal("3"), v.getCostValue());
    }

    @Test
    void testAddCostValue_AccumulatesWhenPresent() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.setCostValue(new BigDecimal("2"));
        v.addCostValue(new BigDecimal("3"));
        assertEquals(new BigDecimal("5"), v.getCostValue());
    }

    @Test
    void testSetCostValueDetailByName_CreatesNew() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        PcmCostValueDetail d = v.setCostValueDetail("name", new BigDecimal("10"), new BigDecimal("0.5"));
        assertNotNull(d);
        assertEquals("name", d.getCostValueName());
        assertEquals(new BigDecimal("10"), d.getCostValueValue());
        assertEquals(new BigDecimal("0.5"), d.getCostValueBlend());
        assertSame(v, d.getCostRecordValue());
        assertSame(d, v.getCostValueDetails().get("name"));
    }

    @Test
    void testSetCostValueDetailByName_UpdatesExisting() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        PcmCostValueDetail first = v.setCostValueDetail("n", BigDecimal.ONE, new BigDecimal("0.1"));
        PcmCostValueDetail second = v.setCostValueDetail("n", new BigDecimal("99"), new BigDecimal("0.9"));
        assertSame(first, second);
        assertEquals(new BigDecimal("99"), second.getCostValueValue());
        assertEquals(new BigDecimal("0.9"), second.getCostValueBlend());
        assertEquals(1, v.getCostValueDetails().size());
    }

    @Test
    void testSetCostValueDetailObject_AddsBacklinkWhenNew() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        PcmCostValueDetail d = new PcmCostValueDetail();
        d.setCostValueName("dn");
        PcmCostValueDetail prev = v.setCostValueDetail(d);
        assertNull(prev);
        assertSame(v, d.getCostRecordValue());
        assertSame(d, v.getCostValueDetails().get("dn"));
    }

    @Test
    void testSetCostValueDetailObject_ReplacesExistingReturnsOld() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        PcmCostValueDetail existing = v.setCostValueDetail("k", BigDecimal.ONE, BigDecimal.ZERO);
        PcmCostValueDetail replacement = new PcmCostValueDetail();
        replacement.setCostValueName("k");
        PcmCostValueDetail returned = v.setCostValueDetail(replacement);
        // Per implementation: returns the existing detail; existing detail is unlinked from this value
        assertSame(existing, returned);
        assertNull(existing.getCostRecordValue());
        // Note: replacement is NOT re-inserted (this matches current behavior)
        assertFalse(v.getCostValueDetails().containsKey("k"));
    }

    @Test
    void testGetValueTotal_SumsDetailsWithBacklink() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.setCostValueDetail("a", new BigDecimal("1.5"), null);
        v.setCostValueDetail("b", new BigDecimal("2.25"), null);
        BigDecimal total = v.getValueTotal();
        // 1.5 + 2.25 = 3.75, scaled to 6 places
        assertEquals(0, new BigDecimal("3.75").compareTo(total));
    }

    @Test
    void testGetValueBlendTotal_SumsNonNullBlends() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.setCostValueDetail("a", BigDecimal.ONE, new BigDecimal("30"));
        v.setCostValueDetail("b", BigDecimal.ONE, new BigDecimal("70"));
        v.setCostValueDetail("c", BigDecimal.ONE, null);
        BigDecimal blend = v.getValueBlendTotal();
        assertEquals(0, new BigDecimal("100").compareTo(blend));
    }

    @Test
    void testGetCalculatedValue_BlendedType() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.setCostValueType("B");
        v.setCostValueDetail("a", new BigDecimal("10"), new BigDecimal("50"));
        v.setCostValueDetail("b", new BigDecimal("20"), new BigDecimal("50"));
        BigDecimal calc = v.getCalculatedValue();
        // 10*0.5 + 20*0.5 = 15.0
        assertNotNull(calc);
        assertEquals(0, new BigDecimal("15").compareTo(calc));
    }

    @Test
    void testGetCalculatedValue_NonBlendedReturnsNull() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.setCostValueType("S");
        v.setCostValueDetail("a", new BigDecimal("10"), new BigDecimal("50"));
        assertNull(v.getCalculatedValue());
    }

    @Test
    void testCopyClonesAndRelinksDetails() {
        PcmCostRecordValue v = new PcmCostRecordValue();
        v.setCostElement(mock(PcmCostElement.class));
        v.setCostRecordRange(mock(PcmCostRecordRange.class));
        v.setCostUom("EA");
        v.setCostValue(new BigDecimal("7"));
        v.setCostValueType("S");
        v.setCostValueDetail("d1", new BigDecimal("1"), null);

        PcmCostRecordValue copy = v.copy();
        assertNotSame(v, copy);
        assertSame(v.getCostElement(), copy.getCostElement());
        assertSame(v.getCostRecordRange(), copy.getCostRecordRange());
        assertEquals(v.getCostValue(), copy.getCostValue());
        assertEquals(v.getCostValueType(), copy.getCostValueType());
        assertEquals(1, copy.getCostValueDetails().size());
        PcmCostValueDetail copiedDetail = copy.getCostValueDetails().get("d1");
        assertSame(copy, copiedDetail.getCostRecordValue());
    }

    @Test
    void testCompareTo() {
        // CompareToBuilder requires Comparable, and PcmCostElement is not Comparable,
        // so use the same element instance and differentiate by costValue (BigDecimal is Comparable).
        PcmCostElement el = new PcmCostElement();
        el.setId(new PcmCostElementId("A", "CT"));
        PcmCostRecordValue a = new PcmCostRecordValue();
        a.setCostElement(el);
        a.setCostValue(new BigDecimal("1"));
        PcmCostRecordValue b = new PcmCostRecordValue();
        b.setCostElement(el);
        b.setCostValue(new BigDecimal("2"));
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void testEqualsAndHashCode() {
        PcmCostElement el = mock(PcmCostElement.class);
        PcmCostRecordRange r = mock(PcmCostRecordRange.class);
        PcmCostRecordValue a = new PcmCostRecordValue();
        PcmCostRecordValue b = new PcmCostRecordValue();
        a.setCostElement(el);
        a.setCostRecordRange(r);
        b.setCostElement(el);
        b.setCostRecordRange(r);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");

        b.setCostElement(mock(PcmCostElement.class));
        assertNotEquals(a, b);
    }
}
