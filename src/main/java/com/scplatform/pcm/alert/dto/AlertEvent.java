/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.dto;

import com.scplatform.pcm.alert.enums.AlertStatus;
import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.ObjectTypes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data transfer object that encapsulates all information about an alert event.
 *
 * <p>This is the message payload sent to and received from the Artemis queue.
 * It is serialized as JSON by the JMS message converter.</p>
 *
 * <h3>Lifecycle:</h3>
 * <pre>
 *   1. Business logic creates AlertEvent via AlertFacade
 *   2. AlertFacade sets status = COMMITTED and calls AlertHandler.queue()
 *   3. AlertHandler sends to Artemis queue as JSON
 *   4. AlertConsumerService receives from queue
 *   5. AlertPublisher determines receivers, creates AlertDetail entities, saves to DB
 *   6. On success → message acknowledged → removed from Artemis journal
 *   7. On failure → message not acknowledged → Artemis redelivers (up to max attempts → DLQ)
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for this alert event */
    @Builder.Default
    private String alertEventID = UUID.randomUUID().toString();

    /** Current status in the lifecycle */
    @Builder.Default
    private AlertStatus status = AlertStatus.NEW;

    /** Type of alert (maps to a specific Artemis queue) */
    private AlertTypes alertType;

    /** Primary key of the business object (e.g., CostRecord key) */
    private Long objectKey;

    /** Type of the business object */
    private ObjectTypes objectType;

    /** Reference key (e.g., Item key) */
    private Long referenceKey;

    /** Reference object type (usually Item) */
    private ObjectTypes referenceType;

    /** User key who triggered the alert */
    private Long actor;

    /** Additional context data for template evaluation */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /** Filters used for receiver determination (responsibility, cost type, etc.) */
    @Builder.Default
    private Map<String, Object> filters = new HashMap<>();

    /** List of changed field names */
    @Builder.Default
    private List<String> changes = new ArrayList<>();

    /** Explicitly specified receivers (if not using subscription lookup) */
    @Builder.Default
    private List<AlertReceiver> receivers = new ArrayList<>();

    /** Number of publish attempts (tracked by Artemis redelivery, kept for compatibility) */
    @Builder.Default
    private int publishAttemptCount = 0;

    /** Last publish attempt timestamp */
    private LocalDateTime publishAttemptDate;

    /**
     * When true, this event signals that existing alerts of this type/object
     * should be cleared (dismissed) rather than creating new ones.
     * Legacy compatibility — see old AlertPublisher.clearAlert().
     */
    @Builder.Default
    private boolean clearAlertFlag = false;

    /** When the event was created */
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    /**
     * Marks this event as committed and ready for queuing.
     * Called by AlertFacade before sending to the queue.
     */
    public void commit() {
        this.status = AlertStatus.COMMITTED;
    }

    /**
     * Increments the publish attempt counter.
     */
    public void incrementPublishAttemptCount() {
        this.publishAttemptCount++;
        this.publishAttemptDate = LocalDateTime.now();
    }
}

