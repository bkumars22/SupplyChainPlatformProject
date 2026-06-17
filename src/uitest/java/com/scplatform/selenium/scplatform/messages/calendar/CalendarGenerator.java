/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.calendar;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.scplatform.qa.iris.model.exceptions.FieldNotFoundException;
import com.scplatform.qa.iris.model.exceptions.InvalidValueException;
import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.utilities.MessageIO;

/**
 * This class is used by {@link CalendarBuilder}
 * 
 * @author dgenrich
 *
 */
public class CalendarGenerator {

	protected List<Calendar> calendarData;
	
	public CalendarGenerator()	{
		calendarData = new ArrayList<Calendar>();
	}
	
	public List<Calendar> build(DateTime startDate, CalendarType calendarType)	{
		parseCalendarType(calendarType);
		
		// we have 4 quarters in the year, 3 months each quarter, then 4 or 5 weeks each month (based on calendarType)
		int year = startDate.getYear();
		DateTime workingDate = startDate;
		int endWeek = 6;		// add 6 days to date
		int nextWeek = 7;		// add 7 days to date
		
		Calendar calendar = Calendar.Factory.newInstance();
		calendar.setName("FY" + year);
		if (checkIfCalendarExists(calendar.getName()))	{
			return calendarData;
		}
		calendar.setCalendarType(calendarTypeStr);
		calendar.setStartDate(startDate);
		
		do	{
			int weekCounter = 0;
			int monthCounter = 0;
			
			for (int quarter = 0; quarter < 4; quarter++){				
				calendar.setQuarter_Name("FY" + year + "FQ" + pad(quarter+1));
				calendar.setQuarter_StartDate(workingDate);
				calendar.setQuarter_FiscalQuarter(quarter+1);
				
				for (int month = 0; month < 3; month++){
					calendar.setMonth_Name(calendar.getQuarter_Name() + "FM" + pad(monthCounter+1));
					calendar.setMonth_FiscalMonth(monthCounter + 1);
					calendar.setMonth_StartDate(workingDate);

					int weeksInMonth = getWeeksInMonth(month);
					
					for (int week = 0; week < weeksInMonth; week++){

						weekCounter++;
						
						calendar.setWeek_Name(calendar.getMonth_Name()  + "FW" + weekCounter);
						
						calendar.setWeek_FiscalWeek(weekCounter);
						calendar.setWeek_StartDate(workingDate);
						
						calendar.setWeek_EndDate(workingDate.plusDays(endWeek));

						
						// add data to dataSet
						calendarData.add(calendar);
						
						try {
							calendar = Calendar.Factory.clone(calendar);
						} catch (FieldNotFoundException | InvalidValueException e) {
							JLog.fail(e);
						}
						
						// move to next week
						workingDate = workingDate.plusDays(nextWeek);
						
					} // end week
					
					monthCounter++;
					
					setMonthEnd (calendar.getMonth_Name(), calendar.getWeek_EndDate());
				} // end month
				
				setQuarterEnd(calendar.getQuarter_Name(), calendar.getWeek_EndDate());
			} // end quarter
			year++;
		} while (year <= startDate.getYear());
		
		setDescription();
		
		saveCalendar(calendarData.get(0).getName());
		return calendarData;
	}
	
	

	protected boolean checkIfCalendarExists(String name) {
		MessageIO<Calendar> calendarMessageIO = new MessageIO<Calendar>(Calendar.class);
		boolean exists = calendarMessageIO.doesMessageExist(name);
		
		if (exists)	{
			calendarData = calendarMessageIO.load(name);
		}

		exists = (calendarData.isEmpty()) ? false : true;
		return exists;
	}

	protected void saveCalendar(String name) {
		MessageIO<Calendar> calendarMessageIO = new MessageIO<Calendar>(Calendar.class);
		calendarMessageIO.save(calendarData, name);
	}
	


	protected String calendarTypeStr;
	protected int weeksForMonth1 = 5;
	protected int weeksForMonth2 = 4;
	protected int weeksForMonth3 = 4;
	protected void parseCalendarType(CalendarType calendarType)	{
		switch (calendarType)	{
		case Type_445:
			weeksForMonth1 = 4;
			weeksForMonth2 = 4;
			weeksForMonth3 = 5;
			calendarTypeStr = "445";
			break;
		case Type_454:
			weeksForMonth1 = 4;
			weeksForMonth2 = 5;
			weeksForMonth3 = 4;
			calendarTypeStr = "454";
			break;
		case Type_544:
			weeksForMonth1 = 5;
			weeksForMonth2 = 4;
			weeksForMonth3 = 4;
			calendarTypeStr = "544";
			break;
		default:
			JLog.error("Unknown Calendar Type: " + calendarType.name());
		}
	}
	
	protected int getWeeksInMonth (int month)	{
		if (month == 0)	{
			return weeksForMonth1;
		} else if (month == 1){
			return weeksForMonth2;
		} else {
			return weeksForMonth3;
		}
		
	}
	
	protected String pad(int value){
		String padValue = "0" + value;
		int begin = padValue.length() - 2;
		
		return padValue.substring(begin);
	}
	
	protected void setMonthEnd (String monthName, DateTime monthEndDate){
		for (int row = 0; row < this.calendarData.size(); row++)	{
			if (this.calendarData.get(row).getMonth_Name().equals(monthName))	{
				this.calendarData.get(row).setMonth_EndDate(monthEndDate);
			}
		}
	}
	
	protected void setQuarterEnd (String quarterName, DateTime quarterEndDate){
		for (int row = 0; row < this.calendarData.size(); row++)	{
			if (this.calendarData.get(row).getQuarter_Name().equals(quarterName))	{
				this.calendarData.get(row).setQuarter_EndDate(quarterEndDate);
			}
		}
	}
	
	protected void setDescription()	{
		DateTime startDate = this.calendarData.get(0).getWeek_StartDate();
		DateTime endDate = this.calendarData.get(this.calendarData.size()-1).getWeek_EndDate();
		String description = "Calendar Type " + this.calendarData.get(0).getCalendarType() + 
				" starting on " + startDate.toString("E yyyy-MM-dd") + 
				" and ending on " + endDate.toString("E yyyy-MM-dd");
		
		for (int row = 0; row < this.calendarData.size(); row++)	{
			this.calendarData.get(row).setDescription(description);;
		}
		
	}
	
}
