/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.user.repository;

import com.scplatform.pcm.user.entity.PcmUserSessionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * Repository for PcmUserSessionInfo entity.
 * 
 * <p>Manages user session tracking for concurrent login detection.
 * 
 * @author PCM Team
 */
@Repository
public interface PcmUserSessionInfoRepository extends JpaRepository<PcmUserSessionInfo, String> {

    /**
     * Find session info by user ID.
     * 
     * @param userId the user ID
     * @return Optional containing session info if found
     */
    Optional<PcmUserSessionInfo> findByUserId(String userId);

    /**
     * Check if a session exists for the user.
     * 
     * @param userId the user ID
     * @return true if session exists
     */
    boolean existsByUserId(String userId);

    /**
     * Delete session info by user ID.
     * 
     * @param userId the user ID
     */
    void deleteByUserId(String userId);

    /**
     * Update the last access time for a user's session.
     * 
     * @param userId       the user ID
     * @param lastUpdateOn the new timestamp
     * @return number of rows updated
     */
    @Modifying
    @Query("UPDATE PcmUserSessionInfo p SET p.lastUpdateOn = :lastUpdateOn WHERE p.userId = :userId")
    int updateLastAccessTime(@Param("userId") String userId, @Param("lastUpdateOn") Timestamp lastUpdateOn);
}
