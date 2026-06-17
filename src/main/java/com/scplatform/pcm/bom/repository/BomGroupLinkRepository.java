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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.bom.entity.BomGroupLink;
import com.scplatform.pcm.bom.entity.BomGroupLinkId;
import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.entity.BomGroup;

/**
 * Spring Data JPA Repository for BomGroupLink entity.
 * Provides data access operations for BomGroupLink entities (composite ID).
 */
@Repository
public interface BomGroupLinkRepository extends JpaRepository<BomGroupLink, BomGroupLinkId> {

	// ==================== Find by Foreign Keys ====================
	
	List<BomGroupLink> findByBom(Bom bom);
	List<BomGroupLink> findByBomGroup(BomGroup bomGroup);
	
	// ==================== Find by Combinations ====================
	
	List<BomGroupLink> findByBomAndBomGroup(Bom bom, BomGroup bomGroup);
	
	// ==================== Exists Checks ====================
	
	boolean existsByBomAndBomGroup(Bom bom, BomGroup bomGroup);
}
