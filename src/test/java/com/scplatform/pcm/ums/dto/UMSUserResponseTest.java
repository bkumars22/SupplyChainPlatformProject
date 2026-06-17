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

class UMSUserResponseTest {

    @Test
    void defaultsAreEmpty() {
        UMSUserResponse r = new UMSUserResponse();
        assertNotNull(r.getUsers());
        assertTrue(r.getUsers().isEmpty());
    }

    @Test
    void addUser() {
        UMSUserResponse r = new UMSUserResponse();
        r.addUser("u1");
        r.addUser("u2");
        assertEquals(2, r.getUsers().size());
    }

    @Test
    void equalsAndHashCode() {
        UMSUserResponse a = new UMSUserResponse();
        UMSUserResponse b = new UMSUserResponse();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
