/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.common.enums;

/**
 * Object types whose BigDecimal precision/rounding can be configured
 * independently via {@code pcm.<code>.decimal.precision} and
 * {@code pcm.<code>.roundingmode} properties.
 */
public enum BigDecimalObjectType {

    FORECASTS("forecast"),
    COST_RECORDS("costrecord"),
    ITEM_CATEGORY_COST_RECORD("itemcategorycostrecord");

    private final String code;

    BigDecimalObjectType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

