/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast.details.header;

import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.ui.forecast.searchForecast.details.ForecastDetailsPage;

public class ForecastDetailsHeaderPage extends ForecastDetailsPage {

	
	public WebElement forecastType()	{
		return getByLabel("forecastType");
	}

	public WebElement calendarType()	{
		return getByLabel("calendarType");
	}

	public WebElement periodType()	{
		return getByLabel("periodType");
	}

	
	private WebElement getByLabel(String label){
		ForecastDetailsHeaderModel model = new ForecastDetailsHeaderModel();
		return getLabelElement(model.getDisplayName(label));
	}
	
	public ForecastDetailsHeaderModel getHeader()	{
		ForecastDetailsHeaderModel model = new ForecastDetailsHeaderModel();
		
		model.setForecastType((String) getElementValue(forecastType()));
		model.setCalendarType((String) getElementValue(calendarType()));
		model.setPeriodType((String) getElementValue(periodType()));
		
		return model;
	}
}
