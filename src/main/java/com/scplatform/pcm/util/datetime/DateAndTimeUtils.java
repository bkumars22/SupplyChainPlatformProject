/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */package com.scplatform.pcm.util.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Based on work by Aaron Gadberry
 *
 */
public class DateAndTimeUtils {

	/**
	 * Unit is utilized to distinguish the valid types of units that can be utilized
	 * by a series of difference methods within this class.
	 */
	public enum Unit {
		/**
		 * Represents a unit of time defined by Calendar.DAY_OF_MONTH
		 */
		DAY(Calendar.DAY_OF_MONTH, 1000l * 60 * 60 * 24),
		/**
		 * Represents a unit of time defined by Calendar.HOUR_OF_DAY
		 */
		HOUR(Calendar.HOUR_OF_DAY, 1000l * 60 * 60),
		/**
		 * Represents a unit of time defined by Calendar.MILLISECOND
		 */
		MILLISECOND(Calendar.MILLISECOND, 1),
		/**
		 * Represents a unit of time defined by Calendar.MINUTE
		 */
		MINUTE(Calendar.MINUTE, 1000l * 60),
		/**
		 * Represents a unit of time defined by Calendar.MONTH
		 */
		MONTH(Calendar.MONTH, 1000l * 60 * 60 * 24 * 30),
		/**
		 * Represents a unit of time defined by Calendar.SECOND
		 */
		SECOND(Calendar.SECOND, 1000l),
		/**
		 * Represents a unit of time defined by Calendar.YEAR
		 */
		YEAR(Calendar.YEAR, 1000l * 60 * 60 * 24 * 365);
		private final int calendarUnit;

		private final long estimate;

		Unit(int calendarUnit, long estimate) {
			this.calendarUnit = calendarUnit;
			this.estimate = estimate;
		}
	}

	public enum DateValidationType {
		INCLUSIVE, EXCLUSIVE, EXCLUSIVE_END
	}

	static Calendar MAX_DATE = null;
	static {
		MAX_DATE = Calendar.getInstance();
		MAX_DATE.set(9999, 12, 31, 23, 59, 59);
	}

	public static Date defaultToMax(Date date) {
		return (date == null) ? MAX_DATE.getTime() : date;
	}

	public static Date getCurrentDateOnly() {
		return dateOnly(new Date());
	}

    public static Date dateOnly (Date date) {
      if (date instanceof java.sql.Date) {
        date = new Date(date.getTime());
      }
      LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
      ldt = ldt.withHour(0).withMinute(0).withSecond(0).withNano(0);
      return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

	public static boolean before(Date thisDate, Date thatDate, boolean inclusive) {
		thisDate = dateOnly(defaultToMax(thisDate));
		thatDate = dateOnly(defaultToMax(thatDate));
		if (inclusive) {
			return (thisDate.getTime() <= thatDate.getTime());
		} else {
			return (thisDate.getTime() < thatDate.getTime());
		}
	}

	public static boolean after(Date thisDate, Date thatDate, boolean inclusive) {
		thisDate = dateOnly(defaultToMax(thisDate));
		thatDate = dateOnly(defaultToMax(thatDate));
		if (inclusive) {
			return (thisDate.getTime() >= thatDate.getTime());
		} else {
			return (thisDate.getTime() > thatDate.getTime());
		}

	}

	public static boolean same(Date thisDate, Date thatDate) {
		thisDate = dateOnly(defaultToMax(thisDate));
		thatDate = dateOnly(defaultToMax(thatDate));
		return (thisDate.getTime() == thatDate.getTime());
	}

	/**
	 * Tests for an inclusive or exclusive overlap.
	 */
	public static boolean overlap(Date periodOneStart, Date periodOneEnd, Date periodTwoStart, Date periodTwoEnd) {
		return overlap(periodOneStart, periodOneEnd, periodTwoStart, periodTwoEnd, DateValidationType.INCLUSIVE);
	}

	public static boolean overlap(Date periodOneStart, Date periodOneEnd, Date periodTwoStart, Date periodTwoEnd,
			DateValidationType dateValidationType) {
		if (dateValidationType.equals(DateValidationType.INCLUSIVE)) {
			return overlapInclusive(periodOneStart, periodOneEnd, periodTwoStart, periodTwoEnd);
		} else if (dateValidationType.equals(DateValidationType.EXCLUSIVE)) {
			return overlapExclusive(periodOneStart, periodOneEnd, periodTwoStart, periodTwoEnd);
		} else if (dateValidationType.equals(DateValidationType.EXCLUSIVE_END)) {
			return overlapExclusiveEnd(periodOneStart, periodOneEnd, periodTwoStart, periodTwoEnd);
		}

		return false;
	}

	private static boolean overlapInclusive(Date periodOneStart, Date periodOneEnd, Date periodTwoStart,
			Date periodTwoEnd) {
		periodOneStart = dateOnly(defaultToMax(periodOneStart));
		periodOneEnd = dateOnly(defaultToMax(periodOneEnd));
		periodTwoStart = dateOnly(defaultToMax(periodTwoStart));
		periodTwoEnd = dateOnly(defaultToMax(periodTwoEnd));

		// Right overlap
		if (between(periodOneStart, periodTwoStart, periodTwoEnd)) {
			return true;
		}
		// Left overlap
		if (between(periodOneEnd, periodTwoStart, periodTwoEnd)) {
			return true;
		}
		// Complete overlap
		if (before(periodOneStart, periodTwoStart, true) && after(periodOneEnd, periodTwoEnd, true)) {
			return true;
		}
		return false;
	}

	private static boolean overlapExclusive(Date periodOneStart, Date periodOneEnd, Date periodTwoStart,
			Date periodTwoEnd) {
		periodOneStart = dateOnly(defaultToMax(periodOneStart));
		periodOneEnd = dateOnly(defaultToMax(periodOneEnd));
		periodTwoStart = dateOnly(defaultToMax(periodTwoStart));
		periodTwoEnd = dateOnly(defaultToMax(periodTwoEnd));

		// Right overlap
		if (after(periodOneStart, periodTwoStart, false) && before(periodOneStart, periodTwoEnd, false)) {
			return true;
		}
		// Left overlap
		if (after(periodOneEnd, periodTwoStart, false) && before(periodOneEnd, periodTwoEnd, false)) {
			return true;
		}
		// Complete overlap
		if (before(periodOneStart, periodTwoStart, true) && after(periodOneEnd, periodTwoEnd, true)) {
			return true;
		}
		return false;
	}

	private static boolean overlapExclusiveEnd(Date periodOneStart, Date periodOneEnd, Date periodTwoStart,
			Date periodTwoEnd) {
		periodOneStart = dateOnly(defaultToMax(periodOneStart));
		periodOneEnd = dateOnly(defaultToMax(periodOneEnd));
		periodTwoStart = dateOnly(defaultToMax(periodTwoStart));
		periodTwoEnd = dateOnly(defaultToMax(periodTwoEnd));

		// Right overlap
		if (after(periodOneStart, periodTwoStart, true) && before(periodOneStart, periodTwoEnd, false)) {
			return true;
		}
		// Left overlap
		if (after(periodOneEnd, periodTwoStart, false) && before(periodOneEnd, periodTwoEnd, true)) {
			return true;
		}
		// Complete overlap
		if (before(periodOneStart, periodTwoStart, true) && after(periodOneEnd, periodTwoEnd, true)) {
			return true;
		}
		return false;
	}

	/**
	 * Consider null date as evergreen. Compares only the date and the period is
	 * inclusive which means 1/1/09 is between 1/1/09 and 1/7/09
	 */
	public static boolean between(Date date, Date from, Date to) {
		date = dateOnly(defaultToMax(date));
		from = dateOnly(defaultToMax(from));
		to = dateOnly(defaultToMax(to));
		return (date.getTime() >= from.getTime() && date.getTime() <= to.getTime());
	}

	/**
	 * Consider null date as evergreen. Compares only the date and the period is
	 * inclusive which means 1/1/09 is between 1/1/09 and 1/7/09. Time is included
	 */
	public static boolean betweenWithTime(Date date, Date from, Date to) {
		date = defaultToMax(date);
		from = defaultToMax(from);
		to = defaultToMax(to);
		return (date.getTime() >= from.getTime() && date.getTime() <= to.getTime());
	}

	public static long diffInDays(Date first, Date second) {
		first = defaultToMax(first);
		second = defaultToMax(second);
		return (first.before(second)) ? difference(first, second, Unit.DAY) * -1 : difference(second, first, Unit.DAY);
	}

	/**
	 * Add a long amount to a calendar. Similar to calendar.add() but accepts a long
	 * argument instead of limiting it to an int.
	 * 
	 * @param c
	 *            the calendar
	 * 
	 * @param unit
	 *            the unit to increment
	 * 
	 * @param increment
	 *            the amount to increment
	 */
	public static void add(Calendar c, int unit, long increment) {
		while (increment > Integer.MAX_VALUE) {
			c.add(unit, Integer.MAX_VALUE);
			increment -= Integer.MAX_VALUE;
		}
		c.add(unit, (int) increment);
	}

	/**
	 * Find the number of units passed between two {@link Calendar} objects.
	 * 
	 * @param c1
	 *            The first occurring {@link Calendar}
	 * 
	 * @param c2
	 *            The later occurring {@link Calendar}
	 * 
	 * @param unit
	 *            The unit to calculate the difference in
	 * 
	 * @return the number of units passed
	 */
	public static long difference(Calendar c1, Calendar c2, Unit unit) {
		Calendar first = (Calendar) c1.clone();
		Calendar last = (Calendar) c2.clone();
		long difference = c2.getTimeInMillis() - c1.getTimeInMillis();
		long increment = (long) Math.floor((double) difference / (double) unit.estimate);
		increment = Math.max(increment, 1);
		long total = 0;
		while (increment > 0) {
			add(first, unit.calendarUnit, increment);
			if (first.after(last)) {
				add(first, unit.calendarUnit, increment * -1);
				increment = (long) Math.floor(increment / 2);
			} else {
				total += increment;
			}
		}
		return total;
	}

	/**
	 * Find the number of units passed between two {@link Date} objects.
	 * 
	 * @param d1
	 *            The first occurring {@link Date}
	 * 
	 * @param d2
	 *            The later occurring {@link Date}
	 * 
	 * @param unit
	 *            The unit to calculate the difference in
	 * 
	 * @return the number of units passed
	 */
	public static long difference(Date d1, Date d2, Unit unit) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		return difference(c1, c2, unit);
	}

	/**
	 * Find the number of units, including a fraction, passed between two
	 * {@link Calendar} objects.
	 * 
	 * @param c1
	 *            The first occurring {@link Calendar}
	 * 
	 * @param c2
	 *            The later occurring {@link Calendar}
	 * 
	 * @param unit
	 *            The unit to calculate the difference in
	 * 
	 * @return the number of units passed
	 * @throws Exception 
	 */
	public static double exactDifference(Calendar c1, Calendar c2, Unit unit) throws Exception {
		long unitDifference = difference(c1, c2, unit);
		Calendar mid = (Calendar) c1.clone();
		DateAndTimeUtils.add(mid, unit.calendarUnit, unitDifference);
		Calendar end = (Calendar) mid.clone();
		end.add(unit.calendarUnit, 1);
		long millisPassed = DateAndTimeUtils.difference(mid, c2, Unit.MILLISECOND);
		long millisTotal = DateAndTimeUtils.difference(mid, end, Unit.MILLISECOND);
		if (millisTotal == 0) {
			throw new Exception("Division by zero not possible");
		}
		double remainder = (double) millisPassed / (double) millisTotal;
		return unitDifference + remainder;
	}

	/**
	 * Find the number of units passed between two {@link Calendar} objects in all
	 * units. This would return a result like 1 year, 2 months and 3 days.
	 * 
	 * This method assumes you want the difference broken down in all available
	 * units.S
	 * 
	 * @param c1
	 *            The first occurring {@link Calendar}
	 * 
	 * @param c2
	 *            The later occurring {@link Calendar}
	 * 
	 * @return the number of units passed without overlap
	 */
	public static Map<Unit, Long> tieredDifference(Calendar c1, Calendar c2) {
		return tieredDifference(c1, c2, Arrays.asList(Unit.values()));
	}

	/**
	 * Find the number of units passed between two {@link Calendar} objects in all
	 * units. This would return a result like 1 year, 2 months and 3 days.
	 * 
	 * @param c1
	 *            The first occurring {@link Calendar}
	 * 
	 * @param c2
	 *            The later occurring {@link Calendar}
	 * 
	 * @param units
	 *            The list of units to calculate the difference in
	 * 
	 * @return the number of units passed without overlap
	 */
	public static Map<Unit, Long> tieredDifference(Calendar c1, Calendar c2, List<Unit> units) {
		Calendar first = (Calendar) c1.clone();
		Calendar last = (Calendar) c2.clone();
		Map<Unit, Long> differences = new HashMap<Unit, Long>();
		List<Unit> allUnits = new ArrayList<Unit>();
		allUnits.add(Unit.YEAR);
		allUnits.add(Unit.MONTH);
		allUnits.add(Unit.DAY);
		allUnits.add(Unit.HOUR);
		allUnits.add(Unit.MINUTE);
		allUnits.add(Unit.SECOND);
		allUnits.add(Unit.MILLISECOND);
		for (Unit unit : allUnits) {
			if (units.contains(unit)) {
				long difference = difference(first, last, unit);
				differences.put(unit, difference);
				DateAndTimeUtils.add(first, unit.calendarUnit, difference);
			}
		}
		return differences;
	}

	/**
	 * Returns a Date that is N days after the given date.
	 * 
	 * Example: if date is 7/6/10, and N=3 then result is 7/9/10 (since tue 7/7 is
	 * +1, wed 7/8 is +2 and thur 7/9 is +3).
	 * 
	 * Example: (excludeWeekends=true) if date is 7/1/10, and N=2 then result is
	 * 7/5/10 (since fri 7/2 is +1, sat 7/3 is excluded, sun 7/4 is excluded, and
	 * mon 7/5 is +2)
	 * 
	 * @param startDate
	 * @param days
	 *            -- number of days to add
	 * @param excludeWeekends
	 *            -- set to true to exclude saturday and sunday in the count
	 * @return the number of days that elapsed from startDate to endDate
	 */
	public static Date addDays(Date startDate, int days, boolean excludeWeekends, List<String> allowedDays) {

		List<Integer> weekend = Arrays.asList(Calendar.SUNDAY, Calendar.SATURDAY);
		int elapsedDays = 0;

		Calendar current = Calendar.getInstance();
		current.setTime(DateAndTimeUtils.dateOnly(startDate));

		while (elapsedDays < days) {

			current.add(Calendar.DATE, 1);

			elapsedDays++;

			// Check to see if we are on a weekend day and
			// the exclude weekend rule is set
			if (excludeWeekends && weekend.contains(current.get(Calendar.DAY_OF_WEEK))) {
				// Allow the day to be on a weekend if it is in the list of allowed days
				if (elapsedDays != days) {
					elapsedDays--;
				} else if (!allowedDays.contains(Integer.toString(current.get(Calendar.DAY_OF_WEEK)))) {
					elapsedDays--;
				}
			}
		}

		return current.getTime();
	}

	public static String dateAsString(Date dt) {
		DateFormat sdf = new SimpleDateFormat("M/d/yyyy EEE HH:mm:ss Z");
		return sdf.format(dt);
	}

	/**
	 * Return the difference between now and the date in days
	 * 
	 * @param d
	 * @return
	 */
	public static String differenceInDaysFromNowAsString(Date d) {
		return differenceAsString(d, new Date(), Unit.DAY);
	}

	/**
	 * Return the difference between two dates as a string.
	 * 
	 * NOTE: Mostly used by test classes for consistency in testing date based data
	 * 
	 * @param d1
	 * @param d2
	 * @param unit
	 * @return
	 */
	public static String differenceAsString(Date d1, Date d2, Unit unit) {
		long l = d1.before(d2) ? difference(d1, d2, unit) * -1 : difference(d2, d1, unit);
		return "" + l + ' ' + unit.name().toLowerCase() + "(s)";
	}

	 public static String getDateFromParticularDayFromCurrentDate(int days) {
		 Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -days);
			Date date = DateAndTimeUtils.dateOnly(cal.getTime());
			DateAndTimeUtils.dateAsString(date);
			return DateAndTimeUtils.dateAsString(date).split(" ")[0];
	}
	
	public static String getDateInddMMYYYY(Date strDate) {
		if (strDate != null) {
			SimpleDateFormat sdfDestination = new SimpleDateFormat("dd/MM/yyyy");
			return sdfDestination.format(strDate);
		}
		return "";
	}

	public static String getFormattedDate(Date dateValue, String format) {
		if (dateValue != null) {
			SimpleDateFormat sdfDestination = new SimpleDateFormat(format);
			return sdfDestination.format(dateValue);
		}
		return "";
	}

	/**
	 * Subtract or add the number of days provided to the given date negative number
	 * represents subtract
	 * 
	 * @param from
	 * @param days
	 * @return
	 */
	public static Date getDaysAddedDate(Date from, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(from);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}
}
