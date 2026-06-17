/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.repository;

import com.scplatform.pcm.alert.entity.PcmAlertSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for PcmAlertSubscription entities.
 * Maps to the existing PCM_ALERT_SUBSCRIPTION table.
 *
 * <p>Used by {@link com.scplatform.pcm.alert.service.AlertSubscriptionService}
 * to find which users should receive a given alert.</p>
 */
@Repository
public interface AlertSubscriptionRepository extends JpaRepository<PcmAlertSubscription, Long> {

    /**
     * Finds all active subscriptions (subscribeFlag = 1) for a given alert type.
     * Eagerly fetches options to avoid N+1 queries.
     */
    @Query("""
            SELECT DISTINCT s FROM PcmAlertSubscription s
            LEFT JOIN FETCH s.options
            WHERE s.alertType = :alertType
              AND s.subscribeFlag = 1
            """)
    List<PcmAlertSubscription> findActiveSubscriptions(@Param("alertType") String alertType);

    /** Find all subscriptions for a user */
    List<PcmAlertSubscription> findByUserKey(Long userKey);

    /** Find subscription by alert type and user */
    PcmAlertSubscription findByAlertTypeAndUserKey(String alertType, Long userKey);

    /** Check if a user is already subscribed to an alert type */
    boolean existsByAlertTypeAndUserKey(String alertType, Long userKey);
}

