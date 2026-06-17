/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.plaf;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PlafExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String msg = "test error message";
        PlafException ex = new PlafException(msg);
        assertEquals(msg, ex.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        PlafException ex = new PlafException("err");
        assertTrue(ex instanceof Exception);
    }
}
