/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.itemAVL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class SearchItemAVLPage extends SCPlatformSearchPage {

	public WebElement itemNumber()	{
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement itemBusiness()	{
		return getElement(By.name("value(businessName)"));
	}

	public WebElement itemDescription()	{
		return getElement(By.name("value(itemDescription)"));
	}

	public WebElement multipleItemNumbers()	{
		return getElement(By.name("value(itemNumbers)"));
	}

	public WebElement itemType()	{
		return getElement(By.name("values(itemType)"));
	}

	public WebElement topLevelItem()	{
		return getElement(By.name("value(topLevel)"));
	}

	public WebElement productFamily()	{
		return getElement(By.name("value(productFamily)"));
	}

	public WebElement classification()	{
		return getElement(By.name("value(itemClassification)"));
	}

	public WebElement platform()	{
		return getElement(By.name("value(platformName)"));
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

	public WebElement commodityName()	{
		return getElement(By.name("value(categoryName)"));
	}

	public WebElement multipleCommodityNames()	{
		return getElement(By.name("value(categoryNames)"));
	}
	
	public WebElement supplierItemNumber()	{
		return getElement(By.name("value(supplierItemNumber)"));
	}

	public WebElement supplierName()	{
		return getElement(By.name("value(supplierName)"));
	}

	
}
