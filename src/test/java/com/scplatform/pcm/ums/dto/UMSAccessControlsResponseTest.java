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

class UMSAccessControlsResponseTest {

    @Test
    void defaultsAreEmpty() {
        UMSAccessControlsResponse r = new UMSAccessControlsResponse();
        assertNotNull(r.getAccessControls());
        assertTrue(r.getAccessControls().isEmpty());
        assertEquals(0, r.getTotalSize());
    }

    @Test
    void addAccessControl() {
        UMSAccessControlsResponse r = new UMSAccessControlsResponse();
        r.addAccessControl(new UMSAccessControl());
        r.addAccessControl(new UMSAccessControl());
        assertEquals(2, r.getAccessControls().size());
    }

    @Test
    void equalsAndHashCode() {
        UMSAccessControlsResponse a = new UMSAccessControlsResponse();
        UMSAccessControlsResponse b = new UMSAccessControlsResponse();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
