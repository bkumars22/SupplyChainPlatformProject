/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FavoriteFormTest {

    @Test
    void noArgsDefaults() {
        FavoriteForm f = new FavoriteForm();
        assertNull(f.getFavName());
        assertNull(f.getUrl());
    }

    @Test
    void allArgsConstructor() {
        FavoriteForm f = new FavoriteForm("name", "http://x");
        assertEquals("name", f.getFavName());
        assertEquals("http://x", f.getUrl());
    }

    @Test
    void builder() {
        FavoriteForm f = FavoriteForm.builder().favName("n").url("u").build();
        assertEquals("n", f.getFavName());
        assertEquals("u", f.getUrl());
    }

    @Test
    void settersWork() {
        FavoriteForm f = new FavoriteForm();
        f.setFavName("a");
        f.setUrl("b");
        assertEquals("a", f.getFavName());
        assertEquals("b", f.getUrl());
    }

    @Test
    void equalsAndHashCode() {
        FavoriteForm a = new FavoriteForm("n", "u");
        FavoriteForm b = new FavoriteForm("n", "u");
        FavoriteForm c = new FavoriteForm("x", "u");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotNull(a.toString());
    }
}
