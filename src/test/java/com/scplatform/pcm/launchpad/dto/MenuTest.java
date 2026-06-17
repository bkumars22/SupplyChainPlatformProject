/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class MenuTest {

    @Test
    void defaultsAreNullExceptAdditionalProps() {
        Menu menu = new Menu();
        assertNull(menu.getLabel());
        assertNull(menu.getTitle());
        assertNull(menu.getFilter());
        assertNull(menu.getItems());
        assertNotNull(menu.getAdditionalProperties());
    }

    @Test
    void scalarSettersWork() {
        Menu menu = new Menu();
        menu.setLabel("Menu");
        menu.setTitle("Menu");
        assertEquals("Menu", menu.getLabel());
        assertEquals("Menu", menu.getTitle());
    }

    @Test
    void filterAndItemsAreRetained() {
        Menu menu = new Menu();
        Filter f = new Filter();
        Item i = new Item();
        List<Item> items = Arrays.asList(i);
        menu.setFilter(f);
        menu.setItems(items);

        assertSame(f, menu.getFilter());
        assertEquals(1, menu.getItems().size());
        assertSame(i, menu.getItems().get(0));
    }

    @Test
    void additionalPropertyStored() {
        Menu menu = new Menu();
        menu.setAdditionalProperty("a", 1);
        assertEquals(1, menu.getAdditionalProperties().get("a"));
    }
}
