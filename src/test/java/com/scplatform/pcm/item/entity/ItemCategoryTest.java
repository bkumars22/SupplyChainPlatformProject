/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.VersionRevision;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemCategoryTest {

    private static SCPlatformMessages prev;

    @BeforeAll
    static void install() throws Exception {
        prev = SCPlatformMessages.INSTANCE;
        SCPlatformMessages msgs = mock(SCPlatformMessages.class);
        when(msgs.getAuditMessage(eq("audit.itemCategoryTitle"), any(), any())).thenReturn("AUDIT_CAT_TITLE");
        Field f = SCPlatformMessages.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, msgs);
    }

    @AfterAll
    static void restore() throws Exception {
        Field f = SCPlatformMessages.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, prev);
    }

    @Test
    void testNoArgConstructorAndSetters() {
        ItemCategory c = new ItemCategory();
        c.setCategoryKey(10L);
        c.setCategoryId("CAT1");
        c.setCategoryName("Cat One");
        c.setProductFamily("FAM");
        c.setManagedFlag("y");

        BusinessEntity be = mock(BusinessEntity.class);
        c.setBusinessEntity(be);

        ItemCategory parent = new ItemCategory(99L);
        c.setParentCategory(parent);

        c.setItems(new java.util.HashSet<>());
        c.setAssignments(new java.util.HashSet<>());

        assertEquals(10L, c.getCategoryKey());
        assertEquals("CAT1", c.getCategoryId());
        assertEquals("Cat One", c.getCategoryName());
        assertEquals("FAM", c.getProductFamily());
        assertEquals("Y", c.getManagedFlag()); // upper-cased
        assertSame(be, c.getBusinessEntity());
        assertSame(parent, c.getParentCategory());
        assertEquals(99L, parent.getCategoryKey());
        assertNotNull(c.getItems());
        assertNotNull(c.getAssignments());
    }

    @Test
    void testSetManagedFlagNull() {
        ItemCategory c = new ItemCategory();
        c.setManagedFlag(null);
        assertNull(c.getManagedFlag());
    }

    @Test
    void testToStringIsCategoryName() {
        ItemCategory c = new ItemCategory();
        c.setCategoryName("Hello");
        assertEquals("Hello", c.toString());
    }

    @Test
    void testCategoriesNaturalKeyAsJSON() {
        ItemCategory c = new ItemCategory();
        c.setCategoryName("X");
        ObjectNode n = c.getCategoriesNaturalKeyAsJSON();
        assertEquals("X", n.get("categoryName").asText());
    }

    @Test
    void testGetAuditTitle() {
        ItemCategory c = new ItemCategory();
        c.setCategoryId("CAT");
        c.setCategoryName("Name");
        assertEquals("AUDIT_CAT_TITLE", c.getAuditTitle());
    }

    @Test
    void testEqualsHashCode() {
        BusinessEntity be = mock(BusinessEntity.class);
        ItemCategory a = new ItemCategory();
        a.setCategoryId("ID1");
        a.setBusinessEntity(be);

        ItemCategory b = new ItemCategory();
        b.setCategoryId("ID1");
        b.setBusinessEntity(be);

        ItemCategory c = new ItemCategory();
        c.setCategoryId("ID2");
        c.setBusinessEntity(be);

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }
}
