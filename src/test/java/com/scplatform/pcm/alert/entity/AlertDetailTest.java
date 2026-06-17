/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import com.scplatform.pcm.alert.enums.AlertDetailState;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AlertDetailTest {

    @Test
    void noArgsConstructor_isInvokable() {
        AlertDetail d = new AlertDetail();
        assertNull(d.getId());
        assertNull(d.getState());
    }

    @Test
    void builder_setsCoreFields() {
        LocalDate created = LocalDate.of(2026, 5, 8);
        LocalDate exp = LocalDate.of(2026, 6, 8);
        AlertDetail d = AlertDetail.builder()
                .id(10L)
                .state(AlertDetailState.ACTIVE)
                .dismissedBy("ops")
                .userLoginId("jdoe")
                .alertLabel("label")
                .alertId("CostChange-1")
                .alertType("CostChange")
                .created(created)
                .shortSummary("short")
                .longSummary("long")
                .expirationDate(exp)
                .punchoutUrl("http://x/y")
                .build();
        assertEquals(10L, d.getId());
        assertEquals(AlertDetailState.ACTIVE, d.getState());
        assertEquals("ops", d.getDismissedBy());
        assertEquals("jdoe", d.getUserLoginId());
        assertEquals("label", d.getAlertLabel());
        assertEquals("CostChange-1", d.getAlertId());
        assertEquals("CostChange", d.getAlertType());
        assertEquals(created, d.getCreated());
        assertEquals("short", d.getShortSummary());
        assertEquals("long", d.getLongSummary());
        assertEquals(exp, d.getExpirationDate());
        assertEquals("http://x/y", d.getPunchoutUrl());
    }

    @Test
    void stringAttributes_settersAndGetters_allTwenty() {
        AlertDetail d = new AlertDetail();
        d.setStringAttribute1("s1");
        d.setStringAttribute2("s2");
        d.setStringAttribute3("s3");
        d.setStringAttribute4("s4");
        d.setStringAttribute5("s5");
        d.setStringAttribute6("s6");
        d.setStringAttribute7("s7");
        d.setStringAttribute8("s8");
        d.setStringAttribute9("s9");
        d.setStringAttribute10("s10");
        d.setStringAttribute11("s11");
        d.setStringAttribute12("s12");
        d.setStringAttribute13("s13");
        d.setStringAttribute14("s14");
        d.setStringAttribute15("s15");
        d.setStringAttribute16("s16");
        d.setStringAttribute17("s17");
        d.setStringAttribute18("s18");
        d.setStringAttribute19("s19");
        d.setStringAttribute20("s20");
        assertEquals("s1", d.getStringAttribute1());
        assertEquals("s2", d.getStringAttribute2());
        assertEquals("s3", d.getStringAttribute3());
        assertEquals("s4", d.getStringAttribute4());
        assertEquals("s5", d.getStringAttribute5());
        assertEquals("s6", d.getStringAttribute6());
        assertEquals("s7", d.getStringAttribute7());
        assertEquals("s8", d.getStringAttribute8());
        assertEquals("s9", d.getStringAttribute9());
        assertEquals("s10", d.getStringAttribute10());
        assertEquals("s11", d.getStringAttribute11());
        assertEquals("s12", d.getStringAttribute12());
        assertEquals("s13", d.getStringAttribute13());
        assertEquals("s14", d.getStringAttribute14());
        assertEquals("s15", d.getStringAttribute15());
        assertEquals("s16", d.getStringAttribute16());
        assertEquals("s17", d.getStringAttribute17());
        assertEquals("s18", d.getStringAttribute18());
        assertEquals("s19", d.getStringAttribute19());
        assertEquals("s20", d.getStringAttribute20());
    }

    @Test
    void dateAttributes_settersAndGetters_allTen() {
        AlertDetail d = new AlertDetail();
        LocalDate base = LocalDate.of(2026, 1, 1);
        d.setDateAttribute1(base.plusDays(1));
        d.setDateAttribute2(base.plusDays(2));
        d.setDateAttribute3(base.plusDays(3));
        d.setDateAttribute4(base.plusDays(4));
        d.setDateAttribute5(base.plusDays(5));
        d.setDateAttribute6(base.plusDays(6));
        d.setDateAttribute7(base.plusDays(7));
        d.setDateAttribute8(base.plusDays(8));
        d.setDateAttribute9(base.plusDays(9));
        d.setDateAttribute10(base.plusDays(10));
        assertEquals(base.plusDays(1), d.getDateAttribute1());
        assertEquals(base.plusDays(2), d.getDateAttribute2());
        assertEquals(base.plusDays(3), d.getDateAttribute3());
        assertEquals(base.plusDays(4), d.getDateAttribute4());
        assertEquals(base.plusDays(5), d.getDateAttribute5());
        assertEquals(base.plusDays(6), d.getDateAttribute6());
        assertEquals(base.plusDays(7), d.getDateAttribute7());
        assertEquals(base.plusDays(8), d.getDateAttribute8());
        assertEquals(base.plusDays(9), d.getDateAttribute9());
        assertEquals(base.plusDays(10), d.getDateAttribute10());
    }

    @Test
    void numericAttributes_settersAndGetters_allTen() {
        AlertDetail d = new AlertDetail();
        d.setNumericAttribute1(1.0);
        d.setNumericAttribute2(2.0);
        d.setNumericAttribute3(3.0);
        d.setNumericAttribute4(4.0);
        d.setNumericAttribute5(5.0);
        d.setNumericAttribute6(6.0);
        d.setNumericAttribute7(7.0);
        d.setNumericAttribute8(8.0);
        d.setNumericAttribute9(9.0);
        d.setNumericAttribute10(10.0);
        assertEquals(1.0, d.getNumericAttribute1());
        assertEquals(2.0, d.getNumericAttribute2());
        assertEquals(3.0, d.getNumericAttribute3());
        assertEquals(4.0, d.getNumericAttribute4());
        assertEquals(5.0, d.getNumericAttribute5());
        assertEquals(6.0, d.getNumericAttribute6());
        assertEquals(7.0, d.getNumericAttribute7());
        assertEquals(8.0, d.getNumericAttribute8());
        assertEquals(9.0, d.getNumericAttribute9());
        assertEquals(10.0, d.getNumericAttribute10());
    }

    @Test
    void allArgsConstructor_yieldsObjectWithProvidedValues() {
        // Verify all-args ctor exists and runs (no assertion on every field — getters covered above)
        AlertDetail d = new AlertDetail(
                1L, AlertDetailState.DISMISSED, "ops", "user", "label", "aid", "type",
                LocalDate.now(), "ss", "ls", LocalDate.now(), "url",
                "1","2","3","4","5","6","7","8","9","10",
                "11","12","13","14","15","16","17","18","19","20",
                LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now(),
                LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now(),
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
        assertEquals(1L, d.getId());
        assertEquals(AlertDetailState.DISMISSED, d.getState());
        assertEquals("20", d.getStringAttribute20());
        assertEquals(10.0, d.getNumericAttribute10());
    }
}
