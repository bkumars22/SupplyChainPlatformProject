/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.bomManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.businessEntities.BusinessEntitiesView;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchController;
import com.test.selenium.scplatform.ui.search.boms.SearchBOMsResultsModel;
import com.google.common.base.Verify;

public class BomManagementController extends SCPlatformSearchController {

	static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
	public static String UniqueItemNumber = "AutoBusItem" + timeStamp;
	public static String UniqueItemNumber1 = "AutoBusItem1" + timeStamp;

	@Override
	public PageImpl getView() {
		return new BomManagementPage();
	}

	public BomManagementPage view;
	public static WebDriver driver;

	private static final JavascriptExecutor WebDriver = null;

	public BomManagementController() {
		// TODO Auto-generated constructor stub

		super();
		this.driver = driver;
		this.view = (BomManagementPage) getView();

	}
	
	

	public static String updateItemNumberAndTimestampInExcelforBOM(String filePath) throws Exception {
		// Use the static UniqueItemNumber value declared at the top
		String itemNumber = UniqueItemNumber;
		String itemNumber1 = UniqueItemNumber1;
		String timeStamp = new java.text.SimpleDateFormat("yyMMddHHmmss").format(new java.util.Date());
		JLog.write("The Item Number has been generated using the Excel file, and the number is\r\n" + itemNumber);

		org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = null;
		org.apache.poi.ss.usermodel.Sheet sheet = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
			workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis);
			sheet = workbook.getSheetAt(0);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					/* ignore */ }
			}
		}

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
			throw new RuntimeException("Item Number column not found or header row is missing!");
		}

		// Set different values in the first and second data rows below the header row
		for (int i = 1; i <= 2; i++) { // i=1 (first data row), i=2 (second data row)
			int dataRowIdx = headerRow.getRowNum() + i;
			org.apache.poi.ss.usermodel.Row dataRow = sheet.getRow(dataRowIdx);
			if (dataRow == null)
				dataRow = sheet.createRow(dataRowIdx);
			org.apache.poi.ss.usermodel.Cell itemCell = dataRow.getCell(itemNumberCol);
			if (itemCell == null)
				itemCell = dataRow.createCell(itemNumberCol);
			if (i == 1) {
				itemCell.setCellValue(itemNumber);
			} else {
				itemCell.setCellValue(itemNumber1);
			}
		}

		// Use try-with-resources for FileOutputStream
		try (FileOutputStream fos = new FileOutputStream(filePath)) {
			workbook.write(fos);
		}
		workbook.close();

		JLog.write("Excel file updated successfully. Item Number: " + itemNumber + ", Timestamp: " + timeStamp);
		return itemNumber;
	}

	/**
	 * Robustly finds and clicks the first upload job hyperlink for any uploaded file in the UI.
	 * Handles context switching, session checks, waits for the link to be present and visible, and retries if needed.
	 * No file name required; matches any upload job message.
	 *
	 * @return true if the link was found and clicked, false otherwise
	 */
	public static boolean clickBOMItemUploadHyperLink() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
			String originalWindow = driver.getWindowHandle();
			for (String windowHandle : driver.getWindowHandles()) {
				driver.switchTo().window(windowHandle);
			}
			// Check for session validity
			if (driver.getPageSource().toLowerCase().contains("login")
					|| driver.getCurrentUrl().toLowerCase().contains("login")) {
				JLog.fail("Session expired or user is logged out. Cannot click the upload job hyperlink.",
						TakeScreenshot.True);
				driver.switchTo().window(originalWindow);
				return false;
			}
			// Wait for the first upload job hyperlink to be present and visible
			By linkXpath = By.xpath("//li[contains(text(),'uploaded and submitted to system as')]/a");
			List<WebElement> links = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(linkXpath));
			if (links.isEmpty()) {
				JLog.fail("Upload job hyperlink not found on the page.", TakeScreenshot.True);
				driver.switchTo().window(originalWindow);
				return false;
			}
			WebElement link = wait.until(ExpectedConditions.visibilityOf(links.get(0)));
			wait.until(ExpectedConditions.elementToBeClickable(link));
			String linkText = link.getText();
			link.click();
			JLog.write("Successfully clicked the upload job hyperlink: " + linkText);
			driver.switchTo().window(originalWindow);
			return true;
		} catch (Exception e) {
			JLog.fail("Could not find or click the upload job hyperlink. Exception: " + e.getMessage(),
					TakeScreenshot.True);
			return false;
		}
	}

	public void selectBOMType() {
		BusinessEntitiesView view1 = new BusinessEntitiesView();
		view1.getItemDropdown().click();
		view.getBOMption().click();
		JLog.write("Successfully selected Item AVL option in the dropdown.");
	}
	
	public void uploadBOMFile(File fileName) {
		BusinessEntitiesView view1 = new BusinessEntitiesView();
		WebElement fileInput = view1.getFileInput();
		JLog.write("Successfully selected the file " + fileName);
		WebElement uploadBtn = view1.getSubmitButton();
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

	
	public boolean isUploadSuccess() throws Throwable {
	    BusinessEntitiesView view = new BusinessEntitiesView();
	    WebElement messageBlock = view.get(By.xpath("//div[contains(@class,'eto-messageblock__body')]"));
	    String s = (messageBlock != null) ? messageBlock.getText() : "";
	    // Return true if the expected message is found, false otherwise
	    return s.contains("File MTCM-Tenant-123_MTCM-Tenant-123_BusinessEntity_MCM1.0_1.xml uploaded and submitted to system as DIRECT");
	}
	
	    
    // Search for an item number in the BOM page
    public void searchItemNumber(String itemNumber) {
        // Cucumber: When I search for the item number in the BOM page
        WebElement searchField = driver.findElement(By.xpath("//input[@id='searchField1']"));
        searchField.clear();
        searchField.sendKeys(itemNumber);
        driver.findElement(By.id("applyBtn")).click(); // Adjust locator as needed
    }

    // Validate headers in the BOM table
    public boolean validateHeaders(List<String> expectedHeaders) {
        // Cucumber: Then I validate the BOM table headers
        List<WebElement> headerElements = driver.findElements(By.xpath("//table//th"));
        for (String expected : expectedHeaders) {
            boolean found = headerElements.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase(expected));
            if (!found) return false;
        }
        return true;
    }

    // Validate input values in the BOM table
    public boolean validateInputs(List<String> expectedInputs) {
        // Cucumber: Then I validate the BOM table input values
        List<WebElement> inputElements = driver.findElements(By.xpath("//table//td//input | //table//td//span"));
        for (String expected : expectedInputs) {
            boolean found = inputElements.stream().anyMatch(e -> e.getAttribute("value") != null ? e.getAttribute("value").equals(expected) : e.getText().trim().equals(expected));
            if (!found) return false;
        }
        return true;
    }
    
    /**
	 * Clicks the select-all checkbox, then clicks the edit (mode_edit) icon to navigate to the BOM edit page.
	 */
	public void selectAllAndNavigateToEditBOM() {
		// Click the select-all checkbox
		WebElement selectAllCheckbox = driver.findElement(By.xpath("//input[@type='checkbox' and contains(@class, 'eto-checkbox__field') and contains(@class, 'eto-all-rows-indicator')]"));
		if (!selectAllCheckbox.isSelected()) {
			selectAllCheckbox.click();
		}
		// Click the edit (mode_edit) icon
		WebElement editIcon = driver.findElement(By.xpath("//span[contains(@class, 'md-icon') and text()='mode_edit']"));
		editIcon.click();
		// Optionally, wait for navigation to the BOM edit page (add explicit wait if needed)
	}

     
 

	/**
	 * Searches for a BOM item by item number and validates headers and input values in the UI.
	 * Returns true if all validations pass.
	 */
	public boolean searchAndValidateBOMItem(String itemNumber, List<String> expectedHeaders, List<String> expectedInputs) {
		// Cucumber: When I search for the item number in the BOM page and validate headers/inputs
		
		searchItemNumber(itemNumber);
		// Validate headers
		boolean headersValid = validateHeaders(expectedHeaders);
		// Validate input values
		boolean inputsValid = validateInputs(expectedInputs);
		// Optionally, validate specific BOM fields using SearchBOMsResultsModel
		SearchBOMsResultsModel bomsRes = new SearchBOMsResultsModel();
		String foundItemNumber = bomsRes.getItemNumber();
		String foundBomName = bomsRes.getBusinessName();
		String foundStatus = bomsRes.getStatus();
		// Example: check if found item number matches searched item number
		boolean itemNumberMatches = itemNumber.equals(foundItemNumber);		
		// You can add more detailed assertions as needed
		return headersValid && inputsValid && itemNumberMatches;
	}
	
	

	
	/**
     * Clicks the file_download icon, then clicks the Download BOM button, validates the file is downloaded, and deletes it.
     * @param downloadFileName The expected file name to validate and delete (e.g., "BOM.xlsx")
     * @return true if the file was downloaded and deleted successfully, false otherwise
     */
    public boolean downloadAndValidateBOMFile(String downloadFileName) throws InterruptedException {
        // Click the file_download icon
        WebElement fileDownloadIcon = driver.findElement(By.xpath("//i[@class='md-icon' and text()='file_download']"));
        fileDownloadIcon.click();
        // Click the Download BOM button
        WebElement downloadButton = driver.findElement(By.xpath("//button[contains(@class,'eto-btn--link')]//i[text()='file_download']/parent::button"));
        downloadButton.click();
        // Wait for the file to be downloaded (simple sleep, replace with explicit wait if needed)
        String downloadDir = "/src/test/resources/com/scplatform/selenium/scplatform/Working";
        File downloadedFile = new File(downloadDir, downloadFileName);
        int retries = 0;
        while (!downloadedFile.exists() && retries < 20) { // Wait up to 20 seconds
            Thread.sleep(1000);
            retries++;
        }
        boolean fileExists = downloadedFile.exists();
        // Delete the file after validation
        if (fileExists) {
            downloadedFile.delete();
        }
        return fileExists;
    }
	
    
    /**
     * Clicks the Download Cost button, validates the file is downloaded, and deletes it.
     * @param downloadFileName The expected file name to validate and delete (e.g., "Cost.xlsx")
     * @return true if the file was downloaded and deleted successfully, false otherwise
     */
    public boolean downloadAndValidateCostFile(String downloadFileName) throws InterruptedException {
        // Click the Download Cost button
        WebElement downloadButton = driver.findElement(By.xpath("//button[contains(@class,'eto-btn--link') and contains(.,'Download Cost')]"));
        downloadButton.click();
        // Wait for the file to be downloaded (simple sleep, replace with explicit wait if needed)
        String downloadDir = "src/test/resources/com/scplatform/selenium/scplatform/Working";
        File downloadedFile = new File(downloadDir, downloadFileName);
        int retries = 0;
        while (!downloadedFile.exists() && retries < 20) { // Wait up to 20 seconds
            Thread.sleep(1000);
            retries++;
        }
        boolean fileExists = downloadedFile.exists();
        // Delete the file after validation
        if (fileExists) {
            downloadedFile.delete();
        }
        return fileExists;
    }
	
	
    /**
     * Validates the values in the Item Details popover UI.
     * @param driver Selenium WebDriver instance
     * @param expectedData Map of field IDs to expected values (String or List<String>)
     * @throws AssertionError if any value does not match
     */
    public void validateItemDetailsPopover(WebDriver driver, Map<String, Object> expectedData) {
        for (Map.Entry<String, Object> entry : expectedData.entrySet()) {
            String fieldId = entry.getKey();
            Object expectedValue = entry.getValue();

            if (expectedValue instanceof List) {
                List<?> expectedList = (List<?>) expectedValue;
                List<WebElement> actualElements = driver.findElements(By.cssSelector("#" + fieldId + " li"));
                if (actualElements.size() != expectedList.size()) {
                    throw new AssertionError("Mismatch in list size for field: " + fieldId + ". Expected: " + expectedList.size() + ", Actual: " + actualElements.size());
                }
                for (int i = 0; i < expectedList.size(); i++) {
                    String actualText = actualElements.get(i).getText().trim();
                    if (!actualText.equals(expectedList.get(i).toString())) {
                        throw new AssertionError("Mismatch in list value for field: " + fieldId +
                                " at index " + i + ". Expected: " + expectedList.get(i) + ", Actual: " + actualText);
                    }
                }
            } else {
                try {
                    WebElement element = driver.findElement(By.id(fieldId));
                    String actualText = element.getText().trim();
                    if (!actualText.equals(expectedValue.toString())) {
                        throw new AssertionError("Mismatch in value for field: " + fieldId +
                                ". Expected: " + expectedValue + ", Actual: " + actualText);
                    }
                } catch (NoSuchElementException e) {
                    throw new AssertionError("Field not found in popover: " + fieldId);
                }
            }
        }
        
        
    }
    /**
     * Selects a value from the Managed By dropdown, clicks Save, and validates the success message .
     * @param driver Selenium WebDriver instance
     * @param dropdownId The id of the dropdown (e.g., "MF_Select_9562395")
     * @param valueToSelect The value attribute to select (e.g., "EM" or "DELL")
     * @throws AssertionError if the success message is not found
     */
    public void selectManagedByAndSave(WebDriver driver, String dropdownId, String valueToSelect) throws InterruptedException {
        // Select the value from the dropdown
        WebElement dropdown = driver.findElement(By.id(dropdownId));
        Select select = new Select(dropdown);
        select.selectByValue(valueToSelect);

        // Click the Save button
        WebElement saveButton = driver.findElement(By.id("saveButton"));
        saveButton.click();

        // Simple sleep to wait for the message to appear (not recommended for production)
        Thread.sleep(2000); // 2 seconds

        // Validate the success message
        WebElement messageBlock = driver.findElement(By.cssSelector("div.eto-messageblock__body ul li"));
        String actualMessage = messageBlock.getText().trim();
        String expectedMessage = "BOM changes has been saved successfully";
        if (!actualMessage.equals(expectedMessage)) {
            throw new AssertionError("Expected message: '" + expectedMessage + "', but got: '" + actualMessage + "'");
        }
    }
    
    /**
     * Clicks the audit history (history) icon, validates the audit history table headers and row values, and closes the popup.
     * @param driver Selenium WebDriver instance
     * @param expectedHeaders List of expected header names
     * @param expectedRowValues List of expected row values (in order)
     * @throws AssertionError if any header or value does not match
     */
    public void clickHistoryAndValidateAuditHistory(WebDriver driver, List<String> expectedHeaders, List<String> expectedRowValues) {
        // Click the history icon (assume only one visible for the row, adjust XPath if needed)
        WebElement historyIcon = driver.findElement(By.xpath("//i[contains(@class,'md-icon') and text()='history']"));
        historyIcon.click();

        // Wait for the audit history table to be visible
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[contains(@class,'audit-history-table')]")));

        // Validate headers
        List<WebElement> headerElements = table.findElements(By.xpath(".//th"));
        for (int i = 0; i < expectedHeaders.size(); i++) {
            String actualHeader = headerElements.get(i).getText().trim();
            if (!actualHeader.equalsIgnoreCase(expectedHeaders.get(i))) {
                throw new AssertionError("Header mismatch at index " + i + ": expected '" + expectedHeaders.get(i) + "', got '" + actualHeader + "'");
            }
        }

        // Validate first row values
        List<WebElement> rowCells = table.findElements(By.xpath(".//tbody/tr[1]/td"));
        for (int i = 0; i < expectedRowValues.size(); i++) {
            String actualValue = rowCells.get(i).getText().trim();
            if (!actualValue.equals(expectedRowValues.get(i))) {
                throw new AssertionError("Row value mismatch at index " + i + ": expected '" + expectedRowValues.get(i) + "', got '" + actualValue + "'");
            }
        }

        // Click the Close button in the popup
        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'eto-btn') and @data-modal-close='']")));
        closeButton.click();
    }
    

    /**
     * Clicks the Back button in the BOM UI popup or page.
     * @param driver Selenium WebDriver instance
     */
    public void clickBackButton(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("backButton")));
        backButton.click();
    }

    
    /**
     * Clicks the Approve button, validates the approved message, searches for the item number, applies the filter, and validates Approved status.
     * @param driver Selenium WebDriver instance
     * @param itemNumber The item number to search and validate
     */
    public void approveBOMAndValidate(WebDriver driver, String itemNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Click Approve button
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("ApproveEventButton")));
        approveButton.click();

        // Validate approved message
        WebElement messageBlock = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("div.eto-messageblock__body ul li")));
        String messageText = messageBlock.getText().trim();
        if (!messageText.contains("has been APPROVED")) {
            throw new AssertionError("Approval message not found. Actual: " + messageText);
        }

        // Search for the item number
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchField1']")));
        searchField.clear();
        searchField.sendKeys(itemNumber);
        WebElement applyBtn = driver.findElement(By.id("applyBtn"));
        applyBtn.click();

        // Validate Approved status in the BOM table
        WebElement approvedStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//b[contains(@class,'cellMessage') and text()='APPROVED']")));
        if (!approvedStatus.getText().trim().equals("APPROVED")) {
            throw new AssertionError("Approved status not found in BOM table.");
        }
    }
    
    /**
     * Returns the current WebDriver instance used by the controller.
     */
    public WebDriver getDriver() {
        return driver;
    } 
   
    /**
     * Validates the presence and text of specific table cell values in the current page.
     * @param itemNumber The expected item number (e.g., "0905S").
     * @param itemType The expected item type (e.g., "Supplier Item").
     * @param supplierName The expected supplier name (e.g., "10:10 COMPUTER SERVICES").
     * @param mcmValue The expected MCM value (e.g., "MCM").
     * @return true if all values are found and match, false otherwise.
     */
    public boolean validateItemValues(String itemNumber, String itemType, String supplierName, String mcmValue) {
        try {
            WebDriver driverToUse = (this.driver != null) ? this.driver : view.browser();
            // Find the row containing the item number link
            String xpath = "//tr[td/a[text()='" + itemNumber + "'] and td[normalize-space(text())='" + itemType + "'] and td[normalize-space(text())='" + supplierName + "'] and td[normalize-space(text())='" + mcmValue + "']]";
            List<WebElement> rows = driverToUse.findElements(By.xpath(xpath));
            if (rows != null && !rows.isEmpty()) {
                JLog.write("Validated table row with values: " + itemNumber + ", " + itemType + ", " + supplierName + ", " + mcmValue);
                return true;
            } else {
                JLog.fail("Table row with expected values not found: " + itemNumber + ", " + itemType + ", " + supplierName + ", " + mcmValue, TakeScreenshot.True);
                return false;
            }
        } catch (Exception e) {
            JLog.fail("Exception while validating table row values: " + e.getMessage(), TakeScreenshot.True);
            return false;
        }
    }
    
    /**
	 * Switches to the contentFrame iframe and enters a value into the searchField1 input.
	 * @param value The value to enter in the input field.
	 */
	public void enterBOMItemNumberInIframe(String value) {
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
			JLog.fail("Exception while entering value in #searchField1 inside iframe: " + e.getMessage(), TakeScreenshot.True);
		}
	}

  
}



