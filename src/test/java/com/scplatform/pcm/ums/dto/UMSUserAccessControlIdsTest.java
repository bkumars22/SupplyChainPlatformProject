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

class UMSUserAccessControlIdsTest {

    @Test
    void defaultsAreNull() {
        UMSUserAccessControlIds c = new UMSUserAccessControlIds();
        assertNull(c.getAccessControlId());
        assertNull(c.getAccessGroupId());
    }

    @Test
    void settersWork() {
        UMSUserAccessControlIds c = new UMSUserAccessControlIds();
        c.setAccessControlId("ac");
        c.setAccessGroupId("ag");
        assertEquals("ac", c.getAccessControlId());
        assertEquals("ag", c.getAccessGroupId());
    }

    @Test
    void equalsAndHashCode() {
        UMSUserAccessControlIds a = new UMSUserAccessControlIds();
        a.setAccessControlId("ac");
        a.setAccessGroupId("ag");
        UMSUserAccessControlIds b = new UMSUserAccessControlIds();
        b.setAccessControlId("ac");
        b.setAccessGroupId("ag");
        UMSUserAccessControlIds c = new UMSUserAccessControlIds();
        c.setAccessControlId("xx");
        c.setAccessGroupId("ag");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotNull(a.toString());
    }
}
