/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class UMSPostResponseTest {

    @Test
    void noArgConstructorDefaults() {
        UMSPostResponse r = new UMSPostResponse();
        assertNull(r.getMessage());
        assertEquals(0, r.getStatus());
    }

    @Test
    void allArgsConstructor() {
        UMSPostResponse r = new UMSPostResponse("ok", 200);
        assertEquals("ok", r.getMessage());
        assertEquals(200, r.getStatus());
    }

    @Test
    void settersWork() {
        UMSPostResponse r = new UMSPostResponse();
        r.setMessage("err");
        r.setStatus(500);
        assertEquals("err", r.getMessage());
        assertEquals(500, r.getStatus());
    }

    @Test
    void equalsAndHashCode() {
        UMSPostResponse a = new UMSPostResponse("x", 1);
        UMSPostResponse b = new UMSPostResponse("x", 1);
        UMSPostResponse c = new UMSPostResponse("y", 2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotNull(a.toString());
    }
}
