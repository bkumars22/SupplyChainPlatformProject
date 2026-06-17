/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

/**
 * Navigation: Administration -> Manage Roles
 * 
 * @author dgenrich
 */
public class ManageRolesPage extends SCPlatformPage {

	public WebElement findRow(String roleID){
		return tableRowContainingText(tableLocator(), roleID, COMPARE.Equals);
	}
	
	public WebElement button_CreateRole()	{
		return getElement(By.partialLinkText("Create Role"));
	}
	
	public WebElement button_Download()	{
		return getElement(By.partialLinkText("Download"));
	}
	
	public WebElement button_Cancel()	{
		return getElement(By.partialLinkText("Cancel"));
	}
	
	public By tableLocator()	{
		return By.id("roleList_data");
	}
	
}
