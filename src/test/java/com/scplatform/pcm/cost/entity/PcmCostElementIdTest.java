/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PcmCostElementIdTest {

    @Test
    void testNoArgsAndAllArgsConstructors() {
        PcmCostElementId empty = new PcmCostElementId();
        assertNull(empty.getCostElementKey());
        assertNull(empty.getCostTypeKey());

        PcmCostElementId id = new PcmCostElementId("E1", "T1");
        assertEquals("E1", id.getCostElementKey());
        assertEquals("T1", id.getCostTypeKey());
    }

    @Test
    void testSettersAndGetters() {
        PcmCostElementId id = new PcmCostElementId();
        id.setCostElementKey("E2");
        id.setCostTypeKey("T2");
        assertEquals("E2", id.getCostElementKey());
        assertEquals("T2", id.getCostTypeKey());
    }

    @Test
    void testEqualsReflexiveSymmetricAndContentBased() {
        PcmCostElementId a = new PcmCostElementId("E", "T");
        PcmCostElementId b = new PcmCostElementId("E", "T");
        PcmCostElementId c = new PcmCostElementId("X", "T");

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(b, a);
        assertNotEquals(a, c);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEqualsWithNullsAndDifferentType() {
        PcmCostElementId a = new PcmCostElementId("E", "T");
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    void testEqualsWithNullFields() {
        PcmCostElementId a = new PcmCostElementId(null, null);
        PcmCostElementId b = new PcmCostElementId(null, null);
        PcmCostElementId c = new PcmCostElementId("E", null);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(c, a);
        // hashCode should not throw for null fields.
        assertDoesNotThrow(a::hashCode);
    }

    @Test
    void testHashCodeDiffersForDifferentValues() {
        PcmCostElementId a = new PcmCostElementId("E1", "T1");
        PcmCostElementId b = new PcmCostElementId("E2", "T2");
        assertNotEquals(a.hashCode(), b.hashCode());
    }
}
