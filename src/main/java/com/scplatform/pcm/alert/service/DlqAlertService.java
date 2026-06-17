/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.entity.DlqAlertRecord;
import com.scplatform.pcm.alert.enums.DlqStatus;
import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.repository.DlqAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for handling Dead Letter Queue (DLQ) alert events.
 *
 * <p>When an alert event exhausts all retry attempts, Artemis moves it
 * to the DLQ. This service:</p>
 * <ol>
 *   <li>Persists the failed event to the SC_ALERT_DLQ table</li>
 *   <li>Optionally sends an email notification to the operations team</li>
 *   <li>Provides methods for ops to review and resolve DLQ entries</li>
 * </ol>
 *
 * <h3>Email Notification:</h3>
 * <p>Enabled via {@code pcm.alert.dlq.notify-email}. When set, an email
 * is sent for each new DLQ entry. Disabled by default.
 * If no SMTP server is configured (no JavaMailSender bean), email is silently skipped.</p>
 */
@Service
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
public class DlqAlertService {

    private static final Logger log = LoggerFactory.getLogger(DlqAlertService.class);

    private final DlqAlertRepository dlqRepository;
    private final JsonMapper jsonMapper;

    /**
     * Optional: JavaMailSender is only available if spring.mail.* is configured.
     * If not present, email notifications are silently disabled.
     */
    private JavaMailSender mailSender;

    @Value("${pcm.alert.dlq.notify-email:}")
    private String notifyEmail;

    @Value("${pcm.alert.dlq.notify-enabled:false}")
    private boolean notifyEnabled;

    @Value("${spring.application.name:scplatform}")
    private String applicationName;

    public DlqAlertService(DlqAlertRepository dlqRepository,
                           @Qualifier("alertJsonMapper") JsonMapper jsonMapper) {
        this.dlqRepository = dlqRepository;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Inject JavaMailSender optionally. If no SMTP is configured,
     * this setter won't be called and mailSender stays null.
     */
    @Autowired(required = false)
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        if (mailSender != null) {
            log.info("JavaMailSender configured — DLQ email notifications available");
        }
    }

    /**
     * Records a failed alert event from the DLQ to the error table
     * and optionally sends an email notification.
     *
     * @param event the failed alert event
     */
    @Transactional
    public void recordFailedAlert(AlertEvent event) {
        try {
            // Serialize event to JSON for potential replay
            String payload = serializeEvent(event);

            DlqAlertRecord record = DlqAlertRecord.builder()
                    .alertEventId(event.getAlertEventID())
                    .alertType(event.getAlertType())
                    .objectKey(event.getObjectKey())
                    .referenceKey(event.getReferenceKey())
                    .actor(event.getActor())
                    .deliveryAttempts(event.getPublishAttemptCount())
                    .errorMessage("Alert exhausted all retry attempts and was moved to DLQ")
                    .eventPayload(payload)
                    .status(DlqStatus.NEW)
                    .build();

            dlqRepository.save(record);

            log.info("DLQ alert recorded: eventId=[{}], type=[{}], objectKey=[{}]",
                    event.getAlertEventID(), event.getAlertType(), event.getObjectKey());

            // Send notification if enabled and mail sender is available
            if (notifyEnabled && mailSender != null && notifyEmail != null && !notifyEmail.isBlank()) {
                sendDlqNotification(event, record);
            } else if (notifyEnabled && mailSender == null) {
                log.warn("DLQ email notification is enabled but no JavaMailSender is configured. " +
                        "Configure spring.mail.* properties to enable email notifications.");
            }

        } catch (Exception e) {
            // DLQ recording should never fail silently — log at ERROR level
            log.error("CRITICAL: Failed to record DLQ alert event [{}] to error table. " +
                            "Manual intervention required! Event type=[{}], objectKey=[{}]",
                    event.getAlertEventID(), event.getAlertType(), event.getObjectKey(), e);
        }
    }

    /**
     * Sends an email notification to the operations team about a DLQ event.
     */
    private void sendDlqNotification(AlertEvent event, DlqAlertRecord record) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notifyEmail.split(","));
            message.setSubject(String.format("[%s] ALERT DLQ: %s event failed - %s",
                    applicationName, event.getAlertType(), event.getAlertEventID()));
            message.setText(String.format("""
                    Alert Dead Letter Queue Notification
                    =====================================
                    
                    Application: %s
                    DLQ Record Key: %d
                    Alert Event ID: %s
                    Alert Type: %s
                    Object Key: %s
                    Reference Key: %s
                    Actor: %s
                    Delivery Attempts: %d
                    Received: %s
                    
                    This alert event has exhausted all retry attempts and requires manual review.
                    Please check the SC_ALERT_DLQ table for details.
                    """,
                    applicationName,
                    record.getDlqKey(),
                    event.getAlertEventID(),
                    event.getAlertType(),
                    event.getObjectKey(),
                    event.getReferenceKey(),
                    event.getActor(),
                    event.getPublishAttemptCount(),
                    record.getReceivedDate()));

            mailSender.send(message);
            log.info("DLQ notification email sent to [{}] for event [{}]",
                    notifyEmail, event.getAlertEventID());

        } catch (Exception e) {
            log.error("Failed to send DLQ notification email for event [{}]: {}",
                    event.getAlertEventID(), e.getMessage());
        }
    }

    /**
     * Serializes an AlertEvent to JSON for storage in the DLQ table.
     */
    private String serializeEvent(AlertEvent event) {
        try {
            return jsonMapper.writeValueAsString(event);
        } catch (Exception e) {
            log.warn("Failed to serialize DLQ alert event [{}]: {}",
                    event.getAlertEventID(), e.getMessage());
            return "{ \"error\": \"serialization failed\", \"eventId\": \"" +
                    event.getAlertEventID() + "\" }";
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Ops / Review Methods
    // ─────────────────────────────────────────────────────────────

    /** Get all unresolved DLQ entries */
    @Transactional(readOnly = true)
    public List<DlqAlertRecord> getUnresolvedEntries() {
        return dlqRepository.findByStatusOrderByReceivedDateDesc(DlqStatus.NEW);
    }

    /** Get count of unresolved DLQ entries */
    @Transactional(readOnly = true)
    public long getUnresolvedCount() {
        return dlqRepository.countByStatus(DlqStatus.NEW);
    }

    /** Mark a DLQ entry as resolved */
    @Transactional
    public void resolveEntry(Long dlqKey, String resolvedBy, String notes) {
        dlqRepository.markAsResolved(dlqKey, resolvedBy, notes, LocalDateTime.now());
        log.info("DLQ entry [{}] resolved by [{}]", dlqKey, resolvedBy);
    }

    /** Cleanup old resolved DLQ entries (older than given days) */
    @Transactional
    public int cleanupOldEntries(int olderThanDays) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(olderThanDays);
        int deleted = dlqRepository.deleteOldResolvedEntries(cutoff);
        log.info("Cleaned up {} resolved DLQ entries older than {} days", deleted, olderThanDays);
        return deleted;
    }
}

