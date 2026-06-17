/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.accessControl.entity;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.Users;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessControlTest {

    @Test
    void defaultConstructor() {
        AccessControl ac = new AccessControl();
        assertNull(ac.getAccessControlKey());
        assertNull(ac.getAcl());
        assertNull(ac.getEntityType());
        assertNull(ac.getEntityKey());
        assertNull(ac.getRole());
        assertNull(ac.getUser());
    }

    @Test
    void constructorWithKey() {
        AccessControl ac = new AccessControl(42L);
        assertEquals(42L, ac.getAccessControlKey());
    }

    @Test
    void settersAndGetters() {
        AccessControl ac = new AccessControl();
        ac.setAccessControlKey(1L);
        ac.setEntityType("Item");
        ac.setEntityKey("key-123");
        ac.setAcl("READ");

        assertEquals(1L, ac.getAccessControlKey());
        assertEquals("Item", ac.getEntityType());
        assertEquals("key-123", ac.getEntityKey());
        assertEquals("READ", ac.getAcl());
    }

    @Test
    void roleSetterGetter() {
        AccessControl ac = new AccessControl();
        Role role = new Role();
        ac.setRole(role);
        assertSame(role, ac.getRole());
    }

    @Test
    void userSetterGetter() {
        AccessControl ac = new AccessControl();
        Users user = new Users();
        ac.setUser(user);
        assertSame(user, ac.getUser());
    }

    @Test
    void equalsReflexive() {
        AccessControl ac = new AccessControl();
        ac.setEntityType("Item");
        ac.setEntityKey("k1");
        ac.setAcl("READ");
        assertEquals(ac, ac);
    }

    @Test
    void equalsNull() {
        AccessControl ac = new AccessControl();
        assertNotEquals(null, ac);
    }

    @Test
    void equalsDifferentType() {
        AccessControl ac = new AccessControl();
        assertNotEquals("string", ac);
    }

    @Test
    void equalsSameFields() {
        AccessControl a = new AccessControl();
        a.setEntityType("Item");
        a.setEntityKey("k1");
        a.setAcl("READ");

        AccessControl b = new AccessControl();
        b.setEntityType("Item");
        b.setEntityKey("k1");
        b.setAcl("READ");

        assertEquals(a, b);
    }

    @Test
    void notEqualsDifferentAcl() {
        AccessControl a = new AccessControl();
        a.setEntityType("Item");
        a.setAcl("READ");

        AccessControl b = new AccessControl();
        b.setEntityType("Item");
        b.setAcl("WRITE");

        assertNotEquals(a, b);
    }

    @Test
    void hashCodeConsistency() {
        AccessControl a = new AccessControl();
        a.setEntityType("Item");
        a.setAcl("READ");

        AccessControl b = new AccessControl();
        b.setEntityType("Item");
        b.setAcl("READ");

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringContainsAclAndEntityType() {
        AccessControl ac = new AccessControl();
        ac.setAcl("READ");
        ac.setEntityType("Item");
        ac.setEntityKey("k1");
        String s = ac.toString();
        assertTrue(s.contains("READ"));
        assertTrue(s.contains("Item"));
        assertTrue(s.contains("k1"));
    }
}
