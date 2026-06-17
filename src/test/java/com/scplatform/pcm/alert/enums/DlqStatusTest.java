/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DlqStatusTest {

    @Test
    void hasFourValuesInLifecycleOrder() {
        DlqStatus[] values = DlqStatus.values();
        assertEquals(4, values.length);
        assertEquals(DlqStatus.NEW, values[0]);
        assertEquals(DlqStatus.REVIEWED, values[1]);
        assertEquals(DlqStatus.RESOLVED, values[2]);
        assertEquals(DlqStatus.REPLAYED, values[3]);
    }

    @Test
    void valueOf_roundTripsAllValues() {
        for (DlqStatus s : DlqStatus.values()) {
            assertSame(s, DlqStatus.valueOf(s.name()));
        }
    }
}
