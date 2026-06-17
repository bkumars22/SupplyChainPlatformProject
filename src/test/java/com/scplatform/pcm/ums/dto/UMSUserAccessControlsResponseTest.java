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

class UMSUserAccessControlsResponseTest {

    @Test
    void defaultsAreEmpty() {
        UMSUserAccessControlsResponse r = new UMSUserAccessControlsResponse();
        assertNotNull(r.getAccessControls());
        assertTrue(r.getAccessControls().isEmpty());
    }

    @Test
    void addAccessControl() {
        UMSUserAccessControlsResponse r = new UMSUserAccessControlsResponse();
        r.addAccessControl(new UMSUserAccessControl());
        assertEquals(1, r.getAccessControls().size());
    }

    @Test
    void equalsAndHashCode() {
        UMSUserAccessControlsResponse a = new UMSUserAccessControlsResponse();
        UMSUserAccessControlsResponse b = new UMSUserAccessControlsResponse();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
