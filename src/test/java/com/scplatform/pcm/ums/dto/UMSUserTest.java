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

class UMSUserTest {

    @Test
    void defaults() {
        UMSUser u = new UMSUser();
        assertTrue(u.isStatus());
        assertNotNull(u.getAccessControls());
        assertTrue(u.getAccessControls().isEmpty());
    }

    @Test
    void settersWork() {
        UMSUser u = new UMSUser();
        u.setUserName("u");
        u.setFirstName("f");
        u.setLastName("l");
        u.setEmail("e@e");
        u.setStatus(false);
        u.setPreferredLocale("en_US");
        u.setPreferredTimezone("UTC");
        u.setPreferredPagination("25");
        assertEquals("u", u.getUserName());
        assertEquals("f", u.getFirstName());
        assertEquals("l", u.getLastName());
        assertEquals("e@e", u.getEmail());
        assertEquals(false, u.isStatus());
        assertEquals("en_US", u.getPreferredLocale());
        assertEquals("UTC", u.getPreferredTimezone());
        assertEquals("25", u.getPreferredPagination());
    }

    @Test
    void accessControlsAddable() {
        UMSUser u = new UMSUser();
        u.getAccessControls().add(new UMSUserAccessControlIds());
        assertEquals(1, u.getAccessControls().size());
    }

    @Test
    void equalsAndHashCode() {
        UMSUser a = new UMSUser();
        a.setUserName("x");
        UMSUser b = new UMSUser();
        b.setUserName("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
