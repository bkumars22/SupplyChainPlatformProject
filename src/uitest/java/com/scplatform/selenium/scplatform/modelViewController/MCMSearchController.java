/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import com.test.selenium.common.modelViewController.controller.BrowserSearchControllerImpl;
import com.test.selenium.common.modelViewController.model.Model;

public abstract class SCPlatformSearchController extends BrowserSearchControllerImpl {

	protected boolean navigateToDetails = false;
	
	@Override
	public String getErrorMessage() {
        SCPlatformPage page = new SCPlatformPage();
        return page.getErrorMessage();
	}


    public void populateValues(Model model) throws Exception {
    	super.setProcessSearch(false);	// handle the search button in the current method rather than the super method
    	
        super.populateValues(model);

        super.doSearch();
    }
    
	public void navigateToDetails(boolean goToDetails)	{
		navigateToDetails = goToDetails;
	}
	
	public void clickClear()	{
		SCPlatformPage page = new SCPlatformPage();
		clickAndCheckForPOSTError(page.clearButton());
	}
	
}
