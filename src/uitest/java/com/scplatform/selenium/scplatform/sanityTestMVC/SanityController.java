/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.sanityTestMVC;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.common.unity.visual.Loading;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.modelViewController.MTCMView;
import com.test.selenium.scplatform.steps.General;
import com.google.common.base.Verify;

public class SanityController extends MTCMController {

	MTCMView view;
	int maxSecondsToWait = 60;

	@Override
	public PageImpl getView() {
		view = new MTCMView();
		return view;
	}

	public void verifyDashboardSubSectionElements() {
		List<WebElement> elements = view.getList(By.xpath("//div[contains(@id,'eto-card')]"));
		int rowCount = elements.size();
		Verify.verify(rowCount==9||rowCount==8,"sub sections missing on dashboard page.");
		for(WebElement ele: elements) {
			Verify.verify(ele.isDisplayed(),"Subsection not visible on dashboard page.");
		}
	}
	
	public void verifyDashboardTitle() {
		boolean status = view.get(By.xpath("//span[@class='eto-dashboard__title scplatform-dashboard__title']")).isDisplayed();
		Verify.verify(status,"Dashboard title not displayed.");
	}
	
	public void verifyRebatesPage() {
		boolean status = view.get(By.id("rpHeader")).isDisplayed();
		Verify.verify(status,"New Rebates Page is not loaded.");
	}
	
}
