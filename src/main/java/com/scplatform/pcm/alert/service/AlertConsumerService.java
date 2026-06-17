/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.enums.AlertStatus;
import com.scplatform.pcm.alert.dto.AlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumes alert events from Artemis <b>topics</b> and delegates to AlertPublisher.
 *
 * <p>Replaces the old AlertThread polling pattern with event-driven @JmsListener
 * on durable topic subscriptions. Each alert type has its own topic and subscription,
 * providing independent processing and monitoring.</p>
 *
 * <h3>Topic Subscriptions:</h3>
 * <ul>
 *   <li>Each @JmsListener subscribes to a MULTICAST topic (not an ANYCAST queue)</li>
 *   <li>Subscriptions are <b>durable</b> — messages published while this consumer is
 *       offline are held in the file-based journal and delivered on reconnect</li>
 *   <li>Subscriptions are <b>shared</b> — multiple concurrent consumers can process
 *       messages from the same subscription for load balancing</li>
 * </ul>
 *
 * <h3>Retry/DLQ:</h3>
 * <p>Handled natively by Artemis — no manual retry logic needed.
 * On failure, the transaction rolls back → Artemis redelivers.
 * After max retries, message moves to DLQ (ANYCAST queue).</p>
 *
 * <h3>Storage:</h3>
 * <p>All messages are persisted in the Artemis <b>file-based journal</b> (NOT database).
 * Durable subscription queues are backed by the same journal files.</p>
 *
 * <pre>
 *   Old: AlertThread.run() every 60s → queue.getAlerts(100) → publish batch
 *   New: @JmsListener on durable topic → instant processing → file-based journal persistence
 * </pre>
 */
@Component
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
public class AlertConsumerService {

    private static final Logger log = LoggerFactory.getLogger(AlertConsumerService.class);

    private final AlertPublisher alertPublisher;
    private final DlqAlertService dlqAlertService;

    public AlertConsumerService(AlertPublisher alertPublisher,
                                DlqAlertService dlqAlertService) {
        this.alertPublisher = alertPublisher;
        this.dlqAlertService = dlqAlertService;
    }

    // ── One @JmsListener per AlertType topic (durable subscription) ──

    @JmsListener(destination = "alert.topic.CostChange", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.CostChange")
    public void onCostChange(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.CostPending", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.CostPending")
    public void onCostPending(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.ForecastChange", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.ForecastChange")
    public void onForecastChange(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.SupplyAllocationChange", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.SupplyAllocationChange")
    public void onSupplyAllocationChange(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.SupplyAllocationMissing", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.SupplyAllocationMissing")
    public void onSupplyAllocationMissing(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.ItemAssignment", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.ItemAssignment")
    public void onItemAssignment(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.ItemUnassignment", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.ItemUnassignment")
    public void onItemUnassignment(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.CostExpiring", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.CostExpiring")
    public void onCostExpiring(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.CostMissing", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.CostMissing")
    public void onCostMissing(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.BOMAttritionRateMissing", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.BOMAttritionRateMissing")
    public void onBOMAttritionRateMissing(AlertEvent event) {
        processAlert(event);
    }

    @JmsListener(destination = "alert.topic.ForecastPending", containerFactory = "alertListenerFactory",
            subscription = "alert.sub.ForecastPending")
    public void onForecastPending(AlertEvent event) {
        processAlert(event);
    }

    // ── Dead Letter Queue listener (ANYCAST — uses separate dlqListenerFactory) ──

    /**
     * Monitors the DLQ for messages that failed all retry attempts.
     * Persists the failed event to the SC_ALERT_DLQ error table for manual review.
     *
     * <p>DLQ uses ANYCAST (point-to-point queue) — only one consumer processes each
     * DLQ message, unlike the alert topics which are MULTICAST.</p>
     */
    @JmsListener(destination = "DLQ", containerFactory = "dlqListenerFactory")
    public void onDeadLetterMessage(AlertEvent event) {
        log.error("ALERT DLQ: Event [{}] type [{}] moved to DLQ after max retries. ObjectKey=[{}]",
                event.getAlertEventID(), event.getAlertType(), event.getObjectKey());

        // Persist to error table for manual review and optionally notify ops
        dlqAlertService.recordFailedAlert(event);
    }

    // ── Core processing logic ──

    /**
     * Processes a single alert event from an Artemis topic.
     *
     * <p>On success: method returns → message auto-acknowledged → removed from journal file.</p>
     * <p>On failure: RuntimeException → message NOT acknowledged → Artemis redelivers from journal.</p>
     * <p>After max retries: message moves to DLQ.</p>
     */
    private void processAlert(AlertEvent event) {
        if (event == null) {
            log.warn("Received null alert event from topic. Skipping.");
            return;
        }

        String eventId = event.getAlertEventID();
        String alertType = event.getAlertType() != null ? event.getAlertType().name() : "UNKNOWN";

        log.debug("Processing alert event [{}] of type [{}]", eventId, alertType);

        try {
            event.setStatus(AlertStatus.PROCESSING);
            event.incrementPublishAttemptCount();

            boolean success = alertPublisher.publish(event);

            if (success) {
                event.setStatus(AlertStatus.PUBLISHED);
                log.info("Successfully published alert [{}] type [{}]", eventId, alertType);
            } else {
                event.setStatus(AlertStatus.UNPUBLISHED);
                throw new RuntimeException(
                        "AlertPublisher.publish() returned false for event [" + eventId + "]");
            }

        } catch (RuntimeException e) {
            log.error("Failed to process alert [{}] type [{}], attempt #{}. Will be redelivered by Artemis.",
                    eventId, alertType, event.getPublishAttemptCount(), e);
            throw e; // re-throw to prevent acknowledgment → triggers Artemis redelivery
        }
    }
}
