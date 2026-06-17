/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ItemWrapTest {

    @Test
    void testGetSetItem() {
        ItemWrap wrap = new ItemWrap();
        assertNull(wrap.getItem());

        Map<String, Object> item = new HashMap<>();
        item.put("key", "value");

        wrap.setItem(item);
        Map<String, Object> out = wrap.getItem();
        assertSame(item, out);
        assertEquals("value", out.get("key"));
    }

    @Test
    void testEqualsHashCodeToString() {
        ItemWrap a = new ItemWrap();
        ItemWrap b = new ItemWrap();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());

        Map<String, Object> item = new HashMap<>();
        item.put("x", 1);
        a.setItem(item);
        assertNotEquals(a, b);
    }
}
