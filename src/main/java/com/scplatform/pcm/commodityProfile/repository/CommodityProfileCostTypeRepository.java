/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.commodityProfile.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.commodityProfile.entity.CommodityProfileCostType;

/**
 * Spring Data JPA Repository for CommodityProfileCostType entity.
 * Provides data access operations for CommodityProfileCostType entities.
 */
@Repository
public interface CommodityProfileCostTypeRepository extends JpaRepository<CommodityProfileCostType, Long> {


}
