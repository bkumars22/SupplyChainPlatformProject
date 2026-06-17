/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FilterTest {

    @Test
    void defaultsAreNull() {
        Filter f = new Filter();
        assertNull(f.getPlaceholder());
        assertNull(f.getTitle());
        assertNotNull(f.getAdditionalProperties());
    }

    @Test
    void settersWork() {
        Filter f = new Filter();
        f.setPlaceholder("type here");
        f.setTitle("Filter");
        assertEquals("type here", f.getPlaceholder());
        assertEquals("Filter", f.getTitle());
    }

    @Test
    void additionalPropertyStored() {
        Filter f = new Filter();
        f.setAdditionalProperty("a", "b");
        assertEquals("b", f.getAdditionalProperties().get("a"));
    }
}
