/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.workflow;

import java.lang.reflect.Field;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.annotations.AutoPopulateOff;
import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.RoleRightsController;

public class WorkflowController extends RoleRightsController {

	@Override
	public PageImpl getView() {
		return new WorkflowPage();
	}
	
	@Override
    public void edit() throws Exception {
		JLog.test("Setting Workflows for this role");
        super.assertModelsExist();
        clickTabWorkflow();
        for (Model model : models) {
            populateValues(model);
            clickSave();
            handlePostErrors();
        }
    }
	
	// writes values to the page
	@Override
    public void populateValues(Model model) throws Exception {
    	try	{
	        for (Field field : getFields(model)) {
	            String name = field.getName();
	            openSectionIfNeeded(model, name);
	            if (field.getAnnotation(AutoPopulateOff.class) == null) {
	                Object value = getValue(model, name);
	                setElementValue(name, value);
	            }
	        }
    	} catch (Exception e)	{
    		JLog.fail(e);
    	}
    }
	
	private void openSectionIfNeeded(Model model, String fieldName){
		WorkflowPage page = new WorkflowPage();
		WorkflowModel workflowModel = (WorkflowModel) model;
		
		if ("main".equals(fieldName))	{
			if (workflowModel.open_main()) page.openMain();
		} else if ("costing".equals(fieldName))	{
			if (workflowModel.open_costing()) page.openCosting();
		} else if ("rebates".equals(fieldName))	{
			if (workflowModel.open_rebates()) page.openRebates();
		} else if ("supplyAllocation".equals(fieldName))	{
			if (workflowModel.open_supplyAllocation()) page.openSupplyAllocation();
		} else if ("masterDataManagement".equals(fieldName))	{
			if (workflowModel.open_masterDataManagement()) page.openMasterDataManagement();
		} else if ("forecast".equals(fieldName))	{
			if (workflowModel.open_forecast()) page.openForecast();
		} else if ("reports".equals(fieldName))	{
			if (workflowModel.open_reports()) page.openReports();
		} else if ("search".equals(fieldName))	{
			if (workflowModel.open_search()) page.openSearch();
		} else if ("administration".equals(fieldName))	{
			if (workflowModel.open_administration()) page.openAdministration();
		}
	}
}
