/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void defaultsAreNullExceptAdditionalProps() {
        User u = new User();
        assertNull(u.getUsername());
        assertNull(u.getName());
        assertNull(u.getRole());
        assertNull(u.getImage());
        assertNull(u.getTitle());
        assertNull(u.getShowRole());
        assertNull(u.getBackToCLP());
        assertNull(u.getAvailableRoles());
        assertNotNull(u.getAdditionalProperties());
    }

    @Test
    void allScalarSettersWork() {
        User u = new User();
        u.setUsername("kswamy");
        u.setName("Kumara");
        u.setRole("admin");
        u.setImage("img.svg");
        u.setTitle("My Profile");
        u.setShowRole(true);
        u.setBackToCLP("javascript:logout()");

        assertEquals("kswamy", u.getUsername());
        assertEquals("Kumara", u.getName());
        assertEquals("admin", u.getRole());
        assertEquals("img.svg", u.getImage());
        assertEquals("My Profile", u.getTitle());
        assertTrue(u.getShowRole());
        assertEquals("javascript:logout()", u.getBackToCLP());
    }

    @Test
    void availableRolesListIsRetained() {
        User u = new User();
        AvailableRole r = new AvailableRole();
        List<AvailableRole> roles = Arrays.asList(r);
        u.setAvailableRoles(roles);
        assertEquals(1, u.getAvailableRoles().size());
        assertSame(r, u.getAvailableRoles().get(0));
    }

    @Test
    void additionalPropertyStored() {
        User u = new User();
        u.setAdditionalProperty("k", "v");
        assertEquals("v", u.getAdditionalProperties().get("k"));
    }
}
