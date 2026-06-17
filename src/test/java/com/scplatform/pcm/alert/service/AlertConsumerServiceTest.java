/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.enums.AlertStatus;
import com.scplatform.pcm.alert.enums.AlertTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertConsumerServiceTest {

    @Mock
    private AlertPublisher alertPublisher;

    @Mock
    private DlqAlertService dlqAlertService;

    @InjectMocks
    private AlertConsumerService consumer;

    private AlertEvent newEvent(AlertTypes type) {
        return AlertEvent.builder().alertType(type).objectKey(1L).build();
    }

    @Test
    void onCostChange_publishesAndSetsPublishedStatus() {
        AlertEvent e = newEvent(AlertTypes.CostChange);
        when(alertPublisher.publish(e)).thenReturn(true);

        consumer.onCostChange(e);

        verify(alertPublisher).publish(e);
        assertEquals(AlertStatus.PUBLISHED, e.getStatus());
        assertEquals(1, e.getPublishAttemptCount());
        assertNotNull(e.getPublishAttemptDate());
    }

    @Test
    void processAlert_publishReturnsFalse_throwsAndSetsUnpublished() {
        AlertEvent e = newEvent(AlertTypes.CostChange);
        when(alertPublisher.publish(e)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> consumer.onCostChange(e));
        assertEquals(AlertStatus.UNPUBLISHED, e.getStatus());
    }

    @Test
    void processAlert_publishThrows_propagatesException() {
        AlertEvent e = newEvent(AlertTypes.CostChange);
        when(alertPublisher.publish(e)).thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> consumer.onCostChange(e));
        assertEquals(1, e.getPublishAttemptCount());
    }

    @Test
    void processAlert_nullEvent_returnsSilently() {
        assertDoesNotThrow(() -> consumer.onCostChange(null));
        verifyNoInteractions(alertPublisher);
    }

    @Test
    void processAlert_handlesNullAlertType() {
        AlertEvent e = AlertEvent.builder().objectKey(1L).build();
        e.setAlertType(null);
        when(alertPublisher.publish(e)).thenReturn(true);

        consumer.onCostChange(e);
        assertEquals(AlertStatus.PUBLISHED, e.getStatus());
    }

    @Test
    void onCostPending_delegates() {
        AlertEvent e = newEvent(AlertTypes.CostPending);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onCostPending(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onForecastChange_delegates() {
        AlertEvent e = newEvent(AlertTypes.ForecastChange);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onForecastChange(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onSupplyAllocationChange_delegates() {
        AlertEvent e = newEvent(AlertTypes.SupplyAllocationChange);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onSupplyAllocationChange(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onSupplyAllocationMissing_delegates() {
        AlertEvent e = newEvent(AlertTypes.SupplyAllocationMissing);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onSupplyAllocationMissing(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onItemAssignment_delegates() {
        AlertEvent e = newEvent(AlertTypes.ItemAssignment);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onItemAssignment(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onItemUnassignment_delegates() {
        AlertEvent e = newEvent(AlertTypes.ItemUnassignment);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onItemUnassignment(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onCostExpiring_delegates() {
        AlertEvent e = newEvent(AlertTypes.CostExpiring);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onCostExpiring(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onCostMissing_delegates() {
        AlertEvent e = newEvent(AlertTypes.CostMissing);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onCostMissing(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onBOMAttritionRateMissing_delegates() {
        AlertEvent e = newEvent(AlertTypes.BOMAttritionRateMissing);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onBOMAttritionRateMissing(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onForecastPending_delegates() {
        AlertEvent e = newEvent(AlertTypes.ForecastPending);
        when(alertPublisher.publish(e)).thenReturn(true);
        consumer.onForecastPending(e);
        verify(alertPublisher).publish(e);
    }

    @Test
    void onDeadLetterMessage_recordsToDlqService() {
        AlertEvent e = newEvent(AlertTypes.CostChange);
        consumer.onDeadLetterMessage(e);
        verify(dlqAlertService).recordFailedAlert(e);
        verifyNoInteractions(alertPublisher);
    }

    @Test
    void processAlert_setsProcessingStatusBeforeSuccess() {
        AlertEvent e = newEvent(AlertTypes.CostChange);
        when(alertPublisher.publish(any())).thenAnswer(inv -> {
            AlertEvent ev = inv.getArgument(0);
            assertEquals(AlertStatus.PROCESSING, ev.getStatus());
            return true;
        });
        consumer.onCostChange(e);
        assertEquals(AlertStatus.PUBLISHED, e.getStatus());
    }
}
