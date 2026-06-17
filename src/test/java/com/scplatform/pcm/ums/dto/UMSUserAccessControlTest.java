/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UMSUserAccessControlTest {

    @Test
    void defaults() {
        UMSUserAccessControl c = new UMSUserAccessControl();
        assertEquals("SINGLE", c.getAccessControlCardinality());
        assertFalse(c.isAccessControlRequired());
    }

    @Test
    void settersWork() {
        UMSUserAccessControl c = new UMSUserAccessControl();
        c.setAccessControlId("ac");
        c.setAccessControlDisplayName("acd");
        c.setAccessControlDescription("acDesc");
        c.setAccessGroupId("ag");
        c.setAccessGroupName("agn");
        c.setAccessGroupDescription("agDesc");
        c.setAccessControlCardinality("MULTI");
        c.setAccessControlRequired(true);
        assertEquals("ac", c.getAccessControlId());
        assertEquals("acd", c.getAccessControlDisplayName());
        assertEquals("acDesc", c.getAccessControlDescription());
        assertEquals("ag", c.getAccessGroupId());
        assertEquals("agn", c.getAccessGroupName());
        assertEquals("agDesc", c.getAccessGroupDescription());
        assertEquals("MULTI", c.getAccessControlCardinality());
        assertEquals(true, c.isAccessControlRequired());
    }

    @Test
    void equalsAndHashCode() {
        UMSUserAccessControl a = new UMSUserAccessControl();
        a.setAccessControlId("x");
        UMSUserAccessControl b = new UMSUserAccessControl();
        b.setAccessControlId("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
