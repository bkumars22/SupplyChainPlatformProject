/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.businessEntity.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Data Transfer Object for BusinessEntity entity.
 * Used for API responses and data transformation.
 * Segregated from BomUtil for clean API contracts.
 */
@Data
public class BusinessEntityDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long businessEntityKey;
    private String businessEntityIdentifier;
    private String businessEntityName;
    private String businessEntityTypeName;
    private String status;
    private String description;
    
    /**
     * Default constructor
     */
    public BusinessEntityDto() {
    }
    
    /**
     * Constructor with key fields
     */
    public BusinessEntityDto(Long businessEntityKey, String businessEntityIdentifier, String businessEntityName) {
        this.businessEntityKey = businessEntityKey;
        this.businessEntityIdentifier = businessEntityIdentifier;
        this.businessEntityName = businessEntityName;
    }
}
