/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionPricingTest {

    @Test
    void noArgsConstructor_defaultsAreNull() {
        CostExceptionPricing p = new CostExceptionPricing();
        assertNull(p.getId());
        assertNull(p.getException());
        assertNull(p.getFileName());
        assertNull(p.getCostRecordCount());
        assertNull(p.getUploadedBy());
        assertNull(p.getUploadedOn());
    }

    @Test
    void settersAndGetters_roundTrip() {
        CostExceptionPricing p = new CostExceptionPricing();
        CostException ex = new CostException();
        ex.setExceptionKey(7L);
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        p.setId(50L);
        p.setException(ex);
        p.setFileName("pricing.xlsx");
        p.setCostRecordCount(100L);
        p.setUploadedBy("admin");
        p.setUploadedOn(ts);

        assertEquals(50L, p.getId());
        assertSame(ex, p.getException());
        assertEquals("pricing.xlsx", p.getFileName());
        assertEquals(100L, p.getCostRecordCount());
        assertEquals("admin", p.getUploadedBy());
        assertSame(ts, p.getUploadedOn());
    }

    @Test
    void allArgsConstructor_roundTrip() {
        CostException ex = new CostException();
        Timestamp ts = new Timestamp(0L);
        CostExceptionPricing p = new CostExceptionPricing(11L, ex, "file.xlsx", 200L, "user1", ts);

        assertEquals(11L, p.getId());
        assertSame(ex, p.getException());
        assertEquals("file.xlsx", p.getFileName());
        assertEquals(200L, p.getCostRecordCount());
        assertEquals("user1", p.getUploadedBy());
        assertSame(ts, p.getUploadedOn());
    }

    @Test
    void equalsAndHashCode_symmetric() {
        Timestamp ts = new Timestamp(2000L);
        CostExceptionPricing a = new CostExceptionPricing(1L, null, "f.xlsx", 50L, "u", ts);
        CostExceptionPricing b = new CostExceptionPricing(1L, null, "f.xlsx", 50L, "u", ts);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_excludesException() {
        CostExceptionPricing p = new CostExceptionPricing();
        p.setFileName("pricing.xlsx");
        p.setCostRecordCount(42L);
        String s = p.toString();
        assertNotNull(s);
        assertTrue(s.contains("pricing.xlsx"));
    }
}
