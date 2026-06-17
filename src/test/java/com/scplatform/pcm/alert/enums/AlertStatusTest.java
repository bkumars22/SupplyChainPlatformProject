/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertStatusTest {

    @Test
    void hasAllExpectedValues() {
        assertEquals(6, AlertStatus.values().length);
    }

    @Test
    void valueOf_roundTripsAllValues() {
        for (AlertStatus s : AlertStatus.values()) {
            assertSame(s, AlertStatus.valueOf(s.name()));
        }
    }

    @Test
    void name_matchesEnumLabel() {
        assertEquals("NEW", AlertStatus.NEW.name());
        assertEquals("PENDING", AlertStatus.PENDING.name());
        assertEquals("COMMITTED", AlertStatus.COMMITTED.name());
        assertEquals("PROCESSING", AlertStatus.PROCESSING.name());
        assertEquals("PUBLISHED", AlertStatus.PUBLISHED.name());
        assertEquals("UNPUBLISHED", AlertStatus.UNPUBLISHED.name());
    }
}
