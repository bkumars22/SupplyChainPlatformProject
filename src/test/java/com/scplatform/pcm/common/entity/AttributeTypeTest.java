/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AttributeTypeTest {

    @Test
    void enumHasFiveConstants() {
        assertEquals(5, AttributeType.values().length);
        assertNotNull(AttributeType.valueOf("STRING"));
        assertNotNull(AttributeType.valueOf("DATE"));
        assertNotNull(AttributeType.valueOf("INTEGER"));
        assertNotNull(AttributeType.valueOf("FLOAT"));
        assertNotNull(AttributeType.valueOf("BOOLEAN"));
    }

    @Test
    void getAttributeValueOnNullAttributeReturnsNull() {
        for (AttributeType t : AttributeType.values()) {
            // Skip DATE - DATE override may not handle null specially
            org.junit.jupiter.api.Assertions.assertNull(t.getAttributeValue(null));
        }
    }
}
