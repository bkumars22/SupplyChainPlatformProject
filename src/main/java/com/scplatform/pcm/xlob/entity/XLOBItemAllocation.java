/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.xlob.entity;


import java.sql.Timestamp;
import java.util.Date;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "XLOB_ITEM_ALLOCATION")
public class XLOBItemAllocation {

	@Id
	@GeneratedValue(generator = "XLOB_ITEM_ALLOCATION_SEQ")
	@SequenceGenerator(sequenceName = "XLOB_ITEM_ALLOCATION_SEQ", name = "XLOB_ITEM_ALLOCATION_SEQ", allocationSize = 1, initialValue = 1)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "XLOB_ALLOCATION_ID")
	private XLOBAllocation xlobAllocation;

	@ManyToOne(optional = false)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY")
	private BusinessEntity supplier;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ITEM_KEY")
	private Item item;

	@Column(name = "MANUFACTURER_PART")
	private String manufacturerPart;

	@Column(name = "START_DATE", nullable = false)
	private Date startDate;

	@Column(name = "END_DATE", nullable = false)
	private Date endDate;

	@Column(name = "ALLOCATION")
	private Double allocation;

	@Column(name = "CREATED_ON", nullable = false)
	private Timestamp createdOn;

	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public XLOBAllocation getXlobAllocation() {
		return xlobAllocation;
	}

	public void setXlobAllocation(XLOBAllocation xlobAllocation) {
		this.xlobAllocation = xlobAllocation;
	}

	public BusinessEntity getSupplier() {
		return supplier;
	}

	public void setSupplier(BusinessEntity supplier) {
		this.supplier = supplier;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getManufacturerPart() {
		return manufacturerPart;
	}

	public void setManufacturerPart(String manufacturerPart) {
		this.manufacturerPart = manufacturerPart;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Double getAllocation() {
		return allocation;
	}

	public void setAllocation(Double allocation) {
		this.allocation = allocation;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((manufacturerPart == null) ? 0 : manufacturerPart.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((supplier == null) ? 0 : supplier.hashCode());
		result = prime * result + ((xlobAllocation == null) ? 0 : xlobAllocation.hashCode());
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
		XLOBItemAllocation other = (XLOBItemAllocation) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (manufacturerPart == null) {
			if (other.manufacturerPart != null)
				return false;
		} else if (!manufacturerPart.equals(other.manufacturerPart))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (supplier == null) {
			if (other.supplier != null)
				return false;
		} else if (!supplier.equals(other.supplier))
			return false;
		if (xlobAllocation == null) {
			if (other.xlobAllocation != null)
				return false;
		} else if (!xlobAllocation.equals(other.xlobAllocation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "XLOBItemAllocation [id=" + id + ", supplier=" + supplier + ", item=" + item + ", manufacturerPart="
				+ manufacturerPart + ", startDate=" + startDate + ", endDate=" + endDate + ", allocation=" + allocation
				+ ", createdOn=" + createdOn + ", createdBy=" + createdBy + "]";
	}

}