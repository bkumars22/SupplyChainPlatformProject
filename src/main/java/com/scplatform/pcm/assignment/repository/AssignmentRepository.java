/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.assignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.assignment.entity.Assignment;

/**
 * Spring Data JPA Repository for Assignment entity.
 * Provides data access operations for Assignment entities (base class with inheritance).
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

}
