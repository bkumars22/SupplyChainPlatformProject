/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2020, by E2open Inc. All rights reserved.
 */
package com.scplatform.pcm.cost.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;

@Entity
@Table(name = "PCM_COST_VALUE_DETAIL_EXCEPTION")
@SuppressWarnings("serial")
public class PcmCostValueDetailException implements java.io.Serializable
{
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PCM_COST_RECORD_VALUE_DETAIL_EXCEPTION_SEQ")
	@SequenceGenerator(sequenceName = "PCM_COST_RECORD_VALUE_DETAIL_EXCEPTION_SEQ", name = "PCM_COST_RECORD_VALUE_DETAIL_EXCEPTION_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name="COST_VALUE_DETAIL_KEY")
	private Long costValueDetailKey;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "COST_RECORD_VALUE_KEY", nullable = false)
    @Fetch(value = FetchMode.SELECT)
	private PcmCostRecordValueException costRecordValue;
	
	@Column(name = "COST_VALUE_NAME", nullable = false)
	private String costValueName;
	
	@Column(name = "COST_VALUE_VALUE", nullable = false)
	private BigDecimal costValueValue;
	
	@Column(name = "COST_VALUE_BLEND", nullable = false)
	private BigDecimal costValueBlend;

	public PcmCostValueDetailException()
	{
	}

	public PcmCostValueDetailException(Long costValueDetailKey, PcmCostRecordValueException pcmCostRecordValue,
			String costValueName, BigDecimal costValueValue, BigDecimal costValueBlend)
	{
		this.costValueDetailKey = costValueDetailKey;
		this.costRecordValue = pcmCostRecordValue;
		this.costValueName = costValueName;
		this.costValueValue = costValueValue;
		this.costValueBlend = costValueBlend;
	}

	public Long getCostValueDetailKey()
	{
		return this.costValueDetailKey;
	}

	public void setCostValueDetailKey(Long costValueDetailKey)
	{
		this.costValueDetailKey = costValueDetailKey;
	}

	public PcmCostRecordValueException getCostRecordValue()
	{
		return this.costRecordValue;
	}

	public void setCostRecordValue(PcmCostRecordValueException pcmCostRecordValue)
	{
		this.costRecordValue = pcmCostRecordValue;
	}

	public String getCostValueName()
	{
		return this.costValueName;
	}

	public void setCostValueName(String costValueName)
	{
		this.costValueName = costValueName;
	}

	public BigDecimal getCostValueValue()
	{
		return this.costValueValue;
	}

	public void setCostValueValue(BigDecimal costValueValue)
	{
		this.costValueValue = costValueValue;
	}

	public BigDecimal getCostValueBlend()
	{
		return this.costValueBlend;
	}

	public void setCostValueBlend(BigDecimal costValueBlend)
	{
		this.costValueBlend = costValueBlend;
	}
	
	public PcmCostValueDetailException copy() {
	    PcmCostValueDetailException copy = new PcmCostValueDetailException();
	    // TODO copy.costRecordValue = this.costRecordValue; was not set earlier	
	    copy.costValueBlend = this.costValueBlend;
	    copy.costValueName = this.costValueName;
	    copy.costValueValue = this.costValueValue;
	    return copy;
	}
	
	
	public int compareTo(Object o)
	{
		PcmCostValueDetailException other = (PcmCostValueDetailException)o;
		return new CompareToBuilder()
	       	.append(this.getCostRecordValue(), other.getCostRecordValue())		
		    .append(this.getCostValueName(),other.getCostValueName())		       
		    .toComparison();
	}
	
	@Override
	public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PcmCostValueDetailException))
            return false;
        // We can use compare to
        return (compareTo(other) == 0);
    }
	
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
        .append(this.getCostRecordValue()).toHashCode();
    }
	
}
