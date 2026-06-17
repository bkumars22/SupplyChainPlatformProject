/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PcmCostElementTypeTest {

    @Test
    void testValuesContainsExpected() {
        PcmCostElementType[] vals = PcmCostElementType.values();
        assertEquals(3, vals.length);
        assertEquals(PcmCostElementType.MATERIAL, vals[0]);
        assertEquals(PcmCostElementType.TRANSFORMATION, vals[1]);
        assertEquals(PcmCostElementType.FIXED, vals[2]);
    }

    @Test
    void testValueOf() {
        assertSame(PcmCostElementType.MATERIAL, PcmCostElementType.valueOf("MATERIAL"));
        assertSame(PcmCostElementType.TRANSFORMATION, PcmCostElementType.valueOf("TRANSFORMATION"));
        assertSame(PcmCostElementType.FIXED, PcmCostElementType.valueOf("FIXED"));
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> PcmCostElementType.valueOf("NOPE"));
    }

    @Test
    void testName() {
        assertEquals("MATERIAL", PcmCostElementType.MATERIAL.name());
        assertEquals("FIXED", PcmCostElementType.FIXED.name());
    }
}
