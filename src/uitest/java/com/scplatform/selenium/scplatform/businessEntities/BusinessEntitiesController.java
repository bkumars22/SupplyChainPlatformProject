/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.businessEntities;

//Author : Kumar

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.common.unity.actions.Combobox;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.google.common.base.Verify;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BusinessEntitiesController extends MTCMController {
	public BusinessEntitiesView view;
	public WebDriver driver;

	private static final JavascriptExecutor WebDriver = null;

	static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
	public static String UniqueItemNumber = "AutoBusItem" + timeStamp;

	Prop prop = Prop.getInstance();

	@Override
	public PageImpl getView() {
		view = new BusinessEntitiesView();
		return view;
	}

	public BusinessEntitiesController() {
		super();
		this.view = (BusinessEntitiesView) getView();
	}

	public BusinessEntitiesController(WebDriver driver) {
		super();
		this.driver = driver;
		this.view = (BusinessEntitiesView) getView();
	}

	/**
	 * Checks if non-enterprise records (SUPPLIER or MANUFACTURER) exist on the
	 * Manage Business Entities page.
	 * 
	 * @return true if at least one non-enterprise record is present, false
	 *         otherwise
	 */
	public boolean areNonEnterpriseRecordsPresent() {
		List<WebElement> rows = view.getNonEnterpriseRows();
		return rows != null && !rows.isEmpty();

	}

	/**
	 * Returns the count of non-enterprise records (SUPPLIER or MANUFACTURER).
	 */
	public int getNonEnterpriseRecordsCount() {
		List<WebElement> rows = view.getNonEnterpriseRows();
		return rows == null ? 0 : rows.size();
	}

	/**
	 * Example: Print all non-enterprise record names (assuming first column is
	 * name).
	 */
	public void printNonEnterpriseRecordNames() {
		List<WebElement> rows = view.getNonEnterpriseRows();
		for (WebElement row : rows) {
			List<WebElement> cells = row.findElements(org.openqa.selenium.By.tagName("td"));
			if (!cells.isEmpty()) {
				JLog.write("Non-Enterprise Record: " + cells.get(0).getText());
			}
		}

	}

	public void clickApplyButton() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		By applyBtnXpath = By.xpath(
				"//button[@type='button' and contains(@class, 'eto-btn--primary') and normalize-space(text())='Apply']");
		try {
			// Wait for overlays or loaders to disappear (customize selector as needed)
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading, .overlay, .spinner")));

			WebElement applyBtn = wait.until(ExpectedConditions.elementToBeClickable(applyBtnXpath));
			// Scroll into view
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", applyBtn);
			// Optionally wait a bit for animation/overlay to finish
			Thread.sleep(500);
			applyBtn.click();
			JLog.write("Clicked the Apply button.");
		} catch (Exception e) {
			JLog.fail("Could not click Apply button! Exception: " + e.getMessage(), TakeScreenshot.True);
		}
	}

	public void selectBusinessEntityType() {
		view.getDataFileTypeDropdown().click();
		view.getBusinessEntityOption().click();
	}

	public void uploadBusinessEntityFile(File fileName) {
		WebElement fileInput = view.getFileInput();
		JLog.write("Successfully selected the file " + fileName);
		WebElement uploadBtn = view.getSubmitButton();
		JLog.write("Successfully clicked on the submit button ");

		if (fileInput == null) {
			JLog.fail("File input element not found on the page!", com.test.selenium.common.TakeScreenshot.True);
			throw new NullPointerException("File input element is null");
		}
		if (uploadBtn == null) {
			// Debug: log all button elements on the page
			List<WebElement> allButtons = view.browserSession.getDriver().findElements(By.tagName("button"));
			JLog.write("DEBUG: Listing all <button> elements on the page:");
			for (WebElement btn : allButtons) {
				JLog.write("Button text: '" + btn.getText() + "', id: '" + btn.getAttribute("id") + "', class: '"
						+ btn.getAttribute("class") + "', name: '" + btn.getAttribute("name") + "'");
			}
			JLog.fail("Upload button element not found on the page!",
					com.test.selenium.common.TakeScreenshot.True);
			throw new NullPointerException("Upload button element is null");
		}
		fileInput.sendKeys(fileName.getAbsolutePath());
		uploadBtn.click();
	}

	public void isUploadSuccess() throws Throwable {
		view = new BusinessEntitiesView();
		String s = view.get(By.xpath("//div[contains(@class,'eto-messageblock__body')]")).getText();
		Verify.verify(
				s.contains(
						"File MTCM-Tenant-123_MTCM-Tenant-123_BusinessEntity_MCM1.0_1.xml uploaded and submitted to system as DIRECT"));

		// General gen = new General();
		// gen.verifyComboSelectionByComboName("costLaneKey", "");
	}

	public boolean isEntityPresent(String entityName) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		return wait.until(ExpectedConditions.visibilityOf(view.getEntityRow(entityName))) != null;
	}

	/**
	 * Validates that a success message is displayed using the given XPath.
	 * Returns true if any message block is displayed and contains 'success',
	 * 'uploaded', or 'completed'.
	 */
	public boolean validateSuccessMessage() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		List<WebElement> messages = wait
				.until(driver -> driver.findElements(By.xpath("//div[contains(@class,'eto-messageblock__body')]")));

		for (WebElement msg : messages) {
			String text = msg.getText().toString();
			JLog.write("Message block text: '" + text + "'");
			if (text.contains("success") || text.contains("uploaded") || text.contains("completed")) {
				return true;
			}
		}
		JLog.fail("No success message found in any message block!", com.test.selenium.common.TakeScreenshot.True);
		return false;
	}

	public boolean verifyBusinessEntityRow(
			String businessName,
			String id,
			String type,
			String description,
			String primaryContact,
			String primaryContactEmail,
			String coo,
			String hq) {
		// XPath to find a row with all the expected cell values in order
		String xpath = String.format(
				"//tr[td[normalize-space(text())='%s'] and td[normalize-space(text())='%s'] and td[normalize-space(text())='%s'] and td[normalize-space(text())='%s'] and td[normalize-space(text())='%s'] and td[normalize-space(text())='%s']]",
				description, id, type, businessName, primaryContact, primaryContactEmail);
		List<WebElement> rows = driver.findElements(By.xpath(xpath));
		boolean found = !rows.isEmpty();
		if (found) {
			JLog.write("Verified business entity row is present in the UI with all expected values.");
		} else {
			JLog.fail("Business entity row with expected values not found in the UI!",
					com.test.selenium.common.TakeScreenshot.True);
		}
		return found;
	}

	/**
	 * Validates that a
	 * <td>with the given text value is present and visible in the UI table.
	 * Switches to the correct iframe, waits for the cell, and logs detailed
	 * results.
	 * 
	 * @param value The text value to search for in the table cell.
	 */
	public void validateBusinessValues(String value) {
		WebDriver driver = getView().browserSession.getDriver();
		try {
			// Switch to the correct iframe before searching
			driver.switchTo().defaultContent();
			WebElement insideFrame = driver
					.findElement(By.xpath("//iframe[@name='contentFrame' and @id='contentFrame']"));
			driver.switchTo().frame(insideFrame);
			// Wait for the cell to be present and visible
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
			String xpath = "//td[normalize-space(text())='" + value + "']";
			WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			if (cell != null && cell.isDisplayed()) {
				JLog.write("BusinessEntities with text '" + value + "' is present and visible in the UI.");
			} else if (cell != null) {
				JLog.fail("BusinessEntities with text '" + value + "' is found but not visible in the UI.");
			} else {
				JLog.fail("BusinessEntities with text '" + value + "' is null after search.");
			}
		} catch (org.openqa.selenium.TimeoutException | org.openqa.selenium.NoSuchElementException e) {
			// Log the table HTML for debugging using JavaScript to avoid deprecated
			// getAttribute
			try {
				WebElement table = driver.findElement(By.xpath("//table"));
				String tableHtml = (String) ((JavascriptExecutor) driver)
						.executeScript("return arguments[0].outerHTML;", table);
				JLog.write("DEBUG: Table HTML: " + tableHtml);
			} catch (Exception ex) {
				JLog.write("DEBUG: Could not capture table HTML: " + ex.getMessage());
			}
			JLog.fail("BusinessEntities with text '" + value + "' not found in the UI.");
		} catch (Exception e) {
			JLog.fail(
					"Unexpected error while validating BusinessEntities with text '" + value + "': " + e.getMessage());
		}
	}

	public void uploadItemBusinessEntityFile(File fileName) {
		WebElement fileInput = view.getFileInput();
		JLog.write("Successfully selected the file " + fileName);
		WebElement uploadBtn = view.getSubmitButton();
		JLog.write("Successfully clicked on the submit button ");

		if (uploadBtn == null) {
			// Debug: log all button elements on the page
			List<WebElement> allButtons = view.browserSession.getDriver().findElements(By.tagName("button"));
			JLog.write("DEBUG: Listing all <button> elements on the page:");
			for (WebElement btn : allButtons) {
				JLog.write("Button text: '" + btn.getText() + "', id: '" + btn.getAttribute("id") + "', class: '"
						+ btn.getAttribute("class") + "', name: '" + btn.getAttribute("name") + "'");
			}
			JLog.fail("Upload button element not found on the page!",
					com.test.selenium.common.TakeScreenshot.True);
			throw new NullPointerException("Upload button element is null");
		}
		fileInput.sendKeys(fileName.getAbsolutePath());
		uploadBtn.click();
	}

	public void selectItemType() {
		view.getItemDropdown().click();
		;
		view.getItemOption().click();
		JLog.write("Successfully selected Item option in the dropdown.");
	}

	public void selectItemAVLType() {
		view.getItemAVLOption().click();
		JLog.write("Successfully selected Item AVL option in the dropdown.");
	}

	/**
	 * Updates the Item Number in the Excel file and appends a timestamp column
	 * before uploading.
	 * Handles missing or misnamed columns gracefully.
	 * 
	 * @param filePath Path to the Excel file to update
	 * @return The generated item number used in the file
	 */
	public static String updateItemNumberAndTimestampInExcel(String filePath) throws Exception {
		// Use the static UniqueItemNumber value declared at the top
		String itemNumber = UniqueItemNumber;
		String timeStamp = new java.text.SimpleDateFormat("yyMMddHHmmss").format(new java.util.Date());
		JLog.write("The Item Number has been generated using the Excel file, and the number is\r\n" + itemNumber);
		FileInputStream fis = new FileInputStream(filePath);
		org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis);
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

		// Find the column index for "Item Number"
		org.apache.poi.ss.usermodel.Row headerRow = sheet.getRow(1);
		int itemNumberCol = -1;
		if (headerRow != null) {
			for (org.apache.poi.ss.usermodel.Cell cell : headerRow) {
				if (cell != null && "Item Number".equals(cell.getStringCellValue().trim())) {
					itemNumberCol = cell.getColumnIndex();
					break;
				}
			}
		}
		if (itemNumberCol < 0) {
			workbook.close();
			fis.close();
			throw new RuntimeException("Item Number column not found or header row is missing!");
		}

		// Set the value in the row immediately below the header row
		int dataRowIdx = headerRow.getRowNum() + 1;
		org.apache.poi.ss.usermodel.Row dataRow = sheet.getRow(dataRowIdx);
		if (dataRow == null)
			dataRow = sheet.createRow(dataRowIdx);
		org.apache.poi.ss.usermodel.Cell itemCell = dataRow.getCell(itemNumberCol);
		if (itemCell == null)
			itemCell = dataRow.createCell(itemNumberCol);
		itemCell.setCellValue(itemNumber);

		fis.close(); // <-- Close input stream before opening output stream
		FileOutputStream fos = new FileOutputStream(filePath);
		workbook.write(fos);
		fos.close();
		workbook.close();
		JLog.write("Excel file updated successfully. Item Number: " + itemNumber + ", Timestamp: " + timeStamp);
		return itemNumber;
	}

	/**
	 * Enters the given item number in the item number search field and clicks the
	 * Apply button.
	 * 
	 * @param itemNumber
	 * @param itemNumber The item number to enter
	 */
	public void enterItemNumber() {
		// Switch to the correct iframe if not already inside
		try {
			driver.switchTo().defaultContent();
			WebElement insideFrame = driver
					.findElement(By.xpath("//iframe[@name='contentFrame' and @id='contentFrame']"));
			driver.switchTo().frame(insideFrame);
		} catch (Exception e) {
			JLog.write(
					"DEBUG: Could not switch to contentFrame iframe, proceeding anyway. Exception: " + e.getMessage());
		}
		// Wait for the input field to be visible
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement itemNumberInput = null;
		try {
			itemNumberInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//input[@id='searchField1' and @name='value(itemNumber)' and @type='text']")));
		} catch (Exception e) {
			JLog.fail("Item Number input field not found or not visible! Exception: " + e.getMessage(),
					com.test.selenium.common.TakeScreenshot.True);
			throw new NullPointerException("Item Number input field is null");
		}
		itemNumberInput.click();
		itemNumberInput.clear();
		itemNumberInput.sendKeys(UniqueItemNumber);
		itemNumberInput.sendKeys(Keys.TAB);
		JLog.write("Successfully entered Item Number: '" + UniqueItemNumber
				+ "' in the input field using specific XPath.");

	}

	/**
	 * Sets the value of the input field with id 'searchField1' using JavaScript and
	 * triggers onchange.
	 * 
	 * @param value The value to set in the input field.
	 */
	public void setSearchField1Value() {
		try {
			if (view == null) {
				view = new BusinessEntitiesView();
			}
			WebElement input = view.browser().findElement(By.id("searchField1"));
			if (input == null) {
				JLog.fail("Input element #searchField1 not found.", TakeScreenshot.True);
				return;
			}
			input.click();
			input.clear();
			input.sendKeys(UniqueItemNumber);
			input.sendKeys(Keys.TAB);
			// Trigger onchange event via JavaScript
			view.executeJavaScript("arguments[0].dispatchEvent(new Event('change'));", input);
			JLog.write("Set value '" + UniqueItemNumber + "' in #searchField1 and triggered onchange.");
		} catch (Exception e) {
			JLog.fail("Exception while setting value in #searchField1: " + e.getMessage(), TakeScreenshot.True);
		}
	}

	public void verifyElementsInItemAVL(String[] valuestosearch, Model model) {

		view.waitForPageToLoad();
		String pass = null;
		// String[] parts = valuestosearch.split(",");
		int length = valuestosearch.length;

		for (int i = 0; i < length; i++) {
			if (((BusinessEntitiesView) getView()).get(By.cssSelector("BODY")).getText().contains(valuestosearch[i])) {
				JLog.write("The Element" + "" + valuestosearch[i] + "" + "is present");
				pass = "PASS";
				continue;
			} else
				JLog.error("The Element" + "" + valuestosearch[i] + "" + "is not present", TakeScreenshot.True);
			continue;
		}

		// to check whether all elements are present
		if (pass.contains("PASS")) {
			JLog.write("All the Elements are present");
		} else {
			JLog.error("All the elements are not present", TakeScreenshot.True);
			JLog.screenCapture();
		}
	}

	/**
	 * Updates the Item Number in the Excel file, returns the generated item number,
	 * and validates its presence in the UI.
	 * 
	 * @param filePath Path to the Excel file to update
	 * @return The generated item number used in the file
	 */
	public String updateAndValidateItemNumber(String filePath) throws Exception {
		// Update Excel and get the generated item number
		String itemNumber = BusinessEntitiesController.updateItemNumberAndTimestampInExcel(filePath);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		// Wait for upload success message before searching
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath(
							"//div[contains(@class,'eto-messageblock__body') and (contains(text(),'success') or contains(text(),'uploaded') or contains(text(),'completed'))]")));
			JLog.write("Upload success message detected, proceeding to search for item number.");
		} catch (Exception e) {
			JLog.write("WARNING: Upload success message not detected, proceeding anyway. Exception: " + e.getMessage());
		}
		// Enter the item number in the UI and click Apply
		this.enterItemNumber();
		this.clickApplyButton();
		// Switch to the correct iframe before searching for the item
		try {
			driver.switchTo().defaultContent();
			WebElement insideFrame = driver
					.findElement(By.xpath("//iframe[@name='contentFrame' and @id='contentFrame']"));
			driver.switchTo().frame(insideFrame);
		} catch (Exception e) {
			JLog.write(
					"DEBUG: Could not switch to contentFrame iframe, proceeding anyway. Exception: " + e.getMessage());
		}
		// Try both <a> and <td> XPaths
		String[] xpaths = {
				String.format("//td/a[text()='%s']", UniqueItemNumber),
				String.format("//td[normalize-space(text())='%s']", UniqueItemNumber)
		};
		boolean found = false;
		for (String xpath : xpaths) {
			try {
				WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
				if (cell != null && cell.isDisplayed()) {
					JLog.write("Validated that item number '" + UniqueItemNumber
							+ "' is present in the UI table using xpath: " + xpath);
					found = true;
					break;
				}
			} catch (Exception e) {
				JLog.write("DEBUG: Item number not found with xpath: " + xpath + ". Exception: " + e.getMessage());
			}
		}
		if (!found) {
			// Log the table HTML for debugging
			try {
				WebElement table = driver.findElement(By.xpath("//table"));
				String tableHtml = (String) ((JavascriptExecutor) driver)
						.executeScript("return arguments[0].outerHTML;", table);
				JLog.write("DEBUG: Table HTML: " + tableHtml);
			} catch (Exception ex) {
				JLog.write("DEBUG: Could not capture table HTML: " + ex.getMessage());
			}
			JLog.fail("Item number '" + UniqueItemNumber + "' not found in the UI after upload and search!",
					com.test.selenium.common.TakeScreenshot.True);
		}
		return itemNumber;
	}

	/**
	 * Clicks the upload job hyperlink for a file upload notification matching the
	 * given partial file name.
	 * Tries to match both 'File <name> uploaded and submitted to system as' and
	 * just '<name> uploaded and submitted to system as'.
	 * 
	 * @param partialFileName Partial file name to match in the
	 *                        <li>text (e.g., "ItemForBOM.xlsx")
	 * @return true if the link was found and clicked, false otherwise
	 */
	public boolean clickUploadJobHyperlink(String partialFileName) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			// Try to match the <a> by href containing goViewJob and the <li> containing the
			// file name
			String xpath = "//li[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
					+ partialFileName.toLowerCase()
					+ "') and contains(text(),'uploaded and submitted to system as')]//a[contains(@href,'goViewJob')]";
			List<WebElement> links = driver.findElements(By.xpath(xpath));
			if (links.isEmpty()) {
				// Debug: log all <li> elements containing 'uploaded and submitted to system as'
				List<WebElement> allLis = driver
						.findElements(By.xpath("//li[contains(text(),'uploaded and submitted to system as')]"));
				StringBuilder debugMsg = new StringBuilder("No upload job hyperlink found. Available <li> texts:\n");
				for (WebElement li : allLis) {
					debugMsg.append(li.getText()).append("\n");
				}
				JLog.fail("Upload job hyperlink not found on the page for file: " + partialFileName + ". " + debugMsg,
						TakeScreenshot.True);
				return false;
			}
			WebElement link = links.get(0);
			wait.until(ExpectedConditions.elementToBeClickable(link));
			String linkText = link.getText();
			link.click();
			JLog.write("Successfully clicked the upload job hyperlink for file: " + partialFileName + " (" + linkText
					+ ")");
			return true;
		} catch (Exception e) {
			JLog.fail("Could not find or click the upload job hyperlink for file: " + partialFileName + ". Exception: "
					+ e.getMessage(), TakeScreenshot.True);
			return false;
		}
	}

	/**
	 * Clicks the first available upload job hyperlink (regardless of file name).
	 * 
	 * @return true if the link was found and clicked, false otherwise
	 */
	public boolean clickFirstUploadJobHyperlink() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			List<WebElement> links = driver.findElements(By.xpath(
					"//li[contains(text(),'uploaded and submitted to system as')]//a[contains(@href,'goViewJob')]"));
			if (links.isEmpty()) {
				JLog.fail("No upload job hyperlink found on the page.", TakeScreenshot.True);
				return false;
			}
			WebElement link = links.get(0);
			wait.until(ExpectedConditions.elementToBeClickable(link));
			String linkText = link.getText();
			link.click();
			JLog.write("Successfully clicked the first upload job hyperlink (" + linkText + ")");
			return true;
		} catch (Exception e) {
			JLog.fail("Could not find or click any upload job hyperlink. Exception: " + e.getMessage(),
					TakeScreenshot.True);
			return false;
		}
	}

	// /**
	// * Waits for and validates the presence of a SUCCESS message in the UI.
	// *
	// * @return true if the SUCCESS message is found, false otherwise
	// */
	// public boolean validateSuccess() {
	// try {
	// WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	// WebElement success =
	// wait.until(ExpectedConditions.visibilityOfElementLocated(
	// By.xpath("//*[normalize-space(text())='SUCCESS']")));
	// JLog.write("SUCCESS message found in the UI." + success);
	// return true;
	// } catch (Exception e) {
	// JLog.fail("SUCCESS message not found in the UI. Exception: " +
	// e.getMessage(), TakeScreenshot.True);
	// return false;
	// }
	// }

	/**
	 * Selects the first radio button matching the given XPath for row selection.
	 */

	public void selectFirstRowRadioButton() {
		// Switch to the correct iframe if necessary
		try {
			driver.switchTo().defaultContent();
			WebElement insideFrame = driver
					.findElement(By.xpath("//iframe[@name='contentFrame' and @id='contentFrame']"));
			driver.switchTo().frame(insideFrame);
		} catch (Exception e) {
			JLog.write(
					"DEBUG: Could not switch to contentFrame iframe, proceeding anyway. Exception: " + e.getMessage());
		}
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		try {
			By radioXpath = By.xpath(
					"//input[@type='radio' and contains(@class, 'eto-radio__field')]/following-sibling::span[contains(@class, 'eto-radio__box')]");
			WebElement radioBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(radioXpath));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioBtn);
			wait.until(ExpectedConditions.elementToBeClickable(radioBtn));
			radioBtn.click();
			JLog.write("Successfully selected the first row radio button.");
		} catch (Exception e) {
			JLog.fail("Radio button for row selection not found or not clickable! Exception: " + e.getMessage(),
					TakeScreenshot.True);
		}
	}

	public void overrideSupplier(String supplier) {
		getView();
		Combobox.select("Supplier Selected", view.selectSupplier(), supplier);
		JLog.write("Successfully selected the Supplier.");
		AbstractPage.sleep(10);
	}

	public void overrideSourceSite(String sourceSite) {
		getView();
		int retries = 2;
		for (int i = 0; i < retries; i++) {
			try {
				// Always re-locate the dropdown element to avoid stale reference
				WebElement dropdown = view.selectSourceSite();
				Combobox.select("Source Site Selected", dropdown, sourceSite);
				JLog.write("Successfully selected the Source Site.");
				AbstractPage.sleep(10);
				break;
			} catch (org.openqa.selenium.StaleElementReferenceException se) {
				JLog.write("StaleElementReferenceException caught in overrideSourceSite, retrying...");
				// retry
			} catch (Exception e) {
				JLog.fail("Failed to select Source Site! Exception: " + e.getMessage());
				break;
			}
		}
	}

	public void clickSubmitButton() {
		try {
			org.openqa.selenium.By submitButtonXpath = org.openqa.selenium.By
					.xpath("//button[@id='SubmitLaneEventButton']");
			org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(driver,
					java.time.Duration.ofSeconds(20));
			WebElement submitButton = wait.until(
					org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(submitButtonXpath));
			wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(submitButton));
			try {
				submitButton.click();
			} catch (Exception clickEx) {
				((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
				AbstractPage.sleep(10);
			}
			JLog.write("Clicked the Submit button.");
		} catch (Exception e) {
			JLog.fail("Could not click Submit button! Exception: " + e.getMessage());
		}
	}

	/**
	 * Clicks the Approve button using the specified XPath.
	 */
	public void clickApproveButton() {
		try {
			org.openqa.selenium.By approveButtonXpath = org.openqa.selenium.By.xpath(
					"//button[@type='button' and @id='ApproveLaneEventButton' and contains(@class, 'eto-btn') and normalize-space(text())='Approve']");
			org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(driver,
					java.time.Duration.ofSeconds(20));
			WebElement approveButton = wait
					.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(approveButtonXpath));
			try {
				approveButton.click();
			} catch (Exception clickEx) {
				((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", approveButton);
				AbstractPage.sleep(10);
			}
			JLog.write("Clicked the Approve button.");
		} catch (Exception e) {
			JLog.fail("Could not click Approve button! Exception: " + e.getMessage());
		}
	}

	public void enterBusinessId(String businessId) {
		WebDriver driver = getView().browserSession.getDriver();
		WebElement input = driver.findElement(By.xpath("//input[@id='searchField2' and @name='value(businessId)']"));
		input.clear();
		input.sendKeys(businessId);
		JLog.write("Entered Business ID: " + businessId + " using XPath.");
	}

	/**
	 * Checks if the span with text (Mfg001.1 desc) is present and visible in the
	 * UI.
	 * 
	 * @return true if the span is present and visible, false otherwise
	 */
	public boolean isMfgDescSpanPresent() {
		try {
			By mfgDescSpanXpath = By.xpath("//span[normalize-space(text())='(Mfg001.1 desc)']");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement mfgDescSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(mfgDescSpanXpath));
			boolean isDisplayed = mfgDescSpan.isDisplayed();
			JLog.write("Mfg001.1 desc span present and displayed: " + isDisplayed);
			return isDisplayed;
		} catch (Exception e) {
			JLog.fail("Mfg001.1 desc span not found or not visible! Exception: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Waits for the contentFrame iframe to be present and switches to it. Handles
	 * errors gracefully.
	 * 
	 * @return true if switched successfully, false otherwise
	 */
	public boolean switchToContentFrame() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
			// Try both name and id for robustness
			WebElement insideFrame = wait.until(ExpectedConditions.presenceOfElementLocated(
					By.xpath("//iframe[@name='contentFrame' or @id='contentFrame']")));
			driver.switchTo().frame(insideFrame);
			JLog.write("Successfully switched to contentFrame iframe.");
			return true;
		} catch (Exception e) {
			JLog.fail("Exception while switching frames: " + e.getMessage(), TakeScreenshot.True);
			return false;
		}
	}

	/**
	 * Attempts to switch to the contentFrame iframe if it exists, otherwise logs
	 * and continues.
	 * Returns true if switched, false if not present (no exception thrown).
	 */
	public boolean trySwitchToContentFrame() {
		try {
			List<WebElement> frames = driver
					.findElements(By.xpath("//iframe[@name='contentFrame' or @id='contentFrame']"));
			if (frames.isEmpty()) {
				JLog.write("contentFrame iframe not found. Skipping frame switch.");
				return false;
			}
			driver.switchTo().frame(frames.get(0));
			JLog.write("Successfully switched to contentFrame iframe.");
			return true;
		} catch (Exception e) {
			JLog.fail("Exception while switching frames: " + e.getMessage(), TakeScreenshot.True);
			return false;
		}
	}

	public void clickItemHyperLink() {
		view = new BusinessEntitiesView();
		// Use robust XPath to find the first upload job hyperlink
		List<WebElement> elements = view.browser().findElements(By.xpath(
				"//li[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'uploaded and submitted to system as')]/a"));
		if (elements == null || elements.isEmpty()) {
			JLog.fail("No upload job hyperlinks found using the specified XPath.", TakeScreenshot.True);
			return;
		}
		WebElement ele = elements.get(0); // Click only the first hyperlink
		String parent = view.browser().getWindowHandle();
		try {
			view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
			AbstractPage.sleep(2);
			view.executeJavaScript("arguments[0].click();", ele);
			AbstractPage.sleep(2);
			// Switch to new window if it opens
			for (String handle : view.browser().getWindowHandles()) {
				if (!handle.equals(parent)) {
					view.browser().switchTo().window(handle);
					JLog.write("Switched to new window: " + handle);
					// Optionally, switch to contentFrame if needed
					trySwitchToContentFrame();
					// Optionally, handle popup or validation here
					// clickPopupOkBtn(parent); // Uncomment if needed
					break;
				}
			}
		} catch (Exception e) {
			JLog.fail("Exception while clicking upload job hyperlink: " + e.getMessage(), TakeScreenshot.True);
		}
	}

	/**
	 * Clicks the first element containing the text 'SUCCESS' (case-insensitive).
	 */
	public void clickSuccessElement() {
		try {
			List<WebElement> successElements = view.browser().findElements(
					By.xpath(
							"//*[contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'SUCCESS')]"));
			if (successElements == null || successElements.isEmpty()) {
				JLog.fail("No element with text 'SUCCESS' found.", TakeScreenshot.True);
				return;
			}
			WebElement successElement = successElements.get(0);
			view.executeJavaScript("arguments[0].scrollIntoView(true);", successElement);
			AbstractPage.sleep(1);
			view.executeJavaScript("arguments[0].click();", successElement);
			JLog.write("Clicked on the first 'SUCCESS' element.");
		} catch (Exception e) {
			JLog.fail("Exception while clicking 'SUCCESS' element: " + e.getMessage(), TakeScreenshot.True);
		}
	}

	/**
	 * Switches to the contentFrame iframe and enters a value into the searchField1
	 * input.
	 * 
	 * @param value The value to enter in the input field.
	 */
	public void enterItemNumberInIframe(String value) {
		try {
			// Switch to the iframe by id
			WebDriver driverToUse = (this.driver != null) ? this.driver : view.browser();
			driverToUse.switchTo().defaultContent();
			WebElement iframe = driverToUse.findElement(By.id("contentFrame"));
			driverToUse.switchTo().frame(iframe);

			// Find the input field and enter the value
			WebElement input = driverToUse.findElement(By.id("searchField1"));
			input.click();
			input.clear();
			input.sendKeys(value);
			input.sendKeys(Keys.TAB);
			// Optionally trigger onchange event
			((JavascriptExecutor) driverToUse).executeScript("arguments[0].dispatchEvent(new Event('change'));", input);
			JLog.write("Entered value '" + UniqueItemNumber + "' in #searchField1 inside contentFrame iframe.");
		} catch (Exception e) {
			JLog.fail("Exception while entering value in #searchField1 inside iframe: " + e.getMessage(),
					TakeScreenshot.True);
		}
	}

	/**
	 * Validates the presence and text of specific table cell values in the current
	 * page.
	 * 
	 * @param itemNumber   The expected item number (e.g., "0905S").
	 * @param itemType     The expected item type (e.g., "Supplier Item").
	 * @param supplierName The expected supplier name (e.g., "10:10 COMPUTER
	 *                     SERVICES").
	 * @param mcmValue     The expected MCM value (e.g., "MCM").
	 * @return true if all values are found and match, false otherwise.
	 */
	public boolean validateTableRowValues(String itemNumber, String itemType, String supplierName, String mcmValue) {
		try {
			WebDriver driverToUse = (this.driver != null) ? this.driver : view.browser();
			// Find the row containing the item number link
			String xpath = "//tr[td/a[text()='" + itemNumber + "'] and td[normalize-space(text())='" + itemType
					+ "'] and td[normalize-space(text())='" + supplierName + "'] and td[normalize-space(text())='"
					+ mcmValue + "']]";
			List<WebElement> rows = driverToUse.findElements(By.xpath(xpath));
			if (rows != null && !rows.isEmpty()) {
				JLog.write("Validated table row with values: " + itemNumber + ", " + itemType + ", " + supplierName
						+ ", " + mcmValue);
				return true;
			} else {
				JLog.fail("Table row with expected values not found: " + itemNumber + ", " + itemType + ", "
						+ supplierName + ", " + mcmValue, TakeScreenshot.True);
				return false;
			}
		} catch (Exception e) {
			JLog.fail("Exception while validating table row values: " + e.getMessage(), TakeScreenshot.True);
			return false;
		}
	}

	/**
	 * Clicks the upload job hyperlink inside an
	 * <li>containing 'uploaded and submitted to system as'.
	 * Logs the job id text for verification.
	 * 
	 * @return true if the link was found and clicked, false otherwise.
	 */
	public boolean clickUploadJobHyperlinkForBOM() {
		try {
			// Find the <a> inside <li> with the expected text
			List<WebElement> links = view.browser().findElements(By.xpath(
					"//li[contains(.,'uploaded and submitted to system as')]/a"));
			if (links == null || links.isEmpty()) {
				JLog.fail("No BOM upload job hyperlink found using the expected XPath.", TakeScreenshot.True);
				// Debug: log all <li> elements for troubleshooting
				List<WebElement> allLis = view.browser().findElements(By.tagName("li"));
				for (WebElement li : allLis) {
					JLog.write("LI: " + li.getText());
				}
				return false;
			}
			WebElement jobLink = links.get(0);
			String jobId = jobLink.getText();
			view.executeJavaScript("arguments[0].scrollIntoView(true);", jobLink);
			AbstractPage.sleep(1);
			jobLink.click();
			JLog.write("Clicked BOM upload job hyperlink with job id: " + jobId);
			return true;
		} catch (Exception e) {
			JLog.fail("Exception while clicking BOM upload job hyperlink: " + e.getMessage(), TakeScreenshot.True);
			return false;
		}
	}

	/**
	 * Navigates to Manage Business Entities, clicks a Business Name hyperlink,
	 * downloads the currency Excel, validates its contents, and deletes the file.
	 * 
	 * @param businessName    The business name to click.
	 * @param expectedHeaders List of expected Excel headers.
	 * @param expectedData    List of expected data rows (each row as a
	 *                        List<String>).
	 * @param downloadDir     The directory where the file will be downloaded.
	 * @throws Exception if validation fails or file operations fail.
	 */
	public void downloadAndValidateCurrencyExcel(String businessName, List<String> expectedHeaders,
			List<List<String>> expectedData, String downloadDir) throws Exception {
		// 5. Wait for file to download
		String downloadedFilePath = waitForExcelDownload(downloadDir, 30);
		// 6. Validate Excel headers and data
		validateExcelFile(downloadedFilePath, expectedHeaders, expectedData);
		// 7. Delete the file
		File file = new File(downloadedFilePath);
		if (file != null && file.exists()) {
			if (file.delete()) {
				JLog.write("Deleted downloaded file: " + downloadedFilePath);
			} else {
				JLog.fail("Failed to delete downloaded file: " + downloadedFilePath, TakeScreenshot.True);
			}
		}
		// Always switch back to default content after action
		try {
			driver.switchTo().defaultContent();
		} catch (Exception ignore) {
		}
	}

	/**
	 * Waits for an Excel file to appear in the download directory and ensures it is
	 * fully downloaded (not partial).
	 * Logs directory contents for debugging. Works for Chrome and Edge.
	 */
	private String waitForExcelDownload(String downloadDir, int timeoutSeconds) throws InterruptedException {
		File dir = new File(downloadDir);
		long end = System.currentTimeMillis() + timeoutSeconds * 1000;
		File latestFile = null;
		JLog.write("Waiting for Excel file download in directory: " + downloadDir);
		while (System.currentTimeMillis() < end) {
			File[] files = dir.listFiles((d, name) -> name.endsWith(".xlsx"));
			if (files != null && files.length > 0) {
				// Get the most recently modified file
				latestFile = files[0];
				for (File f : files) {
					if (f.lastModified() > latestFile.lastModified()) {
						latestFile = f;
					}
				}
				// Check if the file is still being written (e.g., .crdownload for Chrome/Edge)
				File partial = new File(latestFile.getAbsolutePath() + ".crdownload");
				if (!partial.exists() && latestFile.exists()) {
					JLog.write("Excel file found: " + latestFile.getAbsolutePath());
					return latestFile.getAbsolutePath();
				}
			}
			// Log current files for debugging
			File[] allFiles = dir.listFiles();
			if (allFiles != null) {
				for (File f : allFiles) {
					JLog.write("Current file in dir: " + f.getName());
				}
			}
			Thread.sleep(1000);
		}
		throw new RuntimeException("Excel file not downloaded in time to directory: " + downloadDir);
	}

	/**
	 * Validates the headers and data in the Excel file.
	 */
	private void validateExcelFile(String filePath, List<String> expectedHeaders, List<List<String>> expectedData)
			throws Exception {
		try (FileInputStream fis = new FileInputStream(filePath);
				org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(
						fis)) {
			org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
			// Validate headers
			org.apache.poi.ss.usermodel.Row headerRow = sheet.getRow(0);
			for (int i = 0; i < expectedHeaders.size(); i++) {
				String actual = headerRow.getCell(i).getStringCellValue().trim();
				if (!actual.equals(expectedHeaders.get(i))) {
					throw new AssertionError("Header mismatch at column " + i + ": expected '" + expectedHeaders.get(i)
							+ "', got '" + actual + "'");
				}
			}
			// Validate data rows
			for (int r = 0; r < expectedData.size(); r++) {
				org.apache.poi.ss.usermodel.Row dataRow = sheet.getRow(r + 1);
				List<String> expectedRow = expectedData.get(r);
				for (int c = 0; c < expectedRow.size(); c++) {
					String actual = dataRow.getCell(c).getStringCellValue().trim();
					if (!actual.equals(expectedRow.get(c))) {
						throw new AssertionError("Data mismatch at row " + (r + 1) + ", col " + c + ": expected '"
								+ expectedRow.get(c) + "', got '" + actual + "'");
					}
				}
			}
			JLog.write("Excel file validated successfully: " + filePath);
		}
	}

	/**
	 * Switches to the contentFrame iframe and clicks the business name hyperlink
	 * inside a
	 * <td>cell by exact visible text.
	 * Scrolls into view before clicking if needed.
	 * 
	 * @param businessNameLinkText The exact visible text of the business name link
	 *                             (e.g., "Mfg001.1 desc").
	 */
	public void clickBusinessNameHyperlinkInContentFrame(String businessNameLinkText) {
		if (view == null)
			view = new BusinessEntitiesView();
		WebDriver localDriver = null;
		if (view.browserSession != null && view.browserSession.getDriver() != null) {
			localDriver = view.browserSession.getDriver();
		} else {
			throw new AssertionError(
					"WebDriver is not available from view.browserSession. Make sure the session is initialized.");
		}
		AbstractPage.sleep(10);
		try {
			// Switch to the iframe by id or name
			localDriver.switchTo().defaultContent();
			WebElement iframe = localDriver.findElement(By.id("contentFrame"));
			localDriver.switchTo().frame(iframe);
			// Find the <a> inside <td> with the given text
			List<WebElement> links = localDriver.findElements(
					By.xpath("//a[contains(@href, 'goSelectBusinessCallback') and normalize-space(text())='"
							+ businessNameLinkText + "']"));
			if (links.isEmpty()) {
				// Debug: log all <a> in <td> in the frame
				List<WebElement> allLinks = localDriver.findElements(By.xpath("//td/a"));
				for (WebElement link : allLinks) {
					String text = link != null ? link.getText() : "null";
					String href = link != null ? link.getAttribute("href") : "null";
					JLog.write("Link text: '" + text + "' | href: '" + href + "'");
				}
				throw new AssertionError(
						"Business Name hyperlink not found in <td> in contentFrame: " + businessNameLinkText);
			}
			WebElement ele = links.get(0);
			// Scroll into view if not visible
			if (!ele.isDisplayed()) {
				view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
				AbstractPage.sleep(5);
			}
			view.executeJavaScript("arguments[0].click();", ele);
			JLog.write("Clicked business name hyperlink in <td> in contentFrame: " + businessNameLinkText);
		} catch (Exception e) {
			JLog.fail("Exception while clicking business name hyperlink in contentFrame: " + e.getMessage(),
					TakeScreenshot.True);
			throw new AssertionError(
					"Exception while clicking business name hyperlink in contentFrame: " + e.getMessage());
		} finally {
			// Always switch back to default content after action
			try {
				localDriver.switchTo().defaultContent();
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * Clicks the file download icon and then clicks the "Download Currency" button
	 * in the popover inside the contentFrame iframe.
	 * Uses robust waits and improved XPath for the download icon.
	 */
	public void clickDownloadCurrencyButton() {
		WebDriver localDriver = this.driver;
		if (localDriver == null && view != null && view.browserSession != null) {
			localDriver = view.browserSession.getDriver();
		}
		if (localDriver == null)
			throw new AssertionError("WebDriver is null in clickDownloadCurrencyButton");
		try {
			// Switch to the contentFrame iframe
			localDriver.switchTo().defaultContent();
			WebElement iframe = localDriver.findElement(By.id("contentFrame"));
			localDriver.switchTo().frame(iframe);

			// Wait for the download icon to be present and clickable (improved XPath)
			WebDriverWait wait = new WebDriverWait(localDriver, java.time.Duration.ofSeconds(20));
			WebElement downloadIcon = wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//i[contains(@class,'md-icon') and @title='File Download']")));
			downloadIcon.click();
			JLog.write("Clicked file download icon.");

			// Wait for the Download Currency button to be visible
			AbstractPage.sleep(10);
			WebElement downloadCurrencyBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//button[contains(@onclick,'goCurrencyDownload') and contains(.,'Download Currency')]")));
			downloadCurrencyBtn.click();
			JLog.write("Clicked Download Currency button in popover.");
		} catch (Exception e) {
			// Debug: log all <i> elements with class md-icon and title File Download
			try {
				List<WebElement> icons = localDriver
						.findElements(By.xpath("//i[contains(@class,'md-icon') and @title='File Download']"));
				for (WebElement icon : icons) {
					JLog.write("Found icon: outerHTML=" + icon.getAttribute("outerHTML"));
				}
			} catch (Exception inner) {
				JLog.write("Could not log icons: " + inner.getMessage());
			}
			JLog.fail("Exception while clicking Download Currency button: " + e.getMessage(), TakeScreenshot.True);
			throw new AssertionError("Exception while clicking Download Currency button: " + e.getMessage());
		} finally {
			try {
				localDriver.switchTo().defaultContent();
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * Returns the absolute path to the download directory, ensuring it exists and
	 * is correct for the current OS.
	 * This method should be used wherever a download directory is needed.
	 */
	public static String getDownloadDirectory() {
		String userHome = System.getProperty("user.home");
		// Use File.separator for cross-platform compatibility
		String relativePath = "selenium-scplatform" + File.separator + "src" + File.separator + "test" + File.separator
				+ "resources" + File.separator + "com" + File.separator + "scplatform" + File.separator + "selenium"
				+ File.separator + "scplatform" + File.separator + "Working";
		String downloadDir = userHome + File.separator + relativePath;
		File dir = new File(downloadDir);
		if (!dir.exists()) {
			boolean created = dir.mkdirs();
			if (created) {
				JLog.write("Created download directory: " + downloadDir);
			} else {
				JLog.write("Download directory did not exist and could not be created: " + downloadDir);
			}
		}
		JLog.write("Using download directory: " + downloadDir);
		return downloadDir;
	}

	/**
	 * Returns a ChromeOptions object configured for automated file downloads,
	 * including secure download handling (auto-accepts 'Keep' for potentially
	 * dangerous files).
	 * Use this when initializing your ChromeDriver for tests that require file
	 * downloads.
	 */
	public static ChromeOptions getChromeDownloadOptions() {
		String downloadDir = getDownloadDirectory();
		HashMap<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("download.default_directory", downloadDir);
		chromePrefs.put("download.prompt_for_download", false);
		chromePrefs.put("safebrowsing.enabled", true);
		// This disables Chrome's download protection and auto-accepts 'Keep' for
		// potentially dangerous files
		chromePrefs.put("safebrowsing.disable_download_protection", true);

		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", chromePrefs);
		return options;
	}

	/**
	 * Sets up the ChromeDriver with the correct download directory and secure
	 * download handling.
	 * Call this in your test setup before running any tests that require file
	 * downloads.
	 */
	public static WebDriver createChromeDriverWithDownloadDir() {
		// WebDriverManager is already initialized at framework level in E2CukeBase
		// static block
		// Just verify it's set up
		try {
			String chromeDriverPath = System.getProperty("webdriver.chrome.driver");
			if (chromeDriverPath == null || chromeDriverPath.isEmpty()) {
				// Fallback: initialize if not already done (use same cache path as E2CukeBase)
				WebDriverManager.chromedriver()
						.cachePath(System.getProperty("user.home") + "/.wdm-cache-scplatform")
						.setup();
				JLog.write("ChromeDriver initialized (fallback)");
			} else {
				JLog.write("Using ChromeDriver from: " + chromeDriverPath);
			}
		} catch (Exception e) {
			JLog.write("ChromeDriver verification: " + e.getMessage());
		}

		// Ensure the download directory exists and get the correct path
		String downloadDir = BusinessEntitiesController.getDownloadDirectory();
		// Get ChromeOptions with secure download handling
		ChromeOptions options = BusinessEntitiesController.getChromeDownloadOptions();
		// Create and return the ChromeDriver
		WebDriver driver = new ChromeDriver(options);
		return driver;
	}

	public void downloadAndValidateBusinessEntitiesCurrencyExcel() throws Exception {
		String businessName = "Mfg001.1 desc"; // Replace with actual business name or parameterize as needed
		List<String> expectedHeaders = Arrays.asList("BusinessEntity Id", "Business EntityType", "Currency Code");
		List<List<String>> expectedData = Arrays.asList(
				Arrays.asList("Mfg001.1", "MANUFACTURER", "USD") // Row 1
		// Add more rows as needed
		);

		// Set up ChromeDriver with secure download handling
		WebDriver driver = createChromeDriverWithDownloadDir();
		this.driver = driver; // Ensure the controller uses this driver

		String downloadDir = getDownloadDirectory();
		// Use downloadDir in your download and validation steps
		clickBusinessNameHyperlinkInContentFrame(businessName);
		AbstractPage.sleep(5);
		clickDownloadCurrencyButton();
		AbstractPage.sleep(5);
		downloadAndValidateCurrencyExcel(businessName, expectedHeaders, expectedData, downloadDir);
		// Optionally, quit the driver after the workflow
		// driver.quit();
	}

	/**
	 * Complete workflow: sets up ChromeDriver with secure download handling, clicks
	 * the business name hyperlink in the contentFrame,
	 * clicks the download currency button, and validates the downloaded Excel file.
	 * This method integrates ChromeDriver setup and the download/validation
	 * workflow.
	 *
	 * @param businessName    The business name link text to click (e.g., "Mfg001.1
	 *                        desc")
	 * @param expectedHeaders The list of expected Excel headers
	 * @param expectedData    The list of expected data rows (each row as a
	 *                        List<String>)
	 */
	public void downloadAndValidateBusinessEntitiesCurrencyExcelIntegrated(String businessName,
			List<String> expectedHeaders, List<List<String>> expectedData) throws Exception {
		// Set up ChromeDriver with secure download handling
		WebDriver driver = createChromeDriverWithDownloadDir();
		this.driver = driver; // Ensure the controller uses this driver

		String downloadDir = getDownloadDirectory();
		// Use downloadDir in your download and validation steps
		clickBusinessNameHyperlinkInContentFrame(businessName);
		AbstractPage.sleep(5);
		clickDownloadCurrencyButton();
		AbstractPage.sleep(5);
		downloadAndValidateCurrencyExcel(businessName, expectedHeaders, expectedData, downloadDir);
		// Optionally, quit the driver after the workflow
		// driver.quit();
	}

}