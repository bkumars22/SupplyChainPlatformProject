/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.workflow.dto;

import com.scplatform.pcm.ums.dto.Favorites;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class HeaderTest {

    @Test
    void defaultConstructor_initializesNullCollections() {
        Header h = new Header();
        assertNull(h.getMenu());
        assertNull(h.getFavorites());
    }

    @Test
    void allArgsConstructor_setsFields() {
        List<List<Menu>> menu = Collections.singletonList(Collections.singletonList(new Menu("m", "txt")));
        List<Favorites> favs = Collections.emptyList();
        Header h = new Header(menu, favs);
        assertEquals(menu, h.getMenu());
        assertEquals(favs, h.getFavorites());
    }

    @Test
    void setters_work() {
        Header h = new Header();
        h.setMenu(Collections.emptyList());
        h.setFavorites(Collections.emptyList());
        assertNotNull(h.getMenu());
        assertNotNull(h.getFavorites());
    }

    @Test
    void toString_isNotNull() {
        assertNotNull(new Header().toString());
    }
}
