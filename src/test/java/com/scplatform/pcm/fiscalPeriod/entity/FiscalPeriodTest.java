/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.fiscalPeriod.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class FiscalPeriodTest {

    @Test
    void defaultConstructor() {
        FiscalPeriod fp = new FiscalPeriod();
        assertNull(fp.getFiscalPeriodType());
        assertNull(fp.getFiscalPeriodName());
        assertEquals(0, fp.getFiscalPeriod());
        assertNull(fp.getFiscalPeriodStartDate());
        assertNull(fp.getFiscalPeriodEndDate());
    }

    @Test
    void allArgsConstructor() {
        Date start = new Date(1000L);
        Date end = new Date(2000L);
        FiscalPeriod fp = new FiscalPeriod("M", 5, "May", start, end);
        assertEquals("M", fp.getFiscalPeriodType());
        assertEquals("May", fp.getFiscalPeriodName());
        assertEquals(5, fp.getFiscalPeriod());
        assertEquals(start, fp.getFiscalPeriodStartDate());
        assertEquals(end, fp.getFiscalPeriodEndDate());
    }

    @Test
    void settersAndGetters() {
        FiscalPeriod fp = new FiscalPeriod();
        Date start = new Date(100L);
        Date end = new Date(200L);
        fp.setFiscalPeriodType("Q");
        fp.setFiscalPeriodName("Q1");
        fp.setFiscalPeriod(1);
        fp.setFiscalPeriodStartDate(start);
        fp.setFiscalPeriodEndDate(end);

        assertEquals("Q", fp.getFiscalPeriodType());
        assertEquals("Q1", fp.getFiscalPeriodName());
        assertEquals(1, fp.getFiscalPeriod());
        assertEquals(start, fp.getFiscalPeriodStartDate());
        assertEquals(end, fp.getFiscalPeriodEndDate());
    }

    @Test
    void periodTypeEnum() {
        assertEquals("W", FiscalPeriod.PeriodType.WEEK.getType());
        assertEquals("M", FiscalPeriod.PeriodType.MONTH.getType());
        assertEquals("Q", FiscalPeriod.PeriodType.QUARTER.getType());
        assertEquals("Y", FiscalPeriod.PeriodType.YEAR.getType());
    }

    @Test
    void equalsReflexive() {
        FiscalPeriod fp = new FiscalPeriod("M", 1, "Jan", new Date(0L), new Date(1000L));
        assertEquals(fp, fp);
    }

    @Test
    void equalsNull() {
        FiscalPeriod fp = new FiscalPeriod();
        assertNotEquals(null, fp);
    }

    @Test
    void equalsDifferentType() {
        FiscalPeriod fp = new FiscalPeriod();
        assertNotEquals("string", fp);
    }

    @Test
    void equalsSameFields() {
        Date start = new Date(0L);
        Date end = new Date(1000L);
        FiscalPeriod a = new FiscalPeriod("M", 1, "Jan", start, end);
        FiscalPeriod b = new FiscalPeriod("M", 1, "Jan", start, end);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEqualsDifferentPeriod() {
        Date start = new Date(0L);
        Date end = new Date(1000L);
        FiscalPeriod a = new FiscalPeriod("M", 1, "Jan", start, end);
        FiscalPeriod b = new FiscalPeriod("M", 2, "Feb", start, end);
        assertNotEquals(a, b);
    }

    @Test
    void toStringContainsType() {
        FiscalPeriod fp = new FiscalPeriod("M", 1, "Jan", new Date(0L), new Date(1000L));
        String s = fp.toString();
        assertTrue(s.contains("M"));
    }

    @Test
    void fiscalPeriodIdEquality() {
        Date start = new Date(0L);
        Date end = new Date(1000L);
        FiscalPeriod.FiscalPeriodId a = new FiscalPeriod.FiscalPeriodId("M", "Jan", 1, start, end);
        FiscalPeriod.FiscalPeriodId b = new FiscalPeriod.FiscalPeriodId("M", "Jan", 1, start, end);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void fiscalPeriodIdReflexive() {
        FiscalPeriod.FiscalPeriodId id = new FiscalPeriod.FiscalPeriodId();
        assertEquals(id, id);
    }

    @Test
    void fiscalPeriodIdNotEqualsNull() {
        FiscalPeriod.FiscalPeriodId id = new FiscalPeriod.FiscalPeriodId();
        assertNotEquals(null, id);
    }

    @Test
    void fiscalPeriodIdNotEqualsDifferentType() {
        FiscalPeriod.FiscalPeriodId id = new FiscalPeriod.FiscalPeriodId();
        assertNotEquals("string", id);
    }

    @Test
    void getPeriodTense() {
        Date past = new Date(0L);
        Date present = new Date(System.currentTimeMillis() + 1_000_000L);
        FiscalPeriod fp = new FiscalPeriod("M", 1, "Jan", past, present);
        assertNotNull(fp.getPeriodTense());
    }
}
