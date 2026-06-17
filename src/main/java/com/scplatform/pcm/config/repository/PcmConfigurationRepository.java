/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.repository;

import com.scplatform.pcm.config.entity.PcmConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PCM Configuration entity.
 * Provides database operations for configuration management.
 */
@Repository
public interface PcmConfigurationRepository extends JpaRepository<PcmConfiguration, Long> {

    /**
     * Find configuration by its unique key
     */
    Optional<PcmConfiguration> findByConfigKey(String configKey);

    /**
     * Find all active configurations
     */
    List<PcmConfiguration> findByIsActiveTrue();

    /**
     * Find all configurations by key prefix (e.g., "pcm.sourcingLane.")
     */
    @Query("SELECT c FROM PcmConfiguration c WHERE c.configKey LIKE :prefix% AND c.isActive = true")
    List<PcmConfiguration> findByConfigKeyStartingWith(@Param("prefix") String prefix);

    /**
     * Find configuration by key pattern using LIKE
     */
    @Query("SELECT c FROM PcmConfiguration c WHERE c.configKey LIKE :pattern AND c.isActive = true")
    List<PcmConfiguration> findByConfigKeyLike(@Param("pattern") String pattern);

    /**
     * Check if a configuration key exists
     */
    boolean existsByConfigKey(String configKey);

    /**
     * Update configuration value by key
     */
    @Modifying
    @Query("UPDATE PcmConfiguration c SET c.configValue = :value, c.modifiedBy = :modifiedBy WHERE c.configKey = :key")
    int updateValueByKey(@Param("key") String key, @Param("value") String value, @Param("modifiedBy") String modifiedBy);

    /**
     * Deactivate a configuration by key
     */
    @Modifying
    @Query("UPDATE PcmConfiguration c SET c.isActive = false, c.modifiedBy = :modifiedBy WHERE c.configKey = :key")
    int deactivateByKey(@Param("key") String key, @Param("modifiedBy") String modifiedBy);

    /**
     * Search configurations by key or description containing search term
     */
    @Query("SELECT c FROM PcmConfiguration c WHERE (LOWER(c.configKey) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND c.isActive = true")
    List<PcmConfiguration> searchByKeyOrDescription(@Param("searchTerm") String searchTerm);

    /**
     * Get count of active configurations
     */
    long countByIsActiveTrue();

    /**
     * Find all configurations ordered by key
     */
    List<PcmConfiguration> findAllByOrderByConfigKeyAsc();

    /**
     * Delete configuration by key
     */
    void deleteByConfigKey(String configKey);
}
