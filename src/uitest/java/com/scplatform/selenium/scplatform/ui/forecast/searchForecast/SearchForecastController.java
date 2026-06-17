/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast;

import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchController;

/**
 * Navigation: Forecast -> Search Forecast
 * 
 * @see	#setModel(com.test.selenium.common.modelViewController.model.Model)
 * @see #search()
 */
public class SearchForecastController extends SCPlatformSearchController {

	@Override
	public PageImpl getView() {
		return new SearchForecastPage();
	}

}
