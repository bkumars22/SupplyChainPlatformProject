/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.enums;

import com.scplatform.pcm.forecast.service.AdjustedValueCalculator;
import com.scplatform.pcm.forecast.service.PercentageBasedAdjustmentValueCalculator;
import com.scplatform.pcm.forecast.service.ValueBasedAdjustedValueCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjustmentTypeTest {

    @Test
    void testEnumValues() {
        AdjustmentType[] values = AdjustmentType.values();
        assertEquals(2, values.length);
        assertEquals(AdjustmentType.FIXED, values[0]);
        assertEquals(AdjustmentType.PERCENT, values[1]);
    }

    @Test
    void testGetString_Fixed() {
        assertEquals("", AdjustmentType.FIXED.getString());
    }

    @Test
    void testGetString_Percent() {
        assertEquals("%", AdjustmentType.PERCENT.getString());
    }

    @Test
    void testGetCalculator_Fixed() {
        AdjustedValueCalculator calc = AdjustmentType.FIXED.getCalculator();
        assertNotNull(calc);
        assertInstanceOf(ValueBasedAdjustedValueCalculator.class, calc);
    }

    @Test
    void testGetCalculator_Percent() {
        AdjustedValueCalculator calc = AdjustmentType.PERCENT.getCalculator();
        assertNotNull(calc);
        assertInstanceOf(PercentageBasedAdjustmentValueCalculator.class, calc);
    }

    @Test
    void testGetValueFromString_FixedByEmptyString() {
        assertEquals(AdjustmentType.FIXED, AdjustmentType.getValueFromString(""));
    }

    @Test
    void testGetValueFromString_PercentBySymbol() {
        assertEquals(AdjustmentType.PERCENT, AdjustmentType.getValueFromString("%"));
    }

    @Test
    void testGetValueFromString_NoMatch() {
        assertNull(AdjustmentType.getValueFromString("UNKNOWN"));
    }

    @Test
    void testGetValueFromString_NullThrows() {
        assertThrows(NullPointerException.class, () -> AdjustmentType.getValueFromString(null));
    }

    @Test
    void testValueOf() {
        assertEquals(AdjustmentType.FIXED, AdjustmentType.valueOf("FIXED"));
        assertEquals(AdjustmentType.PERCENT, AdjustmentType.valueOf("PERCENT"));
    }
}
