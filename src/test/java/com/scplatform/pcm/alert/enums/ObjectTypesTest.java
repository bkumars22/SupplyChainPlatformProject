/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectTypesTest {

    @Test
    void hasAllExpectedValues() {
        assertEquals(7, ObjectTypes.values().length);
    }

    @Test
    void getDisplayName_returnsHumanReadableLabel() {
        assertEquals("Cost Record", ObjectTypes.COST_RECORD.getDisplayName());
        assertEquals("Item", ObjectTypes.ITEM.getDisplayName());
        assertEquals("Forecast", ObjectTypes.FORECAST.getDisplayName());
        assertEquals("Supply Allocation", ObjectTypes.SUPPLY_ALLOCATION.getDisplayName());
        assertEquals("Bill of Materials", ObjectTypes.BOM.getDisplayName());
        assertEquals("BOM Component", ObjectTypes.BOM_COMPONENT.getDisplayName());
        assertEquals("User", ObjectTypes.USER.getDisplayName());
    }

    @Test
    void valueOf_roundTripsAllValues() {
        for (ObjectTypes t : ObjectTypes.values()) {
            assertSame(t, ObjectTypes.valueOf(t.name()));
        }
    }
}
