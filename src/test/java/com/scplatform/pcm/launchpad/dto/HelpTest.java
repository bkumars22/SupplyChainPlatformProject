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

class HelpTest {

    @Test
    void defaultsAreNull() {
        Help help = new Help();
        assertNull(help.getUrl());
        assertNull(help.getTitle());
        assertNull(help.getApp());
        assertNull(help.getLabel());
        assertNull(help.getName());
        assertNotNull(help.getAdditionalProperties());
    }

    @Test
    void settersWork() {
        Help help = new Help();
        help.setUrl("javascript:goShowContentPageHelp()");
        help.setTitle("Help");
        help.setApp("MCM");
        help.setLabel("Help");
        help.setName("Help");

        assertEquals("javascript:goShowContentPageHelp()", help.getUrl());
        assertEquals("Help", help.getTitle());
        assertEquals("MCM", help.getApp());
        assertEquals("Help", help.getLabel());
        assertEquals("Help", help.getName());
    }

    @Test
    void additionalPropertyStored() {
        Help help = new Help();
        help.setAdditionalProperty("k", "v");
        assertEquals("v", help.getAdditionalProperties().get("k"));
    }
}
