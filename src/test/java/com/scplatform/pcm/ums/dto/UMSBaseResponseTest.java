/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UMSBaseResponseTest {

    @Test
    void defaultsAndSetter() {
        UMSBaseResponse r = new UMSBaseResponse();
        assertEquals(0, r.getTotalSize());
        r.setTotalSize(5);
        assertEquals(5, r.getTotalSize());
    }

    @Test
    void equalsAndHashCode() {
        UMSBaseResponse a = new UMSBaseResponse();
        UMSBaseResponse b = new UMSBaseResponse();
        a.setTotalSize(3);
        b.setTotalSize(3);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        b.setTotalSize(4);
        assertNotEquals(a, b);
        assertNotNull(a.toString());
    }
}
