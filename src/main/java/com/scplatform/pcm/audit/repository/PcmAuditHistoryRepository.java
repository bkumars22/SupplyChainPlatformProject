/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.audit.repository;

import com.scplatform.pcm.audit.entity.PcmAuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PcmAuditHistoryRepository extends JpaRepository<PcmAuditHistory, String> {

    /**
     * Get the minimum year from audit history records.
     * Used to determine the start year for audit reports.
     * 
     * @return the minimum year from ACTION_DATE column, or null if no records exist
     */
    @Query(value = "SELECT EXTRACT(YEAR FROM MIN(ACTION_DATE)) AS MIN_YEAR FROM PCM_AUDIT_HISTORY", nativeQuery = true)
    Integer getStartYear();

}
