/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionExceptionsTest {

    // ---- CostRecordAuditException ----

    @Test
    void auditException_storesMessage() {
        CostRecordAuditException e = new CostRecordAuditException("audit error");
        assertEquals("audit error", e.getMessage());
    }

    @Test
    void auditException_isCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(CostRecordAuditException.class));
    }

    @Test
    void auditException_thrownAndCaught() {
        assertThrows(CostRecordAuditException.class, () -> {
            throw new CostRecordAuditException("thrown");
        });
    }

    @Test
    void auditException_serialVersionUID_exists() throws Exception {
        java.lang.reflect.Field f = CostRecordAuditException.class.getDeclaredField("serialVersionUID");
        f.setAccessible(true);
        long uid = (long) f.get(null);
        assertEquals(1L, uid);
    }

    // ---- CostRecordHandleException ----

    @Test
    void handleException_storesMessage() {
        CostRecordHandleException e = new CostRecordHandleException("handle error");
        assertEquals("handle error", e.getMessage());
    }

    @Test
    void handleException_isCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(CostRecordHandleException.class));
    }

    @Test
    void handleException_thrownAndCaught() {
        assertThrows(CostRecordHandleException.class, () -> {
            throw new CostRecordHandleException("thrown");
        });
    }

    @Test
    void handleException_serialVersionUID_exists() throws Exception {
        java.lang.reflect.Field f = CostRecordHandleException.class.getDeclaredField("serialVersionUID");
        f.setAccessible(true);
        long uid = (long) f.get(null);
        assertEquals(1L, uid);
    }

    @Test
    void bothExceptions_distinctTypes() {
        CostRecordAuditException audit = new CostRecordAuditException("a");
        CostRecordHandleException handle = new CostRecordHandleException("h");
        assertFalse(audit.getClass().equals(handle.getClass()));
    }
}
