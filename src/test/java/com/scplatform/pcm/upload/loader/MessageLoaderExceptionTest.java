/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MessageLoaderExceptionTest {

    @Test
    void noArgConstructor() {
        MessageLoaderException ex = new MessageLoaderException();
        assertNull(ex.getMessage());
        assertFalse(ex.isSoft());
    }

    @Test
    void messageOnlyConstructor() {
        MessageLoaderException ex = new MessageLoaderException("boom");
        assertEquals("boom", ex.getMessage());
    }

    @Test
    void messageWithSingleDetailFormatsViaMessageFormat() {
        MessageLoaderException ex = new MessageLoaderException("Hello {0}", "world");
        assertEquals("Hello world", ex.getMessage());
    }

    @Test
    void messageWithDetailsArrayFormats() {
        MessageLoaderException ex = new MessageLoaderException("a={0} b={1}", new Object[]{1, "x"});
        assertEquals("a=1 b=x", ex.getMessage());
    }

    @Test
    void causeOnlyConstructor() {
        Throwable cause = new RuntimeException("root");
        MessageLoaderException ex = new MessageLoaderException(cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void messageAndCauseConstructor() {
        Throwable cause = new RuntimeException("root");
        MessageLoaderException ex = new MessageLoaderException("wrap", cause);
        assertEquals("wrap", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void messageDetailAndCauseConstructor() {
        Throwable cause = new RuntimeException("root");
        MessageLoaderException ex = new MessageLoaderException("hi {0}", "you", cause);
        assertEquals("hi you", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void messageDetailsArrayAndCauseConstructor() {
        Throwable cause = new RuntimeException("root");
        MessageLoaderException ex = new MessageLoaderException("a={0}", new Object[]{1}, cause);
        assertEquals("a=1", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void softFlagToggle() {
        MessageLoaderException ex = new MessageLoaderException("x");
        assertFalse(ex.isSoft());
        ex.setSoft(true);
        assertTrue(ex.isSoft());
    }
}
