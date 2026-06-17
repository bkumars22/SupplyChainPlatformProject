/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.currentForecast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;
import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.FileHelper;
import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.calendar.CalendarUtils;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.messages.forecast.ForecastBuilder;
import com.test.selenium.scplatform.messages.utilities.ExcelWriter;
import com.google.common.collect.Lists;

/**
 * @author dgenrich
 *
 * @param <T>	Forecast or any class that extends it
 * 
 * @see MessageWriter
 */
public class CurrentForecastWriter<T extends Forecast> extends MessageWriter<T> {

	private ExcelWriter excel;
	private List<T> messageData;
	private List<Calendar> calendarData;
	
	/**
	 * Writes the Adjustable Forecast as a XLS or XLSX document
	 * 
	 * @param messageClazz	
	 * 		The Forecast Message class, typically Forecast.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link ForecastBuilder}
	 * @param calendar
	 * 		The {@link Calendar} data
	 */
	public CurrentForecastWriter(Class<T> messageClazz, Iterable<T> messageLines, List<Calendar> calendar) {
		super(messageClazz, messageLines);
		messageData = Lists.newArrayList(messageLines);
		this.calendarData = calendar;
	}
	
	@Override
	public String generate() throws IOException	{
		String file = (currentUploadType.equals(UPLOAD_TYPE.XLSX)) ? "CurrentForecast.xlsx" : "CurrentForecast.xls";
		File template = FileHelper.getResourceFile(getClass(), file);

		excel = new ExcelWriter ();
		ExcelWriter.setCreateXLSX(currentUploadType.equals(UPLOAD_TYPE.XLSX));
		excel.modifyExisting(template, new File(saveToFile), "Current Forecast");

		createFiscalPeriods(messageData);

		for (int row = 0; row < messageData.size(); row++)	{
			createForecastRow(messageData.get(row));
		}
		excel.closeExcel();

		return saveToFile;
	}
	
	
	private HashMap<String, String> fiscalPeriodData;
    private int excelRow = 0;
	private void createFiscalPeriods(List<T> data) {
		// For FiscalPeriods, there are
		// 11 PastFiscalPeriod buckets
		//  1 CurrentFiscalPeriod bucket
		// 12 FutureFiscalPeriod buckets
		
		fiscalPeriodData = getFiscalPeriods(data);
		List<String> fiscalPeriodElements = getCurrentFiscalPeriods();
		List<String> fields = getCurrentElements();
		
		excelRow = 0;
		for (int index = 0; index < fiscalPeriodElements.size(); index++)	{
			int col = fields.indexOf(fiscalPeriodElements.get(index));
			String periodName = fiscalPeriodData.get(fiscalPeriodElements.get(index));
			Calendar calendar = getCalendarDate(periodName);
			if (calendar != null){
				String header = periodName + "\n" + calendar.getMonth_StartDate().toString("MMM dd");
				excel.writeStringWithWrap(header, col, excelRow);
			}
		}
		
	}
	
		
    private void createForecastRow(T data) {
    	excelRow++;
    	int col = 0;
    	
    	excel.writeString("ACTUALFORECAST", col++, excelRow);
    	excel.writeString(data.getForecastType(), col++, excelRow);
    	excel.writeString(data.getBusinessEntity(), col++, excelRow);
    	excel.writeString(data.getBusinessEntityType(), col++, excelRow);
    	excel.writeString(data.getPRODUCTIONResponsibility(), col++, excelRow);
    	excel.writeString(data.getSERVICEResponsibility(), col++, excelRow);
    	excel.writeDate(DateTime.now().withTimeAtStartOfDay(), col++, excelRow);
    	excel.writeString(data.getLastChangeBy(), col++, excelRow);	// LastChangeBy
    	excel.writeString("", col++, excelRow); // Commodity - for validation, this is pulled in from Item data
    	excel.writeString("", col++, excelRow); // CFG
    	excel.writeString(data.getVerification_Status(), col++, excelRow);	// state
    	excel.writeString(data.getItemIdentifier(), col++, excelRow);
    	excel.writeString(data.getDescription(), col++, excelRow);
    	excel.writeString(data.getSite(), col++, excelRow);
    	
    	if (data.getRemainingRolloverPeriods() == NullValue.INTEGER)	{
    		excel.writeString("", col++, excelRow); // ExtendedForecastTerm
    	} else	{
    		excel.writeInterger(data.getRemainingRolloverPeriods(), col++, excelRow);	// ExtendedForecastTerm
    	}
    	 		

    	List<String> fields = getCurrentElements();    	
    	for (int row = 0; row < data.getPointInTime().size(); row++)	{
    		Calendar calendar = null;
    		if (data.getPointInTime().get(row).getPeriod() != null){
    			calendar = getCalendarDate(data.getPointInTime().get(row).getPeriod());
    		} else	{
    			calendar = getCalendarDate(data.getPointInTime().get(row).getStartDate());
    		}
    		
    		String key = findFiscalPeriodKey (calendar.getMonth_Name());
    		if (key == null)	{
    			continue;
    		}
    		
    		int excelCol = fields.indexOf(key);
    		excel.writeFloat(data.getPointInTime().get(row).getPitValue(), excelCol, excelRow);
    		
    	}		
	}

    
	private HashMap<String, String> getFiscalPeriods (List<T> data){
		// For FiscalPeriods, there are
		// 11 PastFiscalPeriod buckets
		//  1 CurrentFiscalPeriod bucket
		// 12 FutureFiscalPeriod buckets
		
		HashMap<String, String> fiscalPeriods = new HashMap<String, String>();
		
		List<DateTime> pitDates = getPITDates (data);
		
		// Find the Current Fiscal Period
		int indexOfCurrentDate = getCurrentDateIndex (pitDates);
		Calendar currentCalendar = getCalendarDate(pitDates.get(indexOfCurrentDate));
		
		fiscalPeriods.put("CurrentFiscalPeriod", currentCalendar.getMonth_Name());
		
		// Add in the Future Fiscal Periods.  
		Calendar lastUsedCalendar = null;
		int furtureIndex = indexOfCurrentDate + 1;
		for (int index = 1; index <= 12; index++)	{
			Calendar calendar = null;
			if (furtureIndex < pitDates.size()){
				calendar = getCalendarDate(pitDates.get(furtureIndex));
				furtureIndex++;
			} else	{
				calendar = getNextMonth(lastUsedCalendar);
			}
			
			lastUsedCalendar = calendar;
			
			if (calendar == null){
				fiscalPeriods.put("FutureFiscalPeriod" + index, "");
			} else	{
				fiscalPeriods.put("FutureFiscalPeriod" + index, calendar.getMonth_Name());
			}
			
		}
		
		// 2014-11-25 dgenrich - seems that past fiscal periods are no longer allowed on the CurrentForecast message?
		// Add in the Past Fiscal Periods.  
		lastUsedCalendar = null;
		int pastIndex = indexOfCurrentDate - 1;
		for (int index = 11; index > 0; index--)	{
			Calendar calendar = null;
			if (pastIndex >= 0){
				calendar = getCalendarDate(pitDates.get(pastIndex));
				pastIndex--;
			} else	{
				calendar = getPreviousMonth(lastUsedCalendar);
			}
			
			lastUsedCalendar = calendar;
			
			if (calendar == null){
				fiscalPeriods.put("PastFiscalPeriod" + index, "");
			} else	{
				fiscalPeriods.put("PastFiscalPeriod" + index, calendar.getMonth_Name());
			}
			
		}
		
		return fiscalPeriods;
	}
	
	protected String findFiscalPeriodKey(String period) {
		if (!fiscalPeriodData.containsValue(period)){
			return null;
		}
		
		for (String key : fiscalPeriodData.keySet())	{
			if (period.equals(fiscalPeriodData.get(key)))	{
				return key;
			}
		}
		
		return null;
	}
	
	protected List<String> getCurrentFiscalPeriods ()	{
		List<String> elemementList = new ArrayList<String> ();
		
		elemementList.add("PastFiscalPeriod1");
		elemementList.add("PastFiscalPeriod2");
		elemementList.add("PastFiscalPeriod3");
		elemementList.add("PastFiscalPeriod4");
		elemementList.add("PastFiscalPeriod5");
		elemementList.add("PastFiscalPeriod6");
		elemementList.add("PastFiscalPeriod7");
		elemementList.add("PastFiscalPeriod8");
		elemementList.add("PastFiscalPeriod9");
		elemementList.add("PastFiscalPeriod10");
		elemementList.add("PastFiscalPeriod11");
		elemementList.add("CurrentFiscalPeriod");
		elemementList.add("FutureFiscalPeriod1");
		elemementList.add("FutureFiscalPeriod2");
		elemementList.add("FutureFiscalPeriod3");
		elemementList.add("FutureFiscalPeriod4");
		elemementList.add("FutureFiscalPeriod5");
		elemementList.add("FutureFiscalPeriod6");
		elemementList.add("FutureFiscalPeriod7");
		elemementList.add("FutureFiscalPeriod8");
		elemementList.add("FutureFiscalPeriod9");
		elemementList.add("FutureFiscalPeriod10");
		elemementList.add("FutureFiscalPeriod11");
		elemementList.add("FutureFiscalPeriod12");
		
		return elemementList;
	}
	
	public List<String> getCurrentElements ()	{
		List<String> elemementList = new ArrayList<String> ();
		
		elemementList.add("DataMeasure");
		elemementList.add("ForecastType");
		elemementList.add("BusinessEntity");
		elemementList.add("BusinessEntityType");
		elemementList.add("PRODUCTIONResponsibility");
		elemementList.add("SERVICEResponsibility");
		elemementList.add("LastChangedOn");
		elemementList.add("LastChangeBy");
		elemementList.add("Commodity");
		elemementList.add("CFG");
		elemementList.add("State");
		elemementList.add("ItemIdentifier");
		elemementList.add("ItemDescription");
		elemementList.add("Site");
		elemementList.add("ExtendedForecastTerm");
		elemementList.addAll(getCurrentFiscalPeriods());
		
		return elemementList;
	}
	
	private int getCurrentDateIndex(List<DateTime> pitDates) {
		int index = -1;
		
		DateTime currentDate = DateTime.now().withTimeAtStartOfDay();
		DateTime currentPIT;
		DateTime nextPIT;
		int total = pitDates.size() - 1;
		
		for (int row = 0; row <= total; row++)	{
			if (row == total)	{
				// at the end, so this must be the index since nothing else found
				index = row;
				break;
			}
			
			currentPIT = pitDates.get(row);
			nextPIT = pitDates.get(row+1);
			
			if ( ((currentDate.equals(currentPIT)) || (currentDate.isAfter(currentPIT))) &&
					(currentDate.isBefore(nextPIT)) )	{
				
				index = row;
				break;
			}
			
		}
		
		return index;
	}
	
	private Calendar getNextMonth (Calendar calendar){
		CalendarUtils utils = new CalendarUtils();
		return utils.getCalendarNextMonth(this.calendarData, calendar);
	}
	
	private List<DateTime> getPITDates(List<T> data) {
		List<DateTime> pitDates = new ArrayList<DateTime>();
		
		for (int row = 0; row < data.size(); row++)	{
			for (int index = 0; index < data.get(row).getPointInTime().size(); index++)	{
				if (!pitDates.contains(data.get(row).getPointInTime().get(index).getStartDate()))		{
					pitDates.add(data.get(row).getPointInTime().get(index).getStartDate());
				}
			}
		}
		
		Collections.sort(pitDates);
		
		return pitDates;
	}
	
	protected Calendar getPreviousMonth (Calendar calendar){
		CalendarUtils utils = new CalendarUtils();
		return utils.getCalendarPreviousMonth(this.calendarData, calendar);
	}
	
	private Calendar getCalendarDate (DateTime date)	{
		CalendarUtils utils = new CalendarUtils();
		return utils.findByDate(this.calendarData, date);
	}
	
	private Calendar getCalendarDate (String periodName)	{
		CalendarUtils utils = new CalendarUtils();
		return utils.findByPeriodName(this.calendarData, periodName);
	}
	
}
