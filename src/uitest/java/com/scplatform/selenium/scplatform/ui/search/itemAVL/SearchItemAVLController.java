/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.itemAVL;

import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchController;

public class SearchItemAVLController extends SCPlatformSearchController {

	@Override
	public PageImpl getView() {
		return new SearchItemAVLPage();
	}

}
