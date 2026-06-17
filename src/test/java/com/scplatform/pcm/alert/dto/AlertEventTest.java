/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.dto;

import com.scplatform.pcm.alert.enums.AlertStatus;
import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.ObjectTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlertEventTest {

    @Test
    void noArgsConstructor_appliesBuilderDefaults() {
        AlertEvent ev = new AlertEvent();
        assertNotNull(ev.getAlertEventID(), "alertEventID should be auto-generated");
        assertEquals(AlertStatus.NEW, ev.getStatus());
        assertEquals(0, ev.getPublishAttemptCount());
        assertFalse(ev.isClearAlertFlag());
        assertNotNull(ev.getMetadata());
        assertNotNull(ev.getFilters());
        assertNotNull(ev.getChanges());
        assertNotNull(ev.getReceivers());
        assertNotNull(ev.getCreationDate());
    }

    @Test
    void builder_setsAllFields() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("k", "v");
        AlertEvent ev = AlertEvent.builder()
                .alertType(AlertTypes.CostChange)
                .objectKey(42L)
                .objectType(ObjectTypes.COST_RECORD)
                .referenceKey(7L)
                .referenceType(ObjectTypes.ITEM)
                .actor(99L)
                .metadata(meta)
                .clearAlertFlag(true)
                .build();
        assertEquals(AlertTypes.CostChange, ev.getAlertType());
        assertEquals(42L, ev.getObjectKey());
        assertEquals(ObjectTypes.COST_RECORD, ev.getObjectType());
        assertEquals(7L, ev.getReferenceKey());
        assertEquals(ObjectTypes.ITEM, ev.getReferenceType());
        assertEquals(99L, ev.getActor());
        assertEquals(meta, ev.getMetadata());
        assertTrue(ev.isClearAlertFlag());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        AlertEvent ev = new AlertEvent(
                "id-1", AlertStatus.PUBLISHED, AlertTypes.ItemAssignment,
                1L, ObjectTypes.ITEM, 2L, ObjectTypes.USER, 3L,
                Map.of("a", 1), Map.of("b", 2), List.of("name"), List.of(),
                3, now, true, now);
        assertEquals("id-1", ev.getAlertEventID());
        assertEquals(AlertStatus.PUBLISHED, ev.getStatus());
        assertEquals(3, ev.getPublishAttemptCount());
        assertEquals(now, ev.getPublishAttemptDate());
        assertEquals(now, ev.getCreationDate());
        assertTrue(ev.isClearAlertFlag());
    }

    @Test
    void commit_setsStatusToCommitted() {
        AlertEvent ev = new AlertEvent();
        assertEquals(AlertStatus.NEW, ev.getStatus());
        ev.commit();
        assertEquals(AlertStatus.COMMITTED, ev.getStatus());
    }

    @Test
    void incrementPublishAttemptCount_incrementsAndStampsDate() {
        AlertEvent ev = new AlertEvent();
        assertEquals(0, ev.getPublishAttemptCount());
        assertNull(ev.getPublishAttemptDate());
        ev.incrementPublishAttemptCount();
        assertEquals(1, ev.getPublishAttemptCount());
        assertNotNull(ev.getPublishAttemptDate());
        ev.incrementPublishAttemptCount();
        assertEquals(2, ev.getPublishAttemptCount());
    }

    @Test
    void settersUpdateState() {
        AlertEvent ev = new AlertEvent();
        ev.setAlertEventID("custom-id");
        ev.setStatus(AlertStatus.PROCESSING);
        ev.setObjectKey(5L);
        assertEquals("custom-id", ev.getAlertEventID());
        assertEquals(AlertStatus.PROCESSING, ev.getStatus());
        assertEquals(5L, ev.getObjectKey());
    }

    @Test
    void equalsAndHashCode_followLombokDataContract() {
        AlertEvent a = AlertEvent.builder()
                .alertEventID("same")
                .status(AlertStatus.NEW)
                .creationDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
        AlertEvent b = AlertEvent.builder()
                .alertEventID("same")
                .status(AlertStatus.NEW)
                .creationDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
