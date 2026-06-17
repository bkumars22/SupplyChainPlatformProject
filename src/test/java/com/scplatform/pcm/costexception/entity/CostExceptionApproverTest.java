/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionApproverTest {

    @Test
    void noArgsConstructor_defaultsAreNull() {
        CostExceptionApprover a = new CostExceptionApprover();
        assertNull(a.getId());
        assertNull(a.getException());
        assertNull(a.getApproverRole());
        assertNull(a.getApprover());
        assertNull(a.getState());
        assertNull(a.getProxyApprover());
        assertNull(a.getProxyApproverRole());
        assertNull(a.getActionDate());
        assertNull(a.getActualUser());
    }

    @Test
    void settersAndGetters_roundTrip() {
        CostExceptionApprover a = new CostExceptionApprover();
        CostException ex = new CostException();
        ex.setExceptionKey(10L);

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        a.setId(1L);
        a.setException(ex);
        a.setApproverRole("MANAGER");
        a.setApprover("approver1");
        a.setState("APPROVED");
        a.setProxyApprover("proxy1");
        a.setProxyApproverRole("PROXY_MANAGER");
        a.setActionDate(ts);
        a.setActualUser("actual1");

        assertEquals(1L, a.getId());
        assertSame(ex, a.getException());
        assertEquals("MANAGER", a.getApproverRole());
        assertEquals("approver1", a.getApprover());
        assertEquals("APPROVED", a.getState());
        assertEquals("proxy1", a.getProxyApprover());
        assertEquals("PROXY_MANAGER", a.getProxyApproverRole());
        assertSame(ts, a.getActionDate());
        assertEquals("actual1", a.getActualUser());
    }

    @Test
    void allArgsConstructor_roundTrip() {
        CostException ex = new CostException();
        Timestamp ts = new Timestamp(0L);
        CostExceptionApprover a = new CostExceptionApprover(
                5L, ex, "ROLE1", "user1", "PENDING",
                "proxy1", "PROXY_ROLE", ts, "actual1");

        assertEquals(5L, a.getId());
        assertSame(ex, a.getException());
        assertEquals("ROLE1", a.getApproverRole());
        assertEquals("user1", a.getApprover());
        assertEquals("PENDING", a.getState());
        assertEquals("proxy1", a.getProxyApprover());
        assertEquals("PROXY_ROLE", a.getProxyApproverRole());
        assertSame(ts, a.getActionDate());
        assertEquals("actual1", a.getActualUser());
    }

    @Test
    void equalsAndHashCode_basedOnFields() {
        Timestamp ts = new Timestamp(1000L);
        CostExceptionApprover a1 = new CostExceptionApprover(1L, null, "R", "u", "S", null, null, ts, "act");
        CostExceptionApprover a2 = new CostExceptionApprover(1L, null, "R", "u", "S", null, null, ts, "act");
        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void toString_excludesException() {
        CostExceptionApprover a = new CostExceptionApprover();
        a.setApproverRole("ADMIN");
        String s = a.toString();
        assertNotNull(s);
        assertTrue(s.contains("ADMIN"));
    }
}
