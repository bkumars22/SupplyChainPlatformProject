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

class AboutTest {

    @Test
    void defaultConstructorInitialisesNullFieldsAndEmptyAdditionalProps() {
        About about = new About();

        assertNull(about.getLabel());
        assertNull(about.getBuild());
        assertNull(about.getVersion());
        assertNotNull(about.getAdditionalProperties());
        assertEquals(0, about.getAdditionalProperties().size());
    }

    @Test
    void gettersAndSettersRoundTripValues() {
        About about = new About();
        about.setLabel("Modern App");
        about.setBuild("12345");
        about.setVersion("21.4");

        assertEquals("Modern App", about.getLabel());
        assertEquals("12345", about.getBuild());
        assertEquals("21.4", about.getVersion());
    }

    @Test
    void additionalPropertyStoresArbitraryValues() {
        About about = new About();
        about.setAdditionalProperty("env", "dev");
        about.setAdditionalProperty("count", 7);

        assertEquals("dev", about.getAdditionalProperties().get("env"));
        assertEquals(7, about.getAdditionalProperties().get("count"));
    }
}
