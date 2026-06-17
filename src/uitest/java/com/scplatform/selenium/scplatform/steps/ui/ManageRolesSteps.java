/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.ui;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.administration.manageRoles.ManageRolesController;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.administrator.AdministratorController;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.administrator.AdministratorModel;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessDocument.BusinessDocumentController;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessDocument.BusinessDocumentModel;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessProcess.BusinessProcessController;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessProcess.BusinessProcessModel;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.workflow.WorkflowController;
import com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.workflow.WorkflowModel;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;

public class ManageRolesSteps {

	protected SCPlatformNavigation nav;
	
	@Before
	public void beforeMethod(Scenario scenario){
		JLog.setScenarioForCucumber(scenario);
		JLog.resetErrorCount();
		nav = new SCPlatformNavigation();
	}

	private void checkForErrors()	{
		if (JLog.getErrorCount() > 0){
			JLog.fail(JLog.getErrorCount() + " errors occured in the test.  Check log.", TakeScreenshot.True);
		}
	}
	
	@Then("I assign all access to Role ID {string}")
	public void assignAllAccess(String roleID) throws Exception{
		
		nav.ManageRoles();
		
		ManageRolesController manageRolesController = new ManageRolesController();
		manageRolesController.select(roleID);
		
		// ------------------
		WorkflowModel workflowModel = new WorkflowModel();
		workflowModel.setExecuteAnyWorkflow("true");
		
		WorkflowController workflowController = new WorkflowController();
		workflowController.setModel(workflowModel);
		workflowController.edit();
		
		// ------------------
		BusinessDocumentModel businessDocumentModel = new BusinessDocumentModel();
		businessDocumentModel.setBillOfMaterial_setAll("true");
		businessDocumentModel.setCostRecord_setAll("true");
		businessDocumentModel.setForecast_setAll("true");
		businessDocumentModel.setItemAssignment_setAll("true");
		businessDocumentModel.setItemCommodity_setAll("true");
		businessDocumentModel.setRebate_setAll("true");
		businessDocumentModel.setReports_setAll("true");
		businessDocumentModel.setSourcingLane_setAll("true");
		businessDocumentModel.setSupplyAllocation_setAll("true");
		businessDocumentModel.setUploadAndDownload_setAll("true");
		businessDocumentModel.setUploadDocuments_setAll("true");
		
		BusinessDocumentController businessDocumentController = new BusinessDocumentController();
		businessDocumentController.setModel(businessDocumentModel);
		businessDocumentController.edit();
		
		// ------------------
		BusinessProcessModel businessProcessModel = new BusinessProcessModel();
		businessProcessModel.setBom_setAll("true");
		businessProcessModel.setForecast_setAll("true");
		businessProcessModel.setForecastADJ_setAll("true");
		businessProcessModel.setRebate_setAll("true");
		businessProcessModel.setSourcing_setAll("true");
		
		BusinessProcessController businessProcessController = new BusinessProcessController();
		businessProcessController.setModel(businessProcessModel);
		businessProcessController.edit();
		
		// ------------------
		AdministratorModel administratorModel = new AdministratorModel();
		administratorModel.setChangeAssignmentRules("true");
		administratorModel.setChangeBusiness("true");
		administratorModel.setChangeComplianceRules("true");
		administratorModel.setChangeContact("true");
		administratorModel.setChangeDashboardNewsAlertItems("true");
		administratorModel.setChangeItems("true");
		administratorModel.setChangeRole("true");
		administratorModel.setChangeUser("true");
		administratorModel.setCreateAssignmentRules("true");
		administratorModel.setCreateComplianceRules("true");
		administratorModel.setCreateContact("true");
		administratorModel.setCreateRole("true");
		administratorModel.setDataManagementUpload("true");
		administratorModel.setEnterpriseWideVisibility("true");
		administratorModel.setViewAssignmentRules("true");
		administratorModel.setViewBusiness("true");
		administratorModel.setViewComplianceRules("true");
		administratorModel.setViewContact("true");
		administratorModel.setViewRole("true");
		administratorModel.setViewUser("true");
		
		AdministratorController administratorController = new AdministratorController();
		administratorController.setModel(administratorModel);
		administratorController.edit();
		
		checkForErrors();
	}
}
