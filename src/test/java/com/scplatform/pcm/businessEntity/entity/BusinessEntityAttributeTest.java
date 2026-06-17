/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.businessEntity.entity;

import com.scplatform.pcm.common.entity.AttributeGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessEntityAttributeTest {

    @Test
    void defaultConstructor() {
        BusinessEntityAttribute a = new BusinessEntityAttribute();
        assertNull(a.getBusinessEntityAttributeId());
        assertNull(a.getBusinessEntity());
        assertNull(a.getAttributeName());
        assertNull(a.getAttributeType());
        assertNull(a.getAttributeValue());
        assertNull(a.getDescription());
        assertNull(a.getAttributeGroup());
    }

    @Test
    void settersAndGetters() {
        BusinessEntityAttribute a = new BusinessEntityAttribute();
        BusinessEntity be = new BusinessEntity();
        AttributeGroup ag = new AttributeGroup();

        a.setBusinessEntityAttributeId(1L);
        a.setBusinessEntity(be);
        a.setAttributeGroup(ag);
        a.setAttributeName("attr1");
        a.setAttributeType("STRING");
        a.setAttributeValue("val1");
        a.setDescription("desc");

        assertEquals(1L, a.getBusinessEntityAttributeId());
        assertSame(be, a.getBusinessEntity());
        assertSame(ag, a.getAttributeGroup());
        assertEquals("attr1", a.getAttributeName());
        assertEquals("STRING", a.getAttributeType());
        assertEquals("val1", a.getAttributeValue());
        assertEquals("desc", a.getDescription());
    }

    @Test
    void equalsReflexive() {
        BusinessEntityAttribute a = new BusinessEntityAttribute();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        BusinessEntityAttribute a = new BusinessEntityAttribute();
        assertNotEquals(null, a);
    }

    @Test
    void equalsDifferentType() {
        BusinessEntityAttribute a = new BusinessEntityAttribute();
        assertNotEquals("string", a);
    }

    @Test
    void equalsSameFields() {
        BusinessEntityAttribute a = new BusinessEntityAttribute();
        a.setAttributeName("attr1");
        a.setAttributeType("STRING");

        BusinessEntityAttribute b = new BusinessEntityAttribute();
        b.setAttributeName("attr1");
        b.setAttributeType("STRING");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEqualsDifferentName() {
        BusinessEntityAttribute a = new BusinessEntityAttribute();
        a.setAttributeName("attr1");
        BusinessEntityAttribute b = new BusinessEntityAttribute();
        b.setAttributeName("attr2");
        assertNotEquals(a, b);
    }
}
