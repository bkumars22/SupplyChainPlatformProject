/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.dialogs.itemDetails;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class ItemDetailsPage extends SCPlatformPage {

    public void setContext() {
        String[] frames = new String[0];
        setFrame(frames);
    }
	
	public WebElement itemId()	{
		return getByLabel("itemId");
	}

	public WebElement itemDescription()	{
		return getByLabel("itemDescription");
	}

	public WebElement itemType()	{
		return getByLabel("itemType");
	}

	public WebElement managedBy()	{
		return getByLabel("managedBy");
	}

	public WebElement revision()	{
		return getByLabel("revision");
	}

	public WebElement version()	{
		return getByLabel("version");
	}

	public WebElement topLevelItem()	{
		return getByLabel("topLevelItem");
	}

	public WebElement costCommodity()	{
		return getByLabel("costCommodity");
	}

	public WebElement classification()	{
		return getByLabel("classification");
	}

	public WebElement platform()	{
		return getByLabel("platform");
	}

	public WebElement productFamily()	{
		return getByLabel("productFamily");
	}

	public WebElement state()	{
		return getByLabel("state");
	}

	public WebElement uom()	{
		return getByLabel("uom");
	}

	public WebElement inventory()	{
		return getByLabel("inventory");
	}

	public WebElement itemBusiness()	{
		return getByLabel("itemBusiness");
	}

	public WebElement firstLoadedOn()	{
		return getByLabel("firstLoadedOn");
	}

	public WebElement lastUpdatedOn()	{
		return getByLabel("lastUpdatedOn");
	}

	public WebElement sourceSystem()	{
		return getByLabel("sourceSystem");
	}

	public WebElement button_OK()	{
		return getElement(By.partialLinkText("Ok"));
	}
	
	public ItemDetailsModel parse()	{
		ItemDetailsModel model = new ItemDetailsModel();
		
		
		model.setItemId(getElementValue(itemId()).toString());
		model.setItemDescription(getElementValue(itemDescription()).toString());
		model.setItemType(getElementValue(itemType()).toString());
		model.setManagedBy(getElementValue(managedBy()).toString());
		model.setRevision(getElementValue(revision()).toString());
		model.setVersion(getElementValue(version()).toString());
		model.setTopLevelItem(getElementValue(topLevelItem()).toString());
		model.setCostCommodity(getElementValue(costCommodity()).toString());
		model.setClassification(getElementValue(classification()).toString());
		model.setPlatform(getElementValue(platform()).toString());
		model.setProductFamily(getElementValue(productFamily()).toString());
		model.setState(getElementValue(state()).toString());
		model.setUom(getElementValue(uom()).toString());
		model.setInventory(getElementValue(inventory()).toString());
		model.setItemBusiness(getElementValue(itemBusiness()).toString());
		model.setSourceSystem(getElementValue(sourceSystem()).toString());
		model.setFirstLoadedOn(parseDate("firstLoadedOn", getElementValue(firstLoadedOn()).toString()));
		model.setLastUpdatedOn(parseDate("lastUpdatedOn", getElementValue(lastUpdatedOn()).toString()));

		return model;
	}
	
	
	protected DateTime parseDate(String label, String date)	{
		DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DateTimeFormatUI());
		DateTime parsedDate = null;
		if (StringUtils.isBlank(date))	{
			return parsedDate;
		}
		
		try	{
			parsedDate = DateTime.parse(date, formatter);
		} catch (IllegalArgumentException e){
			JLog.error(String.format("Error parsing '%s' with value '%s'", getDisplayName(label), date) , e, TakeScreenshot.True);
		}
		
		return parsedDate;
	}
	
	
	
	
	protected String getDisplayName(String label){
		ItemDetailsModel model = new ItemDetailsModel();
		return model.getDisplayName(label);
	}
	
	protected WebElement getByLabel(String label){
		return getLabelElement(getDisplayName(label));
	}
	
	@Override
	public WebElement getLabelElement(String labelName){
		return getElement(By.xpath("//tr[contains(@class,'tableRow')]/td[contains(.,'" + labelName + "')]/following-sibling::td"));
	}
}
