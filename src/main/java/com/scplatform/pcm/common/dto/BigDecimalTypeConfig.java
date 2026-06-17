/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.common.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.scplatform.pcm.common.enums.BigDecimalObjectType;

public final class BigDecimalTypeConfig {

    private final int scale;
    private final RoundingMode roundingMode;

    public BigDecimalTypeConfig(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    public int getScale() {
        return scale;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }
    public BigDecimal normalize(BigDecimal bd) {
        return bd == null ? null : bd.setScale(scale, roundingMode);
    }
}

