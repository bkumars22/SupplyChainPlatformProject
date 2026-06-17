/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PcmCostTypeTest {

    @Test
    void testNoArgsConstructorDefaults() {
        PcmCostType ct = new PcmCostType();
        assertNull(ct.getCostTypeKey());
        assertNull(ct.getCostTypeName());
        assertNull(ct.getDisplayOrder());
        assertFalse(ct.isUseInRollup());
        assertFalse(ct.isUseSupplierInRollup());
        assertFalse(ct.isAllowMultiProvider());
        assertFalse(ct.isUseInItemCategoryCost());
        // @Builder.Default initializes the set even via the no-args constructor.
        assertNotNull(ct.getPcmCostElements());
        assertTrue(ct.getPcmCostElements().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        Set<PcmCostElement> elements = new HashSet<>();
        elements.add(mock(PcmCostElement.class));
        PcmCostType ct = new PcmCostType("KEY", "Name", 1L, true, true, true, true, elements);
        assertEquals("KEY", ct.getCostTypeKey());
        assertEquals("Name", ct.getCostTypeName());
        assertEquals(1L, ct.getDisplayOrder());
        assertTrue(ct.isUseInRollup());
        assertTrue(ct.isUseSupplierInRollup());
        assertTrue(ct.isAllowMultiProvider());
        assertTrue(ct.isUseInItemCategoryCost());
        assertSame(elements, ct.getPcmCostElements());
    }

    @Test
    void testBuilderInitializesDefaults() {
        PcmCostType ct = PcmCostType.builder().costTypeKey("K").build();
        assertEquals("K", ct.getCostTypeKey());
        assertFalse(ct.isUseInRollup());
        assertFalse(ct.isUseSupplierInRollup());
        assertFalse(ct.isAllowMultiProvider());
        assertFalse(ct.isUseInItemCategoryCost());
        assertNotNull(ct.getPcmCostElements());
        assertTrue(ct.getPcmCostElements().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        PcmCostType ct = new PcmCostType();
        ct.setCostTypeKey("K");
        ct.setCostTypeName("N");
        ct.setDisplayOrder(9L);
        ct.setUseInRollup(true);
        ct.setUseSupplierInRollup(true);
        ct.setAllowMultiProvider(true);
        ct.setUseInItemCategoryCost(true);
        Set<PcmCostElement> set = new HashSet<>();
        ct.setPcmCostElements(set);

        assertEquals("K", ct.getCostTypeKey());
        assertEquals("N", ct.getCostTypeName());
        assertEquals(9L, ct.getDisplayOrder());
        assertTrue(ct.isUseInRollup());
        assertTrue(ct.isUseSupplierInRollup());
        assertTrue(ct.isAllowMultiProvider());
        assertTrue(ct.isUseInItemCategoryCost());
        assertSame(set, ct.getPcmCostElements());
    }

    @Test
    void testHashCodeUsesKey() {
        PcmCostType ct = new PcmCostType();
        ct.setCostTypeKey("ABC");
        assertEquals("ABC".hashCode(), ct.hashCode());

        PcmCostType empty = new PcmCostType();
        assertEquals(0, empty.hashCode());
    }

    @Test
    void testEqualsByKeyOnly() {
        PcmCostType a = new PcmCostType();
        a.setCostTypeKey("X");
        a.setCostTypeName("one");
        PcmCostType b = new PcmCostType();
        b.setCostTypeKey("X");
        b.setCostTypeName("DIFFERENT");
        assertEquals(a, b);

        b.setCostTypeKey("Y");
        assertNotEquals(a, b);
    }

    @Test
    void testEqualsWithNullAndDifferentType() {
        PcmCostType a = new PcmCostType();
        a.setCostTypeKey("X");
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
        assertEquals(a, a);
    }

    @Test
    void testCompareToOrdersByDisplayOrderThenKey() {
        PcmCostType a = PcmCostType.builder().costTypeKey("A").displayOrder(1L).build();
        PcmCostType b = PcmCostType.builder().costTypeKey("B").displayOrder(2L).build();
        PcmCostType c = PcmCostType.builder().costTypeKey("C").displayOrder(2L).build();

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertTrue(b.compareTo(c) < 0);
        assertEquals(0, a.compareTo(PcmCostType.builder().costTypeKey("A").displayOrder(1L).build()));
    }

    @Test
    void testToStringReturnsKey() {
        PcmCostType ct = new PcmCostType();
        ct.setCostTypeKey("KEY-1");
        assertEquals("KEY-1", ct.toString());
    }
}
