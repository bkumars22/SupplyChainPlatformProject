/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.priceTam.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PriceTAMFiscalDataTest {

    private PriceTAMFiscalData newData(long ts, double price, double alloc) {
        return new PriceTAMFiscalData(new Date(ts), new Date(ts + 1000), price, alloc, false, false);
    }

    @Test
    void constructorAndGetters() {
        Date s = new Date(1L);
        Date e = new Date(2L);
        PriceTAMFiscalData d = new PriceTAMFiscalData(s, e, 1.5, 2.0, true, false);
        assertEquals(s, d.getFiscalStartDate());
        assertEquals(e, d.getFiscalEndDate());
        assertEquals(1.5, d.getPrice());
        assertEquals(2.0, d.getAllocation());
        assertTrue(d.isAllocationVariance());
        assertTrue(d.getIsAllocationVariance());
        assertFalse(d.isPriceVariance());
        assertFalse(d.getIsPriceVariance());
    }

    @Test
    void setters() {
        PriceTAMFiscalData d = newData(1L, 1.0, 1.0);
        d.setFiscalStartDate(new Date(10L));
        d.setFiscalEndDate(new Date(20L));
        d.setPrice(9.0);
        d.setAllocation(8.0);
        d.setAllocationVariance(true);
        d.setPriceVariance(true);

        assertEquals(new Date(10L), d.getFiscalStartDate());
        assertEquals(new Date(20L), d.getFiscalEndDate());
        assertEquals(9.0, d.getPrice());
        assertEquals(8.0, d.getAllocation());
        assertTrue(d.isAllocationVariance());
        assertTrue(d.isPriceVariance());
    }

    @Test
    void compareTo_byStartDate() {
        PriceTAMFiscalData a = newData(100L, 1.0, 1.0);
        PriceTAMFiscalData b = newData(200L, 1.0, 1.0);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(newData(100L, 9.0, 9.0)));
    }

    @Test
    void equalsAndHashCode_sameValues() {
        PriceTAMFiscalData a = newData(100L, 1.0, 2.0);
        PriceTAMFiscalData b = newData(100L, 1.0, 2.0);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentPrice() {
        assertNotEquals(newData(100L, 1.0, 2.0), newData(100L, 9.0, 2.0));
    }

    @Test
    void equals_nullsAndType() {
        PriceTAMFiscalData a = newData(100L, 1.0, 2.0);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
        assertEquals(a, a);
    }

    @Test
    void toString_containsFields() {
        String s = newData(100L, 1.5, 2.5).toString();
        assertTrue(s.contains("PriceTAMFiscalData"));
        assertTrue(s.contains("price=1.5"));
        assertTrue(s.contains("allocation=2.5"));
    }
}
