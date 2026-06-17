/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.details;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class SupplierAllocationPage extends SCPlatformPage {

	private WebElement getByLabel(String label){
		SupplierAllocationModel model = new SupplierAllocationModel();
		return getLabelElement(model.getDisplayName(label));
	}
	
	public WebElement itemNumber()	{
		return getByLabel("itemNumber");
	}

	public WebElement periodType()	{
		return getElement(By.name("periodType"));
	}

	public WebElement itemDescription()	{
		return getByLabel("itemDescription");
	}

	public WebElement managedBy()	{
		return getByLabel("managedBy");
	}

	public WebElement periodStart()	{
		return getElement(By.name("fromDate"));
	}

	public WebElement periodEnd()	{
		return getElement(By.name("toDate"));
	}

	public WebElement button_ShowHistory()	{
		return getElement(By.partialLinkText("Show History"));
	}
	
	public WebElement button_Refresh()	{
		return getElement(By.partialLinkText("Refresh"));
	}
	
	public WebElement button_Copy()	{
		return getElement(By.partialLinkText("Copy"));
	}
	
	public SupplierAllocationModel getHeader()	{
		SupplierAllocationModel model = new SupplierAllocationModel();
		DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DateFormatUI());
		
		model.setItemNumber((String) getElementValue(itemNumber()));
		model.setPeriodType((String) getElementValue(periodType()));
		model.setItemDescription((String) getElementValue(itemDescription()));
		model.setManagedBy((String) getElementValue(managedBy()));
		model.setPeriodStart(DateTime.parse((String) getElementValue(periodStart()), formatter));
		model.setPeriodEnd(DateTime.parse((String) getElementValue(periodEnd()), formatter));
		
		return model;
	}
}
