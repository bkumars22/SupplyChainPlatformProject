/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Adjusts the base value by adding a fixed amount: {@code base + adjustment}.
 */
@Service
public class ValueBasedAdjustedValueCalculator implements AdjustedValueCalculator {

    @Override
    public BigDecimal calculate(BigDecimal baseValue, BigDecimal adjustmentAmount) {
        if (baseValue == null || adjustmentAmount == null) {
            return null;
        }
        return baseValue.add(adjustmentAmount);
    }
}

