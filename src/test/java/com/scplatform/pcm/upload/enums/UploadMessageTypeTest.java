/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class UploadMessageTypeTest {

    @Test
    void itemContainsExpectedTypes() {
        List<String> types = UploadMessageType.Item.getMessageTypes();
        assertTrue(types.contains("ItemBOMAVLUI"));
        assertTrue(types.contains("ItemUI"));
        assertTrue(types.contains("ItemAVLUI"));
        assertTrue(types.contains("ItemPlatformUI"));
        assertEquals(4, types.size());
    }

    @Test
    void getMessageTypesReturnsImmutableList() {
        List<String> types = UploadMessageType.SourcingLane.getMessageTypes();
        assertFalse(types.isEmpty());
        // List.of(...) is immutable
        org.junit.jupiter.api.Assertions.assertThrows(
            UnsupportedOperationException.class, () -> types.add("X"));
    }

    @Test
    void lookupMessageType_findsCaseInsensitiveMatch() {
        assertSame(UploadMessageType.Item, UploadMessageType.lookupMessageType("ItemUI"));
        assertSame(UploadMessageType.Item, UploadMessageType.lookupMessageType("itemui"));
        assertSame(UploadMessageType.SourcingLane, UploadMessageType.lookupMessageType("CostRecordUI"));
        assertSame(UploadMessageType.SupplierAllocation,
                UploadMessageType.lookupMessageType("SupplierAllocationUI"));
        assertSame(UploadMessageType.Currency, UploadMessageType.lookupMessageType("CurrencyUploadUI"));
    }

    @Test
    void lookupMessageType_returnsNullForUnknown() {
        assertNull(UploadMessageType.lookupMessageType("NoSuchType"));
        assertNull(UploadMessageType.lookupMessageType(""));
    }

    @Test
    void allEnumValuesHaveAtLeastOneType() {
        for (UploadMessageType t : UploadMessageType.values()) {
            assertFalse(t.getMessageTypes().isEmpty(), "Type " + t.name() + " has no message types");
        }
    }
}
