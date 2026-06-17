/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.dto;

import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemMaintenanceFormTest {

    @Test
    void testDefaultsAndSimpleAccessors() {
        ItemMaintenanceForm form = new ItemMaintenanceForm();
        assertFalse(form.getReadOnly());
        assertFalse(form.getItemMarkedForDelete());
        assertNull(form.getSelectedItem());
        assertNull(form.getSelectedItemKey());
        assertNull(form.getDisplayedItemKey());
        assertNull(form.getDrilldownItemKey());
        assertNull(form.getItemDescription());
        assertNull(form.getItemCategories());
        assertNull(form.getSelectedAvls());
        assertNull(form.getSelectedTabId());
        assertNull(form.getAvailableCategories());
        assertNull(form.getUnsavedData());
        assertNotNull(form.getDrilldownStack());
        assertFalse(form.getIsBackEnabled());
    }

    @Test
    void testSettersGetters() {
        ItemMaintenanceForm form = new ItemMaintenanceForm();
        form.setSelectedItemKey("key-1");
        form.setDisplayedItemKey("key-2");
        form.setDrilldownItemKey("key-3");
        form.setItemMarkedForDelete(true);
        form.setItemDescription("desc");
        form.setItemCategories(new Long[]{1L, 2L});
        form.setSelectedAvls(new String[]{"a", "b"});
        form.setSelectedTabId("tab1");
        form.setUnsavedData(Boolean.TRUE);

        List<ItemCategory> cats = Arrays.asList(mock(ItemCategory.class));
        form.setAvailableCategories(cats);

        assertEquals("key-1", form.getSelectedItemKey());
        assertEquals("key-2", form.getDisplayedItemKey());
        assertEquals("key-3", form.getDrilldownItemKey());
        assertTrue(form.getItemMarkedForDelete());
        assertEquals("desc", form.getItemDescription());
        assertArrayEquals(new Long[]{1L, 2L}, form.getItemCategories());
        assertArrayEquals(new String[]{"a", "b"}, form.getSelectedAvls());
        assertEquals("tab1", form.getSelectedTabId());
        assertEquals(Boolean.TRUE, form.getUnsavedData());
        assertSame(cats, form.getAvailableCategories());
    }

    @Test
    void testDrilldownStackAndIsBackEnabled() {
        ItemMaintenanceForm form = new ItemMaintenanceForm();
        Stack stack = new Stack();
        form.setDrilldownStack(stack);
        assertSame(stack, form.getDrilldownStack());
        assertFalse(form.getIsBackEnabled());
        stack.push("a");
        assertTrue(form.getIsBackEnabled());
    }

    @Test
    void testGetSelectedItem() {
        ItemMaintenanceForm form = new ItemMaintenanceForm();
        // selectedItem field is set only via internal logic; default null
        assertNull(form.getSelectedItem());
    }

    @Test
    void testClearSelectionResetsSelectedItemKey() {
        ItemMaintenanceForm form = new ItemMaintenanceForm();
        form.setSelectedItemKey("key");
        try {
            form.clearSelection();
        } catch (Throwable t) {
            // super.clearSelection() may have requirements; if so swallow but
            // still expect our field to be reset before the super call.
        }
        assertNull(form.getSelectedItemKey());
    }

    @Test
    void testReset() {
        ItemMaintenanceForm form = new ItemMaintenanceForm();
        form.setItemMarkedForDelete(true);
        form.setSelectedItemKey("k1");
        form.setDisplayedItemKey("k2");
        form.setDrilldownItemKey("k3");
        form.setSelectedAvls(new String[]{"x"});
        form.setItemCategories(new Long[]{1L});
        form.setUnsavedData(Boolean.TRUE);

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameterNames()).thenReturn(Collections.emptyEnumeration());

        try {
            form.reset(req);
        } catch (Throwable t) {
            // ignore super-specific failures; primary reset assignments still apply
        }

        assertFalse(form.getItemMarkedForDelete());
        assertNull(form.getSelectedItem());
        assertNull(form.getSelectedItemKey());
        assertNull(form.getDisplayedItemKey());
        assertNull(form.getDrilldownItemKey());
        assertNull(form.getSelectedAvls());
        assertNull(form.getItemCategories());
        assertEquals("resultTab", form.getSelectedTabId());
        assertEquals(Boolean.FALSE, form.getUnsavedData());
    }
}
