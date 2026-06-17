/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.cost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.cost.entity.PcmCostElement;
import com.scplatform.pcm.cost.entity.PcmCostElementId;

/**
 * Spring Data JPA Repository for PcmCostElement entity.
 * Provides data access operations for cost elements within a cost type structure.
 */
@Repository
public interface PcmCostElementRepository extends JpaRepository<PcmCostElement, PcmCostElementId> {

	@Query("SELECT c FROM PcmCostElement c WHERE c.id.costTypeKey = :costTypeKey AND c.id.costElementKey = :costElementKey")
	PcmCostElement getCostElement(@Param("costTypeKey") String costTypeKey,
							   @Param("costElementKey") String costElementKey);

	@Query("SELECT c FROM PcmCostElement c ORDER BY c.displayOrder ASC")
	List<PcmCostElement> getAllCostElements();
}
