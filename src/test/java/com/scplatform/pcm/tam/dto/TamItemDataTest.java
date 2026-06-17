/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TamItemDataTest {

    @Test
    void defaultConstructor_createsInstance() {
        TamItemData data = new TamItemData();
        assertNotNull(data);
        assertNull(data.getItemAllocation());
    }

    @Test
    void setterAndGetter_workCorrectly() {
        TamItemData data = new TamItemData();
        data.setItemAllocation("50%");
        assertEquals("50%", data.getItemAllocation());
    }

    @Test
    void toString_containsItemAllocation() {
        TamItemData data = new TamItemData();
        data.setItemAllocation("75%");
        assertTrue(data.toString().contains("75%"));
        assertTrue(data.toString().contains("TamItemData"));
    }
}
