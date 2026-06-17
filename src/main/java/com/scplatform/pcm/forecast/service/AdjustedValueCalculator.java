/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;

import java.math.BigDecimal;
public interface AdjustedValueCalculator {

    BigDecimal calculate(BigDecimal baseValue, BigDecimal adjustmentAmount);
}

