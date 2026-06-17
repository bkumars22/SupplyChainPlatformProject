/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class InvalidAttributeValueExceptionTest {

    @Test
    void noArgConstructor() {
        InvalidAttributeValueException ex = new InvalidAttributeValueException();
        assertNull(ex.getMessage());
        assertNotNull(ex);
    }

    @Test
    void messageConstructor() {
        InvalidAttributeValueException ex = new InvalidAttributeValueException("bad");
        assertEquals("bad", ex.getMessage());
    }

    @Test
    void causeConstructor() {
        Throwable cause = new IllegalArgumentException("root");
        InvalidAttributeValueException ex = new InvalidAttributeValueException(cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void messageAndCauseConstructor() {
        Throwable cause = new RuntimeException("c");
        InvalidAttributeValueException ex = new InvalidAttributeValueException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
