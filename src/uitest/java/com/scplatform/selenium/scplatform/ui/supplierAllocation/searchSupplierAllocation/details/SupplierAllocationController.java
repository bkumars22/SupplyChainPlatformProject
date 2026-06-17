/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.details;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;

public class SupplierAllocationController extends SCPlatformController {
	private SupplierAllocationPage page;
	
	public SupplierAllocationController()	{
		super();
		page = new SupplierAllocationPage();
	}
	
	@Override
	public PageImpl getView() {
		return new SupplierAllocationPage();
	}

	public void clickShowHistory()	{
		clickAndCheckForPOSTError(page.button_ShowHistory());
	}
	
	public void clickRefresh()	{
		clickAndCheckForPOSTError(page.button_Refresh());
	}
	
	public void clickCopy()	{
		clickAndCheckForPOSTError(page.button_Copy());
	}
	
	/**
	 * Verifies the Supplier Allocation Header data
	 * @param expectedData	{@link SupplierAllocation} data to validate
	 * @return				True if validation successful.
	 */
	public boolean validate(SupplierAllocation expectedData){
		boolean success = true;
		boolean verified = true;
		
		JLog.section("Validate Supplier Allocation Header - " + expectedData.getCustomerItemIdentifier());
		
		SupplierAllocationModel actual = page.getHeader();
		
		verified = verify(actual.getDisplayName("itemNumber"), actual.getItemNumber(), expectedData.getCustomerItemIdentifier());
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("periodType"), actual.getPeriodType(), "Month");
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("itemDescription"), actual.getItemDescription(), expectedData.getDescription());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("periodStart"), actual.getPeriodStart(), expectedData.getEffectiveFromDate());
		success = (verified) ? success : verified;
		
		// can not verify period end date.  This is only used for Copy function
		// https://jira.dev.scplatform.local/jira/browse/SSP-11203
//		verified = verify(actual.getDisplayName("periodEnd"), actual.getPeriodStart(), expectedData.getEffectiveToDate());
//		success = (verified) ? success : verified;
		
		JLog.blankLine();
		return success;
	}
	
}
