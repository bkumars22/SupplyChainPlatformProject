/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PcmCostRecordValueTypeTest {

    @Test
    void testAllValuesPresent() {
        PcmCostRecordValueType[] vals = PcmCostRecordValueType.values();
        assertEquals(8, vals.length);
        assertNotNull(PcmCostRecordValueType.valueOf("S"));
        assertNotNull(PcmCostRecordValueType.valueOf("B"));
        assertNotNull(PcmCostRecordValueType.valueOf("PM"));
        assertNotNull(PcmCostRecordValueType.valueOf("PT"));
        assertNotNull(PcmCostRecordValueType.valueOf("PF"));
        assertNotNull(PcmCostRecordValueType.valueOf("PR"));
        assertNotNull(PcmCostRecordValueType.valueOf("P"));
        assertNotNull(PcmCostRecordValueType.valueOf("C"));
    }

    @Test
    void testOrdinalDeterministic() {
        // Ensure declaration order is preserved (downstream code may rely on it).
        assertEquals(0, PcmCostRecordValueType.S.ordinal());
        assertEquals(7, PcmCostRecordValueType.C.ordinal());
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> PcmCostRecordValueType.valueOf("ZZ"));
    }
}
