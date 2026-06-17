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
import com.scplatform.pcm.tam.entity.TAMAllocation;

/**
 * Spring Data JPA Repository for TAMAllocation entity.
 * Provides data access operations for TAMAllocation entities using derived query methods.
 */
@Repository
public interface TAMAllocationRepository extends JpaRepository<TAMAllocation, Long> {


	// ==================== CUSTOM QUERIES ====================
	
	/**
	 * Count TAM allocations by functional group key within a date range.
	 * Used to check if TAM exists for a functional group during fiscal periods.
	 * 
	 * @param functionalGroupKey The functional group key
	 * @param startDate The start date of the range
	 * @param endDate The end date of the range
	 * @return Count of matching allocations
	 */
	@Query("SELECT COUNT(ta) FROM TAMAllocation ta WHERE ta.functionalGroup.id = :functionalGroupKey " +
	       "AND ta.lastChangedOn BETWEEN :startDate AND :endDate")
	long countByFunctionalGroupKeyAndDateRange(@Param("functionalGroupKey") Long functionalGroupKey, 
	                                           @Param("startDate") Date startDate, 
	                                           @Param("endDate") Date endDate);

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE ta.site.siteType = 'SITE' "
			+ "AND ta.site.siteDescription = :site "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname)")
	List<TAMAllocation> getTAMAllocationByFGAndSiteWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("site") String site);

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE ta.site.siteType = 'SITE' "
			+ "AND ta.site.siteDescription = :site "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getTAMAllocationByFGAndSiteWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("site") String site,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getTAMAllocationByFGAndSiteWithoutFilter(String fgname, String site) {
		List<TAMAllocation> allocations = getTAMAllocationByFGAndSiteWithoutFilterInternal(fgname, site);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByFGAndSiteWithFilter(String fgname, String site, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getTAMAllocationByFGAndSiteWithFilterInternal(fgname, site, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByFGAndSite(String fgname, String site, Date startDate, Date endDate) {
		return getTAMAllocationByFGAndSiteWithFilter(fgname, site, startDate, endDate);
	}

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "LEFT JOIN FETCH sa.itemAllocations ia "
			+ "WHERE ta.site.siteType = 'REGION' "
			+ "AND ta.site.siteDescription = :region "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname)")
	List<TAMAllocation> getRegionTAMAllocationByFGWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("region") String region);

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "LEFT JOIN FETCH sa.itemAllocations ia "
			+ "WHERE ta.site.siteType = 'REGION' "
			+ "AND ta.site.siteDescription = :region "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getRegionTAMAllocationByFGWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("region") String region,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getRegionTAMAllocationByFGWithoutFilter(String fgname, String region) {
		List<TAMAllocation> allocations = getRegionTAMAllocationByFGWithoutFilterInternal(fgname, region);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getRegionTAMAllocationByFGWithFilter(String fgname, String region, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getRegionTAMAllocationByFGWithFilterInternal(fgname, region, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getRegionTAMAllocationByFG(String fgname, String region, Date startDate, Date endDate) {
		return getRegionTAMAllocationByFGWithFilter(fgname, region, startDate, endDate);
	}

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "LEFT JOIN FETCH sa.itemAllocations ia "
			+ "WHERE ta.site.siteType = 'GLOBAL' "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname)")
	List<TAMAllocation> getGlobalTAMAllocationByFGWithoutFilterInternal(
			@Param("fgname") String fgname);

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "LEFT JOIN FETCH sa.itemAllocations ia "
			+ "WHERE ta.site.siteType = 'GLOBAL' "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getGlobalTAMAllocationByFGWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getGlobalTAMAllocationByFGWithoutFilter(String fgname) {
		List<TAMAllocation> allocations = getGlobalTAMAllocationByFGWithoutFilterInternal(fgname);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getGlobalTAMAllocationByFGWithFilter(String fgname, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getGlobalTAMAllocationByFGWithFilterInternal(fgname, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getGlobalTAMAllocationByFG(String fgname, Date startDate, Date endDate) {
		return getGlobalTAMAllocationByFGWithFilter(fgname, startDate, endDate);
	}

	@Query("SELECT COUNT(DISTINCT ta.id) FROM TAMAllocation ta "
			+ "JOIN ta.supplierAllocations sa "
			+ "WHERE ta.functionalGroup.id = :functionalGroupId "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate ")
	long countTAMByFunctionalGroupInDateRange(
			@Param("functionalGroupId") Long functionalGroupId,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

    @Query("SELECT COUNT(DISTINCT ta.id) FROM TAMAllocation ta "
            + "JOIN ta.supplierAllocations sa "
            + "JOIN sa.itemAllocations ia "
            + "WHERE ta.functionalGroup.id = :functionalGroupId "
            + "AND ta.site.siteKey = :siteKey "
            + "AND sa.startDate BETWEEN :startDate AND :endDate "
            + "AND (ia.allocation > 0 or sa.allocation > 0)")
    long countTAMWithAllocationByFunctionalGroupAndSiteInDateRange(
            @Param("functionalGroupId") Long functionalGroupId,
            @Param("siteKey") Long siteKey,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT COUNT(DISTINCT ta.id) FROM TAMAllocation ta "
            + "JOIN ta.supplierAllocations sa "
            + "JOIN sa.itemAllocations ia "
            + "WHERE ta.functionalGroup.id = :functionalGroupId "
            + "AND sa.startDate BETWEEN :startDate AND :endDate "
            + "AND (ia.allocation > 0 or sa.allocation > 0)")
    long countTAMWithAllocationByFunctionalGroupInDateRange(
            @Param("functionalGroupId") Long functionalGroupId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT COUNT(DISTINCT ta.id) FROM TAMAllocation ta "
            + "JOIN ta.supplierAllocations sa "
            + "WHERE ta.functionalGroup.id = :functionalGroupId "
            + "AND sa.startDate BETWEEN :startDate AND :endDate")
    long countTAMByFunctionalGroupAndSiteInDateRange(
            @Param("functionalGroupId") Long functionalGroupId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

	// ==================== WITH FGTYPE PARAMETER ====================

	// getTAMAllocationByFGAndSite with fgType - WithoutFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE ta.site.siteType = 'SITE' "
			+ "AND ta.site.siteDescription = :site "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType")
	List<TAMAllocation> getTAMAllocationByFGAndSiteWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType,
			@Param("site") String site);

	// getTAMAllocationByFGAndSite with fgType - WithFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE ta.site.siteType = 'SITE' "
			+ "AND ta.site.siteDescription = :site "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getTAMAllocationByFGAndSiteWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType,
			@Param("site") String site,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getTAMAllocationByFGAndSiteWithoutFilter(String fgname, String fgType, String site) {
		List<TAMAllocation> allocations = getTAMAllocationByFGAndSiteWithoutFilterInternal(fgname, fgType, site);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByFGAndSiteWithFilter(String fgname, String fgType, String site, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getTAMAllocationByFGAndSiteWithFilterInternal(fgname, fgType, site, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByFGAndSite(String fgname, String fgType, String site, Date startDate, Date endDate) {
		return getTAMAllocationByFGAndSiteWithFilter(fgname, fgType, site, startDate, endDate);
	}

	// getRegionTAMAllocationByFG with fgType - WithoutFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "LEFT JOIN FETCH sa.itemAllocations ia "
			+ "WHERE ta.site.siteType = 'REGION' "
			+ "AND ta.site.siteDescription = :region "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType")
	List<TAMAllocation> getRegionTAMAllocationByFGWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType,
			@Param("region") String region);

	// getRegionTAMAllocationByFG with fgType - WithFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "LEFT JOIN FETCH sa.itemAllocations ia "
			+ "WHERE ta.site.siteType = 'REGION' "
			+ "AND ta.site.siteDescription = :region "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getRegionTAMAllocationByFGWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType,
			@Param("region") String region,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getRegionTAMAllocationByFGWithoutFilter(String fgname, String fgType, String region) {
		List<TAMAllocation> allocations = getRegionTAMAllocationByFGWithoutFilterInternal(fgname, fgType, region);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getRegionTAMAllocationByFGWithFilter(String fgname, String fgType, String region, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getRegionTAMAllocationByFGWithFilterInternal(fgname, fgType, region, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getRegionTAMAllocationByFG(String fgname, String fgType, String region, Date startDate, Date endDate) {
		return getRegionTAMAllocationByFGWithFilter(fgname, fgType, region, startDate, endDate);
	}

	// getGlobalTAMAllocationByFG with fgType - WithoutFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "LEFT JOIN FETCH sa.itemAllocations ia "
			+ "WHERE ta.site.siteType = 'GLOBAL' "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType")
	List<TAMAllocation> getGlobalTAMAllocationByFGWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType);

	// getGlobalTAMAllocationByFG with fgType - WithFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "LEFT JOIN FETCH sa.itemAllocations ia "
			+ "WHERE ta.site.siteType = 'GLOBAL' "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getGlobalTAMAllocationByFGWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getGlobalTAMAllocationByFGWithoutFilter(String fgname, String fgType) {
		List<TAMAllocation> allocations = getGlobalTAMAllocationByFGWithoutFilterInternal(fgname, fgType);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getGlobalTAMAllocationByFGWithFilter(String fgname, String fgType, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getGlobalTAMAllocationByFGWithFilterInternal(fgname, fgType, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getGlobalTAMAllocationByFG(String fgname, String fgType, Date startDate, Date endDate) {
		return getGlobalTAMAllocationByFGWithFilter(fgname, fgType, startDate, endDate);
	}

	// ==================== BY FG, SITE DESCRIPTION AND SITE TYPE ====================

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE UPPER(ta.site.siteType) = UPPER(:siteType) "
			+ "AND ta.site.siteDescription = :siteDescription "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname)")
	List<TAMAllocation> getTAMAllocationByFGSiteDescriptionAndSiteTypeWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("siteDescription") String siteDescription,
			@Param("siteType") String siteType);

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE UPPER(ta.site.siteType) = UPPER(:siteType) "
			+ "AND ta.site.siteDescription = :siteDescription "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getTAMAllocationByFGSiteDescriptionAndSiteTypeWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("siteDescription") String siteDescription,
			@Param("siteType") String siteType,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getTAMAllocationByFGSiteDescriptionAndSiteTypeWithoutFilter(
			String fgname, String siteDescription, String siteType) {
		List<TAMAllocation> allocations = getTAMAllocationByFGSiteDescriptionAndSiteTypeWithoutFilterInternal(
				fgname, siteDescription, siteType);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByFGSiteDescriptionAndSiteTypeWithFilter(
			String fgname, String siteDescription, String siteType, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getTAMAllocationByFGSiteDescriptionAndSiteTypeWithFilterInternal(
				fgname, siteDescription, siteType, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByFGSiteDescriptionAndSiteType(
			String fgname, String siteDescription, String siteType, Date startDate, Date endDate) {
		return getTAMAllocationByFGSiteDescriptionAndSiteTypeWithFilter(fgname, siteDescription, siteType, startDate, endDate);
	}

	// ==================== BY ID ====================

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE ta.id = :id")
	List<TAMAllocation> getTAMAllocationByIdWithoutFilterInternal(@Param("id") Long id);

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE ta.id = :id "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getTAMAllocationByIdWithFilterInternal(
			@Param("id") Long id,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getTAMAllocationByIdWithoutFilter(Long id) {
		List<TAMAllocation> allocations = getTAMAllocationByIdWithoutFilterInternal(id);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByIdWithFilter(Long id, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getTAMAllocationByIdWithFilterInternal(id, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationById(Long id, Date startDate, Date endDate) {
		return getTAMAllocationByIdWithFilter(id, startDate, endDate);
	}

	// ==================== BY FUNCTIONAL GROUP AND SITE ====================

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE ta.site = :site "
			+ "AND ta.functionalGroup = :functionalGroup")
	List<TAMAllocation> getTAMAllocationByFGAndSiteWithoutFilterInternal(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("site") Site site);

	@Query("SELECT DISTINCT ta FROM TAMAllocation ta "
			+ "LEFT JOIN FETCH ta.supplierAllocations sa "
			+ "WHERE ta.site = :site "
			+ "AND ta.functionalGroup = :functionalGroup "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocation> getTAMAllocationByFGAndSiteWithFilterInternal(
			@Param("functionalGroup") FunctionalGroup functionalGroup,
			@Param("site") Site site,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocation getTAMAllocationByFGAndSiteWithoutFilter(FunctionalGroup functionalGroup, Site site) {
		List<TAMAllocation> allocations = getTAMAllocationByFGAndSiteWithoutFilterInternal(functionalGroup, site);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByFGAndSiteWithFilter(
			FunctionalGroup functionalGroup, Site site, Date startDate, Date endDate) {
		List<TAMAllocation> allocations = getTAMAllocationByFGAndSiteWithFilterInternal(
				functionalGroup,
				site,
				startDate,
				endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocation getTAMAllocationByFGAndSite(
			FunctionalGroup functionalGroup, Site site, Date startDate, Date endDate) {
		return getTAMAllocationByFGAndSiteWithFilter(functionalGroup, site, startDate, endDate);
	}

	// ==================== UNIQUE ITEMS AND SUPPLIERS ====================

	@Query("SELECT DISTINCT ia.item FROM TAMAllocation ta "
			+ "JOIN ta.supplierAllocations sa "
			+ "JOIN sa.itemAllocations ia "
			+ "WHERE ta.id = :tamId "
			+ "AND sa.startDate >= :startDate")
	List<Item> getUniqueItemListFromTAM(
			@Param("tamId") Long tamId,
			@Param("startDate") Date startDate);

	@Query("SELECT DISTINCT sa.businessEntity FROM TAMAllocation ta "
			+ "JOIN ta.supplierAllocations sa "
			+ "WHERE ta.id = :tamId "
			+ "AND sa.startDate >= :startDate")
	List<BusinessEntity> getUniqueSupplierListFromTAM(
			@Param("tamId") Long tamId,
			@Param("startDate") Date startDate);

	@Modifying
	@Transactional
	@Query("UPDATE TAMAllocation t "
			+ "SET t.discpExtractFlag = 'P', t.extractFlag = 'P', t.rollOverCount = 0 "
			+ "WHERE t.id IN ( "
			+ "  SELECT tam.id FROM TAMAllocation tam "
			+ "  LEFT JOIN tam.functionalGroup fg "
			+ "  WHERE fg.functionalGroupId = :functionalGroupId "
			+ ")")
	int markTAMAsUpdated(@Param("functionalGroupId") Long functionalGroupId);

	@Modifying
	@Transactional
	@Query("UPDATE TAMAllocation t "
			+ "SET t.isCurrentDataDeleted = TRUE "
			+ "WHERE t.functionalGroup.functionalGroupId = :fgId")
	int markTAMAsAllAllocationDeleted(@Param("fgId") Long functionalGroupId);

}
