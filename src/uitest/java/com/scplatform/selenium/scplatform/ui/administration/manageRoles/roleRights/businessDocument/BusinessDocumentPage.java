/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessDocument;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class BusinessDocumentPage extends SCPlatformPage {

	public WebElement sourcingLane_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'SOURCING_LANE')]"));
	}

	public WebElement costRecord_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'COST_RECORD')]"));
	}

	public WebElement rebate_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'REBATE')]"));
	}

	public WebElement supplyAllocation_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'SUPPLY_ALLOC')]"));
	}
	
	public WebElement forecast_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'FORECAST')]"));
	}

	public WebElement itemAssignment_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'ITEM_ASSIGNMENT')]"));
	}

	public WebElement itemCommodity_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'ITEM_CATEGORY')]"));
	}

	public WebElement billOfMaterial_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'BOM')]"));
	}

	public WebElement uploadAndDownload_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'UPDOWN')]"));
	}

	public WebElement uploadDocuments_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'UPLOAD_TYPE')]"));
	}
	
	public WebElement reports_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'REPORTS')]"));
	}

	
	
}
