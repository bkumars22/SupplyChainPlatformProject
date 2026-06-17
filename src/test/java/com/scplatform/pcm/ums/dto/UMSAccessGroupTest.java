/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UMSAccessGroupTest {

    @Test
    void defaults() {
        UMSAccessGroup g = new UMSAccessGroup();
        assertEquals("SINGLE", g.getAccessControlCardinality());
        assertFalse(g.isAccessControlRequired());
    }

    @Test
    void settersWork() {
        UMSAccessGroup g = new UMSAccessGroup();
        g.setAccessGroupId("id");
        g.setAccessGroupDisplayName("dn");
        g.setAccessGroupDescription("d");
        g.setAccessControlCardinality("MULTI");
        g.setAccessControlRequired(true);
        assertEquals("id", g.getAccessGroupId());
        assertEquals("dn", g.getAccessGroupDisplayName());
        assertEquals("d", g.getAccessGroupDescription());
        assertEquals("MULTI", g.getAccessControlCardinality());
        assertTrue(g.isAccessControlRequired());
    }

    @Test
    void equalsAndHashCode() {
        UMSAccessGroup a = new UMSAccessGroup();
        a.setAccessGroupId("x");
        UMSAccessGroup b = new UMSAccessGroup();
        b.setAccessGroupId("x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
