/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.forecast;

import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.common.MathUtils;
import com.test.selenium.common.StringUtilities;
import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.forecast.subClasses.PointInTime;

public class ForecastUtils {

	public int findByDate(Forecast forecast, DateTime date)	{
		int pitIndex = NullValue.INTEGER;
		
		for (int pit = 0; pit < forecast.getPointInTime().size(); pit++)	{
			DateTime startDate = forecast.getPointInTime().get(pit).getStartDate();
			DateTime endDate = forecast.getPointInTime().get(pit).getEndDate();
			
			if (startDate == null)	{
				Calendar calendar = forecast.getPointInTime().get(pit).getCalendar();
				startDate = calendar.getMonth_StartDate();
				endDate = calendar.getMonth_EndDate();
			}
			
			if (
					date.equals(startDate) ||
					date.equals(endDate) ||
					( (date.isAfter(startDate)) && (date.isBefore(endDate))) )
						
			{
				pitIndex = pit;
				break;
			}
		}
		
		return pitIndex;
	}
	
	public int findByPeriodName(Forecast forecast,  String periodName)	{
		int pitIndex = NullValue.INTEGER;
		
		for (int pit = 0; pit < forecast.getPointInTime().size(); pit++)	{
			if (periodName.equals(forecast.getPointInTime().get(pit).getPeriod()))	{
				pitIndex = pit;
				break;
			}
		}
		
		return pitIndex;
	}
	

	
	public int getForecastNextMonth(Forecast forecast, int currentPITIndex)	{
		int nextForecastIndex = NullValue.INTEGER;
		int index = currentPITIndex + 1;
		if (index < forecast.getPointInTime().size()){
			nextForecastIndex = index;
		}
		return nextForecastIndex;
	}
	
	public int getForecastPreviousMonth(Forecast forecast, int currentPITIndex)	{
		int prevForecastIndex = NullValue.INTEGER;
		int index = currentPITIndex - 1;
		if (index >= 0){
			prevForecastIndex = index;
		}
		return prevForecastIndex;
	}
	
	public String createUILabel(PointInTime pointInTime)	{
		Calendar calendar = pointInTime.getCalendar();
		return calendar.getMonth_Name() + " " + calendar.getMonth_StartDate().toString("MMM dd");
	}

	private String pitCalculation;
	public String getPitCalculationMessage()	{
		return pitCalculation;
	}
	public float calculatePITValue(PointInTime pointInTime) {
		float pitValue = MathUtils.format(pointInTime.getPitValue(), "###.###", RoundingMode.HALF_UP);
		
		if (StringUtils.isNotBlank(pointInTime.getPeriodicAdjustmentType()))	{
			float periodicAdjustmentValue = MathUtils.format(pointInTime.getPeriodicAdjustmentValue(), "###.###", RoundingMode.HALF_UP);

			if ("Percent".equalsIgnoreCase(pointInTime.getPeriodicAdjustmentType()))	{
				float pitValue2 = pitValue + (pitValue * (periodicAdjustmentValue/100));	
				
				// this is truncated to 4 decimal places (not rounded up or down)
				pitValue2 = MathUtils.truncate(pitValue2, 4);
				pitCalculation = String.format("CALCULATION: [PeriodicAdjustmentType=%s] pitValue[%f] + (pitValue[%f] * (periodicAdjustmentValue[%f]/100)) == %f", 
						pointInTime.getPeriodicAdjustmentType(), 
						pitValue,
						pitValue,
						periodicAdjustmentValue,
						pitValue2);
				pitValue = pitValue2;
			} else if ("Fixed".equalsIgnoreCase(pointInTime.getPeriodicAdjustmentType()))	{
				float pitValue2 = pitValue + periodicAdjustmentValue;
				pitValue2 = MathUtils.format(pitValue2, "###.###", RoundingMode.HALF_UP);
				pitCalculation = String.format("CALCULATION: [PeriodicAdjustmentType=%s] pitValue[%f] + (periodicAdjustmentValue[%f]) == %f", 
						pointInTime.getPeriodicAdjustmentType(), 
						pitValue,
						periodicAdjustmentValue,
						pitValue2);
				pitValue = pitValue2;
			} else	{
				JLog.error(this.getClass() + ".calculatePITValue() - unknown PeriodicAdjustmentType; " + pointInTime.getPeriodicAdjustmentType());
			}
		} else	{
			float pitValue2 = MathUtils.format(pointInTime.getPitValue(), "###.##", RoundingMode.HALF_UP);
			pitCalculation = String.format("CALCULATION: pitValue[%f] = %f", pitValue, pitValue2);
			pitValue = pitValue2;
		}
		return pitValue;
	}
	

}

