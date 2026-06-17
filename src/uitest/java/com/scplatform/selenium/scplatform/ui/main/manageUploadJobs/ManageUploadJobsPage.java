/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class ManageUploadJobsPage extends SCPlatformSearchPage {

	public WebElement loadedBy()	{
		return getElement(By.name("value(loadedBy)"));
	}

	public WebElement dateLoaded()	{
		return getElement(By.name("dateValue(loadDate)"));
	}

	public WebElement jobID()	{
		return getElement(By.name("value(jobId)"));
	}

	public WebElement status()	{
		return getElement(By.name("values(status)"));
	}

	public WebElement state()	{
		return getElement(By.name("values(state)"));
	}

	public WebElement fileLoaded()	{
		return getElement(By.name("value(datasource)"));
	}

	
}
