/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.allocationAudit.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.xlob.entity.XLOBAllocation;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@DiscriminatorValue("XLOB")
public class XLOBAuditHistory extends AllocationAuditHistory {

	@ManyToOne
	@JoinColumn(name = "TAM_KEY")
	private XLOBAllocation xlobAllocation;


	public XLOBAuditHistory() {
	}

	public XLOBAllocation getXlobAllocation() {
		return xlobAllocation;
	}

	public void setXlobAllocation(XLOBAllocation xlobAllocation) {
		this.xlobAllocation = xlobAllocation;
	}

	public XLOBAuditHistory(String userId, String userRole, String actionPerformed, String operationCode,
                            XLOBAllocation xlobAllocation, Site site, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = userRole;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.xlobAllocation = xlobAllocation;
		this.site = site;
		this.comment = comment;
		this.datePerformed = timeStamp;
		this.functionalGroup = xlobAllocation.getXlob();
	}

	public XLOBAuditHistory(String userId, String actionPerformed, String operationCode, XLOBAllocation xlobAllocation,
                            Site site, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.xlobAllocation = xlobAllocation;
		this.site = site;
		this.comment = comment;
		this.datePerformed = timeStamp;
		this.functionalGroup = xlobAllocation.getXlob();
	}

	public XLOBAuditHistory(String userId, String roleId, String actionPerformed, String operationCode,
                            XLOBAllocation xlobAllocation, Site site, Item item, BusinessEntity supplier, Date bucketStartDate,
                            Date bucketEndDate, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = roleId;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.xlobAllocation = xlobAllocation;
		this.site = site;
		this.item = item;
		this.supplier = supplier;
		this.bucketStartDate = bucketStartDate;
		this.bucketEndDate = bucketEndDate;
		this.comment = comment;
		this.datePerformed = timeStamp;
		this.functionalGroup = xlobAllocation.getXlob();
	}

	public XLOBAuditHistory(String userId, String roleId, String actionPerformed, String operationCode,
                            XLOBAllocation xlobAllocation, Site site, BusinessEntity supplier, Date bucketStartDate, Date bucketEndDate,
                            String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = roleId;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.xlobAllocation = xlobAllocation;
		this.site = site;
		this.supplier = supplier;
		this.bucketStartDate = bucketStartDate;
		this.bucketEndDate = bucketEndDate;
		this.comment = comment;
		this.datePerformed = timeStamp;
		this.functionalGroup = xlobAllocation.getXlob();
	}
}
