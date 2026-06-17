/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLane;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchController;
import com.test.selenium.scplatform.ui.costing.searchCostRecords.SearchCostRecordsResultsModel;
import com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.details.SupplierAllocationDetailsController;
import com.test.selenium.scplatform.utilities.DatabaseUtils;

/**
 * Navigation: Supply Allocation -> Search Supply Allocation
 * <br>Handles doing the search
 * 
 * @see #setModel(com.test.selenium.common.modelViewController.model.Model)
 * @see #setModels(java.util.List)
 * @see #search()
 */
public class SearchSupplierAllocationController extends SCPlatformSearchController {
	
	@Override
	public PageImpl getView() {
		return new SearchSupplierAllocationPage();
	}

	/**
	 * Uses the expectedData to search on each item. Once the search is done, the results
	 * are validated against the expectedData.  if validateDetails=true, then the item radio 
	 * button is then selected and {@link SupplierAllocationDetailsController#validate(SupplierAllocation)} 
	 * is called.
	 * 
	 * @param expectedData		List of {@link SupplierAllocation} data to validate
	 * @param validateDetails	True to select the item radio button and validate the detail page.
	 * @return					True if search and validation successful.
	 */
	public boolean validate(List<SupplierAllocation> expectedData, boolean validateDetails){
		boolean success = true;
		boolean verified = true;
		
		for (SupplierAllocation data : expectedData){
			
			JLog.section("Verify Item = " + data.getCustomerItemIdentifier());
			
			search(data);
			
			SearchSupplierAllocationResultsController c = new SearchSupplierAllocationResultsController();
			verified = c.validate(data, validateDetails);
			success = (verified) ? success : verified;
		}
		
		return success;
	}
	
	protected void search(SupplierAllocation supplierAllocationData)	{
		SearchSupplierAllocationModel searchModel = new SearchSupplierAllocationModel();
		searchModel.setItemNumber(supplierAllocationData.getCustomerItemIdentifier());
		searchModel.setSupplier(supplierAllocationData.getSupplierBusinessEntity());
		
		SearchSupplierAllocationController c = new SearchSupplierAllocationController();
		c.clickClear();
		c.setModel(searchModel);
		c.search();
	}
}
