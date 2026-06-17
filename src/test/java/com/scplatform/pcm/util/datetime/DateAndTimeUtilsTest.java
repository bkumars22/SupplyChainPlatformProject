/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.datetime;

import com.scplatform.pcm.util.datetime.DateAndTimeUtils.DateValidationType;
import com.scplatform.pcm.util.datetime.DateAndTimeUtils.Unit;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DateAndTimeUtilsTest {

    private Date date(int y, int m, int d) {
        Calendar c = Calendar.getInstance();
        c.set(y, m - 1, d, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private Calendar cal(int y, int m, int d) {
        Calendar c = Calendar.getInstance();
        c.set(y, m - 1, d, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    @Test
    void defaultToMax_nullReturnsMax() {
        Date d = DateAndTimeUtils.defaultToMax(null);
        assertNotNull(d);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        // Source code uses set(9999, 12, ...) — month 12 rolls over to year 10000 (Jan)
        assertTrue(c.get(Calendar.YEAR) >= 9999);
    }

    @Test
    void defaultToMax_nonNullReturnsSame() {
        Date d = date(2025, 1, 1);
        assertEquals(d, DateAndTimeUtils.defaultToMax(d));
    }

    @Test
    void getCurrentDateOnly_hasZeroTime() {
        Date d = DateAndTimeUtils.getCurrentDateOnly();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
    }

    @Test
    void dateOnly_truncatesTime() {
        Calendar c = Calendar.getInstance();
        c.set(2025, Calendar.JUNE, 15, 14, 35, 22);
        Date d = DateAndTimeUtils.dateOnly(c.getTime());
        Calendar r = Calendar.getInstance();
        r.setTime(d);
        assertEquals(0, r.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, r.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void dateOnly_handlesSqlDate() {
        java.sql.Date sql = new java.sql.Date(System.currentTimeMillis());
        assertNotNull(DateAndTimeUtils.dateOnly(sql));
    }

    @Test
    void before_inclusiveTrue_whenSame() {
        assertTrue(DateAndTimeUtils.before(date(2025, 1, 1), date(2025, 1, 1), true));
    }

    @Test
    void before_exclusiveFalse_whenSame() {
        assertFalse(DateAndTimeUtils.before(date(2025, 1, 1), date(2025, 1, 1), false));
    }

    @Test
    void before_strictBefore() {
        assertTrue(DateAndTimeUtils.before(date(2025, 1, 1), date(2025, 1, 2), false));
    }

    @Test
    void after_inclusiveTrue_whenSame() {
        assertTrue(DateAndTimeUtils.after(date(2025, 1, 1), date(2025, 1, 1), true));
    }

    @Test
    void after_exclusiveFalse_whenSame() {
        assertFalse(DateAndTimeUtils.after(date(2025, 1, 1), date(2025, 1, 1), false));
    }

    @Test
    void after_strictAfter() {
        assertTrue(DateAndTimeUtils.after(date(2025, 1, 2), date(2025, 1, 1), false));
    }

    @Test
    void same_returnsTrueForEqualDates() {
        assertTrue(DateAndTimeUtils.same(date(2025, 1, 1), date(2025, 1, 1)));
    }

    @Test
    void same_returnsFalseForDifferentDates() {
        assertFalse(DateAndTimeUtils.same(date(2025, 1, 1), date(2025, 1, 2)));
    }

    @Test
    void overlap_inclusiveDefault_overlapping() {
        assertTrue(DateAndTimeUtils.overlap(date(2025, 1, 1), date(2025, 1, 10),
                date(2025, 1, 5), date(2025, 1, 15)));
    }

    @Test
    void overlap_inclusiveType_complete() {
        assertTrue(DateAndTimeUtils.overlap(date(2025, 1, 1), date(2025, 1, 31),
                date(2025, 1, 10), date(2025, 1, 20), DateValidationType.INCLUSIVE));
    }

    @Test
    void overlap_exclusiveType_touchingBoundaryReturnsFalse() {
        assertFalse(DateAndTimeUtils.overlap(date(2025, 1, 10), date(2025, 1, 20),
                date(2025, 1, 20), date(2025, 1, 30), DateValidationType.EXCLUSIVE));
    }

    @Test
    void overlap_exclusiveEnd_completeOverlap() {
        assertTrue(DateAndTimeUtils.overlap(date(2025, 1, 1), date(2025, 1, 31),
                date(2025, 1, 10), date(2025, 1, 20), DateValidationType.EXCLUSIVE_END));
    }

    @Test
    void overlap_nonOverlapping_returnsFalse() {
        assertFalse(DateAndTimeUtils.overlap(date(2025, 1, 1), date(2025, 1, 5),
                date(2025, 2, 1), date(2025, 2, 5)));
    }

    @Test
    void between_inclusiveBoundaries() {
        assertTrue(DateAndTimeUtils.between(date(2025, 1, 1), date(2025, 1, 1), date(2025, 1, 7)));
        assertTrue(DateAndTimeUtils.between(date(2025, 1, 7), date(2025, 1, 1), date(2025, 1, 7)));
        assertTrue(DateAndTimeUtils.between(date(2025, 1, 5), date(2025, 1, 1), date(2025, 1, 7)));
    }

    @Test
    void between_outsideRange_returnsFalse() {
        assertFalse(DateAndTimeUtils.between(date(2025, 1, 8), date(2025, 1, 1), date(2025, 1, 7)));
    }

    @Test
    void betweenWithTime_includesTime() {
        Date now = new Date();
        Date earlier = new Date(now.getTime() - 1000);
        Date later = new Date(now.getTime() + 1000);
        assertTrue(DateAndTimeUtils.betweenWithTime(now, earlier, later));
    }

    @Test
    void diffInDays_positiveAndNegative() {
        long diff = DateAndTimeUtils.diffInDays(date(2025, 1, 1), date(2025, 1, 5));
        assertEquals(-4, diff);
        long diff2 = DateAndTimeUtils.diffInDays(date(2025, 1, 5), date(2025, 1, 1));
        assertEquals(4, diff2);
    }

    @Test
    void add_smallIncrement() {
        Calendar c = cal(2025, 1, 1);
        DateAndTimeUtils.add(c, Calendar.DAY_OF_MONTH, 5);
        assertEquals(6, c.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void add_negativeIncrement() {
        Calendar c = cal(2025, 1, 10);
        DateAndTimeUtils.add(c, Calendar.DAY_OF_MONTH, -3);
        assertEquals(7, c.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void difference_calendarUnit_days() {
        long diff = DateAndTimeUtils.difference(cal(2025, 1, 1), cal(2025, 1, 11), Unit.DAY);
        assertEquals(10, diff);
    }

    @Test
    void difference_dateUnit_days() {
        long diff = DateAndTimeUtils.difference(date(2025, 1, 1), date(2025, 1, 11), Unit.DAY);
        assertEquals(10, diff);
    }

    @Test
    void difference_hours() {
        Calendar c1 = Calendar.getInstance();
        c1.set(2025, 0, 1, 0, 0, 0);
        Calendar c2 = (Calendar) c1.clone();
        c2.add(Calendar.HOUR_OF_DAY, 5);
        assertEquals(5, DateAndTimeUtils.difference(c1, c2, Unit.HOUR));
    }

    @Test
    void exactDifference_returnsFractional() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(2025, 0, 1, 0, 0, 0);
        c1.set(Calendar.MILLISECOND, 0);
        Calendar c2 = (Calendar) c1.clone();
        c2.add(Calendar.HOUR_OF_DAY, 36);
        double d = DateAndTimeUtils.exactDifference(c1, c2, Unit.DAY);
        assertEquals(1.5, d, 0.01);
    }

    @Test
    void tieredDifference_allUnits() {
        Map<Unit, Long> diff = DateAndTimeUtils.tieredDifference(cal(2024, 1, 1), cal(2025, 3, 5));
        assertEquals(1L, diff.get(Unit.YEAR));
        assertEquals(2L, diff.get(Unit.MONTH));
        assertEquals(4L, diff.get(Unit.DAY));
    }

    @Test
    void tieredDifference_specificUnits() {
        Map<Unit, Long> diff = DateAndTimeUtils.tieredDifference(cal(2024, 1, 1), cal(2025, 3, 5),
                List.of(Unit.YEAR, Unit.MONTH));
        assertTrue(diff.containsKey(Unit.YEAR));
        assertTrue(diff.containsKey(Unit.MONTH));
        assertFalse(diff.containsKey(Unit.DAY));
    }

    @Test
    void addDays_simpleAddition() {
        Date result = DateAndTimeUtils.addDays(date(2025, 7, 6), 3, false, Collections.emptyList());
        Calendar c = Calendar.getInstance();
        c.setTime(result);
        assertEquals(9, c.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void addDays_excludeWeekends() {
        // 2025-07-04 is Friday → +2 business days = 2025-07-08 (Tue)
        Date result = DateAndTimeUtils.addDays(date(2025, 7, 4), 2, true, Collections.emptyList());
        Calendar c = Calendar.getInstance();
        c.setTime(result);
        assertEquals(8, c.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void addDays_excludeWeekendsButAllowSpecificDay() {
        // 2025-07-04 Fri → +1 days, allow Sat → 2025-07-05 (Sat)
        Date result = DateAndTimeUtils.addDays(date(2025, 7, 4), 1, true,
                List.of(String.valueOf(Calendar.SATURDAY)));
        Calendar c = Calendar.getInstance();
        c.setTime(result);
        assertEquals(5, c.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void dateAsString_formatsExpectedPattern() {
        String s = DateAndTimeUtils.dateAsString(date(2025, 6, 15));
        assertTrue(s.contains("6/15/2025"));
    }

    @Test
    void differenceInDaysFromNowAsString_format() {
        String s = DateAndTimeUtils.differenceInDaysFromNowAsString(new Date());
        assertTrue(s.contains("day(s)"));
    }

    @Test
    void differenceAsString_negativeWhenFirstBefore() {
        String s = DateAndTimeUtils.differenceAsString(date(2025, 1, 1), date(2025, 1, 11), Unit.DAY);
        assertEquals("-10 day(s)", s);
    }

    @Test
    void differenceAsString_positiveWhenSecondBefore() {
        String s = DateAndTimeUtils.differenceAsString(date(2025, 1, 11), date(2025, 1, 1), Unit.DAY);
        assertEquals("10 day(s)", s);
    }

    @Test
    void getDateFromParticularDayFromCurrentDate_format() {
        String s = DateAndTimeUtils.getDateFromParticularDayFromCurrentDate(0);
        assertNotNull(s);
        assertTrue(s.matches("\\d+/\\d+/\\d+"));
    }

    @Test
    void getDateInddMMYYYY_formatsAndHandlesNull() {
        assertEquals("15/06/2025", DateAndTimeUtils.getDateInddMMYYYY(date(2025, 6, 15)));
        assertEquals("", DateAndTimeUtils.getDateInddMMYYYY(null));
    }

    @Test
    void getFormattedDate_customFormat() {
        assertEquals("2025-06-15", DateAndTimeUtils.getFormattedDate(date(2025, 6, 15), "yyyy-MM-dd"));
        assertEquals("", DateAndTimeUtils.getFormattedDate(null, "yyyy-MM-dd"));
    }

    @Test
    void getDaysAddedDate_addAndSubtract() {
        Date plus = DateAndTimeUtils.getDaysAddedDate(date(2025, 6, 15), 5);
        Date minus = DateAndTimeUtils.getDaysAddedDate(date(2025, 6, 15), -5);
        Calendar pc = Calendar.getInstance(); pc.setTime(plus);
        Calendar mc = Calendar.getInstance(); mc.setTime(minus);
        assertEquals(20, pc.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, mc.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void unit_enumValues_present() {
        assertNotNull(Unit.valueOf("DAY"));
        assertNotNull(Unit.valueOf("MILLISECOND"));
        assertEquals(7, Unit.values().length);
    }

    @Test
    void dateValidationType_enumValues() {
        assertEquals(3, DateValidationType.values().length);
        assertNotNull(DateValidationType.valueOf("INCLUSIVE"));
        assertNotNull(DateValidationType.valueOf("EXCLUSIVE"));
        assertNotNull(DateValidationType.valueOf("EXCLUSIVE_END"));
    }
}
