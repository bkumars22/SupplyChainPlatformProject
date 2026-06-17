/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.scplatform.pcm.config.entity.PcmConfiguration;
import com.scplatform.pcm.config.repository.PcmConfigurationRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for accessing PCM configuration from database with caching support.
 * <p>
 * This utility maintains an in-memory cache of configuration values to reduce
 * database calls. The cache can be refreshed on-demand or for specific keys.
 * <p>
 * Supported value types:
 * - STRING: Plain string values
 * - BOOLEAN: true/false values
 * - LIST: Comma-separated values
 * - INTEGER: Integer numbers
 * - DOUBLE: Decimal numbers
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PcmConfigUtil {

    private final PcmConfigurationRepository configurationRepository;

    /**
     * In-memory cache storing configuration key-value pairs
     */
    private final Map<String, PcmConfiguration> configCache = new ConcurrentHashMap<>();

    /**
     * Read-write lock for thread-safe cache operations
     */
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

    /**
     * Flag indicating if cache is initialized
     */
    private volatile boolean cacheInitialized = false;

    /**
     * Timestamp of last cache refresh
     */
    private volatile long lastCacheRefreshTime = 0;

    /**
     * Pattern to match placeholders like ${key.name}
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * Maximum depth for recursive placeholder resolution to prevent infinite loops
     */
    private static final int MAX_RESOLUTION_DEPTH = 10;

    // ========================================================================
    // Cache Management
    // ========================================================================

    /**
     * Initialize cache on application startup
     */
    @PostConstruct
    public void initializeCache() {
        log.info("Initializing PCM configuration cache...");
        refreshCache();
        log.info("PCM configuration cache initialized with {} entries", configCache.size());
    }

    /**
     * Refresh the entire cache by reloading all configurations from database
     *
     * @return number of configurations loaded
     */
    public int refreshCache() {
        cacheLock.writeLock().lock();
        try {
            log.info("Refreshing PCM configuration cache...");
            configCache.clear();

            List<PcmConfiguration> configs = configurationRepository.findByIsActiveTrue();
            for (PcmConfiguration config : configs) {
                configCache.put(config.getConfigKey(), config);
            }

            cacheInitialized = true;
            lastCacheRefreshTime = System.currentTimeMillis();

            log.info("PCM configuration cache refreshed successfully. Loaded {} configurations", configs.size());
            return configs.size();
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * Refresh a specific configuration key in cache
     *
     * @param configKey the configuration key to refresh
     * @return true if key was found and refreshed, false otherwise
     */
    public boolean refreshKey(String configKey) {
        cacheLock.writeLock().lock();
        try {
            log.debug("Refreshing configuration key: {}", configKey);
            Optional<PcmConfiguration> config = configurationRepository.findByConfigKey(configKey);

            if (config.isPresent() && config.get().getIsActive()) {
                configCache.put(configKey, config.get());
                log.debug("Configuration key refreshed: {}", configKey);
                return true;
            } else {
                configCache.remove(configKey);
                log.debug("Configuration key removed from cache (not found or inactive): {}", configKey);
                return false;
            }
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * Refresh configurations matching a key prefix
     *
     * @param prefix the key prefix to match
     * @return number of configurations refreshed
     */
    public int refreshByPrefix(String prefix) {
        cacheLock.writeLock().lock();
        try {
            log.info("Refreshing configurations with prefix: {}", prefix);

            // Remove existing entries with this prefix
            configCache.keySet().removeIf(key -> key.startsWith(prefix));

            // Load fresh from database
            List<PcmConfiguration> configs = configurationRepository.findByConfigKeyStartingWith(prefix);
            for (PcmConfiguration config : configs) {
                configCache.put(config.getConfigKey(), config);
            }

            log.info("Refreshed {} configurations with prefix: {}", configs.size(), prefix);
            return configs.size();
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * Clear the entire cache (forces reload on next access)
     */
    public void clearCache() {
        cacheLock.writeLock().lock();
        try {
            configCache.clear();
            cacheInitialized = false;
            log.info("PCM configuration cache cleared");
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * Get cache statistics
     *
     * @return map containing cache statistics
     */
    public Map<String, Object> getCacheStats() {
        cacheLock.readLock().lock();
        try {
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("cacheSize", configCache.size());
            stats.put("cacheInitialized", cacheInitialized);
            stats.put("lastRefreshTime", lastCacheRefreshTime > 0 ? new Date(lastCacheRefreshTime) : null);
            stats.put("cacheAgeMillis", lastCacheRefreshTime > 0 ? System.currentTimeMillis() - lastCacheRefreshTime : -1);
            return stats;
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    // ========================================================================
    // Placeholder Resolution
    // ========================================================================

    /**
     * Resolve placeholders in configuration value.
     * Placeholders follow format: ${key.name}
     * Example: ${pcm.ssp_project_config.dir}/search/ will resolve the referenced key
     *
     * @param value the value potentially containing placeholders
     * @return the value with all placeholders resolved
     */
    private String resolvePlaceholders(String value) {
        return resolvePlaceholders(value, 0, new java.util.HashSet<>());
    }

    /**
     * Resolve placeholders recursively with depth tracking to prevent infinite loops
     *
     * @param value the value potentially containing placeholders
     * @param depth current recursion depth
     * @param visited set of visited keys to prevent circular references
     * @return the value with all placeholders resolved
     */
    private String resolvePlaceholders(String value, int depth, Set<String> visited) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // Prevent infinite recursion
        if (depth > MAX_RESOLUTION_DEPTH) {
            log.warn("Maximum placeholder resolution depth ({}) exceeded. Possible circular reference.", MAX_RESOLUTION_DEPTH);
            return value;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholderKey = matcher.group(1);

            // Prevent circular references
            if (visited.contains(placeholderKey)) {
                log.warn("Circular reference detected for key: {}", placeholderKey);
                matcher.appendReplacement(result, Matcher.quoteReplacement("${" + placeholderKey + "}"));
                continue;
            }

            PcmConfiguration config = configCache.get(placeholderKey);
            if (config != null && config.getConfigValue() != null && config.getIsActive()) {
                String resolvedValue = config.getConfigValue();

                // Add current key to visited set for next recursion
                Set<String> newVisited = new java.util.HashSet<>(visited);
                newVisited.add(placeholderKey);

                // Recursively resolve nested placeholders
                resolvedValue = resolvePlaceholders(resolvedValue, depth + 1, newVisited);
                matcher.appendReplacement(result, Matcher.quoteReplacement(resolvedValue));

                log.debug("Resolved placeholder ${} to: {}", placeholderKey, resolvedValue);
            } else {
                log.warn("Configuration key not found or inactive: {}", placeholderKey);
                // Keep the placeholder as-is if key not found
                matcher.appendReplacement(result, Matcher.quoteReplacement("${" + placeholderKey + "}"));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    // ========================================================================
    // Configuration Getters - String Values
    // ========================================================================

    /**
     * Get configuration value as String
     *
     * @param key the configuration key
     * @return the configuration value, or null if not found
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * Get configuration value as String with default
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value with placeholders resolved, or defaultValue if not found
     */
    public String getString(String key, String defaultValue) {
        ensureCacheInitialized();
        cacheLock.readLock().lock();
        try {
            PcmConfiguration config = configCache.get(key);
            if (config != null && config.getConfigValue() != null) {
                String resolvedValue = resolvePlaceholders(config.getConfigValue());
                return resolvedValue;
            }
            return defaultValue;
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    // ========================================================================
    // Configuration Getters - Boolean Values
    // ========================================================================

    /**
     * Get configuration value as Boolean
     *
     * @param key the configuration key
     * @return the configuration value as boolean, or null if not found
     */
    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * Get configuration value as Boolean with default
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value as boolean
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    /**
     * Get configuration value as primitive boolean
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value as primitive boolean
     */
    public boolean getBooleanValue(String key, boolean defaultValue) {
        Boolean result = getBoolean(key, defaultValue);
        return result != null ? result : defaultValue;
    }

    // ========================================================================
    // Configuration Getters - List Values (Comma-separated)
    // ========================================================================

    /**
     * Get configuration value as List of Strings (comma-separated)
     *
     * @param key the configuration key
     * @return list of values, or empty list if not found
     */
    public List<String> getList(String key) {
        return getList(key, ",");
    }

    /**
     * Get configuration value as List of Strings with custom delimiter
     *
     * @param key       the configuration key
     * @param delimiter the delimiter to split values
     * @return list of values, or empty list if not found
     */
    public List<String> getList(String key, String delimiter) {
        String value = getString(key);
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(delimiter))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Get configuration value as List of Strings with default value (comma-separated)
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return list of values, or defaultValue if not found
     */
    public List<String> getList(String key, List<String> defaultValue) {
        return getList(key, ",", defaultValue);
    }

    /**
     * Get configuration value as List of Strings with custom delimiter and default value
     *
     * @param key          the configuration key
     * @param delimiter    the delimiter to split values
     * @param defaultValue the default value if key not found
     * @return list of values, or defaultValue if not found
     */
    public List<String> getList(String key, String delimiter, List<String> defaultValue) {
        String value = getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Arrays.stream(value.split(delimiter))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Get configuration value as Set of Strings (comma-separated, unique values)
     *
     * @param key the configuration key
     * @return set of unique values, or empty set if not found
     */
    public Set<String> getSet(String key) {
        return new LinkedHashSet<>(getList(key));
    }

    // ========================================================================
    // Configuration Getters - Numeric Values
    // ========================================================================

    /**
     * Get configuration value as Integer
     *
     * @param key the configuration key
     * @return the configuration value as Integer, or null if not found or invalid
     */
    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    /**
     * Get configuration value as Integer with default
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value as Integer
     */
    public Integer getInteger(String key, Integer defaultValue) {
        String value = getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value for key '{}': {}", key, value);
            return defaultValue;
        }
    }

    /**
     * Get configuration value as primitive int
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value as primitive int
     */
    public int getIntValue(String key, int defaultValue) {
        Integer result = getInteger(key, defaultValue);
        return result != null ? result : defaultValue;
    }

    /**
     * Get configuration value as Long
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value as Long
     */
    public Long getLong(String key, Long defaultValue) {
        String value = getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid long value for key '{}': {}", key, value);
            return defaultValue;
        }
    }

    /**
     * Get configuration value as Double
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key not found
     * @return the configuration value as Double
     */
    public Double getDouble(String key, Double defaultValue) {
        String value = getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid double value for key '{}': {}", key, value);
            return defaultValue;
        }
    }

    // ========================================================================
    // Configuration Lookup and Search
    // ========================================================================

    /**
     * Get the full configuration object for a key
     *
     * @param key the configuration key
     * @return Optional containing the configuration, or empty if not found
     */
    public Optional<PcmConfiguration> getConfiguration(String key) {
        ensureCacheInitialized();
        cacheLock.readLock().lock();
        try {
            return Optional.ofNullable(configCache.get(key));
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Get all configurations matching a key prefix
     *
     * @param prefix the key prefix to match
     * @return map of matching key-value pairs
     */
    public Map<String, String> getByPrefix(String prefix) {
        ensureCacheInitialized();
        cacheLock.readLock().lock();
        try {
            return configCache.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(prefix))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().getConfigValue() != null ? e.getValue().getConfigValue() : "",
                            (v1, v2) -> v1,
                            LinkedHashMap::new
                    ));
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Get all configurations matching a pattern (supports * wildcard)
     *
     * @param pattern the pattern to match (e.g., "pcm.*.enabled")
     * @return map of matching key-value pairs
     */
    public Map<String, String> getByPattern(String pattern) {
        ensureCacheInitialized();
        String regex = pattern.replace(".", "\\.").replace("*", ".*");
        cacheLock.readLock().lock();
        try {
            return configCache.entrySet().stream()
                    .filter(e -> e.getKey().matches(regex))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().getConfigValue() != null ? e.getValue().getConfigValue() : "",
                            (v1, v2) -> v1,
                            LinkedHashMap::new
                    ));
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Check if a configuration key exists
     *
     * @param key the configuration key
     * @return true if key exists in cache
     */
    public boolean containsKey(String key) {
        ensureCacheInitialized();
        cacheLock.readLock().lock();
        try {
            return configCache.containsKey(key);
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Get all configuration keys
     *
     * @return set of all configuration keys
     */
    public Set<String> getAllKeys() {
        ensureCacheInitialized();
        cacheLock.readLock().lock();
        try {
            return new TreeSet<>(configCache.keySet());
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Get all configurations as a map
     *
     * @return map of all key-value pairs
     */
    public Map<String, String> getAllConfigurations() {
        ensureCacheInitialized();
        cacheLock.readLock().lock();
        try {
            return configCache.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().getConfigValue() != null ? e.getValue().getConfigValue() : "",
                            (v1, v2) -> v1,
                            LinkedHashMap::new
                    ));
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Get total number of configurations in cache
     *
     * @return number of configurations
     */
    public int getConfigurationCount() {
        cacheLock.readLock().lock();
        try {
            return configCache.size();
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Get all configurations as Properties object
     *
     * @return Properties object containing all key-value pairs
     */
    public Properties getProperties() {
        ensureCacheInitialized();
        Properties properties = new Properties();
        cacheLock.readLock().lock();
        try {
            configCache.forEach((key, config) -> {
                if (config.getConfigValue() != null) {
                    properties.setProperty(key, config.getConfigValue());
                }
            });
        } finally {
            cacheLock.readLock().unlock();
        }
        return properties;
    }

    // ========================================================================
    // Private Helper Methods
    // ========================================================================

    /**
     * Ensure cache is initialized before access
     */
    private void ensureCacheInitialized() {
        if (!cacheInitialized) {
            refreshCache();
        }
    }

}
