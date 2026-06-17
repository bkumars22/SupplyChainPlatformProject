/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void defaultsAreNullExceptAdditionalProps() {
        Item item = new Item();
        assertNull(item.getUrl());
        assertNull(item.getTitle());
        assertNull(item.getApp());
        assertNull(item.getLabel());
        assertNull(item.getName());
        assertNull(item.getChildren());
        assertNotNull(item.getAdditionalProperties());
    }

    @Test
    void scalarSettersWork() {
        Item item = new Item();
        item.setUrl("/u");
        item.setTitle("t");
        item.setApp("a");
        item.setLabel("l");
        item.setName("n");

        assertEquals("/u", item.getUrl());
        assertEquals("t", item.getTitle());
        assertEquals("a", item.getApp());
        assertEquals("l", item.getLabel());
        assertEquals("n", item.getName());
    }

    @Test
    void nestedChildrenAreRetained() {
        Item parent = new Item();
        Item leaf = new Item();
        leaf.setName("leaf");
        List<List<Item>> nested = new ArrayList<>();
        nested.add(Collections.singletonList(leaf));
        parent.setChildren(nested);

        assertEquals(1, parent.getChildren().size());
        assertEquals("leaf", parent.getChildren().get(0).get(0).getName());
    }

    @Test
    void additionalPropertyStored() {
        Item item = new Item();
        item.setAdditionalProperty("x", 9);
        assertEquals(9, item.getAdditionalProperties().get("x"));
    }
}
