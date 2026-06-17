/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PcmConfigurationTest {

    private PcmConfiguration config;

    @BeforeEach
    void setUp() {
        config = new PcmConfiguration();
    }

    // ========================================================================
    // Builder Pattern Tests
    // ========================================================================

    @Test
    void testBuilder_AllFields() {
        LocalDateTime now = LocalDateTime.now();
        
        PcmConfiguration built = PcmConfiguration.builder()
                .id(1L)
                .configKey("pcm.test.key")
                .configValue("test.value")
                .description("Test description")
                .valueType("STRING")
                .isActive(true)
                .createdDate(now)
                .modifiedDate(now)
                .createdBy("admin")
                .modifiedBy("admin")
                .build();

        assertNotNull(built);
        assertEquals(1L, built.getId());
        assertEquals("pcm.test.key", built.getConfigKey());
        assertEquals("test.value", built.getConfigValue());
        assertEquals("Test description", built.getDescription());
        assertEquals("STRING", built.getValueType());
        assertTrue(built.getIsActive());
        assertEquals(now, built.getCreatedDate());
        assertEquals(now, built.getModifiedDate());
        assertEquals("admin", built.getCreatedBy());
        assertEquals("admin", built.getModifiedBy());
    }

    @Test
    void testBuilder_MinimalFields() {
        PcmConfiguration built = PcmConfiguration.builder()
                .configKey("test.key")
                .configValue("value")
                .build();

        assertNotNull(built);
        assertEquals("test.key", built.getConfigKey());
        assertEquals("value", built.getConfigValue());
    }

    // ========================================================================
    // getValueAsBoolean Tests
    // ========================================================================

    @ParameterizedTest
    @ValueSource(strings = {"true", "TRUE", "True", "TrUe"})
    void testGetValueAsBoolean_ReturnsTrueForTrueValues(String value) {
        config.setConfigValue(value);
        assertTrue(config.getValueAsBoolean());
    }

    @ParameterizedTest
    @ValueSource(strings = {"false", "FALSE", "False", "FaLsE"})
    void testGetValueAsBoolean_ReturnsFalseForFalseValues(String value) {
        config.setConfigValue(value);
        assertFalse(config.getValueAsBoolean());
    }

    @Test
    void testGetValueAsBoolean_ReturnsNullForNull() {
        config.setConfigValue(null);
        assertNull(config.getValueAsBoolean());
    }

    @Test
    void testGetValueAsBoolean_ReturnsFalseForInvalidValue() {
        config.setConfigValue("invalid");
        assertFalse(config.getValueAsBoolean());
    }

    @Test
    void testGetValueAsBoolean_ReturnsFalseForEmptyString() {
        config.setConfigValue("");
        assertFalse(config.getValueAsBoolean());
    }

    @Test
    void testGetValueAsBoolean_ReturnsFalseForWhitespace() {
        config.setConfigValue("   ");
        assertFalse(config.getValueAsBoolean());
    }

    // ========================================================================
    // getValueAsList Tests
    // ========================================================================

    @Test
    void testGetValueAsList_SingleItem() {
        config.setConfigValue("item1");
        List<String> result = config.getValueAsList();
        
        assertEquals(1, result.size());
        assertEquals("item1", result.get(0));
    }

    @Test
    void testGetValueAsList_MultipleItems() {
        config.setConfigValue("item1,item2,item3");
        List<String> result = config.getValueAsList();
        
        assertEquals(3, result.size());
        assertEquals("item1", result.get(0));
        assertEquals("item2", result.get(1));
        assertEquals("item3", result.get(2));
    }

    @Test
    void testGetValueAsList_WithWhitespace() {
        config.setConfigValue("item1, item2 , item3");
        List<String> result = config.getValueAsList();
        
        assertEquals(3, result.size());
        assertEquals("item1", result.get(0));
        assertEquals("item2", result.get(1));
        assertEquals("item3", result.get(2));
    }

    @Test
    void testGetValueAsList_NullValue() {
        config.setConfigValue(null);
        List<String> result = config.getValueAsList();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetValueAsList_EmptyString() {
        config.setConfigValue("");
        List<String> result = config.getValueAsList();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetValueAsList_OnlyWhitespace() {
        config.setConfigValue("   ");
        List<String> result = config.getValueAsList();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetValueAsList_EmptyItemsBetweenCommas() {
        config.setConfigValue("item1,,item2,  ,item3");
        List<String> result = config.getValueAsList();
        
        // Should filter out empty items
        assertTrue(result.contains("item1"));
        assertTrue(result.contains("item2"));
        assertTrue(result.contains("item3"));
    }

    // ========================================================================
    // getValueAsInteger Tests
    // ========================================================================

    @Test
    void testGetValueAsInteger_ValidPositive() {
        config.setConfigValue("100");
        assertEquals(100, config.getValueAsInteger());
    }

    @Test
    void testGetValueAsInteger_ValidNegative() {
        config.setConfigValue("-50");
        assertEquals(-50, config.getValueAsInteger());
    }

    @Test
    void testGetValueAsInteger_ValidZero() {
        config.setConfigValue("0");
        assertEquals(0, config.getValueAsInteger());
    }

    @Test
    void testGetValueAsInteger_NullReturnsNull() {
        config.setConfigValue(null);
        assertNull(config.getValueAsInteger());
    }

    @Test
    void testGetValueAsInteger_InvalidReturnsNull() {
        config.setConfigValue("not_a_number");
        assertNull(config.getValueAsInteger());
    }

    @Test
    void testGetValueAsInteger_EmptyStringReturnsNull() {
        config.setConfigValue("");
        assertNull(config.getValueAsInteger());
    }

    @Test
    void testGetValueAsInteger_DecimalReturnsNull() {
        config.setConfigValue("100.99");
        // Should fail to parse as integer and return null
        assertNull(config.getValueAsInteger());
    }

    @Test
    void testGetValueAsInteger_LargeValue() {
        config.setConfigValue("2147483647"); // Integer.MAX_VALUE
        assertEquals(Integer.MAX_VALUE, config.getValueAsInteger());
    }

    @Test
    void testGetValueAsInteger_WithWhitespace() {
        config.setConfigValue("  100  ");
        assertEquals(100, config.getValueAsInteger());
    }

    // ========================================================================
    // getValueAsDouble Tests
    // ========================================================================

    @Test
    void testGetValueAsDouble_ValidPositive() {
        config.setConfigValue("100.50");
        assertEquals(100.50, config.getValueAsDouble(), 0.001);
    }

    @Test
    void testGetValueAsDouble_ValidNegative() {
        config.setConfigValue("-50.25");
        assertEquals(-50.25, config.getValueAsDouble(), 0.001);
    }

    @Test
    void testGetValueAsDouble_ValidZero() {
        config.setConfigValue("0.0");
        assertEquals(0.0, config.getValueAsDouble(), 0.001);
    }

    @Test
    void testGetValueAsDouble_IntegerValue() {
        config.setConfigValue("100");
        assertEquals(100.0, config.getValueAsDouble(), 0.001);
    }

    @Test
    void testGetValueAsDouble_NullReturnsNull() {
        config.setConfigValue(null);
        assertNull(config.getValueAsDouble());
    }

    @Test
    void testGetValueAsDouble_InvalidReturnsNull() {
        config.setConfigValue("not_a_number");
        assertNull(config.getValueAsDouble());
    }

    @Test
    void testGetValueAsDouble_EmptyStringReturnsNull() {
        config.setConfigValue("");
        assertNull(config.getValueAsDouble());
    }

    @Test
    void testGetValueAsDouble_ScientificNotation() {
        config.setConfigValue("1.5E2"); // 150.0
        assertEquals(150.0, config.getValueAsDouble(), 0.001);
    }

    @Test
    void testGetValueAsDouble_WithWhitespace() {
        config.setConfigValue("  99.99  ");
        assertEquals(99.99, config.getValueAsDouble(), 0.001);
    }

    // ========================================================================
    // Setter/Getter Tests
    // ========================================================================

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        
        config.setId(1L);
        config.setConfigKey("test.key");
        config.setConfigValue("test.value");
        config.setDescription("Description");
        config.setValueType("STRING");
        config.setIsActive(true);
        config.setCreatedDate(now);
        config.setModifiedDate(now);
        config.setCreatedBy("admin");
        config.setModifiedBy("system");

        assertEquals(1L, config.getId());
        assertEquals("test.key", config.getConfigKey());
        assertEquals("test.value", config.getConfigValue());
        assertEquals("Description", config.getDescription());
        assertEquals("STRING", config.getValueType());
        assertTrue(config.getIsActive());
        assertEquals(now, config.getCreatedDate());
        assertEquals(now, config.getModifiedDate());
        assertEquals("admin", config.getCreatedBy());
        assertEquals("system", config.getModifiedBy());
    }

    @Test
    void testIsActive_DefaultValue() {
        PcmConfiguration newConfig = new PcmConfiguration();
        // Default is true due to @Builder.Default annotation
        assertTrue(newConfig.getIsActive());
    }

    // ========================================================================
    // Edge Cases and Special Values
    // ========================================================================

    @Test
    void testSpecialCharactersInValue() {
        config.setConfigValue("value with spaces & special chars! @#$%");
        assertEquals("value with spaces & special chars! @#$%", config.getConfigValue());
    }

    @Test
    void testUnicodeInValue() {
        config.setConfigValue("Unicode: 日本語 中文 한국어");
        assertEquals("Unicode: 日本語 中文 한국어", config.getConfigValue());
    }

    @Test
    void testNewLineInValue() {
        config.setConfigValue("line1\nline2\nline3");
        assertEquals("line1\nline2\nline3", config.getConfigValue());
    }

    @Test
    void testJsonInValue() {
        config.setConfigValue("{\"key\":\"value\",\"array\":[1,2,3]}");
        assertEquals("{\"key\":\"value\",\"array\":[1,2,3]}", config.getConfigValue());
    }

    @Test
    void testVeryLongValue() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("x");
        }
        String longValue = sb.toString();
        
        config.setConfigValue(longValue);
        assertEquals(1000, config.getConfigValue().length());
    }

    // ========================================================================
    // equals/hashCode/toString Tests (if Lombok generated)
    // ========================================================================

    @Test
    void testEqualsAndHashCode_SameObject() {
        config.setId(1L);
        assertEquals(config, config);
        assertEquals(config.hashCode(), config.hashCode());
    }

    @Test
    void testToString_NotNull() {
        config.setId(1L);
        config.setConfigKey("test.key");
        config.setConfigValue("value");
        
        String toString = config.toString();
        assertNotNull(toString);
        // Should contain meaningful data
        assertTrue(toString.length() > 0);
    }

    // ========================================================================
    // Type Conversion Edge Cases
    // ========================================================================

    @Test
    void testBooleanConversion_YesNoNotSupported() {
        config.setConfigValue("yes");
        // Standard Boolean.parseBoolean only recognizes "true"
        assertFalse(config.getValueAsBoolean());
    }

    @Test
    void testBooleanConversion_OneZeroNotSupported() {
        config.setConfigValue("1");
        // Standard Boolean.parseBoolean only recognizes "true"
        assertFalse(config.getValueAsBoolean());
    }

    @Test
    void testIntegerOverflow() {
        config.setConfigValue("99999999999999999999");
        // Should catch NumberFormatException and return null
        assertNull(config.getValueAsInteger());
    }

    @Test
    void testDoubleSpecialValues() {
        config.setConfigValue("Infinity");
        // Double.parseDouble can handle "Infinity"
        assertEquals(Double.POSITIVE_INFINITY, config.getValueAsDouble(), 0.001);
    }

    @Test
    void testDoubleNaN() {
        config.setConfigValue("NaN");
        assertTrue(Double.isNaN(config.getValueAsDouble()));
    }
}
