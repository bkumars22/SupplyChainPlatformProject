/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.allocationAudit.entity;

import com.scplatform.pcm.parentFunctionalGroup.entity.ParentFunctionalGroup;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ParentFunctionalGroupAuditHistoryTest {

    @Test
    void defaultConstructor() {
        ParentFunctionalGroupAuditHistory h = new ParentFunctionalGroupAuditHistory();
        assertNull(h.getParentFunctionalGroup());
        assertNull(h.getUserId());
    }

    @Test
    void constructorWithAllFields() {
        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ParentFunctionalGroupAuditHistory h = new ParentFunctionalGroupAuditHistory(
                "pfgUser", "ROLE_MGR", "CREATE", "PFG_OP", pfg, "pfg comment", ts);
        assertEquals("pfgUser", h.getUserId());
        assertEquals("ROLE_MGR", h.getUserRole());
        assertEquals("CREATE", h.getActionPerformed());
        assertEquals("PFG_OP", h.getOperationCode());
        assertSame(pfg, h.getParentFunctionalGroup());
        assertEquals("pfg comment", h.getComment());
        assertEquals(ts, h.getDatePerformed());
    }

    @Test
    void setterGetter() {
        ParentFunctionalGroupAuditHistory h = new ParentFunctionalGroupAuditHistory();
        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        h.setParentFunctionalGroup(pfg);
        assertSame(pfg, h.getParentFunctionalGroup());
    }

    @Test
    void isInstanceOfAllocationAuditHistory() {
        ParentFunctionalGroupAuditHistory h = new ParentFunctionalGroupAuditHistory();
        assertInstanceOf(AllocationAuditHistory.class, h);
    }
}
