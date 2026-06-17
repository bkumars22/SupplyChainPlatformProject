/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionLOBTest {

    @Test
    void noArgsConstructor_defaultsAreNull() {
        CostExceptionLOB lob = new CostExceptionLOB();
        assertNull(lob.getId());
        assertNull(lob.getException());
        assertNull(lob.getLineOfBusiness());
    }

    @Test
    void settersAndGetters_roundTrip() {
        CostExceptionLOB lob = new CostExceptionLOB();
        CostException ex = new CostException();
        ex.setExceptionKey(3L);

        lob.setId(10L);
        lob.setException(ex);
        lob.setLineOfBusiness("COMPUTE");

        assertEquals(10L, lob.getId());
        assertSame(ex, lob.getException());
        assertEquals("COMPUTE", lob.getLineOfBusiness());
    }

    @Test
    void allArgsConstructor_roundTrip() {
        CostException ex = new CostException();
        CostExceptionLOB lob = new CostExceptionLOB(7L, ex, "STORAGE");

        assertEquals(7L, lob.getId());
        assertSame(ex, lob.getException());
        assertEquals("STORAGE", lob.getLineOfBusiness());
    }

    @Test
    void equalsAndHashCode_symmetric() {
        CostExceptionLOB a = new CostExceptionLOB(1L, null, "NET");
        CostExceptionLOB b = new CostExceptionLOB(1L, null, "NET");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_excludesException() {
        CostExceptionLOB lob = new CostExceptionLOB();
        lob.setLineOfBusiness("COMPUTE");
        String s = lob.toString();
        assertNotNull(s);
        assertTrue(s.contains("COMPUTE"));
    }
}
