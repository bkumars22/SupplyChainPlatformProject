/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.bomManagement;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

/**
 * View (Page Object) for BOM Management UI actions.
 */
public class BomManagementPage extends SCPlatformSearchPage {
	private WebDriver driver;

	public BomManagementPage() {
		this.driver = driver;
	}

	public WebElement itemNumber() {
		return getElement(By.name("value(itemNumber)"));
	}

	public WebElement revision() {
		return getElement(By.name("value(revisionNumber)"));
	}

	public WebElement topLevelItem() {
		return getElement(By.name("value(topLevel)"));
	}

	public WebElement multipleItemNumbers() {
		return getElement(By.name("value(itemNumbers)"));
	}

	public WebElement status() {
		return getElement(By.name("values(status)"));
	}

	public WebElement replacePending() {
		return getElement(By.name("value(mergeStatus)"));
	}

	public WebElement assigned() {
		return getElement(By.name("value(isAssigned)"));
	}

	public WebElement responsibility() {
		return getElement(By.name("value(owner)"));
	}

	public WebElement assignedTo() {
		return getElement(By.name("value(user)"));
	}

	public WebElement platform() {
		return getElement(By.name("value(platformName)"));
	}

	public WebElement usedAsSubassembly() {
		return getElement(By.name("value(subassembly)"));
	}

	public WebElement repairs() {
		return getElement(By.name("value(isRepairs)"));
	}

	public WebElement getBOMption() {
		return getElement(By.xpath("//select[@id='messageType']/option[text()='Bom(*.xlsx)']"));
	}

}
