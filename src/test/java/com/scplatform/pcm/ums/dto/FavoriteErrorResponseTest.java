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

class FavoriteErrorResponseTest {

    @Test
    void noArgConstructorEmpty() {
        FavoriteErrorResponse r = new FavoriteErrorResponse();
        assertNotNull(r.getErrors());
        assertTrue(r.getErrors().isEmpty());
    }

    @Test
    void messageConstructorAdds() {
        FavoriteErrorResponse r = new FavoriteErrorResponse("err");
        assertEquals(1, r.getErrors().size());
        assertEquals("err", r.getErrors().get(0));
    }

    @Test
    void addError() {
        FavoriteErrorResponse r = new FavoriteErrorResponse();
        r.addError("a");
        r.addError("b");
        assertEquals(2, r.getErrors().size());
    }

    @Test
    void equalsAndHashCode() {
        FavoriteErrorResponse a = new FavoriteErrorResponse("x");
        FavoriteErrorResponse b = new FavoriteErrorResponse("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
