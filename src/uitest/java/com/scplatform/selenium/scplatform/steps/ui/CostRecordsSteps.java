/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.ui;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLane;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.costing.searchCostRecords.SearchCostRecordsResultsController;
import com.test.selenium.scplatform.utilities.MessageIO;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;

public class CostRecordsSteps {
	protected SCPlatformNavigation nav;
	
	@Before
	public void beforeMethod(Scenario scenario){
		JLog.setScenarioForCucumber(scenario);
		JLog.resetErrorCount();
		nav = new SCPlatformNavigation();
	}

	private void checkForErrors()	{
		if (JLog.getErrorCount() > 0){
			JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
		}
	}
	
	/**
	 * http://confluence.dev.scplatform.local/display/QA/Search+Cost+Records
	 */
	@Given("I validate the {string} Cost Records Data and {string} Item data")
	public void validateCostRecords(String sourcingLaneSaveKey, String itemSaveKey){
		MessageIO<SourcingLane> messageIOSourcingLane = new MessageIO<SourcingLane>(SourcingLane.class);
		MessageIO<Item> messageIOItem = new MessageIO<Item>(Item.class);
		
		nav.SearchCostRecords();
		SearchCostRecordsResultsController c = new SearchCostRecordsResultsController();
		c.validate(
				messageIOSourcingLane.load(sourcingLaneSaveKey),
				messageIOItem.load(itemSaveKey));
		
		checkForErrors();
	}
	
}
