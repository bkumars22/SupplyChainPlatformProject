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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.Set;

import com.scplatform.pcm.item.entity.ItemCategory;

@SuppressWarnings("serial")
@Entity
@Table(name = "COMMODITY_PROFILE")
public class CommodityProfile implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_id_seq")
	@SequenceGenerator(name = "profile_id_seq", sequenceName = "PROFILE_ID_SEQ", allocationSize = 1)
	@Column(name = "PROFILE_ID", unique = true, nullable = false)
	private Long profileId;

	@NaturalId
	@Column(name = "PROFILE_NAME", nullable = false)
	private String profileName;

	@Column(name = "COMPANY_ITEM_TYPE", nullable = false)
	private String companyItemType;

	@Column(name = "INCLUDE_EXCLUDE_COST_RECORD")
	private String includeExcludeCostRecord;

	@Column(name = "INCLUDE_EXCLUDE_COST_FORECAST")
	private String includeExcludeCostForecast;

	@Column(name = "INCLUDE_EXCLUDE_REBATE")
	private String includeExcludeRebate;

	@Column(name = "INCLUDE_EXCLUDE_ITEM")
	private String includeExcludeItem;

	@Column(name = "INCLUDE_EXCLUDE_BOM")
	private String includeExcludeBOM;

	@Column(name = "INCLUDE_EXCLUDE_TAM")
	private String includeExcludeTAM;

	@Column(name = "INCLUDE_EXCLUDE_PRICE_TAM")
	private String includeExcludePriceTAM;

	@ManyToOne
	@JoinColumn(name = "ITEM_CATEGORY_KEY", unique = true)
	private ItemCategory itemCategory;

	@OneToMany(mappedBy = "commodityProfile", cascade = CascadeType.ALL)
	private Set<CommodityProfileCostType> costTypes;

	public CommodityProfile() {
		super();
	}

	public CommodityProfile(Long profileId) {
		super();
		this.profileId = profileId;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getCompanyItemType() {
		return companyItemType;
	}

	public void setCompanyItemType(String companyItemType) {
		this.companyItemType = companyItemType;
	}

	public String getIncludeExcludeCostRecord() {
		return includeExcludeCostRecord;
	}

	public void setIncludeExcludeCostRecord(String includeExcludeCostRecord) {
		this.includeExcludeCostRecord = includeExcludeCostRecord;
	}

	public String getIncludeExcludeCostForecast() {
		return includeExcludeCostForecast;
	}

	public void setIncludeExcludeCostForecast(String includeExcludeCostForecast) {
		this.includeExcludeCostForecast = includeExcludeCostForecast;
	}

	public String getIncludeExcludeRebate() {
		return includeExcludeRebate;
	}

	public void setIncludeExcludeRebate(String includeExcludeRebate) {
		this.includeExcludeRebate = includeExcludeRebate;
	}

	public String getIncludeExcludeItem() {
		return includeExcludeItem;
	}

	public void setIncludeExcludeItem(String includeExcludeItem) {
		this.includeExcludeItem = includeExcludeItem;
	}

	public String getIncludeExcludeBOM() {
		return includeExcludeBOM;
	}

	public void setIncludeExcludeBOM(String includeExcludeBOM) {
		this.includeExcludeBOM = includeExcludeBOM;
	}

	public ItemCategory getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(ItemCategory itemCategory) {
		this.itemCategory = itemCategory;
	}

	public Set<CommodityProfileCostType> getCostTypes() {
		return costTypes;
	}

	public void setCostTypes(Set<CommodityProfileCostType> costTypes) {
		this.costTypes = costTypes;
	}
	
	public String getIncludeExcludeTAM() {
		return includeExcludeTAM;
	}

	public void setIncludeExcludeTAM(String includeExcludeTAM) {
		this.includeExcludeTAM = includeExcludeTAM;
	}
	
	public String getIncludeExcludePriceTAM() {
		return includeExcludePriceTAM;
	}

	public void setIncludeExcludePriceTAM(String includeExcludePriceTAM) {
		this.includeExcludePriceTAM = includeExcludePriceTAM;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((profileId == null) ? 0 : profileId.hashCode());
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
		CommodityProfile other = (CommodityProfile) obj;
		if (profileId == null) {
			if (other.profileId != null)
				return false;
		} else if (!profileId.equals(other.profileId))
			return false;
		return true;
	}

}
