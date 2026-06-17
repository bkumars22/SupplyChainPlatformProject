/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.businessEntity.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import com.scplatform.pcm.bom.entity.BaseBomEntity;

/**
 * Models the business entity alternate.  This is an alias name for a business entity
 * and is used when searching for businesses.  It is very commom to have multiple 
 * names for the same vendor in a system and this attempts to resolve those without
 * having to create seperate business entities. 
 * This is a case insensitive name so isEqual will return true of 
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "BUSINESS_ENTITY_ALT")
@Getter
@Setter
@Filter(
	name = "businessFilter",
	condition = "(BUSINESS_ENTITY_KEY IN (:businessEntity))")
public class BusinessEntityAlternate extends BaseBomEntity
	implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "businessEntityAltSeqGen")
	@SequenceGenerator(name = "businessEntityAltSeqGen", sequenceName = "BUSINESS_ENTITY_ALT_SEQ", allocationSize = 1)
	@Column(name = "BUSINESS_ENTITY_ALT_KEY", nullable = false, unique = true)
	private Long businessEntityAltKey;

	@Column(name = "BUSINESS_ENTITY_NAME", nullable = false)
	private String businessEntityName;

	@Column(name = "DATA_SOURCE", nullable = false)
	private String dataSource = "MCM";
	
	@ManyToOne
	@JoinColumn(name = "BUSINESS_ENTITY_KEY", nullable = false, unique = true)
	private BusinessEntity businessEntity;
	
	public BusinessEntityAlternate()
	{
		super();
	}

	/** constructor with id */
	public BusinessEntityAlternate(Long key)
	{
		super();
		this.businessEntityAltKey = key;
	}

	/**
	 * Case INSENSITIVE compare of the name
	 */
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof BusinessEntityAlternate))
            return false;
        BusinessEntityAlternate castOther = (BusinessEntityAlternate) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(StringUtils.upperCase(getBusinessEntityName()),StringUtils.upperCase(castOther.getBusinessEntityName()));               
        return eb.isEquals();
    }
    
    public int hashCode()
    {
    	HashCodeBuilder hcb = new HashCodeBuilder(17, 37);
        hcb.append(StringUtils.upperCase(getBusinessEntityName()));
        return hcb.toHashCode();
    }
	
}
