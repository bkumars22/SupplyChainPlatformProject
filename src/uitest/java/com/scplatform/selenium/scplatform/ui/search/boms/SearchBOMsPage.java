/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.boms;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class SearchBOMsPage extends SCPlatformSearchPage {

	public WebElement itemNumber()	{
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement itemBusiness()	{
		return getElement(By.name("value(businessName)"));
	}

	public WebElement bomDescription()	{
		return getElement(By.name("value(description)"));
	}

	public WebElement multipleItemNumbers()	{
		return getElement(By.name("value(itemNumbers)"));
	}

	public WebElement topLevelItem()	{
		return getElement(By.name("value(topLevel)"));
	}

	public WebElement platform()	{
		return getElement(By.name("value(platform)"));
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

	public WebElement status()	{
		return getElement(By.name("values(status)"));
	}

	public WebElement revision()	{
		return getElement(By.name("value(revisionNumber)"));
	}

	public WebElement usedAsSubassembly()	{
		return getElement(By.name("value(subassembly)"));
	}

	public WebElement version()	{
		return getElement(By.name("value(versionNumber)"));
	}

	public WebElement repairs()	{
		return getElement(By.name("value(isRepairs)"));
	}

	
}

