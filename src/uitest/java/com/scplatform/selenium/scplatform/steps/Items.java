/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import org.apache.poi.ss.usermodel.Cell;
import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.messages.utilities.ExcelWriter;
import com.test.selenium.scplatform.ui.search.items.SearchItemsController;
import com.test.selenium.scplatform.ui.search.items.SearchItemsModel;
import com.test.selenium.scplatform.ui.search.items.SearchItemsResultsController;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import java.lang.Exception;
import io.cucumber.java.Scenario;

public class Items {

	SearchItemsController controller;

	@Before
	public void beforeMethod(Scenario scenario) {
		JLog.setScenarioForCucumber(scenario);
		JLog.resetErrorCount();
	}

	private void checkForErrors() {
		if (JLog.getErrorCount() > 0) {
			JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
		}
	}

	@And("I search for {string} item uploaded")
	public void selectAndClickBack(String itemCount) throws Exception {
		controller = new SearchItemsController();
		SearchItemsModel model = new SearchItemsModel();
		ExcelWriter writer = new ExcelWriter();
		Cell cell1 = writer.getCell(1, 0);
		if (itemCount.equals("multiple")) {
			Cell cell2 = writer.getCell(2, 0);
			model.setMultipleItemNumbers(cell1.toString() + "," + cell2.toString());
		} else {
			model.setItemNumber(cell1.toString());
		}
		controller.setModel(model);
		controller.populateValues(model);
		checkForErrors();
	}
	
	@Then("I verify the items uploaded on search page")
	public void verifySearchItems() throws Exception {
		SearchItemsResultsController resultCrtller = new SearchItemsResultsController();
		//resultCrtller.
	}
	
}
