/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.service;

import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.forecast.dto.ForecastForm;
import com.scplatform.pcm.forecast.dto.ForecastPeriod;
import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.repo.PcmForecastRepository;
import com.scplatform.pcm.util.validator.Errors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PcmForecastServiceTest {

    @Mock
    private PcmConfigUtil pcmConfigUtil;

    @Mock
    private ForecastFormService forecastFormService;

    @Mock
    private PcmForecastRepository pcmForecastRepository;

    @InjectMocks
    private PcmForecastService service;

    @Test
    void testFindAllByIds_DelegatesToRepository() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<PcmForecast> expected = Arrays.asList(new PcmForecast(), new PcmForecast());
        when(pcmForecastRepository.findAllById(ids)).thenReturn(expected);

        List<PcmForecast> result = service.findAllByIds(ids);
        assertSame(expected, result);
        verify(pcmForecastRepository).findAllById(ids);
    }

    @Test
    void testGetAttribute_StringAttribute1() {
        PcmForecast fc = mock(PcmForecast.class);
        when(fc.getStringAttribute1()).thenReturn("val");
        assertEquals("val", service.getAttribute(fc, "stringAttribute1"));
    }

    @Test
    void testGetAttribute_NumberAttribute5() {
        PcmForecast fc = mock(PcmForecast.class);
        when(fc.getNumberAttribute5()).thenReturn(42);
        assertEquals(42, service.getAttribute(fc, "numberAttribute5"));
    }

    @Test
    void testGetAttribute_FloatAttribute3() {
        PcmForecast fc = mock(PcmForecast.class);
        when(fc.getFloatAttribute3()).thenReturn(new BigDecimal("3.14"));
        assertEquals(new BigDecimal("3.14"), service.getAttribute(fc, "floatAttribute3"));
    }

    @Test
    void testGetAttribute_DateAttribute1() {
        PcmForecast fc = mock(PcmForecast.class);
        Date d = new Date(123456789L);
        when(fc.getDateAttribute1()).thenReturn(d);
        assertEquals(d, service.getAttribute(fc, "dateAttribute1"));
    }

    @Test
    void testGetAttribute_UnknownReturnsNull() {
        PcmForecast fc = mock(PcmForecast.class);
        assertNull(service.getAttribute(fc, "doesNotExist"));
    }

    @Test
    void testSetAttribute_StringAttribute2() {
        when(pcmConfigUtil.getList(anyString())).thenReturn(Collections.emptyList());
        PcmForecast fc = mock(PcmForecast.class);
        service.setAttribute(fc, "stringAttribute2", "hello");
        verify(fc).setStringAttribute2("hello");
    }

    @Test
    void testSetAttribute_NumberAttribute1() {
        when(pcmConfigUtil.getList(anyString())).thenReturn(Collections.emptyList());
        PcmForecast fc = mock(PcmForecast.class);
        service.setAttribute(fc, "numberAttribute1", 123);
        verify(fc).setNumberAttribute1(123);
    }

    @Test
    void testSetAttribute_FloatAttribute1() {
        when(pcmConfigUtil.getList(anyString())).thenReturn(Collections.emptyList());
        PcmForecast fc = mock(PcmForecast.class);
        BigDecimal v = new BigDecimal("9.99");
        service.setAttribute(fc, "floatAttribute1", v);
        verify(fc).setFloatAttribute1(v);
    }

    @Test
    void testSetAttribute_DateAttribute1() {
        when(pcmConfigUtil.getList(anyString())).thenReturn(Collections.emptyList());
        PcmForecast fc = mock(PcmForecast.class);
        Date d = new Date(99999L);
        service.setAttribute(fc, "dateAttribute1", d);
        verify(fc).setDateAttribute1(d);
    }

    @Test
    void testSetAttribute_ReadonlyKeyIsSkipped() {
        when(pcmConfigUtil.getList(anyString())).thenReturn(Arrays.asList("stringAttribute1"));
        PcmForecast fc = mock(PcmForecast.class);
        service.setAttribute(fc, "stringAttribute1", "no");
        verify(fc, never()).setStringAttribute1(anyString());
    }

    @Test
    void testSetReadonlyAttribute_BypassesReadonlyCheck() {
        // setReadonlyAttribute does NOT check pcmConfigUtil
        PcmForecast fc = mock(PcmForecast.class);
        service.setReadonlyAttribute(fc, "stringAttribute5", "val");
        verify(fc).setStringAttribute5("val");
    }

    @Test
    void testSetReadonlyAttribute_NumberAttribute() {
        PcmForecast fc = mock(PcmForecast.class);
        service.setReadonlyAttribute(fc, "numberAttribute8", 77);
        verify(fc).setNumberAttribute8(77);
    }

    @Test
    void testAddAttribute_DelegatesToSetAttribute() {
        when(pcmConfigUtil.getList(anyString())).thenReturn(Collections.emptyList());
        PcmForecast fc = mock(PcmForecast.class);
        Attribute attr = mock(Attribute.class);
        when(attr.getAssociatedAttribute()).thenReturn("stringAttribute3");
        when(attr.getAttrValue()).thenReturn("xyz");

        service.addAttribute(fc, attr);
        verify(fc).setStringAttribute3("xyz");
    }

    @Test
    void testGetCurrentPeriodIndex_FindsCurrent() {
        ForecastPeriod p0 = new ForecastPeriod("a", new Date(), new Date(),
                ForecastTimeline.ForecastPeriodState.PAST);
        ForecastPeriod p1 = new ForecastPeriod("b", new Date(), new Date(),
                ForecastTimeline.ForecastPeriodState.CURRENT);
        assertEquals(1, service.getCurrentPeriodIndex(Arrays.asList(p0, p1)));
    }

    @Test
    void testGetCurrentPeriodIndex_NoCurrentReturnsMinus1() {
        ForecastPeriod p0 = new ForecastPeriod("a", new Date(), new Date(),
                ForecastTimeline.ForecastPeriodState.PAST);
        assertEquals(-1, service.getCurrentPeriodIndex(Collections.singletonList(p0)));
    }

    @Test
    void testGetCurrentPeriod_Found() {
        ForecastPeriod p0 = new ForecastPeriod("X", new Date(), new Date(),
                ForecastTimeline.ForecastPeriodState.CURRENT);
        ForecastPeriod result = service.getCurrentPeriod(Collections.singletonList(p0));
        assertSame(p0, result);
    }

    @Test
    void testGetCurrentPeriod_NotFoundReturnsNull() {
        ForecastPeriod p0 = new ForecastPeriod("X", new Date(), new Date(),
                ForecastTimeline.ForecastPeriodState.FUTURE);
        assertNull(service.getCurrentPeriod(Collections.singletonList(p0)));
    }

    @Test
    void testGetFuturePeriodCount() {
        List<ForecastPeriod> periods = Arrays.asList(
                new ForecastPeriod("a", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST),
                new ForecastPeriod("b", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.FUTURE),
                new ForecastPeriod("c", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.FUTURE));
        assertEquals(2, service.getFuturePeriodCount(periods));
    }

    @Test
    void testGetPastPeriodCount() {
        List<ForecastPeriod> periods = Arrays.asList(
                new ForecastPeriod("a", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST),
                new ForecastPeriod("b", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.PAST),
                new ForecastPeriod("c", new Date(), new Date(), ForecastTimeline.ForecastPeriodState.CURRENT));
        assertEquals(2, service.getPastPeriodCount(periods));
    }

    @Test
    void testGetPeriod_FoundBySameDay() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2026, java.util.Calendar.MAY, 18, 10, 30, 0);
        Date startDate = cal.getTime();
        ForecastPeriod p = new ForecastPeriod("M", startDate, new Date(),
                ForecastTimeline.ForecastPeriodState.CURRENT);
        // Same day different hour
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        Date sameDay = cal.getTime();

        ForecastPeriod result = service.getPeriod(Collections.singletonList(p), sameDay);
        assertSame(p, result);
    }

    @Test
    void testGetPeriod_NotFoundReturnsNull() {
        ForecastPeriod p = new ForecastPeriod("M", new Date(0L), new Date(),
                ForecastTimeline.ForecastPeriodState.CURRENT);
        assertNull(service.getPeriod(Collections.singletonList(p), new Date(999999999L)));
    }

    @Test
    void testValidateForecastForm_NullFormThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.validateForecastForm(null));
    }

    @Test
    void testValidateForecastForm_DelegatesToFormService() {
        ForecastForm form = mock(ForecastForm.class);
        Errors expected = new Errors();
        when(forecastFormService.validate(eq(form), any(Errors.class))).thenReturn(expected);

        Errors result = service.validateForecastForm(form);
        assertSame(expected, result);
        verify(forecastFormService).validate(eq(form), any(Errors.class));
    }

    @Test
    void testConstructorInjection() {
        // Verify the 3-arg constructor stores collaborators
        PcmForecastService s = new PcmForecastService(pcmConfigUtil, forecastFormService, pcmForecastRepository);
        assertNotNull(s);
    }
}
