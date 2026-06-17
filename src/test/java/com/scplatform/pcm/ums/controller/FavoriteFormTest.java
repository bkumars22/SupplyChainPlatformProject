/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FavoriteFormTest {

    @Test
    void defaultsAreNull() {
        FavoriteForm f = new FavoriteForm();
        assertNull(f.getFavName());
        assertNull(f.getUrl());
    }

    @Test
    void settersWork() {
        FavoriteForm f = new FavoriteForm();
        f.setFavName("n");
        f.setUrl("u");
        assertEquals("n", f.getFavName());
        assertEquals("u", f.getUrl());
    }
}
