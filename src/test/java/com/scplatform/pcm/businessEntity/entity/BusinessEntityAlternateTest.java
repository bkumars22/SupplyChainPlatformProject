/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.businessEntity.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessEntityAlternateTest {

    @Test
    void defaultConstructor() {
        BusinessEntityAlternate alt = new BusinessEntityAlternate();
        assertNull(alt.getBusinessEntityAltKey());
        assertNull(alt.getBusinessEntityName());
        assertNull(alt.getBusinessEntity());
    }

    @Test
    void constructorWithKey() {
        BusinessEntityAlternate alt = new BusinessEntityAlternate(5L);
        assertEquals(5L, alt.getBusinessEntityAltKey());
    }

    @Test
    void settersAndGetters() {
        BusinessEntityAlternate alt = new BusinessEntityAlternate();
        alt.setBusinessEntityAltKey(10L);
        alt.setBusinessEntityName("Alt Name");
        BusinessEntity be = new BusinessEntity();
        alt.setBusinessEntity(be);

        assertEquals(10L, alt.getBusinessEntityAltKey());
        assertEquals("Alt Name", alt.getBusinessEntityName());
        assertSame(be, alt.getBusinessEntity());
    }

    @Test
    void equalsCaseInsensitive() {
        BusinessEntityAlternate a = new BusinessEntityAlternate();
        a.setBusinessEntityName("vendor");
        BusinessEntityAlternate b = new BusinessEntityAlternate();
        b.setBusinessEntityName("VENDOR");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsReflexive() {
        BusinessEntityAlternate a = new BusinessEntityAlternate();
        a.setBusinessEntityName("vendor");
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        BusinessEntityAlternate a = new BusinessEntityAlternate();
        assertNotEquals(null, a);
    }

    @Test
    void equalsDifferentType() {
        BusinessEntityAlternate a = new BusinessEntityAlternate();
        assertNotEquals("string", a);
    }

    @Test
    void notEqualsDifferentName() {
        BusinessEntityAlternate a = new BusinessEntityAlternate();
        a.setBusinessEntityName("vendor");
        BusinessEntityAlternate b = new BusinessEntityAlternate();
        b.setBusinessEntityName("other");
        assertNotEquals(a, b);
    }
}
