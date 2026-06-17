/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BooleanToCharConverterTest {

    private final BooleanToCharConverter c = new BooleanToCharConverter();

    @Test
    void toDbTrueIsT() {
        assertEquals("T", c.convertToDatabaseColumn(Boolean.TRUE));
    }

    @Test
    void toDbFalseIsF() {
        assertEquals("F", c.convertToDatabaseColumn(Boolean.FALSE));
    }

    @Test
    void toDbNullIsF() {
        assertEquals("F", c.convertToDatabaseColumn(null));
    }

    @Test
    void fromDbTIsTrue() {
        assertTrue(c.convertToEntityAttribute("T"));
    }

    @Test
    void fromDbLowerTIsTrue() {
        assertTrue(c.convertToEntityAttribute("t"));
    }

    @Test
    void fromDbOtherIsFalse() {
        assertFalse(c.convertToEntityAttribute("F"));
        assertFalse(c.convertToEntityAttribute("X"));
    }

    @Test
    void fromDbNullIsFalse() {
        assertFalse(c.convertToEntityAttribute(null));
    }
}
