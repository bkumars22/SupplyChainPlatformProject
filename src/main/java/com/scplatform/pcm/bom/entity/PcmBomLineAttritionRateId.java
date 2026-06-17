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

import com.scplatform.pcm.item.entity.Item;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@SuppressWarnings("serial")
@Embeddable
public class PcmBomLineAttritionRateId implements java.io.Serializable {
	
	@ManyToOne
	@JoinColumn(name="BOM_LINE_ITEM_KEY",nullable=false)
    private BomLine bomLine;
	
	@ManyToOne
	@JoinColumn(name="ITEM_KEY",nullable=false)
    private Item bomItem;
	
	@ManyToOne
	@JoinColumn(name="DEFECT_TYPE_KEY",nullable=false)
    private PcmDefectType defectType;

    public PcmBomLineAttritionRateId() {
    }

    public PcmBomLineAttritionRateId(BomLine bomLine, Item bomItem, PcmDefectType defectType) {
        this.bomLine = bomLine;
        this.bomItem = bomItem;
        this.defectType = defectType;
    }

    public BomLine getBomLine() {
        return bomLine;
    }

    public void setBomLine(BomLine bomLine) {
        this.bomLine = bomLine;
    }

    public Item getBomItem() {
        return bomItem;
    }

    public void setBomItem(Item bomItem) {
        this.bomItem = bomItem;
    }

    public PcmDefectType getDefectType() {
        return defectType;
    }

    public void setDefectType(PcmDefectType defectType) {
        this.defectType = defectType;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(this.getBomItem());
        hcb.append(this.getBomLine());
        hcb.append(this.getDefectType());
        return hcb.toHashCode();

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PcmBomLineAttritionRateId)) {
            return false;
        }
        PcmBomLineAttritionRateId castOther = (PcmBomLineAttritionRateId) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getBomItem(), castOther.getBomItem());
        eb.append(this.getBomLine(), castOther.getBomLine());
        eb.append(this.getDefectType(), castOther.getDefectType());
        return eb.isEquals();
    }

    @Override
    public String toString() {
        return "Bom Line=" + bomLine.getItem().getItemNumber() + ", Bom Item=" + bomItem.getItemNumber() + ", Defect Type=" + defectType.getDefectName() ;
    }
}
