/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommodityProfileCostTypeTest {

    @Test
    void defaultsAreNull() {
        CommodityProfileCostType ct = new CommodityProfileCostType();
        assertNull(ct.getId());
        assertNull(ct.getCostType());
        assertNull(ct.getCommodityProfile());
    }

    @Test
    void idConstructorAssignsId() {
        CommodityProfileCostType ct = new CommodityProfileCostType(11L);
        assertEquals(11L, ct.getId());
    }

    @Test
    void settersStoreValues() {
        CommodityProfileCostType ct = new CommodityProfileCostType();
        CommodityProfile cp = new CommodityProfile(2L);
        ct.setId(3L);
        ct.setCostType("STANDARD");
        ct.setCommodityProfile(cp);

        assertEquals(3L, ct.getId());
        assertEquals("STANDARD", ct.getCostType());
        assertSame(cp, ct.getCommodityProfile());
    }

    @Test
    void equalsUsesProfileAndCostType() {
        CommodityProfile cp = new CommodityProfile(1L);
        CommodityProfileCostType a = new CommodityProfileCostType();
        a.setCommodityProfile(cp);
        a.setCostType("X");

        CommodityProfileCostType b = new CommodityProfileCostType();
        b.setCommodityProfile(cp);
        b.setCostType("X");

        CommodityProfileCostType diff = new CommodityProfileCostType();
        diff.setCommodityProfile(cp);
        diff.setCostType("Y");

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, diff);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
    }

    @Test
    void toStringContainsAllFields() {
        CommodityProfileCostType ct = new CommodityProfileCostType(9L);
        ct.setCostType("STD");
        String s = ct.toString();
        assertTrue(s.contains("9"));
        assertTrue(s.contains("STD"));
    }
}
