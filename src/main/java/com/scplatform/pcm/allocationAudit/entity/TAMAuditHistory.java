/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.allocationAudit.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.tam.entity.TAMAllocation;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@DiscriminatorValue("TAM")
public class TAMAuditHistory extends AllocationAuditHistory {

	@ManyToOne
	@JoinColumn(name = "TAM_KEY")
	private TAMAllocation tamAllocation;

	public TAMAuditHistory() {
	}
	
	public TAMAuditHistory(String userId, String userRole,
                           String actionPerformed, String operationCode, TAMAllocation tamAllocation, Site site, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = userRole;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.tamAllocation = tamAllocation;
		this.site = site;
		this.comment = comment;
		this.datePerformed = timeStamp;
		this.functionalGroup = tamAllocation.getFunctionalGroup();
	}
	
	public TAMAuditHistory(String userId, String actionPerformed, String operationCode, TAMAllocation tamAllocation, Site site, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.tamAllocation = tamAllocation;
		this.site = site;
		this.comment = comment;
		this.datePerformed = timeStamp;
		this.functionalGroup = tamAllocation.getFunctionalGroup();
	}
	
	public TAMAuditHistory(String userId, String roleId, String actionPerformed, String operationCode, TAMAllocation tamAllocation, Site site, Item item, BusinessEntity supplier,
                           Date bucketStartDate, Date bucketEndDate, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = roleId;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.tamAllocation = tamAllocation;
		this.site = site;
		this.item = item;
		this.supplier = supplier;
		this.bucketStartDate = bucketStartDate;
		this.bucketEndDate = bucketEndDate;
		this.comment = comment;
		this.datePerformed = timeStamp;
		this.functionalGroup = tamAllocation.getFunctionalGroup();
	}
	
	public TAMAuditHistory(String userId, String roleId, String actionPerformed, String operationCode, TAMAllocation tamAllocation, Site site, BusinessEntity supplier, 
			Date bucketStartDate, Date bucketEndDate, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = roleId;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.tamAllocation = tamAllocation;
		this.site = site;
		this.supplier = supplier;
		this.bucketStartDate = bucketStartDate;
		this.bucketEndDate = bucketEndDate;
		this.comment = comment;
		this.datePerformed = timeStamp;
		this.functionalGroup = tamAllocation.getFunctionalGroup();
	}

	public TAMAllocation getTamAllocation() {
		return tamAllocation;
	}

	public void setTamAllocation(TAMAllocation tamAllocation) {
		this.tamAllocation = tamAllocation;
	}
}
