/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchSourcingLane;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class SearchSourcingLanePage extends SCPlatformSearchPage {
	
	public WebElement itemNumber()	{
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement itemBusiness()	{
		return getElement(By.name("value(business)"));
	}

	public WebElement commodityName()	{
		return getElement(By.name("value(category)"));
	}

	public WebElement multipleItemNumbers()	{
		return getElement(By.name("value(itemNumbers)"));
	}

	public WebElement status()	{
		return getElement(By.name("values(status)"));
	}

	public WebElement multipleCommodityNames()	{
		return getElement(By.name("value(categories)"));
	}

	public WebElement supplierName()	{
		return getElement(By.name("value(supplierName)"));
	}

	public WebElement productState()	{
		return getElement(By.name("value(productState)"));
	}

	public WebElement laneName()	{
		return getElement(By.name("value(name)"));
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

	public WebElement destinationSite()	{
		return getElement(By.name("value(toSite)"));
	}

	public WebElement multipleDestinationSites()	{
		return getElement(By.name("value(toSites)"));
	}

}
