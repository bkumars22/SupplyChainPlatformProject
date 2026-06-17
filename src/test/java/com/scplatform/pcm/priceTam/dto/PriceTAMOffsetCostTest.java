/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.priceTam.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriceTAMOffsetCostTest {

    @Test
    void fullConstructor_normalizesNullsToEmpty() {
        PriceTAMOffsetCost c = new PriceTAMOffsetCost(null, null, null, null, 5);
        // Constructor stores "" for nulls; toString surfaces them
        assertEquals(Integer.valueOf(5), c.getOffsetValue());
        assertTrue(c.toString().contains("dataSource="));
        assertTrue(c.toString().contains("offsetValue=5"));
    }

    @Test
    void shortConstructor_offsetValueRemainsNull() {
        PriceTAMOffsetCost c = new PriceTAMOffsetCost("DS", "CT", "CC", "IB");
        assertNull(c.getOffsetValue());
    }

    @Test
    void setOffsetValue() {
        PriceTAMOffsetCost c = new PriceTAMOffsetCost("a", "b", "c", "d");
        c.setOffsetValue(7);
        assertEquals(Integer.valueOf(7), c.getOffsetValue());
    }

    @Test
    void equals_sameValues() {
        PriceTAMOffsetCost a = new PriceTAMOffsetCost("a", "b", "c", "d", 1);
        PriceTAMOffsetCost b = new PriceTAMOffsetCost("a", "b", "c", "d", 1);
        assertEquals(a, b);
    }

    @Test
    void equals_wildcardOnOther() {
        PriceTAMOffsetCost a = new PriceTAMOffsetCost("a", "b", "c", "d", 1);
        PriceTAMOffsetCost wildcard = new PriceTAMOffsetCost("*", "*", "*", "*", 1);
        // Other has wildcards, so all four field comparisons fall through to "true"
        assertEquals(a, wildcard);
    }

    @Test
    void equals_selfNullAndType() {
        PriceTAMOffsetCost a = new PriceTAMOffsetCost("a", "b", "c", "d", 1);
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    void toString_containsAllFields() {
        PriceTAMOffsetCost c = new PriceTAMOffsetCost("ds", "ct", "cc", "ib", 9);
        String s = c.toString();
        assertTrue(s.contains("dataSource=ds"));
        assertTrue(s.contains("costType=ct"));
        assertTrue(s.contains("commodityCode=cc"));
        assertTrue(s.contains("itemBusinessName=ib"));
        assertTrue(s.contains("offsetValue=9"));
    }
}
