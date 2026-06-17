/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.repository;

import com.scplatform.pcm.alert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a WHERE LOWER(a.userId) = LOWER(:userId) AND a.state = :state")
    List<Alert> getAlertByUserInternal(@Param("userId") String userId, @Param("state") String state);

    default List<Alert> getAlertByUser(String userId) {
        return getAlertByUserInternal(userId, Alert.ACTIVE);
    }

    @Modifying
    @Transactional
    @Query("UPDATE Alert a SET a.state = :state, a.dismisedBy = :user WHERE a.id IN :ids")
    int dismissAlertInternal(@Param("ids") List<Long> ids, @Param("user") String userId, @Param("state") String state);

    @Transactional
    default void dismissAlert(List<Long> ids, String userId) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        dismissAlertInternal(ids, userId, Alert.DISMISSED);
    }

    @Query("SELECT a.alertType, COUNT(a) FROM Alert a " +
           "WHERE LOWER(a.userId) = LOWER(:userId) AND a.state = :state " +
           "GROUP BY a.alertType")
    List<Object[]> countAlertsByTypeForUser(@Param("userId") String userId,
                                            @Param("state") String state);
}
