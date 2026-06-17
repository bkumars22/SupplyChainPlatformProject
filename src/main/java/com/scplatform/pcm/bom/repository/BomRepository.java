/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.bom.entity.Bom;

/**
 * Spring JPA Repository for Bom entity.
 * 
 * Provides database operations for Bom entities including CRUD operations
 * and custom query methods.
 * 
 * @author @suvasish.bhoi
 */
@Repository
public interface BomRepository extends JpaRepository<Bom, Long> {

	@Query(value = "SELECT CURRENT_VERSION FROM BOM_VERSION WHERE ITEM_KEY = :itemKey AND BUSINESS_ENTITY_KEY = :businessEntityKey", nativeQuery = true)
	Integer findCurrentBomVersion(@Param("itemKey") Long itemKey, @Param("businessEntityKey") Long businessEntityKey);

	@Modifying
	@Query(value = "INSERT INTO BOM_VERSION (ITEM_KEY, BUSINESS_ENTITY_KEY, CURRENT_VERSION) VALUES (:itemKey, :businessEntityKey, :version)", nativeQuery = true)
	void insertBomVersion(@Param("itemKey") Long itemKey,
	                      @Param("businessEntityKey") Long businessEntityKey,
	                      @Param("version") Integer version);

	@Modifying
	@Query(value = "UPDATE BOM_VERSION SET CURRENT_VERSION = :version WHERE ITEM_KEY = :itemKey AND BUSINESS_ENTITY_KEY = :businessEntityKey", nativeQuery = true)
	int updateBomVersion(@Param("itemKey") Long itemKey,
	                     @Param("businessEntityKey") Long businessEntityKey,
	                     @Param("version") Integer version);

	@Modifying
	@Query(value = "INSERT INTO COST_ROLLUP_CHANGE (BOM_KEY, EFFECTIVE_DATE, USER_KEY) VALUES (:bomKey, :effectiveDate, :userKey)", nativeQuery = true)
	void insertBomCostRollupChangeForTrigger(@Param("bomKey") Long bomKey,
	                                         @Param("effectiveDate") Date effectiveDate,
	                                         @Param("userKey") Long userKey);

	@Query(value = "SELECT DISTINCT " +
			"CONNECT_BY_ROOT b.BOM_KEY AS TOP_BOM_KEY, " +
			"b.BOM_KEY AS CURRENT_BOM_KEY, " +
			"h.EFFECTIVE_FROM_DT AS EFFECTIVE_FROM " +
			"FROM BOM_LINE_ITEM b " +
			"JOIN BOM_HEADER h ON b.BOM_KEY = h.BOM_KEY " +
			"WHERE h.STATUS IN ('APPROVED') " +
			"START WITH b.ITEM_KEY = :itemKey " +
			"CONNECT BY PRIOR b.BOM_KEY = b.SUB_BOM_KEY", nativeQuery = true)
	List<Object[]> findParentBomNthLevel(@Param("itemKey") long itemKey);

	/**
	 * Find a Bom by its external ID
	 * 
	 * @param bomExternalId the external ID of the BOM
	 * @return Optional containing the Bom if found, empty otherwise
	 */
	Optional<Bom> findByBomExternalId(String bomExternalId);

	/**
	 * Find all BOMs by name
	 * 
	 * @param bomName the name of the BOM
	 * @return List of BOMs matching the name
	 */
	List<Bom> findByBomName(String bomName);

	/**
	 * Find all BOMs by status
	 * 
	 * @param status the status of the BOM
	 * @return List of BOMs with the specified status
	 */
	List<Bom> findByStatus(String status);

	/**
	 * Find all BOMs by business entity key
	 * 
	 * @param businessEntityKey the key of the business entity
	 * @return List of BOMs for the business entity
	 */
	@Query("SELECT b FROM Bom b WHERE b.businessEntity.businessEntityKey = :businessEntityKey")
	List<Bom> findByBusinessEntityKey(@Param("businessEntityKey") Long businessEntityKey);

	/**
	 * Find all BOMs by item key
	 * 
	 * @param itemKey the key of the item
	 * @return List of BOMs for the item
	 */
	@Query("SELECT b FROM Bom b WHERE b.item.itemKey = :itemKey")
	List<Bom> findByItemKey(@Param("itemKey") Long itemKey);

	/**
	 * Find all top-level BOMs
	 * 
	 * @return List of top-level BOMs
	 */
	List<Bom> findByIsTopLevel(Boolean isTopLevel);

	/**
	 * Find BOMs by data source
	 * 
	 * @param dataSource the data source of the BOM
	 * @return List of BOMs from the specified data source
	 */
	List<Bom> findByDataSource(String dataSource);

	/**
	 * Find BOMs by type code
	 * 
	 * @param typeCode the type code of the BOM
	 * @return List of BOMs with the specified type code
	 */
	List<Bom> findByTypeCode(String typeCode);

	/**
	 * Find BOMs by status and business entity
	 * 
	 * @param status the status of the BOM
	 * @param businessEntityKey the key of the business entity
	 * @return List of BOMs matching the criteria
	 */
	@Query("SELECT b FROM Bom b WHERE b.status = :status AND b.businessEntity.businessEntityKey = :businessEntityKey")
	List<Bom> findByStatusAndBusinessEntity(@Param("status") String status, 
											@Param("businessEntityKey") Long businessEntityKey);

	/**
	 * Count BOMs by status
	 * 
	 * @param status the status of the BOM
	 * @return Count of BOMs with the specified status
	 */
	long countByStatus(String status);

	/**
	 * Check if a BOM exists by external ID
	 * 
	 * @param bomExternalId the external ID of the BOM
	 * @return true if BOM exists, false otherwise
	 */
	boolean existsByBomExternalId(String bomExternalId);

	/**
	 * Find all BOMs by item key and status list ordered by version descending
	 * 
	 * @param itemKey the key of the item
	 * @param statuses the list of statuses to match
	 * @return List of BOMs for the item with specified statuses ordered by version
	 */
	@Query("SELECT b FROM Bom b WHERE b.item.itemKey = :itemKey AND b.status IN :statuses ORDER BY b.bomVersion.version DESC, b.effectiveFrom DESC")
	List<Bom> findByItemKeyAndStatusIn(@Param("itemKey") Long itemKey, @Param("statuses") List<String> statuses);

	/**
	 * Find all BOMs by item key with optional effective date filtering.
	 * Returns BOMs where effectiveTo is null or >= effectiveOn AND effectiveFrom is null or <= effectiveOn.
	 * Results are ordered by bomVersion.version DESC, then effectiveFrom DESC.
	 * 
	 * @param itemKey the key of the item
	 * @param effectiveOn the effective date to filter by (may be null)
	 * @return List of BOMs for the item ordered by version descending
	 */
	@Query("SELECT b FROM Bom b WHERE b.item.itemKey = :itemKey " +
	       "AND (b.effectiveTo IS NULL OR b.effectiveTo >= :effectiveOn) " +
	       "AND (b.effectiveFrom IS NULL OR b.effectiveFrom <= :effectiveOn) " +
	       "ORDER BY b.bomVersion.version DESC, b.effectiveFrom DESC")
	List<Bom> findByItemKeyAndEffectiveDate(@Param("itemKey") Long itemKey, @Param("effectiveOn") Date effectiveOn);

	/**
	 * Find all BOMs by item key with effective date filtering and status filtering.
	 * Returns BOMs where effectiveTo is null or >= effectiveOn AND effectiveFrom is null or <= effectiveOn.
	 * Results are ordered by bomVersion.version DESC, then effectiveFrom DESC.
	 * 
	 * @param itemKey the key of the item
	 * @param statuses the list of statuses to match
	 * @param effectiveOn the effective date to filter by (may be null)
	 * @return List of BOMs for the item ordered by version descending
	 */
	@Query("SELECT b FROM Bom b WHERE b.item.itemKey = :itemKey " +
	       "AND b.status IN :statuses " +
	       "AND (b.effectiveTo IS NULL OR b.effectiveTo >= :effectiveOn) " +
	       "AND (b.effectiveFrom IS NULL OR b.effectiveFrom <= :effectiveOn) " +
	       "ORDER BY b.bomVersion.version DESC, b.effectiveFrom DESC")
	List<Bom> findByItemKeyStatusAndEffectiveDate(@Param("itemKey") Long itemKey, 
	                                               @Param("statuses") List<String> statuses,
	                                               @Param("effectiveOn") Date effectiveOn);

	/**
	 * Find all BOMs by item key ordered by version descending (no status or date filtering).
	 * Results are ordered by bomVersion.version DESC, then effectiveFrom DESC.
	 * 
	 * @param itemKey the key of the item
	 * @return List of all BOMs for the item ordered by version descending
	 */
	@Query("SELECT b FROM Bom b WHERE b.item.itemKey = :itemKey " +
	       "ORDER BY b.bomVersion.version DESC, b.effectiveFrom DESC")
	List<Bom> findByItemKeyOrderByVersionDesc(@Param("itemKey") Long itemKey);

	@Query("SELECT b FROM Bom b WHERE b.item.itemKey = :itemKey " +
			"AND b.status IN :statuses " +
			"AND (b.effectiveFrom IS NULL OR b.effectiveFrom <= :effectiveDate) " +
			"AND (b.effectiveTo   IS NULL OR b.effectiveTo   >= :effectiveDate) " +
			"ORDER BY b.bomKey DESC")
	List<Bom> findBomsForItem(@Param("itemKey") Long itemKey,
	                          @Param("effectiveDate") Date effectiveDate,
	                          @Param("statuses") List<String> statuses);

	@Query(name = "dashboard:bom")
	List<Object[]> findBomStatus(@Param("status") List<String> status,
	                             @Param("cutoffDate") Date cutoffDate);

	/**
	 * Executes the {@code dashboard:bomForOwner} JPA named query.
	 */
	@Query(name = "dashboard:bomForOwner")
	List<Object[]> findBomStatusForOwner(@Param("status") List<String> status,
	                                     @Param("cutoffDate") Date cutoffDate,
	                                     @Param("userId") String userId);
}