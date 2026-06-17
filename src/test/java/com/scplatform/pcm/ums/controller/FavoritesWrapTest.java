/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.ums.dto.Favorites;

class FavoritesWrapTest {

    @Test
    void noArgConstructorEmpty() {
        FavoritesWrap w = new FavoritesWrap();
        assertNotNull(w.getFavorites());
        assertTrue(w.getFavorites().isEmpty());
    }

    @Test
    void setFavorites() {
        FavoritesWrap w = new FavoritesWrap();
        w.setFavorites(Arrays.asList(Favorites.builder().id("1").build()));
        assertEquals(1, w.getFavorites().size());
        assertEquals("1", w.getFavorites().get(0).getId());
    }
}
