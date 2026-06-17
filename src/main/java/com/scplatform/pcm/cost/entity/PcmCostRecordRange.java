/**
 *	PcmCostRecordRange.java
 *	Created on Sep 26, 2011
 *     
 *	Copyright (c) 2010 E2open, Inc.
 *	All Rights Reserved.
 *
 *	THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *	The copyright notice above does not evidence any
 *	actual or intended publication of such source code. 
 *	
 *	Author: sgupta
 */
package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.common.entity.AuditRevisionBase;
import com.scplatform.pcm.cost.enums.PcmCostElementType;
import com.scplatform.pcm.cost.service.PcmCostRecordRangeService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * PcmCostRecordRange - represents pricing tier ranges for cost records
 */
@Entity
@Table(name = "PCM_COST_RECORD_RANGE")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"costRecord", "costRecordValues"})
@SuppressWarnings("serial")
public class PcmCostRecordRange extends AuditRevisionBase implements Serializable, Comparable<PcmCostRecordRange> {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(PcmCostRecordRange.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCM_COST_RECORD_RANGE_SEQ")
	@SequenceGenerator(sequenceName = "PCM_COST_RECORD_RANGE_SEQ", name = "PCM_COST_RECORD_RANGE_SEQ", allocationSize = 1, initialValue = 1)
    @Column(name = "COST_RECORD_RANGE_KEY")
	private Long costRecordRangeKey;
	
	@NaturalId(mutable = true)
	@ManyToOne(optional = false)
	@JoinColumn(name = "COST_RECORD_KEY", nullable = false)
    @Fetch(value = FetchMode.SELECT)
	private PcmCostRecord costRecord;
	
    @NaturalId(mutable = true)
    @Column(name = "FROM_RANGE", nullable = false, precision = 19, scale = 6)
    private BigDecimal fromRange;
    
    @NaturalId(mutable = true)
    @Column(name = "TO_RANGE", precision = 19, scale = 6)
    private BigDecimal toRange;
    
    @Column(name = "IS_ACTIVE", length = 1)
    private Boolean active;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "costRecordRange", fetch = FetchType.EAGER)
    @MapKeyColumn(name = "COST_ELEMENT_KEY")
    private Map<String, PcmCostRecordValue> costRecordValues = new LinkedHashMap<>();

    public PcmCostRecordRange(BigDecimal fromRange) {
        super();
        this.fromRange = fromRange;
    }

    /**
     * Add a cost record value to the cost record range. This method will update an existing cost record value with the
     * same element key
     * 
     * @param element the cost element
     * @param value the cost value
     * @param uom the unit of measure
     * @return the added/updated cost record value
     */
    public PcmCostRecordValue addCostRecordValue(PcmCostElement element, BigDecimal value, String uom) {
        PcmCostRecordValue result = costRecordValues.get(element.getId().getCostElementKey());
        if (result == null) {
            result = new PcmCostRecordValue();
            result.setCostRecordRange(this);
            result.setCostElement(element);
            costRecordValues.put(element.getId().getCostElementKey(), result);
        }
        result.setCostUom(uom);
        result.setCostValueType(element.getCostElementValueType());
        result.setCostValue(value);
        return result;
    }
    
    /**
     * Remove a cost record value from the cost record range
     * 
     * @param element the cost element to remove
     */
    public void removeCostRecordValue(PcmCostElement element) {
    	costRecordValues.remove(element.getId().getCostElementKey());
    }

    /**
     * Add a cost record value to the cost record range
     * 
     * @param val the value to add
     * @return the added cost record value
     */
    public PcmCostRecordValue addCostRecordValue(PcmCostRecordValue val) {
        costRecordValues.put(val.getCostElement().getId().getCostElementKey(), val);
        val.setCostRecordRange(this);
        return val;
    }

    /**
     * Get a cost record value associated with a cost element
     * 
     * @param element the element
     * @return the cost record value or null if not found
     */
    public PcmCostRecordValue getCostRecordValue(PcmCostElement element) {
        return getCostRecordValue(element.getId().getCostElementKey());
    }

    /**
     * Get a cost record value associated with an element key
     * 
     * @param elementKey the element key
     * @return the cost record value or null if not found
     */
    public PcmCostRecordValue getCostRecordValue(String elementKey) {
        return costRecordValues.get(elementKey);
    }

    public BigDecimal getComputedTotalNotOfCostElementTypeFixed() {
        PcmCostRecordRangeService rangeService = SpringContextHolder.getBean(PcmCostRecordRangeService.class);
        return rangeService.getComputedTotalNotOfCostElementTypeFixed(this);
    }

    public BigDecimal getTotalByCostElementType(PcmCostElementType type) {
        PcmCostRecordRangeService rangeService = SpringContextHolder.getBean(PcmCostRecordRangeService.class);
        return rangeService.getTotalByCostElementType(this, type);
    }

    public BigDecimal getComputedTotalByCostElementType(PcmCostElementType type) {
        PcmCostRecordRangeService rangeService = SpringContextHolder.getBean(PcmCostRecordRangeService.class);
        return rangeService.getComputedTotalByCostElementType(this, type);
    }

    public BigDecimal getTotalNotOfCostElementType(PcmCostElementType excludedType) {
        PcmCostRecordRangeService rangeService = SpringContextHolder.getBean(PcmCostRecordRangeService.class);
        return rangeService.getTotalNotOfCostElementType(this, excludedType);
    }

    public PcmCostRecordRange copy() {
        PcmCostRecordRange copy = new PcmCostRecordRange();
        copy.active = this.active;
        copy.fromRange = this.fromRange;
        copy.toRange = this.toRange;
        copy.costRecord = this.costRecord;
        for (PcmCostRecordValue value : costRecordValues.values()) {
            PcmCostRecordValue newValue = value.copy();
            copy.addCostRecordValue(newValue);
        }
        return copy;
    }

    @Override
    public int compareTo(PcmCostRecordRange other) {
        return this.fromRange.compareTo(other.getFromRange());
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PcmCostRecordRange))
            return false;
        PcmCostRecordRange castOther = (PcmCostRecordRange) other;
        EqualsBuilder eb = new EqualsBuilder();
        try {
            eb.append(this.costRecord, castOther.getCostRecord());
            eb.append(this.getFromRange(), castOther.getFromRange());
            eb.append(this.getToRange(), castOther.getToRange());
        } catch (Throwable t) {
            log.warn("isEqual failed, using KEY", t);
            eb.append(this.getCostRecordRangeKey(), castOther.getCostRecordRangeKey());
        }
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.getFromRange()).append(this.getToRange()).toHashCode();
    }
}