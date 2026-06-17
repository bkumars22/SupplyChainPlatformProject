/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class SearchFormExceptionTest {

    @Test
    void noArgConstructor() {
        SearchFormException ex = new SearchFormException();
        assertNull(ex.getMessage());
    }

    @Test
    void messageConstructor() {
        SearchFormException ex = new SearchFormException("msg");
        assertEquals("msg", ex.getMessage());
    }

    @Test
    void causeConstructor() {
        Throwable cause = new RuntimeException("c");
        SearchFormException ex = new SearchFormException(cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void messageAndCauseConstructor() {
        Throwable cause = new RuntimeException("c");
        SearchFormException ex = new SearchFormException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
