/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.ui;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.businessEntities.BusinessEntitiesController;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.masterDataManagement.bomManagement.BomManagementController;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BOMSteps {
	protected SCPlatformNavigation nav;
	BomManagementController bomctrl = new BomManagementController();
	BusinessEntitiesController businessCtrl = new BusinessEntitiesController();

	// @Before
	public void beforeMethod(Scenario scenario) {
		JLog.setScenarioForCucumber(scenario);
		JLog.resetErrorCount();
		nav = new SCPlatformNavigation();
	}

	private void checkForErrors() {
		if (JLog.getErrorCount() > 0) {
			JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
		}
	}

	@When("I uploaded the XLSX file for the BOMs Item and checked it on the UI page")
	public void uploadItemBusinessEntities() throws Exception {
		AbstractPage page = new AbstractPage();
		String filePath = "src/test/resources/com/scplatform/selenium/scplatform/data/ItemForBOM.xlsx";
		BomManagementController.updateItemNumberAndTimestampInExcelforBOM(filePath);
		JLog.write("The file upload has been completed, and the following file is now displayed. " + filePath);

		BusinessEntitiesController businessCtrl = new BusinessEntitiesController();

		businessCtrl.selectItemType();
		// Try to find the XLSX file in the test resources data directory

		File fileName = new File(filePath);
		if (!fileName.exists()) {
			JLog.fail("Test data file not found: " + filePath, TakeScreenshot.True);
			throw new RuntimeException("Test data file not found: " + filePath);
		}
		businessCtrl.uploadBusinessEntityFile(fileName);
		JLog.write("Successfully clicked on the submit button " + fileName);
		AbstractPage.sleep(10);
		businessCtrl.clickItemHyperLink();
		AbstractPage.sleep(10);
		businessCtrl.clickSuccessElement();
		checkForErrors();
	}

	@When("I uploaded the XLSX file for the BOM Item AVL and checked it on the UI page")
	public void uploadItemAVLBusinessEntities() throws Exception {
		String filePath = "src/test/resources/com/scplatform/selenium/scplatform/data/ItemForBOM.xlsx";
		BusinessEntitiesController businessCtrl = new BusinessEntitiesController();
		businessCtrl.selectItemAVLType();
		// Try to find the XLSX file in the test resources data directory
		File fileName = new File(filePath);
		if (!fileName.exists()) {
			JLog.fail("Test data file not found: " + filePath, TakeScreenshot.True);
			throw new RuntimeException("Test data file not found: " + filePath);
		}
		businessCtrl.uploadBusinessEntityFile(fileName);
		JLog.write("Successfully clicked on the submit button " + fileName);
		AbstractPage.sleep(10);
		businessCtrl.clickItemHyperLink();
		AbstractPage.sleep(10); // Wait for job details to load
		businessCtrl.clickSuccessElement();
		// businessCtrl.validateSuccessMessage();
		checkForErrors();
	}

	@And("I validate the item row values with item type {string} supplier name {string} and MCM value {string}")
	public void validateItemTableRow(String itemType, String supplierName, String mcm) {
		// Debug: Log the item number being used
		JLog.write("Validating table row for Item Number: " + BomManagementController.UniqueItemNumber);
		// Debug: Log all table rows before validation
		WebDriver driverToUse = (businessCtrl.driver != null) ? businessCtrl.driver : businessCtrl.view.browser();
		List<WebElement> allRows = driverToUse.findElements(By.xpath("//table//tr"));
		for (WebElement row : allRows) {
			List<WebElement> cells = row.findElements(By.tagName("td"));
			StringBuilder rowText = new StringBuilder();
			for (WebElement cell : cells) {
				rowText.append(cell.getText()).append(" | ");
			}
			JLog.write("TABLE ROW: " + rowText.toString());
		}
		// Wait for the expected row to appear
		try {
			WebDriverWait wait = new WebDriverWait(driverToUse, java.time.Duration.ofSeconds(10));
			wait.until(ExpectedConditions.presenceOfElementLocated(
					By.xpath("//tr[td/a[text()='" + BomManagementController.UniqueItemNumber
							+ "'] and td[normalize-space(text())='" + itemType + "'] and td[normalize-space(text())='"
							+ supplierName + "'] and td[normalize-space(text())='" + mcm + "']]")));
		} catch (Exception e) {
			JLog.fail("Expected table row not found within wait: " + e.getMessage(), TakeScreenshot.True);
		}
		boolean result = businessCtrl.validateTableRowValues(BomManagementController.UniqueItemNumber, itemType,
				supplierName, mcm);
		if (!result) {
			throw new AssertionError("Table row validation failed for: " + BomManagementController.UniqueItemNumber
					+ ", " + itemType + ", " + supplierName + ", " + mcm);
		}
	}

	@And("I entered the BOM Item Number in the UI page")
	public void enterItemNumber() throws InterruptedException {
		// businessCtrl.enterItemNumber();
		businessCtrl.enterItemNumberInIframe(BomManagementController.UniqueItemNumber);
		checkForErrors();
	}

	@When("I create a new BOM via upload and validate in the UI")
	public void createBOMandValidateinUI() throws Exception {
		AbstractPage page = new AbstractPage();
		String filePath = "src/test/resources/com/scplatform/selenium/scplatform/data/BOM.xlsx";
		BomManagementController.updateItemNumberAndTimestampInExcelforBOM(filePath);
		JLog.write("The file upload has been completed, and the following file is now displayed. " + filePath);

		BusinessEntitiesController businessCtrl = new BusinessEntitiesController();
		AbstractPage.sleep(10);
		bomctrl.selectBOMType();
		// Try to find the XLSX file in the test resources data directory

		File fileName = new File(filePath);
		if (!fileName.exists()) {
			JLog.fail("Test data file not found: " + filePath, TakeScreenshot.True);
			throw new RuntimeException("Test data file not found: " + filePath);
		}
		businessCtrl.uploadBusinessEntityFile(fileName);
		JLog.write("Successfully clicked on the submit button " + fileName);
		AbstractPage.sleep(10);
		businessCtrl.clickItemHyperLink();
		AbstractPage.sleep(10);
		businessCtrl.clickSuccessElement();
		// businessCtrl.validateSuccessMessage();
		checkForErrors();

	}

	@Then("the new BOM should be visible in the UI")
	public void newBomValidateInUI() {
		// Split comma-separated strings into lists for headers and inputs
		List<String> expectedHeaders = Arrays.asList("Business Name", "Status", "Version");
		List<String> expectedInputs = Arrays.asList("10:10 COMPUTER SERVICES", "PENDING", "00000001");
		bomctrl.searchAndValidateBOMItem(BomManagementController.UniqueItemNumber, expectedHeaders, expectedInputs);
		checkForErrors();
	}

	@When("I download the BOM template and validated")
	public void iDownlaodedBOMTemplate() throws InterruptedException {
		bomctrl.selectAllAndNavigateToEditBOM();
		String fileName = BomManagementController.UniqueItemNumber + ".xlsx";
		bomctrl.downloadAndValidateBOMFile(fileName);
	}

	@When("I download the BOM Cost record template and validated")
	public void iDownlaodedBOMCostRecordTemplate() throws InterruptedException {
		String fileName = "_cost_records.xlsx";
		bomctrl.downloadAndValidateCostFile(fileName);
	}

	@Then("I validate the BOM view page UI values")
	public void validateBOMViewpage() {
		// Get the WebDriver instance
		WebDriver driver = AbstractPage.browserSession.getDriver();

		// Build the expected values map for the popover
		Map<String, Object> expectedData = new HashMap<>();
		expectedData.put("popupItemKey", BomManagementController.UniqueItemNumber);
		expectedData.put("popupItemDescription", "");
		expectedData.put("popupItemRevision", "*");
		expectedData.put("popupItemVersion", "00000001");
		expectedData.put("popupItemState", "PENDING");
		// Add more fields as needed

		// Call the model's validation method
		bomctrl.validateItemDetailsPopover(driver, expectedData);
	}

	@When("Select the Managed By {string} and validate in the UI")
	public void iSelectMnagedBYandValidateUI(String value) throws Exception {
		// Get the WebDriver instance
		WebDriver driver = AbstractPage.browserSession.getDriver();
		// The dropdown id is hardcoded here; update if needed
		String dropdownId = "MF_Select_9562395";
		// Use the model's method to select and validate
		bomctrl.selectManagedByAndSave(driver, dropdownId, value);
	}

	@Then("I opened the bom audit history and validated the values")
	public void iOpenedTheBomAuditHistoryAndValidatedTheValues() {
		WebDriver driver = AbstractPage.browserSession.getDriver();
		List<String> expectedHeaders = Arrays.asList(
				"Date Performed", "User ID", "Role ID", "Action", "Comment", "Type", "Last Loaded By User");
		List<String> expectedRowValues = Arrays.asList(
				"ADMIN", "Bom", "BOM_MANAGE_BY_UPDATED", "UPLOAD", "PENDING");
		// Call the model method to perform the click and validation
		bomctrl.clickHistoryAndValidateAuditHistory(driver, expectedHeaders, expectedRowValues);
	}

	@Then("I validated the Approved BOMs in UI")
	public void validateApprovedRecords() {
		bomctrl.approveBOMAndValidate(bomctrl.getDriver(), BomManagementController.UniqueItemNumber);
	}

}
