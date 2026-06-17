/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.priceTam.dto;

/**
 * @author SBhoi
 *
 */
public class PriceTAMOffsetCost {
	private final static String ALL = "*";
	private String dataSource;
	private String costType;
	private String commodityCode;
	private String itemBusinessName;
	private Integer offsetValue;
	
	public PriceTAMOffsetCost(String dataSource, String costType, String commodityCode, String itemBusinessName, Integer offsetValue) {
		super();
		this.dataSource = dataSource == null ? "" : dataSource;
		this.costType = costType == null ? "" : costType;
		this.commodityCode = commodityCode == null ? "" : commodityCode;
		this.itemBusinessName = itemBusinessName == null ?  "" : itemBusinessName;
		this.offsetValue = offsetValue;
	}
	
	public PriceTAMOffsetCost(String dataSource, String costType, String commodityCode, String itemBusinessName) {
		super();
		this.dataSource = dataSource == null ? "" : dataSource;
		this.costType = costType == null ? "" : costType;
		this.commodityCode = commodityCode == null ? "" : commodityCode;
		this.itemBusinessName = itemBusinessName == null ?  "" : itemBusinessName;
	}

	public Integer getOffsetValue() {
		return offsetValue;
	}

	public void setOffsetValue(Integer offsetValue) {
		this.offsetValue = offsetValue;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriceTAMOffsetCost other = (PriceTAMOffsetCost) obj;
		if (commodityCode == null) {
			if (other.commodityCode != null)
				return false;
		} else if (!commodityCode.equals(other.commodityCode))
			if(!other.commodityCode.equals(ALL))
				return false;
		if (costType == null) {
			if (other.costType != null)
				return false;
		} else if (!costType.equals(other.costType))
			if(!other.costType.equals(ALL))
				return false;
		if (dataSource == null) {
			if (other.dataSource != null)
				return false;
		} else if (!dataSource.equals(other.dataSource))
			if(!other.dataSource.equals(ALL))
				return false;
		if (itemBusinessName == null) {
			if (other.itemBusinessName != null)
				return false;
		} else if (!itemBusinessName.equals(other.itemBusinessName))
			if(!other.itemBusinessName.equals(ALL))
				return false;
		return true;
	}

	@Override
	public String toString() {
		return "PriceTAMOffsetCost [dataSource=" + dataSource + ", costType=" + costType + ", commodityCode="
				+ commodityCode + ", itemBusinessName=" + itemBusinessName + ", offsetValue=" + offsetValue + "]";
	}

}