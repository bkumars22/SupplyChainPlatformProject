/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.bom.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Data Transfer Object for BOM entity.
 * Used for API responses and data transformation.
 * Segregated from BomUtil for clean API contracts.
 */
@Data
public class BomDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long bomKey;
    private String bomNumber;
    private String status;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String description;
    private Long itemKey;
    private Long businessEntityKey;
    
    /**
     * Default constructor
     */
    public BomDto() {
    }
    
    /**
     * Constructor with key fields
     */
    public BomDto(Long bomKey, String bomNumber, String status) {
        this.bomKey = bomKey;
        this.bomNumber = bomNumber;
        this.status = status;
    }
}
