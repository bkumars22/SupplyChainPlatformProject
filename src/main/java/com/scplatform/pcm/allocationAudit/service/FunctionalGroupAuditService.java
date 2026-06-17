/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.allocationAudit.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.scplatform.pcm.allocationAudit.entity.AllocationAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.FunctionalGroupAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.ParentFunctionalGroupAuditHistory;
import com.scplatform.pcm.allocationAudit.repository.AllocationAuditHistoryRepository;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.parentFunctionalGroup.entity.ParentFunctionalGroup;
import com.scplatform.pcm.parentFunctionalGroup.repository.ParentFunctionalGroupRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for FunctionalGroup audit operations.
 * Handles business logic related to functional group auditing.
 */
@Service
@RequiredArgsConstructor
public class FunctionalGroupAuditService {

	private final AllocationAuditHistoryRepository allocationAuditHistoryRepository;
	private final ParentFunctionalGroupRepository parentFunctionalGroupRepository;

	public List<FunctionalGroupAuditHistory> getFunctionalGroupAudit(Long fgID) {
		return allocationAuditHistoryRepository.getFunctionalGroupAudit(fgID);
	}

	public List<ParentFunctionalGroupAuditHistory> getParentFunctionalGroupAudit(Long pfgID) {
		return allocationAuditHistoryRepository.getParentFunctionalGroupAudit(pfgID);
	}

	public void recordFunctionalGroupAudit(String userId, String roleId, String actionPerformed,
			String operationCode, FunctionalGroup functionalGroup, String comment, Timestamp timeStamp) {
		AllocationAuditHistory functionalAudit = new AllocationAuditHistory(userId, defaultRole(roleId), actionPerformed,
				operationCode, functionalGroup, comment, timeStamp);
		allocationAuditHistoryRepository.save(functionalAudit);
	}

	public void recordFunctionalGroupAuditWithItem(String userId, String roleId, String actionPerformed,
			String operationCode, FunctionalGroup functionalGroup, Item item, String comment, Timestamp timeStamp) {
		AllocationAuditHistory functionalAuditHistory = new AllocationAuditHistory(userId, defaultRole(roleId),
				actionPerformed, operationCode, functionalGroup, item, comment, timeStamp);
		allocationAuditHistoryRepository.save(functionalAuditHistory);
	}

	public void recordParentFunctionalGroupAudit(String userId, String roleId, String actionPerformed,
			String operationCode, ParentFunctionalGroup parentFunctionalGroup, String comment, Timestamp timeStamp) {
		ParentFunctionalGroupAuditHistory parentFunctionalAuditHistory = new ParentFunctionalGroupAuditHistory(userId,
				defaultRole(roleId), actionPerformed, operationCode, parentFunctionalGroup, comment, timeStamp);
		allocationAuditHistoryRepository.save(parentFunctionalAuditHistory);
	}

	/** Oracle treats empty string as NULL; USER_ROLE column is NOT NULL. */
	private String defaultRole(String roleId) {
		return (roleId != null && !roleId.trim().isEmpty()) ? roleId : "SYSTEM";
	}

	/**
	 * Get parent functional group names for a given functional group
	 * 
	 * @param fg the functional group entity
	 * @return comma-separated string of parent functional group names
	 */
	public String getPFG(FunctionalGroup fg) {
		Set<ParentFunctionalGroup> pfgs = new HashSet<>();
		String pfgsString = "";
		
		// Fetch parent functional groups using repository method
		pfgs.addAll(parentFunctionalGroupRepository.getParentGroupListByFunctionalGroupName(fg.getName()));
		
		for (ParentFunctionalGroup pfg : pfgs) {
			if (pfgsString.isEmpty()) {
				pfgsString = pfg.getName();
			} else {
				pfgsString = pfgsString + ", " + pfg.getName();
			}
		}
		pfgs.clear();
		
		return pfgsString;
	}
}
