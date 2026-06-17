/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.searchframework.dto.SearchForm;

public class CostForecastVarianceForm extends SearchForm {

    private ApplicationContext applicationContext = null;
    private boolean isParentFGVariance = true;

    /** No-arg constructor required by Spring DataBinder. */
    public CostForecastVarianceForm() {
        super();
    }

    public boolean isParentFGVariance() {
        return isParentFGVariance;
    }

    public void setParentFGVariance(boolean isParentFGVariance) {
        this.isParentFGVariance = isParentFGVariance;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}

