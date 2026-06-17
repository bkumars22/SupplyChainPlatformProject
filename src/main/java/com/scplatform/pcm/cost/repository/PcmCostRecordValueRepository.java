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

import com.scplatform.pcm.cost.entity.PcmCostElement;
import com.scplatform.pcm.cost.entity.PcmCostRecord;
import com.scplatform.pcm.cost.entity.PcmCostRecordValue;

/**
 * Spring Data JPA Repository for PcmCostRecordValue entity.
 * Provides data access operations for individual cost values for a pricing range.
 * 
 * PcmCostRecordValue - represents individual cost values for a pricing range
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@Repository
public interface PcmCostRecordValueRepository extends JpaRepository<PcmCostRecordValue, Long> {

}
