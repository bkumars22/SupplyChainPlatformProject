/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.currency.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyConversionRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CurrencyConversionRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(CurrencyConversionRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void findByBusinessEntityKeyAndFromCurrencyAndToCurrency_methodPresent() throws NoSuchMethodException {
        Method m = CurrencyConversionRepository.class.getMethod(
                "findByBusinessEntityKeyAndFromCurrencyAndToCurrency",
                Long.class, String.class, String.class);
        assertEquals(List.class, m.getReturnType());
    }

    @Test
    void findEffectiveConversionRates_hasQueryAndCorrectReturn() throws NoSuchMethodException {
        Method m = CurrencyConversionRepository.class.getMethod("findEffectiveConversionRates",
                Long.class, String.class, String.class, Date.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("CurrencyConversion"));
        assertEquals(List.class, m.getReturnType());
    }

    @Test
    void getAverageConversionRate_returnsBigDecimalWithQuery() throws NoSuchMethodException {
        Method m = CurrencyConversionRepository.class.getMethod("getAverageConversionRate",
                Long.class, String.class, String.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("AVG"));
        assertEquals(BigDecimal.class, m.getReturnType());
    }

    @Test
    void getAverageEffectiveConversionRate_returnsBigDecimalWithQuery() throws NoSuchMethodException {
        Method m = CurrencyConversionRepository.class.getMethod("getAverageEffectiveConversionRate",
                Long.class, String.class, String.class, Date.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("AVG"));
        assertTrue(q.value().contains("startDate"));
        assertEquals(BigDecimal.class, m.getReturnType());
    }
}
