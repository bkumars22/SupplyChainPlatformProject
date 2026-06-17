/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchCostRecords.sourcingLaneInformation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class SourcingLaneInformationPage extends SCPlatformPage {

	public WebElement laneName()	{
		return getElement(By.name("selectedLane.sourcingLaneName"));
	}

	public WebElement item()	{
		return getElement(By.xpath("//a[contains(@href, 'showPopup(\"viewItemDetails.do?itemKey=')]"));
	}

	public WebElement supplier()	{
		return getElement(By.name("supplierKey"));
	}

	public WebElement currency()	{
		return getElement(By.name("selectedLane.currencyCode"));
	}

	public WebElement endDatesAreRequiredForPricing()	{
		return getElement(By.xpath("//input[contains(@onclick, 'endDateRequired')]"));
	}

	public WebElement collaboration()	{
		return getElement(By.xpath("//input[contains(@onclick, 'collaboration')]"));
	}

	public WebElement status()	{
		return getLabelElement("Status");
	}

	public WebElement destinationSite()	{
		return getElement(By.name("toSiteKey"));
	}

	public WebElement sourceSite()	{
		return getElement(By.name("fromSiteKey"));
	}

	public WebElement offsetInDays()	{
		return getElement(By.name("selectedLane.dateOffset"));
	}

	public WebElement productState()	{
		return getElement(By.name("selectedLane.productState"));
	}

	public WebElement existingLanes()	{
		return getElement(By.name("costLaneKey"));
	}
	
	public WebElement refresh()	{
		return getElement(By.partialLinkText("Refresh"));
	}
	
	public WebElement close()	{
		return getElement(By.partialLinkText("Close"));
	}
	
	public WebElement approve()	{
		return getElement(By.partialLinkText("Approve"));
	}
	
	public WebElement submit()	{
		return getElement(By.partialLinkText("Submit"));
	}
	
}
