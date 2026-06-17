/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.ui;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntity;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.administration.manageBusinessEntities.ManageBusinessEntitiesResultsController;
import com.test.selenium.scplatform.utilities.MessageIO;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;

public class ManageBusinessEntitiesSteps {

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
	
	@Then("I validate the {string} BusinessEntity Master Data")
	public void validateBusinessEntity(String businessEntityMsgKey){
		MessageIO<BusinessEntity> messageIO = new MessageIO<BusinessEntity>(BusinessEntity.class);
		
		nav.ManageBusinessEntities();
		
		ManageBusinessEntitiesResultsController c = new ManageBusinessEntitiesResultsController();
		c.validate(messageIO.load(businessEntityMsgKey));
				
		checkForErrors();
	}
}
