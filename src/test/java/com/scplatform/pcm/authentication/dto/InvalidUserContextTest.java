/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.authentication.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvalidUserContextTest {

    @Test
    void noArgConstructor_hasNullMessageAndCause() {
        InvalidUserContext ex = new InvalidUserContext();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void messageConstructor_setsMessage() {
        InvalidUserContext ex = new InvalidUserContext("NoSession");
        assertEquals("NoSession", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void messageAndCauseConstructor_setsBoth() {
        Throwable cause = new RuntimeException("root");
        InvalidUserContext ex = new InvalidUserContext("wrap", cause);
        assertEquals("wrap", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void causeConstructor_setsCause() {
        Throwable cause = new IllegalStateException("bad");
        InvalidUserContext ex = new InvalidUserContext(cause);
        assertSame(cause, ex.getCause());
        assertNotNull(ex.getMessage()); // Throwable derives a message from cause
    }

    @Test
    void isCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(InvalidUserContext.class));
        assertTrue(!RuntimeException.class.isAssignableFrom(InvalidUserContext.class));
    }
}
