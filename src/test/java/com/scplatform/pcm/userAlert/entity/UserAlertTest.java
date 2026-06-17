/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.userAlert.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserAlertTest {

    @Test
    void defaultConstructor_setsAlertKeyToNegativeOne() {
        UserAlert a = new UserAlert();
        assertEquals(-1L, a.getAlertKey());
        assertNull(a.getAlertDate());
        assertNull(a.getAlertTitle());
        assertNull(a.getAlertURL());
        assertNull(a.getAlertTarget());
        assertNull(a.getAlertFilter());
    }

    @Test
    void settersAndGetters() {
        UserAlert a = new UserAlert();
        Date d = new Date(123_000L);
        a.setAlertKey(99L);
        a.setAlertDate(d);
        a.setAlertTitle("title");
        a.setAlertURL("http://example.com");
        a.setAlertTarget("_blank");
        a.setAlertFilter("FILT");

        assertEquals(99L, a.getAlertKey());
        assertSame(d, a.getAlertDate());
        assertEquals("title", a.getAlertTitle());
        assertEquals("http://example.com", a.getAlertURL());
        assertEquals("_blank", a.getAlertTarget());
        assertEquals("FILT", a.getAlertFilter());
    }

    @Test
    void isSerializable() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(UserAlert.class));
    }
}
