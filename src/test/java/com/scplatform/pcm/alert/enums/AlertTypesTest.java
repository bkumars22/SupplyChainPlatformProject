/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertTypesTest {

    @Test
    void hasExpectedNumberOfTypes() {
        assertEquals(11, AlertTypes.values().length);
    }

    @Test
    void getDescription_returnsConfiguredDescription() {
        assertEquals("Cost record was modified", AlertTypes.CostChange.getDescription());
        assertEquals("Cost record awaiting approval", AlertTypes.CostPending.getDescription());
        assertEquals("Forecast was modified", AlertTypes.ForecastChange.getDescription());
        assertEquals("Supply allocation changed", AlertTypes.SupplyAllocationChange.getDescription());
        assertEquals("Required allocation missing", AlertTypes.SupplyAllocationMissing.getDescription());
        assertEquals("User assigned to item", AlertTypes.ItemAssignment.getDescription());
        assertEquals("User unassigned from item", AlertTypes.ItemUnassignment.getDescription());
        assertEquals("Cost record about to expire", AlertTypes.CostExpiring.getDescription());
        assertEquals("Required cost data missing", AlertTypes.CostMissing.getDescription());
        assertEquals("BOM attrition rate not set", AlertTypes.BOMAttritionRateMissing.getDescription());
        assertEquals("Forecast awaiting approval", AlertTypes.ForecastPending.getDescription());
    }

    @Test
    void getTopicName_isPrefixedWithAlertTopic() {
        assertEquals("alert.topic.CostChange", AlertTypes.CostChange.getTopicName());
        assertEquals("alert.topic.ForecastPending", AlertTypes.ForecastPending.getTopicName());
    }

    @Test
    void getAddressName_matchesTopicName() {
        for (AlertTypes t : AlertTypes.values()) {
            assertEquals(t.getTopicName(), t.getAddressName());
        }
    }

    @Test
    void getSubscriptionName_isPrefixedWithAlertSub() {
        assertEquals("alert.sub.CostChange", AlertTypes.CostChange.getSubscriptionName());
        assertEquals("alert.sub.ForecastPending", AlertTypes.ForecastPending.getSubscriptionName());
    }

    @Test
    void valueOf_roundTripsAllValues() {
        for (AlertTypes t : AlertTypes.values()) {
            assertSame(t, AlertTypes.valueOf(t.name()));
        }
    }
}
