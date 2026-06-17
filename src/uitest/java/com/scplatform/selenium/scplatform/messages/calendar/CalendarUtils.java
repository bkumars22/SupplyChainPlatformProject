/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.calendar;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;

public class CalendarUtils {

	public Calendar findByDate(Iterable<Calendar> calendar, DateTime date)	{
		Calendar findCalendar = null;
		
		if (date == null)	{
			JLog.error("CalendarUtils.findByDate() - the date to find is null!", TakeScreenshot.True);
			return null;
		}
		
		for (Calendar data : calendar)	{
			if (
					date.equals(data.getWeek_StartDate()) ||
					date.equals(data.getWeek_EndDate()) ||
					( (date.isAfter(data.getWeek_StartDate())) && (date.isBefore(data.getWeek_EndDate())) )
					)	{
				
				findCalendar = data;
				break;
			}
		}
		return findCalendar;
	}
	
	public Calendar findByPeriodName(Iterable<Calendar> calendar, String periodName)	{
		Calendar findCalendar = null;
		
		if (periodName == null)	{
			return findCalendar;
		}
		
		for (Calendar data : calendar)	{
			if (periodName.equals(data.getName()))	{
				findCalendar = data;
				break;
			} else if (periodName.equals(data.getQuarter_Name()))	{
				findCalendar = data;
				break;
			} else if (periodName.equals(data.getMonth_Name()))	{
				findCalendar = data;
				break;
			} else if (periodName.equals(data.getWeek_Name()))	{
				findCalendar = data;
				break;
			}
		}
		return findCalendar;
	}
	
	
	public Calendar getCalendarNextMonth(List<Calendar> calendarList, Calendar currentCalendar)	{
		Calendar calenderNextMonth = null;
		int total = calendarList.size() - 1;
		int nextFiscalMonth = -1;
		
		int index = -1;			
		// first, find the index of the currentCalendar
		for (int row = 0; row <= total; row++)	{
			if (currentCalendar.getMonth_StartDate().equals(calendarList.get(row).getMonth_StartDate()))	{
				nextFiscalMonth = calendarList.get(row).getMonth_FiscalMonth() + 1;
				if (nextFiscalMonth == 13)	{
					nextFiscalMonth = 1;
				}
				index = row;
				
				break;
			}
		}
		
		// next, find the nextFiscalMonth, starting at index+1
		for (int x = index+1; x <= total; x++)	{
			if (calendarList.get(x).getMonth_FiscalMonth() == nextFiscalMonth)	{
				calenderNextMonth = calendarList.get(x);
				break;
			}
		}
		
		return calenderNextMonth;
	}
	
	public Calendar getCalendarPreviousMonth(List<Calendar> calendarList, Calendar currentCalendar)	{
		Calendar calenderNextMonth = null;
		int total = calendarList.size() - 1;
		int previousFiscalMonth = -1;
		
		int index = -1;			
		// first, find the index of the currentCalendar
		for (int row = 0; row <= total; row++)	{
			try	{
				if (currentCalendar.getMonth_StartDate().equals(calendarList.get(row).getMonth_StartDate()))	{
					previousFiscalMonth = calendarList.get(row).getMonth_FiscalMonth() - 1;
					if (previousFiscalMonth == 0)	{
						previousFiscalMonth = 12;
					}
					index = row;
					
					break;
				}
			} catch (NullPointerException e){
				// ignore
			}

		}
		
		// next, find the nextFiscalMonth, starting at index+1
		for (int x = index+1; x <= total; x++)	{
			if (calendarList.get(x).getMonth_FiscalMonth() == previousFiscalMonth)	{
				calenderNextMonth = calendarList.get(x);
				break;
			}
		}
		
		return calenderNextMonth;
	}
	
	
	public int findIndexForDate(List<Calendar> calendar, DateTime date)	{
		int index = -1;
			
		for (int row = 0; row < calendar.size(); row++)	{
			if (
					date.equals(calendar.get(row).getWeek_StartDate()) ||
					date.equals(calendar.get(row).getWeek_EndDate()) ||
					( (date.isAfter(calendar.get(row).getWeek_StartDate())) && (date.isBefore(calendar.get(row).getWeek_EndDate())) )
					)	{
				
				index = row;
				break;
			}
		}
		
		return index;
	}
	
	public List<Calendar> getCalendarForPeriod(List<Calendar> calendar, String period)	{
		List<Calendar> newCalendar = new ArrayList<Calendar>();

		for (int i = 0; i < calendar.size(); i++){
			if ("Week".equals(period))	{
				newCalendar.add(calendar.get(i));
			} else if ("Month".equals(period))	{
				newCalendar.add(calendar.get(i));
			} else if ("Quarter".equals(period))	{
				newCalendar.add(calendar.get(i));
			} else	{
				JLog.error("Unkown period name: " + period + "; Valid values are (Week|Month|Quarter)");
				newCalendar.addAll(calendar);
				break;
			}

		}
		
		return newCalendar;
	}
}
