/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.newSourcingLane;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class NewSourcingLanePage extends SCPlatformSearchPage {
	public WebElement itemNumber()	{
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement itemBusiness()	{
		return getElement(By.name("value(business)"));
	}

	public WebElement supplier()	{
		return getElement(By.name("value(supplierName)"));
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

	public WebElement platform()	{
		return getElement(By.name("value(platformName)"));
	}

	public WebElement commodityName()	{
		return getElement(By.name("value(category)"));
	}

	public WebElement multipleCommodityNames()	{
		return getElement(By.name("value(categories)"));
	}

}
