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

class ItemErrorResponseTest {

    @Test
    void noArgConstructorEmpty() {
        ItemErrorResponse r = new ItemErrorResponse();
        assertNotNull(r.getErrors());
        assertTrue(r.getErrors().isEmpty());
    }

    @Test
    void messageConstructorAdds() {
        ItemErrorResponse r = new ItemErrorResponse("oops");
        assertEquals(1, r.getErrors().size());
    }

    @Test
    void addError() {
        ItemErrorResponse r = new ItemErrorResponse();
        r.addError("a");
        r.addError("b");
        assertEquals(2, r.getErrors().size());
    }

    @Test
    void equalsAndHashCode() {
        ItemErrorResponse a = new ItemErrorResponse("x");
        ItemErrorResponse b = new ItemErrorResponse("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
