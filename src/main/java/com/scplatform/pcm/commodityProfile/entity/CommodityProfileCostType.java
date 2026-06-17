/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.commodityProfile.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.io.Serializable;

@SuppressWarnings("serial")
@Entity
@Table(name = "COMMODITY_PROFILE_COST_TYPE")
public class CommodityProfileCostType implements Serializable
{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ct_profile_id_seq")
	@SequenceGenerator(name = "ct_profile_id_seq", sequenceName = "CT_PROFILE_ID_SEQ", allocationSize = 1)
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "PROFILE_ID")
	private CommodityProfile commodityProfile;

	@Column(name = "COST_TYPE")
	private String costType;
	 
	
	public CommodityProfileCostType() {
		super();
	}
	
	public CommodityProfileCostType(Long id) {
		super();
		this.id = id;
	}

	

	public String getCostType() {
		return costType;
	}

	public Long getId() {
		return id;
	}

	public CommodityProfile getCommodityProfile() {
		return commodityProfile;
	}

	public void setCommodityProfile(CommodityProfile commodityProfile) {
		this.commodityProfile = commodityProfile;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCostType(String costType) {
		this.costType = costType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((commodityProfile == null) ? 0 : commodityProfile.hashCode());
		result = prime * result
				+ ((costType == null) ? 0 : costType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommodityProfileCostType other = (CommodityProfileCostType) obj;
		if (commodityProfile == null) {
			if (other.commodityProfile != null)
				return false;
		} else if (!commodityProfile.equals(other.commodityProfile))
			return false;
		if (costType == null) {
			if (other.costType != null)
				return false;
		} else if (!costType.equals(other.costType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CommodityProfileCostType [id=" + id + ", commodityProfile="
				+ commodityProfile + ", costType=" + costType + "]";
	}

}
