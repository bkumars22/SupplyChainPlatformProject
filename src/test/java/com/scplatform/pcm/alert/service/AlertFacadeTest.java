/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.dto.AlertReceiver;
import com.scplatform.pcm.alert.enums.AlertStatus;
import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.ObjectTypes;
import com.scplatform.pcm.alert.exception.AlertQueueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertFacadeTest {

    @Mock
    private AlertHandler alertHandler;

    @InjectMocks
    private AlertFacade alertFacade;

    private AlertEvent capturedEvent() throws AlertQueueException {
        ArgumentCaptor<AlertEvent> cap = ArgumentCaptor.forClass(AlertEvent.class);
        verify(alertHandler).queue(cap.capture());
        return cap.getValue();
    }

    @Test
    void createAndCommit_buildsAndQueuesEvent() throws AlertQueueException {
        Map<String, Object> meta = new HashMap<>();
        meta.put("Item", "I1");
        List<String> changes = List.of("price");
        List<AlertReceiver> recs = List.of(AlertReceiver.builder().userLoginId("u").build());

        alertFacade.createAndCommit(AlertTypes.CostChange, 100L, ObjectTypes.COST_RECORD,
                50L, 9L, meta, changes, recs);

        AlertEvent ev = capturedEvent();
        assertEquals(AlertTypes.CostChange, ev.getAlertType());
        assertEquals(100L, ev.getObjectKey());
        assertEquals(ObjectTypes.COST_RECORD, ev.getObjectType());
        assertEquals(50L, ev.getReferenceKey());
        assertEquals(ObjectTypes.ITEM, ev.getReferenceType());
        assertEquals(9L, ev.getActor());
        assertEquals(AlertStatus.COMMITTED, ev.getStatus());
        assertEquals(meta, ev.getMetadata());
        assertEquals(changes, ev.getChanges());
        assertEquals(recs, ev.getReceivers());
    }

    @Test
    void createAndCommit_withFilters_setsFilters() throws AlertQueueException {
        Map<String, Object> filters = Map.of("CostType", "STANDARD");

        alertFacade.createAndCommit(AlertTypes.CostPending, 1L, ObjectTypes.COST_RECORD,
                2L, 3L, null, filters, null, null);

        assertEquals(filters, capturedEvent().getFilters());
    }

    @Test
    void createAndCommit_nullCollections_useDefaults() throws AlertQueueException {
        alertFacade.createAndCommit(AlertTypes.CostChange, 1L, ObjectTypes.COST_RECORD,
                2L, 3L, null, null, null);
        AlertEvent ev = capturedEvent();
        assertNotNull(ev.getMetadata());
        assertNotNull(ev.getChanges());
        assertNotNull(ev.getReceivers());
    }

    @Test
    void createAndCommit_swallowsAlertQueueException() throws AlertQueueException {
        doThrow(new AlertQueueException("boom")).when(alertHandler).queue(any());
        assertDoesNotThrow(() -> alertFacade.createAndCommit(
                AlertTypes.CostChange, 1L, ObjectTypes.COST_RECORD, 2L, 3L, null, null, null));
    }

    @Test
    void recordCostChangeAlert_usesCostChangeType() throws AlertQueueException {
        alertFacade.recordCostChangeAlert(1L, 2L, 3L, null, null);
        AlertEvent ev = capturedEvent();
        assertEquals(AlertTypes.CostChange, ev.getAlertType());
        assertEquals(ObjectTypes.COST_RECORD, ev.getObjectType());
    }

    @Test
    void recordCostPendingAlert_usesCostPendingType() throws AlertQueueException {
        alertFacade.recordCostPendingAlert(1L, 2L, 3L, null, null);
        assertEquals(AlertTypes.CostPending, capturedEvent().getAlertType());
    }

    @Test
    void recordForecastChangeAlert_usesForecastType() throws AlertQueueException {
        alertFacade.recordForecastChangeAlert(1L, 2L, 3L, null, null);
        AlertEvent ev = capturedEvent();
        assertEquals(AlertTypes.ForecastChange, ev.getAlertType());
        assertEquals(ObjectTypes.FORECAST, ev.getObjectType());
    }

    @Test
    void recordForecastPendingAlert_usesForecastPendingType() throws AlertQueueException {
        alertFacade.recordForecastPendingAlert(1L, 2L, 3L, null);
        assertEquals(AlertTypes.ForecastPending, capturedEvent().getAlertType());
    }

    @Test
    void recordSupplyAllocationChangeAlert_usesType() throws AlertQueueException {
        alertFacade.recordSupplyAllocationChangeAlert(1L, 2L, 3L, null, null);
        AlertEvent ev = capturedEvent();
        assertEquals(AlertTypes.SupplyAllocationChange, ev.getAlertType());
        assertEquals(ObjectTypes.SUPPLY_ALLOCATION, ev.getObjectType());
    }

    @Test
    void recordSupplyAllocationMissingAlert_nullObjectKeyItemType() throws AlertQueueException {
        alertFacade.recordSupplyAllocationMissingAlert(2L, 3L, null);
        AlertEvent ev = capturedEvent();
        assertEquals(AlertTypes.SupplyAllocationMissing, ev.getAlertType());
        assertNull(ev.getObjectKey());
        assertEquals(ObjectTypes.ITEM, ev.getObjectType());
        assertEquals(2L, ev.getReferenceKey());
    }

    @Test
    void recordItemAssignmentAlert_setsReceivers() throws AlertQueueException {
        List<AlertReceiver> recs = List.of(AlertReceiver.builder().userLoginId("a").build());
        alertFacade.recordItemAssignmentAlert(10L, 5L, new HashMap<>(), recs);
        AlertEvent ev = capturedEvent();
        assertEquals(AlertTypes.ItemAssignment, ev.getAlertType());
        assertEquals(recs, ev.getReceivers());
    }

    @Test
    void recordItemUnassignmentAlert_setsReceivers() throws AlertQueueException {
        List<AlertReceiver> recs = List.of(AlertReceiver.builder().userLoginId("a").build());
        alertFacade.recordItemUnassignmentAlert(10L, 5L, new HashMap<>(), recs);
        assertEquals(AlertTypes.ItemUnassignment, capturedEvent().getAlertType());
    }

    @Test
    void recordCostExpiringAlert_usesType() throws AlertQueueException {
        alertFacade.recordCostExpiringAlert(1L, 2L, 3L, null);
        assertEquals(AlertTypes.CostExpiring, capturedEvent().getAlertType());
    }

    @Test
    void recordCostMissingAlert_nullObjectKey() throws AlertQueueException {
        alertFacade.recordCostMissingAlert(2L, 3L, null);
        AlertEvent ev = capturedEvent();
        assertEquals(AlertTypes.CostMissing, ev.getAlertType());
        assertNull(ev.getObjectKey());
        assertEquals(ObjectTypes.ITEM, ev.getObjectType());
    }

    @Test
    void recordBOMAttritionRateMissingAlert_usesType() throws AlertQueueException {
        alertFacade.recordBOMAttritionRateMissingAlert(7L, 8L, 9L, null);
        AlertEvent ev = capturedEvent();
        assertEquals(AlertTypes.BOMAttritionRateMissing, ev.getAlertType());
        assertEquals(ObjectTypes.BOM, ev.getObjectType());
        assertEquals(7L, ev.getObjectKey());
    }
}
