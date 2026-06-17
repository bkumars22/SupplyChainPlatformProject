/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.priceTam.dto;

import java.util.Date;

public class PriceTAMFiscalData implements Comparable<PriceTAMFiscalData>{

	private Date fiscalStartDate;
	private Date fiscalEndDate;
	private Double price;
	private Double allocation;
	private Boolean isAllocationVariance;
	private Boolean isPriceVariance;

	public PriceTAMFiscalData(Date fiscalStartDate, Date fiscalEndDate, Double price, Double allocation, Boolean isAllocationVariance,
                              Boolean isPriceVariance) {
		super();
		this.fiscalStartDate = fiscalStartDate;
		this.fiscalEndDate = fiscalEndDate;
		this.price = price;
		this.allocation = allocation;
		this.isAllocationVariance = isAllocationVariance;
		this.isPriceVariance = isPriceVariance;
	}

	public Date getFiscalStartDate() {
		return fiscalStartDate;
	}

	public void setFiscalStartDate(Date fiscalStartDate) {
		this.fiscalStartDate = fiscalStartDate;
	}

	public Date getFiscalEndDate() {
		return fiscalEndDate;
	}

	public void setFiscalEndDate(Date fiscalEndDate) {
		this.fiscalEndDate = fiscalEndDate;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getAllocation() {
		return allocation;
	}

	public void setAllocation(Double allocation) {
		this.allocation = allocation;
	}

	public boolean isAllocationVariance() {
		return isAllocationVariance;
	}
	
	public boolean getIsAllocationVariance() {
		return isAllocationVariance;
	}

	public void setAllocationVariance(boolean isAllocationVariance) {
		this.isAllocationVariance = isAllocationVariance;
	}
	
	public boolean isPriceVariance() {
		return isPriceVariance;
	}
	
	public boolean getIsPriceVariance() {
		return isPriceVariance;
	}

	public void setPriceVariance(boolean isPriceVariance) {
		this.isPriceVariance = isPriceVariance;
	}

	@Override
	public int compareTo(PriceTAMFiscalData o) {
		return this.fiscalStartDate.compareTo(o.fiscalStartDate);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allocation == null) ? 0 : allocation.hashCode());
		result = prime * result + ((fiscalStartDate == null) ? 0 : fiscalStartDate.hashCode());
		result = prime * result + ((isAllocationVariance == null) ? 0 : isAllocationVariance.hashCode());
		result = prime * result + ((isPriceVariance == null) ? 0 : isPriceVariance.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
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
		PriceTAMFiscalData other = (PriceTAMFiscalData) obj;
		if (allocation == null) {
			if (other.allocation != null)
				return false;
		} else if (!allocation.equals(other.allocation))
			return false;
		if (fiscalStartDate == null) {
			if (other.fiscalStartDate != null)
				return false;
		} else if (!fiscalStartDate.equals(other.fiscalStartDate))
			return false;
		if (isAllocationVariance == null) {
			if (other.isAllocationVariance != null)
				return false;
		} else if (!isAllocationVariance.equals(other.isAllocationVariance))
			return false;
		if (isPriceVariance == null) {
			if (other.isPriceVariance != null)
				return false;
		} else if (!isPriceVariance.equals(other.isPriceVariance))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PriceTAMFiscalData [fiscalStartDate=" + fiscalStartDate + ", price=" + price + ", allocation="
				+ allocation + ", isAllocationVariance=" + isAllocationVariance + ", isPriceVariance=" + isPriceVariance + "]";
	}
	
}
