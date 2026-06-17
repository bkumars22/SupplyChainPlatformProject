/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

/**
 * Enumeration of all alert types supported by the SCPlatform alert system.
 * Each type maps to a dedicated Artemis topic: alert.topic.{name}
 *
 * <p>Topics use MULTICAST routing — every durable subscriber receives
 * a copy of each message. This allows multiple consumers (e.g., multi-node
 * deployment) to independently process alerts.</p>
 */
public enum AlertTypes {

    CostChange("Cost record was modified"),
    CostPending("Cost record awaiting approval"),
    ForecastChange("Forecast was modified"),
    SupplyAllocationChange("Supply allocation changed"),
    SupplyAllocationMissing("Required allocation missing"),
    ItemAssignment("User assigned to item"),
    ItemUnassignment("User unassigned from item"),
    CostExpiring("Cost record about to expire"),
    CostMissing("Required cost data missing"),
    BOMAttritionRateMissing("BOM attrition rate not set"),
    ForecastPending("Forecast awaiting approval");

    private final String description;

    AlertTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the Artemis topic name for this alert type.
     * e.g., "alert.topic.CostChange"
     */
    public String getTopicName() {
        return "alert.topic." + this.name();
    }

    /**
     * Returns the Artemis address name for this alert type.
     * e.g., "alert.topic.CostChange" (same as topic name for JMS mapping)
     */
    public String getAddressName() {
        return "alert.topic." + this.name();
    }

    /**
     * Returns the durable subscription name for this alert type.
     * Used by @JmsListener for durable topic subscriptions.
     * e.g., "alert.sub.CostChange"
     */
    public String getSubscriptionName() {
        return "alert.sub." + this.name();
    }
}

