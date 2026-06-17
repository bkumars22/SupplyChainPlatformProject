/*
 * Copyright (c) 2010 E2open Inc. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2010, by E2open Inc. All rights reserved.
 */

package com.scplatform.pcm.responsibility.entity;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.scplatform.pcm.role.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "PCM_RESPONSIBILITY")
@SuppressWarnings({ "serial", "rawtypes" })
public class PcmResponsibility implements java.io.Serializable, Comparable
{
	@Id
	@Column(name = "RESPONSIBILITY_KEY", length = 32)
	private String responsibilityKey;

	@Column(name = "RESPONSIBILITY_NAME", length = 64, nullable = false)
	private String responsibilityName;

	@Column(name = "RESPONSIBILITY_ORDER", length = 4)
	private Long displayOrder;

	@Column(name = "RESPONSIBILITY_TYPE", length = 2, nullable = false)
	private String responsibilityType;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
		name = "PCM_ROLE_RESPONSIBILITY",
		joinColumns = @JoinColumn(name = "RESPONSIBILITY_KEY"),
		inverseJoinColumns = @JoinColumn(name = "ROLE_KEY")
	)
	private Set<Role> roles = new HashSet<Role>();
	
	public static final String TYPE_ITEM = "I";
	public static final String TYPE_ITEMCATEGORY = "IC";
	
	public PcmResponsibility()
	{
	}

	public PcmResponsibility(String responsibilityKey,
			String responsibilityName)
	{
		this.responsibilityKey = responsibilityKey;
		this.responsibilityName = responsibilityName;
	}

	public String getResponsibilityKey()
	{
		return this.responsibilityKey;
	}

	public void setResponsibilityKey(String responsibilityKey)
	{
		this.responsibilityKey = responsibilityKey;
	}

	public String getResponsibilityName()
	{
		return this.responsibilityName;
	}

	public void setResponsibilityName(String responsibilityName)
	{
		this.responsibilityName = responsibilityName;
	}

	public Long getDisplayOrder()
	{
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder)
	{
		this.displayOrder = displayOrder;
	}
	
	public String getResponsibilityType()
	{
		return this.responsibilityType;
	}

	public void setResponsibilityType(String responsibilityType)
	{
		this.responsibilityType = responsibilityType;
	}
	
	public int hashCode()
	{
		return (responsibilityKey != null) ? responsibilityKey.hashCode():0;
	}
	
	public void setRoles(Set<Role> roles)
	{
		this.roles = roles;
	}

	public Set<Role> getRoles()
	{
		return roles;
	}
	
	public ObjectNode getResponsibilityNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("responsibility",this.responsibilityName);
        return o;
    }
	
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PcmResponsibility))
            return false;
        PcmResponsibility castOther = (PcmResponsibility) other;
        return (this.responsibilityKey.equals(castOther.responsibilityKey));
    }
	    
	public int compareTo(Object o)
	{
		PcmResponsibility other = (PcmResponsibility)o;
		return new CompareToBuilder()
	       .append(this.displayOrder,other.displayOrder)
	       .append(this.responsibilityKey,other.responsibilityKey)
	       .toComparison();
	}

	@Override
	public String toString()
	{
		return responsibilityKey;
	}
}
