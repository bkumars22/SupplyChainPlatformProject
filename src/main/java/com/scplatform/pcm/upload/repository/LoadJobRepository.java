/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.repository;

import com.scplatform.pcm.upload.entity.LoadJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoadJobRepository extends JpaRepository<LoadJob, String> {

    Optional<LoadJob> findByExternalId(String externalId);

    /** Eagerly fetches load events in a single JOIN FETCH to avoid N+1. */
    @Query("SELECT lj FROM LoadJob lj LEFT JOIN FETCH lj.loadEvents WHERE lj.loadJobKey = :key")
    Optional<LoadJob> findWithEventsByLoadJobKey(@Param("key") String key);
}
