/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.forecast.enums.AdjustmentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AdjustableForecastValueTest {

    @Test
    void defaultConstructor_leavesFieldsNull() {
        AdjustableForecastValue v = new AdjustableForecastValue();
        assertNull(v.getAdjustmentAmount());
        assertNull(v.getAdjustmentType());
        assertNull(v.getAdjAmnt());
        assertNull(v.getType());
    }

    @Test
    void setAdjustmentAmount_trimsToNull() {
        AdjustableForecastValue v = new AdjustableForecastValue();
        v.setAdjustmentAmount("   ");
        assertNull(v.getAdjustmentAmount());
        v.setAdjustmentAmount("  10.5 ");
        assertEquals("10.5", v.getAdjustmentAmount());
    }

    @Test
    void setAdjustmentType_trimsToNull() {
        AdjustableForecastValue v = new AdjustableForecastValue();
        v.setAdjustmentType("  ");
        assertNull(v.getAdjustmentType());
        v.setAdjustmentType("  FIXED ");
        assertEquals("FIXED", v.getAdjustmentType());
    }

    @Test
    void parsedFields_settersAndGetters() {
        AdjustableForecastValue v = new AdjustableForecastValue();
        BigDecimal amt = new BigDecimal("5.5");
        v.setAdjAmnt(amt);
        assertSame(amt, v.getAdjAmnt());

        v.setType(AdjustmentType.FIXED);
        assertEquals(AdjustmentType.FIXED, v.getType());
    }

    @Test
    void isSubclassOfForecastFormRecordDataForecastValue() {
        assertTrue(new AdjustableForecastValue() instanceof ForecastFormRecordData.ForecastValue);
    }
}
