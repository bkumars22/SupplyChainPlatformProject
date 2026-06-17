/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UMSExceptionTest {

    @Test
    void noArgConstructor() {
        UMSException e = new UMSException();
        assertNull(e.getMessage());
        assertNull(e.getRootCause());
    }

    @Test
    void messageConstructor() {
        UMSException e = new UMSException("oops");
        assertEquals("oops", e.getMessage());
    }

    @Test
    void throwableConstructorComputesRootAndCause() {
        Throwable root = new RuntimeException("root");
        Throwable mid = new RuntimeException("mid", root);
        Throwable top = new RuntimeException("top", mid);
        UMSException e = new UMSException(top);
        assertSame(root, e.getRootCause());
        assertSame(mid, e.getCause());
        assertTrue(e.getMessage().contains("top"));
    }

    @Test
    void throwableMessageConstructorPrependsMessage() {
        Throwable t = new RuntimeException("inner");
        UMSException e = new UMSException(t, "ctx");
        assertTrue(e.getMessage().startsWith("ctx"));
        assertTrue(e.getMessage().contains("inner"));
    }

    @Test
    void settersWork() {
        UMSException e = new UMSException();
        e.setMessage("m");
        Throwable r = new RuntimeException();
        e.setRootCause(r);
        assertEquals("m", e.getMessage());
        assertSame(r, e.getRootCause());
    }

    @Test
    void stackTraceIsPropagated() {
        Throwable t = new RuntimeException("x");
        UMSException e = new UMSException(t);
        assertNotNull(e.getStackTrace());
    }
}
