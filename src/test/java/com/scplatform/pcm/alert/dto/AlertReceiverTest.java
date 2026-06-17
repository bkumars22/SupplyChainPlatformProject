/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertReceiverTest {

    @Test
    void noArgsConstructor_yieldsAllNullFields() {
        AlertReceiver r = new AlertReceiver();
        assertNull(r.getUserId());
        assertNull(r.getUserLoginId());
        assertNull(r.getUserName());
        assertNull(r.getEmail());
    }

    @Test
    void allArgsConstructor_setsEveryField() {
        AlertReceiver r = new AlertReceiver(42L, "jdoe", "John Doe", "jdoe@example.com");
        assertEquals(42L, r.getUserId());
        assertEquals("jdoe", r.getUserLoginId());
        assertEquals("John Doe", r.getUserName());
        assertEquals("jdoe@example.com", r.getEmail());
    }

    @Test
    void builder_setsEveryField() {
        AlertReceiver r = AlertReceiver.builder()
                .userId(7L)
                .userLoginId("u")
                .userName("name")
                .email("e@x.com")
                .build();
        assertEquals(7L, r.getUserId());
        assertEquals("u", r.getUserLoginId());
        assertEquals("name", r.getUserName());
        assertEquals("e@x.com", r.getEmail());
    }

    @Test
    void setters_updateFields() {
        AlertReceiver r = new AlertReceiver();
        r.setUserId(1L);
        r.setUserLoginId("a");
        r.setUserName("b");
        r.setEmail("c");
        assertEquals(1L, r.getUserId());
        assertEquals("a", r.getUserLoginId());
        assertEquals("b", r.getUserName());
        assertEquals("c", r.getEmail());
    }

    @Test
    void equalsHashCodeToString_followLombokDataContract() {
        AlertReceiver a = new AlertReceiver(1L, "u", "n", "e");
        AlertReceiver b = new AlertReceiver(1L, "u", "n", "e");
        AlertReceiver c = new AlertReceiver(2L, "u", "n", "e");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotNull(a.toString());
    }
}
