/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.platform.entity;


import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import java.io.Serializable;

/**
 * Entity implementation class for Entity: Platform
 *
 */
@Entity
@Table(name="ITEM_PLATFORM")
@SuppressWarnings("serial")
@FilterDef(
	name = "platformTypeFilter",
	parameters = @ParamDef(name = "platformType", type = String.class))
@FilterDef(
	name = "platformFilter",
	parameters = {
		@ParamDef(name = "user", type = Long.class),
		@ParamDef(name = "role", type = Long.class)
	})
@Filters( {
    @Filter(name="platformTypeFilter", condition="(COALESCE(ITEM_PLATFORM_TYPE,'NULL') IN (:platformType))"),
    @Filter(name="platformFilter", condition="(ITEM_PLATFORM_KEY IN (SELECT DISTINCT PAC.TARGET_ENTITY_KEY" 
		     	+ " FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='PLATFORM'"
    			+ " AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role)))")
} )
public class Platform implements Serializable 
{
	@Id
	@SequenceGenerator(name="ITEM_PLATFORM_SEQ", sequenceName = "ITEM_PLATFORM_SEQ",allocationSize=1)
	@GeneratedValue(generator = "ITEM_PLATFORM_SEQ")
	@Column(name="ITEM_PLATFORM_KEY")
	protected long platformKey;
	
	@Column(name="ITEM_PLATFORM_NAME")
	protected String platformName;	

	@Column(name="ITEM_PLATFORM_TYPE")
	protected String platformType;	
	
	@Column(name="ITEM_PLATFORM_DESC")
	protected String platformDescription;
	
	@Column(name="ITEM_PLATFORM_EXTERNAL_ID")
	protected String platformExternalId;
	
	@ManyToOne(fetch=FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name="BUSINESS_ENTITY_KEY")
	protected BusinessEntity businessEntity;
	
	public static final Platform NO_PLATFORM = new Platform();
        
        static {
            NO_PLATFORM.setPlatformName("NO_PLATFORM");
            NO_PLATFORM.setPlatformDescription("No platform");
        }
	
	public Platform() 
	{
		super();
	}

	public long getPlatformKey()
	{
		return platformKey;
	}

	public void setPlatformKey(long platformKey)
	{
		this.platformKey = platformKey;
	}

	public String getPlatformName()
	{
		return platformName;
	}

	public void setPlatformName(String platformName)
	{
		this.platformName = platformName;
	}

	public String getPlatformType()
	{
		return platformType;
	}

	public void setPlatformType(String platformType)
	{
		this.platformType = platformType;
	}

	public String getPlatformDescription()
	{
		return platformDescription;
	}

	public void setPlatformDescription(String platformDescription)
	{
		this.platformDescription = platformDescription;
	}

	public String getPlatformExternalId()
	{
		return platformExternalId;
	}

	public void setPlatformExternalId(String platformExternalId)
	{
		this.platformExternalId = platformExternalId;
	}

	public BusinessEntity getBusinessEntity()
	{
		return businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity)
	{
		this.businessEntity = businessEntity;
	} 
	
	public ObjectNode getPlatformsNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("platformName",this.platformName);
        return o;
    }
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(platformName).toHashCode();
	}

	@Override
	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Platform))
			return false;
		Platform castOther = (Platform) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.platformName, castOther.platformName);
		eb.append(this.platformType, castOther.platformType);
		eb.append(this.getBusinessEntity(), castOther.getBusinessEntity());
		return eb.isEquals();

	}
}
