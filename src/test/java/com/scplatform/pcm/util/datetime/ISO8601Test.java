/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.datetime;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class ISO8601Test {

    @Test
    void parse_nullInput_returnsNull() {
        assertNull(ISO8601.parse(null));
    }

    @Test
    void parse_invalidString_returnsNull() {
        assertNull(ISO8601.parse("not-a-date"));
    }

    @Test
    void parse_zuluFormat_returnsCalendar() {
        Calendar cal = ISO8601.parse("2025-06-15T10:30:45Z");
        assertNotNull(cal);
        assertEquals(2025, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, cal.get(Calendar.MINUTE));
        assertEquals(45, cal.get(Calendar.SECOND));
        assertEquals("GMT", cal.getTimeZone().getID());
    }

    @Test
    void parse_positiveOffset_returnsCalendarWithOffset() {
        Calendar cal = ISO8601.parse("2025-06-15T10:30:45+05:30");
        assertNotNull(cal);
        assertEquals(2025, cal.get(Calendar.YEAR));
        assertEquals("GMT+05:30", cal.getTimeZone().getID());
    }

    @Test
    void parse_negativeOffset_returnsCalendarWithOffset() {
        Calendar cal = ISO8601.parse("2025-06-15T10:30:45-08:00");
        assertNotNull(cal);
        assertEquals("GMT-08:00", cal.getTimeZone().getID());
    }

    @Test
    void parse_noTimeZone_assumesZulu() {
        Calendar cal = ISO8601.parse("2025-06-15T10:30:45");
        assertNotNull(cal);
        assertEquals("GMT", cal.getTimeZone().getID());
    }

    @Test
    void format_calendar_includesTimeZoneOffset() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(2025, Calendar.JUNE, 15, 10, 30, 45);
        cal.set(Calendar.MILLISECOND, 0);
        String result = ISO8601.format(cal);
        assertTrue(result.startsWith("2025-06-15T10:30:45"));
        assertTrue(result.endsWith("+00:00"));
    }

    @Test
    void format_calendarNegativeOffset_formatsCorrectly() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-08:00"));
        cal.set(2025, Calendar.JUNE, 15, 10, 30, 45);
        String result = ISO8601.format(cal);
        assertTrue(result.endsWith("-08:00"));
    }

    @Test
    void format_date_returnsString() {
        Date d = new Date(0);
        String result = ISO8601.format(d);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void safeFormat_nullDate_returnsEmptyString() {
        assertEquals("", ISO8601.safeFormat(null));
    }

    @Test
    void safeFormat_nonNullDate_returnsFormatted() {
        String result = ISO8601.safeFormat(new Date(0));
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void parseAndFormat_roundTrip_zulu() {
        Calendar cal = ISO8601.parse("2025-06-15T10:30:45Z");
        assertNotNull(cal);
        String formatted = ISO8601.format(cal);
        assertTrue(formatted.startsWith("2025-06-15T10:30:45"));
    }
}
