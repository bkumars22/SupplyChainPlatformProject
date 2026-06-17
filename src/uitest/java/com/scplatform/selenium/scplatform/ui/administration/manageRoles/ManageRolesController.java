/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.AbstractPage.COMPARE;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;

/**
 * Navigation: Administration -> Manage Roles
 * 
 * @author dgenrich
 */
public class ManageRolesController extends SCPlatformController {

	@Override
	public PageImpl getView() {
		return new ManageRolesPage();
	}

	public void clickCreateRole()	{
		ManageRolesPage page = new ManageRolesPage();
		clickAndCheckForPOSTError(page.button_CreateRole());
	}
	
	public void clickDownload()	{
		ManageRolesPage page = new ManageRolesPage();
		clickAndCheckForPOSTError(page.button_Download());
	}
	
	public void clickCancel()	{
		ManageRolesPage page = new ManageRolesPage();
		clickAndCheckForPOSTError(page.button_Cancel());
	}
	
	public void select(String roleId)	{
		ManageRolesPage page = new ManageRolesPage();
		WebElement row = page.findRow(roleId);
		WebElement selectedRoleKey = row.findElement(By.name("selectedRoleKey"));
		page.setElementValue(selectedRoleKey, true);
	}
}
