/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.rebate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.rebate.entity.PcmRebateProgram;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA Repository for PcmRebateProgram entity.
 */
@Repository
public interface PcmRebateProgramRepository extends JpaRepository<PcmRebateProgram, Long> {

	long countByRebateName(String name);

	@Query("SELECT COUNT(r) FROM PcmRebateProgram r WHERE LOWER(r.rebateName) = LOWER(:name)")
	long countByRebateNameIgnoreCaseParam(@Param("name") String name);

    /**
     * Find rebate programs by BOM item keys with USE_BOM = 'Y'
     * @param itemKeys Set of item keys to search for
     * @return List of PcmRebateProgram entities matching the criteria
     */
    @Query(value = "SELECT DISTINCT rp.* " +
            "FROM PCM_REBATE_PROGRAM rp " +
            "INNER JOIN PCM_REBATE_RULE rr ON rp.REBATE_PROGRAM_KEY = rr.REBATE_PROGRAM_KEY " +
            "INNER JOIN PCM_REBATE_RULE_ITEM rri ON rr.REBATE_RULE_KEY = rri.REBATE_RULE_KEY " +
            "WHERE rri.ITEM_KEY IN (:itemKeys) " +
            "  AND rr.USE_BOM = 'Y' " +
            "  AND (rp.DELETE_FLAG = 'N' OR rp.DELETE_FLAG IS NULL) " +
            "  AND rp.STATUS IN ('APPROVED', 'PENDING')",
            nativeQuery = true)
    List<PcmRebateProgram> findRebateProgramsByBomItemKeysWithUseBom(@Param("itemKeys") Set<Long> itemKeys);

    @Query(name = "dashboard:rebateProgram")
    List<Object[]> findRebateProgramStatus(@Param("status") List<String> status,
                                           @Param("cutoffDate") Date cutoffDate);
    @Query(name = "dashboard:rebateProgramForOwner")
    List<Object[]> findRebateProgramStatusForOwner(@Param("status") List<String> status,
                                                   @Param("cutoffDate") Date cutoffDate,
                                                   @Param("userId") String userId);
}


