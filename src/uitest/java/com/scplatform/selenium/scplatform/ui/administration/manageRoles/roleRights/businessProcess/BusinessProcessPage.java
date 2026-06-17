/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessProcess;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class BusinessProcessPage extends SCPlatformPage {

	public WebElement sourcing_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'Sourcing')]"));
	}

	public WebElement bom_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'setAll') and contains(@href, 'event') and contains(@href, 'BOM')]"));
	}

	public WebElement forecastADJ_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'Forecast_ADJ')]"));
	}

	public WebElement forecast_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'setAll') and contains(@href, 'Forecast') and not(contains(@href, 'Forecast_ADJ'))]"));
	}

	public WebElement rebate_setAll()	{
		return getElement(By.xpath("//a[contains(@href, 'Rebate')]"));
	}

	
}
