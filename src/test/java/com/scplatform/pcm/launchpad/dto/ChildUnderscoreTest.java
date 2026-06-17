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

class ChildUnderscoreTest {

    @Test
    void defaultsAreNull() {
        Child_ c = new Child_();
        assertNull(c.getUrl());
        assertNull(c.getTitle());
        assertNull(c.getApp());
        assertNull(c.getLabel());
        assertNull(c.getName());
        assertNotNull(c.getAdditionalProperties());
    }

    @Test
    void settersAndGettersWork() {
        Child_ c = new Child_();
        c.setUrl("/u");
        c.setTitle("t");
        c.setApp("a");
        c.setLabel("l");
        c.setName("n");

        assertEquals("/u", c.getUrl());
        assertEquals("t", c.getTitle());
        assertEquals("a", c.getApp());
        assertEquals("l", c.getLabel());
        assertEquals("n", c.getName());
    }

    @Test
    void additionalPropertyStoredCorrectly() {
        Child_ c = new Child_();
        c.setAdditionalProperty("extra", 1);
        assertEquals(1, c.getAdditionalProperties().get("extra"));
    }
}
