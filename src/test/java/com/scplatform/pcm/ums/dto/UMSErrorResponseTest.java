/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UMSErrorResponseTest {

    @Test
    void noArgConstructorEmpty() {
        UMSErrorResponse r = new UMSErrorResponse();
        assertNotNull(r.getErrors());
        assertTrue(r.getErrors().isEmpty());
    }

    @Test
    void messageConstructorAdds() {
        UMSErrorResponse r = new UMSErrorResponse("oops");
        assertEquals(1, r.getErrors().size());
        assertEquals("oops", r.getErrors().get(0));
    }

    @Test
    void addError() {
        UMSErrorResponse r = new UMSErrorResponse();
        r.addError("a");
        r.addError("b");
        assertEquals(2, r.getErrors().size());
    }

    @Test
    void equalsAndHashCode() {
        UMSErrorResponse a = new UMSErrorResponse("x");
        UMSErrorResponse b = new UMSErrorResponse("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
