/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertDetailStateTest {

    @Test
    void hasExpectedValues() {
        assertEquals(2, AlertDetailState.values().length);
        assertEquals(AlertDetailState.ACTIVE, AlertDetailState.valueOf("ACTIVE"));
        assertEquals(AlertDetailState.DISMISSED, AlertDetailState.valueOf("DISMISSED"));
    }

    @Test
    void name_returnsExactStringValue() {
        assertEquals("ACTIVE", AlertDetailState.ACTIVE.name());
        assertEquals("DISMISSED", AlertDetailState.DISMISSED.name());
    }
}
