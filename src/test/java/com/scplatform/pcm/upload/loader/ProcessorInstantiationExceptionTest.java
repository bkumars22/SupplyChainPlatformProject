/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProcessorInstantiationExceptionTest {

    @Test
    void messageOnlyConstructor() {
        ProcessorInstantiationException ex = new ProcessorInstantiationException("oops");
        assertEquals("oops", ex.getMessage());
        assertNotNull(ex);
    }

    @Test
    void messageAndCauseConstructor() {
        Throwable cause = new IllegalStateException("root");
        ProcessorInstantiationException ex = new ProcessorInstantiationException("wrap", cause);
        assertEquals("wrap", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void isCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(ProcessorInstantiationException.class));
        assertTrue(!RuntimeException.class.isAssignableFrom(ProcessorInstantiationException.class));
    }
}
