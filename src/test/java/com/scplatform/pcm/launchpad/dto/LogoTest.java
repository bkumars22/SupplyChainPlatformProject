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

class LogoTest {

    @Test
    void defaultsAreNull() {
        Logo logo = new Logo();
        assertNull(logo.getUrl());
        assertNull(logo.getTitle());
        assertNotNull(logo.getAdditionalProperties());
    }

    @Test
    void settersWork() {
        Logo logo = new Logo();
        logo.setUrl("skins/e2-modern/images/main_logo_277x65.png");
        logo.setTitle("E2");
        assertEquals("skins/e2-modern/images/main_logo_277x65.png", logo.getUrl());
        assertEquals("E2", logo.getTitle());
    }

    @Test
    void additionalPropertyStored() {
        Logo logo = new Logo();
        logo.setAdditionalProperty("k", "v");
        assertEquals("v", logo.getAdditionalProperties().get("k"));
    }
}
