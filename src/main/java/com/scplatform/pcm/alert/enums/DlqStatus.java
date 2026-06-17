/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

/**
 * Status lifecycle for Dead Letter Queue records in SC_ALERT_DLQ.
 *
 * <p>Matches the DB CHECK constraint:
 * {@code CHECK (STATUS IN ('NEW', 'REVIEWED', 'RESOLVED', 'REPLAYED'))}</p>
 *
 * <pre>
 *   NEW      → Unreviewed; alert just landed in DLQ
 *   REVIEWED → Acknowledged by ops but not yet resolved
 *   RESOLVED → Root cause fixed; no further action needed
 *   REPLAYED → Alert event was re-queued for reprocessing
 * </pre>
 */
public enum DlqStatus {

    /** Unreviewed — alert just arrived in the DLQ */
    NEW,

    /** Acknowledged by operations but not yet resolved */
    REVIEWED,

    /** Root cause identified and fixed — no further action needed */
    RESOLVED,

    /** Alert event was re-queued to Artemis for reprocessing */
    REPLAYED
}

