/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.itemAssignment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class ItemAssignmentPage extends SCPlatformSearchPage {
	public WebElement itemNumber()	{
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement itemBusiness()	{
		return getElement(By.name("value(business)"));
	}

	public WebElement costCommodity()	{
		return getElement(By.name("value(categoryName)"));
	}

	public WebElement multipleItemNumbers()	{
		return getElement(By.name("value(itemNumbers)"));
	}

	public WebElement itemType()	{
		return getElement(By.name("values(itemType)"));
	}

	public WebElement managedBy()	{
		return getElement(By.name("value(managedFlag)"));
	}

	public WebElement daysSinceAdded()	{
		return getElement(By.name("value(daysOld)"));
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

}
