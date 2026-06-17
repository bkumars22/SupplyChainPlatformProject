/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BomGroupLinkIdTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        BomGroupLinkId id = new BomGroupLinkId();
        assertNull(id.getBomGroup());
        assertNull(id.getBom());
    }

    @Test
    void allArgConstructorAssignsFields() {
        BomGroupLinkId id = new BomGroupLinkId(1L, 2L);
        assertEquals(1L, id.getBomGroup());
        assertEquals(2L, id.getBom());
    }

    @Test
    void settersUpdateFields() {
        BomGroupLinkId id = new BomGroupLinkId();
        id.setBomGroup(11L);
        id.setBom(22L);
        assertEquals(11L, id.getBomGroup());
        assertEquals(22L, id.getBom());
    }

    @Test
    void equalsAndHashCodeWorkOnBothFields() {
        BomGroupLinkId a = new BomGroupLinkId(1L, 2L);
        BomGroupLinkId b = new BomGroupLinkId(1L, 2L);
        BomGroupLinkId c = new BomGroupLinkId(9L, 2L);

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    void toStringContainsBothKeys() {
        BomGroupLinkId id = new BomGroupLinkId(7L, 8L);
        String s = id.toString();
        assertTrue(s.contains("7"));
        assertTrue(s.contains("8"));
    }
}
