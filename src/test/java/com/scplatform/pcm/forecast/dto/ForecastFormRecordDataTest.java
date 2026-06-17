/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ForecastFormRecordDataTest {

    @Test
    void noArgsConstructor_initialisesNumberFormat() {
        ForecastFormRecordData d = new ForecastFormRecordData();
        NumberFormat nf = d.getNumberFormat();
        assertNotNull(nf);
        assertEquals(6, nf.getMaximumFractionDigits());
        assertEquals(2, nf.getMinimumFractionDigits());
        assertFalse(nf.isGroupingUsed());
    }

    @Test
    void simpleAccessors() {
        ForecastFormRecordData d = new ForecastFormRecordData();
        d.setSiteKey(123L);
        assertEquals(Long.valueOf(123L), d.getSiteKey());

        d.setRowAdjustmentType(" FIXED ");
        assertEquals("FIXED", d.getRowAdjustmentType());
        d.setRowAdjustmentType("  ");
        assertNull(d.getRowAdjustmentType());

        d.setExtendPeriods(" 5 ");
        assertEquals("5", d.getExtendPeriods());
        d.setExtendPeriods("  ");
        assertNull(d.getExtendPeriods());
    }

    @Test
    void getForecastValuesForDate_createsAndCaches() {
        ForecastFormRecordData d = new ForecastFormRecordData();
        Date period = new Date(0L);
        ForecastValues v1 = d.getForecastValuesForDate(period);
        ForecastValues v2 = d.getForecastValuesForDate(period);
        assertSame(v1, v2);
        Map<Date, ForecastValues> map = d.getForecastValuesMap();
        assertTrue(map.containsKey(period));
    }

    @Test
    void getForecastValues_byTimeString() {
        ForecastFormRecordData d = new ForecastFormRecordData();
        ForecastValues v = d.getForecastValues("1700000000000");
        assertNotNull(v);
    }

    @Test
    void simpleForecastValue_settersAndGetters() {
        ForecastFormRecordData.SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        sfv.setForecastValue("  12.5 ");
        assertEquals("12.5", sfv.getForecastValue());
        sfv.setForecastValue("   ");
        assertNull(sfv.getForecastValue());

        BigDecimal bd = new BigDecimal("3.14");
        sfv.setValue(bd);
        assertSame(bd, sfv.getValue());
    }

    @Test
    void simpleForecastValue_isSubclassOfForecastValue() {
        assertTrue(new ForecastFormRecordData.SimpleForecastValue() instanceof ForecastFormRecordData.ForecastValue);
    }

    @Test
    void forecastConstructor_throwsWithoutSpringContext() throws Exception {
        Object prev = readContext();
        try {
            writeContext(null);
            assertThrows(IllegalStateException.class,
                    () -> new ForecastFormRecordData(new PcmForecast()));
        } finally {
            writeContext(prev);
        }
    }

    private static Object readContext() throws Exception {
        Field f = SpringContextHolder.class.getDeclaredField("applicationContext");
        f.setAccessible(true);
        return f.get(null);
    }

    private static void writeContext(Object value) throws Exception {
        Field f = SpringContextHolder.class.getDeclaredField("applicationContext");
        f.setAccessible(true);
        f.set(null, value);
    }
}
