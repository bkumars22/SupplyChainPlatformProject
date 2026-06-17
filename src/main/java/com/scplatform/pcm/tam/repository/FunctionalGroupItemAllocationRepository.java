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
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.tam.entity.FunctionalGroupItemAllocation;

/**
 * Spring Data JPA Repository for FunctionalGroupItemAllocation entity.
 * Provides data access operations for FunctionalGroupItemAllocation entities using derived query methods.
 */
@Repository
public interface FunctionalGroupItemAllocationRepository extends JpaRepository<FunctionalGroupItemAllocation, Long> {
	
	Optional<FunctionalGroupItemAllocation> findById(Long id);

	@Query("SELECT SUM(ia.allocation) FROM FunctionalGroupItemAllocation ia "
			+ "WHERE ia.functionalGroupSupplierAllocation.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND ia.functionalGroupSupplierAllocation.startDate >= :fromDate "
			+ "AND ia.item = :item "
			+ "AND ia.allocation IS NOT NULL")
	Double sumAllocationByFunctionalGroupAndItemFromDate(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("item") Item item,
			@Param("fromDate") Date fromDate);

	@Query("SELECT SUM(ia.allocation) FROM FunctionalGroupItemAllocation ia "
			+ "WHERE ia.functionalGroupSupplierAllocation.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND ia.functionalGroupSupplierAllocation.tamAllocation.site = :site "
			+ "AND ia.functionalGroupSupplierAllocation.endDate >= :endDate")
	Double sumAllocationByFunctionalGroupSiteAndSupplierEndDate(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("site") Site site,
			@Param("endDate") Date endDate);

	@Query("SELECT SUM(ia.allocation) FROM FunctionalGroupItemAllocation ia "
			+ "WHERE ia.functionalGroupSupplierAllocation.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND ia.functionalGroupSupplierAllocation.tamAllocation.site = :site "
			+ "AND ia.functionalGroupSupplierAllocation.startDate = :startDate")
	Double sumItemAllocationByFunctionalGroupSiteAndStartDate(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("site") Site site,
			@Param("startDate") Date startDate);

	@Query("SELECT SUM(ia.allocation) FROM FunctionalGroupItemAllocation ia "
			+ "WHERE ia.functionalGroupSupplierAllocation.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND ia.functionalGroupSupplierAllocation.tamAllocation.site = :site "
			+ "AND ia.functionalGroupSupplierAllocation.startDate BETWEEN :startDate AND :endDate")
	Double sumItemAllocationByFunctionalGroupSiteAndDateRange(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("site") Site site,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Query("SELECT SUM(ia.allocation) FROM FunctionalGroupItemAllocation ia "
			+ "WHERE ia.functionalGroupSupplierAllocation.tamAllocation.functionalGroup = :functionalGroup "
			+ "AND ia.functionalGroupSupplierAllocation.tamAllocation.site.siteType = :siteType "
			+ "AND ia.functionalGroupSupplierAllocation.startDate = :startDate")
	Double sumItemAllocationByFunctionalGroupSiteTypeAndStartDate(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("siteType") String siteType,
			@Param("startDate") Date startDate);

	@Modifying
	@Transactional
	@Query("DELETE FROM FunctionalGroupItemAllocation fia "
			+ "WHERE fia.id IN ( "
			+ "  SELECT ia.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.functionalGroup fg "
			+ "  LEFT JOIN tam.supplierAllocations sa "
			+ "  LEFT JOIN sa.itemAllocations ia "
			+ "  LEFT JOIN ia.item i "
			+ "  WHERE i.itemKey = :itemKey "
			+ "  AND fg.functionalGroupId = :functionalGroupId "
			+ "  AND sa.startDate >= :startDate "
			+ "  AND sa.endDate >= :endDate "
			+ ")")
	int deleteItemAllocationByItemAndFG(
			@Param("itemKey") Long itemKey,
			@Param("functionalGroupId") Long functionalGroupId,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Modifying
	@Transactional
	@Query("DELETE FROM FunctionalGroupItemAllocation fia "
			+ "WHERE fia.id IN ( "
			+ "  SELECT ia.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.functionalGroup fg "
			+ "  LEFT JOIN tam.supplierAllocations sa "
			+ "  LEFT JOIN sa.itemAllocations ia "
			+ "  LEFT JOIN sa.businessEntity be "
			+ "  LEFT JOIN ia.item i "
			+ "  WHERE i.itemKey = :itemKey "
			+ "  AND fg.functionalGroupId = :functionalGroupId "
			+ "  AND sa.startDate >= :startDate "
			+ "  AND sa.endDate <= :endDate "
			+ "  AND tam.id = :tamId "
			+ "  AND be.businessEntityKey = :businessEntityKey "
			+ ")")
	int deleteItemAllocationByItemAndFGByDatePeriod(
			@Param("itemKey") Long itemKey,
			@Param("functionalGroupId") Long functionalGroupId,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			@Param("tamId") Long tamId,
			@Param("businessEntityKey") Long businessEntityKey);

	@Modifying
	@Transactional
	@Query("DELETE FROM FunctionalGroupItemAllocation fia "
			+ "WHERE fia.id IN ( "
			+ "  SELECT ia.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.functionalGroup fg "
			+ "  LEFT JOIN tam.supplierAllocations sa "
			+ "  LEFT JOIN sa.itemAllocations ia "
			+ "  LEFT JOIN ia.item i "
			+ "  WHERE i.itemKey = :itemKey "
			+ "  AND fg.functionalGroupId = :functionalGroupId "
			+ "  AND sa.businessEntity = :be "
			+ "  AND sa.startDate >= :startDate "
			+ "  AND sa.endDate >= :endDate "
			+ ")")
	int deleteItemAllocationByItemAndSupplier(
			@Param("be") BusinessEntity be,
			@Param("itemKey") Long itemKey,
			@Param("functionalGroupId") Long functionalGroupId,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Modifying
	@Transactional
	@Query("UPDATE FunctionalGroupItemAllocation fia SET fia.allocation = NULL "
			+ "WHERE fia.id IN ( "
			+ "  SELECT ia.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.supplierAllocations sa "
			+ "  LEFT JOIN sa.itemAllocations ia "
			+ "  LEFT JOIN ia.item i "
			+ "  WHERE i.itemKey = :itemKey "
			+ "  AND tam.id = :tamId "
			+ "  AND sa.endDate >= :endDate "
			+ ")")
	int removeItemAllocationByItemAndTAM(
			@Param("itemKey") Long itemKey,
			@Param("tamId") Long tamId,
			@Param("endDate") Date endDate);
}
