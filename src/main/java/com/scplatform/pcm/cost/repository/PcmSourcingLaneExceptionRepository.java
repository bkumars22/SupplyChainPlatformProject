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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.cost.entity.PcmSourcingLaneException;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;

/**
 * Spring Data JPA Repository for PcmSourcingLaneException entity.
 * Provides data access operations for sourcing lane exception entities.
 */
@Repository
public interface PcmSourcingLaneExceptionRepository extends JpaRepository<PcmSourcingLaneException, Long> {

	Site NULLSITE = new Site(-9999L);

	@Query("SELECT p FROM PcmSourcingLaneException p WHERE p.item = :item AND " +
		"(:bom IS NULL AND p.bom IS NULL OR p.bom = :bom) AND " +
		"(:supplier IS NULL AND p.supplier IS NULL OR p.supplier = :supplier) AND " +
		"(:fromSite IS NULL AND p.fromSite IS NULL OR p.fromSite = :fromSite) AND " +
		"(:toSite IS NULL AND p.toSite IS NULL OR p.toSite = :toSite) AND " +
		"(:currencyCode IS NULL AND p.currencyCode IS NULL OR p.currencyCode = :currencyCode)")
	List<PcmSourcingLaneException> findSLExceptionListByNaturalKey(
		@Param("item") Item item,
		@Param("bom") Bom bom,
		@Param("supplier") BusinessEntity supplier,
		@Param("fromSite") Site fromSite,
		@Param("toSite") Site toSite,
		@Param("currencyCode") String currencyCode);

	default List<PcmSourcingLaneException> findSLExceptionByNaturalKey(Item item, Bom bom, BusinessEntity fromBe,
			Site fromSite, Site toSite, String currencyCode) {
		return findSLExceptionListByNaturalKey(item, bom, fromBe, fromSite, toSite, currencyCode);
	}

	default List<PcmSourcingLaneException> findSLExceptionByNaturalKey(Item item, BusinessEntity fromBe,
			Site fromSite, Site toSite, String currencyCode) {
		return findSLExceptionListByNaturalKey(item, null, fromBe, fromSite, toSite, currencyCode);
	}
}
