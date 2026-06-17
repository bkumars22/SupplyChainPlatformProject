/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.administrator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class AdministratorPage extends SCPlatformPage {

	public WebElement viewRole()	{
		return getElement(By.id("ReadRole"));
	}

	public WebElement viewUser()	{
		return getElement(By.id("ReadUser"));
	}

	public WebElement viewContact()	{
		return getElement(By.id("ReadContact"));
	}

	public WebElement viewBusiness()	{
		return getElement(By.id("ReadBusiness"));
	}

	public WebElement viewAssignmentRules()	{
		return getElement(By.id("ReadARule"));
	}

	public WebElement viewComplianceRules()	{
		return getElement(By.id("ReadCRule"));
	}

	public WebElement changeDashboardNewsAlertItems()	{
		return getElement(By.id("AdminAlert"));
	}

	public WebElement changeItems()	{
		return getElement(By.id("SaveItem"));
	}

	public WebElement enterpriseWideVisibility()	{
		return getElement(By.id("GlobalVisibility"));
	}

	public WebElement changeRole()	{
		return getElement(By.id("SaveRole"));
	}

	public WebElement changeUser()	{
		return getElement(By.id("SaveUser"));
	}

	public WebElement changeContact()	{
		return getElement(By.id("SaveContact"));
	}

	public WebElement changeBusiness()	{
		return getElement(By.id("SaveBusiness"));
	}

	public WebElement changeAssignmentRules()	{
		return getElement(By.id("SaveARule"));
	}

	public WebElement changeComplianceRules()	{
		return getElement(By.id("SaveCRule"));
	}

	public WebElement dataManagementUpload()	{
		return getElement(By.id("AdminUploadFile"));
	}

	public WebElement createRole()	{
		return getElement(By.id("CreateRole"));
	}

	public WebElement createContact()	{
		return getElement(By.id("CreateContact"));
	}

	public WebElement createAssignmentRules()	{
		return getElement(By.id("CreateARule"));
	}

	public WebElement createComplianceRules()	{
		return getElement(By.id("CreateCRule"));
	}

	
}
