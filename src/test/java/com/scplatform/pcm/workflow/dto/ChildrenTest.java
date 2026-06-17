/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.workflow.dto;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChildrenTest {

    @Test
    void defaultConstructor_initializesAllNull() {
        Children c = new Children();
        assertNull(c.getTitle());
        assertNull(c.getText());
        assertNull(c.getName());
        assertNull(c.getUrl());
        assertNull(c.getChildren());
    }

    @Test
    void nameTextConstructor_setsFields() {
        Children c = new Children("n", "t");
        assertEquals("n", c.getName());
        assertEquals("t", c.getText());
        assertNull(c.getTitle());
        assertNull(c.getUrl());
    }

    @Test
    void nameTextUrlConstructor_setsFields() {
        Children c = new Children("n", "t", "u");
        assertEquals("n", c.getName());
        assertEquals("t", c.getText());
        assertEquals("u", c.getUrl());
    }

    @Test
    void titleTextNameUrlConstructor_setsFields() {
        Children c = new Children("ti", "te", "n", "u");
        assertEquals("ti", c.getTitle());
        assertEquals("te", c.getText());
        assertEquals("n", c.getName());
        assertEquals("u", c.getUrl());
    }

    @Test
    void allArgsConstructor_setsChildren() {
        List<List<Children>> kids = Collections.singletonList(Collections.singletonList(new Children("a", "b")));
        Children c = new Children("ti", "te", "n", "u", kids);
        assertEquals(kids, c.getChildren());
    }

    @Test
    void setters_work() {
        Children c = new Children();
        c.setTitle("ti"); c.setText("te"); c.setName("n"); c.setUrl("u");
        assertEquals("ti", c.getTitle());
        assertEquals("te", c.getText());
        assertEquals("n", c.getName());
        assertEquals("u", c.getUrl());
    }

    @Test
    void toString_includesFields() {
        Children c = new Children("ti", "te", "n", "u");
        String s = c.toString();
        assertNotNull(s);
        assertEquals(true, s.contains("title = ti"));
        assertEquals(true, s.contains("name = n"));
    }
}
