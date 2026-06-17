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

class MenuTest {

    @Test
    void defaultConstructor_initializesAllNull() {
        Menu m = new Menu();
        assertNull(m.getTitle());
        assertNull(m.getText());
        assertNull(m.getName());
        assertNull(m.getChildren());
    }

    @Test
    void nameTextConstructor_setsFields() {
        Menu m = new Menu("n", "t");
        assertEquals("n", m.getName());
        assertEquals("t", m.getText());
    }

    @Test
    void nameTextTitleConstructor_setsFields() {
        Menu m = new Menu("n", "t", "ti");
        assertEquals("n", m.getName());
        assertEquals("t", m.getText());
        assertEquals("ti", m.getTitle());
    }

    @Test
    void allArgsConstructor_setsChildren() {
        List<List<Children>> kids = Collections.singletonList(Collections.singletonList(new Children("a", "b")));
        Menu m = new Menu("ti", "te", "n", kids);
        assertEquals("ti", m.getTitle());
        assertEquals(kids, m.getChildren());
    }

    @Test
    void setters_work() {
        Menu m = new Menu();
        m.setTitle("ti"); m.setText("te"); m.setName("n");
        assertEquals("ti", m.getTitle());
        assertEquals("te", m.getText());
        assertEquals("n", m.getName());
    }

    @Test
    void toString_isNotNull() {
        assertNotNull(new Menu("n", "t").toString());
    }
}
