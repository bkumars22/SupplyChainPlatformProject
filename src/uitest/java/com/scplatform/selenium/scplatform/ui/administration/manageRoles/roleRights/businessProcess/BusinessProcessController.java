/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessProcess;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.RoleRightsController;

public class BusinessProcessController extends RoleRightsController {

	@Override
	public PageImpl getView() {
		return new BusinessProcessPage();
	}
	
	@Override
    public void edit() throws Exception {
		JLog.test("Setting Business Process Rights for this role");

        super.assertModelsExist();
        clickTabBusinessProcess();
        for (Model model : models) {
            populateValues(model);
            clickSave();
            handlePostErrors();
        }
    }
	
}
