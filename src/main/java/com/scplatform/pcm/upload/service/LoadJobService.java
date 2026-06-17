/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.service;

import com.scplatform.pcm.upload.entity.LoadJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link LoadJob} that was previously embedded in the entity.
 */
@Service
@RequiredArgsConstructor
public class LoadJobService {

    private final LoadEventService loadEventService;

    /**
     * Returns {@code true} if every event in this job has been cleared.
     */
    public boolean getAllEventsCleared(LoadJob job) {
        if (job.getLoadEvents() != null) {
            for (var event : job.getLoadEvents()) {
                if (!loadEventService.isLoadEventCleared(event)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns {@code true} when the given user is allowed to replay this job.
     *
     * @param job               the load job to check
     * @param userId            the user requesting the replay
     * @param replayOwnJobsOnly when {@code true}, only the original submitter may replay
     */
    public boolean canLoadJobBeReplayedByUserId(LoadJob job, String userId, boolean replayOwnJobsOnly) {
        if (!"COMPLETED".equals(job.getState())) {
            return false;
        }
        if (replayOwnJobsOnly) {
            return userId.equals(job.getLoadedBy());
        }
        return true;
    }
}
