/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PercentageBasedAdjustmentValueCalculator implements AdjustedValueCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Override
    public BigDecimal calculate(BigDecimal baseValue, BigDecimal adjustmentAmount) {
        if (baseValue == null || adjustmentAmount == null) {
            return null;
        }
        BigDecimal delta = baseValue.multiply(adjustmentAmount)
                .divide(HUNDRED, 10, RoundingMode.HALF_UP);
        return baseValue.add(delta);
    }
}

