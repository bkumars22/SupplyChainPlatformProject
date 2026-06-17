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

class PcmCostValueDetailTest {

    @Test
    void testNoArgsConstructor() {
        PcmCostValueDetail d = new PcmCostValueDetail();
        assertNull(d.getCostValueDetailKey());
        assertNull(d.getCostRecordValue());
        assertNull(d.getCostValueName());
        assertNull(d.getCostValueValue());
        assertNull(d.getCostValueBlend());
    }

    @Test
    void testAllArgsConstructor() {
        PcmCostRecordValue crv = mock(PcmCostRecordValue.class);
        PcmCostValueDetail d = new PcmCostValueDetail(1L, crv, "name",
                new BigDecimal("12.34"), new BigDecimal("0.50"));
        assertEquals(1L, d.getCostValueDetailKey());
        assertSame(crv, d.getCostRecordValue());
        assertEquals("name", d.getCostValueName());
        assertEquals(new BigDecimal("12.34"), d.getCostValueValue());
        assertEquals(new BigDecimal("0.50"), d.getCostValueBlend());
    }

    @Test
    void testSettersAndGetters() {
        PcmCostValueDetail d = new PcmCostValueDetail();
        d.setCostValueDetailKey(2L);
        d.setCostValueName("nm");
        d.setCostValueValue(new BigDecimal("1"));
        d.setCostValueBlend(new BigDecimal("2"));
        PcmCostRecordValue crv = mock(PcmCostRecordValue.class);
        d.setCostRecordValue(crv);

        assertEquals(2L, d.getCostValueDetailKey());
        assertEquals("nm", d.getCostValueName());
        assertEquals(new BigDecimal("1"), d.getCostValueValue());
        assertEquals(new BigDecimal("2"), d.getCostValueBlend());
        assertSame(crv, d.getCostRecordValue());
    }

    @Test
    void testCopyProducesEqualButIndependentInstance() {
        PcmCostValueDetail d = new PcmCostValueDetail();
        d.setCostValueDetailKey(99L);
        d.setCostValueName("nm");
        d.setCostValueValue(new BigDecimal("3"));
        d.setCostValueBlend(new BigDecimal("4"));
        d.setCostRecordValue(mock(PcmCostRecordValue.class));

        PcmCostValueDetail copy = d.copy();
        assertNotSame(d, copy);
        // copy() intentionally skips the key and the cost record value link.
        assertNull(copy.getCostValueDetailKey());
        assertNull(copy.getCostRecordValue());
        assertEquals("nm", copy.getCostValueName());
        assertEquals(new BigDecimal("3"), copy.getCostValueValue());
        assertEquals(new BigDecimal("4"), copy.getCostValueBlend());
    }

    @Test
    void testCompareToBySameCostRecordValueAndName() {
        PcmCostRecordValue shared = mock(PcmCostRecordValue.class);
        PcmCostValueDetail a = new PcmCostValueDetail();
        PcmCostValueDetail b = new PcmCostValueDetail();
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
    void testEqualsReflexiveAndSelf() {
        PcmCostValueDetail a = new PcmCostValueDetail();
        a.setCostRecordValue(mock(PcmCostRecordValue.class));
        a.setCostValueName("X");
        assertEquals(a, a);
    }

    @Test
    void testEqualsWithNullAndDifferentType() {
        PcmCostValueDetail a = new PcmCostValueDetail();
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    void testEqualsContentBased() {
        PcmCostRecordValue shared = mock(PcmCostRecordValue.class);
        PcmCostValueDetail a = new PcmCostValueDetail();
        PcmCostValueDetail b = new PcmCostValueDetail();
        a.setCostRecordValue(shared);
        b.setCostRecordValue(shared);
        a.setCostValueName("N");
        b.setCostValueName("N");
        assertEquals(a, b);
        // hashCode only honors costRecordValue per implementation.
        assertEquals(a.hashCode(), b.hashCode());
    }
}
