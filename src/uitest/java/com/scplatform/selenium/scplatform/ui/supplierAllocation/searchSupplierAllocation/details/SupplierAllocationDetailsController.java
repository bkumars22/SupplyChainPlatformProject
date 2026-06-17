/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.details;

import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;
import com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.SearchSupplierAllocationController;
import com.test.selenium.scplatform.utilities.DatabaseUtils;

public class SupplierAllocationDetailsController extends SCPlatformController {
	private SupplierAllocationDetailsPage page;
	
	public SupplierAllocationDetailsController()	{
		super();
		page = new SupplierAllocationDetailsPage();
	}
	
	@Override
	public PageImpl getView() {
		return new SupplierAllocationDetailsPage();
	}

	/**
	 * Validates the Supplier Allocation Details line data.
	 * 
	 * @param expectedData	{@link SupplierAllocation} data to validate
	 * @return				True if validation successful.
	 * 
	 * @see SupplierAllocationController#validate(SupplierAllocation)
	 * @see SearchSupplierAllocationController#validate(List, boolean)
	 */
	public boolean validate(SupplierAllocation expectedData){
		boolean success = true;
		boolean verified = true;
		
		JLog.section("Validate Supplier Allocation Detail Lines - " + expectedData.getSupplierItemIdentifier());
		
		List<SupplierAllocationDetailsModel> actualData = page.parseResults();
		
		SupplierAllocationDetailsModel actual = findActual(actualData, expectedData.getSupplierItemIdentifier());
		if (actual == null)	{
			JLog.error(
					String.format("Unable to find Supplier Item '%s'", expectedData.getSupplierItemIdentifier()), 
					TakeScreenshot.True);
			return false;  // ← STOP HERE, don't continue!
		}

		
		verified = verify(actual.getDisplayName("supplierItemNumber"), actual.getSupplierItemNumber(), expectedData.getSupplierItemIdentifier());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("supplier"), actual.getSupplier(), expectedData.getSupplierBusinessEntity());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("supplierSite"), actual.getSupplierSite(), DatabaseUtils.getSiteName(expectedData.getSupplierSite()));
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("allocation"), actual.getAllocation(), expectedData.getAllocation());
		success = (verified) ? success : verified;

		// Unknown where Price is coming from.  Currently empty in UI
//		verified = verify(actual.getDisplayName("price"), actual.getPrice(), expectedData.get);
//		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("description"), actual.getDescription(), expectedData.getCustomerItemDescription());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("periodStart"), actual.getPeriodStart(), expectedData.getEffectiveFromDate());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("periodEnd"), actual.getPeriodEnd(), expectedData.getEffectiveToDate());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("status"), actual.getStatus(), "APPROVED");
		success = (verified) ? success : verified;
		
		return success;
	}
	
	
	public void clickBack()	{
		clickAndCheckForPOSTError(page.button_Back());
	}
	
	public void clickDelete()	{
		clickAndCheckForPOSTError(page.button_Delete());
	}
	
	public void clickNew()	{
		clickAndCheckForPOSTError(page.button_New());
	}
	
	public void clickReload()	{
		clickAndCheckForPOSTError(page.button_Reload());
	}
	
	public void clickSave()	{
		clickAndCheckForPOSTError(page.button_Save());
	}
	
	public void clickSaveAndExit()	{
		clickAndCheckForPOSTError(page.button_SaveAndExit());
	}

	protected SupplierAllocationDetailsModel findActual(List<SupplierAllocationDetailsModel> actualModel, String supplierItemNumber) {
		for (SupplierAllocationDetailsModel actual : actualModel){
			if (actual.getSupplierItemNumber().equals(supplierItemNumber))	{
				return actual;
			}
		}
		return null;
	}
	
	
}
