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

import com.scplatform.pcm.cost.entity.PcmPricingScenario;

/**
 * Spring Data JPA Repository for PcmPricingScenario entity.
 * Provides data access operations for pricing scenarios.
 */
@Repository
public interface PcmPricingScenarioRepository extends JpaRepository<PcmPricingScenario, Long> {

    @Query("SELECT p FROM PcmPricingScenario p")
    List<PcmPricingScenario> getAllPricingScenarios();

    @Query("SELECT p FROM PcmPricingScenario p WHERE LOWER(p.pricingScenarioName) = LOWER(:pricingScenarioName)")
    PcmPricingScenario getPricingScenarioByName(@Param("pricingScenarioName") String pricingScenarioName);
}
