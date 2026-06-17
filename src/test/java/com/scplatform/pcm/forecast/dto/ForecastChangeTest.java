/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.service.ChangeRecordService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForecastChangeTest {

    private Object previousContext;

    @BeforeEach
    void setUp() throws Exception {
        previousContext = readStaticContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        writeStaticContext(previousContext);
    }

    @Test
    void constructor_throwsWithoutSpringContext() throws Exception {
        writeStaticContext(null);
        PcmForecast target = new PcmForecast();
        assertThrows(IllegalStateException.class, () -> new ForecastChange(target));
    }

    @Test
    void constructor_generatesRecordIdViaService() throws Exception {
        ApplicationContext ctx = mock(ApplicationContext.class);
        ChangeRecordService svc = mock(ChangeRecordService.class);
        when(ctx.getBean(ChangeRecordService.class)).thenReturn(svc);
        when(svc.generateRecordId(any(PcmForecast.class))).thenReturn("GEN-1");
        new SpringContextHolder().setApplicationContext(ctx);

        PcmForecast target = new PcmForecast();
        ForecastChange c = new ForecastChange(target);
        assertEquals("GEN-1", c.getRecordId());
        assertSame(target, c.getForecast());

        PcmForecast other = new PcmForecast();
        c.setForecast(other);
        assertSame(other, c.getForecast());

        c.setRecordId("manual");
        assertEquals("manual", c.getRecordId());
    }

    @Test
    void generateRecordId_staticDelegatesToService() throws Exception {
        ApplicationContext ctx = mock(ApplicationContext.class);
        ChangeRecordService svc = mock(ChangeRecordService.class);
        when(ctx.getBean(ChangeRecordService.class)).thenReturn(svc);
        when(svc.generateRecordId(any(PcmForecast.class))).thenReturn("X");
        new SpringContextHolder().setApplicationContext(ctx);

        assertEquals("X", ForecastChange.generateRecordId(new PcmForecast()));
    }

    private static Object readStaticContext() throws Exception {
        Field f = SpringContextHolder.class.getDeclaredField("applicationContext");
        f.setAccessible(true);
        return f.get(null);
    }

    private static void writeStaticContext(Object value) throws Exception {
        Field f = SpringContextHolder.class.getDeclaredField("applicationContext");
        f.setAccessible(true);
        f.set(null, value);
    }
}
