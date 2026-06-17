/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PercentageBasedAdjustmentValueCalculatorTest {

    private final PercentageBasedAdjustmentValueCalculator calculator =
            new PercentageBasedAdjustmentValueCalculator();

    @Test
    void testCalculate_PositiveAdjustment() {
        // base=100, adj=10% → 100 + 10 = 110
        BigDecimal result = calculator.calculate(
                BigDecimal.valueOf(100), BigDecimal.valueOf(10));
        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(110).compareTo(result.setScale(0, java.math.RoundingMode.HALF_UP)));
    }

    @Test
    void testCalculate_NegativeAdjustment() {
        // base=200, adj=-50% → 200 - 100 = 100
        BigDecimal result = calculator.calculate(
                BigDecimal.valueOf(200), BigDecimal.valueOf(-50));
        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result.setScale(0, java.math.RoundingMode.HALF_UP)));
    }

    @Test
    void testCalculate_ZeroAdjustment() {
        BigDecimal result = calculator.calculate(
                BigDecimal.valueOf(150), BigDecimal.ZERO);
        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(150).compareTo(result.setScale(0, java.math.RoundingMode.HALF_UP)));
    }

    @Test
    void testCalculate_NullBaseReturnsNull() {
        assertNull(calculator.calculate(null, BigDecimal.valueOf(10)));
    }

    @Test
    void testCalculate_NullAdjustmentReturnsNull() {
        assertNull(calculator.calculate(BigDecimal.valueOf(100), null));
    }

    @Test
    void testCalculate_BothNullReturnsNull() {
        assertNull(calculator.calculate(null, null));
    }

    @Test
    void testCalculate_FractionalPercent() {
        // base=1000, adj=0.5% → 1005
        BigDecimal result = calculator.calculate(
                BigDecimal.valueOf(1000), BigDecimal.valueOf(0.5));
        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(1005).compareTo(result.setScale(0, java.math.RoundingMode.HALF_UP)));
    }

    @Test
    void testCalculate_100Percent() {
        // base=50, adj=100% → 100
        BigDecimal result = calculator.calculate(
                BigDecimal.valueOf(50), BigDecimal.valueOf(100));
        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result.setScale(0, java.math.RoundingMode.HALF_UP)));
    }
}
