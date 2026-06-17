/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class RoleRightsPage extends SCPlatformPage {

	public WebElement button_Save()	{
		return getElement(By.partialLinkText("Save"));
	}
	
	public WebElement button_DeleteRole()	{
		return getElement(By.partialLinkText("Delete Role"));
	}
	
	public WebElement button_CopyRole()	{
		return getElement(By.partialLinkText("Copy Role"));
	}
	
	public WebElement button_Cancel()	{
		return getElement(By.partialLinkText("Cancel"));
	}
	
	public WebElement tab_Workflow()	{
		return getElement(By.name("workflowtab"));
	}
	
	public WebElement tab_BusinessDocument()	{
		return getElement(By.name("botypetab"));
	}
	
	public WebElement tab_BusinessProcess()	{
		return getElement(By.name("eventtab"));
	}
	
	public WebElement tab_BusinessData()	{
		return getElement(By.name("datatab"));
	}
	
	public WebElement tab_Administrator()	{
		return getElement(By.name("admintab"));
	}
	
	
}
