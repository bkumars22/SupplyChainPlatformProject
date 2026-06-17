/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.currency.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyConversionTest {

    @Test
    void noArgsConstructor_allFieldsNull() {
        CurrencyConversion c = new CurrencyConversion();
        assertNull(c.getCurrencyKey());
        assertNull(c.getBusinessEntityKey());
        assertNull(c.getFromCurrency());
        assertNull(c.getToCurrency());
        assertNull(c.getConversionRate());
        assertNull(c.getStartDate());
        assertNull(c.getEndDate());
        assertNull(c.getInsertDate());
        assertNull(c.getUpdateDate());
    }

    @Test
    void allArgsConstructor() {
        Date d = new Date(0L);
        CurrencyConversion c = new CurrencyConversion(1L, 2L, "USD", "EUR",
                new BigDecimal("0.93"), d, d, d, d);
        assertEquals(1L, c.getCurrencyKey());
        assertEquals(2L, c.getBusinessEntityKey());
        assertEquals("USD", c.getFromCurrency());
        assertEquals("EUR", c.getToCurrency());
        assertEquals(new BigDecimal("0.93"), c.getConversionRate());
        assertSame(d, c.getStartDate());
        assertSame(d, c.getEndDate());
        assertSame(d, c.getInsertDate());
        assertSame(d, c.getUpdateDate());
    }

    @Test
    void settersAndGetters() {
        CurrencyConversion c = new CurrencyConversion();
        Date d = new Date(123L);
        c.setCurrencyKey(7L);
        c.setBusinessEntityKey(8L);
        c.setFromCurrency("USD");
        c.setToCurrency("INR");
        c.setConversionRate(new BigDecimal("83.2"));
        c.setStartDate(d);
        c.setEndDate(d);
        c.setInsertDate(d);
        c.setUpdateDate(d);

        assertEquals(7L, c.getCurrencyKey());
        assertEquals(8L, c.getBusinessEntityKey());
        assertEquals("USD", c.getFromCurrency());
        assertEquals("INR", c.getToCurrency());
        assertEquals(new BigDecimal("83.2"), c.getConversionRate());
        assertSame(d, c.getStartDate());
        assertSame(d, c.getEndDate());
        assertSame(d, c.getInsertDate());
        assertSame(d, c.getUpdateDate());
    }

    @Test
    void equalsAndHashCode_lombok() {
        Date d = new Date(0L);
        CurrencyConversion a = new CurrencyConversion(1L, 2L, "USD", "EUR", BigDecimal.ONE, d, d, d, d);
        CurrencyConversion b = new CurrencyConversion(1L, 2L, "USD", "EUR", BigDecimal.ONE, d, d, d, d);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_containsCurrencyCode() {
        CurrencyConversion c = new CurrencyConversion();
        c.setFromCurrency("USD");
        assertTrue(c.toString().contains("USD"));
    }

    @Test
    void isSerializable() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(CurrencyConversion.class));
    }
}
