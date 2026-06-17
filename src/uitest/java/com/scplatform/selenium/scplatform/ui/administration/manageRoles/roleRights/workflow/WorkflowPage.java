/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.workflow;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class WorkflowPage extends SCPlatformPage {

	public By tableLocator()	{
		return By.id("workflowRights");
	}
	
	public WebElement executeAnyWorkflow()	{
		return getElement(By.name("workflowAccess(*)"));
	}

	
	public WebElement main()	{
		return getElement(By.name("workflowAccess(MAIN)"));
	}

	public WebElement main_Dashboard()	{
		return getElement(By.name("workflowAccess(MAIN_DASHBOARD)"));
	}

	public WebElement main_Upload()	{
		return getElement(By.name("workflowAccess(MAIN_UPLOAD)"));
	}

	public WebElement main_ManageUploadJobs()	{
		return getElement(By.name("workflowAccess(MAIN_LOADJOB)"));
	}

	public WebElement main_ReviewAlerts()	{
		return getElement(By.name("workflowAccess(MAIN_ALERTS)"));
	}

	public WebElement openMain(){
		WebElement row = tableRowContainingText(tableLocator(), "Main", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}
	

	public WebElement costing()	{
		return getElement(By.name("workflowAccess(SL)"));
	}

	public WebElement costing_NewSourcingLane()	{
		return getElement(By.name("workflowAccess(NEW_SL)"));
	}

	public WebElement costing_SearchSourcingLane()	{
		return getElement(By.name("workflowAccess(SEARCH_SL)"));
	}

	public WebElement costing_SearchCostRecords()	{
		return getElement(By.name("workflowAccess(SEARCH_CR)"));
	}

	public WebElement costing_NewCommodityCodeCostRecord()	{
		return getElement(By.name("workflowAccess(NEW_ICCR)"));
	}

	public WebElement costing_SearchCommodityCodeCostRecords()	{
		return getElement(By.name("workflowAccess(SEARCH_ICCR)"));
	}

	public WebElement openCosting(){
		WebElement row = tableRowContainingText(tableLocator(), "Costing", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}
	

	public WebElement rebates()	{
		return getElement(By.name("workflowAccess(RP)"));
	}

	public WebElement rebates_NewRebateProgram()	{
		return getElement(By.name("workflowAccess(NEW_RP)"));
	}

	public WebElement rebates_SearchRebateProgram()	{
		return getElement(By.name("workflowAccess(SEARCH_RP)"));
	}

	public WebElement openRebates(){
		WebElement row = tableRowContainingText(tableLocator(), "Rebates", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}
	
	
	public WebElement supplyAllocation()	{
		return getElement(By.name("workflowAccess(SA)"));
	}

	public WebElement supplyAllocation_NewSupplyAllocation()	{
		return getElement(By.name("workflowAccess(VIEW_SA)"));
	}

	public WebElement supplyAllocation_SearchSupplyAllocation()	{
		return getElement(By.name("workflowAccess(SEARCH_SA)"));
	}

	public WebElement openSupplyAllocation(){
		WebElement row = tableRowContainingText(tableLocator(), "Supply Allocation", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}
	
	
	public WebElement masterDataManagement()	{
		return getElement(By.name("workflowAccess(MDM)"));
	}

	public WebElement masterDataManagement_ItemAssignment()	{
		return getElement(By.name("workflowAccess(MDM_ITEM)"));
	}

	public WebElement masterDataManagement_CommodityManagement()	{
		return getElement(By.name("workflowAccess(MDM_ICAT)"));
	}

	public WebElement masterDataManagement_BOMManagement()	{
		return getElement(By.name("workflowAccess(MDM_BOM)"));
	}

	public WebElement openMasterDataManagement(){
		WebElement row = tableRowContainingText(tableLocator(), "Master Data Management", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}
	

	public WebElement forecast()	{
		return getElement(By.name("workflowAccess(FC)"));
	}

	public WebElement forecast_NewForecast()	{
		return getElement(By.name("workflowAccess(NEW_FC)"));
	}

	public WebElement forecast_SearchForecast()	{
		return getElement(By.name("workflowAccess(SEARCH_FC)"));
	}

	public WebElement openForecast(){
		WebElement row = tableRowContainingText(tableLocator(), "Forecast", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}
	
	
	public WebElement reports()	{
		return getElement(By.name("workflowAccess(RPT)"));
	}

	public WebElement reports_SubmitViewReports()	{
		return getElement(By.name("workflowAccess(RPT_SUBMIT_JOB)"));
	}

	public WebElement reports_SellPriceReport()	{
		return getElement(By.name("workflowAccess(RPT_COST)"));
	}

	public WebElement openReports(){
		WebElement row = tableRowContainingText(tableLocator(), "Reports", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}
	
	
	public WebElement search()	{
		return getElement(By.name("workflowAccess(SEARCH)"));
	}

	public WebElement search_Items()	{
		return getElement(By.name("workflowAccess(SEARCH_ITEM_ONLY)"));
	}

	public WebElement search_ItemAVL()	{
		return getElement(By.name("workflowAccess(SEARCH_ITEM)"));
	}

	public WebElement search_BOMs()	{
		return getElement(By.name("workflowAccess(SEARCH_BOM)"));
	}

	public WebElement openSearch(){
		WebElement row = tableRowContainingText(tableLocator(), "Search", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}

	
	public WebElement administration()	{
		return getElement(By.name("workflowAccess(ADMIN)"));
	}

	public WebElement administration_EditProfile()	{
		return getElement(By.name("workflowAccess(EDIT_PROFILE)"));
	}

	public WebElement administration_ChangeDashboardNews()	{
		return getElement(By.name("workflowAccess(EDIT_NEWS)"));
	}

	public WebElement administration_ManageAlerts()	{
		return getElement(By.name("workflowAccess(EDIT_ALERT_SUB)"));
	}

	public WebElement administration_ManageItems()	{
		return getElement(By.name("workflowAccess(EDIT_ITEM)"));
	}

	public WebElement administration_ManageRoles()	{
		return getElement(By.name("workflowAccess(EDIT_ROLE)"));
	}

	public WebElement administration_ManageContacts()	{
		return getElement(By.name("workflowAccess(EDIT_CONTACT)"));
	}

	public WebElement administration_ManageBusinessEntities()	{
		return getElement(By.name("workflowAccess(EDIT_BE)"));
	}

	public WebElement administration_ManageUsers()	{
		return getElement(By.name("workflowAccess(EDIT_USER)"));
	}

	public WebElement administration_AuditHistory()	{
		return getElement(By.name("workflowAccess(VIEW_AUDIT)"));
	}

	public WebElement administration_AdminUpload()	{
		return getElement(By.name("workflowAccess(ADMIN_UPLOAD)"));
	}

	public WebElement openAdministration(){
		WebElement row = tableRowContainingText(tableLocator(), "Administration", COMPARE.Contains);
		return row.findElement(By.tagName("img"));
	}
	
	
}
