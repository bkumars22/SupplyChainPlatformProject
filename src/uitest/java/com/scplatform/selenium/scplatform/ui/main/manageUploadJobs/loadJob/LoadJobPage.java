/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class LoadJobPage extends SCPlatformPage {

	public WebElement button_ShowHistory()	{
		return getElement(By.partialLinkText("Show History"));
	}
	
	public WebElement button_RefreshJob()	{
		return getElement(By.partialLinkText("Refresh Job"));
	}
	
	public WebElement dateLoaded()	{
		return getByLabel("dateLoaded");
	}

	public WebElement loadedBy()	{
		return getByLabel("loadedBy");
	}

	public WebElement state()	{
		return getByLabel("state");
	}

	public WebElement clearAnyMatchingEvents()	{
		return getElement(By.xpath("//input[@onclick='handleClearAll(this)']"));
	}

	public WebElement jobID()	{
		return getByLabel("jobID");
	}

	public WebElement uploadType()	{
		return getByLabel("uploadType");
	}

	public WebElement status()	{
		return getByLabel("status");
	}

	public WebElement fileLoaded()	{
		return getByLabel("fileLoaded");
	}

	private WebElement getByLabel(String label){
		LoadJobModel model = new LoadJobModel();
		return getLabelElement(model.getDisplayName(label));
	}
	
	public LoadJobModel getHeader()	{
		LoadJobModel model = new LoadJobModel();
		
		model.setDateLoaded(getElementValue(dateLoaded()).toString());
		model.setLoadedBy(getElementValue(loadedBy()).toString());
		model.setState(getElementValue(state()).toString());
		model.setJobID(getElementValue(jobID()).toString());
		model.setUploadType(getElementValue(uploadType()).toString());
		model.setStatus(getElementValue(status()).toString());
		model.setFileLoaded(getElementValue(fileLoaded()).toString());
		return model;
	}
}
