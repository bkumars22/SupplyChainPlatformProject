/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights;

import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;

public class RoleRightsController extends SCPlatformController {

	@Override
	public PageImpl getView() {
		return new RoleRightsPage();
	}

	
	public void clickCancel()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.button_Cancel());
	}
	
	public void clickCopyRole()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.button_CopyRole());
	}
	
	public void clickDeleteRole()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.button_DeleteRole());
	}
	
	public void clickSave()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.button_Save());
	}
	
	public void clickTabAdministrator()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.tab_Administrator());
	}
	
	public void clickTabBusinessData()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.tab_BusinessData());
	}
	
	public void clickTabBusinessDocument()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.tab_BusinessDocument());
	}
	
	public void clickTabBusinessProcess()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.tab_BusinessProcess());
	}
	
	public void clickTabWorkflow()	{
		RoleRightsPage page = new RoleRightsPage();
		clickAndCheckForPOSTError(page.tab_Workflow());
	}

	
}
