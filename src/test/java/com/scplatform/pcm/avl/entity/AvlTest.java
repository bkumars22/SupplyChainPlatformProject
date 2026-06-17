/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.avl.entity;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AvlTest {

    @Test
    void defaultConstructor() {
        Avl avl = new Avl();
        assertNull(avl.getAvlKey());
        assertNull(avl.getSupplier());
        assertNull(avl.getItem());
        assertNull(avl.getSupplierItem());
        assertNotNull(avl.getSupplierSites());
        assertNotNull(avl.getInsertDate());
        assertFalse(avl.getDeleteFlag());
        assertFalse(avl.getCurrentFlag());
        assertNotNull(avl.getAttributes());
    }

    @Test
    void constructorWithKey() {
        Avl avl = new Avl(99L);
        assertEquals(99L, avl.getAvlKey());
    }

    @Test
    void settersAndGetters() {
        Avl avl = new Avl();
        avl.setAvlKey(1L);
        avl.setPreferredStatusCode("PREF");
        avl.setDescription("desc");
        avl.setCurrentFlag(true);
        avl.setDeleteFlag(true);
        avl.setSuppliedBy("Vendor1");

        Date insert = new Date(100L);
        Date update = new Date(200L);
        avl.setInsertDate(insert);
        avl.setUpdateDate(update);

        assertEquals(1L, avl.getAvlKey());
        assertEquals("PREF", avl.getPreferredStatusCode());
        assertEquals("desc", avl.getDescription());
        assertTrue(avl.getCurrentFlag());
        assertTrue(avl.getDeleteFlag());
        assertEquals("Vendor1", avl.getSuppliedBy());
        assertEquals(insert, avl.getInsertDate());
        assertEquals(update, avl.getUpdateDate());
    }

    @Test
    void itemSetterGetter() {
        Avl avl = new Avl();
        Item item = new Item();
        item.setItemKey(5L);
        avl.setItem(item);
        assertSame(item, avl.getItem());
        assertEquals(5L, avl.getItemKey());
    }

    @Test
    void supplierItemSetterGetter() {
        Avl avl = new Avl();
        Item supplierItem = new Item();
        supplierItem.setItemKey(7L);
        avl.setSupplierItem(supplierItem);
        assertSame(supplierItem, avl.getSupplierItem());
        assertEquals(7L, avl.getSupplierItemKey());
    }

    @Test
    void itemKeyNullWhenNoItem() {
        Avl avl = new Avl();
        assertNull(avl.getItemKey());
        assertNull(avl.getSupplierItemKey());
    }

    @Test
    void setSupplierSetsSuppliedBy() {
        Avl avl = new Avl();
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityName("Vendor Corp");
        avl.setSupplier(be);
        assertSame(be, avl.getSupplier());
        assertEquals("Vendor Corp", avl.getSuppliedBy());
    }

    @Test
    void bomSetterGetter() {
        Avl avl = new Avl();
        Bom bom = new Bom();
        avl.setBom(bom);
        assertSame(bom, avl.getBom());
    }

    @Test
    void supplierSitesSetterGetter() {
        Avl avl = new Avl();
        Set<Site> sites = new HashSet<>();
        sites.add(new Site());
        avl.setSupplierSites(sites);
        assertEquals(1, avl.getSupplierSites().size());
    }

    @Test
    void attributesSetterGetter() {
        Avl avl = new Avl();
        Attribute attr = new Attribute();
        attr.setAttrName("color");
        attr.setAttrValue("red");
        assertTrue(avl.addAttribute(attr));
        assertFalse(avl.addAttribute(attr)); // duplicate
        assertEquals("red", avl.getAttribute("color"));
        assertNull(avl.getAttribute("missing"));
    }

    @Test
    void setAttributes() {
        Avl avl = new Avl();
        List<Attribute> attrs = new java.util.ArrayList<>();
        avl.setAttributes(attrs);
        assertSame(attrs, avl.getAttributes());
    }

    @Test
    void equalsReflexive() {
        Avl avl = new Avl();
        assertEquals(avl, avl);
    }

    @Test
    void equalsNull() {
        Avl avl = new Avl();
        assertNotEquals(null, avl);
    }

    @Test
    void equalsDifferentType() {
        Avl avl = new Avl();
        assertNotEquals("string", avl);
    }

    @Test
    void equalsSameItemAndSupplierItem() {
        Item item = new Item();
        item.setItemKey(1L);
        Item supplier = new Item();
        supplier.setItemKey(2L);

        Avl a = new Avl();
        a.setItem(item);
        a.setSupplierItem(supplier);

        Avl b = new Avl();
        b.setItem(item);
        b.setSupplierItem(supplier);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
