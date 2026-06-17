/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.allocationAudit.entity;

import java.sql.Timestamp;

import com.scplatform.pcm.parentFunctionalGroup.entity.ParentFunctionalGroup;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("PFG")
public class ParentFunctionalGroupAuditHistory extends AllocationAuditHistory {

	// -----------------------------------------------------------------------
	// Operation-code constants
	// -----------------------------------------------------------------------

	public static final String OPERATION_CREATEPFG    = "PFG CREATED";
	public static final String OPERATION_DELETEPFG    = "PFG DELETED";
	public static final String OPERATION_REMOVEFG     = "REMOVE FG";
	public static final String OPERATION_ADDFG        = "ADD FG";
	public static final String OPERATION_RENAMEPARENT = "PFG RENAME";
	public static final String OPERATION_UPDATEPARENT = "PFG UPDATE";
	/** A FunctionalGroup was added to a ParentFunctionalGroup via upload. */
	public static final String OPERATION_ADDPFG       = "ADDPFG";

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PFG_KEY", unique = true, nullable = false)
	private ParentFunctionalGroup parentFunctionalGroup;

	public ParentFunctionalGroupAuditHistory() {
	}
	
	public ParentFunctionalGroupAuditHistory(String userId, String userRole,
			String actionPerformed, String operationCode, ParentFunctionalGroup parentFunctionalGroup, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = userRole;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.parentFunctionalGroup = parentFunctionalGroup;
		this.comment = comment;
		this.datePerformed = timeStamp;
	}

	public ParentFunctionalGroup getParentFunctionalGroup() {
		return parentFunctionalGroup;
	}

	public void setParentFunctionalGroup(ParentFunctionalGroup parentFunctionalGroup) {
		this.parentFunctionalGroup = parentFunctionalGroup;
	}
	
}
