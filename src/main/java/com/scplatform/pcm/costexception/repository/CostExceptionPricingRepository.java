/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.costexception.repository;

import com.scplatform.pcm.costexception.entity.CostExceptionPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostExceptionPricingRepository extends JpaRepository<CostExceptionPricing, Long> {
}
