/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.scheduler.repository;

import com.scplatform.pcm.scheduler.entity.JobConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobConfigRepository extends JpaRepository<JobConfig, Long> {
    
    /**
     * Retrieves all enabled job configurations.
     * @return List of enabled JobConfig entities
     */
    List<JobConfig> findByEnabledTrue();
}

