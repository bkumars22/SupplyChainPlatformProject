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

class PcmCostRecordValueExceptionTest {

    @Test
    void testNoArgsAndDefaults() {
        PcmCostRecordValueException v = new PcmCostRecordValueException();
        assertNull(v.getCostRecordValueKey());
        assertNotNull(v.getCostValueDetails());
        assertTrue(v.getCostValueDetails().isEmpty());
    }

    @Test
    void testAddCostValue_InitializesAndAccumulates() {
        PcmCostRecordValueException v = new PcmCostRecordValueException();
        v.addCostValue(new BigDecimal("3"));
        assertEquals(new BigDecimal("3"), v.getCostValue());
        v.addCostValue(new BigDecimal("2"));
        assertEquals(new BigDecimal("5"), v.getCostValue());
    }

    @Test
    void testSetDetailByName_CreatesAndUpdates() {
        PcmCostRecordValueException v = new PcmCostRecordValueException();
        PcmCostValueDetailException first = v.setCostValueDetail("n", BigDecimal.ONE, new BigDecimal("0.1"));
        assertNotNull(first);
        assertSame(v, first.getCostRecordValue());
        PcmCostValueDetailException second = v.setCostValueDetail("n", new BigDecimal("9"), new BigDecimal("0.9"));
        assertSame(first, second);
        assertEquals(new BigDecimal("9"), second.getCostValueValue());
        assertEquals(1, v.getCostValueDetails().size());
    }

    @Test
    void testSetDetailObject_NewAndReplace() {
        PcmCostRecordValueException v = new PcmCostRecordValueException();
        PcmCostValueDetailException d = new PcmCostValueDetailException();
        d.setCostValueName("k");
        PcmCostValueDetailException prev = v.setCostValueDetail(d);
        assertNull(prev);
        assertSame(v, d.getCostRecordValue());

        PcmCostValueDetailException replacement = new PcmCostValueDetailException();
        replacement.setCostValueName("k");
        PcmCostValueDetailException old = v.setCostValueDetail(replacement);
        assertSame(d, old);
        assertNull(d.getCostRecordValue());
        // Replacement is intentionally NOT re-inserted per current behavior.
        assertFalse(v.getCostValueDetails().containsKey("k"));
    }

    @Test
    void testCopy() {
        PcmCostRecordValueException v = new PcmCostRecordValueException();
        v.setCostElement(mock(PcmCostElement.class));
        v.setCostRecordRange(mock(PcmCostRecordRangeException.class));
        v.setCostUom("EA");
        v.setCostValue(new BigDecimal("4"));
        v.setCostValueType("S");
        v.setCostValueDetail("d", new BigDecimal("1"), new BigDecimal("1"));

        PcmCostRecordValueException copy = v.copy();
        assertNotSame(v, copy);
        assertEquals(v.getCostValue(), copy.getCostValue());
        assertEquals(1, copy.getCostValueDetails().size());
        assertSame(copy, copy.getCostValueDetails().get("d").getCostRecordValue());
    }

    @Test
    void testCompareToUsesElementThenValue() {
        // Same element instance for both; differentiate by costValue (BigDecimal is Comparable).
        PcmCostElement el = new PcmCostElement();
        el.setId(new PcmCostElementId("A", "CT"));
        PcmCostRecordValueException a = new PcmCostRecordValueException();
        a.setCostElement(el);
        a.setCostValue(BigDecimal.ONE);
        PcmCostRecordValueException b = new PcmCostRecordValueException();
        b.setCostElement(el);
        b.setCostValue(new BigDecimal("5"));
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void testEqualsAndHashCode() {
        PcmCostElement el = mock(PcmCostElement.class);
        PcmCostRecordRangeException r = mock(PcmCostRecordRangeException.class);
        PcmCostRecordValueException a = new PcmCostRecordValueException();
        a.setCostElement(el);
        a.setCostRecordRange(r);
        PcmCostRecordValueException b = new PcmCostRecordValueException();
        b.setCostElement(el);
        b.setCostRecordRange(r);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
    }
}
