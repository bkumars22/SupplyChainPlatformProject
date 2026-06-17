/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.ui;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.SearchSupplierAllocationController;
import com.test.selenium.scplatform.utilities.MessageIO;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;

public class SupplyAllocationSteps {

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
	
	
	/**
	 * http://confluence.dev.scplatform.local/display/QA/Search+Supply+Allocation
	 */
	@Given("I validate the {string} Supplier Allocation Data")
	public void validateSupplierAllocation(String supplierAllocationSaveKey){
		MessageIO<SupplierAllocation> messageIO = new MessageIO<SupplierAllocation>(SupplierAllocation.class);
		
		nav.SearchSupplyAllocation();
		
		SearchSupplierAllocationController c = new SearchSupplierAllocationController();
		c.validate(messageIO.load(supplierAllocationSaveKey), true);
		
		checkForErrors();
	}
}
