/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for BomGroupLink entity.
 * Represents the composite key consisting of bomGroupKey and bomKey.
 */
public class BomGroupLinkId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long bomGroup;
    private Long bom;
    
    /**
     * Default constructor
     */
    public BomGroupLinkId() {
    }
    
    /**
     * Constructor with parameters
     *
     * @param bomGroup the BOM group key
     * @param bom the BOM key
     */
    public BomGroupLinkId(Long bomGroup, Long bom) {
        this.bomGroup = bomGroup;
        this.bom = bom;
    }
    
    // Property accessors
    public Long getBomGroup() {
        return bomGroup;
    }
    
    public void setBomGroup(Long bomGroup) {
        this.bomGroup = bomGroup;
    }
    
    public Long getBom() {
        return bom;
    }
    
    public void setBom(Long bom) {
        this.bom = bom;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BomGroupLinkId that = (BomGroupLinkId) o;
        return Objects.equals(bomGroup, that.bomGroup) &&
               Objects.equals(bom, that.bom);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(bomGroup, bom);
    }
    
    @Override
    public String toString() {
        return "BomGroupLinkId{" +
                "bomGroup=" + bomGroup +
                ", bom=" + bom +
                '}';
    }
}

