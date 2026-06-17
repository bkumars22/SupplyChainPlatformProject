/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.enums.AlertStatus;
import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.ObjectTypes;
import com.scplatform.pcm.alert.exception.AlertQueueException;
import com.scplatform.pcm.alert.dto.AlertEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertHandlerTest {

    @Mock
    private JmsTemplate alertJmsTemplate;

    @InjectMocks
    private AlertHandler alertHandler;

    private AlertEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = AlertEvent.builder()
                .alertType(AlertTypes.CostPending)
                .objectKey(12345L)
                .objectType(ObjectTypes.COST_RECORD)
                .referenceKey(67890L)
                .actor(100L)
                .metadata(Map.of("costType", "Material"))
                .changes(List.of("unitCost", "effectiveDate"))
                .build();
        testEvent.commit();
    }

    @Nested
    @DisplayName("queue()")
    class QueueTests {

        @Test
        @DisplayName("should send alert event to correct Artemis topic")
        void shouldSendToCorrectQueue() throws AlertQueueException {
            alertHandler.queue(testEvent);

            verify(alertJmsTemplate).convertAndSend(
                    eq("alert.topic.CostPending"),
                    eq(testEvent),
                    any());
        }

        @Test
        @DisplayName("should send to different topics for different alert types")
        void shouldSendToDifferentQueues() throws AlertQueueException {
            for (AlertTypes type : AlertTypes.values()) {
                AlertEvent event = AlertEvent.builder()
                        .alertType(type)
                        .objectKey(1L)
                        .build();
                event.commit();

                alertHandler.queue(event);

                verify(alertJmsTemplate).convertAndSend(
                        eq(type.getTopicName()),
                        eq(event),
                        any());
            }
        }

        @Test
        @DisplayName("should throw AlertQueueException for null event")
        void shouldThrowForNullEvent() {
            assertThrows(AlertQueueException.class, () -> alertHandler.queue(null));
        }

        @Test
        @DisplayName("should throw AlertQueueException for null alert type")
        void shouldThrowForNullAlertType() {
            AlertEvent noType = AlertEvent.builder().objectKey(1L).build();
            assertThrows(AlertQueueException.class, () -> alertHandler.queue(noType));
        }

        @Test
        @DisplayName("should throw AlertQueueException when JmsTemplate fails")
        void shouldThrowWhenJmsTemplateFails() {
            doThrow(new RuntimeException("Broker down"))
                    .when(alertJmsTemplate)
                    .convertAndSend(anyString(), any(AlertEvent.class), any());

            assertThrows(AlertQueueException.class, () -> alertHandler.queue(testEvent));
        }
    }

    @Nested
    @DisplayName("AlertEvent model")
    class AlertEventTests {

        @Test
        @DisplayName("commit() should set status to COMMITTED")
        void commitShouldSetStatus() {
            AlertEvent event = AlertEvent.builder().alertType(AlertTypes.CostChange).build();
            assertEquals(AlertStatus.NEW, event.getStatus());

            event.commit();
            assertEquals(AlertStatus.COMMITTED, event.getStatus());
        }

        @Test
        @DisplayName("should generate unique event IDs")
        void shouldGenerateUniqueIds() {
            AlertEvent e1 = AlertEvent.builder().alertType(AlertTypes.CostChange).build();
            AlertEvent e2 = AlertEvent.builder().alertType(AlertTypes.CostChange).build();
            assertNotEquals(e1.getAlertEventID(), e2.getAlertEventID());
        }

        @Test
        @DisplayName("incrementPublishAttemptCount should track attempts")
        void shouldIncrementAttemptCount() {
            AlertEvent event = AlertEvent.builder().alertType(AlertTypes.CostChange).build();
            assertEquals(0, event.getPublishAttemptCount());

            event.incrementPublishAttemptCount();
            assertEquals(1, event.getPublishAttemptCount());

            event.incrementPublishAttemptCount();
            assertEquals(2, event.getPublishAttemptCount());
            assertNotNull(event.getPublishAttemptDate());
        }
    }

    @Nested
    @DisplayName("AlertTypes enum")
    class AlertTypesTests {

        @Test
        @DisplayName("getTopicName should return correct topic name")
        void shouldReturnCorrectQueueName() {
            assertEquals("alert.topic.CostChange", AlertTypes.CostChange.getTopicName());
            assertEquals("alert.topic.CostPending", AlertTypes.CostPending.getTopicName());
        }

        @Test
        @DisplayName("should have 11 alert types")
        void shouldHaveAllTypes() {
            assertEquals(11, AlertTypes.values().length);
        }
    }
}

