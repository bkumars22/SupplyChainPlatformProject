/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class BreadCrumbTest {

    @Test
    void noArgConstructorEmpty() {
        BreadCrumb b = new BreadCrumb();
        assertNotNull(b.getPageItems());
        assertTrue(b.getPageItems().isEmpty());
    }

    @Test
    void setPageItems() {
        BreadCrumb b = new BreadCrumb();
        PageItem item = new PageItem();
        item.setKey("k");
        b.setPageItems(Arrays.asList(item));
        assertEquals(1, b.getPageItems().size());
        assertEquals("k", b.getPageItems().get(0).getKey());
    }

    @Test
    void setNullClears() {
        BreadCrumb b = new BreadCrumb();
        b.setPageItems(null);
        assertNull(b.getPageItems());
    }

    @Test
    void setEmptyList() {
        BreadCrumb b = new BreadCrumb();
        b.setPageItems(Collections.emptyList());
        assertTrue(b.getPageItems().isEmpty());
    }
}
