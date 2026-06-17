/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

class TenseTest {

    @Test
    void enumHasThreeValues() {
        assertEquals(3, Tense.values().length);
        Tense.valueOf("PAST");
        Tense.valueOf("PRESENT");
        Tense.valueOf("FUTURE");
    }

    @Test
    void getTenseForPeriodPresentWhenReferenceWithinRange() {
        Calendar c = Calendar.getInstance();
        Date ref = c.getTime();
        c.add(Calendar.YEAR, -1);
        Date start = c.getTime();
        c.add(Calendar.YEAR, 2);
        Date end = c.getTime();
        assertEquals(Tense.PRESENT, Tense.getTenseForPeriod(ref, start, end));
    }

    @Test
    void getTenseForPeriodFutureWhenStartAfterReference() {
        Calendar c = Calendar.getInstance();
        Date ref = c.getTime();
        c.add(Calendar.YEAR, 1);
        Date start = c.getTime();
        c.add(Calendar.YEAR, 1);
        Date end = c.getTime();
        assertEquals(Tense.FUTURE, Tense.getTenseForPeriod(ref, start, end));
    }

    @Test
    void getTenseForPeriodPastWhenEndBeforeReference() {
        Calendar c = Calendar.getInstance();
        Date ref = c.getTime();
        c.add(Calendar.YEAR, -2);
        Date start = c.getTime();
        c.add(Calendar.YEAR, 1);
        Date end = c.getTime();
        assertEquals(Tense.PAST, Tense.getTenseForPeriod(ref, start, end));
    }

    @Test
    void getTenseForPeriodWithNowOverloadDoesNotThrow() {
        // Just verify the no-reference overload returns a non-null value
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        Date start = c.getTime();
        c.add(Calendar.YEAR, 2);
        Date end = c.getTime();
        Tense t = Tense.getTenseForPeriod(start, end);
        assertEquals(Tense.PRESENT, t);
    }
}
