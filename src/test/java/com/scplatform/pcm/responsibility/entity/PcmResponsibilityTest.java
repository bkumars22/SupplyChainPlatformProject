/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.responsibility.entity;

import com.scplatform.pcm.role.entity.Role;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PcmResponsibilityTest {

    @Test
    void defaultConstructor() {
        PcmResponsibility r = new PcmResponsibility();
        assertNull(r.getResponsibilityKey());
        assertNull(r.getResponsibilityName());
        assertNull(r.getDisplayOrder());
        assertNull(r.getResponsibilityType());
        assertNotNull(r.getRoles());
    }

    @Test
    void constructorWithKeyAndName() {
        PcmResponsibility r = new PcmResponsibility("KEY1", "Buyer");
        assertEquals("KEY1", r.getResponsibilityKey());
        assertEquals("Buyer", r.getResponsibilityName());
    }

    @Test
    void settersAndGetters() {
        PcmResponsibility r = new PcmResponsibility();
        r.setResponsibilityKey("OWNER");
        r.setResponsibilityName("Owner");
        r.setDisplayOrder(1L);
        r.setResponsibilityType(PcmResponsibility.TYPE_ITEM);

        assertEquals("OWNER", r.getResponsibilityKey());
        assertEquals("Owner", r.getResponsibilityName());
        assertEquals(1L, r.getDisplayOrder());
        assertEquals("I", r.getResponsibilityType());
    }

    @Test
    void typeConstants() {
        assertEquals("I", PcmResponsibility.TYPE_ITEM);
        assertEquals("IC", PcmResponsibility.TYPE_ITEMCATEGORY);
    }

    @Test
    void rolesSetterGetter() {
        PcmResponsibility r = new PcmResponsibility("KEY1", "Buyer");
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1L);
        roles.add(role);
        r.setRoles(roles);
        assertEquals(1, r.getRoles().size());
    }

    @Test
    void equalsReflexive() {
        PcmResponsibility r = new PcmResponsibility("KEY1", "Buyer");
        assertEquals(r, r);
    }

    @Test
    void equalsNull() {
        PcmResponsibility r = new PcmResponsibility("KEY1", "Buyer");
        assertNotEquals(null, r);
    }

    @Test
    void equalsDifferentType() {
        PcmResponsibility r = new PcmResponsibility("KEY1", "Buyer");
        assertNotEquals("string", r);
    }

    @Test
    void equalsSameKey() {
        PcmResponsibility a = new PcmResponsibility("KEY1", "Buyer");
        PcmResponsibility b = new PcmResponsibility("KEY1", "Other");
        assertEquals(a, b);
    }

    @Test
    void notEqualsDifferentKey() {
        PcmResponsibility a = new PcmResponsibility("KEY1", "Buyer");
        PcmResponsibility b = new PcmResponsibility("KEY2", "Buyer");
        assertNotEquals(a, b);
    }

    @Test
    void hashCodeFromKey() {
        PcmResponsibility r = new PcmResponsibility("KEY1", "Buyer");
        assertEquals("KEY1".hashCode(), r.hashCode());
    }

    @Test
    void compareToSameDisplayOrder() {
        PcmResponsibility a = new PcmResponsibility("KEY1", "Buyer");
        a.setDisplayOrder(1L);
        PcmResponsibility b = new PcmResponsibility("KEY2", "Owner");
        b.setDisplayOrder(1L);
        assertTrue(a.compareTo(b) < 0); // "KEY1" < "KEY2"
    }

    @Test
    void compareToByDisplayOrder() {
        PcmResponsibility a = new PcmResponsibility("KEY1", "Buyer");
        a.setDisplayOrder(1L);
        PcmResponsibility b = new PcmResponsibility("KEY1", "Buyer");
        b.setDisplayOrder(2L);
        assertTrue(a.compareTo(b) < 0);
    }

    @Test
    void toStringReturnsKey() {
        PcmResponsibility r = new PcmResponsibility("MY_KEY", "Buyer");
        assertEquals("MY_KEY", r.toString());
    }

    @Test
    void getResponsibilityNaturalKeyAsJSON() {
        PcmResponsibility r = new PcmResponsibility("KEY1", "Buyer Responsibility");
        var json = r.getResponsibilityNaturalKeyAsJSON();
        assertNotNull(json);
        assertEquals("Buyer Responsibility", json.get("responsibility").asText());
    }
}
