/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ValueBasedAdjustedValueCalculatorTest {

    private final ValueBasedAdjustedValueCalculator calculator =
            new ValueBasedAdjustedValueCalculator();

    @Test
    void testCalculate_PositiveAmount() {
        BigDecimal result = calculator.calculate(
                BigDecimal.valueOf(100), BigDecimal.valueOf(25));
        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(125).compareTo(result));
    }

    @Test
    void testCalculate_NegativeAmount() {
        BigDecimal result = calculator.calculate(
                BigDecimal.valueOf(100), BigDecimal.valueOf(-30));
        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(70).compareTo(result));
    }

    @Test
    void testCalculate_ZeroAmount() {
        BigDecimal result = calculator.calculate(
                BigDecimal.valueOf(200), BigDecimal.ZERO);
        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(200).compareTo(result));
    }

    @Test
    void testCalculate_NullBaseReturnsNull() {
        assertNull(calculator.calculate(null, BigDecimal.valueOf(10)));
    }

    @Test
    void testCalculate_NullAmountReturnsNull() {
        assertNull(calculator.calculate(BigDecimal.valueOf(100), null));
    }

    @Test
    void testCalculate_BothNullReturnsNull() {
        assertNull(calculator.calculate(null, null));
    }

    @Test
    void testCalculate_FractionalValues() {
        BigDecimal result = calculator.calculate(
                new BigDecimal("99.99"), new BigDecimal("0.01"));
        assertNotNull(result);
        assertEquals(0, new BigDecimal("100.00").compareTo(result));
    }
}
