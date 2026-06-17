/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostForecastVarianceFormTest {

    @Test
    void testDefaultConstructor() {
        CostForecastVarianceForm form = new CostForecastVarianceForm();
        assertNotNull(form);
    }

    @Test
    void testIsParentFGVariance_DefaultTrue() {
        CostForecastVarianceForm form = new CostForecastVarianceForm();
        assertTrue(form.isParentFGVariance());
    }

    @Test
    void testSetParentFGVariance() {
        CostForecastVarianceForm form = new CostForecastVarianceForm();
        form.setParentFGVariance(false);
        assertFalse(form.isParentFGVariance());
        form.setParentFGVariance(true);
        assertTrue(form.isParentFGVariance());
    }

    @Test
    void testGetApplicationContext_DefaultNull() {
        CostForecastVarianceForm form = new CostForecastVarianceForm();
        assertNull(form.getApplicationContext());
    }

    @Test
    void testSetApplicationContext() {
        CostForecastVarianceForm form = new CostForecastVarianceForm();
        ApplicationContext ctx = new ApplicationContext();
        form.setApplicationContext(ctx);
        assertSame(ctx, form.getApplicationContext());
    }
}
