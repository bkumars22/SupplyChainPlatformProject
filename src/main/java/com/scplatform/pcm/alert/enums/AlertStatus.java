/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

/**
 * Alert event status lifecycle:
 *
 *   NEW → COMMITTED → PROCESSING → PUBLISHED
 *                                → UNPUBLISHED (retry / DLQ)
 *
 * With Artemis, the UNPUBLISHED retry logic is handled by the broker's
 * redelivery mechanism and Dead Letter Queue (DLQ).
 */
public enum AlertStatus {

    /** Initial state when AlertEvent is created */
    NEW,

    /** Alert data populated, ready to be committed (legacy: set by AlertFacade.populateAlert) */
    PENDING,

    /** Ready for processing — queued to Artemis */
    COMMITTED,

    /** Currently being published by AlertConsumerService */
    PROCESSING,

    /** Successfully published to all receivers */
    PUBLISHED,

    /** Publishing failed — Artemis will redeliver or route to DLQ */
    UNPUBLISHED
}

