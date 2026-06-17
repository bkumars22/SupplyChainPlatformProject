/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.service;

import com.scplatform.pcm.config.entity.PcmConfiguration;
import com.scplatform.pcm.config.repository.PcmConfigurationRepository;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing PCM configurations.
 * Provides CRUD operations and integrates with the configuration cache.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PcmConfigurationService {

    private final PcmConfigurationRepository configurationRepository;
    private final PcmConfigUtil configUtil;

    // ========================================================================
    // Read Operations
    // ========================================================================

    /**
     * Get configuration by key
     *
     * @param key the configuration key
     * @return Optional containing the configuration
     */
    public Optional<PcmConfiguration> getByKey(String key) {
        return configurationRepository.findByConfigKey(key);
    }

    /**
     * Get all active configurations
     *
     * @return list of active configurations
     */
    public List<PcmConfiguration> getAllActive() {
        return configurationRepository.findByIsActiveTrue();
    }

    /**
     * Get all configurations
     *
     * @return list of all configurations
     */
    public List<PcmConfiguration> getAll() {
        return configurationRepository.findAllByOrderByConfigKeyAsc();
    }

    /**
     * Get configurations by prefix
     *
     * @param prefix the key prefix
     * @return list of matching configurations
     */
    public List<PcmConfiguration> getByPrefix(String prefix) {
        return configurationRepository.findByConfigKeyStartingWith(prefix);
    }

    /**
     * Search configurations by key or description
     *
     * @param searchTerm the search term
     * @return list of matching configurations
     */
    public List<PcmConfiguration> search(String searchTerm) {
        return configurationRepository.searchByKeyOrDescription(searchTerm);
    }

    /**
     * Get count of active configurations
     *
     * @return count of active configurations
     */
    public long getActiveCount() {
        return configurationRepository.countByIsActiveTrue();
    }

    // ========================================================================
    // Write Operations
    // ========================================================================

    /**
     * Create or update a configuration
     *
     * @param config the configuration to save
     * @return saved configuration
     */
    @Transactional
    public PcmConfiguration save(PcmConfiguration config) {
        log.info("Saving configuration: {}", config.getConfigKey());
        PcmConfiguration saved = configurationRepository.save(config);
        // Refresh this key in cache
        configUtil.refreshKey(config.getConfigKey());
        return saved;
    }

    /**
     * Update configuration value by key
     *
     * @param key        the configuration key
     * @param value      the new value
     * @param modifiedBy the user making the change
     * @return true if updated successfully
     */
    @Transactional
    public boolean updateValue(String key, String value, String modifiedBy) {
        log.info("Updating configuration value: {} by {}", key, modifiedBy);
        int updated = configurationRepository.updateValueByKey(key, value, modifiedBy);
        if (updated > 0) {
            configUtil.refreshKey(key);
            return true;
        }
        return false;
    }

    /**
     * Create a new configuration
     *
     * @param key         the configuration key
     * @param value       the configuration value
     * @param description the description
     * @param valueType   the value type (STRING, BOOLEAN, LIST)
     * @param createdBy   the user creating the configuration
     * @return created configuration
     */
    @Transactional
    public PcmConfiguration create(String key, String value, String description, String valueType, String createdBy) {
        if (configurationRepository.existsByConfigKey(key)) {
            throw new IllegalArgumentException("Configuration with key '" + key + "' already exists");
        }

        PcmConfiguration config = PcmConfiguration.builder()
                .configKey(key)
                .configValue(value)
                .description(description)
                .valueType(valueType != null ? valueType : "STRING")
                .isActive(true)
                .createdBy(createdBy)
                .build();

        log.info("Creating new configuration: {}", key);
        PcmConfiguration saved = configurationRepository.save(config);
        configUtil.refreshKey(key);
        return saved;
    }

    /**
     * Deactivate a configuration
     *
     * @param key        the configuration key
     * @param modifiedBy the user making the change
     * @return true if deactivated successfully
     */
    @Transactional
    public boolean deactivate(String key, String modifiedBy) {
        log.info("Deactivating configuration: {} by {}", key, modifiedBy);
        int updated = configurationRepository.deactivateByKey(key, modifiedBy);
        if (updated > 0) {
            configUtil.refreshKey(key);
            return true;
        }
        return false;
    }

    /**
     * Delete a configuration
     *
     * @param key the configuration key
     */
    @Transactional
    public void delete(String key) {
        log.info("Deleting configuration: {}", key);
        configurationRepository.deleteByConfigKey(key);
        configUtil.refreshKey(key);
    }

    // ========================================================================
    // Cache Operations
    // ========================================================================

    /**
     * Refresh the entire configuration cache
     *
     * @return number of configurations loaded
     */
    public int refreshCache() {
        log.info("Refreshing entire configuration cache...");
        return configUtil.refreshCache();
    }

    /**
     * Refresh a specific key in cache
     *
     * @param key the configuration key
     * @return true if key was refreshed
     */
    public boolean refreshKey(String key) {
        return configUtil.refreshKey(key);
    }

    /**
     * Refresh configurations by prefix in cache
     *
     * @param prefix the key prefix
     * @return number of configurations refreshed
     */
    public int refreshByPrefix(String prefix) {
        return configUtil.refreshByPrefix(prefix);
    }

    /**
     * Clear the cache
     */
    public void clearCache() {
        configUtil.clearCache();
    }

    /**
     * Get cache statistics
     *
     * @return map of cache statistics
     */
    public Map<String, Object> getCacheStats() {
        return configUtil.getCacheStats();
    }

    // ========================================================================
    // Validation
    // ========================================================================

    /**
     * Check if a key exists
     *
     * @param key the configuration key
     * @return true if key exists
     */
    public boolean exists(String key) {
        return configurationRepository.existsByConfigKey(key);
    }
}
