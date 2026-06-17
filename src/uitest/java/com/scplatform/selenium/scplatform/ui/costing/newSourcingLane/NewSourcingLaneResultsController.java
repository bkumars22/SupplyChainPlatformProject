/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.newSourcingLane;

import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;

public class NewSourcingLaneResultsController extends SCPlatformSearchResultsController {
    private NewSourcingLaneResultsPage page;

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new NewSourcingLaneResultsPage();
        }
        return page;
    }

    public void select(String itemNumber) {
        getView();
        select(itemNumber, page.tableLocator());
    }

}
