/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.functionalGroup.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroupLob;

/**
 * Spring Data JPA Repository for FunctionalGroupLob entity.
 * Provides data access operations for FunctionalGroupLob entities.
 */
@Repository
public interface FunctionalGroupLobRepository extends JpaRepository<FunctionalGroupLob, Long> {


}
