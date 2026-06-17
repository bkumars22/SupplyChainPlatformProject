/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.aml.entity;

import com.scplatform.pcm.item.entity.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmlIdTest {

    @Test
    void defaultConstructor() {
        AmlId id = new AmlId();
        assertNull(id.getItem());
        assertNull(id.getMfgItem());
        assertNull(id.getItemKey());
        assertNull(id.getMfgItemKey());
    }

    @Test
    void constructorWithItems() {
        Item item = new Item();
        item.setItemKey(10L);
        Item mfgItem = new Item();
        mfgItem.setItemKey(20L);
        AmlId id = new AmlId(item, mfgItem);
        assertSame(item, id.getItem());
        assertSame(mfgItem, id.getMfgItem());
        assertEquals(10L, id.getItemKey());
        assertEquals(20L, id.getMfgItemKey());
    }

    @Test
    void settersAndGetters() {
        AmlId id = new AmlId();
        Item item = new Item();
        item.setItemKey(5L);
        Item mfgItem = new Item();
        mfgItem.setItemKey(15L);
        id.setItem(item);
        id.setMfgItem(mfgItem);
        assertSame(item, id.getItem());
        assertSame(mfgItem, id.getMfgItem());
        assertEquals(5L, id.getItemKey());
        assertEquals(15L, id.getMfgItemKey());
    }

    @Test
    void getItemKeyNullItem() {
        AmlId id = new AmlId();
        assertNull(id.getItemKey());
        assertNull(id.getMfgItemKey());
    }

    @Test
    void equalsReflexive() {
        Item item = new Item();
        item.setItemKey(1L);
        Item mfg = new Item();
        mfg.setItemKey(2L);
        AmlId id = new AmlId(item, mfg);
        assertEquals(id, id);
    }

    @Test
    void equalsNull() {
        AmlId id = new AmlId();
        assertNotEquals(null, id);
    }

    @Test
    void equalsDifferentType() {
        AmlId id = new AmlId();
        assertNotEquals("string", id);
    }

    @Test
    void equalsSameValues() {
        Item item = new Item();
        item.setItemKey(1L);
        Item mfg = new Item();
        mfg.setItemKey(2L);
        AmlId a = new AmlId(item, mfg);
        AmlId b = new AmlId(item, mfg);
        assertEquals(a, b);
    }

    @Test
    void hashCodeConsistency() {
        Item item = new Item();
        item.setItemKey(1L);
        Item mfg = new Item();
        mfg.setItemKey(2L);
        AmlId a = new AmlId(item, mfg);
        AmlId b = new AmlId(item, mfg);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringContainsKeys() {
        Item item = new Item();
        item.setItemKey(3L);
        Item mfg = new Item();
        mfg.setItemKey(7L);
        AmlId id = new AmlId(item, mfg);
        String s = id.toString();
        assertTrue(s.contains("3"));
        assertTrue(s.contains("7"));
    }
}
