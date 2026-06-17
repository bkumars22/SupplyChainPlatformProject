/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob;

import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;

public class LoadJobDetailsController extends SCPlatformController {
	private LoadJobDetailsPage page;
	
	public LoadJobDetailsController()	{
		super();
		page = new LoadJobDetailsPage();
	}
	
	@Override
	public PageImpl getView() {
		return new LoadJobDetailsPage();
	}

	public void print()	{
		List<LoadJobDetailsModel> tableData = page.parseResults();
		
		JLog.section("Load Job Details");
		for (LoadJobDetailsModel model : tableData)	{
			write(model);
		}
	}
	
	private void write(LoadJobDetailsModel model)	{
		JLog.write(String.format("%s = %s", model.getDisplayName("type"), model.getType()));
		JLog.write(String.format("%s = %s", model.getDisplayName("message"), model.getMessage()));
		JLog.write(String.format("%s = %s", model.getDisplayName("location"), model.getLocation()));
		JLog.write(String.format("%s = %s", model.getDisplayName("dateLoaded"), model.getDateLoaded()));
		JLog.blankLine();
	}
}
