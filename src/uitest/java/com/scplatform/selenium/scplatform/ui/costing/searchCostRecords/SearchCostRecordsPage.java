/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchCostRecords;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class SearchCostRecordsPage extends SCPlatformPage {

	public WebElement itemNumber()	{
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement multipleItemNumbers()	{
		return getElement(By.name("value(itemNumbers)"));
	}

	public WebElement supplierName()	{
		return getElement(By.name("value(supplierName)"));
	}

	public WebElement assignedTo()	{
		return getElement(By.name("value(user)"));
	}

	public WebElement multipleCommodityNames()	{
		return getElement(By.name("value(categories)"));
	}

	public WebElement effectiveDate()	{
		return getElement(By.name("dateValue(effectiveDate)"));
	}

	public WebElement itemBusiness()	{
		return getElement(By.name("value(business)"));
	}

	public WebElement costType()	{
		return getElement(By.name("values(costType)"));
	}

	public WebElement assigned()	{
		return getElement(By.name("value(isAssigned)"));
	}

	public WebElement platform()	{
		return getElement(By.name("value(platformName)"));
	}

	public WebElement destinationSite()	{
		return getElement(By.name("value(toSite)"));
	}

	public WebElement expirationDate()	{
		return getElement(By.name("dateValue(expireDate)"));
	}

	public WebElement itemDescription()	{
		return getElement(By.name("value(itemDescription)"));
	}

	public WebElement status()	{
		return getElement(By.name("values(status)"));
	}

	public WebElement responsibility()	{
		return getElement(By.name("value(owner)"));
	}

	public WebElement commodityName()	{
		return getElement(By.name("value(category)"));
	}

	public WebElement multipleDestinationSites()	{
		return getElement(By.name("value(toSites)"));
	}

	public WebElement pricingScenario()	{
		return getElement(By.name("values(pricingScenario)"));
	}

}
