/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.enums;

public enum ForecastModel {

    CURRENT("CURRENT"),
    ADJUSTABLE("ADJUSTABLE");

    private final String alias;

    ForecastModel(String alias) {
        this.alias = alias;
    }

    /**
     * @return the display alias for this model
     */
    public String getAlias() {
        return alias;
    }

    public static ForecastModel valueOfFromAlias(String aliasString) {
        if (aliasString == null) {
            throw new NullPointerException("Cannot find a forecast model using a null mapped value");
        }
        for (ForecastModel model : ForecastModel.values()) {
            if (aliasString.equalsIgnoreCase(model.getAlias().trim())) {
                return model;
            }
        }
        return null;
    }
}
