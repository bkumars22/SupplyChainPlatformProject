/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TamSupplierDataTest {

    @Test
    void defaultConstructor_initializesMap() {
        TamSupplierData data = new TamSupplierData();
        assertNotNull(data.getTamItemDatas());
        assertTrue(data.getTamItemDatas().isEmpty());
        assertNull(data.getSupplierAllocation());
    }

    @Test
    void setterAndGetter_supplierAllocation() {
        TamSupplierData data = new TamSupplierData();
        data.setSupplierAllocation("60%");
        assertEquals("60%", data.getSupplierAllocation());
    }

    @Test
    void getItemData_createsEntryIfMissing() {
        TamSupplierData data = new TamSupplierData();

        TamItemData item = data.getItemData("item-001");

        assertNotNull(item);
        assertTrue(data.getTamItemDatas().containsKey("item-001"));
    }

    @Test
    void getItemData_returnsSameInstanceOnSecondCall() {
        TamSupplierData data = new TamSupplierData();
        TamItemData first = data.getItemData("key-X");
        TamItemData second = data.getItemData("key-X");

        assertSame(first, second);
    }

    @Test
    void getItemData_storesAndRetrievesAllocation() {
        TamSupplierData data = new TamSupplierData();
        TamItemData item = data.getItemData("item-002");
        item.setItemAllocation("30%");

        TamItemData retrieved = data.getItemData("item-002");
        assertEquals("30%", retrieved.getItemAllocation());
    }

    @Test
    void getTamItemDatas_returnsAllEntries() {
        TamSupplierData data = new TamSupplierData();
        data.getItemData("A");
        data.getItemData("B");
        data.getItemData("C");

        Map<String, TamItemData> map = data.getTamItemDatas();
        assertEquals(3, map.size());
        assertTrue(map.containsKey("A"));
        assertTrue(map.containsKey("B"));
        assertTrue(map.containsKey("C"));
    }

    @Test
    void toString_containsSupplierAllocation() {
        TamSupplierData data = new TamSupplierData();
        data.setSupplierAllocation("45%");
        String str = data.toString();
        assertTrue(str.contains("TamSupplierData"));
        assertTrue(str.contains("45%"));
    }
}
