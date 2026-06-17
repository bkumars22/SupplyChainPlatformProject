/**
 * @CalendarBuilder.java@
 *
 * Created on Mon Oct 20 12:15:33 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.calendar;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;

/**
 * Used to build default {@link Calendar} message data.  
 *
 * Default Data:
 * <UL>
 * <LI> startDayOfWeek = DateTimeConstants.SUNDAY;.  Change using {@link #withStartDayOfWeek(int)}
 * <LI> calendarType = {@link CalendarType#Type_544}.  Change using {@link #withCalendarType(CalendarType)}
 * <LI> startDate = start day of the year.  Change using {@link #withStartDate(DateTime)}
 * <LI> operationCode = C.  Change using {@link #withOperationCode(String)}
 * </UL>
 * <br><br>
 * Chained Call Example
 * <pre>
 * CalendarBuilder<Calendar> builder = 
 * 				new CalendarBuilder<Calendar>(Calendar.class, offsetFromCurrentYear);
 * Iterable<Calendar> data = builder.build();
 * </pre>
 * 
 *
 */
public class CalendarBuilder<T extends Calendar> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
	protected DateTime startYear;
	protected List<Calendar> calendarData;
	
    protected CalendarBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new CalendarEnricher());
    }

    /**
     * Builds a set of {@link Calendar} data.  This should be called once for each year to create.
     * 
     * @param messageClazz
     * 		The Calendar Message class, typically Calendar.class, but can be any class that extends it.
     * @param offsetFromCurrentYear
     * 		Start creating the calendar for currentYear+offsetFromCurrentYear
     * 
     */
	public CalendarBuilder(Class<T> messageClazz, int offsetFromCurrentYear) {
		this(messageClazz, defaultNumMessages);
		
		startYear = new DateTime(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + offsetFromCurrentYear, 1, 1, 0, 0);
	}
	
    @Override
    public Iterable<T> build() {
		CalendarGenerator generator = new CalendarGenerator();
		calendarData = generator.build(getStartDate(), calendarType);
    	
     	setNumMessages(calendarData.size());
        return super.build();
    }
    

    protected class CalendarEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line. 
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @SuppressWarnings("unchecked")
		@Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	messageLine = (T) calendarData.get((int) lineNumber);
        	messageLine.setOperationCode(operationCode);

            return messageLine;
        }
        
    }
    
   
	protected DateTime getStartDate()	{
		if (startDate == null){
			DateTime onTheFirstDayOfTheFirstWeek = startYear.withWeekOfWeekyear(1).withDayOfWeek(startDayOfWeek).withTimeAtStartOfDay();
			return onTheFirstDayOfTheFirstWeek;
		}
		return startDate;
	}
    
    
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
	protected int startDayOfWeek = DateTimeConstants.SUNDAY;
	protected CalendarType calendarType = CalendarType.Type_544;
	protected DateTime startDate = null;
	protected String operationCode = "C";
	 
	/**
	 * Set to the start date of the week.  
	 * <br>Default=DateTimeConstants.SUNDAY
	 */
	public CalendarBuilder<T> withStartDayOfWeek(int startDay) {
		startDayOfWeek = startDay;
		return this;
	}

	/**
	 * Set to the {@link CalendarType} to use.
	 * <br>Default={@link CalendarType#Type_544}
	 */
	public CalendarBuilder<T> withCalendarType(CalendarType type) {
		calendarType = type;
		return this;
	}

	/**
	 * Sets the start date of the calendar.  
	 * If building multiple calendars, this should be set to the week after the previous calendar
	 * <br>Default=Start day of the desire year
	 * <br>The default should only be used for the first calendar
	 * 
	 * @see #withStartDayOfWeek(int)
	 */
	public CalendarBuilder<T> withStartDate(DateTime date) {
		if (date != null){
			startDate = date;
		}
		return this;
	}
	
    
    /**
	 * Used to set {@link Calendar#setOperationCode(String)}<br>
	 * Default: C
     */
    public CalendarBuilder<T> withOperationCode(String value){
    	this.operationCode = value;
    	return this;
    }
    
    

}
