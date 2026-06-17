/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.tam.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.tam.entity.FunctionalGroupSupplierAllocation;
import com.scplatform.pcm.tam.entity.TAMAllocation;

/**
 * Spring Data JPA Repository for FunctionalGroupSupplierAllocation entity.
 * Provides data access operations for FunctionalGroupSupplierAllocation entities using derived query methods.
 */
@Repository
public interface FunctionalGroupSupplierAllocationRepository extends JpaRepository<FunctionalGroupSupplierAllocation, Long> {

	@Query("SELECT SUM(sa.allocation) FROM FunctionalGroupSupplierAllocation sa "
			+ "WHERE sa.tamAllocation.id = :tamAllocationId "
			+ "AND sa.startDate = :startDate")
	Double getTotalSupplierAllocationOnDate(
			@Param("tamAllocationId") Long tamAllocationId,
			@Param("startDate") Date startDate);

	@Query("SELECT SUM(sa.allocation) FROM FunctionalGroupSupplierAllocation sa "
			+ "WHERE sa.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND sa.tamAllocation.site = :site "
			+ "AND sa.endDate >= :endDate")
	Double sumAllocationByFunctionalGroupAndSiteFromEndDate(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("site") Site site,
			@Param("endDate") Date endDate);

	@Query("SELECT SUM(sa.allocation) FROM FunctionalGroupSupplierAllocation sa "
			+ "WHERE sa.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND sa.tamAllocation.site = :site "
			+ "AND sa.startDate = :startDate")
	Double sumAllocationByFunctionalGroupSiteAndStartDate(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("site") Site site,
			@Param("startDate") Date startDate);

	@Query("SELECT SUM(sa.allocation) FROM FunctionalGroupSupplierAllocation sa "
			+ "WHERE sa.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND sa.tamAllocation.site = :site "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	Double sumAllocationByFunctionalGroupSiteAndDateRange(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("site") Site site,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Query("SELECT 1 FROM FunctionalGroupSupplierAllocation sa "
			+ "LEFT JOIN sa.itemAllocations ia "
			+ "WHERE sa.tamAllocation.functionalGroup.functionalGroupId = :fgId "
			+ "AND sa.tamAllocation.site.siteKey = :siteKey "
			+ "AND sa.endDate >= CURRENT_DATE "
			+ "AND (sa.allocation > 0 OR ia.allocation > 0)")
	List<Integer> findSupplierOrItemAllocationExist(
			@Param("fgId") Long fgId,
			@Param("siteKey") Long siteKey);

	@Query("SELECT 1 FROM FunctionalGroupSupplierAllocation sa "
			+ "LEFT JOIN sa.itemAllocations ia "
			+ "WHERE sa.tamAllocation.functionalGroup.functionalGroupId = :fgId "
			+ "AND sa.tamAllocation.site.siteKey = :siteKey "
			+ "AND sa.endDate >= :pastStartDate "
			+ "AND sa.endDate < :currentMonthStart "
			+ "AND (sa.allocation >= 0 OR ia.allocation >= 0)")
	List<Integer> findPastSupplierOrItemAllocationExist(
			@Param("fgId") Long fgId,
			@Param("siteKey") Long siteKey,
			@Param("pastStartDate") Date pastStartDate,
			@Param("currentMonthStart") Date currentMonthStart);

	@Query("SELECT SUM(sa.allocation) FROM FunctionalGroupSupplierAllocation sa "
			+ "WHERE sa.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND sa.tamAllocation.site.siteType = :siteType "
			+ "AND sa.startDate = :startDate")
	Double sumSupplierAllocationByFunctionalGroupSiteTypeAndStartDate(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("siteType") String siteType,
			@Param("startDate") Date startDate);

	@Query("SELECT fsa.startDate FROM FunctionalGroupSupplierAllocation fsa "
			+ "WHERE fsa.id IN ( "
			+ "  SELECT sa.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.supplierAllocations sa "
			+ "  LEFT JOIN sa.itemAllocations ia "
			+ "  LEFT JOIN ia.item i "
			+ "  WHERE i.itemKey = :itemKey "
			+ "  AND tam.id = :tamId "
			+ "  AND sa.businessEntity = :be "
			+ "  AND sa.endDate >= :startDate "
			+ "  AND ia.allocation IS NOT NULL "
			+ ")")
	List<Date> getBucketsListToBeDefaulted(
			@Param("be") BusinessEntity be,
			@Param("itemKey") Long itemKey,
			@Param("startDate") Date startDate,
			@Param("tamId") Long tamId);

	@Modifying
	@Transactional
	@Query("DELETE FROM FunctionalGroupSupplierAllocation fsa "
			+ "WHERE fsa.id IN ( "
			+ "  SELECT sa.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.functionalGroup fg "
			+ "  LEFT JOIN tam.supplierAllocations sa "
			+ "  WHERE sa.businessEntity IN :supplierList "
			+ "  AND fg.functionalGroupId = :functionalGroupId "
			+ "  AND sa.endDate <= :endDate "
			+ ")")
	int deleteSupplierAllocationByBusinessEntityAndFG(
			@Param("supplierList") Set<BusinessEntity> businessEntities,
			@Param("functionalGroupId") Long functionalGroupId,
			@Param("endDate") Date endDate);

	@Modifying
	@Transactional
	@Query("DELETE FROM FunctionalGroupSupplierAllocation fsa "
			+ "WHERE fsa.id IN ( "
			+ "  SELECT sa.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.functionalGroup fg "
			+ "  LEFT JOIN tam.supplierAllocations sa "
			+ "  WHERE sa.businessEntity IN :supplierList "
			+ "  AND fg.functionalGroupId = :functionalGroupId "
			+ "  AND sa.startDate >= :startDate "
			+ "  AND sa.endDate <= :endDate "
			+ ")")
	int deleteSupplierAllocationByBusinessEntityAndFGByDatePeriod(
			@Param("supplierList") Set<BusinessEntity> businessEntities,
			@Param("functionalGroupId") Long functionalGroupId,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Modifying
	@Transactional
	@Query("UPDATE FunctionalGroupSupplierAllocation fsa "
			+ "SET fsa.allocation = NULL "
			+ "WHERE fsa.id IN ( "
			+ "  SELECT sa.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.supplierAllocations sa "
			+ "  WHERE sa.businessEntity IN :supplierList "
			+ "  AND tam.id = :tamId "
			+ "  AND sa.endDate >= :endDate "
			+ ")")
	int removeSupplierAllocationByBusinessEntityAndTAM(
			@Param("supplierList") Set<BusinessEntity> businessEntities,
			@Param("tamId") Long tamId,
			@Param("endDate") Date endDate);

	// ==================== TAM ITEM CFG LOADER HELPERS ====================

	/**
	 * Find the supplier allocation for a specific TAM + supplier + bucket start date.
	 * Used by TAMItemCFGLoader to attach item allocations to the correct bucket row.
	 */
	@Query("SELECT sa FROM FunctionalGroupSupplierAllocation sa "
			+ "WHERE sa.tamAllocation.id = :tamId "
			+ "AND sa.businessEntity.businessEntityKey = :supplierKey "
			+ "AND sa.startDate = :startDate")
	Optional<FunctionalGroupSupplierAllocation> findByTamAllocationIdAndSupplierKeyAndStartDate(
			@Param("tamId") Long tamId,
			@Param("supplierKey") Long supplierKey,
			@Param("startDate") Date startDate);

	/**
	 * Delete all supplier allocations (and their cascaded item allocations) for a given
	 * TAMAllocation + supplier. Used by TAMAllocationMassUpdateCFGLoader before reinserting.
	 */
	@Modifying
	@Transactional
	@Query("DELETE FROM FunctionalGroupSupplierAllocation sa "
			+ "WHERE sa.tamAllocation.id = :tamId "
			+ "AND sa.businessEntity.businessEntityKey = :supplierKey")
	int deleteByTamAllocationIdAndSupplierKey(
			@Param("tamId") Long tamId,
			@Param("supplierKey") Long supplierKey);

}
