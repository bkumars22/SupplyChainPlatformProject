/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.dto.AlertEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for generating alert summaries (short, long, and alert ID).
 *
 * <p>Builds summary text directly from the {@link AlertEvent} metadata
 * and the {@link AlertTypes} enum description. All alerts are stored
 * in the existing {@code SC_ALERT_DETAIL} table — no separate
 * template table is required.</p>
 *
 * <p>Replaces the old Velocity/OGNL XML-based alert definition templates
 * with simple Java-based summary builders keyed by AlertType.</p>
 */
@Service
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
public class AlertTemplateService {


    /**
     * Builds the short summary for the given alert event.
     * Used for SC_ALERT_DETAIL.SHORT_SUMMARY (max 255 chars).
     *
     * @param alertEvent the alert event providing context data
     * @return short summary string
     */
    public String evaluateShortSummary(AlertEvent alertEvent) {
        AlertTypes type = alertEvent.getAlertType();
        String objectInfo = alertEvent.getObjectKey() != null
                ? " for object " + alertEvent.getObjectKey()
                : "";
        return type.getDescription() + objectInfo;
    }

    /**
     * Builds the long summary for the given alert event.
     * Used for SC_ALERT_DETAIL.LONG_SUMMARY (max 512 chars).
     *
     * @param alertEvent the alert event providing context data
     * @return long summary string
     */
    public String evaluateLongSummary(AlertEvent alertEvent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Alert Type: ").append(alertEvent.getAlertType().getDescription()).append("\n");

        if (alertEvent.getObjectKey() != null) {
            sb.append("Object Key: ").append(alertEvent.getObjectKey()).append("\n");
        }
        if (alertEvent.getReferenceKey() != null) {
            sb.append("Reference Key: ").append(alertEvent.getReferenceKey()).append("\n");
        }
        if (alertEvent.getChanges() != null && !alertEvent.getChanges().isEmpty()) {
            sb.append("Changes: ").append(String.join(", ", alertEvent.getChanges())).append("\n");
        }
        if (alertEvent.getMetadata() != null && !alertEvent.getMetadata().isEmpty()) {
            sb.append("Details: ").append(alertEvent.getMetadata()).append("\n");
        }
        sb.append("Generated: ").append(alertEvent.getCreationDate());
        return sb.toString();
    }

    /**
     * Builds a unique alert ID for the given alert event.
     * Used for SC_ALERT_DETAIL.ALERT_ID (max 512 chars).
     *
     * <p>Format: {@code {AlertType}-{objectKey}-{first8CharsOfUUID}}</p>
     *
     * @param alertEvent the alert event providing context data
     * @return alert ID string
     */
    public String evaluateAlertId(AlertEvent alertEvent) {
        return String.format("%s-%s-%s",
                alertEvent.getAlertType().name(),
                alertEvent.getObjectKey() != null ? alertEvent.getObjectKey() : "0",
                alertEvent.getAlertEventID().substring(0,
                        Math.min(8, alertEvent.getAlertEventID().length())));
    }
}
