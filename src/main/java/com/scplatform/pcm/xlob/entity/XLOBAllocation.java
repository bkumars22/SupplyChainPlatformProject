/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.xlob.entity;

import java.sql.Timestamp;
import java.util.Set;

import org.hibernate.annotations.Filter;
import org.hibernate.type.YesNoConverter;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.site.entity.Site;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "XLOB_ALLOCATION")
public class XLOBAllocation {

	@Id
	@GeneratedValue(generator = "XLOB_ALLOCATION_SEQ")
	@SequenceGenerator(sequenceName = "XLOB_ALLOCATION_SEQ", name = "XLOB_ALLOCATION_SEQ", allocationSize = 1, initialValue = 1)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "FUNCTIONAL_GROUP_ID")
	private FunctionalGroup xlob;

	@ManyToOne(optional = false)
	@JoinColumn(name = "SITE_KEY")
	private Site site;

	@Column(name = "EXTRACT_FLAG", nullable = false)
	private String extractFlag;

	@Column(name = "LAST_CHANGED_ON", nullable = false)
	private Timestamp lastChangedOn;

	@Column(name = "LAST_CHANGED_BY", nullable = false)
	private String lastChangedBy;
	
	@Column(name = "LAST_SYSTEM_UPDATED_ON", nullable = false)
	private Timestamp lastSystemUpdatedOn;

	@Column(name = "LAST_SYSTEM_UPDATED_BY", nullable = false)
	private String lastSystemChangedBy;

	@Column(name = "CREATED_ON", nullable = false)
	private Timestamp createdOn;

	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;

	@Column(name = "RECORD_SOURCE", nullable = true)
	private String recordSource;

	@Column(name = "ROLL_OVER_COUNT", nullable = true)
	private Integer rollOverCount;

	@Column(name = "NEXT_ROLLOVER_DATE", nullable = true)
	private Timestamp nextRolloverDate;
	
	@Column(name="CURRENT_DATA_DELETED", length=1)
	@Convert(converter = YesNoConverter.class)
	private Boolean isCurrentDataDeleted;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "XLOB_ALLOCATION_ID")
	@Filter(name = "xlobItemAllocationDateFilter", condition = "(START_DATE BETWEEN  :startDate and :endDate)")
	private Set<XLOBItemAllocation> itemAllocations;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FunctionalGroup getXlob() {
		return xlob;
	}

	public void setXlob(FunctionalGroup xlob) {
		this.xlob = xlob;
	}

	public Site getSite() {
		return site;
	}

	public Timestamp getLastSystemUpdatedOn() {
		return lastSystemUpdatedOn;
	}

	public void setLastSystemUpdatedOn(Timestamp lastSystemUpdatedOn) {
		this.lastSystemUpdatedOn = lastSystemUpdatedOn;
	}

	public String getLastSystemChangedBy() {
		return lastSystemChangedBy;
	}

	public void setLastSystemChangedBy(String lastSystemChangedBy) {
		this.lastSystemChangedBy = lastSystemChangedBy;
	}

	public Boolean getCurrentDataDeleted() {
		return isCurrentDataDeleted;
	}

	public void setCurrentDataDeleted(Boolean currentDataDeleted) {
		isCurrentDataDeleted = currentDataDeleted;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getExtractFlag() {
		return extractFlag;
	}

	public void setExtractFlag(String extractFlag) {
		this.extractFlag = extractFlag;
	}

	public Timestamp getLastChangedOn() {
		return lastChangedOn;
	}

	public void setLastChangedOn(Timestamp lastChangedOn) {
		this.lastChangedOn = lastChangedOn;
	}

	public String getLastChangedBy() {
		return lastChangedBy;
	}

	public void setLastChangedBy(String lastChangedBy) {
		this.lastChangedBy = lastChangedBy;
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

	public String getRecordSource() {
		return recordSource;
	}

	public void setRecordSource(String recordSource) {
		this.recordSource = recordSource;
	}

	public Integer getRollOverCount() {
		return rollOverCount;
	}

	public void setRollOverCount(Integer rollOverCount) {
		this.rollOverCount = rollOverCount;
	}

	public Timestamp getNextRolloverDate() {
		return nextRolloverDate;
	}

	public void setNextRolloverDate(Timestamp nextRolloverDate) {
		this.nextRolloverDate = nextRolloverDate;
	}

	public Boolean getIsCurrentDataDeleted() {
		return isCurrentDataDeleted;
	}

	public void setIsCurrentDataDeleted(Boolean isCurrentDataDeleted) {
		this.isCurrentDataDeleted = isCurrentDataDeleted;
	}

	public Set<XLOBItemAllocation> getItemAllocations() {
		return itemAllocations;
	}

	public void setItemAllocations(Set<XLOBItemAllocation> itemAllocations) {
		this.itemAllocations = itemAllocations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		result = prime * result + ((xlob == null) ? 0 : xlob.hashCode());
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
		XLOBAllocation other = (XLOBAllocation) obj;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		if (xlob == null) {
			if (other.xlob != null)
				return false;
		} else if (!xlob.equals(other.xlob))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "XLOBAllocation [id=" + id + ", xlob=" + xlob + ", site=" + site + ", extractFlag=" + extractFlag
				+ ", lastChangedOn=" + lastChangedOn + ", lastChangedBy=" + lastChangedBy + ", createdOn=" + createdOn
				+ ", createdBy=" + createdBy + ", recordSource=" + recordSource + ", rollOverCount=" + rollOverCount
				+ ", nextRolloverDate=" + nextRolloverDate + "]";
	}

}