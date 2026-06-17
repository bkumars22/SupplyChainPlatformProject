/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.xlob.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class XLOBItemAllocationTest {

    @Test
    void defaultConstructor_allFieldsNull() {
        XLOBItemAllocation a = new XLOBItemAllocation();
        assertNull(a.getId());
        assertNull(a.getXlobAllocation());
        assertNull(a.getSupplier());
        assertNull(a.getItem());
        assertNull(a.getManufacturerPart());
        assertNull(a.getStartDate());
        assertNull(a.getEndDate());
        assertNull(a.getAllocation());
        assertNull(a.getCreatedOn());
        assertNull(a.getCreatedBy());
    }

    @Test
    void settersAndGetters() {
        XLOBItemAllocation a = new XLOBItemAllocation();
        XLOBAllocation parent = new XLOBAllocation();
        BusinessEntity supplier = mock(BusinessEntity.class);
        Item item = mock(Item.class);
        Date start = new Date(1_000L);
        Date end = new Date(2_000L);
        Timestamp ts = new Timestamp(3_000L);

        a.setId(11L);
        a.setXlobAllocation(parent);
        a.setSupplier(supplier);
        a.setItem(item);
        a.setManufacturerPart("MP-1");
        a.setStartDate(start);
        a.setEndDate(end);
        a.setAllocation(0.75);
        a.setCreatedOn(ts);
        a.setCreatedBy("creator");

        assertEquals(11L, a.getId());
        assertSame(parent, a.getXlobAllocation());
        assertSame(supplier, a.getSupplier());
        assertSame(item, a.getItem());
        assertEquals("MP-1", a.getManufacturerPart());
        assertSame(start, a.getStartDate());
        assertSame(end, a.getEndDate());
        assertEquals(0.75, a.getAllocation());
        assertSame(ts, a.getCreatedOn());
        assertEquals("creator", a.getCreatedBy());
    }

    @Test
    void equals_reflexive() {
        XLOBItemAllocation a = new XLOBItemAllocation();
        assertEquals(a, a);
    }

    @Test
    void equals_null_returnsFalse() {
        assertNotEquals(null, new XLOBItemAllocation());
    }

    @Test
    void equals_otherType_returnsFalse() {
        assertNotEquals("string", new XLOBItemAllocation());
    }

    @Test
    void equals_emptyInstances_areEqual() {
        assertEquals(new XLOBItemAllocation(), new XLOBItemAllocation());
        assertEquals(new XLOBItemAllocation().hashCode(), new XLOBItemAllocation().hashCode());
    }

    @Test
    void equals_sameKeyFields_isEqual() {
        Item item = mock(Item.class);
        BusinessEntity supplier = mock(BusinessEntity.class);
        XLOBAllocation parent = new XLOBAllocation();
        Date start = new Date(0L);
        Date end = new Date(1L);

        XLOBItemAllocation a = build(parent, supplier, item, "MP", start, end);
        XLOBItemAllocation b = build(parent, supplier, item, "MP", start, end);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentManufacturerPart_notEqual() {
        Item item = mock(Item.class);
        BusinessEntity supplier = mock(BusinessEntity.class);
        XLOBAllocation parent = new XLOBAllocation();
        Date d = new Date(0L);
        XLOBItemAllocation a = build(parent, supplier, item, "MP-A", d, d);
        XLOBItemAllocation b = build(parent, supplier, item, "MP-B", d, d);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentEndDate_notEqual() {
        Item item = mock(Item.class);
        BusinessEntity supplier = mock(BusinessEntity.class);
        XLOBAllocation parent = new XLOBAllocation();
        XLOBItemAllocation a = build(parent, supplier, item, "MP", new Date(0L), new Date(1L));
        XLOBItemAllocation b = build(parent, supplier, item, "MP", new Date(0L), new Date(2L));
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentItem_notEqual() {
        BusinessEntity supplier = mock(BusinessEntity.class);
        XLOBAllocation parent = new XLOBAllocation();
        Date d = new Date(0L);
        XLOBItemAllocation a = build(parent, supplier, mock(Item.class), "MP", d, d);
        XLOBItemAllocation b = build(parent, supplier, mock(Item.class), "MP", d, d);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentSupplier_notEqual() {
        Item item = mock(Item.class);
        XLOBAllocation parent = new XLOBAllocation();
        Date d = new Date(0L);
        XLOBItemAllocation a = build(parent, mock(BusinessEntity.class), item, "MP", d, d);
        XLOBItemAllocation b = build(parent, mock(BusinessEntity.class), item, "MP", d, d);
        assertNotEquals(a, b);
    }

    private static XLOBItemAllocation build(XLOBAllocation parent, BusinessEntity supplier, Item item,
                                            String mp, Date start, Date end) {
        XLOBItemAllocation a = new XLOBItemAllocation();
        a.setXlobAllocation(parent);
        a.setSupplier(supplier);
        a.setItem(item);
        a.setManufacturerPart(mp);
        a.setStartDate(start);
        a.setEndDate(end);
        return a;
    }
}
