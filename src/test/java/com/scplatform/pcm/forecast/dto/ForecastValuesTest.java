/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ForecastValuesTest {

    @Test
    void getSimpleMeasureValue_createsLazilyAndCaches() {
        ForecastValues fv = new ForecastValues();
        ForecastFormRecordData.SimpleForecastValue sfv1 = fv.getSimpleMeasureValue("Q");
        ForecastFormRecordData.SimpleForecastValue sfv2 = fv.getSimpleMeasureValue("Q");
        assertNotNull(sfv1);
        assertSame(sfv1, sfv2);
    }

    @Test
    void setSimpleMeasureValue_storesAndReturnsViaGetMeasureValue() {
        ForecastValues fv = new ForecastValues();
        ForecastFormRecordData.SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        fv.setSimpleMeasureValue("Q", sfv);
        assertSame(sfv, fv.getMeasureValue("Q"));
    }

    @Test
    void getAdjustableMeasureValue_createsLazilyAndCaches() {
        ForecastValues fv = new ForecastValues();
        AdjustableForecastValue a1 = fv.getAdjustableMeasureValue("Q");
        AdjustableForecastValue a2 = fv.getAdjustableMeasureValue("Q");
        assertNotNull(a1);
        assertSame(a1, a2);
    }

    @Test
    void setAdjustableMeasureValue_stores() {
        ForecastValues fv = new ForecastValues();
        AdjustableForecastValue a = new AdjustableForecastValue();
        fv.setAdjustableMeasureValue("Q", a);
        assertSame(a, fv.getAdjustableMeasureValue("Q"));
    }

    @Test
    void getMeasureKeys_returnsAllRegisteredKeys() {
        ForecastValues fv = new ForecastValues();
        fv.getSimpleMeasureValue("S1");
        fv.getAdjustableMeasureValue("A1");
        Set keys = fv.getMeasureKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("S1"));
        assertTrue(keys.contains("A1"));
    }

    /** Tiny concrete ForecastValue stub to exercise the abstract class. */
    private static class StubForecastValue extends ForecastValue {
        boolean valid; boolean set; boolean updated; boolean created;
        @Override void validate(String prop) { valid = true; }
        @Override boolean isValueSet() { set = true; return true; }
        @Override void updatePcmForecastValue(com.scplatform.pcm.forecast.entity.PcmForecastValue pfv,
                                              ForecastChange changeRecord, ForecastTimeline timeline) { updated = true; }
        @Override com.scplatform.pcm.forecast.entity.PcmForecastValue createPcmForecastValue() {
            created = true;
            return new PcmSimpleForecastValue(new Date(), new Date(), "Q", BigDecimal.ONE, "EA");
        }
    }

    @Test
    void abstractForecastValue_stubExercisesAbstractContract() throws Exception {
        StubForecastValue v = new StubForecastValue();
        v.validate("p");
        assertTrue(v.valid);
        assertTrue(v.isValueSet());
        v.updatePcmForecastValue(null, null, null);
        assertTrue(v.updated);
        assertNotNull(v.createPcmForecastValue());
        assertTrue(v.created);
    }
}
