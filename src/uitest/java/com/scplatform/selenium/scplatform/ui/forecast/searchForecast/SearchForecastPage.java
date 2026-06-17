/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

/**
 * Navigation: Forecast -> Search Forecast
 */
public class SearchForecastPage extends SCPlatformSearchPage {

	public WebElement itemNumber()	{
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement memberOfGroup()	{
		return getElement(By.name("value(memberOf)"));
	}

	public WebElement commodityName()	{
		return getElement(By.name("value(categoryName)"));
	}

	public WebElement multipleItemNumbers()	{
		return getElement(By.name("value(itemNumbers)"));
	}

	public WebElement status()	{
		return getElement(By.name("values(status)"));
	}

	public WebElement multipleCommodityNames()	{
		return getElement(By.name("value(categoryNames)"));
	}

	public WebElement assigned()	{
		return getElement(By.name("value(isAssigned)"));
	}

	public WebElement responsibility()	{
		return getElement(By.name("value(owner)"));
	}

	public WebElement assignedTo()	{
		return getElement(By.name("value(user)"));
	}

	public WebElement region()	{
		return getElement(By.name("values(siteList)"));
	}

	public WebElement forecastModel()	{
		return getElement(By.name("values(forecastModel)"));
	}

	public WebElement platform()	{
		return getElement(By.name("value(platformName)"));
	}

	public WebElement lastChangedAfter()	{
		return getElement(By.name("dateValue(modifyDateStart)"));
	}

	public WebElement lastChangedBefore()	{
		return getElement(By.name("dateValue(modifyDateEnd)"));
	}

	public WebElement lastChangeBy()	{
		return getElement(By.name("value(lastChangeBy)"));
	}

	public WebElement extendForecastTerm()	{
		return getElement(By.name("value(extendForecast)"));
	}

	
}
