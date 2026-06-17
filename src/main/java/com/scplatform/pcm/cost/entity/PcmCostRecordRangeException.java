/**
 *	PcmCostRecordRangeException.java
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

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PcmCostRecordRangeException entity - represents pricing ranges with exception handling
 */
@Entity
@Table(name = "PCM_COST_RECORD_RANGE_EXCEPTION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"costRecord", "costRecordValues"})
@SuppressWarnings("serial")
public class PcmCostRecordRangeException implements Serializable, Comparable<PcmCostRecordRangeException> {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(PcmCostRecordRangeException.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCM_COST_RECORD_RANGE_EXCEPTION_SEQ")
	@SequenceGenerator(sequenceName = "PCM_COST_RECORD_RANGE_EXCEPTION_SEQ", name = "PCM_COST_RECORD_RANGE_EXCEPTION_SEQ", allocationSize = 1, initialValue = 1)
    @Column(name = "COST_RECORD_RANGE_KEY")
	private Long costRecordRangeKey;
	
	@NaturalId(mutable = true)
	@ManyToOne(optional = false)
	@JoinColumn(name = "COST_RECORD_KEY")
    @Fetch(value = FetchMode.SELECT)
	private PcmCostRecordException costRecord;
	
    @NaturalId(mutable = true)
    @Column(name = "FROM_RANGE", nullable = false, precision = 19, scale = 6)
    private BigDecimal fromRange;
    
    @NaturalId(mutable = true)
    @Column(name = "TO_RANGE", precision = 19, scale = 6)
    private BigDecimal toRange;
    
    @Column(name = "IS_ACTIVE", length = 1)
    private Boolean active;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "costRecordRange")
    @MapKeyColumn(name = "COST_ELEMENT_KEY")
    private Map<String, PcmCostRecordValueException> costRecordValues = new LinkedHashMap<>();

    /**
     * Add a cost record value to the cost record range. This method will update an existing cost record value with the
     * same element key
     * 
     * @param element
     * @param value
     * @param uom
     * @return
     */
    public PcmCostRecordValueException addCostRecordValue(PcmCostElement element, BigDecimal value, String uom) {
        PcmCostRecordValueException result = costRecordValues.get(element.getId().getCostElementKey());
        if (result == null) {
            result = new PcmCostRecordValueException();
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
     * @param element
     */
    public void removeCostRecordValue(PcmCostElement element) {
    	costRecordValues.remove(element.getId().getCostElementKey());
    }

    /**
     * Add a cost record value to the cost record range
     * 
     * @param val
     * @return
     */
    public PcmCostRecordValueException addCostRecordValue(PcmCostRecordValueException val) {
        costRecordValues.put(val.getCostElement().getId().getCostElementKey(), val);
        val.setCostRecordRange(this);
        return val;
    }

    /**
     * Get a cost record value associated with a PcmCostElement. Returns null if not found
     * 
     * @param element
     * @return
     */
    public PcmCostRecordValueException getCostRecordValue(PcmCostElement element) {
        return getCostRecordValue(element.getId().getCostElementKey());
    }

    /**
     * Get a cost record value associated with an element key. Returns null if not found
     * 
     * @param elementKey
     * @return
     */
    public PcmCostRecordValueException getCostRecordValue(String elementKey) {
        return costRecordValues.get(elementKey);
    }

    /**
     * Create a copy of this cost record range exception
     * 
     * @return
     */
    public PcmCostRecordRangeException copy() {
        PcmCostRecordRangeException copy = new PcmCostRecordRangeException();
        copy.active = this.active;
        copy.fromRange = this.fromRange;
        copy.toRange = this.toRange;
        copy.costRecord = this.costRecord;
        for (PcmCostRecordValueException value : costRecordValues.values()) {
            PcmCostRecordValueException newValue = value.copy();
            copy.addCostRecordValue(newValue);
        }
        return copy;
    }

    /**
     * Compare based on fromRange (natural ordering)
     * Lower fromRange represents a lower tier
     */
    @Override
    public int compareTo(PcmCostRecordRangeException other) {
        return this.fromRange.compareTo(other.getFromRange());
    }
}