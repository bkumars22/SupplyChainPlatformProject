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


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.commodityProfile.entity.RoleCommodityProfileMapping;
import com.scplatform.pcm.commodityProfile.entity.RoleCommodityProfileMappingId;

/**
 * Spring Data JPA Repository for RoleCommodityProfileMapping entity.
 * Provides data access operations for role-profile mappings (composite ID).
 */
@Repository
public interface RoleCommodityProfileMappingRepository extends JpaRepository<RoleCommodityProfileMapping, RoleCommodityProfileMappingId> {

    long deleteByCommodityProfile_ProfileIdIn(List<Long> profileIds);

	@Modifying
	@Query(value = "DELETE FROM ROLE_COMMODITY_PROFILE_MAPPING WHERE ROLE_KEY = :roleKey " +
			"AND PROFILE_ID IN (SELECT DISTINCT PROFILE_ID FROM COMMODITY_PROFILE WHERE PROFILE_NAME = :profileName) " +
			"AND BUSINESS_ENTITY_KEY = :businessEntityKey", nativeQuery = true)
	int deleteRoleProfileMapping(@Param("roleKey") Long roleKey,
			@Param("profileName") String profileName,
			@Param("businessEntityKey") Long businessEntityKey);
}

