/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class UMSAccessControlTest {

    @Test
    void defaultsAndSetters() {
        UMSAccessControl c = new UMSAccessControl();
        assertNull(c.getAccessControlId());
        c.setAccessControlId("id");
        c.setAccessControlDisplayName("dn");
        c.setAccessControlDescription("d");
        assertEquals("id", c.getAccessControlId());
        assertEquals("dn", c.getAccessControlDisplayName());
        assertEquals("d", c.getAccessControlDescription());
    }

    @Test
    void equalsAndHashCode() {
        UMSAccessControl a = new UMSAccessControl();
        a.setAccessControlId("x");
        UMSAccessControl b = new UMSAccessControl();
        b.setAccessControlId("x");
        UMSAccessControl c = new UMSAccessControl();
        c.setAccessControlId("y");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotNull(a.toString());
    }
}
