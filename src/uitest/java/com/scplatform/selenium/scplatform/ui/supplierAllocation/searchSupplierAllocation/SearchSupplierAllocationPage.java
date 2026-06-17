/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class SearchSupplierAllocationPage extends SCPlatformSearchPage {
	
	public WebElement itemNumber()	{
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement periodStart()	{
		return getElement(By.name("dateValue(startDate)"));
	}

	public WebElement periodEnd()	{
		return getElement(By.name("dateValue(endDate)"));
	}

	public WebElement multipleItemNumbers()	{
		return getElement(By.name("value(itemNumbers)"));
	}

	public WebElement supplier()	{
		return getElement(By.name("value(supplierName)"));
	}

}
