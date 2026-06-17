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
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.cost.entity.PcmCostType;

/**
 * Spring Data JPA Repository for PcmCostType entity.
 * Provides data access operations for cost type entities.
 */
@Repository
public interface PcmCostTypeRepository extends JpaRepository<PcmCostType, String> {

	@Query("SELECT c FROM PcmCostType c ORDER BY c.displayOrder ASC")
	List<PcmCostType> getAllCostTypes();

	@Query("SELECT c FROM PcmCostType c WHERE c.useInItemCategoryCost = TRUE ORDER BY c.displayOrder ASC")
	List<PcmCostType> getAllItemCategoryCostTypes();

	@Query("SELECT c FROM PcmCostType c WHERE c.useInRollup = TRUE ORDER BY c.displayOrder ASC")
	List<PcmCostType> getAllRollupCostTypes();

	@Query("SELECT c FROM PcmCostType c WHERE c.costTypeKey = :costTypeKey")
	PcmCostType getCostType(@Param("costTypeKey") String costTypeKey);

	/**
	 * Get all cost type keys as a set.
	 * Maps to Hibernate Criteria API code that collects all cost type keys.
	 * 
	 * @return set of all cost type keys
	 */
	@Query("SELECT c.costTypeKey FROM PcmCostType c")
    Set<String> getCostTypesKey();

}
