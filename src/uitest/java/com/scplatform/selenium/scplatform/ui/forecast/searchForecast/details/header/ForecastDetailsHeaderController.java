/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast.details.header;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.ForecastDetailsController;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.header.ForecastDetailsHeaderModel;

public class ForecastDetailsHeaderController extends ForecastDetailsController {
	private ForecastDetailsHeaderPage page;

	public ForecastDetailsHeaderController() {
		super();
		page = new ForecastDetailsHeaderPage();
	}

	@Override
	public PageImpl getView() {
		return new ForecastDetailsHeaderPage();
	}

	public boolean validate(Forecast expected) {
		boolean success = true;
		boolean verified = true;

		JLog.section("Verify Forecast Details Header - " + expected.getItemIdentifier());

		ForecastDetailsHeaderModel actual = page.getHeader();

		verified = verify(actual.getDisplayName("forecastType"), actual.getForecastType().toUpperCase(),
				expected.getForecastType().toUpperCase());
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("calendarType"), actual.getCalendarType(), "Fiscal Calendar");
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("periodType"), actual.getPeriodType(),
				expected.getBucketUnitOfMeasure());
		success = (verified) ? success : verified;

		return success;
	}
}
