/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TagRendererExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String msg = "test error message";
        TagRendererException ex = new TagRendererException(msg);
        assertEquals(msg, ex.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        TagRendererException ex = new TagRendererException("err");
        assertTrue(ex instanceof Exception);
    }
}
