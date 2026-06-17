/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 *
 * Note: this class is a Spring-annotated wrapper holder named identically to
 * the entity {@code com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue}.
 * It just exposes a measure map.
 */
package com.scplatform.pcm.forecast.service;

import com.scplatform.pcm.forecast.dto.ForecastFormRecordData;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PcmSimpleForecastValueTest {

    @Test
    void getSimpleMeasureValue_createsLazilyAndCaches() {
        PcmSimpleForecastValue holder = new PcmSimpleForecastValue();
        ForecastFormRecordData.SimpleForecastValue a = holder.getSimpleMeasureValue("Q");
        ForecastFormRecordData.SimpleForecastValue b = holder.getSimpleMeasureValue("Q");
        assertNotNull(a);
        assertSame(a, b);
    }

    @Test
    void getMeasureValue_returnsRegisteredEntry() {
        PcmSimpleForecastValue holder = new PcmSimpleForecastValue();
        holder.getSimpleMeasureValue("Q");
        assertNotNull(holder.getMeasureValue("Q"));
        assertNull(holder.getMeasureValue("MISSING"));
    }

    @Test
    void getMeasureKeys_returnsAllKeys() {
        PcmSimpleForecastValue holder = new PcmSimpleForecastValue();
        holder.getSimpleMeasureValue("A");
        holder.getSimpleMeasureValue("B");
        Set keys = holder.getMeasureKeys();
        assertTrue(keys.contains("A"));
        assertTrue(keys.contains("B"));
    }
}
