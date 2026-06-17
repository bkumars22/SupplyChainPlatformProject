/**
 * @ForecastBuilder.java@
 *
 * Created on Thu Oct 23 06:51:48 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.forecast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.forecast.subClasses.PointInTime;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;

/**
 *
 * Used to build default {@link Forecast} message data.  
 *
 * Default Data:
 * <UL>
 * <LI> totalFiscalPeriods = 24.  Change using {@link #withTotalFiscalPeriods(int)}
 * <LI> offsetFiscalPeriods = -11.  Change using {@link #withOffsetFiscalPeriods(int)}
 * <LI> cost_min = 1f.  Change using {@link #withCostRange(float, float)}
 * <LI> cost_max = 10f.  Change using {@link #withCostRange(float, float)}
 * <LI> inventory_min = 5f.  Change using {@link #withInventoryRange(float, float)}
 * <LI> inventory_max = 60f.  Change using {@link #withInventoryRange(float, float)}
 * <LI> forecastType = "Cost".  Change using {@link #withForecastType(String)}
 * <LI> forecastModel = null.  Change using {@link #withForecastModel(String)}
 * <LI> unitOfMeasure = "Month".  Change using {@link #withUnitOfMeasure(String)}
 * <LI> calendarName = "Fiscal".  Change using {@link #withCalendarName(String)}
 * <LI> usePeriodNames = false.  Change using {@link #withPeriodNames(boolean)}
 * <LI> lastChangedBy = null  Change using {@link #withLastChangedBy(String)}
 * </UL>
 * <br><br>
 * Chained Call Example
 * <pre>
 * ForecastBuilder<Forecast> builder = 
 * 				new ForecastBuilder<Forecast>(Forecast.class, supplierAllocation, calendar);
 * Iterable<Forecast> data = builder.withTotalWeeks(30).withOffsetWeeks(-10).build();
 * </pre>
 * 
 *
 */
public class ForecastBuilder<T extends Forecast> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected List<SupplierAllocation> supplierAllocationData;
    protected List<Calendar> calendarData;
    protected List<T> forecastData;
    protected Class<T> messageClazz;
    
    protected ForecastBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new ForecastEnricher());
    }

    /**
     * 
     * @param messageClazz
     * 		The Forecast Message class, typically Forecast.class, but can be any class that extends it.
     * @param supplierAllocation
     * 		List of {@link SupplierAllocation} data
     * @param calendar
     * 		List of {@link Calendar} data
     */
    public ForecastBuilder(Class<T> messageClazz, List<SupplierAllocation> supplierAllocation, List<Calendar> calendar) {
        this(messageClazz, defaultNumMessages);
        this.messageClazz = messageClazz;
        this.supplierAllocationData = supplierAllocation;
        this.calendarData = calendar;
    }
    
    
	@Override
    public Iterable<T> build() {
		ForecastGenerator<T> generator = new ForecastGenerator<T>(messageClazz, defaultNumMessages);
		generator.totalFiscalPeriods = this.totalFiscalPeriods;	
		generator.offsetFiscalPeriods = this.offsetFiscalPeriods;	
		generator.cost = this.cost;	
		generator.forecastType = this.forecastType;	
		generator.forecastModel = this.forecastModel;	
		generator.unitOfMeasure = this.unitOfMeasure;	
		generator.calendarName = this.calendarName;	
		generator.usePeriodNames = this.usePeriodNames;	
		generator.cost_min = this.cost_min;	
		generator.cost_max = this.cost_max;	
		generator.inventory_min = this.inventory_min;	
		generator.inventory_max = this.inventory_max;	
		generator.lastChangedBy = this.lastChangedBy;
		
		forecastData = generator.build(supplierAllocationData, calendarData);
				
				
     	setNumMessages(forecastData.size());
        return super.build();
    }

    protected class ForecastEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	messageLine = (T) forecastData.get((int) lineNumber);
            return messageLine;
        }
        
    }
    
   
	
    
    
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
    protected int totalFiscalPeriods = 24;	
    protected int offsetFiscalPeriods = -11;
	protected float cost = NullValue.FLOAT;
	protected String forecastType = "Cost";
	protected String forecastModel = null;
	protected String unitOfMeasure = "Month";
	protected String calendarName = "Fiscal";
	protected boolean usePeriodNames = false;
	protected float cost_min = 1f;
	protected float cost_max = 10f;
	protected float inventory_min = 5f;
	protected float inventory_max = 60f;
	protected String lastChangedBy = null;
	
    /**
     * The total number of Fiscal Periods to create the forecast for.
     * The default is 24
     * 
     * @see	#withOffsetFiscalPeriods(int)
     */
    public ForecastBuilder<T> withTotalFiscalPeriods(int totalFiscalPeriods) {
        this.totalFiscalPeriods = totalFiscalPeriods;
        return this;
    }
    
    /**
     * Offset Fiscal Periods from the current week.  
     * Value can be positive (start in the future) or negative (start in the past)
     * The default is -11 (11 past fiscal periods)
     * 
     * @see	#withTotalFiscalPeriods(int)
     */
    public ForecastBuilder<T> withOffsetFiscalPeriods(int offset) {
        this.offsetFiscalPeriods = offset;
        return this;
    }
    
    /**
     * Sets the default range when ForecastType = COST.  
     * Default is:<br>
     * cost_min = 1f<br>
     * cost_max = 10f<br>
     */
    public ForecastBuilder<T> withCostRange(float min, float max) {
        this.cost_min = min;
        this.cost_max = max;
        return this;
    }
    
    /**
     * Sets the default range when ForecastType = INVENTORY.  
     * Default is:<br>
     * inventory_min = 5f<br>
     * inventory_max = 60f<br>
     */
    public ForecastBuilder<T> withInventoryRange(float min, float max) {
        this.inventory_min = min;
        this.inventory_max = max;
        return this;
    }
    
    /**
     * Sets the {@link Forecast#setForecastType(String)}
     * The default is: "Cost"
     */
    public ForecastBuilder<T> withForecastType(String type) {
        this.forecastType = type;
        return this;
    }
    
    /**
     * Sets the {@link Forecast#setForecastModel(String)}
     * The default is: NULL
     */
    public ForecastBuilder<T> withForecastModel(String model) {
        this.forecastModel = model;
        return this;
    }
    
    /**
     * Sets the {@link Forecast#setBucketUnitOfMeasure(String)}
     * The default is: Month
     */
    public ForecastBuilder<T> withUnitOfMeasure(String UOM) {
        this.unitOfMeasure = UOM;
        return this;
    }
    
    /**
     * Sets the {@link Forecast#setCalendarName(String)}
     * The default is: Fiscal
     */
    public ForecastBuilder<T> withCalendarName(String calendarName) {
        this.calendarName = calendarName;
        return this;
    }
    
    /**
     * Logical value to determine if {@link PointInTime#setStartDate(org.joda.time.DateTime)}
     * or {@link PointInTime#setPeriod(String)} is used.
     * The default is: FALSE (use Start Date)
     */
    public ForecastBuilder<T> withPeriodNames(boolean usePeriodNames) {
        this.usePeriodNames = usePeriodNames;
        return this;
    }
    
    
    
	public static Map<String, Class> getSubClasses() {
		Map<String, Class> listClasses = new HashMap<String, Class>();
		listClasses.put("PointInTime", PointInTime.class);
		listClasses.put("Calendar", Calendar.class);
		return listClasses;
	}
	
    
    /**
     * Sets the {@link Forecast#setLastChangeBy(String)}
     * The default is: NULL
     */
    public ForecastBuilder<T> withLastChangedBy(String changedBy) {
        this.lastChangedBy = changedBy;
        return this;
    }
}
