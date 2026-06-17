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

class PcmCostValueDetailExceptionTest {

    @Test
    void testNoArgsConstructor() {
        PcmCostValueDetailException d = new PcmCostValueDetailException();
        assertNull(d.getCostValueDetailKey());
        assertNull(d.getCostRecordValue());
        assertNull(d.getCostValueName());
        assertNull(d.getCostValueValue());
        assertNull(d.getCostValueBlend());
    }

    @Test
    void testAllArgsConstructorAndAccessors() {
        PcmCostRecordValueException crv = mock(PcmCostRecordValueException.class);
        PcmCostValueDetailException d = new PcmCostValueDetailException(
                42L, crv, "name", new BigDecimal("10"), new BigDecimal("0.5"));
        assertEquals(42L, d.getCostValueDetailKey());
        assertSame(crv, d.getCostRecordValue());
        assertEquals("name", d.getCostValueName());
        assertEquals(new BigDecimal("10"), d.getCostValueValue());
        assertEquals(new BigDecimal("0.5"), d.getCostValueBlend());
    }

    @Test
    void testSetters() {
        PcmCostValueDetailException d = new PcmCostValueDetailException();
        d.setCostValueDetailKey(7L);
        d.setCostValueName("X");
        d.setCostValueValue(new BigDecimal("1"));
        d.setCostValueBlend(new BigDecimal("2"));
        PcmCostRecordValueException crv = mock(PcmCostRecordValueException.class);
        d.setCostRecordValue(crv);
        assertEquals(7L, d.getCostValueDetailKey());
        assertEquals("X", d.getCostValueName());
        assertEquals(new BigDecimal("1"), d.getCostValueValue());
        assertEquals(new BigDecimal("2"), d.getCostValueBlend());
        assertSame(crv, d.getCostRecordValue());
    }

    @Test
    void testCopyResetsKeyAndLink() {
        PcmCostRecordValueException crv = mock(PcmCostRecordValueException.class);
        PcmCostValueDetailException d = new PcmCostValueDetailException(
                42L, crv, "n", new BigDecimal("3"), new BigDecimal("0.7"));
        PcmCostValueDetailException copy = d.copy();
        assertNotSame(d, copy);
        // Implementation copies name/value/blend; key and link are intentionally left null.
        assertNull(copy.getCostValueDetailKey());
        assertNull(copy.getCostRecordValue());
        assertEquals("n", copy.getCostValueName());
        assertEquals(new BigDecimal("3"), copy.getCostValueValue());
        assertEquals(new BigDecimal("0.7"), copy.getCostValueBlend());
    }

    @Test
    void testCompareToBySameLinkAndName() {
        PcmCostRecordValueException shared = mock(PcmCostRecordValueException.class);
        PcmCostValueDetailException a = new PcmCostValueDetailException();
        PcmCostValueDetailException b = new PcmCostValueDetailException();
        a.setCostRecordValue(shared);
        b.setCostRecordValue(shared);
        a.setCostValueName("A");
        b.setCostValueName("B");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        b.setCostValueName("A");
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void testEqualsAndHashCode() {
        PcmCostRecordValueException shared = mock(PcmCostRecordValueException.class);
        PcmCostValueDetailException a = new PcmCostValueDetailException();
        PcmCostValueDetailException b = new PcmCostValueDetailException();
        a.setCostRecordValue(shared);
        b.setCostRecordValue(shared);
        a.setCostValueName("N");
        b.setCostValueName("N");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
    }
}
