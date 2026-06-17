/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.common.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.common.entity.MultiPurposeUses;

@Repository
public interface MultiPurposeUsesRepository extends JpaRepository<MultiPurposeUses, Long> {

	/**
	 * Get all MultiPurposeUses filtered by userId and filterType.
	 * Maps to Hibernate Criteria API code with eq and like restrictions.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2 with LIKE)
	 * @return set of matching MultiPurposeUses
	 */
	@Query("SELECT m FROM MultiPurposeUses m WHERE m.longParam1 = :userId AND m.stringParam2 LIKE :filterType")
	Set<MultiPurposeUses> getAllMultiPurposeList(@Param("userId") long userId, @Param("filterType") String filterType);

	/**
	 * Get all display grids filtered by userId and filterType.
	 * Maps to Hibernate HQL query that selects id and stringParam1.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2)
	 * @return list of Object arrays containing [id, stringParam1]
	 */
	@Query("SELECT m.id, m.stringParam1 FROM MultiPurposeUses m WHERE m.objectType = 'GRID_VIEW' AND m.stringParam2 = :filterType AND m.longParam1 = :userId")
	java.util.List<Object[]> getAllDisplay(@Param("userId") long userId, @Param("filterType") String filterType);

	/**
	 * Find MultiPurposeUses by userId and display id.
	 * Used to retrieve display column configuration.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param displayId the display id
	 * @return Optional containing the MultiPurposeUses if found
	 */
	@Query("SELECT m FROM MultiPurposeUses m WHERE m.longParam1 = :userId AND m.id = :displayId AND m.objectType = 'GRID_VIEW'")
	java.util.Optional<MultiPurposeUses> findByUserIdAndDisplayId(@Param("userId") long userId, @Param("displayId") long displayId);

	/**
	 * Find default display for user by userId and filterType.
	 * Returns display marked as default (longParam2 == 1).
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2)
	 * @return Optional containing the default display if found
	 */
	@Query("SELECT m FROM MultiPurposeUses m WHERE m.longParam1 = :userId AND m.stringParam2 = :filterType AND m.objectType = 'GRID_VIEW' AND m.longParam2 = 1")
	java.util.Optional<MultiPurposeUses> findDefaultDisplay(@Param("userId") long userId, @Param("filterType") String filterType);

	/**
	 * Clear default display flag for all displays of a user with specific filterType.
	 * Sets longParam2 to 0 for all records matching userId and filterType.
	 * Maps to Hibernate update query.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2)
	 * @return true if at least one record was updated, false otherwise
	 */
	@Modifying
	@Query("UPDATE MultiPurposeUses m SET m.longParam2 = 0 WHERE m.longParam1 = :userId AND m.stringParam2 = :filterType")
	int updateDefaultDisplay(@Param("userId") long userId, @Param("filterType") String filterType);

	/**
	 * Get available column configuration by userId, filterType, and displayName.
	 * Maps to Hibernate HQL query that selects clobData.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2)
	 * @param displayName the display name (stringParam1)
	 * @return Optional containing the clobData if found
	 */
	@Query("SELECT m.clobData FROM MultiPurposeUses m WHERE m.objectType = 'GRID_VIEW' AND m.stringParam2 = :filterType AND m.longParam1 = :userId AND m.stringParam1 = :displayName")
	java.util.Optional<String> getAvailableColumn(@Param("userId") long userId, @Param("filterType") String filterType, @Param("displayName") String displayName);

	/**
	 * Check if display already exists by userId, filterType, displayName, and optionally objectType.
	 * Maps to Hibernate Criteria API code with eq and like restrictions.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2 with LIKE)
	 * @param displayName the display name (stringParam1 with LIKE)
	 * @param objectType the object type (optional, with LIKE)
	 * @return Optional containing the MultiPurposeUses if found
	 */
	@Query("SELECT m FROM MultiPurposeUses m WHERE m.longParam1 = :userId AND m.stringParam2 LIKE :filterType AND m.stringParam1 LIKE :displayName AND (:objectType IS NULL OR m.objectType LIKE :objectType)")
	java.util.Optional<MultiPurposeUses> checkDisplayAlreadyExist(@Param("userId") long userId, @Param("filterType") String filterType, @Param("displayName") String displayName, @Param("objectType") String objectType);

}