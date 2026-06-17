/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing PCM configuration key-value pairs stored in database.
 * Replaces the file-based pcm-config.properties configuration.
 */
@Entity
@Table(name = "PCM_CONFIGURATION", indexes = {
    @Index(name = "IDX_PCM_CONFIG_KEY", columnList = "CONFIG_KEY", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PcmConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_config_seq")
    @SequenceGenerator(name = "pcm_config_seq", sequenceName = "PCM_CONFIGURATION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CONFIG_KEY", nullable = false, length = 500, unique = true)
    private String configKey;

    @Column(name = "CONFIG_VALUE", length = 4000)
    private String configValue;

    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    @Column(name = "VALUE_TYPE", length = 50)
    @Builder.Default
    private String valueType = "STRING"; // STRING, BOOLEAN, LIST

    @Column(name = "IS_ACTIVE")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "MODIFIED_BY", length = 100)
    private String modifiedBy;

    /**
     * Get value as Boolean
     */
    public Boolean getValueAsBoolean() {
        if (configValue == null) return null;
        return Boolean.parseBoolean(configValue.trim());
    }

    /**
     * Get value as List (comma-separated)
     */
    public java.util.List<String> getValueAsList() {
        if (configValue == null || configValue.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return java.util.Arrays.stream(configValue.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Get value as Integer
     */
    public Integer getValueAsInteger() {
        if (configValue == null || configValue.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(configValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get value as Long
     */
    public Long getValueAsLong() {
        if (configValue == null || configValue.trim().isEmpty()) return null;
        try {
            return Long.parseLong(configValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get value as Double
     */
    public Double getValueAsDouble() {
        if (configValue == null || configValue.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(configValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
