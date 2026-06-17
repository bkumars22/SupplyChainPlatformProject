/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class GenericResultSetTest {

    @Test
    void noArgConstructorEmptyRowsNullNames() {
        GenericResultSet rs = new GenericResultSet();
        assertNotNull(rs.getValues());
        assertTrue(rs.getValues().isEmpty());
        assertNull(rs.getColumnNames());
    }

    @Test
    void arrayConstructorAssignsColumnNames() {
        GenericResultSet rs = new GenericResultSet(new String[]{"a", "b", "c"});
        assertEquals(Arrays.asList("a", "b", "c"), rs.getColumnNames());
    }

    @Test
    void getColumnIndexReturnsPosition() {
        GenericResultSet rs = new GenericResultSet(new String[]{"a", "b", "c"});
        assertEquals(0, rs.getColumnIndex("a"));
        assertEquals(2, rs.getColumnIndex("c"));
        assertEquals(-1, rs.getColumnIndex("missing"));
    }

    @Test
    void addAppendsRow() {
        GenericResultSet rs = new GenericResultSet(new String[]{"x"});
        GenericResultRow r = new GenericResultRow("v");
        rs.add(r);
        assertEquals(1, rs.getValues().size());
        assertEquals(r, rs.getValues().get(0));
    }

    @Test
    void clearEmptiesRowsAndNames() {
        GenericResultSet rs = new GenericResultSet(new String[]{"x"});
        rs.add(new GenericResultRow("v"));
        rs.clear();
        assertTrue(rs.getValues().isEmpty());
        assertTrue(rs.getColumnNames().isEmpty());
    }
}
