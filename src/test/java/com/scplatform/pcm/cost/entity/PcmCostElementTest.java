/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.cost.enums.PcmCostElementType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PcmCostElementTest {

    @Test
    void testNoArgsAndAllArgsConstructors() {
        PcmCostElement empty = new PcmCostElement();
        assertNull(empty.getId());

        PcmCostElementId id = new PcmCostElementId("E1", "T1");
        PcmCostType ct = mock(PcmCostType.class);
        PcmCostElement full = new PcmCostElement(id, ct, "name", "S", 5L,
                PcmCostElementType.MATERIAL, Boolean.TRUE);
        assertSame(id, full.getId());
        assertSame(ct, full.getPcmCostType());
        assertEquals("name", full.getCostElementName());
        assertEquals("S", full.getCostElementValueType());
        assertEquals(5L, full.getDisplayOrder());
        assertEquals(PcmCostElementType.MATERIAL, full.getCostElementType());
        assertEquals(Boolean.TRUE, full.getIsRequired());
    }

    @Test
    void testSettersGetters() {
        PcmCostElement e = new PcmCostElement();
        PcmCostElementId id = new PcmCostElementId("E", "T");
        e.setId(id);
        e.setCostElementName("nm");
        e.setCostElementValueType("B");
        e.setDisplayOrder(2L);
        e.setCostElementType(PcmCostElementType.FIXED);
        e.setIsRequired(Boolean.FALSE);
        assertSame(id, e.getId());
        assertEquals("nm", e.getCostElementName());
        assertEquals("B", e.getCostElementValueType());
        assertEquals(2L, e.getDisplayOrder());
        assertEquals(PcmCostElementType.FIXED, e.getCostElementType());
        assertEquals(Boolean.FALSE, e.getIsRequired());
    }

    @Test
    void testIsOfTypeAndIsNotOfType() {
        PcmCostElement e = new PcmCostElement();
        e.setCostElementType(PcmCostElementType.TRANSFORMATION);
        assertTrue(e.isOfType(PcmCostElementType.TRANSFORMATION));
        assertFalse(e.isOfType(PcmCostElementType.MATERIAL));
        assertFalse(e.isNotOfType(PcmCostElementType.TRANSFORMATION));
        assertTrue(e.isNotOfType(PcmCostElementType.FIXED));
    }

    @Test
    void testGetCostElementKeyDelegatesToId() {
        PcmCostElement e = new PcmCostElement();
        e.setId(new PcmCostElementId("XK", "TK"));
        assertEquals("XK", e.getCostElementKey());
    }

    @Test
    void testEqualsAndHashCodeViaLombokData() {
        PcmCostElementId id = new PcmCostElementId("E", "T");
        PcmCostElement e1 = new PcmCostElement();
        PcmCostElement e2 = new PcmCostElement();
        e1.setId(id);
        e1.setCostElementName("nm");
        e2.setId(new PcmCostElementId("E", "T"));
        e2.setCostElementName("nm");
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());

        e2.setCostElementName("other");
        assertNotEquals(e1, e2);
    }

    @Test
    void testToStringExcludesPcmCostType() {
        PcmCostElement e = new PcmCostElement();
        e.setPcmCostType(mock(PcmCostType.class));
        e.setCostElementName("MyEl");
        String s = e.toString();
        assertTrue(s.contains("MyEl"));
        assertFalse(s.contains("pcmCostType"));
    }
}
