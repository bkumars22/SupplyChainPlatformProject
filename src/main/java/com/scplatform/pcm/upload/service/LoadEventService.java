/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.service;

import com.scplatform.pcm.upload.entity.LoadEvent;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link LoadEvent} that was previously embedded in the entity.
 */
@Service
public class LoadEventService {

    /**
     * Returns {@code true} when the event has been cleared.
     */
    public boolean isLoadEventCleared(LoadEvent event) {
        return event.getType() != null && event.getType().endsWith(LoadEvent.CLEAR_CONTEXT);
    }

    /**
     * Marks the event as cleared by appending {@link LoadEvent#CLEAR_CONTEXT} to the type.
     * Idempotent — safe to call more than once.
     */
    public void clearLoadEvent(LoadEvent event) {
        String type = event.getType();
        if (type != null && !type.endsWith(LoadEvent.CLEAR_CONTEXT)) {
            event.setType(type + LoadEvent.CLEAR_CONTEXT);
        }
    }

    /**
     * Returns {@code true} when this event can be manually cleared.
     * Only uncleared {@link LoadEvent.LoadEventType#MISSING_BUSINESS_ENTITY} events are eligible.
     */
    public boolean canEventBeCleared(LoadEvent event) {
        return LoadEvent.LoadEventType.MISSING_BUSINESS_ENTITY.name().equals(event.getType())
                && !isLoadEventCleared(event);
    }
}
