/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.enums.AlertTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlertTemplateServiceTest {

    private final AlertTemplateService svc = new AlertTemplateService();

    private AlertEvent baseEvent() {
        AlertEvent ev = new AlertEvent();
        ev.setAlertEventID("1234567890abcdef");
        ev.setAlertType(AlertTypes.CostChange);
        ev.setObjectKey(42L);
        ev.setCreationDate(LocalDateTime.of(2026, 1, 1, 12, 0));
        return ev;
    }

    // ── evaluateShortSummary ──

    @Test
    void shortSummary_includesObjectKey_whenPresent() {
        AlertEvent ev = baseEvent();
        String s = svc.evaluateShortSummary(ev);
        assertEquals("Cost record was modified for object 42", s);
    }

    @Test
    void shortSummary_omitsObjectInfo_whenObjectKeyNull() {
        AlertEvent ev = baseEvent();
        ev.setObjectKey(null);
        String s = svc.evaluateShortSummary(ev);
        assertEquals("Cost record was modified", s);
    }

    // ── evaluateLongSummary ──

    @Test
    void longSummary_includesAllSegments_whenAllFieldsPresent() {
        AlertEvent ev = baseEvent();
        ev.setReferenceKey(99L);
        ev.setChanges(List.of("price", "currency"));
        Map<String, Object> meta = new HashMap<>();
        meta.put("k", "v");
        ev.setMetadata(meta);
        String s = svc.evaluateLongSummary(ev);
        assertTrue(s.contains("Alert Type: Cost record was modified"));
        assertTrue(s.contains("Object Key: 42"));
        assertTrue(s.contains("Reference Key: 99"));
        assertTrue(s.contains("Changes: price, currency"));
        assertTrue(s.contains("Details: {k=v}"));
        assertTrue(s.contains("Generated: " + ev.getCreationDate()));
    }

    @Test
    void longSummary_skipsOptionalSegments_whenAbsent() {
        AlertEvent ev = baseEvent();
        ev.setObjectKey(null);
        ev.setReferenceKey(null);
        ev.setChanges(List.of());
        ev.setMetadata(new HashMap<>());
        String s = svc.evaluateLongSummary(ev);
        assertTrue(s.contains("Alert Type: Cost record was modified"));
        assertFalse(s.contains("Object Key:"));
        assertFalse(s.contains("Reference Key:"));
        assertFalse(s.contains("Changes:"));
        assertFalse(s.contains("Details:"));
        assertTrue(s.contains("Generated:"));
    }

    @Test
    void longSummary_handlesNullChangesAndMetadata() {
        AlertEvent ev = baseEvent();
        ev.setChanges(null);
        ev.setMetadata(null);
        String s = svc.evaluateLongSummary(ev);
        assertFalse(s.contains("Changes:"));
        assertFalse(s.contains("Details:"));
    }

    // ── evaluateAlertId ──

    @Test
    void alertId_format_isTypeKeyAndFirst8OfEventId() {
        AlertEvent ev = baseEvent();
        // alertEventID = "1234567890abcdef" -> first 8 = "12345678"
        assertEquals("CostChange-42-12345678", svc.evaluateAlertId(ev));
    }

    @Test
    void alertId_usesZero_whenObjectKeyNull() {
        AlertEvent ev = baseEvent();
        ev.setObjectKey(null);
        assertEquals("CostChange-0-12345678", svc.evaluateAlertId(ev));
    }

    @Test
    void alertId_handlesShortEventId_withoutSubstringOverflow() {
        AlertEvent ev = baseEvent();
        ev.setAlertEventID("abc");
        assertEquals("CostChange-42-abc", svc.evaluateAlertId(ev));
    }
}
