/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionInfoTest {

    @Test
    void noArgsConstructor_defaultsAreNull() {
        CostExceptionInfo i = new CostExceptionInfo();
        assertNull(i.getId());
        assertNull(i.getException());
        assertNull(i.getState());
        assertNull(i.getStateChangeBy());
        assertNull(i.getStateChangeOn());
        assertNull(i.getComment());
    }

    @Test
    void settersAndGetters_roundTrip() {
        CostExceptionInfo i = new CostExceptionInfo();
        CostException ex = new CostException();
        ex.setExceptionKey(5L);
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        i.setId(2L);
        i.setException(ex);
        i.setState("APPROVED");
        i.setStateChangeBy("admin");
        i.setStateChangeOn(ts);
        i.setComment("Looks good");

        assertEquals(2L, i.getId());
        assertSame(ex, i.getException());
        assertEquals("APPROVED", i.getState());
        assertEquals("admin", i.getStateChangeBy());
        assertSame(ts, i.getStateChangeOn());
        assertEquals("Looks good", i.getComment());
    }

    @Test
    void allArgsConstructor_roundTrip() {
        CostException ex = new CostException();
        Timestamp ts = new Timestamp(0L);
        CostExceptionInfo i = new CostExceptionInfo(3L, ex, "DRAFT", "user1", ts, "Initial comment");

        assertEquals(3L, i.getId());
        assertSame(ex, i.getException());
        assertEquals("DRAFT", i.getState());
        assertEquals("user1", i.getStateChangeBy());
        assertSame(ts, i.getStateChangeOn());
        assertEquals("Initial comment", i.getComment());
    }

    @Test
    void equalsAndHashCode_symmetric() {
        Timestamp ts = new Timestamp(500L);
        CostExceptionInfo a = new CostExceptionInfo(1L, null, "S1", "u1", ts, "c");
        CostExceptionInfo b = new CostExceptionInfo(1L, null, "S1", "u1", ts, "c");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_excludesException() {
        CostExceptionInfo i = new CostExceptionInfo();
        i.setState("REJECTED");
        i.setComment("Not approved");
        String s = i.toString();
        assertNotNull(s);
        assertTrue(s.contains("REJECTED"));
        assertTrue(s.contains("Not approved"));
    }
}
