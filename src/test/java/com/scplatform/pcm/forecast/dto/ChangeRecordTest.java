/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChangeRecordTest {

    @Test
    void testDefaultConstructor() {
        ChangeRecord cr = new ChangeRecord();
        assertNull(cr.getRecordId());
        assertNull(cr.getRecordOperation());
        assertTrue(cr.getChanges().isEmpty());
    }

    @Test
    void testSetAndGetRecordId() {
        ChangeRecord cr = new ChangeRecord();
        cr.setRecordId("REC-001");
        assertEquals("REC-001", cr.getRecordId());
    }

    @Test
    void testSetAndGetRecordOperation() {
        ChangeRecord cr = new ChangeRecord();
        cr.setRecordOperation("UPDATE");
        assertEquals("UPDATE", cr.getRecordOperation());
    }

    @Test
    void testGetChanges_ReturnsModifiableMap() {
        ChangeRecord cr = new ChangeRecord();
        Map<String, String> changes = cr.getChanges();
        assertNotNull(changes);
        changes.put("field", "value");
        assertEquals(1, cr.getChanges().size());
    }
}
