/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.enums;

import com.scplatform.pcm.forecast.service.AdjustedValueCalculator;
import com.scplatform.pcm.forecast.service.PercentageBasedAdjustmentValueCalculator;
import com.scplatform.pcm.forecast.service.ValueBasedAdjustedValueCalculator;

public enum AdjustmentType {

    FIXED("", new ValueBasedAdjustedValueCalculator()),
    PERCENT("%", new PercentageBasedAdjustmentValueCalculator());

    private final String displayString;
    private final AdjustedValueCalculator calculator;

    AdjustmentType(String displayString, AdjustedValueCalculator calc) {
        this.displayString = displayString;
        this.calculator = calc;
    }

    /**
     * @return the display string for this adjustment type (e.g. {@code "%"} for PERCENT)
     */
    public String getString() {
        return displayString;
    }

    /**
     * @return the calculator strategy that applies this adjustment
     */
    public AdjustedValueCalculator getCalculator() {
        return this.calculator;
    }

    /**
     * Look up an {@code AdjustmentType} by its display string (case-insensitive).
     *
     * @param val display string to match
     * @return matching {@code AdjustmentType}, or {@code null} if none matches
     * @throws NullPointerException if {@code val} is {@code null}
     */
    public static AdjustmentType getValueFromString(String val) {
        if (val == null) {
            throw new NullPointerException("Cannot find an adjustment type using a null mapped value");
        }
        for (AdjustmentType at : AdjustmentType.values()) {
            if (val.equalsIgnoreCase(at.getString())) {
                return at;
            }
        }
        return null;
    }
}

