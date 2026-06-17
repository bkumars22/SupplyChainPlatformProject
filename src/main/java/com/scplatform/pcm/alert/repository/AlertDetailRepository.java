/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.repository;

import com.scplatform.pcm.alert.entity.AlertDetail;
import com.scplatform.pcm.alert.enums.AlertDetailState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for AlertDetail entities.
 * Maps to the existing SC_ALERT_DETAIL table in Oracle.
 */
@Repository
public interface AlertDetailRepository extends JpaRepository<AlertDetail, Long> {

    /** Find all alerts for a specific user login, ordered by creation date descending */
    List<AlertDetail> findByUserLoginIdOrderByCreatedDesc(String userLoginId);

    /** Find alerts by alert type */
    List<AlertDetail> findByAlertType(String alertType);

    /** Find alerts by state */
    List<AlertDetail> findByState(AlertDetailState state);

    /** Find alerts by alert ID (unique identifier per event+context) */
    List<AlertDetail> findByAlertId(String alertId);

    /**
     * Check if an alert already exists for this alertId + userLoginId (duplicate prevention).
     * Uses ALERT_ID (template-evaluated unique identifier per event context).
     */
    boolean existsByAlertIdAndUserLoginId(String alertId, String userLoginId);

    /** Count non-dismissed alerts for a user */
    long countByUserLoginIdAndState(String userLoginId, AlertDetailState state);

    /** Delete expired alerts */
    @Modifying
    @Query("DELETE FROM AlertDetail a WHERE a.expirationDate < :now")
    int deleteExpiredAlerts(@Param("now") LocalDate now);

    /** Mark alert as dismissed */
    @Modifying
    @Query("UPDATE AlertDetail a SET a.state = com.scplatform.pcm.alert.enums.AlertDetailState.DISMISSED, a.dismissedBy = :dismissedBy WHERE a.id = :id")
    int markAsDismissed(@Param("id") Long id, @Param("dismissedBy") String dismissedBy);

    /** Mark all alerts as dismissed for a user */
    @Modifying
    @Query("UPDATE AlertDetail a SET a.state = com.scplatform.pcm.alert.enums.AlertDetailState.DISMISSED, a.dismissedBy = :userLoginId " +
           "WHERE a.userLoginId = :userLoginId AND a.state <> com.scplatform.pcm.alert.enums.AlertDetailState.DISMISSED")
    int dismissAllForUser(@Param("userLoginId") String userLoginId);
}

