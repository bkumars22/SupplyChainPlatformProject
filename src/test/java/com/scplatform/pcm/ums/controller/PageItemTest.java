/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PageItemTest {

    @Test
    void defaultsAreNull() {
        PageItem p = new PageItem();
        assertNull(p.getKey());
        assertNull(p.getName());
        assertNull(p.getUrl());
    }

    @Test
    void settersWork() {
        PageItem p = new PageItem();
        p.setKey("k");
        p.setName("n");
        p.setUrl("u");
        assertEquals("k", p.getKey());
        assertEquals("n", p.getName());
        assertEquals("u", p.getUrl());
    }
}
