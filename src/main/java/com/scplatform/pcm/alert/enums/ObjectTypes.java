/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

/**
 * Types of business objects that can trigger alerts.
 */
public enum ObjectTypes {

    COST_RECORD("Cost Record"),
    ITEM("Item"),
    FORECAST("Forecast"),
    SUPPLY_ALLOCATION("Supply Allocation"),
    BOM("Bill of Materials"),
    BOM_COMPONENT("BOM Component"),
    USER("User");

    private final String displayName;

    ObjectTypes(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

