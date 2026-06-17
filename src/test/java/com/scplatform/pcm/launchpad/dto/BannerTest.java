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

class BannerTest {

    @Test
    void defaultsAreNullAndAdditionalPropsEmpty() {
        Banner banner = new Banner();
        assertNull(banner.getType());
        assertNotNull(banner.getAdditionalProperties());
        assertEquals(0, banner.getAdditionalProperties().size());
    }

    @Test
    void typeIsSetAndRetrieved() {
        Banner banner = new Banner();
        banner.setType("dev");
        assertEquals("dev", banner.getType());
    }

    @Test
    void additionalPropertiesAreStored() {
        Banner banner = new Banner();
        banner.setAdditionalProperty("color", "red");
        assertEquals("red", banner.getAdditionalProperties().get("color"));
    }
}
