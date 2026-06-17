/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import com.scplatform.pcm.cost.entity.PcmCostType;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionTest {

    @Test
    void noArgsConstructor_defaultsAreNull() {
        CostException e = new CostException();
        assertNull(e.getExceptionKey());
        assertNull(e.getExceptionId());
        assertNull(e.getExceptionName());
        assertNull(e.getState());
        assertNull(e.getCostType());
        assertEquals(Boolean.FALSE, e.getPartiallyApprovedState());
    }

    @Test
    void settersAndGetters_roundTrip() {
        CostException e = new CostException();
        e.setExceptionKey(1L);
        e.setExceptionId("EX-001");
        e.setExceptionName("Test Exception");
        e.setExceptionRequestor("user1");
        e.setExceptionOwner("owner1");
        e.setExceptionApprover("approver1");
        e.setRequestType("BACKDATE");
        e.setCommodity("CHIPS");
        e.setSubtier(true);
        e.setPlatformName("Platform A");
        e.setOdmAcknowledgement("ACK");
        e.setPreviousState("DRAFT");
        e.setState("APPROVED");
        e.setUploadType("PDF");
        e.setCreatedBy("admin");
        e.setApprovedBy("approver1");
        e.setRejectedBy("none");
        e.setClosedBy("admin");
        e.setLastChangedBy("admin");
        e.setReRequestedBy("user1");

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        e.setCreatedOn(ts);
        e.setApprovedOn(ts);
        e.setRejectedOn(ts);
        e.setClosedOn(ts);
        e.setRequestedOn(ts);
        e.setLastChangedOn(ts);
        e.setReRequestedOn(ts);

        PcmCostType ct = new PcmCostType();
        ct.setCostTypeKey("BUY");
        e.setCostType(ct);

        assertEquals(1L, e.getExceptionKey());
        assertEquals("EX-001", e.getExceptionId());
        assertEquals("Test Exception", e.getExceptionName());
        assertEquals("user1", e.getExceptionRequestor());
        assertEquals("owner1", e.getExceptionOwner());
        assertEquals("approver1", e.getExceptionApprover());
        assertEquals("BACKDATE", e.getRequestType());
        assertEquals("CHIPS", e.getCommodity());
        assertTrue(e.getSubtier());
        assertEquals("Platform A", e.getPlatformName());
        assertEquals("ACK", e.getOdmAcknowledgement());
        assertEquals("DRAFT", e.getPreviousState());
        assertEquals("APPROVED", e.getState());
        assertEquals("PDF", e.getUploadType());
        assertSame(ct, e.getCostType());
        assertSame(ts, e.getCreatedOn());
        assertEquals("user1", e.getReRequestedBy());
        assertSame(ts, e.getReRequestedOn());
    }

    @Test
    void collections_setAndGet() {
        CostException e = new CostException();
        Set<CostExceptionODMCM> odmCm = new HashSet<>();
        Set<CostExceptionLOB> lob = new HashSet<>();
        Set<CostExceptionInfo> info = new HashSet<>();
        Set<CostExceptionODMEmail> emails = new HashSet<>();
        Set<CostExceptionApprover> approvers = new HashSet<>();

        e.setCostExceptionOdmCm(odmCm);
        e.setCostExceptionLOB(lob);
        e.setCostExceptionInfo(info);
        e.setCostExceptionODMEmail(emails);
        e.setExceptionApproval(approvers);

        assertSame(odmCm, e.getCostExceptionOdmCm());
        assertSame(lob, e.getCostExceptionLOB());
        // setCostExceptionInfo copies elements into an internal collection,
        // so the returned reference is NOT the same as the input
        assertEquals(info, e.getCostExceptionInfo());
        assertSame(emails, e.getCostExceptionODMEmail());
        assertSame(approvers, e.getExceptionApproval());
    }

    @Test
    void partiallyApprovedState_mutableTransient() {
        CostException e = new CostException();
        assertEquals(Boolean.FALSE, e.getPartiallyApprovedState());
        e.setPartiallyApprovedState(Boolean.TRUE);
        assertEquals(Boolean.TRUE, e.getPartiallyApprovedState());
    }

    @Test
    void equalsAndHashCode_basedOnFields() {
        CostException a = new CostException();
        a.setExceptionKey(1L);
        a.setExceptionId("EX-001");

        CostException b = new CostException();
        b.setExceptionKey(1L);
        b.setExceptionId("EX-001");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentKeys_notEqual() {
        CostException a = new CostException();
        a.setExceptionKey(1L);

        CostException b = new CostException();
        b.setExceptionKey(2L);

        assertNotEquals(a, b);
    }

    @Test
    void toString_excludesCollections() {
        CostException e = new CostException();
        e.setExceptionId("EX-123");
        e.setState("DRAFT");

        String s = e.toString();
        assertNotNull(s);
        assertTrue(s.contains("EX-123"));
        assertTrue(s.contains("DRAFT"));
    }
}
