/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */

package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.cost.enums.PcmCostElementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;

/**
 * Models cost elements within a cost type structure.
 */
@Entity
@Table(name="PCM_COST_ELEMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"pcmCostType"})
public class PcmCostElement implements Serializable, Comparable<PcmCostElement> {

	@EmbeddedId
    private PcmCostElementId id;
    
    @ManyToOne(optional=false)
    @JoinColumn(name="COST_TYPE_KEY",updatable=false,insertable=false)
    private PcmCostType pcmCostType;
    
    @Column(name="COST_ELEMENT_NAME",nullable=false,length=64)
    private String costElementName;
    
    @Column(name="COST_ELEMENT_VALUE_TYPE",nullable=false,length=32)
    private String costElementValueType;
    
    @Column(name="COST_ELEMENT_ORDER",length=4,nullable=true)
    private Long displayOrder;
    
    @Enumerated(EnumType.STRING)
    @Column(name="COST_ELEMENT_TYPE")
    private PcmCostElementType costElementType;
    
    @Column(name="IS_REQUIRED",length=1,nullable=true,columnDefinition="NUMBER")
    private Boolean isRequired;

    public boolean isOfType(PcmCostElementType type) {
        return type.equals(this.costElementType);
    }

    public boolean isNotOfType(PcmCostElementType type) {
        return !type.equals(this.costElementType);
    }
    public String getCostElementKey() {
        return this.id.getCostElementKey();
    }

    @Override
    public int compareTo(PcmCostElement other) {
        if (other == null) return 1;
        return new CompareToBuilder()
                .append(this.costElementType, other.costElementType)
                .append(this.displayOrder, other.displayOrder)
                .append(this.id.getCostElementKey(), other.id.getCostElementKey())
                .toComparison();
    }
}
