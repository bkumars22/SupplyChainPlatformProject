/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.currency.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void defaultConstructor() {
        Currency c = new Currency();
        assertNull(c.getCurrencyCode());
        assertNull(c.getCurrencyName());
    }

    @Test
    void allArgsConstructor() {
        Currency c = new Currency("USD", "US Dollar");
        assertEquals("USD", c.getCurrencyCode());
        assertEquals("US Dollar", c.getCurrencyName());
    }

    @Test
    void settersAndGetters() {
        Currency c = new Currency();
        c.setCurrencyCode("EUR");
        c.setCurrencyName("Euro");
        assertEquals("EUR", c.getCurrencyCode());
        assertEquals("Euro", c.getCurrencyName());
    }

    @Test
    void equals_reflexive() {
        Currency c = new Currency("USD", "US Dollar");
        assertEquals(c, c);
    }

    @Test
    void equals_null_returnsFalse() {
        assertNotEquals(null, new Currency());
    }

    @Test
    void equals_otherType_returnsFalse() {
        assertNotEquals("USD", new Currency("USD", "n"));
    }

    @Test
    void equals_sameCurrencyCode_isEqual() {
        Currency a = new Currency("USD", "x");
        Currency b = new Currency("USD", "y");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentCurrencyCode_notEqual() {
        assertNotEquals(new Currency("USD", "x"), new Currency("EUR", "x"));
    }

    @Test
    void isSerializable() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(Currency.class));
    }
}
