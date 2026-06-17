/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController.CostRecords;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.modelViewController.MTCMView;

public class WAPCostRecordsView extends MTCMView {

	@Override
	public void setContext() {
		if (getOverrideContext() == null) {
			String[] frames = new String[1];
			frames[0] = "iframe[id='contentFrame']";
			setFrame(frames);
		} else {
			String[] frames = { "iframe[name='contentFrame']", "iframe[id='mainModalFrame']" };
			setFrame(frames);
		}
	}

	public WebElement getMaterial(String row) {
		return get(By.xpath("//tr[" + row + "]//input[contains(@name,'value(MATERIAL)')]"));

	}
	
	public WebElement getedit(String buttonName) {
		return get(By.xpath("//span[contains(@class,'md-icon') and contains(text(),'" + buttonName + "')]"));

	}
	
	
	public WebElement getWAPTab(String tab) {
		return get(By.xpath("//tbody/tr[1]/td[10]//a[contains(text(),'" + tab + "')]"));
	}

	public List<WebElement> getMaterialValue() {
		return getList(By.xpath("//tbody[@id='crTableBody']//input[contains(@id,'costValue1_rng')]"));
	}
	
	public WebElement getFGId(String row) {
        return get(By.xpath("//tr[" + row + "]//input[contains(@id,'numberAttribute1')]"));
    }
	
	public WebElement getSearchResultsXlobFlexLOBField(String xlobFGLob) {
		return get(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//td[50][contains(text(),'" + xlobFGLob + "')]"));
		
				
	}

	public WebElement getSearchResultsXlobPlatformBField(String xlobPlatform) {
		return get(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//td[49][contains(text(),'" + xlobPlatform + "')]"));
		
	}

	public WebElement getSearchResultsXlobFGNameBField(String xlobFGName) {
		return get(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//td[48][contains(text(),'" + xlobFGName + "')]"));
		
	}

	
	public WebElement setXLOBFGNameField() {
        return getElement(By.xpath(
                "//*[contains(@name,'value(xlobFGNames)')]"));
    }
}
