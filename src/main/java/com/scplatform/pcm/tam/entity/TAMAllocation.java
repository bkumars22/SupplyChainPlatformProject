/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.entity;


import java.sql.Timestamp;
import java.util.Set;

import org.hibernate.type.YesNoConverter;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.site.entity.Site;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAM_ALLOCATION")
public class TAMAllocation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAM_ALLOCATION_KEY_SEQ")
	@SequenceGenerator(name = "TAM_ALLOCATION_KEY_SEQ", sequenceName = "TAM_ALLOCATION_KEY_SEQ", allocationSize = 1)
	@Column(name = "TAM_ALLOCATION_ID", nullable = false)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "SITE_KEY", unique = true)
	private Site site;

	@ManyToOne(optional = false)
	@JoinColumn(name = "FUNCTIONAL_GROUP_ID", unique = true)
	private FunctionalGroup functionalGroup;

	@Column(name = "ALLOW_HEDGING", length = 1, nullable = false)
	@Convert(converter = YesNoConverter.class)
	private Boolean allowHedging;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tamAllocation")
	private Set<FunctionalGroupSupplierAllocation> supplierAllocations;

	@Column(name = "LAST_CHANGED_ON")
	private Timestamp lastChangedOn;

	@Column(name = "LAST_CHANGED_BY")
	private String lastChangedBy;

	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "EXTRACT_FLAG")
	private String extractFlag;

	@Column(name = "ROLL_OVER_COUNT")
	private Integer rollOverCount;

	@Column(name = "NEXT_ROLLOVER_DATE")
	private Timestamp nextRolloverDate;

	@Column(name = "CURRENT_DATA_DELETED", length = 1)
	@Convert(converter = YesNoConverter.class)
	private Boolean isCurrentDataDeleted;

	@Column(name = "DISCP_EXTRACT_FLAG")
	private String discpExtractFlag;

	@Column(name = "DISCP_ROLLOVER_EXTRACT_FLAG", insertable = false, updatable = false)
	private String discpRolloverExtractFlag;

	@Column(name = "SOURCE_LAST_CHANGED_BY", length = 255)
	private String sourceLastChangedBy;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public FunctionalGroup getFunctionalGroup() {
		return functionalGroup;
	}

	public void setFunctionalGroup(FunctionalGroup functionalGroup) {
		this.functionalGroup = functionalGroup;
	}

	public Boolean getAllowHedging() {
		return allowHedging;
	}

	public void setAllowHedging(Boolean allowHedging) {
		this.allowHedging = allowHedging;
	}

	public Set<FunctionalGroupSupplierAllocation> getSupplierAllocations() {
		return supplierAllocations;
	}

	public void setSupplierAllocations(Set<FunctionalGroupSupplierAllocation> supplierAllocations) {
		this.supplierAllocations = supplierAllocations;
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

	public String getExtractFlag() {
		return extractFlag;
	}

	public void setExtractFlag(String extractFlag) {
		this.extractFlag = extractFlag;
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

	public String getDiscpExtractFlag() {
		return discpExtractFlag;
	}

	public void setDiscpExtractFlag(String discpExtractFlag) {
		this.discpExtractFlag = discpExtractFlag;
	}

	public String getDiscpRolloverExtractFlag() {
		return discpRolloverExtractFlag;
	}

	public void setDiscpRolloverExtractFlag(String discpRolloverExtractFlag) {
		this.discpRolloverExtractFlag = discpRolloverExtractFlag;
	}
	public String getSourceLastChangedBy() {
		return sourceLastChangedBy;
	}
	public void setSourceLastChangedBy(String sourceLastChangedBy) {
		this.sourceLastChangedBy = sourceLastChangedBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((functionalGroup == null) ? 0 : functionalGroup.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
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
		TAMAllocation other = (TAMAllocation) obj;
		if (functionalGroup == null) {
			if (other.functionalGroup != null)
				return false;
		} else if (!functionalGroup.equals(other.functionalGroup))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TAMAllocation [id=" + id + ", site=" + site + ", functionalGroup=" + functionalGroup + ", allowHedging="
				+ allowHedging + ", lastChangedOn=" + lastChangedOn + ", lastChangedBy=" + lastChangedBy
				+ ", createdOn=" + createdOn + ", createdBy=" + createdBy + ", extractFlag=" + extractFlag
				+ ", rollOverCount=" + rollOverCount + ", nextRolloverDate=" + nextRolloverDate
				+ ", isCurrentDataDeleted=" + isCurrentDataDeleted + ", discpExtractFlag=" + discpExtractFlag
				+ ", discpRolloverExtractFlag=" + discpRolloverExtractFlag + "]";
	}

}