/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.service;

import com.scplatform.pcm.config.entity.PcmConfiguration;
import com.scplatform.pcm.config.repository.PcmConfigurationRepository;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PcmConfigurationServiceTest {

    @Mock
    private PcmConfigurationRepository configurationRepository;

    @Mock
    private PcmConfigUtil configUtil;

    @InjectMocks
    private PcmConfigurationService configurationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ========================================================================
    // Read Operations Tests
    // ========================================================================

    @Test
    void testGetByKey_ExistingKey() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.test.key", "test.value");
        when(configurationRepository.findByConfigKey("pcm.test.key")).thenReturn(Optional.of(config));

        // Act
        Optional<PcmConfiguration> result = configurationService.getByKey("pcm.test.key");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test.value", result.get().getConfigValue());
        verify(configurationRepository, times(1)).findByConfigKey("pcm.test.key");
    }

    @Test
    void testGetByKey_NonExistingKey() {
        // Arrange
        when(configurationRepository.findByConfigKey("non.existing")).thenReturn(Optional.empty());

        // Act
        Optional<PcmConfiguration> result = configurationService.getByKey("non.existing");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllActive() {
        // Arrange
        List<PcmConfiguration> activeConfigs = List.of(
                createConfig("key1", "value1"),
                createConfig("key2", "value2")
        );
        when(configurationRepository.findByIsActiveTrue()).thenReturn(activeConfigs);

        // Act
        List<PcmConfiguration> result = configurationService.getAllActive();

        // Assert
        assertEquals(2, result.size());
        verify(configurationRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testGetAll() {
        // Arrange
        List<PcmConfiguration> allConfigs = List.of(
                createConfig("key1", "value1"),
                createConfig("key2", "value2"),
                createConfig("key3", "value3")
        );
        when(configurationRepository.findAllByOrderByConfigKeyAsc()).thenReturn(allConfigs);

        // Act
        List<PcmConfiguration> result = configurationService.getAll();

        // Assert
        assertEquals(3, result.size());
        verify(configurationRepository, times(1)).findAllByOrderByConfigKeyAsc();
    }

    @Test
    void testGetByPrefix() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.sourcing.key1", "value1"),
                createConfig("pcm.sourcing.key2", "value2")
        );
        when(configurationRepository.findByConfigKeyStartingWith("pcm.sourcing")).thenReturn(configs);

        // Act
        List<PcmConfiguration> result = configurationService.getByPrefix("pcm.sourcing");

        // Assert
        assertEquals(2, result.size());
        verify(configurationRepository, times(1)).findByConfigKeyStartingWith("pcm.sourcing");
    }

    @Test
    void testSearch() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.forecast.enabled", "true"),
                createConfig("pcm.forecast.timeout", "30")
        );
        when(configurationRepository.searchByKeyOrDescription("forecast")).thenReturn(configs);

        // Act
        List<PcmConfiguration> result = configurationService.search("forecast");

        // Assert
        assertEquals(2, result.size());
        verify(configurationRepository, times(1)).searchByKeyOrDescription("forecast");
    }

    @Test
    void testGetActiveCount() {
        // Arrange
        when(configurationRepository.countByIsActiveTrue()).thenReturn(100L);

        // Act
        long count = configurationService.getActiveCount();

        // Assert
        assertEquals(100L, count);
        verify(configurationRepository, times(1)).countByIsActiveTrue();
    }

    // ========================================================================
    // Write Operations Tests
    // ========================================================================

    @Test
    void testSave_NewConfiguration() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.new.key", "new.value");
        when(configurationRepository.save(any(PcmConfiguration.class))).thenReturn(config);
        when(configUtil.refreshKey("pcm.new.key")).thenReturn(true);

        // Act
        PcmConfiguration result = configurationService.save(config);

        // Assert
        assertNotNull(result);
        assertEquals("new.value", result.getConfigValue());
        verify(configurationRepository, times(1)).save(config);
        verify(configUtil, times(1)).refreshKey("pcm.new.key");
    }

    @Test
    void testUpdateValue_Success() {
        // Arrange
        when(configurationRepository.updateValueByKey("pcm.test.key", "updated.value", "admin")).thenReturn(1);
        when(configUtil.refreshKey("pcm.test.key")).thenReturn(true);

        // Act
        boolean result = configurationService.updateValue("pcm.test.key", "updated.value", "admin");

        // Assert
        assertTrue(result);
        verify(configurationRepository, times(1)).updateValueByKey("pcm.test.key", "updated.value", "admin");
        verify(configUtil, times(1)).refreshKey("pcm.test.key");
    }

    @Test
    void testUpdateValue_KeyNotFound() {
        // Arrange
        when(configurationRepository.updateValueByKey("non.existing", "value", "admin")).thenReturn(0);

        // Act
        boolean result = configurationService.updateValue("non.existing", "value", "admin");

        // Assert
        assertFalse(result);
        verify(configUtil, never()).refreshKey(anyString());
    }

    @Test
    void testCreate_Success() {
        // Arrange
        when(configurationRepository.existsByConfigKey("pcm.new.key")).thenReturn(false);
        PcmConfiguration savedConfig = createConfig("pcm.new.key", "value");
        when(configurationRepository.save(any(PcmConfiguration.class))).thenReturn(savedConfig);
        when(configUtil.refreshKey("pcm.new.key")).thenReturn(true);

        // Act
        PcmConfiguration result = configurationService.create(
                "pcm.new.key", "value", "Description", "STRING", "admin"
        );

        // Assert
        assertNotNull(result);
        assertEquals("pcm.new.key", result.getConfigKey());
        verify(configurationRepository, times(1)).save(any(PcmConfiguration.class));
    }

    @Test
    void testCreate_DuplicateKey_ThrowsException() {
        // Arrange
        when(configurationRepository.existsByConfigKey("pcm.existing.key")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> configurationService.create("pcm.existing.key", "value", "Desc", "STRING", "admin")
        );
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testCreate_WithNullValueType_DefaultsToString() {
        // Arrange
        when(configurationRepository.existsByConfigKey("pcm.new.key")).thenReturn(false);
        PcmConfiguration savedConfig = PcmConfiguration.builder()
                .configKey("pcm.new.key")
                .configValue("value")
                .valueType("STRING")
                .build();
        when(configurationRepository.save(any(PcmConfiguration.class))).thenReturn(savedConfig);

        // Act
        PcmConfiguration result = configurationService.create(
                "pcm.new.key", "value", "Description", null, "admin"
        );

        // Assert
        assertEquals("STRING", result.getValueType());
    }

    @Test
    void testDeactivate_Success() {
        // Arrange
        when(configurationRepository.deactivateByKey("pcm.test.key", "admin")).thenReturn(1);
        when(configUtil.refreshKey("pcm.test.key")).thenReturn(false);

        // Act
        boolean result = configurationService.deactivate("pcm.test.key", "admin");

        // Assert
        assertTrue(result);
        verify(configurationRepository, times(1)).deactivateByKey("pcm.test.key", "admin");
        verify(configUtil, times(1)).refreshKey("pcm.test.key");
    }

    @Test
    void testDeactivate_KeyNotFound() {
        // Arrange
        when(configurationRepository.deactivateByKey("non.existing", "admin")).thenReturn(0);

        // Act
        boolean result = configurationService.deactivate("non.existing", "admin");

        // Assert
        assertFalse(result);
        verify(configUtil, never()).refreshKey(anyString());
    }

    @Test
    void testDelete() {
        // Arrange
        doNothing().when(configurationRepository).deleteByConfigKey("pcm.test.key");

        // Act
        configurationService.delete("pcm.test.key");

        // Assert
        verify(configurationRepository, times(1)).deleteByConfigKey("pcm.test.key");
        verify(configUtil, times(1)).refreshKey("pcm.test.key");
    }

    // ========================================================================
    // Cache Operations Tests
    // ========================================================================

    @Test
    void testRefreshCache() {
        // Arrange
        when(configUtil.refreshCache()).thenReturn(50);

        // Act
        int result = configurationService.refreshCache();

        // Assert
        assertEquals(50, result);
        verify(configUtil, times(1)).refreshCache();
    }

    @Test
    void testRefreshKey() {
        // Arrange
        when(configUtil.refreshKey("pcm.test.key")).thenReturn(true);

        // Act
        boolean result = configurationService.refreshKey("pcm.test.key");

        // Assert
        assertTrue(result);
        verify(configUtil, times(1)).refreshKey("pcm.test.key");
    }

    @Test
    void testRefreshByPrefix() {
        // Arrange
        when(configUtil.refreshByPrefix("pcm.sourcing")).thenReturn(10);

        // Act
        int result = configurationService.refreshByPrefix("pcm.sourcing");

        // Assert
        assertEquals(10, result);
        verify(configUtil, times(1)).refreshByPrefix("pcm.sourcing");
    }

    @Test
    void testClearCache() {
        // Arrange
        doNothing().when(configUtil).clearCache();

        // Act
        configurationService.clearCache();

        // Assert
        verify(configUtil, times(1)).clearCache();
    }

    @Test
    void testGetCacheStats() {
        // Arrange
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("cacheSize", 100);
        stats.put("cacheInitialized", true);
        when(configUtil.getCacheStats()).thenReturn(stats);

        // Act
        Map<String, Object> result = configurationService.getCacheStats();

        // Assert
        assertEquals(100, result.get("cacheSize"));
        assertTrue((Boolean) result.get("cacheInitialized"));
        verify(configUtil, times(1)).getCacheStats();
    }

    // ========================================================================
    // Validation Tests
    // ========================================================================

    @Test
    void testExists_True() {
        // Arrange
        when(configurationRepository.existsByConfigKey("pcm.existing.key")).thenReturn(true);

        // Act
        boolean result = configurationService.exists("pcm.existing.key");

        // Assert
        assertTrue(result);
    }

    @Test
    void testExists_False() {
        // Arrange
        when(configurationRepository.existsByConfigKey("non.existing")).thenReturn(false);

        // Act
        boolean result = configurationService.exists("non.existing");

        // Assert
        assertFalse(result);
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private PcmConfiguration createConfig(String key, String value) {
        return PcmConfiguration.builder()
                .id(1L)
                .configKey(key)
                .configValue(value)
                .valueType("STRING")
                .isActive(true)
                .description("Test configuration")
                .createdDate(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build();
    }
}
