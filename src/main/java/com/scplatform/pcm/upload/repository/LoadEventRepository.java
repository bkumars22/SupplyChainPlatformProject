/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.repository;

import com.scplatform.pcm.upload.entity.LoadEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadEventRepository extends JpaRepository<LoadEvent, Long> {
}
