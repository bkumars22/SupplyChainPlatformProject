/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.DlqStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DlqAlertRecordTest {

    @Test
    void noArgsConstructor_isInvokable() {
        DlqAlertRecord r = new DlqAlertRecord();
        assertNotNull(r);
    }

    @Test
    void builder_defaultStatusIsNew() {
        DlqAlertRecord r = DlqAlertRecord.builder()
                .alertEventId("evt-1")
                .alertType(AlertTypes.CostChange)
                .build();
        assertEquals(DlqStatus.NEW, r.getStatus());
    }

    @Test
    void builder_setsAllFieldsIncludingOverriddenStatus() {
        LocalDateTime now = LocalDateTime.now();
        DlqAlertRecord r = DlqAlertRecord.builder()
                .dlqKey(10L)
                .alertEventId("evt-2")
                .alertType(AlertTypes.ForecastChange)
                .objectKey(101L)
                .referenceKey(202L)
                .actor(3L)
                .deliveryAttempts(5)
                .errorMessage("boom")
                .eventPayload("{...}")
                .status(DlqStatus.RESOLVED)
                .resolvedBy("ops")
                .resolutionNotes("fixed")
                .receivedDate(now)
                .resolvedDate(now)
                .build();
        assertEquals(10L, r.getDlqKey());
        assertEquals("evt-2", r.getAlertEventId());
        assertEquals(AlertTypes.ForecastChange, r.getAlertType());
        assertEquals(101L, r.getObjectKey());
        assertEquals(202L, r.getReferenceKey());
        assertEquals(3L, r.getActor());
        assertEquals(5, r.getDeliveryAttempts());
        assertEquals("boom", r.getErrorMessage());
        assertEquals("{...}", r.getEventPayload());
        assertEquals(DlqStatus.RESOLVED, r.getStatus());
        assertEquals("ops", r.getResolvedBy());
        assertEquals("fixed", r.getResolutionNotes());
        assertEquals(now, r.getReceivedDate());
        assertEquals(now, r.getResolvedDate());
    }

    @Test
    void settersUpdateAllFields() {
        DlqAlertRecord r = new DlqAlertRecord();
        r.setDlqKey(1L);
        r.setAlertEventId("a");
        r.setAlertType(AlertTypes.CostExpiring);
        r.setObjectKey(2L);
        r.setReferenceKey(3L);
        r.setActor(4L);
        r.setDeliveryAttempts(2);
        r.setErrorMessage("err");
        r.setEventPayload("p");
        r.setStatus(DlqStatus.REVIEWED);
        r.setResolvedBy("u");
        r.setResolutionNotes("n");
        LocalDateTime t = LocalDateTime.now();
        r.setReceivedDate(t);
        r.setResolvedDate(t);
        assertEquals(1L, r.getDlqKey());
        assertEquals(DlqStatus.REVIEWED, r.getStatus());
        assertEquals(t, r.getReceivedDate());
    }

    @Test
    void onCreate_setsReceivedDateWhenNull() throws Exception {
        DlqAlertRecord r = new DlqAlertRecord();
        assertNull(r.getReceivedDate());
        invokeOnCreate(r);
        assertNotNull(r.getReceivedDate());
    }

    @Test
    void onCreate_preservesExistingReceivedDate() throws Exception {
        LocalDateTime fixed = LocalDateTime.of(2025, 12, 31, 23, 59);
        DlqAlertRecord r = DlqAlertRecord.builder().receivedDate(fixed).build();
        invokeOnCreate(r);
        assertEquals(fixed, r.getReceivedDate());
    }

    private static void invokeOnCreate(DlqAlertRecord r) throws Exception {
        Method m = DlqAlertRecord.class.getDeclaredMethod("onCreate");
        m.setAccessible(true);
        m.invoke(r);
    }
}
