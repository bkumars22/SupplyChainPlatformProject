/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchCommodityCode;

import org.openqa.selenium.By;

import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchController;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;

public class SearchCommodityCodeController extends SCPlatformSearchController {

	@Override
	public PageImpl getView() {
		return new SearchCommodityCodePage();
	}

    public void populateValues(Model model) throws Exception {
    	By tableLocator = By.id("iccrSummarySearchResultTable_data");
    	super.setProcessSearch(false);	// handle the search button in the current method rather than the super method
    	
        super.populateValues(model);

        super.doSearch();
        
        if (navigateToDetails){
        	SCPlatformSearchResultsController searchResults = new SCPlatformSearchResultsController();
        	searchResults.select(((SearchCommodityCodeModel) model).getCommodityName(), tableLocator);
        }
    }
    
}
