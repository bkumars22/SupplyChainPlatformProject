/*
 * Copyright (c) 2011 E2open Inc. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2011, by E2open Inc. All rights reserved.
 */
package com.scplatform.pcm.bom.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scplatform.pcm.bom.service.BomLineService;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.util.message.SCPlatformMessages;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="PCM_BOM_LINE_ITEM_ATTRITION_RT")
public class PcmBomLineAttritionRate {
	
	@EmbeddedId
    private PcmBomLineAttritionRateId id;
    
    @Column(nullable=true,scale=4,name="ATTRITION_RATE")
    private BigDecimal attritionRate;
    
    @Autowired
    @Transient
    private BomLineService bomLineService;

    public PcmBomLineAttritionRate() {
    }
    
    public PcmBomLineAttritionRate(PcmBomLineAttritionRateId id) {
        this.id = id;
    }
    
    public PcmBomLineAttritionRateId getId() {
        return id;
    }
    
    public void setId(PcmBomLineAttritionRateId id) {
        this.id = id;
    }

    public BigDecimal getAttritionRate() {
        return this.attritionRate;
    }

    public void setAttritionRate(BigDecimal attritionRate) {
        this.attritionRate = attritionRate;
    }

    /**
     * @return The defect type associated with this attrition rate
     */
    public PcmDefectType getDefectType() {
        if (this.id == null) {
            return null;
        }
        return this.id.getDefectType();
    }
    
    /**
     * @return The bom line associated with this attrition rate
     */
    public BomLine getBomLine() {
        if (this.id == null) {
            return null;
        }
        return this.id.getBomLine();
    }
    
    /**
     * @return The bom item associated with this attrition rate
     */
    public Item getBomItem() {
        if (this.id == null) {
            return null;
        }
        return this.id.getBomItem();
    }
    
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19,39);
        hcb.append(this.getId());
        hcb.append(this.attritionRate);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PcmBomLineAttritionRate)) {
            return false;
        }
        PcmBomLineAttritionRate castOther = (PcmBomLineAttritionRate) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getId(), castOther.getId());
        eb.append(this.getAttritionRate(), castOther.getAttritionRate());
        return eb.isEquals();
    }
    
    @Override
    public String toString() {
        List<Object> args = new ArrayList<Object>();
        if (bomLineService != null && getBomLine() != null) {
            args.add(bomLineService.getTitle(getBomLine()));
        } else if (getBomLine() != null) {
            args.add(getBomLine().toString());
        } else {
            args.add("Unknown BomLine");
        }
        args.add(id);
        args.add(attritionRate);
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bomLineAttritionRate", args.toArray(), null);
    }
}
