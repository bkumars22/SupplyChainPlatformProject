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

class UMSAccessGroupResponseTest {

    @Test
    void defaultsAreEmpty() {
        UMSAccessGroupResponse r = new UMSAccessGroupResponse();
        assertEquals("SINGLE", r.getAccessGroupCardinality());
        assertNotNull(r.getAccessGroups());
        assertTrue(r.getAccessGroups().isEmpty());
    }

    @Test
    void addAccessGroup() {
        UMSAccessGroupResponse r = new UMSAccessGroupResponse();
        r.addAccessGroup(new UMSAccessGroup());
        r.addAccessGroup(new UMSAccessGroup());
        assertEquals(2, r.getAccessGroups().size());
    }

    @Test
    void setAccessGroupCardinality() {
        UMSAccessGroupResponse r = new UMSAccessGroupResponse();
        r.setAccessGroupCardinality("MULTI");
        assertEquals("MULTI", r.getAccessGroupCardinality());
    }

    @Test
    void equalsAndHashCode() {
        UMSAccessGroupResponse a = new UMSAccessGroupResponse();
        UMSAccessGroupResponse b = new UMSAccessGroupResponse();
        assertEquals(a, b);
        assertNotNull(a.toString());
    }
}
