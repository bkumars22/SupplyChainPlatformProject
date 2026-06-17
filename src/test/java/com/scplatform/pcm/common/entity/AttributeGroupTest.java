/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

class AttributeGroupTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        AttributeGroup g = new AttributeGroup();
        assertNull(g.getAttributeGroupKey());
        assertNull(g.getAttributeGroupName());
        assertNull(g.getObjectType());
    }

    @Test
    void allArgConstructorAssignsFields() {
        AttributeGroup g = new AttributeGroup(7L, "GroupX", AttributeEntityType.ITEM);
        assertEquals(7L, g.getAttributeGroupKey());
        assertEquals("GroupX", g.getAttributeGroupName());
        assertEquals(AttributeEntityType.ITEM, g.getObjectType());
    }

    @Test
    void singleKeyConstructorOnlyAssignsKey() {
        AttributeGroup g = new AttributeGroup(99L);
        assertEquals(99L, g.getAttributeGroupKey());
        assertNull(g.getAttributeGroupName());
        assertNull(g.getObjectType());
    }

    @Test
    void builderWorks() {
        AttributeGroup g = AttributeGroup.builder()
                .attributeGroupKey(1L)
                .attributeGroupName("N")
                .objectType(AttributeEntityType.BOM)
                .build();
        assertEquals(1L, g.getAttributeGroupKey());
        assertEquals("N", g.getAttributeGroupName());
        assertEquals(AttributeEntityType.BOM, g.getObjectType());
    }

    @Test
    void equalsAndHashCodeUseNameAndObjectTypeOnly() {
        AttributeGroup a = new AttributeGroup(1L, "X", AttributeEntityType.ITEM);
        AttributeGroup b = new AttributeGroup(2L, "X", AttributeEntityType.ITEM);
        AttributeGroup c = new AttributeGroup(1L, "X", AttributeEntityType.BOM);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void getAttributeGroupNaturalKeyAsJSONReturnsExpectedFields() {
        AttributeGroup g = new AttributeGroup(1L, "GN", AttributeEntityType.COST);
        ObjectNode json = g.getAttributeGroupNaturalKeyAsJSON();
        assertNotNull(json);
        assertEquals("GN", json.get("attributeGroupName").asText());
        assertEquals("COST", json.get("objectType").asText());
    }
}
