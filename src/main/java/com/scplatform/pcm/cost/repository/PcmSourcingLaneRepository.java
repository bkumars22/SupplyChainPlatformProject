/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2024, by E2open Inc. All rights reserved.
 */
package com.scplatform.pcm.cost.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.cost.entity.PcmSourcingLane;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;

/**
 * Spring Data JPA Repository for PcmSourcingLane entity.
 * Provides data access operations for sourcing lane entities.
 * 
 * Spring Data JPA Entity for PCM Sourcing Lane
 * Maps to PCM_SOURCING_LANE table
 * 
 * Copyright (c) 2024, by E2open Inc. All rights reserved.
 */
@Repository
public interface PcmSourcingLaneRepository extends JpaRepository<PcmSourcingLane, Long> {

	Site NULLSITE = new Site(-9999L);

	@Query("SELECT p FROM PcmSourcingLane p WHERE p.item = :item AND " +
		"(:bom IS NULL AND p.bom IS NULL OR p.bom = :bom) AND " +
		"(:supplier IS NULL AND p.supplier IS NULL OR p.supplier = :supplier) AND " +
		"(:fromSite IS NULL AND p.fromSite IS NULL OR p.fromSite = :fromSite) AND " +
		"(:toSite IS NULL AND p.toSite IS NULL OR p.toSite = :toSite) AND " +
		"(:currencyCode IS NULL AND p.currencyCode IS NULL OR p.currencyCode = :currencyCode)")
	PcmSourcingLane findSourcingLaneByNaturalKey(
		@Param("item") Item item,
		@Param("bom") Bom bom,
		@Param("supplier") BusinessEntity supplier,
		@Param("fromSite") Site fromSite,
		@Param("toSite") Site toSite,
		@Param("currencyCode") String currencyCode);

	default PcmSourcingLane findSLByNaturalKey(Item item, BusinessEntity fromBe, Site fromSite, Site toSite, String currencyCode) {
		return findSourcingLaneByNaturalKey(item, null, fromBe, fromSite, toSite, currencyCode);
	}

	@Query("SELECT p FROM PcmSourcingLane p WHERE p.item = :item AND " +
		"(:bom IS NULL AND p.bom IS NULL OR p.bom = :bom) AND " +
		"(:supplier IS NULL AND p.supplier IS NULL OR p.supplier = :supplier) AND " +
		"(:fromSite IS NULL AND p.fromSite IS NULL OR p.fromSite = :fromSite) AND " +
		"(:toSite IS NULL AND p.toSite IS NULL OR p.toSite = :toSite) AND " +
		"(:currencyCode IS NULL AND p.currencyCode IS NULL OR p.currencyCode = :currencyCode)")
	List<PcmSourcingLane> findSLListByNaturalKey(
		@Param("item") Item item,
		@Param("bom") Bom bom,
		@Param("supplier") BusinessEntity supplier,
		@Param("fromSite") Site fromSite,
		@Param("toSite") Site toSite,
		@Param("currencyCode") String currencyCode);

	default List<PcmSourcingLane> findSLByNaturalKey(Item item, Bom bom, BusinessEntity fromBe, Site fromSite, Site toSite, String currencyCode) {
		return findSLListByNaturalKey(item, bom, fromBe, fromSite, toSite, currencyCode);
	}

	/**
	 * Returns distinct fromSite keys for sourcing lanes matching item, bom (nullable) and optional supplier.
	 * Equivalent to legacy Hibernate Criteria with Projections.groupProperty("fromSite.id").
	 *
	 * @param item     the item (required)
	 * @param bom      the bom (nullable — if null, only lanes with no bom are matched)
	 * @param supplier the supplier (optional — if null, all suppliers are included)
	 * @return list of distinct fromSite key values
	 */
	@Query("SELECT DISTINCT p.fromSite.siteKey FROM PcmSourcingLane p WHERE p.item = :item " +
		"AND (:bom IS NULL AND p.bom IS NULL OR p.bom = :bom) " +
		"AND (:supplier IS NULL OR p.supplier = :supplier)")
	List<Long> findFromSitesForSourcingLanes(
		@Param("item") Item item,
		@Param("bom") Bom bom,
		@Param("supplier") BusinessEntity supplier);

	/**
	 * Returns distinct toSite keys for sourcing lanes matching item, bom (nullable) and optional supplier.
	 * Equivalent to legacy Hibernate Criteria with Projections.groupProperty("toSite.id").
	 *
	 * @param item     the item (required)
	 * @param bom      the bom (nullable — if null, only lanes with no bom are matched)
	 * @param supplier the supplier (optional — if null, all suppliers are included)
	 * @return list of distinct toSite key values
	 */
	@Query("SELECT DISTINCT p.toSite.siteKey FROM PcmSourcingLane p WHERE p.item = :item " +
		"AND (:bom IS NULL AND p.bom IS NULL OR p.bom = :bom) " +
		"AND (:supplier IS NULL OR p.supplier = :supplier)")
	List<Long> findToSitesForSourcingLanes(
		@Param("item") Item item,
		@Param("bom") Bom bom,
		@Param("supplier") BusinessEntity supplier);

	@Query("SELECT p FROM PcmSourcingLane p WHERE p.item = :item")
	List<PcmSourcingLane> findAllSourcingLanesForItem(@Param("item") Item item);

	@Query("SELECT p FROM PcmSourcingLane p WHERE p.item = :item " +
		"AND (:bom IS NULL AND p.bom IS NULL OR p.bom = :bom) " +
		"AND (:supplier IS NULL AND p.supplier IS NULL OR p.supplier = :supplier) " +
		"AND (:fromSite IS NULL OR p.fromSite = :fromSite) " +
		"AND (:toSite IS NULL OR p.toSite = :toSite) " +
		"AND (:currencyCode IS NULL AND p.currencyCode IS NULL OR p.currencyCode = :currencyCode)")
	List<PcmSourcingLane> findSourcingLanes(
		@Param("item") Item item,
		@Param("bom") Bom bom,
		@Param("supplier") BusinessEntity supplier,
		@Param("fromSite") Site fromSite,
		@Param("toSite") Site toSite,
		@Param("currencyCode") String currencyCode);

	/**
	 * @param item     the item (required)
	 * @param bomKey   the composite key of bom (nullable — if null, only lanes with no bom are matched)
	 * @param supplier the supplier (nullable — if null, only lanes with no supplier are matched)
	 * @return list of matching sourcing lanes
	 */
	@Query("SELECT p FROM PcmSourcingLane p WHERE p.item = :item " +
		"AND (:bomKey IS NULL AND p.bom IS NULL OR p.bom.bomKey = :bomKey) " +
		"AND (:supplier IS NULL AND p.supplier IS NULL OR p.supplier = :supplier)")
	List<PcmSourcingLane> findSourcingLanesForItemSupplier(
		@Param("item") Item item,
		@Param("bomKey") Long bomKey,
		@Param("supplier") BusinessEntity supplier);

	/**
	 * @param item   the item (required)
	 * @param bomKey the composite key of bom (nullable — if null, only lanes with no bom are matched)
	 * @return list of matching sourcing lanes
	 */
	@Query("SELECT p FROM PcmSourcingLane p WHERE p.item = :item " +
		"AND (:bomKey IS NULL AND p.bom IS NULL OR p.bom.bomKey = :bomKey)")
	List<PcmSourcingLane> findSourcingLanesForItem(
		@Param("item") Item item,
		@Param("bomKey") Long bomKey);

	default PcmSourcingLane saveOrUpdate(PcmSourcingLane lane) {
		return save(lane);
	}

	@Query(name = "dashboard:sourcingLane")
	List<Object[]> findSourcingLaneStatus(@Param("status") List<String> status,
	                                      @Param("cutoffDate") Date cutoffDate,
	                                      @Param("userKey") Long userKey);

	/**
	 * Executes the {@code dashboard:sourcingLaneForOwner} JPA named query.
	 */
	@Query(name = "dashboard:sourcingLaneForOwner")
	List<Object[]> findSourcingLaneStatusForOwner(@Param("status") List<String> status,
	                                              @Param("cutoffDate") Date cutoffDate,
	                                              @Param("userId") String userId);
}
