/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.item.entity.ItemCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PcmItemCategoryCostRecordTest {

    @Test
    void testAllSettersAndGetters() {
        PcmItemCategoryCostRecord r = new PcmItemCategoryCostRecord();
        assertNull(r.getItemCategoryCostRecordKey());
        assertNull(r.getItemCategory());
        assertNull(r.getContextName());
        assertNull(r.getContextType());
        assertNull(r.getCost());

        ItemCategory ic = mock(ItemCategory.class);
        r.setItemCategoryCostRecordKey(42L);
        r.setItemCategory(ic);
        r.setContextName("CTX");
        r.setContextType("TYP");
        r.setCost(new BigDecimal("9.99"));

        assertEquals(42L, r.getItemCategoryCostRecordKey());
        assertSame(ic, r.getItemCategory());
        assertEquals("CTX", r.getContextName());
        assertEquals("TYP", r.getContextType());
        assertEquals(new BigDecimal("9.99"), r.getCost());
    }

    @Test
    void testIsSerializable() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(PcmItemCategoryCostRecord.class));
    }
}
