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


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.bom.entity.PcmDefectType;
import java.util.List;

/**
 * Spring Data JPA Repository for PcmDefectType entity.
 * Provides data access operations for PcmDefectType entities.
 */
@Repository
public interface PcmDefectTypeRepository extends JpaRepository<PcmDefectType, Long> {

    /**
     * Get all defect types ordered by defect name in ascending order.
     * Replaces Hibernate Criteria method: session.createCriteria(PcmDefectType.class)
     *     .addOrder(Order.asc("defectName")).list()
     *
     * @return list of all PcmDefectType entities ordered by defectName
     */
    @Query("SELECT d FROM PcmDefectType d ORDER BY d.defectName ASC")
    List<PcmDefectType> getDefectTypes();

}
