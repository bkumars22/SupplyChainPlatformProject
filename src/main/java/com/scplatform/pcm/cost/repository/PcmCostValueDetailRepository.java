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

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.cost.entity.PcmCostRecordValue;
import com.scplatform.pcm.cost.entity.PcmCostValueDetail;

/**
 * Spring Data JPA Repository for PcmCostValueDetail entity.
 * Provides data access operations for detailed cost values.
 * 
 * PcmCostValueDetail - represents detailed cost values
 */
@Repository
public interface PcmCostValueDetailRepository extends JpaRepository<PcmCostValueDetail, Long> {

	/**
	 * Finds a cost value detail by natural key (cost record value and cost value name).
	 * Equivalent to legacy findCostValueDetailByNaturalKey static method.
	 *
	 * @param costRecordValue  the cost record value (required)
	 * @param costValueName    the cost value name (required)
	 * @return Optional containing the matching cost value detail, or empty if not found
	 */
	@Query("SELECT c FROM PcmCostValueDetail c WHERE c.costRecordValue = :costRecordValue AND c.costValueName = :costValueName")
	Optional<PcmCostValueDetail> findCostValueDetailByNaturalKey(
		@Param("costRecordValue") PcmCostRecordValue costRecordValue,
		@Param("costValueName") String costValueName);
}
