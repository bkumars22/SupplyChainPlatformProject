/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.allocationAudit.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.allocationAudit.entity.AllocationAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.FunctionalGroupAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.ParentFunctionalGroupAuditHistory;

/**
 * Spring Data JPA Repository for AllocationAuditHistory entity.
 * Provides data access operations for AllocationAuditHistory entities.
 */
@Repository
public interface AllocationAuditHistoryRepository extends JpaRepository<AllocationAuditHistory, Long> {

	@Query("SELECT a FROM AllocationAuditHistory a WHERE a.functionalGroup.functionalGroupId = :fgID")
	List<AllocationAuditHistory> getFunctionalGroupAuditInternal(@Param("fgID") Long fgID);

	default List<FunctionalGroupAuditHistory> getFunctionalGroupAudit(Long fgID) {
		return getFunctionalGroupAuditInternal(fgID).stream()
				.map(audit -> (FunctionalGroupAuditHistory) audit)
				.toList();
	}

	@Query("SELECT DISTINCT a FROM AllocationAuditHistory a " +
	       "JOIN a.functionalGroup fg " +
	       "JOIN fg.parentFunctionalGroup pfg " +
	       "WHERE pfg.parentFunctionalGroupId = :pfgID")
	List<AllocationAuditHistory> getParentFunctionalGroupAuditInternal(@Param("pfgID") Long pfgID);

	default List<ParentFunctionalGroupAuditHistory> getParentFunctionalGroupAudit(Long pfgID) {
		return getParentFunctionalGroupAuditInternal(pfgID).stream()
				.map(audit -> (ParentFunctionalGroupAuditHistory) audit)
				.toList();
	}


}
