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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.tam.entity.TAMAllocationArchival;

/**
 * Spring Data JPA Repository for TAMAllocationArchival entity.
 * Provides data access operations for TAMAllocationArchival entities using derived query methods.
 */
@Repository
public interface TAMAllocationArchivalRepository extends JpaRepository<TAMAllocationArchival, Long> {

	@Query("SELECT DISTINCT ta FROM TAMAllocationArchival ta "
			+ "LEFT JOIN FETCH ta.supplierAllocationsArchival sa "
			+ "LEFT JOIN FETCH sa.itemAllocationsArchival ia "
			+ "WHERE ta.site.siteType = 'REGION' "
			+ "AND ta.site.siteDescription = :region "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname)")
	List<TAMAllocationArchival> getRegionTAMAllocationByFGWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("region") String region);

	@Query("SELECT DISTINCT ta FROM TAMAllocationArchival ta "
			+ "LEFT JOIN FETCH ta.supplierAllocationsArchival sa "
			+ "LEFT JOIN FETCH sa.itemAllocationsArchival ia "
			+ "WHERE ta.site.siteType = 'REGION' "
			+ "AND ta.site.siteDescription = :region "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocationArchival> getRegionTAMAllocationByFGWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("region") String region,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocationArchival getRegionTAMAllocationByFGWithoutFilter(String fgname, String region) {
		List<TAMAllocationArchival> allocations = getRegionTAMAllocationByFGWithoutFilterInternal(fgname, region);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocationArchival getRegionTAMAllocationByFGWithFilter(String fgname, String region, Date startDate, Date endDate) {
		List<TAMAllocationArchival> allocations = getRegionTAMAllocationByFGWithFilterInternal(fgname, region, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocationArchival getRegionTAMAllocationByFG(String fgname, String region, Date startDate, Date endDate) {
		return getRegionTAMAllocationByFGWithFilter(fgname, region, startDate, endDate);
	}

	@Query("SELECT DISTINCT ta FROM TAMAllocationArchival ta "
			+ "LEFT JOIN FETCH ta.supplierAllocationsArchival sa "
			+ "LEFT JOIN FETCH sa.itemAllocationsArchival ia "
			+ "WHERE ta.site.siteType = 'GLOBAL' "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname)")
	List<TAMAllocationArchival> getGlobalTAMAllocationByFGWithoutFilterInternal(
			@Param("fgname") String fgname);

	@Query("SELECT DISTINCT ta FROM TAMAllocationArchival ta "
			+ "LEFT JOIN FETCH ta.supplierAllocationsArchival sa "
			+ "LEFT JOIN FETCH sa.itemAllocationsArchival ia "
			+ "WHERE ta.site.siteType = 'GLOBAL' "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocationArchival> getGlobalTAMAllocationByFGWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocationArchival getGlobalTAMAllocationByFGWithoutFilter(String fgname) {
		List<TAMAllocationArchival> allocations = getGlobalTAMAllocationByFGWithoutFilterInternal(fgname);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocationArchival getGlobalTAMAllocationByFGWithFilter(String fgname, Date startDate, Date endDate) {
		List<TAMAllocationArchival> allocations = getGlobalTAMAllocationByFGWithFilterInternal(fgname, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocationArchival getGlobalTAMAllocationByFG(String fgname, Date startDate, Date endDate) {
		return getGlobalTAMAllocationByFGWithFilter(fgname, startDate, endDate);
	}

	// ==================== WITH FGTYPE PARAMETER ====================

	// getRegionTAMAllocationByFG with fgType - WithoutFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocationArchival ta "
			+ "LEFT JOIN FETCH ta.supplierAllocationsArchival sa "
			+ "LEFT JOIN FETCH sa.itemAllocationsArchival ia "
			+ "WHERE ta.site.siteType = 'REGION' "
			+ "AND ta.site.siteDescription = :region "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType")
	List<TAMAllocationArchival> getRegionTAMAllocationByFGWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType,
			@Param("region") String region);

	// getRegionTAMAllocationByFG with fgType - WithFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocationArchival ta "
			+ "LEFT JOIN FETCH ta.supplierAllocationsArchival sa "
			+ "LEFT JOIN FETCH sa.itemAllocationsArchival ia "
			+ "WHERE ta.site.siteType = 'REGION' "
			+ "AND ta.site.siteDescription = :region "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocationArchival> getRegionTAMAllocationByFGWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType,
			@Param("region") String region,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocationArchival getRegionTAMAllocationByFGWithoutFilter(String fgname, String fgType, String region) {
		List<TAMAllocationArchival> allocations = getRegionTAMAllocationByFGWithoutFilterInternal(fgname, fgType, region);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocationArchival getRegionTAMAllocationByFGWithFilter(String fgname, String fgType, String region, Date startDate, Date endDate) {
		List<TAMAllocationArchival> allocations = getRegionTAMAllocationByFGWithFilterInternal(fgname, fgType, region, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocationArchival getRegionTAMAllocationByFG(String fgname, String fgType, String region, Date startDate, Date endDate) {
		return getRegionTAMAllocationByFGWithFilter(fgname, fgType, region, startDate, endDate);
	}

	// getGlobalTAMAllocationByFG with fgType - WithoutFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocationArchival ta "
			+ "LEFT JOIN FETCH ta.supplierAllocationsArchival sa "
			+ "LEFT JOIN FETCH sa.itemAllocationsArchival ia "
			+ "WHERE ta.site.siteType = 'GLOBAL' "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType")
	List<TAMAllocationArchival> getGlobalTAMAllocationByFGWithoutFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType);

	// getGlobalTAMAllocationByFG with fgType - WithFilter variant
	@Query("SELECT DISTINCT ta FROM TAMAllocationArchival ta "
			+ "LEFT JOIN FETCH ta.supplierAllocationsArchival sa "
			+ "LEFT JOIN FETCH sa.itemAllocationsArchival ia "
			+ "WHERE ta.site.siteType = 'GLOBAL' "
			+ "AND UPPER(ta.functionalGroup.name) = UPPER(:fgname) "
			+ "AND ta.functionalGroup.type = :fgType "
			+ "AND sa.startDate BETWEEN :startDate AND :endDate")
	List<TAMAllocationArchival> getGlobalTAMAllocationByFGWithFilterInternal(
			@Param("fgname") String fgname,
			@Param("fgType") String fgType,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	default TAMAllocationArchival getGlobalTAMAllocationByFGWithoutFilter(String fgname, String fgType) {
		List<TAMAllocationArchival> allocations = getGlobalTAMAllocationByFGWithoutFilterInternal(fgname, fgType);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocationArchival getGlobalTAMAllocationByFGWithFilter(String fgname, String fgType, Date startDate, Date endDate) {
		List<TAMAllocationArchival> allocations = getGlobalTAMAllocationByFGWithFilterInternal(fgname, fgType, startDate, endDate);
		return allocations.isEmpty() ? null : allocations.get(0);
	}

	default TAMAllocationArchival getGlobalTAMAllocationByFG(String fgname, String fgType, Date startDate, Date endDate) {
		return getGlobalTAMAllocationByFGWithFilter(fgname, fgType, startDate, endDate);
	}

}
