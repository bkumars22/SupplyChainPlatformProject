/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class AvailableRoleTest {

    @Test
    void defaultsAreNull() {
        AvailableRole role = new AvailableRole();
        assertNull(role.getUrl());
        assertNull(role.getTitle());
        assertNull(role.getApp());
        assertNull(role.getLabel());
        assertNull(role.getIcon());
        assertNotNull(role.getAdditionalProperties());
    }

    @Test
    void settersUpdateAllFields() {
        AvailableRole role = new AvailableRole();
        role.setUrl("/url");
        role.setTitle("Title");
        role.setApp("app1");
        role.setLabel("label");
        role.setIcon("icon.svg");

        assertEquals("/url", role.getUrl());
        assertEquals("Title", role.getTitle());
        assertEquals("app1", role.getApp());
        assertEquals("label", role.getLabel());
        assertEquals("icon.svg", role.getIcon());
    }

    @Test
    void additionalPropertiesAreCaptured() {
        AvailableRole role = new AvailableRole();
        role.setAdditionalProperty("k", "v");
        assertEquals("v", role.getAdditionalProperties().get("k"));
    }
}
