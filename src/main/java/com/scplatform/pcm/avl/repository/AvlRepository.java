/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.avl.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.avl.entity.Avl;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;

/**
 * Spring Data JPA Repository for Avl entity.
 * Provides data access operations for Avl entities.
 */
@Repository
public interface AvlRepository extends JpaRepository<Avl, Long> {

	// ==================== Find by Entities ====================

	@Query("SELECT a FROM Avl a WHERE a.item = :item AND a.supplier = :supplier")
	List<Avl> findByItemAndSupplier(@Param("item") Item item, @Param("supplier") BusinessEntity supplier);

}
