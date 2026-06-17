/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.scplatform.pcm.common.dto.BigDecimalTypeConfig;
import com.scplatform.pcm.common.enums.BigDecimalObjectType;
import com.scplatform.pcm.config.util.PcmConfigUtil;


@Service
public class BigDecimalHelper {

    private  int DEFAULT_SCALE = 6;
    private  RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    private  BigDecimalTypeConfig DEFAULT_CONFIG =
            new BigDecimalTypeConfig(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);

    private final Map<BigDecimalObjectType, BigDecimalTypeConfig> configs;

    public BigDecimalHelper(PcmConfigUtil configUtil) {
        Map<BigDecimalObjectType, BigDecimalTypeConfig> built = new EnumMap<>(BigDecimalObjectType.class);
        for (BigDecimalObjectType type : BigDecimalObjectType.values()) {
            int scale = configUtil.getInteger(
                    "pcm." + type.getCode() + ".decimal.precision", DEFAULT_SCALE);
            RoundingMode mode = RoundingMode.valueOf(configUtil.getString(
                    "pcm." + type.getCode() + ".roundingmode", DEFAULT_ROUNDING_MODE.name()));
            built.put(type, new BigDecimalTypeConfig(scale, mode));
        }
        this.configs = Collections.unmodifiableMap(built);
    }

    public BigDecimalTypeConfig getConfig(BigDecimalObjectType objectType) {
        BigDecimalTypeConfig cfg = configs.get(objectType);
        return cfg != null ? cfg : DEFAULT_CONFIG;
    }

    public BigDecimal normalize(BigDecimalObjectType objectType, BigDecimal value) {
        return getConfig(objectType).normalize(value);
    }

    public int getScale(BigDecimalObjectType objectType) {
        return getConfig(objectType).getScale();
    }

    public RoundingMode getRoundingMode(BigDecimalObjectType objectType) {
        return getConfig(objectType).getRoundingMode();
    }
}
