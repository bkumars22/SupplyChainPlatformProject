/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.newSourcingLane.details;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class SourcingLaneInformationPage extends SCPlatformPage {

	@Override
	public WebElement saveButton()	{
		return submitButton();
	}
	
	public WebElement existingLanes()	{
		return getElement(By.name("costLaneKey"));
	}

	public WebElement laneName()	{
		return getElement(By.name("selectedLane.sourcingLaneName"));
	}

	public WebElement item()	{
		return getByLabel("item");
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
		return getByLabel("status");
	}

	public WebElement destinationSite()	{
		return getElement(By.name("toSiteKey"));
	}

	public WebElement sourceSupplier()	{
		return getElement(By.name("fromSiteKey"));
	}

	public WebElement offsetInDays()	{
		return getElement(By.name("selectedLane.dateOffset"));
	}

	public WebElement productState()	{
		return getElement(By.name("selectedLane.productState"));
	}

	private WebElement getByLabel(String label){
		SourcingLaneInformationModel model = new SourcingLaneInformationModel();
		return getLabelElement(model.getDisplayName(label));
	}
		
	@Override
	public WebElement getLabelElement(String labelName){
		return getElement(By.xpath("//td[@class='formLabel' and contains(.,'" + labelName + "')]/following-sibling::td[1]"));
	}
	
	
	
	public WebElement refreshButton() {
		return getElement(By.partialLinkText("Refresh"));
	}
	
	public WebElement closeButton() {
		return getElement(By.partialLinkText("Close"));
	}
	
	public WebElement approveButton() {
		return getElement(By.partialLinkText("Approve"));
	}
	
	public WebElement submitButton() {
		return getElement(By.partialLinkText("Submit"));
	}
	
	public WebElement reopenButton() {
		return getElement(By.partialLinkText("Reopen"));
	}
	
	
	public SourcingLaneInformationModel parse()	{
		SourcingLaneInformationModel model = new SourcingLaneInformationModel();
		
		model.setLaneName(getElementValue(laneName()).toString());
		model.setItem(getElementValue(item()).toString());
		model.setSupplier(getElementValue(supplier()).toString());
		model.setCurrency(getElementValue(currency()).toString());
		model.setEndDatesAreRequiredForPricing(getElementValue(endDatesAreRequiredForPricing()).toString());
		model.setCollaboration(getElementValue(collaboration()).toString());
		model.setStatus(getElementValue(status()).toString());
		model.setDestinationSite(getElementValue(destinationSite()).toString());
		model.setSourceSupplier(getElementValue(sourceSupplier()).toString());
		model.setOffsetInDays(getElementValue(offsetInDays()).toString());
		model.setProductState(getElementValue(productState()).toString());

		return model;
	}
	
	
}
