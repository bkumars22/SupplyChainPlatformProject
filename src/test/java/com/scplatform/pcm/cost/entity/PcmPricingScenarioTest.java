/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PcmPricingScenarioTest {

    @Test
    void testNoArgsConstructor() {
        PcmPricingScenario s = new PcmPricingScenario();
        assertNull(s.getPricingScenarioKey());
        assertNull(s.getPricingScenarioName());
        assertNull(s.isRangeBased());
    }

    @Test
    void testTwoArgConstructorAndGetters() {
        PcmPricingScenario s = new PcmPricingScenario(10L, "Annual");
        assertEquals(10L, s.getPricingScenarioKey());
        assertEquals("Annual", s.getPricingScenarioName());
    }

    @Test
    void testSettersAndIsRangeBased() {
        PcmPricingScenario s = new PcmPricingScenario();
        s.setPricingScenarioKey(7L);
        s.setPricingScenarioName("Spot");
        s.setRangeBased(Boolean.TRUE);
        assertEquals(7L, s.getPricingScenarioKey());
        assertEquals("Spot", s.getPricingScenarioName());
        assertEquals(Boolean.TRUE, s.isRangeBased());
        assertEquals(Boolean.TRUE, s.getRangeBased());
    }

    @Test
    void testHashCodeUsesKey() {
        PcmPricingScenario s = new PcmPricingScenario(5L, "X");
        assertEquals(Long.valueOf(5L).hashCode(), s.hashCode());

        PcmPricingScenario empty = new PcmPricingScenario();
        assertEquals(0, empty.hashCode());
    }

    @Test
    void testEqualsReflexiveAndSelf() {
        PcmPricingScenario s = new PcmPricingScenario(1L, "N");
        s.setRangeBased(Boolean.FALSE);
        assertEquals(s, s);
    }

    @Test
    void testEqualsByNameAndRangeBased() {
        PcmPricingScenario a = new PcmPricingScenario(1L, "Name");
        a.setRangeBased(Boolean.TRUE);
        PcmPricingScenario b = new PcmPricingScenario(2L, "Name");
        b.setRangeBased(Boolean.TRUE);
        // Equality is name + isRangeBased based (key intentionally ignored).
        assertEquals(a, b);

        b.setRangeBased(Boolean.FALSE);
        assertNotEquals(a, b);
    }

    @Test
    void testEqualsWithNullAndDifferentType() {
        PcmPricingScenario a = new PcmPricingScenario(1L, "N");
        a.setRangeBased(Boolean.TRUE);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    void testToString() {
        PcmPricingScenario s = new PcmPricingScenario(1L, "MyScenario");
        assertEquals("MyScenario", s.toString());
    }
}
