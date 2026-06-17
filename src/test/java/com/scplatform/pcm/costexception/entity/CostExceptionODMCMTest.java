/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionODMCMTest {

    @Test
    void noArgsConstructor_defaultsAreNull() {
        CostExceptionODMCM odm = new CostExceptionODMCM();
        assertNull(odm.getId());
        assertNull(odm.getException());
        assertNull(odm.getApplicableOdmCm());
    }

    @Test
    void settersAndGetters_roundTrip() {
        CostExceptionODMCM odm = new CostExceptionODMCM();
        CostException ex = new CostException();
        ex.setExceptionKey(4L);

        odm.setId(20L);
        odm.setException(ex);
        odm.setApplicableOdmCm("ODM-XYZ");

        assertEquals(20L, odm.getId());
        assertSame(ex, odm.getException());
        assertEquals("ODM-XYZ", odm.getApplicableOdmCm());
    }

    @Test
    void allArgsConstructor_roundTrip() {
        CostException ex = new CostException();
        CostExceptionODMCM odm = new CostExceptionODMCM(8L, ex, "ODM-ABC");

        assertEquals(8L, odm.getId());
        assertSame(ex, odm.getException());
        assertEquals("ODM-ABC", odm.getApplicableOdmCm());
    }

    @Test
    void equalsAndHashCode_symmetric() {
        CostExceptionODMCM a = new CostExceptionODMCM(1L, null, "ODM1");
        CostExceptionODMCM b = new CostExceptionODMCM(1L, null, "ODM1");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_excludesException() {
        CostExceptionODMCM odm = new CostExceptionODMCM();
        odm.setApplicableOdmCm("ODM-123");
        String s = odm.toString();
        assertNotNull(s);
        assertTrue(s.contains("ODM-123"));
    }
}
