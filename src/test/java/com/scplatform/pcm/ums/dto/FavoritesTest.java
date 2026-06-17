/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class FavoritesTest {

    @Test
    void noArgsDefaults() {
        Favorites f = new Favorites();
        assertNull(f.getId());
        assertFalse(f.isHome());
        assertFalse(f.isHide());
        assertFalse(f.isExternal());
    }

    @Test
    void builderAssignsAllFields() {
        Favorites f = Favorites.builder()
                .id("1").title("t").text("x").isHome(true).url("u").app("a")
                .hide(true).roleName("r").isExternal(true).build();
        assertEquals("1", f.getId());
        assertEquals("t", f.getTitle());
        assertEquals("x", f.getText());
        assertTrue(f.isHome());
        assertEquals("u", f.getUrl());
        assertEquals("a", f.getApp());
        assertTrue(f.isHide());
        assertEquals("r", f.getRoleName());
        assertTrue(f.isExternal());
    }

    @Test
    void settersAndItems() {
        Favorites f = new Favorites();
        Favorites child = Favorites.builder().id("c").build();
        f.setItems(Arrays.asList(child));
        List<Favorites> items = f.getItems();
        assertEquals(1, items.size());
        assertEquals("c", items.get(0).getId());
    }

    @Test
    void allArgsConstructor() {
        Favorites f = new Favorites("1", "t", "x", true, "u", "a", false, "r", true, null);
        assertEquals("1", f.getId());
        assertEquals("t", f.getTitle());
    }

    @Test
    void equalsAndCustomToString() {
        Favorites a = Favorites.builder().id("1").title("t").build();
        Favorites b = Favorites.builder().id("1").title("t").build();
        Favorites c = Favorites.builder().id("2").title("t").build();
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotNull(a.toString());
        assertTrue(a.toString().contains("id='1'"));
    }
}
