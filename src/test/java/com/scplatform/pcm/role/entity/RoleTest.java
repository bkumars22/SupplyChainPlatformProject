/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.role.entity;

import com.scplatform.pcm.accessControl.entity.AccessControl;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void defaultConstructor() {
        Role r = new Role();
        assertNull(r.getRoleKey());
        assertNull(r.getRoleId());
        assertNull(r.getRoleName());
        assertFalse(r.getPermRole());
        assertNotNull(r.getAcls());
        assertNotNull(r.getUsers());
        assertNotNull(r.getPreferences());
        assertNotNull(r.getRoleProfileMapping());
    }

    @Test
    void constructorWithKey() {
        Role r = new Role(42L);
        assertEquals(42L, r.getRoleKey());
    }

    @Test
    void settersAndGetters() {
        Role r = new Role();
        r.setRoleKey(1L);
        r.setRoleId("ROLE_BUYER");
        r.setRoleName("Buyer");
        r.setPermRole(true);

        assertEquals(1L, r.getRoleKey());
        assertEquals("ROLE_BUYER", r.getRoleId());
        assertEquals("Buyer", r.getRoleName());
        assertTrue(r.getPermRole());
    }

    @Test
    void preferencesSetterGetter() {
        Role r = new Role();
        r.setPreference("key1", "val1");
        r.setPreference("key2", true);
        assertEquals("val1", r.getPreference("key1"));
        assertTrue(r.getPreferenceAsBoolean("key2"));
        assertTrue(r.getPreferenceKeys().contains("key1"));
        assertTrue(r.getPreferenceKeys().contains("key2"));
    }

    @Test
    void preferencesAsBoolean() {
        Role r = new Role();
        r.setPreference("flag", false);
        assertFalse(r.getPreferenceAsBoolean("flag"));
    }

    @Test
    void aclsSetterGetter() {
        Role r = new Role();
        Set<AccessControl> acls = new HashSet<>();
        AccessControl acl = new AccessControl();
        acls.add(acl);
        r.setAcls(acls);
        assertEquals(1, r.getAcls().size());
    }

    @Test
    void equalsReflexive() {
        Role r = new Role(1L);
        assertEquals(r, r);
    }

    @Test
    void equalsNull() {
        Role r = new Role(1L);
        assertNotEquals(null, r);
    }

    @Test
    void equalsDifferentType() {
        Role r = new Role(1L);
        assertNotEquals("string", r);
    }

    @Test
    void equalsSameKey() {
        Role a = new Role(5L);
        Role b = new Role(5L);
        assertEquals(a, b);
    }

    @Test
    void notEqualsDifferentKey() {
        Role a = new Role(1L);
        Role b = new Role(2L);
        assertNotEquals(a, b);
    }

    @Test
    void hashCodeConsistency() {
        Role a = new Role(5L);
        Role b = new Role(5L);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringContainsRoleId() {
        Role r = new Role();
        r.setRoleId("ROLE_ADMIN");
        r.setRoleName("Admin");
        String s = r.toString();
        assertTrue(s.contains("ROLE_ADMIN"));
    }

    @Test
    void useInAssignmentPrefConstant() {
        assertEquals("ASSIGNMENT", Role.USE_IN_ASSIGNMENT_PREF);
    }
}
