/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.allocationAudit.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalGroupAuditHistoryTest {

    @Test
    void defaultConstructor() {
        FunctionalGroupAuditHistory h = new FunctionalGroupAuditHistory();
        assertNull(h.getUserId());
        assertNull(h.getActionPerformed());
        assertNull(h.getComment());
        assertNull(h.getAuditKey());
    }

    @Test
    void isInstanceOfAllocationAuditHistory() {
        FunctionalGroupAuditHistory h = new FunctionalGroupAuditHistory();
        assertInstanceOf(AllocationAuditHistory.class, h);
    }

    @Test
    void settersInheritedFromParent() {
        FunctionalGroupAuditHistory h = new FunctionalGroupAuditHistory();
        h.setUserId("fgUser");
        h.setActionPerformed("APPROVE");
        assertEquals("fgUser", h.getUserId());
        assertEquals("APPROVE", h.getActionPerformed());
    }
}
