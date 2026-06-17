/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.FunctionalGroupModel;
import com.test.selenium.scplatform.sanityTestMVC.SanityController;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;

public class SanityTests {

	SanityController controller;
	FunctionalGroupModel model;
	General genObj;

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
	
	@Then("I verify the sub sections are displayed on dashboard page")
	public void verifyDashboardSubSectionElements() {
		controller = new SanityController();
		controller.verifyDashboardSubSectionElements();
		checkForErrors();
	}

	@Then("I verify the Dashboard page title")
	public void verifyDashBrdTitle() {
		controller = new SanityController();
		controller.verifyDashboardTitle();
	}
	
	@Then("I verify the Rebates page title")
	public void verifyRebatesPage() {
		controller = new SanityController();
		controller.verifyRebatesPage();
	}
}
