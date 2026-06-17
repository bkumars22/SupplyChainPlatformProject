/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.controller;

import com.scplatform.pcm.config.entity.PcmConfiguration;
import com.scplatform.pcm.config.service.PcmConfigurationService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing PCM configurations and cache.
 * Provides endpoints for:
 * - Cache management (reload, refresh, clear)
 * - Configuration CRUD operations
 * - Configuration lookup and search
 */
@RestController
@RequestMapping("/api/config")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "PCM Configuration", description = "APIs for managing PCM configurations and cache")
public class PcmConfigurationController {

    private final PcmConfigurationService configurationService;
    private final PcmConfigUtil configUtil;

    // ========================================================================
    // Cache Management Endpoints
    // ========================================================================

    /**
     * Reload/Refresh the entire configuration cache from database
     * 
     * GET /api/config/cache/reload
     */
    @Operation(summary = "Reload cache", description = "Reload the entire configuration cache from database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cache reloaded successfully")
    })
    @GetMapping("/cache/reload")
    public ResponseEntity<Map<String, Object>> reloadCache() {
        log.info("Received request to reload configuration cache");
        int count = configurationService.refreshCache();
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "Configuration cache reloaded successfully");
        response.put("configurationsLoaded", count);
        response.put("cacheStats", configurationService.getCacheStats());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh a specific configuration key in cache
     * 
     * GET /api/config/cache/refresh?key=pcm.some.key
     */
    @Operation(summary = "Refresh specific key", description = "Refresh a specific configuration key in cache")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Key refresh status returned")
    })
    @GetMapping("/cache/refresh")
    public ResponseEntity<Map<String, Object>> refreshKey(
            @Parameter(description = "Configuration key to refresh") @RequestParam String key) {
        log.info("Received request to refresh configuration key: {}", key);
        boolean refreshed = configurationService.refreshKey(key);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", refreshed ? "success" : "not_found");
        response.put("key", key);
        response.put("message", refreshed ? "Configuration key refreshed" : "Configuration key not found or inactive");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh configurations by prefix in cache
     * 
     * GET /api/config/cache/refresh/prefix?prefix=pcm.sourcingLane
     */
    @Operation(summary = "Refresh by prefix", description = "Refresh configurations matching a prefix in cache")
    @GetMapping("/cache/refresh/prefix")
    public ResponseEntity<Map<String, Object>> refreshByPrefix(
            @Parameter(description = "Configuration key prefix") @RequestParam String prefix) {
        log.info("Received request to refresh configurations with prefix: {}", prefix);
        int count = configurationService.refreshByPrefix(prefix);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("prefix", prefix);
        response.put("configurationsRefreshed", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear the configuration cache
     * 
     * POST /api/config/cache/clear
     */
    @Operation(summary = "Clear cache", description = "Clear the entire configuration cache")
    @PostMapping("/cache/clear")
    public ResponseEntity<Map<String, Object>> clearCache() {
        log.info("Received request to clear configuration cache");
        configurationService.clearCache();
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "Configuration cache cleared");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get cache statistics
     * 
     * GET /api/config/cache/stats
     */
    @Operation(summary = "Get cache stats", description = "Get cache statistics including size and initialization status")
    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        return ResponseEntity.ok(configurationService.getCacheStats());
    }

    // ========================================================================
    // Configuration Lookup Endpoints
    // ========================================================================

    /**
     * Get a specific configuration by key
     * 
     * GET /api/config/key/{key}
     */
    @Operation(summary = "Get by key", description = "Get a specific configuration by key")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found"),
        @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    @GetMapping("/key/{key}")
    public ResponseEntity<?> getByKey(@PathVariable String key) {
        Optional<PcmConfiguration> config = configurationService.getByKey(key);
        if (config.isPresent()) {
            return ResponseEntity.ok(config.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get configuration value only by key
     * 
     * GET /api/config/value/{key}
     */
    @Operation(summary = "Get value by key", description = "Get configuration value only by key")
    @GetMapping("/value/{key}")
    public ResponseEntity<Map<String, String>> getValueByKey(@PathVariable String key) {
        String value = configUtil.getString(key);
        if (value != null) {
            Map<String, String> response = new LinkedHashMap<>();
            response.put("key", key);
            response.put("value", value);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get configurations by prefix
     * 
     * GET /api/config/prefix/{prefix}
     */
    @Operation(summary = "Get by prefix", description = "Get all configurations matching a key prefix")
    @GetMapping("/prefix/{prefix}")
    public ResponseEntity<Map<String, String>> getByPrefix(@PathVariable String prefix) {
        Map<String, String> configs = configUtil.getByPrefix(prefix);
        return ResponseEntity.ok(configs);
    }

    /**
     * Get all configurations
     * 
     * GET /api/config/all
     */
    @Operation(summary = "Get all", description = "Get all configurations including inactive")
    @GetMapping("/all")
    public ResponseEntity<List<PcmConfiguration>> getAll() {
        return ResponseEntity.ok(configurationService.getAll());
    }

    /**
     * Get all active configurations
     * 
     * GET /api/config/active
     */
    @Operation(summary = "Get active", description = "Get all active configurations")
    @GetMapping("/active")
    public ResponseEntity<List<PcmConfiguration>> getAllActive() {
        return ResponseEntity.ok(configurationService.getAllActive());
    }

    /**
     * Search configurations by key or description
     * 
     * GET /api/config/search?q=searchTerm
     */
    @Operation(summary = "Search", description = "Search configurations by key or description")
    @GetMapping("/search")
    public ResponseEntity<List<PcmConfiguration>> search(
            @Parameter(description = "Search term") @RequestParam String q) {
        return ResponseEntity.ok(configurationService.search(q));
    }

    /**
     * Get all configuration keys
     * 
     * GET /api/config/keys
     */
    @Operation(summary = "Get all keys", description = "Get all configuration keys from cache")
    @GetMapping("/keys")
    public ResponseEntity<?> getAllKeys() {
        return ResponseEntity.ok(configUtil.getAllKeys());
    }

    /**
     * Get count of active configurations
     * 
     * GET /api/config/count
     */
    @Operation(summary = "Get count", description = "Get count of active configurations in database and cache")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCount() {
        Map<String, Long> response = new LinkedHashMap<>();
        response.put("activeCount", configurationService.getActiveCount());
        response.put("cacheCount", (long) configUtil.getConfigurationCount());
        return ResponseEntity.ok(response);
    }

    // ========================================================================
    // Configuration CRUD Endpoints
    // ========================================================================

    /**
     * Create a new configuration
     * 
     * POST /api/config
     */
    @Operation(summary = "Create configuration", description = "Create a new configuration entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Configuration created"),
        @ApiResponse(responseCode = "409", description = "Configuration already exists"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PcmConfiguration config) {
        try {
            if (configurationService.exists(config.getConfigKey())) {
                Map<String, String> error = new LinkedHashMap<>();
                error.put("error", "Configuration with key '" + config.getConfigKey() + "' already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            PcmConfiguration saved = configurationService.save(config);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error creating configuration", e);
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing configuration
     * 
     * PUT /api/config/{id}
     */
    @Operation(summary = "Update configuration", description = "Update an existing configuration by ID")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PcmConfiguration config) {
        try {
            config.setId(id);
            PcmConfiguration saved = configurationService.save(config);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error updating configuration", e);
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update configuration value by key
     * 
     * PATCH /api/config/key/{key}
     */
    @Operation(summary = "Update value", description = "Update configuration value by key")
    @PatchMapping("/key/{key}")
    public ResponseEntity<Map<String, Object>> updateValue(
            @PathVariable String key,
            @RequestParam String value,
            @RequestParam(required = false, defaultValue = "system") String modifiedBy) {
        
        boolean updated = configurationService.updateValue(key, value, modifiedBy);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("key", key);
        response.put("updated", updated);
        response.put("value", value);
        
        if (updated) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deactivate a configuration
     * 
     * POST /api/config/deactivate/{key}
     */
    @Operation(summary = "Deactivate", description = "Deactivate a configuration by key")
    @PostMapping("/deactivate/{key}")
    public ResponseEntity<Map<String, Object>> deactivate(
            @PathVariable String key,
            @RequestParam(required = false, defaultValue = "system") String modifiedBy) {
        
        boolean deactivated = configurationService.deactivate(key, modifiedBy);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("key", key);
        response.put("deactivated", deactivated);
        
        if (deactivated) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a configuration
     * 
     * DELETE /api/config/{key}
     */
    @Operation(summary = "Delete", description = "Delete a configuration by key")
    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String key) {
        if (!configurationService.exists(key)) {
            return ResponseEntity.notFound().build();
        }
        
        configurationService.delete(key);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("key", key);
        response.put("deleted", true);
        
        return ResponseEntity.ok(response);
    }

    // ========================================================================
    // Utility Endpoints
    // ========================================================================

    /**
     * Check if a configuration key exists
     * 
     * GET /api/config/exists/{key}
     */
    @Operation(summary = "Check exists", description = "Check if a configuration key exists in database and cache")
    @GetMapping("/exists/{key}")
    public ResponseEntity<Map<String, Object>> exists(@PathVariable String key) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("key", key);
        response.put("exists", configurationService.exists(key));
        response.put("inCache", configUtil.containsKey(key));
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     * 
     * GET /api/config/health
     */
    @Operation(summary = "Health check", description = "Health check endpoint for configuration service")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "PcmConfigurationService");
        response.put("cacheStats", configurationService.getCacheStats());
        return ResponseEntity.ok(response);
    }
}
