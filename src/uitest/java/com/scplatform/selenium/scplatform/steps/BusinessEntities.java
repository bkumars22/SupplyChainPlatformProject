/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

//Author : Kumar

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.businessEntities.BusinessEntitiesController;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.ui.masterDataManagement.bomManagement.BomManagementController;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BusinessEntities {

	HarmonyLoginUI ui = new HarmonyLoginUI();
	MTCMController c;
	BusinessEntitiesController businessCtrl;
	int recordsCount;
	Prop prop = Prop.getInstance();

	@Before
	public void beforeMethod(Scenario scenario) {
		JLog.setScenarioForCucumber(scenario);
		JLog.resetErrorCount();
//		if (businessCtrl == null) {
//			businessCtrl = new BusinessEntitiesController();
//		}
	}

	public void checkForErrors() {
		if (JLog.getErrorCount() > 0) {
			JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
		}
	}

	@And("I click on Apply Button")
	public void applyBtn() throws InterruptedException {
		c = new MTCMController();
		c.clickButton("Apply");
		checkForErrors();
	}

	@And("I verified non enterprices items are displayed in the UI page")
	public void nonEterprisedItems() throws InterruptedException {
		businessCtrl.areNonEnterpriseRecordsPresent();
		businessCtrl.getNonEnterpriseRecordsCount();
		businessCtrl.printNonEnterpriseRecordNames();

		checkForErrors();

	}

	@When("I uploaded the XML file for the business entity and checked it on the UI page")
	public void uploadtheBusinessEntities() throws InterruptedException, IOException {
		AbstractPage page = new AbstractPage();
		WebElement insideFrame = page.get(By.xpath("//iframe[@name='contentFrame' and @id='contentFrame']"));
		AbstractPage.browserSession.getDriver().switchTo().frame(insideFrame);
		businessCtrl.selectBusinessEntityType();
		// Try to find the XML file in the test resources data directory
		String filePath = "src/test/resources/com/scplatform/selenium/scplatform/data/MTCM-Tenant-123_MTCM-Tenant-123_BusinessEntity_MCM1.0_1.xml";
		File fileName = new File(filePath);
		if (!fileName.exists()) {
			JLog.fail("Test data file not found: " + filePath, TakeScreenshot.True);
			throw new RuntimeException("Test data file not found: " + filePath);
		}
		businessCtrl.uploadBusinessEntityFile(fileName);
		JLog.write("Successfully clicked on the submit button " + fileName);
		// businessCtrl.validateSuccessMessage();
		checkForErrors();
	}

	@And("I entered the Business ID {string} in the text field")
	public void enterBusinessName(String businessId) throws InterruptedException {
		businessCtrl.enterBusinessId(businessId);
		checkForErrors();
	}

	@When("I uploaded the XLSX file for the business entity Item and checked it on the UI page")
	public void uploadItemBusinessEntities() throws Exception {
		AbstractPage page = new AbstractPage();
		String filePath = "src/test/resources/com/scplatform/selenium/scplatform/data/uploadItemForBusinessEntities.xlsx";
		BusinessEntitiesController.updateItemNumberAndTimestampInExcel(filePath);
		JLog.write("The file upload has been completed, and the following file is now displayed. " + filePath);
		WebElement insideFrame = page.get(By.xpath("//iframe[@name='contentFrame' and @id='contentFrame']"));
		AbstractPage.browserSession.getDriver().switchTo().frame(insideFrame);
		businessCtrl.selectItemType();
		// Try to find the XLSX file in the test resources data directory

		File fileName = new File(filePath);
		if (!fileName.exists()) {
			JLog.fail("Test data file not found: " + filePath, TakeScreenshot.True);
			throw new RuntimeException("Test data file not found: " + filePath);
		}
		businessCtrl.uploadBusinessEntityFile(fileName);
		JLog.write("Successfully clicked on the submit button " + fileName);
		businessCtrl.clickUploadJobHyperlink(filePath);
		businessCtrl.validateSuccessMessage();
		// businessCtrl.validateSuccessMessage();
		checkForErrors();
	}

	@When("I uploaded the XLSX file for the business entity Item AVL and checked it on the UI page")
	public void uploadItemAVLBusinessEntities() throws Exception {
		AbstractPage page = new AbstractPage();
		String filePath = "src/test/resources/com/scplatform/selenium/scplatform/data/uploadItemForBusinessEntities.xlsx";
		WebElement insideFrame = page.get(By.xpath("//iframe[@name='contentFrame' and @id='contentFrame']"));
		AbstractPage.browserSession.getDriver().switchTo().frame(insideFrame);
		businessCtrl.selectItemAVLType();
		// Try to find the XLSX file in the test resources data directory

		File fileName = new File(filePath);
		if (!fileName.exists()) {
			JLog.fail("Test data file not found: " + filePath, TakeScreenshot.True);
			throw new RuntimeException("Test data file not found: " + filePath);
		}
		businessCtrl.uploadBusinessEntityFile(fileName);
		JLog.write("Successfully clicked on the submit button " + fileName);
		businessCtrl.clickUploadJobHyperlink(filePath);
		businessCtrl.validateSuccessMessage();
		// businessCtrl.validateSuccessMessage();
		checkForErrors();
	}

	@And("I verified the Business Entities {string} values in the UI page")
	public void verifyBusinessName(String value) throws InterruptedException {
		businessCtrl.validateBusinessValues(value);
		checkForErrors();
	}

	@And("I entered the Business Entities Item Number in the UI page")
	public void enterItemNumber() throws InterruptedException {
		// businessCtrl.enterItemNumber();
		businessCtrl.enterItemNumberInIframe(BusinessEntitiesController.UniqueItemNumber);
		checkForErrors();
	}

	@And("I Created a new sourcing lane with the Item Business Entities & SourceSites are present on the UI page")
	public void createSoucingLanewithSites() throws InterruptedException {
		businessCtrl.enterItemNumber();
		businessCtrl.clickApplyButton();
		businessCtrl.selectFirstRowRadioButton();
		businessCtrl.overrideSupplier("Supp01 desc");
		businessCtrl.overrideSourceSite("Supp01Site01 desc");
		businessCtrl.clickSubmitButton();
		businessCtrl.clickApproveButton();
		businessCtrl.isMfgDescSpanPresent();
		checkForErrors();
	}

	@When("I entered Business Id {string} are displayed in UI page")
	public void verifyBusinessId(String value) throws InterruptedException {
		AbstractPage page = new AbstractPage();
		WebElement insideFrame = page.get(By.xpath("//iframe[@name='contentFrame' and @id='contentFrame']"));
		AbstractPage.browserSession.getDriver().switchTo().frame(insideFrame);
		businessCtrl.enterBusinessId(value);
		checkForErrors();
	}

	@And("I validate the table row with item type {string}, supplier name {string}, and MCM value {string}")
	public void validateTableRow(String itemType, String supplierName, String mcm) {
		boolean result = businessCtrl.validateTableRowValues(BusinessEntitiesController.UniqueItemNumber, itemType,
				supplierName, mcm);
		if (!result) {
			throw new AssertionError("Table row validation failed for: " + BusinessEntitiesController.UniqueItemNumber
					+ ", " + itemType + ", " + supplierName + ", " + mcm);
		}
	}

	@And("I downloaded the Business entities Currency records and validated in the excel file")
	public void downloadAndValidateBusinessEntitiesCurrencyExcelIntegrated() throws Exception {
		businessCtrl.downloadAndValidateBusinessEntitiesCurrencyExcel();
	}
}
