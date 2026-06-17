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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Models the Defect Type lookup
 */
@Entity
@Table(name = "PCM_DEFECT_TYPE")
@SuppressWarnings("serial")
public class PcmDefectType implements Serializable, Comparable<Object> {

    @Id
    @SequenceGenerator(name = "PCM_DEFECT_TYPE_SEQ", sequenceName = "PCM_DEFECT_TYPE_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "PCM_DEFECT_TYPE_SEQ")
    @Column(name = "DEFECT_TYPE_KEY")
    private Long defectTypeKey;

    @Column(name = "DEFECT_NAME", length = 64, nullable = false)
    private String defectName;

    public PcmDefectType() {
        super();
    }

    /**
     * Gets the primary key
     * 
     * @param
     */
    public Long getDefectTypeKey() {
        return this.defectTypeKey;
    }

    /**
     * Sets the primary key
     * 
     * @param defectTypeKey
     */
    public void setDefectTypeKey(Long defectTypeKey) {
        this.defectTypeKey = defectTypeKey;
    }

    /**
     * Gets the item category
     * 
     * @param
     */
    public String getDefectName() {
        return defectName;
    }
    
    
    /**
     * Sets the defect type
     * 
     * @param defectType
     */
    public void setDefectName(String defectType) {
        this.defectName = defectType;
    }

    @Override
    public int compareTo(Object o) {
        PcmDefectType other = (PcmDefectType) o;
        return new CompareToBuilder().append(this.defectName, other.getDefectName()).toComparison();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (!(obj instanceof PcmDefectType)) {
            return false;
        }
        PcmDefectType castOther = (PcmDefectType) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getDefectName(), castOther.getDefectName());
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        return hcb.append(this.defectName).toHashCode();
    }

    @Override
    public String toString() {
        return "PcmDefectType [defectName=" + defectName + ']';
    }
}
