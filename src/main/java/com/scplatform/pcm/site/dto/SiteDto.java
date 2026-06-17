/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.site.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * Data Transfer Object for Site entity.
 * Used for API responses and data transformation.
 * Segregated from BomUtil for clean API contracts.
 */
@Data
public class SiteDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long siteKey;
    private String siteDescription;
    private String siteType;
    private String status;
    private Long businessEntityKey;
    private String region;
    
    /**
     * Default constructor
     */
    public SiteDto() {
    }
    
    /**
     * Constructor with key fields
     */
    public SiteDto(Long siteKey, String siteDescription) {
        this.siteKey = siteKey;
        this.siteDescription = siteDescription;
    }
}
