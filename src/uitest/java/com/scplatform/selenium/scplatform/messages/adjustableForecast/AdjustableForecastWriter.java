/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.adjustableForecast;

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
import com.test.selenium.common.JLog;
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
public class AdjustableForecastWriter<T extends Forecast> extends MessageWriter<T> {
	protected ExcelWriter excel;
	protected List<T> messageData;
	protected List<Calendar> calendarData;
	
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
	public AdjustableForecastWriter(Class<T> messageClazz, Iterable<T> messageLines, List<Calendar> calendar) {
		super(messageClazz, messageLines);
		messageData = Lists.newArrayList(messageLines);
		this.calendarData = calendar;
	}
	
	@Override
	public String generate() throws IOException	{
		String file = (currentUploadType.equals(UPLOAD_TYPE.XLSX)) ? "AdjustableForecast.xlsx" : "AdjustableForecast.xls";
		File template = FileHelper.getResourceFile(getClass(), file);

		excel = new ExcelWriter ();
		ExcelWriter.setCreateXLSX(currentUploadType.equals(UPLOAD_TYPE.XLSX));
		excel.modifyExisting(template, new File(saveToFile), "Adjustable Forecast");

		createFiscalPeriods(messageData);

		for (int row = 0; row < messageData.size(); row++)	{
			createForecastRow(messageData.get(row));
		}
		excel.closeExcel();

		return saveToFile;
	}

	protected HashMap<String, String> fiscalPeriodData;
    protected int excelRow = 0;
	protected void createFiscalPeriods(List<T> data) {
		// For FiscalPeriods, there are
		//  1 CurrentFiscalPeriod bucket
		//  5 FutureFiscalPeriod buckets
		// These are repeated for: Calculated, Adjustment, and Adjustable
		
		fiscalPeriodData = getFiscalPeriods(data);
		List<String> fiscalPeriodElements = getAdjustableFiscalPeriods();
		List<String> fields = getAdjustableElements();
		
		excelRow = 0;
		for (int index = 0; index < fiscalPeriodElements.size(); index++)	{
			int col = fields.indexOf(fiscalPeriodElements.get(index));
			String periodName = fiscalPeriodData.get(fiscalPeriodElements.get(index));
			Calendar calendar = getCalendarDate(periodName);
			String header = periodName + "\n" + calendar.getMonth_StartDate().toString("MMM dd");
			excel.writeStringWithWrap(header, col, excelRow);
		}
		
	}
	
		
    protected void createForecastRow(T data) {
    	excelRow++;
    	int col = 0;
    	
    	excel.writeString("ACTUALFORECAST", col++, excelRow);
    	excel.writeString(data.getForecastType(), col++, excelRow);
    	excel.writeString(data.getBusinessEntity(), col++, excelRow);
    	excel.writeString(data.getBusinessEntityType(), col++, excelRow);
    	excel.writeString(data.getPRODUCTIONResponsibility(), col++, excelRow);
    	excel.writeString(data.getSERVICEResponsibility(), col++, excelRow);
    	excel.writeDate(DateTime.now().withTimeAtStartOfDay(), col++, excelRow);
    	excel.writeString("", col++, excelRow);	// LastChangeBy
    	excel.writeString("", col++, excelRow); // Commodity
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
    	
    	if (data.getPeriodicAdjustmentType() == null)	{
    		excel.writeString("", col++, excelRow);
    	} else if (data.getPeriodicAdjustmentType().equals("Fixed"))	{
    		excel.writeString("$", col++, excelRow);
    	} else	{
    		excel.writeString("%", col++, excelRow);
    	}
    		

    	List<String> fields = getAdjustableElements();    	
    	for (int row = 0; row < data.getPointInTime().size(); row++)	{
    		Calendar calendar = null;
    		if (data.getPointInTime().get(row).getPeriod() != null){
    			calendar = getCalendarDate(data.getPointInTime().get(row).getPeriod());
    		} else	{
    			calendar = getCalendarDate(data.getPointInTime().get(row).getStartDate());
    		}
    		
    		List<String> keys = findFiscalPeriodKeys (calendar.getMonth_Name());
    		if ((keys==null) || (keys.isEmpty()))	{
    			continue;
    		}
    		
    		int subIndex = keys.get(0).indexOf("FiscalPeriod");
    		String subKey = keys.get(0).substring(subIndex);
    		String midKey = null;
    		if (keys.get(0).contains("Current"))	{
    			midKey = "Current";
    		} else	{
    			midKey = "Future";
    		}
    		
    		// Calculated...
    		int excelCol = fields.indexOf("Calculated" + midKey + subKey);
    		if (data.getPointInTime().get(row).getPitValue() != NullValue.FLOAT){
    			excel.writeFloat(data.getPointInTime().get(row).getPitValue(), excelCol, excelRow);
    		}
    		
       		
    		// Adjustment Amount or Adjustable Value
    		if (data.getPointInTime().get(row).getPeriodicAdjustmentValue() != NullValue.FLOAT){
        		if (data.getPeriodicAdjustmentType().equals("Fixed")) {
        			excelCol = fields.indexOf("Adjustable" + midKey + subKey);
        		} else	{
        			excelCol = fields.indexOf("Adjustment" + midKey + subKey);
        		}
        		
        		excel.writeFloat(data.getPointInTime().get(row).getPeriodicAdjustmentValue(), excelCol, excelRow);
    		}
    		
    		if (data.getPeriodicAdjustmentType().equals("Percent")) {
    			excelCol = fields.indexOf("Adjustable" + midKey + subKey);
    			excel.writeFloat(data.getPeriodicAdjustmentValue(), excelCol, excelRow);
    		}
    	}		
	}

    
	protected HashMap<String, String> getFiscalPeriods (List<T> data){
		// For FiscalPeriods, there are
		//  1 CurrentFiscalPeriod bucket
		//  5 FutureFiscalPeriod buckets
		// These are repeated for: Calculated, Adjustment, and Adjustable
		
		HashMap<String, String> fiscalPeriods = new HashMap<String, String>();
		
		List<DateTime> pitDates = getPITDates (data);
		
		// Find the Current Fiscal Period
		int indexOfCurrentDate = getCurrentDateIndex (pitDates);
		Calendar currentCalendar = getCalendarDate(pitDates.get(indexOfCurrentDate));
		
		fiscalPeriods.put("CalculatedCurrentFiscalPeriod", currentCalendar.getMonth_Name());
		fiscalPeriods.put("AdjustmentCurrentFiscalPeriod", currentCalendar.getMonth_Name());
		fiscalPeriods.put("AdjustableCurrentFiscalPeriod", currentCalendar.getMonth_Name());
		
		// Add in the Future Fiscal Periods.  
		Calendar lastUsedCalendar = null;
		int furtureIndex = indexOfCurrentDate + 1;
		for (int index = 1; index <= 5; index++)	{
			Calendar calendar = null;
			if (furtureIndex < pitDates.size()){
				calendar = getCalendarDate(pitDates.get(furtureIndex));
				furtureIndex++;
			} else	{
				calendar = getNextMonth(lastUsedCalendar);
			}
			
			lastUsedCalendar = calendar;
			
			if (calendar == null){
				fiscalPeriods.put("CalculatedFutureFiscalPeriod" + index, "");
				fiscalPeriods.put("AdjustmentFutureFiscalPeriod" + index, "");
				fiscalPeriods.put("AdjustableFutureFiscalPeriod" + index, "");
			} else	{
				fiscalPeriods.put("CalculatedFutureFiscalPeriod" + index, calendar.getMonth_Name());
				fiscalPeriods.put("AdjustmentFutureFiscalPeriod" + index, calendar.getMonth_Name());
				fiscalPeriods.put("AdjustableFutureFiscalPeriod" + index, calendar.getMonth_Name());
			}
			
		}
		
		return fiscalPeriods;
	}
	
    
	protected List<String> findFiscalPeriodKeys(String period) {
		if (!fiscalPeriodData.containsValue(period)){
			JLog.error(this.getClass().getSimpleName() + ".findFiscalPeriodKeys(): Unable to find period name (" + period + ") in fiscalPeriodData!");
			return null;
		}
		List<String> keys = new ArrayList<String>();
		
		for (String key : fiscalPeriodData.keySet())	{
			if (period.equals(fiscalPeriodData.get(key)))	{
				keys.add(key);
			}
		}
		
		if (keys.isEmpty()){
			JLog.error(this.getClass().getSimpleName() + ".findFiscalPeriodKeys(): Unable to find period name (" + period + ") in fiscalPeriodData after searching all keys!");
		}
		
		return keys;
	}
	
	
	protected int getCurrentDateIndex(List<DateTime> pitDates) {
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
	
	protected Calendar getNextMonth (Calendar calendar){
		CalendarUtils utils = new CalendarUtils();
		return utils.getCalendarNextMonth(this.calendarData, calendar);
	}
	
	protected List<DateTime> getPITDates(List<T> data) {
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
	
	
	/**
	 * @return List<String> of the Member Elements in the sequence as giving by the spec
	 */
	protected List<String> getAdjustableFiscalPeriods ()	{
		List<String> elemementList = new ArrayList<String> ();
		
		elemementList.add("CalculatedCurrentFiscalPeriod");
		elemementList.add("CalculatedFutureFiscalPeriod1");
		elemementList.add("CalculatedFutureFiscalPeriod2");
		elemementList.add("CalculatedFutureFiscalPeriod3");
		elemementList.add("CalculatedFutureFiscalPeriod4");
		elemementList.add("CalculatedFutureFiscalPeriod5");
		
		// Adjustment Amount
		elemementList.add("AdjustmentCurrentFiscalPeriod");
		elemementList.add("AdjustmentFutureFiscalPeriod1");
		elemementList.add("AdjustmentFutureFiscalPeriod2");
		elemementList.add("AdjustmentFutureFiscalPeriod3");
		elemementList.add("AdjustmentFutureFiscalPeriod4");
		elemementList.add("AdjustmentFutureFiscalPeriod5");
		
		// Adjustable Value
		elemementList.add("AdjustableCurrentFiscalPeriod");
		elemementList.add("AdjustableFutureFiscalPeriod1");
		elemementList.add("AdjustableFutureFiscalPeriod2");
		elemementList.add("AdjustableFutureFiscalPeriod3");
		elemementList.add("AdjustableFutureFiscalPeriod4");
		elemementList.add("AdjustableFutureFiscalPeriod5");
		
		return elemementList;
	}
	
	/**
	 * @return List<String> of the Member Elements in the sequence as giving by the spec
	 */
	protected List<String> getAdjustableElements ()	{
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
		elemementList.add("AdjustmentType");
		elemementList.addAll(getAdjustableFiscalPeriods());
		
		return elemementList;
	}
	
	protected Calendar getCalendarDate (DateTime date)	{
		CalendarUtils utils = new CalendarUtils();
		return utils.findByDate(this.calendarData, date);
	}
	
	protected Calendar getCalendarDate (String periodName)	{
		CalendarUtils utils = new CalendarUtils();
		return utils.findByPeriodName(this.calendarData, periodName);
	}
	
}
