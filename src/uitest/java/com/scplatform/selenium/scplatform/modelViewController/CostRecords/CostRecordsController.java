/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController.CostRecords;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.login.LoginSCPlatformHarmony;
import com.test.selenium.scplatform.modelViewController.ExceptionController;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.modelViewController.RebatesController;
import com.test.selenium.scplatform.steps.General;
import com.test.selenium.scplatform.ui.main.upload.PriceTAMUploadController;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.google.common.base.Verify;

public class CostRecordsController extends MTCMController {

	private static final JavascriptExecutor WebDriver = null;
	CostRecordsView view;
	Prop prop = Prop.getInstance();

	@Override
	public PageImpl getView() {
		view = new CostRecordsView();
		return view;
	}

	public boolean isCRCheckboxChecked(String val) {
		view = new CostRecordsView();
		WebElement element = view.getCRCheckBox(val);
		view.scrollToElement(element);
		JLog.screenCapture();
		return element.isSelected();
	}

	public void checkUncheckCROptions(String val) {
		view = new CostRecordsView();
		WebElement element = view.getCRCheckBox(val);
		view.executeJavaScript("arguments[0].click();", element);
	}

	public void clickDownloadTab() {
		view = new CostRecordsView();
		WebElement ele = view.get(By.id("Download"));
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		view.executeJavaScript("arguments[0].click();", ele);
		JLog.screenCapture();
	}

	public void verifyShowAll() {
		view = new CostRecordsView();
		WebElement ele = view.get(By.xpath("//div[@id='showAll']//a[@onclick='showAll()']"));
		Verify.verify(ele.isDisplayed(), "Unable to see Show All button");
		JLog.write("Verified that ShowAll button is displayed");
	}

	public void verifyStatusListUnderHideByStatus() {
		view = new CostRecordsView();
		WebElement e = view.getEleByID("statuschecki");
		view.executeJavaScript("arguments[0].click();", e);
		List<WebElement> elements = view.getStatusListUnderHideByStatus();
		for (WebElement ele : elements) {
			Verify.verify(ele.isDisplayed(), "Unable to verify status list under Hide by Status");
		}
		e = view.getHideColumnEle();
		view.executeJavaScript("arguments[0].click();", e);
	}

	public void verifyColumnsListUnderHideByColumns() {
		view = new CostRecordsView();
		view.sleep(1);
		WebElement e = view.getHideColumnEle();
		if (!view.get(By.xpath("//li[@onclick='showAllColumns()']")).isDisplayed())
			view.executeJavaScript("arguments[0].click();", e);
		List<WebElement> elements = view.getColumnsListUnderHideByColumns();
		for (WebElement ele : elements) {
			Verify.verify(ele.isDisplayed(), "Unable to verify Columns list under Hide by Columns");
		}
		view.executeJavaScript("arguments[0].click();", e);
	}

	public void warningMsgs(String msg) {
		view = new CostRecordsView();
		WebElement ele = view.getWarningImg(msg);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		JLog.screenCapture();
		Verify.verify(ele.isDisplayed(), "Unable to see the warning img on UI");
		view.executeJavaScript("arguments[0].click();", ele);
		JLog.screenCapture();
		General gen = new General();
		gen.clickWarningPopup("OK", msg);
	}

	public void warningMsgsIfDisp(String msg) {
		view = new CostRecordsView();
		WebElement ele = null;
		try {
			ele = view.browserSession.getDriver()
					.findElement(By.xpath("//img[contains(@onclick,'" + msg + "')]"));
			// view.getWarningImg(msg);
			JLog.screenCapture();
			if (ele != null && ele.isDisplayed()) {
				JLog.screenCapture();
				view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
				JLog.write("Msg on UI = " + ele.getText());
				view.executeJavaScript("arguments[0].click();", ele);
				JLog.screenCapture();
				General gen = new General();
				gen.clickWarningPopup("OK", msg);
			}
		} catch (Exception e) {
			if (e.toString().contains("NoSuch")) {
				JLog.resetErrorCount();
			}
		}
		if (ele == null)
			JLog.resetErrorCount();
	}

	public void verifyCRUploadPanelDisabled() {
		view = new CostRecordsView();
		WebElement e = null;
		boolean status;
		try {
			e = view.getCRUploadPanel();
			if (e != null) {
				JLog.resetErrorCount();
				status = e.getAttribute("disabled") != null;
				Verify.verify(status, "CR upload panel is enabled!!");
			}
			JLog.resetErrorCount();
			Verify.verify(JLog.getErrorCount() == 1, "Verified that element is disabled");
		} catch (Exception e1) {
			if (e1.toString().contains("Element not enabled")) {
				JLog.resetErrorCount();
				JLog.write("Verified that CR upload panel is disabled");
			}
		}
	}

	public void verifyStatusFieldRows(String expRows, String value) {
		// [9944 Fix] CR auto-approval is async — poll up to 120s for status to reach expected value
		long deadline = System.currentTimeMillis() + 120_000L;
		int actRows = 0;
		do {
			view = new CostRecordsView();
			actRows = 0;
			String s = "";
			List<WebElement> elements = view.getStatusFieldEles();
			JLog.write("[9944 Fix] CR status poll — rows visible: " + elements.size());
			for (WebElement e : elements) {
				s = e.getAttribute("innerText");
				JLog.write("Status at row " + (actRows + 1) + "=" + s);
				if (s.contains(value)) {
					actRows = actRows + 1;
				}
			}
			if (actRows >= Integer.parseInt(expRows)) break;
			if (System.currentTimeMillis() < deadline) {
				JLog.write("[9944 Fix] CR status not yet '" + value + "' — waiting 5s and retrying...");
				try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
				// Refresh the grid before next poll
				try {
					MTCMController refreshCtrl = new MTCMController();
					refreshCtrl.clickButton("Search");
					AbstractPage.sleep(3);
				} catch (Exception ignore) {}
			}
		} while (System.currentTimeMillis() < deadline);
		Verify.verify(
				actRows >= Integer.parseInt(expRows),
				"Unable to verify that at least " + expRows + " rows have status as " + value);
		JLog.write("Successfuly verified " + expRows + " rows with status as " + value);
	}

	public void getStatusFieldsUnderTab(String expRows, String value) {
		view = new CostRecordsView();
		int actRows = 0;
		List<WebElement> elements = view.getStatusFieldsUnderTab();
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
		for (WebElement e : elements) {
			if (e.getAttribute("innerText").contains(value)) {
				actRows = actRows + 1;
			}
		}
		Verify.verify(
				actRows == Integer.parseInt(expRows),
				"Unable to verify that " + expRows + " rows have status as " + value);
		JLog.write("Successfuly verified " + expRows + " rows with status as " + value);
	}

	public void getCRStatusFieldUnderTab(String value) {
		view = new CostRecordsView();
		List<WebElement> elements = view.getStatusFieldsUnderTab();
		boolean status = false;
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
		for (WebElement e : elements) {
			if (e.getAttribute("innerText").contains(value)) {
				status = true;
				break;
			}
		}
		Verify.verify(status, "Unable to verify row with status as " + value);
		JLog.write("Successfuly verified a row with status as " + value);
	}

	public void selectSpecificStatusUnderTab(String status) {
		view = new CostRecordsView();
		List<WebElement> elements = view.getStatusFieldsUnderTab();
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
		for (WebElement e : elements) {
			if (e.getAttribute("innerText").contains(status)) {
				e = view.get(
						By.xpath(
								"//td[contains(text(),'" + status
										+ "')]//ancestor::tr//input[@id='crTable_rowselector' and @type='checkbox']"));
				view.executeJavaScript("arguments[0].click();", e);
				JLog.screenCapture();
				JLog.write("Successfuly selected row with PENDING status");
				break;
			}
		}
		// clickButton(btn);
		// JLog.write("Successfuly clicked on " + btn);
	}

	public void verifyNewSourcingLaneClrBtn() {
		// Verify.verify(getComboBoxTextFieldVal("supplierName","Supplier
		// Name").equals(""),"Value on Supplier Name field is not cleared");
		String s = getSelectedVal("Responsibility");
		Verify.verify(s.equals(""), "ComboBox 'Responsibility' is not set to 'All' back.");
	}

	public void verifyColumnValueOnRow(String colName, String value) {
		view = new CostRecordsView();
		if (colName.equals("MPN")) {
			colName = "49"; // index
		} else if (colName.equals("projectName")) {
			colName = "44";
		} else if (colName.equals("Exception ID")) {
			colName = "51";
			ExceptionController controller = new ExceptionController();
			AbstractPage.sleep(1);
			value = controller.getExcepID();
		} else if (colName.equals("Status")) {
			colName = "1";
		} else if (colName.equals("Cost Type")) {
			colName = "10";
		} else if (colName.equals("Item")) {
			colName = "3";
			UploadController up = new UploadController();
			value = value + up.getTimeStamp();
		} else if (colName.equals("Currency")) {
			colName = "14";
		} else if (colName.equals("Reason Code")) {
			colName = "42";
		}
		if (LoginSCPlatformHarmony.navUrl.contains("dev4160")) {
			colName = String.valueOf(Integer.parseInt(colName) - 3);
		}
		String s = "";
		List<WebElement> ele = view.getSearchColumn(colName);
		for (WebElement e : ele) {
			view.executeJavaScript("arguments[0].scrollIntoView(true);", e);
			s = e.getText();
			if (s.equals("")) {
				s = e.getAttribute("innerText");
			}
			if (s.equals("")) {
				s = e.getAttribute("innerHTML");
			}
			if (s.equals(value)) {
				JLog.write("Succesfully verified value " + value + "on search results");
				return;
			}
		}
		JLog.write("Expected value=" + value);
		JLog.write("Actual value=" + s);
		Verify.verify(s.equals(value), "Unable to verify column value " + value);
	}

	public int getCRListCountDisplayedAboveTable() {
		view = new CostRecordsView();
		String records = view.getDisplayedCountEle("Cost Records").getText();
		records = records.substring(0, records.indexOf(" Records"));
		StringBuilder record = new StringBuilder(records);
		record.deleteCharAt(record.indexOf(","));
		if (record.toString().contains(","))
			record.deleteCharAt(record.indexOf(","));
		int rows = Integer.parseInt(record.toString());
		return rows;
	}

	public void clickTab(String tab) {
		view = new CostRecordsView();
		WebElement ele = view.getTab(tab);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		view.executeJavaScript("arguments[0].click();", ele);
		JLog.screenCapture();
	}

	public void setDestnSite(String val) {
		view = new CostRecordsView();
		view.sleep(2);
		// The Destination Site dropdown is AJAX-loaded after Supplier/Source Site selection.
		// Poll up to 15s (5 attempts x 3s) for the desired option to appear.
		// NOTE: Options have non-breaking space (U+00A0) prefix in their text.
		//       Java String.trim() and XPath normalize-space() do NOT strip U+00A0,
		//       so selectByVisibleText("WW") gives "Cannot locate option with text: WW".
		//       Fix: match on stripped text OR option value, then use selectByValue().
		String matchedValue = null;
		Select select = null;
		WebElement selectEle = null;
		for (int poll = 0; poll <= 5 && matchedValue == null; poll++) {
			if (poll > 0) view.sleep(3);
			selectEle = view.get(
					By.xpath("//label[contains(text(),'Destination Site')]/following-sibling::div//select"));
			if (selectEle == null) continue;
			select = new Select(selectEle);
			for (WebElement opt : select.getOptions()) {
				// Use strip() (Java 11+) which removes U+00A0 non-breaking spaces
				String text = opt.getText().strip();
				String value = opt.getAttribute("value").trim();
				if (text.equalsIgnoreCase(val) || text.startsWith(val) || value.equalsIgnoreCase(val)) {
					matchedValue = value;
					break;
				}
			}
		}
		if (matchedValue != null) {
			// Use selectByValue to avoid XPath normalize-space issue with U+00A0 in option text
			select.selectByValue(matchedValue);
		} else {
			String allOpts = (select != null) ? select.getOptions().stream()
					.map(o -> o.getText().strip() + "=" + o.getAttribute("value"))
					.collect(Collectors.joining("|")) : "select not found";
			JLog.write("Destination Site options: " + allOpts);
			throw new org.openqa.selenium.NoSuchElementException(
					"No Destination Site option matching '" + val + "'. Options: " + allOpts);
		}
		JLog.write("Selected " + val + " on Destination Site");
	}

	public void verifyItemLinksCount(String count) {
		view = new CostRecordsView();
		Verify.verify(
				view.getItemLinks().size() == Integer.parseInt(count),
				"Unable to verify " + count + "item Details Link.");
	}

	public void verifySearchSourcingLaneClrBtn() {
		// Verify.verify(getComboBoxTextFieldVal("supplierName","Supplier
		// Name").equals(""),"Value on Supplier Name field is not cleared");
		String s = getSelectedVal("Responsibility");
		Verify.verify(s.equals(""), "ComboBox 'Responsibility' is not set to 'All' back.");
	}

	public void verifyCostRecordsClrBtn() {
		// Verify.verify(getComboBoxTextFieldVal("supplierName","Supplier
		// Name").equals(""),"Value on Supplier Name field is not cleared");
		String s = getSelectedVal("Responsibility");
		Verify.verify(s.equals(""), "ComboBox 'Responsibility' is not set to 'All' back.");
	}

	public void getAndVerifyLaneName(String item) {
		PriceTAMUploadController upCtrller = new PriceTAMUploadController();
		String timeStamp = upCtrller.getTimeStamp();
		String name = view.getEleByName("selectedLane.sourcingLaneName").getAttribute("value");
		JLog.write("Actual name-" + name);
		String expName = item + timeStamp + "-SERCOM desc-NL-SERCOM-USD-WW-USD";
		JLog.write("Exp name-" + expName);
		Verify.verify(name.equals(expName), "Lane name is incorrect.");
	}

	public void setVerifyLaneName(String item) {
		view = new CostRecordsView();
		WebElement e = view.getEleByName("selectedLane.sourcingLaneName");
		String name = e.getAttribute("value");
		e.sendKeys(name + "ForMassApprove");
	}

	public void getAndVerifyLaneNameFor202(String item) {
		PriceTAMUploadController upCtrller = new PriceTAMUploadController();
		String timeStamp = upCtrller.getTimeStamp();
		String name = view.getEleByName("selectedLane.sourcingLaneName").getAttribute("value");
		JLog.write("Actual name-" + name);
		String expName = item + timeStamp + "-SERCOM desc-NL-SERCOM-EUR-WW-EUR";
		JLog.write("Exp name-" + expName);
		Verify.verify(name.equals(expName), "Lane name is incorrect.");
	}

	public void getAndVerifyStatus(String status, String page) {
		JLog.screenCapture();
		WebElement ele = null;
		String s = "";
		// Poll up to 20s for the status element to appear
		long deadline = System.currentTimeMillis() + 20_000L;
		while (ele == null && System.currentTimeMillis() < deadline) {
			try {
				ele = view.getStatusEle(status);
			} catch (Exception e) {
				if (e.toString().contains("Element not enabled")) {
					JLog.resetErrorCount();
					if (page.equals("Rebates")) {
						try {
							ele = view.get(By.id("status"));
						} catch (Exception e1) {
							if (e1.toString().contains("Element not enabled")) {
								JLog.resetErrorCount();
								return;
							}
						}
					} else {
						return;
					}
				}
			}
			if (ele == null) {
				JLog.write("[wait] status element not yet found for '" + status + "', retrying...");
				try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
				view = new CostRecordsView();
			}
		}
		if (ele == null) {
			Verify.verify(false, "Status element not found, cannot verify status: " + status);
			return;
		}
		s = ele.getAttribute("value");
		Verify.verify(s.equals(status), "Failed to verify the status to" + status);
	}

	public void submitApproveEditApproveNewCrRowAdded() throws Throwable {
		view = new CostRecordsView();
		int row;
		String actStatus = "";
		List<WebElement> elements = view.getList(By.xpath("//tbody[@id='crTableBody']//tr//td[2]"));
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
		JLog.screenCapture();
		JLog.write("No of rows = " + elements.size());
		for (int i = 0; i < elements.size(); i++) {
			actStatus = elements.get(i).getAttribute("innerText");
			JLog.write(
					"Value of status at row " + (i + 1) + " is " + elements.get(i).getAttribute("innerText"));
			if (actStatus.contains("NEW")) {
				row = i + 1;
				selectRow(row, "selectedRecordKeys", "checkbox");
				clickButton("Submit");
				Verify.verify(!getErrorMsg(), "Unexpected error occurred!");
				getAndVerifyCRStatus(String.valueOf(row), "PENDING");
				selectRow(row, "selectedRecordKeys", "checkbox");
				clickButton("Approve");
				Verify.verify(!getErrorMsg(), "Unexpected error occurred!");
				getAndVerifyCRStatus(String.valueOf(row), "CLOSED");
				row = row - 1;
				selectRow(row, "selectedRecordKeys", "checkbox");
				clickButton("Edit");
				AbstractPage.sleep(10);
				JLog.write("Wait for page to reload");

				CostRecordsView view = new CostRecordsView();
				WebElement ele = view.getReasonCodeonRow(String.valueOf(row));
				WebDriverWait wait = new WebDriverWait(view.browser(), Duration.ofSeconds(15));
				view.scrollToElement(ele);
				wait.until(ExpectedConditions.elementToBeClickable(ele)).click();

				getReasonCodeOnRow("NEW PART", String.valueOf(row));
				AbstractPage.sleep(5);
				JLog.write("Selected Reason code successfully");
				selectRow(row, "selectedRecordKeys", "checkbox");
				clickButton("Approve");
				Verify.verify(!getErrorMsg(), "Unexpected error occurred!");
				getAndVerifyCRStatus(String.valueOf(row), "APPROVED");
				return;
			}
		}
	}

	public void getAndVerifyCRStatus(String row, String status) throws Throwable {
		view = new CostRecordsView();
		int rows = Integer.parseInt(row);
		WebElement ele = view.getList(By.xpath("//tbody[@id='crTableBody']//tr//td[2]")).get(rows - 1);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		String s = ele.getAttribute("innerText");
		List<WebElement> elements = view.getList(By.xpath("//tbody[@id='crTableBody']//tr//td[2]"));
		JLog.write("No of rows = " + elements.size());
		for (int i = 0; i < elements.size(); i++) {
			JLog.write(
					"Value of status at row  " + (i + 1) + " is "
							+ elements.get(i).getAttribute("innerText"));
		}
		elements = view.getList(By.xpath("//span[text()='info']"));
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(elements.size() - 1));
		JLog.screenCapture();
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(elements.size() - 1));
		JLog.write("Status found is " + s);
		Verify.verify(s.contains(status), "Status is incorrect for the new CR");
	}

	public void verifyNoSourcingLaneMsg() throws Throwable {
		view = new CostRecordsView();
		String s = view.get(By.xpath("//i[text()='info']/ancestor::span")).getText();
		Verify.verify(
				s.contains("No lane exists for this combination of supplier and site"),
				"No sourcing Lane existing message is missing on Cost Records create page.");
		Verify.verify(
				getComboSelectedOptionByName("costLaneKey").contains(""),
				"Found Sourcing Lane Name when no name expected.");
		// General gen = new General();
		// gen.verifyComboSelectionByComboName("costLaneKey", "");
	}

	public void verifyEndDateCheckBox() {
		view = new CostRecordsView();
		WebElement ele = view.get(By.id("endDateRequired"));
		Verify.verify(ele.getAttribute("value").equals(""), "EndDate checkbox is selected.");
	}

	public void verifySuppItem(String item) {
		view = new CostRecordsView();
		WebElement ele = view.get(By.xpath("//a[contains(text(),'" + item + "')]"));
		Verify.verify(ele != null, "Supp Item " + item + " is not displayed under Summary tab.");
	}

	public void verifyExistingLanesSelectedValue() throws Throwable {
		// UploadController upCtrller = new UploadController();
		// String timeStamp = upCtrller.getTimeStamp();
		String expName = "SERCOM desc-NL-SERCOM-USD-WW-USD";
		String actName = (getComboSelectedOptionByName("costLaneKey"));
		JLog.write("Exp Name= " + expName);
		JLog.write("Act Name = " + actName);
		JLog.screenCapture();
		Verify.verify(
				actName.equals(expName),
				"Existing Lane Name selected is not matching with expected.");
	}

	public void verifyExistingLanesSelectedValueFor202() throws Throwable {
		// UploadController upCtrller = new UploadController();
		// String timeStamp = upCtrller.getTimeStamp();
		String expName = "SERCOM desc-NL-SERCOM-EUR-WW-EUR";
		String actName = (getComboSelectedOptionByName("costLaneKey"));
		JLog.write("Exp Name= " + expName);
		JLog.write("Act Name = " + actName);
		JLog.screenCapture();
		Verify.verify(
				actName.equals(expName),
				"Existing Lane Name selected is not matching with expected.");
	}

	public void setStartEndDate() {
		view = new CostRecordsView();
		view.getNameField("fromDate").sendKeys(DateTime.now().toString("MMddyyyy"));
		view.getNameField("toDate").sendKeys(DateTime.now().plusDays(5).toString("MMddyyyy"));
	}

	public void setFromDateAsToday(String row) throws ParseException {
		// Note - end date is assumed as 5 days from start date(start date is
		// today)
		String currentDay = DateTime.now().toString("MMddyyyy");
		String tdy = currentDay.substring(2, 4);
		// int today = Integer.parseInt(tdy);
		String currntMonth = currentDay.substring(0, 2);
		int expMonth = Integer.parseInt(currntMonth);

		WebElement ele = null;
		// int r = Integer.parseInt(row);

		view = new CostRecordsView();
		JLog.write("Before scrolling to right");
		JLog.screenCapture();
		view.executeJavaScript(
				"arguments[0].scrollIntoView(true);",
				view.get(By.xpath("//th[@id='WARRANTYhead']")));
		view.sleep(3);

		JLog.write("After scrolling to left");
		JLog.screenCapture();
		view.executeJavaScript(
				"arguments[0].scrollIntoView(true);",
				view.get(By.id("crTable_globalrowselector")));
		view.sleep(3);

		// JLog.write("Before scrolling to right");
		// JLog.screenCapture();
		// view.executeJavaScript("arguments[0].scrollIntoView(true);",
		// view.get(By.xpath("//th[@id='WARRANTYhead']")));
		// view.sleep(3);

		// JLog.write("After scrolling to left");
		// JLog.screenCapture();
		// view.executeJavaScript("arguments[0].scrollIntoView(true);",
		// view.get(By.id("crTable_globalrowselector")));

		List<WebElement> elements = view.getList(
				By.xpath("//tr[" + row + "]//i[contains(@onclick,'fromDate') and text()='event']"));
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
		// view.executeJavaScript("arguments[0].click();", elements.get(0));
		view.sleep(3);
		MTCMController mc = new MTCMController();
		mc.clickWithActionClass(elements.get(0));

		view = new CostRecordsView();
		view.sleep(3);
		String actMonth = view.get(By.xpath("//td[@class='title']")).getText();
		actMonth = actMonth.substring(0, actMonth.indexOf(','));
		RebatesController rc = new RebatesController();
		int actualMonth = rc.getMonth(actMonth);

		if (expMonth != actualMonth) {

			try {
				if (actualMonth < expMonth) {
					ele = view.get(By.xpath("//tr[" + row + "][@class='headrow']//td[2]/div"));
				}

				else {
					ele = view.get(By.xpath("//tr[" + row + "][@class='headrow']//td[1]/div"));

				}
				if (ele == null) {
					ele = view.get(By.xpath("//tr[@class='headrow']//td"));
					JLog.resetErrorCount();
				}
				Actions act = new Actions(view.browser());
				act.moveToElement(ele).click().build().perform();
				JLog.screenCapture();
			} catch (Exception e) {
				ele = view.get(By.xpath("//tr[@class='headrow']//td"));
				JLog.resetErrorCount();
			}
		}

		String xpath = "";
		if (row.equals("1"))
			xpath = "//tr[@class='daysrow']";
		else
			xpath = "//tr[@class='daysrow'][" + row + "]";
		// view = new CostRecordsView();
		view.executeJavaScript(
				"arguments[0].scrollIntoView(true);",
				view.get(
						By.xpath(
								xpath
										+ "//td[contains(@class,'day') and not(contains(@class,'day othermonth'))]")));
		JLog.screenCapture();
		view.sleep(3);
		int today = Integer.parseInt(tdy);
		if (today < 10 && tdy.length() == 2) {
			tdy = tdy.substring(1, tdy.length());
		}
		// view.sleep(5);
		view = new CostRecordsView();
		elements = view.getCalendarDay(tdy);
		String s = "";
		for (WebElement e : elements) {
			if (!e.getAttribute("class").contains("othermonth")) {
				s = e.getAttribute("class");
				ele = e;
				break;
			}
		}

		// try {
		// if (!view.visible(By.xpath(xpath + "[@class='headrow']//td[2]/div")))
		// {
		// mc = new MTCMController();
		// mc.clickWithActionClass(elements.get(0));
		// }
		// } catch (Exception e) {
		// if (e.toString().contains("NoSuchElement")) {
		// mc = new MTCMController();
		// mc.clickWithActionClass(elements.get(0));
		// }
		// }

		JLog.screenCapture();
		// mc = new MTCMController();
		// mc.clickWithActionClass(ele);
		Actions act = new Actions(view.browser());
		act.moveToElement(ele).click().build().perform();
		JLog.screenCapture();

	}

	public void setEndDate(String row, String startDate, String endDate) {
		view = new CostRecordsView();
		startDate = DateTime.now().toString("MMddyyyy");
		MTCMController mc = new MTCMController();

		// List<WebElement> elements =
		// view.getList(By.xpath("//i[contains(@onclick,\"showCalendar('fromDate\")]"));
		// view.executeJavaScript("arguments[0].scrollIntoView(true);",
		// elements.get(0));
		// view.sleep(3);
		// view.executeJavaScript("arguments[0].click();", elements.get(0));

		JLog.resetErrorCount();
		List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,'toDate')]"));
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
		view.sleep(3);
		mc = new MTCMController();
		mc.clickWithActionClass(elements.get(0));
		JLog.write("After opening end date calendar");
		JLog.screenCapture();

		String start = startDate.substring(2, 4);
		int day = Integer.parseInt(start);
		startDate = startDate.substring(0, 2);
		int expMonth = Integer.parseInt(startDate);

		WebElement e = view.get(By.xpath("//td[@class='title']"));
		String actMonth = e.getText();
		if (actMonth.equals("")) {
			actMonth = e.getAttribute("innerText");
		}
		JLog.write("Actual month on calendar=" + actMonth);
		actMonth = actMonth.substring(0, actMonth.indexOf(','));
		int actualMonth = 0;
		switch (actMonth) {
			case "January":
				actualMonth = 1;
				break;
			case "February":
				actualMonth = 2;
				break;
			case "March":
				actualMonth = 3;
				break;
			case "April":
				actualMonth = 4;
				break;
			case "May":
				actualMonth = 5;
				break;
			case "June":
				actualMonth = 6;
				break;
			case "July":
				actualMonth = 7;
				break;
			case "August":
				actualMonth = 8;
				break;
			case "September":
				actualMonth = 9;
				break;
			case "October":
				actualMonth = 10;
				break;
			case "November":
				actualMonth = 11;
				break;
			case "December":
				actualMonth = 12;
				break;

			default:
				break;
		}
		WebElement ele = null;
		String xpath;
		if (row.equals("2"))
			xpath = "//tr[" + row + "]";
		else
			xpath = "//tr";

		if (expMonth != actualMonth) {

			if (actualMonth < expMonth) {
				ele = view.get(By.xpath(xpath + "[@class='headrow']//td[2]/div"));
				view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
				view.sleep(3);
				view.executeJavaScript("arguments[0].click();", ele);
				e = view.get(By.xpath("//td[@class='title']"));
				// String actMon = e.getText();
				// if (actMon.equals("")) {
				// actMon = e.getAttribute("innerText");
				// }
				// JLog.write("Actual month on calendar=" + actMon);
				// actMon = actMon.substring(0, actMon.indexOf(','));
				// int actualMon = 0;
				// actualMon = getMonthEquivalent(actMon);
				// if (actualMon < expMonth) {
				// mc = new MTCMController();
				// mc.clickWithActionClass(ele);
				// }
			} else {
				ele = view.get(By.xpath(xpath + "[@class='headrow']//td[1]/div"));
				// view.executeJavaScript("arguments[0].scrollIntoView(true);",
				// ele);
				// view.sleep(3);
				// view.executeJavaScript("arguments[0].click();", ele);
				mc = new MTCMController();
				mc.clickWithActionClass(ele);
			}
		}

		JLog.write("After selecting current month");
		JLog.screenCapture();
		if ((expMonth != 2) && day > 25 || (expMonth == 2 && day > 23)) {
			day = Integer.parseInt(row); // just giving diff values for each row
			// end date, jus giving row no as end
			// date
			// to choose diff date
			// mc = new MTCMController();
			// mc.clickWithActionClass(elements.get(0));
			// view.sleep(5);
			ele = view.get(By.xpath(xpath + "[@class='headrow']//td[2]/div"));
			mc = new MTCMController();
			mc.clickWithActionClass(ele);
			JLog.screenCapture();
		} else {
			day = day + Integer.parseInt(endDate);
		}
		// int today = Integer.parseInt(day);
		String tdy = String.valueOf(day);
		if (day < 10 && tdy.length() == 2) {
			tdy = tdy.substring(1, tdy.length());
		}

		// if (row.equals("2")) {
		// ele = view.getCalendarDay(tdy, "2");
		// } else
		view.sleep(2);
		ele = view.getCalendarDay(tdy).get(0);

		// ele = view.getCalendarDay(tdy);
		// if (ele == null) {
		// view.sleep(3);
		// ele = view.getCalendarDay(tdy);
		// }
		// scrollHorizontally(270);
		view.executeJavaScript(
				"arguments[0].scrollIntoView(true);",
				view.get(By.xpath("//span[contains(text(),'info')]")));
		view.sleep(3);
		// if (ele == null) {
		// view.sleep(3);
		// ele = view.getCalendarDay(tdy);
		// }
		if (!view.visible(By.xpath(xpath + "[@class='headrow']//td[2]/div"))) {
			mc = new MTCMController();
			mc.clickWithActionClass(elements.get(0));
		}
		mc = new MTCMController();
		view.sleep(2);
		mc.clickWithActionClass(ele);
		JLog.write("End date set to " + day);
	}

	public void getReasonCodeOnRow(String val, String row) {
		view = new CostRecordsView();
		WebElement ele = view.getReasonCodeonRow(row);
		Select select = new Select(ele);
		select.selectByValue(val);
		JLog.write("Succesfully set reason code on row - " + row + " with value= " + val);
	}

	public void verifyReasonCodeOnRow(String val, String row) {
		view = new CostRecordsView();
		WebElement ele = view.getReasonCodeonRow(row);
		Select select = new Select(ele);
		String s = select.getFirstSelectedOption().getText();
		JLog.write("Actual value = " + s);
		JLog.write("Expected value = " + val);
		Verify.verify(s.contains(val), "Reason code is not as expected ");
		JLog.write("Succesfully verifed reason code on row - " + row + " with value= " + val);
	}

	public boolean verifyReasonCodeOnRowStatus(String row) {
		view = new CostRecordsView();
		boolean status = true;
		try {
			WebElement ele = view.getReasonCodeonRow(row);
			status = ele.isEnabled();
		} catch (Exception e) {
			if (e.toString().contains("Element not enabled")) {
				status = false;
			}
		}
		return status;
	}

	public void verifyProjectNameOnRow(String val, String row) {
		view = new CostRecordsView();
		WebElement ele = view.getProjectName(row);
		String s = ele.getText();
		if (s.equals("")) {
			s = ele.getAttribute("value");
		}
		JLog.write("Actual value = " + s);
		JLog.write("Expected value = " + val);
		Verify.verify(s.contains(val), "ProjectName On Row" + row + " is not as expected ");
		JLog.write("Succesfully verifed ProjectName on row - " + row + " with value= " + val);
	}

	public void setProjectNameOnRow(String val, String row) {
		view = new CostRecordsView();
		WebElement ele = view.getProjectName(row);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		view.executeJavaScript("arguments[0].click();", ele);
		ele.clear();
		ele.sendKeys(val);
		JLog.write("Succesfully set ProjectName on row - " + row + " with value= " + val);
	}

	public void verifyMPNonSpecifiedRow(String val, String row) {
		view = new CostRecordsView();
		WebElement ele = view.getMPNonRow(row);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		String actVal = ele.getText();
		if (actVal.equals(""))
			actVal = ele.getAttribute("value");
		JLog.write("Actual Value found on row# " + row + " is " + actVal);
		Verify.verify(actVal.contains(val), "Mismatch on MPN value set on row# " + row);
	}

	public void verifySysActionOnSpecifiedRow(String val, String row) {
		view = new CostRecordsView();
		WebElement ele = view.getSystemActionOnRow(row);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		String actVal = ele.getText();
		if (actVal.equals(""))
			actVal = ele.getAttribute("value");
		JLog.write("Actual Value found on row# " + row + " is " + actVal);
		Verify.verify(actVal.contains(val), "Mismatch on System Action value set on row# " + row);
	}

	public void verifyExcepIDOnSpecifiedRow(String expID, String row) throws IOException {
		view = new CostRecordsView();
		List<WebElement> elements = view.getExcepIDonRows();
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
		String actVal;
		int actRows = 0;
		InputStream fo = new FileInputStream(
				prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
		Properties p = new Properties();
		p.load(fo);
		String expVal = p.getProperty(expID);
		for (WebElement e : elements) {
			actVal = e.getText();
			if (actVal.equals(""))
				actVal = e.getAttribute("value");
			if (actVal.equals(expVal))
				actRows = actRows + 1;
		}

		// if(actRows==0) {
		// actRows
		// }

		Verify.verify(actRows == Integer.parseInt(row), "Mismatch on no of rows with exceptionID");
	}

	public void verifyRecordSourceonSpecifiedRow(String val, String row) {
		view = new CostRecordsView();
		WebElement ele = view.getRecSourceonRow(row);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		String actVal = ele.getText();
		if (actVal.equals(""))
			actVal = ele.getAttribute("value");
		JLog.write("Actual Value found on row# " + row + " is " + actVal);
		Verify.verify(actVal.contains(val), "Mismatch on Record Source value set on row# " + row);
	}

	public void verifyRecordSourceStatusOnRow(String row) {
		view = new CostRecordsView();
		WebElement ele = view.getRecSourceonRow(row);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		String txt = ele.getAttribute("readonly");
		JLog.write("Actual Readonly property found = " + txt + " whereas expected=true");
		Verify.verify(txt.equals("true"), "Record Source field on row is not disabled");
	}

	public void verifyCRStatusBasedOnExcepID(String id, String status) throws IOException {
		view = new CostRecordsView();
		InputStream fo = new FileInputStream(
				prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
		Properties p = new Properties();
		p.load(fo);
		String expID = p.getProperty(id);
		List<WebElement> elements = view.getExcepIDs(expID);
		if (elements.size() == 2) {
			String row1 = "";
			String row2 = "";
			elements = view.getList(By.xpath("//tr//td[46]"));
			for (int i = 0; i < elements.size(); i++) {
				// getting row nummber of records whose excep ID is that of
				// existing CR which
				// has an approved and closed record
				if (elements.get(i).getAttribute("innerText").equals(expID)) {
					if (!row1.equals("")) {
						row2 = String.valueOf(i + 1);
					} else {
						row1 = String.valueOf(i + 1);
					}
				}
			}

			String status1 = view.get(By.xpath("//tr[" + row1 + "]//td[1][@class='']")).getAttribute("innerText");
			String status2 = view.get(By.xpath("//tr[" + row2 + "]//td[1][@class='']")).getAttribute("innerText");
			Verify.verify(
					status1.equals(status) || status2.equals(status),
					"CR Status based on ExcepID is incorrect");
			return;
		}
		elements = view.getList(By.xpath("//tr//td[46]"));
		String r = "";
		for (int i = 0; i < elements.size(); i++) {
			// getting row number of record whose excep ID is that of new CR
			// which
			// has an approved status
			if (elements.get(i).getAttribute("innerText").equals(expID)) {
				r = String.valueOf(i + 1);
				break;
			}
		}
		Verify.verify(
				view.get(By.xpath("//tr[" + r + "]//td[1][@class='']")).getAttribute("innerText")
						.equals(status),
				"CR status not matching.");
	}

	public void verifyMPN(String val) {
		view = new CostRecordsView();
		List<WebElement> elements = view.getMPNonAllRows();
		view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
		String actVal = "";
		boolean status = false;
		for (WebElement e : elements) {
			actVal = e.getText();
			if (actVal.equals("")) {
				actVal = e.getAttribute("value");
				JLog.write("Actual Value found = " + actVal);
			}
			if (actVal.equals(val)) {
				status = true;
				break;
			}
		}
		Verify.verify(status, "Mismatch on MPN value set");
	}

	public void setMPNonSpecifiedRow(String val, String row) {
		view = new CostRecordsView();
		WebElement ele = view.getMPNonRow(row);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		ele.click();
		ele.clear();
		ele.sendKeys(val);
	}

	public void selectCopiedRow() {
		view = new CostRecordsView();
		WebElement ele = view.getInputElements("selectedRecordKeys", "checkbox").get(1);
		view.executeJavaScript("arguments[0].click();", ele);
	}

	public void isCheckBoxVisible() {
		view = new CostRecordsView();
		int count = view.getInputElements("selectedRecordKeys", "checkbox").size();
		Verify.verify(count == 1, "CheckBox is still visible even after delete");
	}

	public void setFieldWithValue(String textField, String val) {
		view = new CostRecordsView();
		WebElement ele = view.getNameField(textField);
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		ele.sendKeys(val);
	}

	public void getAndVerifySearchFilterResultStatus(String status) {
		view = new CostRecordsView();
		// Retry for up to 15 seconds — the AJAX grid may still be loading when
		// this method is first called right after an Apply button click.
		boolean flag = false;
		long deadline = System.currentTimeMillis() + 15000;
		while (!flag && System.currentTimeMillis() < deadline) {
			List<WebElement> elements = view.getSearchResultsStatusField(status);
			for (WebElement e : elements) {
				try {
					if (e.getText().contains(status)) {
						flag = true;
						break;
					}
				} catch (Exception ignore) {}
			}
			if (!flag) {
				try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
			}
		}
		Verify.verify(flag, " cannot verify " + status);
	}

	public void clickItemDetailsLink() {
		view = new CostRecordsView();
		List<WebElement> elements = view.getItemLinks();
		for (WebElement ele : elements) {
			MTCMController controller = new MTCMController(); // just to switch
			// to contentFrame
			// frame
			String parent = view.browser().getWindowHandle();
			view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
			view.sleep(5);
			view.executeJavaScript("arguments[0].click();", ele);
			view.sleep(5);
			// ArrayList<String> windows = new
			// ArrayList<String>(view.browser().getWindowHandles());
			// windows.remove(parent);
			// view.sleep(5);
			// view.browser().switchTo().window(windows.get(0));
			// clickPopupOkBtn(parent);
		}
	}

	public void clickPopupOkBtn(String parent) {
		MTCMController.setOverrideContext("null");
		MTCMController controller = new MTCMController();
		view = new CostRecordsView();
		view.executeJavaScript("arguments[0].click();", view.get(By.id("OkButton")));
		JLog.write("Clicked on Ok button");
		controller.browser().switchTo().window(parent);
	}

	public boolean verifyCloseButtonEnabledStatus() {
		view = new CostRecordsView();
		WebElement ele = null;
		try {
			ele = view.browserSession.getDriver().findElement(By.id("CloseEventButton"));
		} catch (Exception e) {
			if (e.toString().contains("no such element")) {
				JLog.resetErrorCount();
				// Element not present in DOM = button is not visible/enabled
				return false;
			}
		}
		if (ele == null) {
			JLog.resetErrorCount();
			return false;
		}
		JLog.resetErrorCount();
		view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
		JLog.screenCapture();
		return ele.isEnabled();
	}

	public String getFilterViewExpandAttribute() {
		view = new CostRecordsView();
		return view.getEleByID("expandfilterVew").getAttribute("class");
	}

	public void expandHeaderFilterView() {
		view = new CostRecordsView();
		view.sleep(2);
		String isHeaderExpanded = getFilterViewExpandAttribute();
		if (!isHeaderExpanded.contains("expanded")) {
			view = new CostRecordsView();
			WebElement e = view.getEleByID("expandfilterVew");
			view.executeJavaScript("arguments[0].scrollIntoView(true);", e);
			MTCMController mc = new MTCMController();
			mc.clickWithActionClass(e);
			// view.executeJavaScript("arguments[0].click();", e);
		}
		JLog.write("Expanded Header filter");
	}

	public void enterTags(String val1, String val2, String textField) {
		String tags = val1 + ";" + val2;
		// values.split(";");
		// for (String tag : tags) {
		view.getInputField(textField).sendKeys(tags.trim());
		view.getInputField(textField).sendKeys(Keys.ENTER);
		// }
	}

	public boolean validateTags(List<CostRecordsModel> expectedTags, String textField) {
		List<String> displayed = view.getDisplayedTags(textField).stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());
		for (CostRecordsModel tag : expectedTags) {
			if (!displayed.contains(tag.getValue())) {
				return false;
			}
		}
		return true;
	}

	public void viewAllTags() {
		view.getViewAllTagsButton();
	}

	public boolean validateInvalidTags(List<CostRecordsModel> expectedInvalidTags) {
		List<String> invalid = view.getInvalidTags().stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());
		for (CostRecordsModel tag : expectedInvalidTags) {
			if (!invalid.contains(tag.getValue())) {
				return false;
			}
		}
		return true;
	}

	public boolean validateNoRecordsMessage(String expectedMessage) {
		org.openqa.selenium.WebDriver driver = view.browserSession.getDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(view.getNoRecordsMessageElement()));
		String actualMessage = view.getNoRecordsMessageElement().getText();
		return actualMessage.equals(expectedMessage);
	}

	public void viewInvalidAllTags() {
		view.getInvalidTags();
	}

	public void clickClearButton() {
		WebElement button = view.clearButton();
		// Use the actual WebDriver instance from the view/browserSession
		org.openqa.selenium.WebDriver driver = view.browserSession.getDriver();
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.elementToBeClickable(button));
		button.click();
	}

}
