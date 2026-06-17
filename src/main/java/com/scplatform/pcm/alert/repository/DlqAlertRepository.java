/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.repository;

import com.scplatform.pcm.alert.entity.DlqAlertRecord;
import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.DlqStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for DlqAlertRecord entities.
 * Maps to the SC_ALERT_DLQ table for tracking dead-lettered alerts.
 */
@Repository
public interface DlqAlertRepository extends JpaRepository<DlqAlertRecord, Long> {

    /** Find all unresolved DLQ entries */
    List<DlqAlertRecord> findByStatusOrderByReceivedDateDesc(DlqStatus status);

    /** Find DLQ entries by alert type */
    List<DlqAlertRecord> findByAlertTypeOrderByReceivedDateDesc(AlertTypes alertType);

    /** Find DLQ entry by original alert event ID */
    List<DlqAlertRecord> findByAlertEventId(String alertEventId);

    /** Count unresolved DLQ entries */
    long countByStatus(DlqStatus status);

    /** Mark a DLQ record as resolved */
    @Modifying
    @Query("""
            UPDATE DlqAlertRecord d
            SET d.status = com.scplatform.pcm.alert.enums.DlqStatus.RESOLVED,
                d.resolvedBy = :resolvedBy,
                d.resolutionNotes = :notes,
                d.resolvedDate = :now
            WHERE d.dlqKey = :key
            """)
    int markAsResolved(
            @Param("key") Long dlqKey,
            @Param("resolvedBy") String resolvedBy,
            @Param("notes") String notes,
            @Param("now") LocalDateTime now);

    /** Delete old resolved DLQ entries (cleanup) */
    @Modifying
    @Query("DELETE FROM DlqAlertRecord d WHERE d.status = com.scplatform.pcm.alert.enums.DlqStatus.RESOLVED AND d.resolvedDate < :cutoff")
    int deleteOldResolvedEntries(@Param("cutoff") LocalDateTime cutoff);
}

