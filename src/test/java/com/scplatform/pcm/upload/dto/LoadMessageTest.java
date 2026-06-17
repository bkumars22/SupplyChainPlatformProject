/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LoadMessageTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        LoadMessage m = new LoadMessage();
        assertNull(m.getLine());
        assertNull(m.getMessage());
        assertNull(m.getType());
    }

    @Test
    void allArgConstructorAssignsFields() {
        LoadMessage m = new LoadMessage("L1", "msg", "ERROR");
        assertEquals("L1", m.getLine());
        assertEquals("msg", m.getMessage());
        assertEquals("ERROR", m.getType());
    }

    @Test
    void settersWork() {
        LoadMessage m = new LoadMessage();
        m.setLine("X");
        m.setMessage("Y");
        m.setType("WARN");
        assertEquals("X", m.getLine());
        assertEquals("Y", m.getMessage());
        assertEquals("WARN", m.getType());
    }

    @Test
    void equalsAndHashCodeWork() {
        LoadMessage a = new LoadMessage("a", "b", "ERROR");
        LoadMessage b = new LoadMessage("a", "b", "ERROR");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
