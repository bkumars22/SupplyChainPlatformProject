/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for YesNoConverter.
 */
@DisplayName("YesNoConverter Tests")
class YesNoConverterTest {

    private YesNoConverter converter;

    @BeforeEach
    void setUp() {
        converter = new YesNoConverter();
    }

    @Nested
    @DisplayName("Convert To Database Column Tests")
    class ToDatabaseTests {

        @Test
        @DisplayName("Should convert true to Y")
        void shouldConvertTrueToY() {
            assertEquals("Y", converter.convertToDatabaseColumn(true));
        }

        @Test
        @DisplayName("Should convert false to N")
        void shouldConvertFalseToN() {
            assertEquals("N", converter.convertToDatabaseColumn(false));
        }

        @Test
        @DisplayName("Should convert null to null")
        void shouldConvertNullToNull() {
            assertNull(converter.convertToDatabaseColumn(null));
        }
    }

    @Nested
    @DisplayName("Convert To Entity Attribute Tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should convert Y to true")
        void shouldConvertYToTrue() {
            assertTrue(converter.convertToEntityAttribute("Y"));
        }

        @Test
        @DisplayName("Should convert y (lowercase) to true")
        void shouldConvertLowercaseYToTrue() {
            assertTrue(converter.convertToEntityAttribute("y"));
        }

        @Test
        @DisplayName("Should convert N to false")
        void shouldConvertNToFalse() {
            assertFalse(converter.convertToEntityAttribute("N"));
        }

        @Test
        @DisplayName("Should convert n (lowercase) to false")
        void shouldConvertLowercaseNToFalse() {
            assertFalse(converter.convertToEntityAttribute("n"));
        }

        @Test
        @DisplayName("Should convert null to false")
        void shouldConvertNullToFalse() {
            assertFalse(converter.convertToEntityAttribute(null));
        }

        @Test
        @DisplayName("Should convert empty string to false")
        void shouldConvertEmptyStringToFalse() {
            assertFalse(converter.convertToEntityAttribute(""));
        }

        @Test
        @DisplayName("Should convert any non-Y value to false")
        void shouldConvertNonYValueToFalse() {
            assertFalse(converter.convertToEntityAttribute("X"));
            assertFalse(converter.convertToEntityAttribute("YES"));
            assertFalse(converter.convertToEntityAttribute("TRUE"));
        }
    }
}
