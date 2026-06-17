/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertQueueExceptionTest {

    @Test
    void messageOnlyConstructor_setsMessage() {
        AlertQueueException ex = new AlertQueueException("boom");
        assertEquals("boom", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void messageAndCauseConstructor_setsBoth() {
        Throwable cause = new IllegalStateException("inner");
        AlertQueueException ex = new AlertQueueException("boom", cause);
        assertEquals("boom", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void causeAndMessageConstructor_setsBoth() {
        Throwable cause = new RuntimeException("inner");
        AlertQueueException ex = new AlertQueueException(cause, "boom");
        assertEquals("boom", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void isCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(AlertQueueException.class));
        assertFalse(RuntimeException.class.isAssignableFrom(AlertQueueException.class));
    }
}
