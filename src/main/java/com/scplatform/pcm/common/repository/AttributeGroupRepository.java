/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.common.entity.AttributeGroup;

/**
 * Spring Data JPA Repository for AttributeGroup entity.
 * Provides data access operations for AttributeGroup entities.
 */
@Repository
public interface AttributeGroupRepository extends JpaRepository<AttributeGroup, Long> {

}
