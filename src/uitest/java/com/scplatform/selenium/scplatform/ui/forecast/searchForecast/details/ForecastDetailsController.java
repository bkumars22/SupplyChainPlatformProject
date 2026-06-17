/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast.details;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.adjustable.ForecastDetailsAdjustableController;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.current.ForecastDetailsCurrentController;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.header.ForecastDetailsHeaderController;

public class ForecastDetailsController extends SCPlatformController {
	private ForecastDetailsPage page;
	
	public ForecastDetailsController()	{
		super();
		page = new ForecastDetailsPage();
	}
	
	@Override
	public PageImpl getView() {
		return new ForecastDetailsPage();
	}

	public boolean validate(Forecast expectedResults, Item itemData)	{

		boolean success = true;
		boolean verified = true;
		
		ForecastDetailsHeaderController forecastDetailsHeaderController = new ForecastDetailsHeaderController();
		verified = forecastDetailsHeaderController.validate(expectedResults);
		success = (verified) ? success : verified;
		
		if ("CURRENT".equalsIgnoreCase(expectedResults.getForecastModel()))	{
			ForecastDetailsCurrentController currentController = new ForecastDetailsCurrentController();
			verified = currentController.validate(expectedResults, itemData);
			success = (verified) ? success : verified;
		} else	{
			ForecastDetailsAdjustableController adjustableController = new ForecastDetailsAdjustableController();
			verified = adjustableController.validate(expectedResults, itemData);
			success = (verified) ? success : verified;
		}
		JLog.blankLine();
		return success;
	}
	
	public void clickBack()	{
		clickAndCheckForPOSTError(page.backButton());
	}
	
	public void clickSaveAndExit()	{
		clickAndCheckForPOSTError(page.saveAndExitButton());
	}
	
	public void clickSave()	{
		clickAndCheckForPOSTError(page.saveButton());
	}
	
	public void clickCancel()	{
		clickAndCheckForPOSTError(page.cancelButton());
	}
	
}

