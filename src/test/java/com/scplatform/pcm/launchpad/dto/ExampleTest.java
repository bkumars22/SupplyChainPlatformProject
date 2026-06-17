/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ExampleTest {

    @Test
    void defaultsAreNull() {
        Example ex = new Example();
        assertNull(ex.getHeader());
        assertNotNull(ex.getAdditionalProperties());
    }

    @Test
    void headerIsRetained() {
        Example ex = new Example();
        Header header = new Header();
        ex.setHeader(header);
        assertSame(header, ex.getHeader());
    }

    @Test
    void additionalPropertiesAreStored() {
        Example ex = new Example();
        ex.setAdditionalProperty("note", "demo");
        assertEquals("demo", ex.getAdditionalProperties().get("note"));
    }
}
