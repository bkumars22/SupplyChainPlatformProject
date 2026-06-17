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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.tam.entity.FunctionalGroupItemAllocationArchival;

/**
 * Spring Data JPA Repository for FunctionalGroupItemAllocationArchival entity.
 * Provides data access operations for FunctionalGroupItemAllocationArchival entities using derived query methods.
 */
@Repository
public interface FunctionalGroupItemAllocationArchivalRepository extends JpaRepository<FunctionalGroupItemAllocationArchival, Long> {

	// ==================== FIND BY PRIMARY COLUMNS ====================
	
	Optional<FunctionalGroupItemAllocationArchival> findById(Long id);

}
