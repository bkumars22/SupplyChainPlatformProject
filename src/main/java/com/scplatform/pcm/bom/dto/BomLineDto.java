/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.bom.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.bom.entity.PcmBomLineAttritionRate;
import com.scplatform.pcm.bom.entity.PcmDefectType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BomLineDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Hierarchy fields
    /**
     * Hierarchy level (1 = top level, 2 = first child, etc.)
     */
    private int level;
    
    /**
     * The actual BOM line entity
     */
    private BomLine line;
    
    /**
     * Child BOM lines for tree structure
     */
    private List<BomLineDto> childList = new ArrayList<>();
    
    /**
     * Attrition rates by defect type
     */
    private Map<PcmDefectType, PcmBomLineAttritionRate> attritionRates = new HashMap<>();
    
    // Basic BOM line fields
    /**
     * BOM Line unique identifier
     */
    private Long bomLineKey;
    
    /**
     * BOM key (parent BOM)
     */
    private Long bomKey;
    
    /**
     * Line number
     */
    private Integer lineNumber;
    
    /**
     * Quantity
     */
    private String quantity;
    
    /**
     * Unit of measure
     */
    private String unitOfMeasure;
    
    /**
     * Line status
     */
    private String status;
    
    /**
     * Effective from date
     */
    private String effectiveFromDate;
    
    /**
     * Effective to date
     */
    private String effectiveToDate;
}
