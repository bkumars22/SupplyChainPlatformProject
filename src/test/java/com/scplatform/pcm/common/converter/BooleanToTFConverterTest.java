/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BooleanToTFConverterTest {

    private final BooleanToTFConverter c = new BooleanToTFConverter();

    @Test
    void convertToDatabaseColumnTrueIsT() {
        assertEquals("T", c.convertToDatabaseColumn(Boolean.TRUE));
    }

    @Test
    void convertToDatabaseColumnFalseIsF() {
        assertEquals("F", c.convertToDatabaseColumn(Boolean.FALSE));
    }

    @Test
    void convertToDatabaseColumnNullIsF() {
        assertEquals("F", c.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttributeTIsTrue() {
        assertTrue(c.convertToEntityAttribute("T"));
    }

    @Test
    void convertToEntityAttributeIsCaseInsensitive() {
        assertTrue(c.convertToEntityAttribute("t"));
    }

    @Test
    void convertToEntityAttributeOtherIsFalse() {
        assertFalse(c.convertToEntityAttribute("F"));
        assertFalse(c.convertToEntityAttribute("X"));
        assertFalse(c.convertToEntityAttribute(null));
    }
}
