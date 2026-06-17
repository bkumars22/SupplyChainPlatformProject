/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.DlqStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity mapped to the SC_ALERT_DLQ table.
 *
 * <p>Records alert events that exhausted all retry attempts and landed
 * in the Dead Letter Queue. Saved here for manual investigation by
 * operations staff.</p>
 *
 * <h3>Flow:</h3>
 * <pre>
 *   AlertEvent fails processing (max retries exceeded)
 *     → Artemis moves to DLQ
 *     → AlertConsumerService.onDeadLetterMessage() receives it
 *     → DlqAlertService.recordFailedAlert() persists to this table
 *     → Optional email notification sent to ops team
 * </pre>
 */
@Entity
@Table(name = "SC_ALERT_DLQ")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DlqAlertRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alert_dlq_seq")
    @SequenceGenerator(
            name = "alert_dlq_seq",
            sequenceName = "SC_ALERT_DLQ_SEQ",
            allocationSize = 1
    )
    @Column(name = "DLQ_KEY")
    private Long dlqKey;

    /** The original alert event ID */
    @Column(name = "ALERT_EVENT_ID", length = 100)
    private String alertEventId;

    /** Alert type that failed */
    @Column(name = "ALERT_TYPE", length = 50)
    @Enumerated(EnumType.STRING)
    private AlertTypes alertType;

    /** Business object key */
    @Column(name = "OBJECT_KEY")
    private Long objectKey;

    /** Reference key (e.g., Item key) */
    @Column(name = "REFERENCE_KEY")
    private Long referenceKey;

    /** User who triggered the original alert */
    @Column(name = "ACTOR")
    private Long actor;

    /** Number of delivery attempts before DLQ */
    @Column(name = "DELIVERY_ATTEMPTS")
    private Integer deliveryAttempts;

    /** Error message / root cause (truncated) */
    @Column(name = "ERROR_MESSAGE", length = 4000)
    private String errorMessage;

    /** Full serialized AlertEvent JSON for replay */
    @Lob
    @Column(name = "EVENT_PAYLOAD")
    private String eventPayload;

    /** Processing status: NEW, REVIEWED, RESOLVED, REPLAYED */
    @Column(name = "STATUS", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DlqStatus status = DlqStatus.NEW;

    /** Ops team member who reviewed/resolved this entry */
    @Column(name = "RESOLVED_BY", length = 200)
    private String resolvedBy;

    /** Resolution notes */
    @Column(name = "RESOLUTION_NOTES", length = 2000)
    private String resolutionNotes;

    /** When the failed event was received in the DLQ */
    @Column(name = "RECEIVED_DATE")
    private LocalDateTime receivedDate;

    /** When this record was resolved */
    @Column(name = "RESOLVED_DATE")
    private LocalDateTime resolvedDate;

    @PrePersist
    protected void onCreate() {
        if (this.receivedDate == null) {
            this.receivedDate = LocalDateTime.now();
        }
    }
}

