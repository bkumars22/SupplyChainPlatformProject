/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.forecast;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.scplatform.qa.iris.model.exceptions.FieldNotFoundException;
import com.scplatform.qa.iris.model.exceptions.InvalidValueException;
import com.test.selenium.common.JLog;
import com.test.selenium.common.MathUtils;
import com.test.selenium.common.RandomUser;
import com.test.selenium.common.RandomUtils;
import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.calendar.CalendarUtils;
import com.test.selenium.scplatform.messages.forecast.subClasses.PointInTime;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;

public class ForecastGenerator<T extends Forecast> extends ForecastBuilder<T> {
	protected ForecastGenerator(Class<T> messageClazz, long numMessages) {
		super(messageClazz, numMessages);
	}

	protected List<T> dataSet; 
    

	

	
	
	public List<T> build(List<SupplierAllocation> supplierAllocation, List<Calendar> calendar)	{
		dataSet = new ArrayList<T>();
        List<String> customerItems = new ArrayList<String>();
        
        List<Calendar> periodCalendar = getPeriodCalendar(calendar);
		int startIndex = findCalendarStart (periodCalendar);
		if (startIndex == -1)	{
			JLog.error("Unable to find the current date from the Calendar (" + periodCalendar.size() + " elements");
			return dataSet;
		}
		
		
		for (int row = 0; row < supplierAllocation.size(); row++)	{
			if (customerItems.contains(supplierAllocation.get(row).getCustomerItemIdentifier()))	{
				// item already added
				continue;
			} else	{
				customerItems.add(supplierAllocation.get(row).getCustomerItemIdentifier());
			}
			
			
			try {
				this.dataSet.add(getBaseData(supplierAllocation.get(row), periodCalendar, startIndex, totalFiscalPeriods, row+1));
			} catch (FieldNotFoundException | InvalidValueException e) {
				JLog.error(e);
			}
		}
		
		return dataSet;
	}
	
	protected List<Calendar> getPeriodCalendar(List<Calendar> calendar){
		CalendarUtils utils = new CalendarUtils();
		return utils.getCalendarForPeriod(calendar, this.unitOfMeasure);
	}
	
	protected int findCalendarStart(List<Calendar> calendar) {
		DateTime date = DateTime.now().withTimeAtStartOfDay();
		int offset = Math.abs(offsetFiscalPeriods);
		
		if (this.unitOfMeasure.equals("Month"))	{
			date = (offsetFiscalPeriods < 0) ? date.minusMonths(offset+1) : date.plusMonths(offset);
		} else if ((this.unitOfMeasure.equals("Week")))	{
			date = (offsetFiscalPeriods < 0) ? date.minusWeeks(offset+1) : date.plusWeeks(offset);
		}
				
		CalendarUtils utils = new CalendarUtils();
		int index = utils.findIndexForDate(calendar, date);
		
		if (index == -1)	{
			JLog.warning (this.getClass().getSimpleName() + ".findCalendarStart(): Using date '" + date + "', unable to find a Calendar Entry!");
		}
		return index;
	}
	
	
	
	protected T getBaseData (SupplierAllocation supplierAllocation, List<Calendar> calendar, int startIndex, int iterations, int counter) throws FieldNotFoundException, InvalidValueException	{
		T baseData = (T) T.Factory.newInstance();
		
		RandomUser user = new RandomUser();
		String responsibility = user.getFirstName() + " " + user.getLastName();
		float pitValue = 0f;
		if (forecastType.equals("Cost"))	{
			if (cost == NullValue.FLOAT){
				cost = MathUtils.format(RandomUtils.randomFloat(cost_min, cost_max), "####.00", RoundingMode.HALF_DOWN);
			}
			float delta = MathUtils.format(RandomUtils.randomFloat(-2, 4), "####.00", RoundingMode.HALF_DOWN);
			pitValue = Math.abs(cost + delta);
		} else	{
			pitValue = Math.abs(MathUtils.format(RandomUtils.randomFloat(inventory_min, inventory_max), "####.00", RoundingMode.HALF_DOWN));
		}

		
		
		
		baseData.setForecastExternalId(null);
		baseData.setDescription("Forecast for item " + supplierAllocation.getCustomerItemIdentifier());
		baseData.setItemIdentifier(supplierAllocation.getCustomerItemIdentifier());
		baseData.setItemUniqueId(supplierAllocation.getItemUniqueId());
		baseData.setItemRevision(supplierAllocation.getItemRevision());
		baseData.setItemVersion(supplierAllocation.getItemVersion());
		baseData.setItemType(null);
		baseData.setBusinessEntity(supplierAllocation.getCustomerBusinessEntity());
		baseData.setBusinessEntityType(supplierAllocation.getCustomerBusinessEntityType());
		baseData.setStartDate(null);
		baseData.setEndDate(null);
		baseData.setSite(supplierAllocation.getCustomerSite());
		baseData.setForecastType(forecastType);
		baseData.setForecastModel(forecastModel);
		baseData.setLastChangeBy(lastChangedBy);
		baseData.setBucketUnitOfMeasure(unitOfMeasure);
		baseData.setCalendarName(calendarName);
		baseData.setPeriodicAdjustmentValue(NullValue.FLOAT);
		baseData.setPeriodicAdjustmentType(null);
		baseData.setConfidenceFactor(MathUtils.Round (RandomUtils.randomFloat(80, 100), 0));
		baseData.setRemainingRolloverPeriods(NullValue.INTEGER);
		baseData.setOperationCode("C");
		baseData.setPRODUCTIONResponsibility(responsibility + " (Production)");
		baseData.setSERVICEResponsibility(responsibility + " (Service)");
		baseData.setVerification_Status("Approved");
		
		List<PointInTime> pointInTime = new ArrayList<PointInTime>();
		Calendar previousCalendarData = Calendar.Factory.newInstance();
		
		String forecastModelLocal = forecastModel;
		boolean adjustable = true;
		if (forecastModel == null){
			adjustable = RandomUtils.randomBoolean();
			forecastModelLocal = (adjustable) ? "ADJUSTABLE" : "CURRENT";
			
			if (forecastModelLocal.equals("ADJUSTABLE"))	{
				baseData.setPeriodicAdjustmentType((RandomUtils.randomBoolean()) ? "Fixed" : "Percent");
			}
		}  
			
		if (forecastModelLocal.equals("CURRENT"))	{
			adjustable = false;
		}
		
		baseData.setForecastModel(forecastModelLocal);
		
		if ( (adjustable) && (StringUtils.isBlank(baseData.getPeriodicAdjustmentType())) )	{
			baseData.setPeriodicAdjustmentType((RandomUtils.randomBoolean()) ? "Fixed" : "Percent");
		}
		
		int currentIteration = 0;
		for (int index = startIndex; currentIteration <= iterations; index++)	{
			PointInTime pit = PointInTime.Factory.newInstance();
			if (index >= calendar.size()){
				// not enough calendar data to go all the way
				break;
			}
			Calendar calendarData = calendar.get(index);

			if ( (StringUtils.isNotBlank(previousCalendarData.getName())) && (unitOfMeasure.equals("Week")) && (calendarData.getWeek_FiscalWeek() == previousCalendarData.getWeek_FiscalWeek()) )	{
				// skip this entry
				continue;
			} else if ( (StringUtils.isNotBlank(previousCalendarData.getName())) && (unitOfMeasure.equals("Month")) && (calendarData.getMonth_FiscalMonth() == previousCalendarData.getMonth_FiscalMonth()) )	{
				// skip this entry
				continue;
			} else	{
				currentIteration++;
				previousCalendarData = Calendar.Factory.clone(calendarData);
			}
				
			if (unitOfMeasure.equals("Month"))	{
				if (this.usePeriodNames){
					pit.setPeriod(calendarData.getMonth_Name());
				} else	{
					pit.setStartDate(calendarData.getMonth_StartDate());
					pit.setEndDate(calendarData.getMonth_EndDate());
				}

				baseData.setEndDate(calendarData.getMonth_EndDate());
				if (index == startIndex){
					baseData.setStartDate(calendarData.getMonth_StartDate());
				}
			} else	{
				if (usePeriodNames){
					pit.setPeriod(calendarData.getWeek_Name());
				} else	{
					pit.setStartDate(calendarData.getWeek_StartDate());
					pit.setEndDate(calendarData.getWeek_EndDate());
				}

				baseData.setEndDate(calendarData.getWeek_EndDate());
				if (index == startIndex){
					baseData.setStartDate(calendarData.getWeek_StartDate());
				}
			}

			pit.setCalendar(calendarData);
			
			pit.setPitTypeCode("ACTUALFORECAST");
			pit.setPitValue(pitValue);
			
			pit.setPeriodicAdjustmentValue(NullValue.FLOAT);
			pit.setPeriodicAdjustmentType(null);
			pit.setOperationCode("A");
			
			
			if ( (forecastType.equals("Cost")) && (adjustable) )	{
				int multiplier = (RandomUtils.randomBoolean()) ? 1 : -1;
				float adjustmentPercentage = MathUtils.Round(multiplier * RandomUtils.randomFloat(1, 15), 1);
				
				pit.setPeriodicAdjustmentType(baseData.getPeriodicAdjustmentType());
				if (pit.getPeriodicAdjustmentType().equals("Percent"))	{
					pit.setPeriodicAdjustmentValue(adjustmentPercentage);
				} else	{
					// needs to be a fixed value, based on current pitValue
					pit.setPeriodicAdjustmentValue(MathUtils.getPercentage(pit.getPitValue(), adjustmentPercentage, 4));
				}
				
			}
			pointInTime.add(pit);
		}



		float lastPitValue = 1f;
		for (int index = 0; index < pointInTime.size(); index++)	{
			if ( (StringUtils.isNotBlank(baseData.getPeriodicAdjustmentType())) && (baseData.getPeriodicAdjustmentType().equals("Percent")) )	{
				if (index == 0){
					pointInTime.get(index).setPitValue(pointInTime.get(index).getPitValue());
					baseData.setPeriodicAdjustmentValue(pointInTime.get(index).getPitValue());
				} else	{
					cost = MathUtils.deductPercentage(baseData.getPeriodicAdjustmentValue(), pointInTime.get(index).getPeriodicAdjustmentValue(), 5);
					if (cost < 0){
						baseData.setPeriodicAdjustmentValue(baseData.getPeriodicAdjustmentValue()+2);
						cost = MathUtils.deductPercentage(baseData.getPeriodicAdjustmentValue(), pointInTime.get(index).getPeriodicAdjustmentValue(), 5);
						cost = Math.abs(cost);
					}
					pointInTime.get(index).setPitValue(MathUtils.format(cost, "####.00", RoundingMode.HALF_DOWN));
				}
				
				
			} 
			
			if (!adjustable)	{
				// RULE:
				// Adjustment amounts or type is not allowed for a forecast model of type CURRENT
				baseData.setPeriodicAdjustmentValue(NullValue.FLOAT);
				baseData.setPeriodicAdjustmentType(null);
				pointInTime.get(index).setPeriodicAdjustmentValue(NullValue.FLOAT);
				pointInTime.get(index).setPeriodicAdjustmentType(null);
				
				if ((pointInTime.get(index).getPitValue() == NullValue.FLOAT) || (pointInTime.get(index).getPitValue() == 0f)){
					pointInTime.get(index).setPitValue(lastPitValue);
				} else	{
					lastPitValue = pointInTime.get(index).getPitValue();
				}
			}

		}
		
		baseData.setPointInTime(pointInTime);
		
		return baseData;
	}

	
	
}
