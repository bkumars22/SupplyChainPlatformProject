/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.controller;

import com.scplatform.pcm.config.entity.PcmConfiguration;
import com.scplatform.pcm.config.service.PcmConfigurationService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PcmConfigurationControllerTest {

    @Mock
    private PcmConfigurationService configurationService;

    @Mock
    private PcmConfigUtil configUtil;

    @InjectMocks
    private PcmConfigurationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ========================================================================
    // Cache Management Endpoint Tests
    // ========================================================================

    @Test
    void testReloadCache_Success() {
        // Arrange
        when(configurationService.refreshCache()).thenReturn(100);
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("cacheSize", 100);
        when(configurationService.getCacheStats()).thenReturn(stats);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.reloadCache();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(100, response.getBody().get("configurationsLoaded"));
        verify(configurationService, times(1)).refreshCache();
    }

    @Test
    void testRefreshKey_ExistingKey() {
        // Arrange
        when(configurationService.refreshKey("pcm.test.key")).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.refreshKey("pcm.test.key");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("pcm.test.key", response.getBody().get("key"));
    }

    @Test
    void testRefreshKey_NonExistingKey() {
        // Arrange
        when(configurationService.refreshKey("non.existing")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.refreshKey("non.existing");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("not_found", response.getBody().get("status"));
    }

    @Test
    void testRefreshByPrefix_Success() {
        // Arrange
        when(configurationService.refreshByPrefix("pcm.sourcing")).thenReturn(15);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.refreshByPrefix("pcm.sourcing");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(15, response.getBody().get("configurationsRefreshed"));
    }

    @Test
    void testClearCache_Success() {
        // Arrange
        doNothing().when(configurationService).clearCache();

        // Act
        ResponseEntity<Map<String, Object>> response = controller.clearCache();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        verify(configurationService, times(1)).clearCache();
    }

    @Test
    void testGetCacheStats() {
        // Arrange
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("cacheSize", 100);
        stats.put("cacheInitialized", true);
        when(configurationService.getCacheStats()).thenReturn(stats);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getCacheStats();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100, response.getBody().get("cacheSize"));
    }

    // ========================================================================
    // Configuration Lookup Endpoint Tests
    // ========================================================================

    @Test
    void testGetByKey_Found() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.test.key", "test.value");
        when(configurationService.getByKey("pcm.test.key")).thenReturn(Optional.of(config));

        // Act
        ResponseEntity<?> response = controller.getByKey("pcm.test.key");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PcmConfiguration result = (PcmConfiguration) response.getBody();
        assertEquals("test.value", result.getConfigValue());
    }

    @Test
    void testGetByKey_NotFound() {
        // Arrange
        when(configurationService.getByKey("non.existing")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = controller.getByKey("non.existing");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetValueByKey_Found() {
        // Arrange
        when(configUtil.getString("pcm.test.key")).thenReturn("test.value");

        // Act
        ResponseEntity<Map<String, String>> response = controller.getValueByKey("pcm.test.key");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("pcm.test.key", response.getBody().get("key"));
        assertEquals("test.value", response.getBody().get("value"));
    }

    @Test
    void testGetValueByKey_NotFound() {
        // Arrange
        when(configUtil.getString("non.existing")).thenReturn(null);

        // Act
        ResponseEntity<Map<String, String>> response = controller.getValueByKey("non.existing");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetByPrefix() {
        // Arrange
        Map<String, String> configs = new LinkedHashMap<>();
        configs.put("pcm.sourcing.key1", "value1");
        configs.put("pcm.sourcing.key2", "value2");
        when(configUtil.getByPrefix("pcm.sourcing")).thenReturn(configs);

        // Act
        ResponseEntity<Map<String, String>> response = controller.getByPrefix("pcm.sourcing");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAll() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("key1", "value1"),
                createConfig("key2", "value2")
        );
        when(configurationService.getAll()).thenReturn(configs);

        // Act
        ResponseEntity<List<PcmConfiguration>> response = controller.getAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllActive() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("active1", "value1"),
                createConfig("active2", "value2")
        );
        when(configurationService.getAllActive()).thenReturn(configs);

        // Act
        ResponseEntity<List<PcmConfiguration>> response = controller.getAllActive();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testSearch() {
        // Arrange
        List<PcmConfiguration> configs = List.of(
                createConfig("pcm.forecast.enabled", "true")
        );
        when(configurationService.search("forecast")).thenReturn(configs);

        // Act
        ResponseEntity<List<PcmConfiguration>> response = controller.search("forecast");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAllKeys() {
        // Arrange
        Set<String> keys = new TreeSet<>(Set.of("key1", "key2", "key3"));
        when(configUtil.getAllKeys()).thenReturn(keys);

        // Act
        ResponseEntity<?> response = controller.getAllKeys();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetCount() {
        // Arrange
        when(configurationService.getActiveCount()).thenReturn(100L);
        when(configUtil.getConfigurationCount()).thenReturn(100);

        // Act
        ResponseEntity<Map<String, Long>> response = controller.getCount();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100L, response.getBody().get("activeCount"));
        assertEquals(100L, response.getBody().get("cacheCount"));
    }

    // ========================================================================
    // CRUD Endpoint Tests
    // ========================================================================

    @Test
    void testCreate_Success() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.new.key", "new.value");
        when(configurationService.exists("pcm.new.key")).thenReturn(false);
        when(configurationService.save(any(PcmConfiguration.class))).thenReturn(config);

        // Act
        ResponseEntity<?> response = controller.create(config);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        PcmConfiguration result = (PcmConfiguration) response.getBody();
        assertEquals("new.value", result.getConfigValue());
    }

    @Test
    void testCreate_DuplicateKey_Conflict() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.existing.key", "value");
        when(configurationService.exists("pcm.existing.key")).thenReturn(true);

        // Act
        ResponseEntity<?> response = controller.create(config);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testCreate_Exception_BadRequest() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.new.key", "value");
        when(configurationService.exists("pcm.new.key")).thenReturn(false);
        when(configurationService.save(any(PcmConfiguration.class))).thenThrow(new RuntimeException("DB Error"));

        // Act
        ResponseEntity<?> response = controller.create(config);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.test.key", "updated.value");
        config.setId(1L);
        when(configurationService.save(any(PcmConfiguration.class))).thenReturn(config);

        // Act
        ResponseEntity<?> response = controller.update(1L, config);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdate_Exception_BadRequest() {
        // Arrange
        PcmConfiguration config = createConfig("pcm.test.key", "value");
        when(configurationService.save(any(PcmConfiguration.class))).thenThrow(new RuntimeException("Error"));

        // Act
        ResponseEntity<?> response = controller.update(1L, config);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateValue_Success() {
        // Arrange
        when(configurationService.updateValue("pcm.test.key", "new.value", "admin")).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateValue("pcm.test.key", "new.value", "admin");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("updated"));
    }

    @Test
    void testUpdateValue_NotFound() {
        // Arrange
        when(configurationService.updateValue("non.existing", "value", "admin")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateValue("non.existing", "value", "admin");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeactivate_Success() {
        // Arrange
        when(configurationService.deactivate("pcm.test.key", "admin")).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deactivate("pcm.test.key", "admin");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("deactivated"));
    }

    @Test
    void testDeactivate_NotFound() {
        // Arrange
        when(configurationService.deactivate("non.existing", "admin")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deactivate("non.existing", "admin");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDelete_Success() {
        // Arrange
        when(configurationService.exists("pcm.test.key")).thenReturn(true);
        doNothing().when(configurationService).delete("pcm.test.key");

        // Act
        ResponseEntity<Map<String, Object>> response = controller.delete("pcm.test.key");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("deleted"));
    }

    @Test
    void testDelete_NotFound() {
        // Arrange
        when(configurationService.exists("non.existing")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.delete("non.existing");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ========================================================================
    // Utility Endpoint Tests
    // ========================================================================

    @Test
    void testExists_KeyExists() {
        // Arrange
        when(configurationService.exists("pcm.test.key")).thenReturn(true);
        when(configUtil.containsKey("pcm.test.key")).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.exists("pcm.test.key");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("exists"));
        assertTrue((Boolean) response.getBody().get("inCache"));
    }

    @Test
    void testExists_KeyNotExists() {
        // Arrange
        when(configurationService.exists("non.existing")).thenReturn(false);
        when(configUtil.containsKey("non.existing")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.exists("non.existing");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("exists"));
        assertFalse((Boolean) response.getBody().get("inCache"));
    }

    @Test
    void testHealth() {
        // Arrange
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("cacheSize", 50);
        when(configurationService.getCacheStats()).thenReturn(stats);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.health();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("PcmConfigurationService", response.getBody().get("service"));
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
