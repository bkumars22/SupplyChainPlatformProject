/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.service;

import com.scplatform.pcm.fiscalPeriod.service.FiscalPeriodService;
import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PcmForecastValueServiceTest {

    @Mock
    private FiscalPeriodService fiscalPeriodService;

    @InjectMocks
    private PcmForecastValueService service;

    private PcmForecast forecast;
    private PcmSimpleForecastValue fv1;
    private PcmSimpleForecastValue fv2;

    @BeforeEach
    void setUp() {
        forecast = mock(PcmForecast.class);

        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date jan = cal.getTime();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        Date feb = cal.getTime();

        fv1 = new PcmSimpleForecastValue(jan, DateUtils.addDays(jan, 30), "QUANTITY", new BigDecimal("10"), "EA");
        fv2 = new PcmSimpleForecastValue(feb, DateUtils.addDays(feb, 28), "QUANTITY", new BigDecimal("20"), "EA");

        SortedSet<PcmForecastValue> values = new TreeSet<>();
        values.add(fv1);
        values.add(fv2);
        when(forecast.getForecastValues()).thenReturn(values);
    }

    @Test
    void testGetForecastValuesByPeriod_ReturnsSortedMap() {
        SortedMap<Date, Map<String, PcmForecastValue>> result =
                service.getForecastValuesByPeriod(forecast);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetForecastValuesByPeriod_GroupsByDate() {
        SortedMap<Date, Map<String, PcmForecastValue>> result =
                service.getForecastValuesByPeriod(forecast);
        // each date has one entry for "QUANTITY"
        for (Map<String, PcmForecastValue> periodMap : result.values()) {
            assertTrue(periodMap.containsKey("QUANTITY"));
        }
    }

    @Test
    void testGetForecastValuesByPeriod_Immutable() {
        SortedMap<Date, Map<String, PcmForecastValue>> result =
                service.getForecastValuesByPeriod(forecast);
        assertThrows(UnsupportedOperationException.class,
                () -> result.put(new Date(), Collections.emptyMap()));
    }

    @Test
    void testGetForecastValuesByMeasure_GroupsByMeasure() {
        Map<String, SortedSet<PcmForecastValue>> result =
                service.getForecastValuesByMeasure(forecast);
        assertNotNull(result);
        assertTrue(result.containsKey("QUANTITY"));
        assertEquals(2, result.get("QUANTITY").size());
    }

    @Test
    void testGetForecastValuesByMeasure_Immutable() {
        Map<String, SortedSet<PcmForecastValue>> result =
                service.getForecastValuesByMeasure(forecast);
        assertThrows(UnsupportedOperationException.class,
                () -> result.put("NEW_KEY", new TreeSet<>()));
    }

    @Test
    void testGetForecastValuesForMeasure_MatchesByName() {
        SortedSet<PcmForecastValue> result =
                service.getForecastValuesForMeasure(forecast, "QUANTITY");
        assertEquals(2, result.size());
    }

    @Test
    void testGetForecastValuesForMeasure_CaseInsensitive() {
        SortedSet<PcmForecastValue> result =
                service.getForecastValuesForMeasure(forecast, "quantity");
        assertEquals(2, result.size());
    }

    @Test
    void testGetForecastValuesForMeasure_NoMatch() {
        SortedSet<PcmForecastValue> result =
                service.getForecastValuesForMeasure(forecast, "COST");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetForecastValue_ByMeasureAndDate() {
        Date jan;
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        jan = cal.getTime();

        PcmForecastValue result = service.getForecastValue(forecast, "QUANTITY", jan);
        assertSame(fv1, result);
    }

    @Test
    void testGetForecastValue_NoMatch() {
        assertNull(service.getForecastValue(forecast, "COST", new Date(0L)));
    }

    @Test
    void testGetForecastValueByPath_ReturnsNull() {
        // PcmForecastValue.getPath() — not set on our test values → no match
        PcmForecastValue result = service.getForecastValueByPath(forecast, "some/path");
        assertNull(result);
    }

    @Test
    void testAddForecastValue_Links() {
        PcmForecast realForecast = new PcmForecast();
        PcmSimpleForecastValue newFv = new PcmSimpleForecastValue();
        // PcmForecast.getForecastValues() returns a live SortedSet
        assertTrue(service.addForecastValue(realForecast, newFv));
        assertSame(realForecast, newFv.getForecast());
    }

    @Test
    void testRemoveForecastValue_UnlinksFromForecast() {
        PcmForecast realForecast = new PcmForecast();
        PcmSimpleForecastValue newFv = new PcmSimpleForecastValue();
        service.addForecastValue(realForecast, newFv);
        assertTrue(service.removeForecastValue(realForecast, newFv));
        assertNull(newFv.getForecast());
    }

    @Test
    void testRemoveForecastValue_NotPresent() {
        PcmForecast realForecast = new PcmForecast();
        PcmSimpleForecastValue notAdded = new PcmSimpleForecastValue();
        assertFalse(service.removeForecastValue(realForecast, notAdded));
    }

    @Test
    void testGetForecastValuesBeyondDate() {
        PcmForecast realForecast = new PcmForecast();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date jan = cal.getTime();
        Date feb = DateUtils.addMonths(jan, 1);
        Date mar = DateUtils.addMonths(jan, 2);

        PcmSimpleForecastValue f1 = new PcmSimpleForecastValue(jan, DateUtils.addDays(jan, 30), "Q", BigDecimal.ONE, "EA");
        PcmSimpleForecastValue f2 = new PcmSimpleForecastValue(feb, DateUtils.addDays(feb, 28), "Q", BigDecimal.TEN, "EA");
        PcmSimpleForecastValue f3 = new PcmSimpleForecastValue(mar, DateUtils.addDays(mar, 31), "Q", new BigDecimal("5"), "EA");
        service.addForecastValue(realForecast, f1);
        service.addForecastValue(realForecast, f2);
        service.addForecastValue(realForecast, f3);

        SortedSet<PcmForecastValue> beyond = service.getForecastValuesBeyondDate(realForecast, jan);
        assertEquals(2, beyond.size());
        assertFalse(beyond.contains(f1));
        assertTrue(beyond.contains(f2));
        assertTrue(beyond.contains(f3));
    }

    @Test
    void testRemoveForecastValuesBeyondDate() {
        PcmForecast realForecast = new PcmForecast();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date jan = cal.getTime();
        Date feb = DateUtils.addMonths(jan, 1);

        PcmSimpleForecastValue f1 = new PcmSimpleForecastValue(jan, DateUtils.addDays(jan, 30), "Q", BigDecimal.ONE, "EA");
        PcmSimpleForecastValue f2 = new PcmSimpleForecastValue(feb, DateUtils.addDays(feb, 28), "Q", BigDecimal.TEN, "EA");
        service.addForecastValue(realForecast, f1);
        service.addForecastValue(realForecast, f2);

        SortedSet<PcmForecastValue> removed = service.removeForecastValuesBeyondDate(realForecast, jan);
        assertEquals(1, removed.size());
        assertTrue(removed.contains(f2));
        assertEquals(1, realForecast.getForecastValues().size());
        assertTrue(realForecast.getForecastValues().contains(f1));
    }

    @Test
    void testNewChangeTracker_NotNullAndObservesObject() {
        PcmSimpleForecastValue val = new PcmSimpleForecastValue();
        com.scplatform.pcm.common.dto.ChangeTracker<PcmForecastValue> tracker =
                service.newChangeTracker(val);
        assertNotNull(tracker);
        assertSame(val, tracker.getObservedObject());
    }
}
