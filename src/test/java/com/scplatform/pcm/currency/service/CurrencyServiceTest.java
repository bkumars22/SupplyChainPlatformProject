/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.currency.service;

import com.scplatform.pcm.currency.entity.CurrencyConversion;
import com.scplatform.pcm.currency.repository.CurrencyConversionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Mock private CurrencyConversionRepository repository;
    private CurrencyService service;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        setStaticRepo(repository);
        service = new CurrencyService();
    }

    @AfterEach
    void tearDown() throws Exception {
        setStaticRepo(null);
        mocks.close();
    }

    private static void setStaticRepo(CurrencyConversionRepository repo) throws Exception {
        Field f = CurrencyService.class.getDeclaredField("currencyConversionRepository");
        f.setAccessible(true);
        f.set(null, repo);
    }

    @Test
    void getCurrencyConversionRate_withDate_usesEffectiveQuery() {
        Date d = new Date(0L);
        when(repository.getAverageEffectiveConversionRate(5L, "USD", "EUR", d))
                .thenReturn(new BigDecimal("0.9"));
        BigDecimal r = CurrencyService.getCurrencyConversionRate(d, 5L, "USD", "EUR");
        assertEquals(new BigDecimal("0.9"), r);
        verify(repository, never()).getAverageConversionRate(anyLong(), anyString(), anyString());
    }

    @Test
    void getCurrencyConversionRate_withoutDate_usesGeneralQuery() {
        when(repository.getAverageConversionRate(7L, "USD", "INR"))
                .thenReturn(new BigDecimal("83.2"));
        BigDecimal r = CurrencyService.getCurrencyConversionRate(null, 7L, "USD", "INR");
        assertEquals(new BigDecimal("83.2"), r);
        verify(repository, never()).getAverageEffectiveConversionRate(
                anyLong(), anyString(), anyString(), any(Date.class));
    }

    @Test
    void saveCurrencyConversion_delegates() {
        CurrencyConversion in = new CurrencyConversion();
        CurrencyConversion saved = new CurrencyConversion();
        saved.setCurrencyKey(99L);
        when(repository.save(in)).thenReturn(saved);
        assertSame(saved, service.saveCurrencyConversion(in));
    }

    @Test
    void findConversionRates_delegates() {
        List<CurrencyConversion> list = Collections.singletonList(new CurrencyConversion());
        when(repository.findByBusinessEntityKeyAndFromCurrencyAndToCurrency(2L, "USD", "EUR"))
                .thenReturn(list);
        assertSame(list, service.findConversionRates(2L, "USD", "EUR"));
    }

    @Test
    void findEffectiveConversionRates_delegates() {
        Date d = new Date(0L);
        List<CurrencyConversion> list = Collections.emptyList();
        when(repository.findEffectiveConversionRates(2L, "USD", "EUR", d)).thenReturn(list);
        assertSame(list, service.findEffectiveConversionRates(2L, "USD", "EUR", d));
    }

    @Test
    void deleteCurrencyConversion_delegates() {
        service.deleteCurrencyConversion(123L);
        verify(repository).deleteById(123L);
    }
}
