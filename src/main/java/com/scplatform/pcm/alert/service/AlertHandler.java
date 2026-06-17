/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.exception.AlertQueueException;
import com.scplatform.pcm.alert.dto.AlertEvent;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * Central manager for queuing alert events to Apache Artemis.
 *
 * <p>Replaces the old AlertHandler singleton pattern with a Spring-managed service.
 * Uses JmsTemplate to send AlertEvent objects as JSON messages to type-specific
 * Artemis <b>topics</b> (MULTICAST).</p>
 *
 * <h3>Old Flow (PersistentQueue — file-based):</h3>
 * <pre>
 *   AlertHandler.getInstance().queue(aeo)
 *     → AlertQueue.add(aeo)
 *       → PersistentQueue.add(aeo)  // file serialization
 * </pre>
 *
 * <h3>New Flow (Artemis Topics — file-based journal):</h3>
 * <pre>
 *   alertHandler.queue(aeo)
 *     → jmsTemplate.convertAndSend("alert.topic.CostPending", aeo)
 *       → Artemis broker writes to journal files (append-only, fsync)
 *       → All durable topic subscribers receive a copy
 * </pre>
 *
 * <h3>Design Decision:</h3>
 * <p>Alert failures are non-fatal. If a message cannot be queued to Artemis,
 * the error is logged but the calling business operation continues.</p>
 */
@Service
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
public class AlertHandler {

    private static final Logger log = LoggerFactory.getLogger(AlertHandler.class);

    private final JmsTemplate alertJmsTemplate;

    public AlertHandler(JmsTemplate alertJmsTemplate) {
        this.alertJmsTemplate = alertJmsTemplate;
    }

    /**
     * Queues an alert event to the appropriate Artemis topic.
     *
     * <p>The topic is determined by the alert type:</p>
     * <ul>
     *   <li>CostChange → alert.topic.CostChange</li>
     *   <li>CostPending → alert.topic.CostPending</li>
     *   <li>etc.</li>
     * </ul>
     *
     * <p>The message is:</p>
     * <ol>
     *   <li>Serialized to JSON by the Jackson message converter</li>
     *   <li>Published to the Artemis MULTICAST topic as a persistent TextMessage</li>
     *   <li>Written to the Artemis file-based journal (crash-safe)</li>
     *   <li>Delivered to all durable subscribers</li>
     * </ol>
     *
     * @param alertEvent the alert event to queue
     * @throws AlertQueueException if the message cannot be sent
     */
    public void queue(AlertEvent alertEvent) throws AlertQueueException {
        if (alertEvent == null) {
            throw new AlertQueueException("AlertEvent cannot be null");
        }

        AlertTypes alertType = alertEvent.getAlertType();
        if (alertType == null) {
            throw new AlertQueueException("AlertEvent.alertType cannot be null");
        }

        String topicName = alertType.getTopicName();

        try {
            log.debug("Queuing alert event [{}] of type [{}] to topic [{}]",
                    alertEvent.getAlertEventID(), alertType, topicName);

            // Send to the type-specific Artemis topic with JMS properties for filtering/monitoring
            alertJmsTemplate.convertAndSend(topicName, alertEvent, (Message message) -> {
                message.setStringProperty("alertType", alertType.name());
                message.setStringProperty("alertEventID", alertEvent.getAlertEventID());
                if (alertEvent.getObjectKey() != null) {
                    message.setLongProperty("objectKey", alertEvent.getObjectKey().longValue());
                }
                if (alertEvent.getActor() != null) {
                    message.setLongProperty("actor", alertEvent.getActor().longValue());
                }
                return message;
            });

            log.info("Alert event [{}] of type [{}] published to topic [{}]",
                    alertEvent.getAlertEventID(), alertType, topicName);

        } catch (Exception e) {
            String errorMsg = String.format(
                    "Failed to publish alert event [%s] of type [%s] to topic [%s]",
                    alertEvent.getAlertEventID(), alertType, topicName);
            log.error(errorMsg, e);
            throw new AlertQueueException(errorMsg, e);
        }
    }
}


