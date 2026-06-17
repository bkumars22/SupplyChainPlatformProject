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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.commodityProfile.entity.CommodityProfile;
import com.scplatform.pcm.item.entity.ItemCategory;

/**
 * Spring Data JPA Repository for CommodityProfile entity.
 * Provides data access operations for CommodityProfile entities.
 */
@Repository
public interface CommodityProfileRepository extends JpaRepository<CommodityProfile, Long>, CommodityProfileCustomRepository {

	@Query("""
		SELECT COUNT(c) FROM CommodityProfile c
		WHERE LOWER(c.profileName) = LOWER(:commodityProfileName)
		""")
	Long getCommodityProfileCountByName(@Param("commodityProfileName") String commodityProfileName);

	@Query("""
		SELECT c FROM CommodityProfile c
		WHERE LOWER(c.profileName) = LOWER(:commodityProfileName)
		""")
	List<CommodityProfile> findCommodityProfileByNameInternal(@Param("commodityProfileName") String commodityProfileName);

	default Set<CommodityProfile> findCommodityProfileByName(String commodityProfileName) {
		return new HashSet<>(findCommodityProfileByNameInternal(commodityProfileName));
	}

	@Query("""
		SELECT c FROM CommodityProfile c
		WHERE c.profileName = :profileName
		""")
	List<CommodityProfile> getCommodityProfileByNameInternal(@Param("profileName") String profileName);

	default CommodityProfile getCommodityProfileByName(String profileName) {
		List<CommodityProfile> results = getCommodityProfileByNameInternal(profileName);
		return results.isEmpty() ? null : results.get(0);
	}

	@Query("""
		SELECT c.profileId FROM CommodityProfile c
		WHERE c.profileName = :profileName
		""")
	List<Long> getCommodityProfileIdByName(@Param("profileName") String profileName);

	@Query("""
		SELECT c FROM CommodityProfile c
		WHERE c.profileName = :profileName
		AND c.companyItemType = :companyItemType
		AND c.itemCategory = :itemCategory
		""")
	Optional<CommodityProfile> findCommodityProfileInternal(
			@Param("profileName") String profileName,
			@Param("companyItemType") String companyItemType,
			@Param("itemCategory") ItemCategory itemCategory);

	/**
	 * Execute a native SQL query passed as a parameter.
	 * 
	 * @param sql the native SQL query string
	 * @param parameters map of named parameters (optional)
	 * @return list of results as objects
	 */
	default List<Object[]> executeNativeSql(String sql, Map<String, Object> parameters) {
		return executeNativeSqlQuery(sql, parameters);
	}

	@Query("""
		SELECT COUNT(c) FROM CommodityProfile c
		WHERE LOWER(c.profileName) = LOWER(:commodityProfileName)
		""")
	Long getCommodityProfileCountByNameCriteria(@Param("commodityProfileName") String commodityProfileName);

	@Modifying
	@Query(value = "DELETE FROM ROLE_COMMODITY_PROFILE_MAPPING WHERE PROFILE_ID IN (:profileIds)", nativeQuery = true)
	int deleteRoleCommodityProfileMappingByIds(@Param("profileIds") List<Long> profileIds);

	@Modifying
	@Query(value = "DELETE FROM USER_COMMODITYPROFILE_MAPPING WHERE PROFILE_NAME IN (:profileNames)", nativeQuery = true)
	int deleteUserCommodityProfileMappingByNames(@Param("profileNames") List<String> profileNames);

	long deleteByProfileIdIn(List<Long> profileIds);

    @Modifying
    @Query(value = "DELETE FROM USER_COMMODITYPROFILE_MAPPING WHERE USER_KEY = :userKey AND PROFILE_NAME IN (:profileNames)", nativeQuery = true)
    int deleteUserCommodityProfileMappingByUserKeyAndProfileNames(@Param("userKey") Long userKey,
                                                                  @Param("profileNames") Set<String> profileNames);

}
