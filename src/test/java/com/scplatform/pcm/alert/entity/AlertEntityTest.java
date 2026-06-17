/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AlertEntityTest {

    @Test
    void constants_haveExpectedStateValues() {
        assertEquals("ACTIVE", Alert.ACTIVE);
        assertEquals("DISMISSED", Alert.DISMISSED);
    }

    @Test
    void coreFieldGettersAndSetters_roundTrip() {
        Alert a = new Alert();
        Date now = new Date();
        Date later = new Date(now.getTime() + 86400000L);

        a.setId(1L);
        a.setUserId("jdoe");
        a.setState(Alert.ACTIVE);
        a.setDismisedBy("ops");
        a.setAlertLabel("label");
        a.setAlertId("aid");
        a.setAlertType("CostChange");
        a.setCreated(now);
        a.setShortSummary("ss");
        a.setLongSummary("ls");
        a.setExpirationDate(later);
        a.setPunchOutURL("http://x");

        assertEquals(1L, a.getId());
        assertEquals("jdoe", a.getUserId());
        assertEquals(Alert.ACTIVE, a.getState());
        assertEquals("ops", a.getDismisedBy());
        assertEquals("label", a.getAlertLabel());
        assertEquals("aid", a.getAlertId());
        assertEquals("CostChange", a.getAlertType());
        assertSame(now, a.getCreated());
        assertEquals("ss", a.getShortSummary());
        assertEquals("ls", a.getLongSummary());
        assertSame(later, a.getExpirationDate());
        assertEquals("http://x", a.getPunchOutURL());
    }

    @Test
    void stringAttributes_allTwentyRoundTrip() {
        Alert a = new Alert();
        a.setStringAttribute1("1");  assertEquals("1", a.getStringAttribute1());
        a.setStringAttribute2("2");  assertEquals("2", a.getStringAttribute2());
        a.setStringAttribute3("3");  assertEquals("3", a.getStringAttribute3());
        a.setStringAttribute4("4");  assertEquals("4", a.getStringAttribute4());
        a.setStringAttribute5("5");  assertEquals("5", a.getStringAttribute5());
        a.setStringAttribute6("6");  assertEquals("6", a.getStringAttribute6());
        a.setStringAttribute7("7");  assertEquals("7", a.getStringAttribute7());
        a.setStringAttribute8("8");  assertEquals("8", a.getStringAttribute8());
        a.setStringAttribute9("9");  assertEquals("9", a.getStringAttribute9());
        a.setStringAttribute10("10"); assertEquals("10", a.getStringAttribute10());
        a.setStringAttribute11("11"); assertEquals("11", a.getStringAttribute11());
        a.setStringAttribute12("12"); assertEquals("12", a.getStringAttribute12());
        a.setStringAttribute13("13"); assertEquals("13", a.getStringAttribute13());
        a.setStringAttribute14("14"); assertEquals("14", a.getStringAttribute14());
        a.setStringAttribute15("15"); assertEquals("15", a.getStringAttribute15());
        a.setStringAttribute16("16"); assertEquals("16", a.getStringAttribute16());
        a.setStringAttribute17("17"); assertEquals("17", a.getStringAttribute17());
        a.setStringAttribute18("18"); assertEquals("18", a.getStringAttribute18());
        a.setStringAttribute19("19"); assertEquals("19", a.getStringAttribute19());
        a.setStringAttribute20("20"); assertEquals("20", a.getStringAttribute20());
    }

    @Test
    void numberAttributes_allTenRoundTrip() {
        Alert a = new Alert();
        Number[] vals = { 1, 2L, BigDecimal.valueOf(3.0), 4.0, 5.0f, 6, 7, 8, 9, 10 };
        a.setNumberAttribute1(vals[0]);  assertSame(vals[0], a.getNumberAttribute1());
        a.setNumberAttribute2(vals[1]);  assertSame(vals[1], a.getNumberAttribute2());
        a.setNumberAttribute3(vals[2]);  assertSame(vals[2], a.getNumberAttribute3());
        a.setNumberAttribute4(vals[3]);  assertSame(vals[3], a.getNumberAttribute4());
        a.setNumberAttribute5(vals[4]);  assertSame(vals[4], a.getNumberAttribute5());
        a.setNumberAttribute6(vals[5]);  assertSame(vals[5], a.getNumberAttribute6());
        a.setNumberAttribute7(vals[6]);  assertSame(vals[6], a.getNumberAttribute7());
        a.setNumberAttribute8(vals[7]);  assertSame(vals[7], a.getNumberAttribute8());
        a.setNumberAttribute9(vals[8]);  assertSame(vals[8], a.getNumberAttribute9());
        a.setNumberAttribute10(vals[9]); assertSame(vals[9], a.getNumberAttribute10());
    }

    @Test
    void dateAttributes_allTenRoundTrip() {
        Alert a = new Alert();
        Date[] dates = new Date[10];
        long base = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) dates[i] = new Date(base + i * 1000L);
        a.setDateAttribute1(dates[0]);  assertSame(dates[0], a.getDateAttribute1());
        a.setDateAttribute2(dates[1]);  assertSame(dates[1], a.getDateAttribute2());
        a.setDateAttribute3(dates[2]);  assertSame(dates[2], a.getDateAttribute3());
        a.setDateAttribute4(dates[3]);  assertSame(dates[3], a.getDateAttribute4());
        a.setDateAttribute5(dates[4]);  assertSame(dates[4], a.getDateAttribute5());
        a.setDateAttribute6(dates[5]);  assertSame(dates[5], a.getDateAttribute6());
        a.setDateAttribute7(dates[6]);  assertSame(dates[6], a.getDateAttribute7());
        a.setDateAttribute8(dates[7]);  assertSame(dates[7], a.getDateAttribute8());
        a.setDateAttribute9(dates[8]);  assertSame(dates[8], a.getDateAttribute9());
        a.setDateAttribute10(dates[9]); assertSame(dates[9], a.getDateAttribute10());
    }
}
