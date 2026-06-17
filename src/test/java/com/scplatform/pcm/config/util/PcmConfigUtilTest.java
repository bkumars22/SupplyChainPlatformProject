/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.util;

import com.scplatform.pcm.config.entity.PcmConfiguration;
import com.scplatform.pcm.config.repository.PcmConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PcmConfigUtilTest {

    @Mock
    private PcmConfigurationRepository configurationRepository;

    @InjectMocks
    private PcmConfigUtil pcmConfigUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ========================================================================
    // Cache Management Tests
    // ========================================================================

    @Test
    void testInitializeCache_Success() {
        // Arrange
        List<PcmConfiguration> configs = createSampleConfigurations();
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);

        // Act
        pcmConfigUtil.initializeCache();

        // Assert
        verify(configurationRepository, times(1)).findByIsActiveTrue();
        assertEquals(3, pcmConfigUtil.getConfigurationCount());
    }

    @Test
    void testRefreshCache_ReturnsCorrectCount() {
        // Arrange
        List<PcmConfiguration> configs = createSampleConfigurations();
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);

        // Act
        int count = pcmConfigUtil.refreshCache();

        // Assert
        assertEquals(3, count);
        verify(configurationRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testRefreshCache_ClearsExistingCache() {
        // Arrange - First load
        List<PcmConfiguration> initialConfigs = createSampleConfigurations();
        when(configurationRepository.findByIsActiveTrue()).thenReturn(initialConfigs);
        pcmConfigUtil.refreshCache();

        // Arrange - Second load with different data
        List<PcmConfiguration> newConfigs = List.of(
                createConfig("new.key", "new.value", "STRING")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(newConfigs);

        // Act
        int count = pcmConfigUtil.refreshCache();

        // Assert
        assertEquals(1, count);
        assertEquals("new.value", pcmConfigUtil.getString("new.key"));
        assertNull(pcmConfigUtil.getString("pcm.customer")); // Old key should be gone
    }

    @Test
    void testRefreshKey_ExistingActiveKey() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.test.key", "test.value", "STRING");
        when(configurationRepository.findByConfigKey("pcm.test.key")).thenReturn(Optional.of(config));
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache(); // Initialize empty cache

        // Act
        boolean result = pcmConfigUtil.refreshKey("pcm.test.key");

        // Assert
        assertTrue(result);
        assertEquals("test.value", pcmConfigUtil.getString("pcm.test.key"));
    }

    @Test
    void testRefreshKey_NonExistingKey() {
        // Arrange
        when(configurationRepository.findByConfigKey("non.existing.key")).thenReturn(Optional.empty());
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        boolean result = pcmConfigUtil.refreshKey("non.existing.key");

        // Assert
        assertFalse(result);
    }

    @Test
    void testRefreshKey_InactiveKey() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.inactive.key", "value", "STRING");
        config.setIsActive(false);
        when(configurationRepository.findByConfigKey("pcm.inactive.key")).thenReturn(Optional.of(config));
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        boolean result = pcmConfigUtil.refreshKey("pcm.inactive.key");

        // Assert
        assertFalse(result);
    }

    @Test
    void testRefreshByPrefix_ReturnsCorrectCount() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.sourcing.key1", "value1", "STRING"),
                createConfig("pcm.sourcing.key2", "value2", "STRING")
        );
        when(configurationRepository.findByConfigKeyStartingWith("pcm.sourcing")).thenReturn(configs);
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        int count = pcmConfigUtil.refreshByPrefix("pcm.sourcing");

        // Assert
        assertEquals(2, count);
    }

    @Test
    void testClearCache() {
        // Arrange
        List<PcmConfiguration> configs = createSampleConfigurations();
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();
        assertEquals(3, pcmConfigUtil.getConfigurationCount());

        // Act
        pcmConfigUtil.clearCache();

        // Assert - Cache should be empty but will reload on next access
        // After clear, accessing will trigger reload
    }

    @Test
    void testGetCacheStats() {
        // Arrange
        List<PcmConfiguration> configs = createSampleConfigurations();
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Map<String, Object> stats = pcmConfigUtil.getCacheStats();

        // Assert
        assertNotNull(stats);
        assertEquals(3, stats.get("cacheSize"));
        assertEquals(true, stats.get("cacheInitialized"));
        assertNotNull(stats.get("lastRefreshTime"));
    }

    // ========================================================================
    // String Value Tests
    // ========================================================================

    @Test
    void testGetString_ExistingKey() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.customer", "PCM", "STRING")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        String result = pcmConfigUtil.getString("pcm.customer");

        // Assert
        assertEquals("PCM", result);
    }

    @Test
    void testGetString_NonExistingKey_ReturnsNull() {
        // Arrange
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        String result = pcmConfigUtil.getString("non.existing.key");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetString_NonExistingKey_WithDefault() {
        // Arrange
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        String result = pcmConfigUtil.getString("non.existing.key", "DEFAULT");

        // Assert
        assertEquals("DEFAULT", result);
    }

    @Test
    void testGetString_NullValue_ReturnsDefault() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.null.value", null, "STRING");
        when(configurationRepository.findByIsActiveTrue()).thenReturn(List.of(config));
        pcmConfigUtil.refreshCache();

        // Act
        String result = pcmConfigUtil.getString("pcm.null.value", "DEFAULT");

        // Assert
        assertEquals("DEFAULT", result);
    }

    // ========================================================================
    // Boolean Value Tests
    // ========================================================================

    @Test
    void testGetBoolean_TrueValue() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.feature.enabled", "true", "BOOLEAN")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Boolean result = pcmConfigUtil.getBoolean("pcm.feature.enabled");

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetBoolean_FalseValue() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.feature.disabled", "false", "BOOLEAN")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Boolean result = pcmConfigUtil.getBoolean("pcm.feature.disabled");

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetBoolean_NonExistingKey_WithDefault() {
        // Arrange
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        Boolean result = pcmConfigUtil.getBoolean("non.existing.key", true);

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetBooleanValue_Primitive() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.feature.enabled", "true", "BOOLEAN")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        boolean result = pcmConfigUtil.getBooleanValue("pcm.feature.enabled", false);

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetBooleanValue_NonExisting_ReturnsDefault() {
        // Arrange
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        boolean result = pcmConfigUtil.getBooleanValue("non.existing.key", true);

        // Assert
        assertTrue(result);
    }

    // ========================================================================
    // List Value Tests
    // ========================================================================

    @Test
    void testGetList_CommaSeparated() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.types", "TYPE1,TYPE2,TYPE3", "LIST")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        List<String> result = pcmConfigUtil.getList("pcm.types");

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains("TYPE1"));
        assertTrue(result.contains("TYPE2"));
        assertTrue(result.contains("TYPE3"));
    }

    @Test
    void testGetList_WithSpaces_TrimmedCorrectly() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.types", "TYPE1 , TYPE2 , TYPE3", "LIST")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        List<String> result = pcmConfigUtil.getList("pcm.types");

        // Assert
        assertEquals(3, result.size());
        assertEquals("TYPE1", result.get(0));
        assertEquals("TYPE2", result.get(1));
        assertEquals("TYPE3", result.get(2));
    }

    @Test
    void testGetList_EmptyValue_ReturnsEmptyList() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.empty.list", "", "LIST")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        List<String> result = pcmConfigUtil.getList("pcm.empty.list");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetList_NonExistingKey_ReturnsEmptyList() {
        // Arrange
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        List<String> result = pcmConfigUtil.getList("non.existing.key");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetList_CustomDelimiter() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.semicolon.list", "A;B;C", "LIST")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        List<String> result = pcmConfigUtil.getList("pcm.semicolon.list", ";");

        // Assert
        assertEquals(3, result.size());
        assertEquals("A", result.get(0));
    }

    @Test
    void testGetSet_ReturnsUniqueValues() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.duplicates", "A,B,A,C,B", "LIST")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Set<String> result = pcmConfigUtil.getSet("pcm.duplicates");

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains("A"));
        assertTrue(result.contains("B"));
        assertTrue(result.contains("C"));
    }

    // ========================================================================
    // Integer Value Tests
    // ========================================================================

    @Test
    void testGetInteger_ValidValue() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.timeout", "30", "INTEGER")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Integer result = pcmConfigUtil.getInteger("pcm.timeout");

        // Assert
        assertEquals(30, result);
    }

    @Test
    void testGetInteger_InvalidValue_ReturnsDefault() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.invalid.int", "not-a-number", "INTEGER")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Integer result = pcmConfigUtil.getInteger("pcm.invalid.int", 100);

        // Assert
        assertEquals(100, result);
    }

    @Test
    void testGetInteger_NonExisting_ReturnsDefault() {
        // Arrange
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        Integer result = pcmConfigUtil.getInteger("non.existing.key", 50);

        // Assert
        assertEquals(50, result);
    }

    @Test
    void testGetIntValue_Primitive() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.count", "42", "INTEGER")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        int result = pcmConfigUtil.getIntValue("pcm.count", 0);

        // Assert
        assertEquals(42, result);
    }

    // ========================================================================
    // Long Value Tests
    // ========================================================================

    @Test
    void testGetLong_ValidValue() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.large.number", "9999999999", "INTEGER")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Long result = pcmConfigUtil.getLong("pcm.large.number", 0L);

        // Assert
        assertEquals(9999999999L, result);
    }

    @Test
    void testGetLong_InvalidValue_ReturnsDefault() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.invalid.long", "invalid", "INTEGER")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Long result = pcmConfigUtil.getLong("pcm.invalid.long", 100L);

        // Assert
        assertEquals(100L, result);
    }

    // ========================================================================
    // Double Value Tests
    // ========================================================================

    @Test
    void testGetDouble_ValidValue() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.percentage", "99.99", "DOUBLE")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Double result = pcmConfigUtil.getDouble("pcm.percentage", 0.0);

        // Assert
        assertEquals(99.99, result, 0.001);
    }

    @Test
    void testGetDouble_InvalidValue_ReturnsDefault() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.invalid.double", "not-double", "DOUBLE")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Double result = pcmConfigUtil.getDouble("pcm.invalid.double", 1.5);

        // Assert
        assertEquals(1.5, result, 0.001);
    }

    // ========================================================================
    // Configuration Lookup Tests
    // ========================================================================

    @Test
    void testGetConfiguration_ExistingKey() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.test", "value", "STRING");
        when(configurationRepository.findByIsActiveTrue()).thenReturn(List.of(config));
        pcmConfigUtil.refreshCache();

        // Act
        Optional<PcmConfiguration> result = pcmConfigUtil.getConfiguration("pcm.test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("value", result.get().getConfigValue());
    }

    @Test
    void testGetConfiguration_NonExistingKey() {
        // Arrange
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act
        Optional<PcmConfiguration> result = pcmConfigUtil.getConfiguration("non.existing");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByPrefix() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.sourcing.key1", "value1", "STRING"),
                createConfig("pcm.sourcing.key2", "value2", "STRING"),
                createConfig("pcm.other.key", "value3", "STRING")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Map<String, String> result = pcmConfigUtil.getByPrefix("pcm.sourcing");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey("pcm.sourcing.key1"));
        assertTrue(result.containsKey("pcm.sourcing.key2"));
        assertFalse(result.containsKey("pcm.other.key"));
    }

    @Test
    void testGetByPattern() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.feature1.enabled", "true", "BOOLEAN"),
                createConfig("pcm.feature2.enabled", "false", "BOOLEAN"),
                createConfig("pcm.feature1.disabled", "true", "BOOLEAN")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Map<String, String> result = pcmConfigUtil.getByPattern("pcm.*.enabled");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey("pcm.feature1.enabled"));
        assertTrue(result.containsKey("pcm.feature2.enabled"));
    }

    @Test
    void testContainsKey_Exists() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.exists", "value", "STRING")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act & Assert
        assertTrue(pcmConfigUtil.containsKey("pcm.exists"));
    }

    @Test
    void testContainsKey_NotExists() {
        // Arrange
        when(configurationRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        pcmConfigUtil.refreshCache();

        // Act & Assert
        assertFalse(pcmConfigUtil.containsKey("non.existing"));
    }

    @Test
    void testGetAllKeys() {
        // Arrange
        List<PcmConfiguration> configs = createSampleConfigurations();
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Set<String> keys = pcmConfigUtil.getAllKeys();

        // Assert
        assertEquals(3, keys.size());
        assertTrue(keys.contains("pcm.customer"));
        assertTrue(keys.contains("pcm.enabled"));
        assertTrue(keys.contains("pcm.types"));
    }

    @Test
    void testGetAllConfigurations() {
        // Arrange
        List<PcmConfiguration> configs = createSampleConfigurations();
        when(configurationRepository.findByIsActiveTrue()).thenReturn(configs);
        pcmConfigUtil.refreshCache();

        // Act
        Map<String, String> allConfigs = pcmConfigUtil.getAllConfigurations();

        // Assert
        assertEquals(3, allConfigs.size());
        assertEquals("PCM", allConfigs.get("pcm.customer"));
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private List<PcmConfiguration> createSampleConfigurations() {
        return List.of(
                createConfig("pcm.customer", "PCM", "STRING"),
                createConfig("pcm.enabled", "true", "BOOLEAN"),
                createConfig("pcm.types", "TYPE1,TYPE2", "LIST")
        );
    }

    private PcmConfiguration createConfig(String key, String value, String valueType) {
        return PcmConfiguration.builder()
                .id(1L)
                .configKey(key)
                .configValue(value)
                .valueType(valueType)
                .isActive(true)
                .description("Test configuration")
                .build();
    }
}
