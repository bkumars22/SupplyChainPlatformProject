/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.item.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Data Transfer Object for Item entity.
 * Used for API responses and data transformation.
 * Segregated from BomUtil for clean API contracts.
 */
@Data
public class ItemDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long itemKey;
    private String itemNumber;
    private String itemId;
    private String itemType;
    private String description;
    private String status;
    private Long businessEntityKey;
    
    /**
     * Default constructor
     */
    public ItemDto() {
    }
    
    /**
     * Constructor with key fields
     */
    public ItemDto(Long itemKey, String itemNumber, String itemId) {
        this.itemKey = itemKey;
        this.itemNumber = itemNumber;
        this.itemId = itemId;
    }
}
