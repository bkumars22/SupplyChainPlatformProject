/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageBusinessEntities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class ManageBusinessEntitiesPage extends SCPlatformSearchPage {

	public WebElement businessName()	{
		return getElement(By.name("value(businessName)"));
	}

	public WebElement id()	{
		return getElement(By.name("value(businessId)"));
	}

	public WebElement alias()	{
		return getElement(By.name("value(businessNameAlt)"));
	}

	public WebElement type()	{
		return getElement(By.name("values(businessType)"));
	}

}
