/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.modelViewController.CostRecords.CostRecordsController;
import com.test.selenium.scplatform.modelViewController.CostRecords.CostRecordsView;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.google.common.base.Verify;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CostRecords {

    HarmonyLoginUI ui = new HarmonyLoginUI();
    MTCMController c;
    CostRecordsController ctrller;
    int recordsCount;
    Prop prop = Prop.getInstance();

    @Before
    public void beforeMethod(Scenario scenario) {
        JLog.setScenarioForCucumber(scenario);
        JLog.resetErrorCount();
    }

    private void checkForErrors() {
        if (JLog.getErrorCount() > 0) {
            JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
        }
    }

    @Then("I verify the CR uploads panel is disabled on exception create page")
    public void verifyCRUploadsPanel() throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyCRUploadPanelDisabled();
        JLog.write("Succcessfully verified  Cost Records Panel is disabled.");
    }

    @Then("I verify the {string} on the Cost Records page")
    public void verifyCRPageUploads(String action) throws Throwable {
        JLog.screenCapture();
        if (action.contains("uploadItemForCR") || action.contains("Supplier") || action.contains("EMCreate")
                || action.equals("uploadItemForWAPCR") || action.equals("uploadItemForXWAPCR")) {
            String item = "";
            if (action.contains("CRStatusCheck"))
                item = "CRStatus";
            else if (action.contains("ODMBuyCRMPN"))
                item = "CRODMBuyMPN";
            else if (action.contains("CRMPN"))
                item = "CRMPN";
            else if (action.contains("Supp")) {
                item = "CRSuppCreate";
            } else if (action.contains("EM")) {
                item = "CREMCreate";
            } else if (action.equals("uploadItemForWAPCR")) {
                item = "CRWP";
            } else if (action.equals("uploadItemForXWAPCR")) {
                item = "UPXWPCR";
            } else
                item = "CR";
            c = new MTCMController();
            General g = new General();
            ui.navHarmonyMTCM("Search", "Item AVL");
            c = new MTCMController();
            c.clickButton("Clear");
            g.enterTextFieldVal(item, "itemNumber");
            c = new MTCMController();
            c.clickButton("Apply");
            // WAP/XWAP items are freshly uploaded and may not be indexed yet.
            // Retry Apply every 30s for up to 5 minutes until results appear.
            if (action.equals("uploadItemForWAPCR") || action.equals("uploadItemForXWAPCR")) {
                boolean found = false;
                for (int attempt = 1; attempt <= 10; attempt++) {
                    try {
                        g.verifySearchFilterResults();
                        found = true;
                        break;
                    } catch (Exception ex) {
                        if (attempt == 10) throw ex;
                        JLog.write("Item '" + item + "' not yet indexed (attempt " + attempt + "/10), waiting 30s and retrying Apply...");
                        Thread.sleep(30000);
                        c = new MTCMController();
                        c.clickButton("Apply");
                    }
                }
            } else {
                g.verifySearchFilterResults();
            }
        }
        JLog.write("Succcessfully verified  Cost Records page.");
    }

    @Then("I verify the fields set for {string} on New Sourcing Lane")
    public void verifyCRSavedFilterPageLoads(String filterName) throws Throwable {
        ctrller = new CostRecordsController();
        String s = ctrller.getSelectedVal("Responsibility");
        Verify.verify(s.equals("PRODUCTION"), "ComboBox 'Responsibility' is not set to 'Production'");
        JLog.write("Successfully verified saved filter values on New Sourcing Lane page.");
    }

    @Then("I verify {string} set for {string} on New Sourcing Lane")
    public void verifyCRFilterInterchnages(String combo, String filterName) throws Throwable {
        ctrller = new CostRecordsController();
        String s = ctrller.getSelectedVal("Responsibility");
        JLog.write("Actual value = " + s);
        Verify.verify(s.equals(combo), "ComboBox 'Responsibility' is not set to " + combo);
        JLog.write("Successfully verified saved filter values on New Sourcing Lane page.");
    }

    @Then("I verify the New Sourcing Lane page values are cleared")
    public void verifyNewSourcingLaneClearBTn() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.verifyNewSourcingLaneClrBtn();
        JLog.write("Succcessfully verified the values are cleared on New Sourcing Lane page.");
        checkForErrors();
    }

    @Then("I verify the Search Sourcing Lane page values are cleared")
    public void verifySearchSourcingLaneClearBTn() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.verifySearchSourcingLaneClrBtn();
        JLog.write("Succcessfully verified the values are cleared on Search Sourcing Lane page.");
        checkForErrors();
    }

    @And("I enter lane name on {string} textfield")
    public void enterTextFieldVal(String textFieldName) {
        ctrller = new CostRecordsController();
        UploadController up = new UploadController();
        // String name = "CR"+ up.getTimeStamp()+ "-SERCOM
        // desc-NL-SERCOM-USD-DOMOCSite1
        // desc-USD";
        ctrller.setValField("CR*", textFieldName);
        JLog.write("Set Lane Name field with " + textFieldName);
    }

    @And("I click on Close Button to close the lane")
    public void closeButton() throws InterruptedException {
        ctrller = new CostRecordsController();
        ctrller.clickButton("Close");
        General gen = new General();
        gen.clickWarningPopup("Yes", "You are closing a lane with open records");
        checkForErrors();
    }

    @Then("I verify the fields set for {string} on Search Sourcing Lane")
    public void verifySearchSourcingSavedFilterPageLoads(String filterName) throws Throwable {
        ctrller = new CostRecordsController();
        // Verify.verify(ctrller.getComboBoxTextFieldVal("supplierName").equals("BANTA"),
        // "Cannot verify value on supplierName textField");
        // Verify.verify(ctrller.getComboBoxTextFieldVal("business").equals("DELL"),
        // "Cannot verify value on business textField");
        // Verify.verify(ctrller.getComboBoxTextFieldVal("user").equals("ADMIN"),
        // "Cannot verify value on user textField");
        // Verify.verify(ctrller.getComboBoxTextFieldVal("category").equals("DELL"),
        // "Cannot verify value on category textField");
        // Verify.verify(ctrller.getComboBoxTextFieldVal("dataSource").equals("DELL"),
        // "Cannot verify value on dataSource textField");
        // Verify.verify(ctrller.getComboBoxTextFieldVal("itemNumber").contains("CR"),
        // "Cannot verify value on itemNumber textField");

        // combo
        String s = ctrller.getSelectedVal("Responsibility");
        Verify.verify(s.equals("PRODUCTION"), "ComboBox 'Responsibility' is not set to 'Production'");
        JLog.write("Successfully verified saved filter values on Search Sourcing Lane page.");
    }

    @Then("I verify the Search CostRecords page values are cleared")
    public void verifyCostRecordsClearBtn() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.verifyCostRecordsClrBtn();
        JLog.write("Succcessfully verified the values are cleared on Search Cost Records page.");
        checkForErrors();
    }

    @Then("I verify the Status as {string} on {string} page")
    public void verifyStatus(String status, String page) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.getAndVerifyStatus(status, page);
        JLog.screenCapture();
        JLog.write("Verified status as " + status + " on " + page + " page.");
        checkForErrors();
    }

    @Then("I click and verify msg {string} displayed on warning button of cost record rows")
    public void verifyWarningMsg(String msg) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.warningMsgs(msg);
        JLog.screenCapture();
        checkForErrors();
    }

    @Then("I click and verify msg {string} if displayed on warning button of cost record rows")
    public void verifyWarningMsgIfDisp(String msg) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.warningMsgsIfDisp(msg);
        JLog.screenCapture();
        checkForErrors();
    }

    @Then("I verify the no sourcing lane existing message")
    public void verifySuccessMsg() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.verifyNoSourcingLaneMsg();
        JLog.write("Succesfully verified no sourcing lane existing message");
        checkForErrors();
    }

    @Then("I verify the value on existing Lane")
    public void verifyExistingLaneValue() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.verifyExistingLanesSelectedValue();
        JLog.write("Successfully verified the value on existing Lane.");
        checkForErrors();
    }

    // @Then("I verify the value on existing Lane on New Sourcing Lane")
    // public void verifyExistingLaneValueFor202() throws Throwable {
    // ctrller = new CostRecordsController();
    // ctrller.verifyExistingLanesSelectedValueFor202();
    // JLog.write("Successfully verified the value on existing Lane.");
    // checkForErrors();
    // }

    @Then("I get the total records count")
    public void getTotalRecordsValue() throws Throwable {
        ctrller = new CostRecordsController();
        recordsCount = ctrller.getCRListCountDisplayedAboveTable();
        checkForErrors();
    }

    @Then("I verify the cost records count got reduced by 1")
    public void verifyCRListCount() throws Throwable {
        ctrller = new CostRecordsController();
        int count = ctrller.getCRListCountDisplayedAboveTable();
        Verify.verify(recordsCount == count + 1, "Records count is not reduced by 1");
        JLog.write("Successfully verified the value on existing Lane.");
        checkForErrors();
    }

    @Then("I should see the Close button should be disabled")
    public void verifyCloseBtnNotEnabled() {
        ctrller = new CostRecordsController();
        boolean state = ctrller.verifyCloseButtonEnabledStatus();
        Verify.verify(!state, "Element not enabled.");
        JLog.write("Verified that the button Close is enabled.");
    }

    @Then("I select and {string} records if any")
    public void selectApprove(String btn) throws IOException {
        ctrller = new CostRecordsController();
        // [CR Fix] Retry up to 30s for selectedPageKeys checkboxes to appear
        // (History tab may not be ready; getRowCount may throw before page loads)
        int r = 0;
        long selectDeadline = System.currentTimeMillis() + 30000;
        while (r == 0 && System.currentTimeMillis() < selectDeadline) {
            try {
                r = ctrller.getRowCount("selectedPageKeys");
            } catch (Exception ignore) {
                // elements not yet rendered — will retry
            }
            if (r == 0) {
                try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                ctrller = new CostRecordsController();
            }
        }
        if (r != 0) {
            ctrller.selectAllCheckBoxes();
            ctrller = new CostRecordsController();
            if (btn.contains("Close")) {
                ctrller.clickCloseBtn();
            } else
                ctrller.clickButton(btn);
        }
    }

    @Then("I verify no of rows got reduced 1 after {string} button action")
    public void verifyRows(String btnAction) throws IOException {
        ctrller = new CostRecordsController();
        int r = ctrller.getRowCount("selectedPageKeys");
        JLog.write("Rows found =" + r);
        InputStream fo = new FileInputStream(prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
        Properties p = new Properties();
        p.load(fo);
        int rows = Integer.parseInt(p.getProperty("rows"));
        JLog.write("Rows before " + btnAction + " action was =" + rows);
        rows = rows - 1;
        JLog.write("Expected rows after " + btnAction + " action is =" + rows);
        JLog.write("Rows found on UI after " + btnAction + " action is =" + r);
        Verify.verify(rows == r, "No of rows not matching");
        JLog.write("Verified the search results list count.");
        JLog.resetErrorCount();
        checkForErrors();
    }

    @When("I set the from to Dates")
    public void setFromToDate() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.setStartEndDate();
        JLog.write("Set the From - To Date succesfully.");
        checkForErrors();
    }

    @When("I set start date from today to {string} days from start Dates as end date on row {string}")
    public void setFromToDateonRow(String endDate, String row) throws Throwable {
        AbstractPage.sleep(2);
        ctrller = new CostRecordsController();
        ctrller.setEndDate(row, "today", endDate);
        JLog.write("Set End Date succesfully on row = " + row + ".");
        checkForErrors();
    }

    @When("I set reasonCode {string} on row {string}")
    public void selectReasonCodeOnRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        AbstractPage.sleep(10);
        JLog.write("Wait for 10 seconds to load page");

        CostRecordsView view = new CostRecordsView();
        WebElement ele = view.getReasonCodeonRow(row);
        WebDriverWait wait = new WebDriverWait(view.browser(), Duration.ofSeconds(15));
        view.scrollToElement(ele);
        wait.until(ExpectedConditions.elementToBeClickable(ele)).click();

        ctrller.getReasonCodeOnRow(value, row);
        JLog.write("Selected reasonCode with value - " + value + " on row - " + row);
    }

    @Then("I verify reasonCode {string} on row {string}")
    public void verifyReasonCodeOnRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyReasonCodeOnRow(value, row);
        JLog.write("Verified reasonCode with value - " + value + " on row - " + row);
    }

    @Then("I verify reasonCode field is disabled on row {string}")
    public void verifyReasonCodeStatusOnRow(String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        boolean status = ctrller.verifyReasonCodeOnRowStatus(row);
        Verify.verify(!status, "ReasonCode dropdown is not disabled.");
        JLog.write("Verified reasonCode is disabled  on row - " + row);
    }

    @Then("I verify projectName as {string} on row {string}")
    public void verifyProjectNameOnRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyProjectNameOnRow(value, row);
        JLog.write("Verified projectName with value - " + value + " on row - " + row);
    }

    @When("I set projectName as {string} on row {string}")
    public void setProjectNameOnRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.setProjectNameOnRow(value, row);
        JLog.write("set projectName with value - " + value + " on row - " + row);
    }

    @When("I set MPN {string} on row {string}")
    public void setMPNonRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.setMPNonSpecifiedRow(value, row);
        JLog.write("Succesfully set MPN code on row - " + row + " with value= " + value);
    }

    @Then("I verify MPN {string} on row {string}")
    public void verifyMPNonRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyMPNonSpecifiedRow(value, row);
        JLog.write("Succesfully verified MPN code on row - " + row + " with value= " + value);
    }

    @Then("I verify System Action {string} on row {string}")
    public void verifySysActionOnRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifySysActionOnSpecifiedRow(value, row);
        JLog.write("Succesfully verified System Action on row - " + row + " with value= " + value);
    }

    @Then("I verify {string} on {string} rows")
    public void verifyExcepIDonRow(String curOrPrevExcep, String rows) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyExcepIDOnSpecifiedRow(curOrPrevExcep, rows);
        JLog.write("Succesfully verified Excep ID code on " + rows + " rows");
    }

    @Then("I verify cost records status for the {string} as {string}")
    public void verifyCRSTatusBasedOnExcepID(String curOrPrevExcep, String status) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyCRStatusBasedOnExcepID(curOrPrevExcep, status);
        JLog.write("Succesfully verified CR status against the exception ID");
    }

    @And("I expand Filter view icon on CR SL")
    public void expandFilter() {
        ctrller = new CostRecordsController();
        ctrller.expandHeaderFilterView();
        AbstractPage.sleep(2);
        JLog.screenCapture();
        checkForErrors();
    }

    @Then("I verify Record Source as {string} on row {string}")
    public void verifyRecordSourceonRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyRecordSourceonSpecifiedRow(value, row);
        JLog.write("Succesfully verified Record Source on row - " + row + " with value= " + value);
    }

    @Then("I verify recordSource field is not editable on row {string}")
    public void verifyRecordSourceStatus(String row) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyRecordSourceStatusOnRow(row);
        JLog.write("Verified that record source field is not editable.");
    }

    @Then("I see Show All button is displayed")
    public void verifyShowAllBtn() throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyShowAll();
    }

    @Then("I verify Status List under Hide By Status button")
    public void verifyStatusListUnderHideByStatus() throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyStatusListUnderHideByStatus();
    }

    @Then("I verify Columns List under Hide By Columns button")
    public void verifyColumnsListUnderHideByColumns() throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyColumnsListUnderHideByColumns();
    }

    @Then("I verify MPN set as {string}")
    public void verifyMPNonRow(String value) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyMPN(value);
        JLog.write("Succesfully verified MPN with value= " + value);
    }

    @Then("I verify {string} has value {string} assigned")
    public void verifyColValue(String colName, String value) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyColumnValueOnRow(colName, value);
        JLog.write("Succesfully verified column" + colName + " with value= " + value);
    }

    @Then("I verify {string} rows has status value as {string}")
    public void verifyNoOfstatusFound(String rows, String value) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.verifyStatusFieldRows(rows, value);
        JLog.write("Succesfully verified " + rows + " rows has value on status field as " + value);
    }

    @Then("I verify {string} rows has status value as {string} under Tab")
    public void verifyStatusFound(String rows, String value) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.getStatusFieldsUnderTab(rows, value);
        JLog.write("Succesfully verified " + rows + " rows has value on status field as " + value);
    }

    @Then("I verify CRs has status value as {string} under Tab")
    public void verifyCRStatusFound(String value) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.getCRStatusFieldUnderTab(value);
        JLog.write("Succesfully verified rows with status field as " + value);
    }

    @Then("I select CR with {string} status under Tab")
    public void selectSpecificStatusUnderTab(String status) throws Throwable {
        JLog.screenCapture();
        ctrller = new CostRecordsController();
        ctrller.selectSpecificStatusUnderTab(status);
        JLog.write("Succesfully selected CR with status " + status);
    }

    @When("I select the value on existing Lane")
    public void selectExistingLaneName() throws Throwable {
        ctrller = new CostRecordsController();
        String name = "SERCOM desc-NL-SERCOM-USD-WW-USD";
        JLog.screenCapture();
        ctrller.setComboByName("costLaneKey", name);
        JLog.screenCapture();
        JLog.write("Succesfully selected name on existing Lane combo.");
        checkForErrors();
    }

    @When("I select new one from existing Sourcing Lane")
    public void selectNewLaneName() throws Throwable {
        ctrller = new CostRecordsController();
        String name = "";
        JLog.screenCapture();
        ctrller.setComboByName("costLaneKey", name);
        JLog.screenCapture();
        JLog.write("Succesfully selected name on existing Lane combo.");
        checkForErrors();
    }

    @Then("I verify the Lane Name for itemNumber {string}")
    public void verifyLane(String item) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.getAndVerifyLaneName(item);
        JLog.write("Verified Lane Name succesfully.");
        checkForErrors();
    }

    // @Then("I verify the Lane Name on New Sourcing Lane for itemNumber
    // {string}")
    // public void verifyLaneFor202(String item) throws Throwable {
    // ctrller = new CostRecordsController();
    // ctrller.getAndVerifyLaneNameFor202(item);
    // JLog.write("Verified Lane Name succesfully.");
    // checkForErrors();
    // }
    //
    //

    @Then("I verify {string} status on search filter results")
    public void verifyStatusOnSearchFilterResults(String status) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.getAndVerifySearchFilterResultStatus(status);
        JLog.write("Successfully verified the cost record status as " + status);
    }

    @When("I click on the itemLinks to trigger the popup")
    public void clickItemLinks() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.clickItemDetailsLink();
        JLog.write("Successfully verified itemDetails Link.");
    }

    @When("I select {string} on Destination Site Combobox")
    public void setDestnSiteCombo(String val) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.setDestnSite(val);
    }

    @Then("I verify {string} item links")
    public void verifyItemLinks(String count) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.verifyItemLinksCount(count);
        JLog.write("Verified that " + count + " item Details link is displayed.");
        checkForErrors();
    }

    @Then("I verify the endDate checkBox is deSelected")
    public void verifyEndDateCheckBox() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.verifyEndDateCheckBox();
        JLog.write("Verified that EndDate Checkbox is unchecked.");
        checkForErrors();
    }

    @Then("I verify the copied cost record got deleted")
    public void verifyCRDeleted() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.isCheckBoxVisible();
        JLog.write("Successfully verified that copied cost record got deleted.");
        checkForErrors();
    }

    @And("I click on Submit button")
    public void clickSubmitButton() {
        ctrller = new CostRecordsController();
        ctrller.clickEleByID("SubmitEventButton");
        JLog.write("Clicked on Submit Button");
        JLog.screenCapture();
        checkForErrors();
    }

    @When("I verify row {string} cost records status as {string}")
    public void verifyCRStatus(String row, String status) throws Throwable {
        ctrller = new CostRecordsController();
        JLog.screenCapture();
        ctrller.getAndVerifyCRStatus(row, status);
        JLog.write("Successfully verified the cost record status as " + status);
    }

    @Then("I verify submit,approve,edit actions on new CR row added")
    public void submitNewCR() throws Throwable {
        ctrller = new CostRecordsController();
        JLog.screenCapture();
        ctrller.submitApproveEditApproveNewCrRowAdded();
        JLog.write("Successfully done all actions on new CR added.");
    }

    @When("I set start date as today on row {string}")
    public void setStartDateOnRow(String row) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.setFromDateAsToday(row);
        JLog.write("Selected start date on 2nd row as today.");
        checkForErrors();
    }

    @Then("I verify the CR {string} checkbox status as {string}")
    public void verifyCheckBoxStatus(String val, String status) {
        ctrller = new CostRecordsController();
        boolean s;
        s = ctrller.isCRCheckboxChecked(val);
        JLog.write("Found status is " + s);
        JLog.screenCapture();
        if (status.equals("checked"))
            Verify.verify(s, "Checkbox is not selected.");
        else
            Verify.verify(!(s), "Checkbox is unselected.");
    }

    @When("I {string} the CR {string} checkbox")
    public void selectCheckBox(String selection, String name) {
        ctrller = new CostRecordsController();
        if (selection.equals("check") && (!ctrller.isCRCheckboxChecked(name))) {
            ctrller.checkUncheckCROptions(name);
            JLog.write("Checkbox checked.");
        } else if (selection.equals("uncheck") && ctrller.isCRCheckboxChecked(name)) {
            ctrller.checkUncheckCROptions(name);
            JLog.write("Checkbox unchecked.");
        }
    }

    @When("I select the copied row")
    public void selectCopiedRow() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.selectCopiedRow();
        JLog.write("Selected the copied row.");
    }

    @When("I set the {string} field with {string}")
    public void setField(String textField, String val) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.setFieldWithValue(textField, val);
        JLog.write("Set the " + textField + "with " + val);
    }

    @When("I verify the supplierItem {string} under Summary tab")
    public void verifySupplierItem(String item) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.verifySuppItem(item);
        JLog.write("Succesfuly verifies supplierItem " + item + " under Summary tab.");
        checkForErrors();
    }

    @When("I click on the {string} tab")
    public void clickTab(String tabName) throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.clickTab(tabName);
        JLog.write("Clicked on " + tabName + " tab.");
        checkForErrors();
    }

    @When("I click on the Download tab")
    public void clickDownloadTab() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.clickDownloadTab();
        JLog.write("Clicked on Download tab.");
        checkForErrors();
    }

    public void clearTestDataSavedForFilter(Scenario s) throws Throwable {
        AbstractPage.sleep(2);
        String subMenu = null;
        String name = s.getName();
        if (name.contains("verify saved Filter") || (name.contains("saved Filter delete functionality")
                && (s.getStatus().toString().equals("FAILED")))) {
            String filterName = "CRFilter";
            General gen = new General();
            if (name.contains("New Sourcing Lane"))
                subMenu = "New Sourcing Lane";
            else if (name.contains("Search Sourcing Lane"))
                subMenu = "Search Sourcing Lane";
            else if (name.contains("Search Cost Records"))
                subMenu = "Search Cost Records";
            gen.deleteSavedFilter(filterName, "Pricing", subMenu);
        }
        JLog.write("Test data cleared.");
    }

    @Then("I validated the No records found error message in the UI")
    public void noErrorValidation() throws Throwable {
        ctrller = new CostRecordsController();
        assert ctrller.validateNoRecordsMessage("No records found") : "Error message not found or does not match!";
        checkForErrors();
    }

    @And("I clicked on Clear Button")
    public void clickedonClearBtn() throws Throwable {
        ctrller = new CostRecordsController();
        ctrller.clickClearButton();
        checkForErrors();
    }

    @Then("I validated the No records found error message in the UI\"")
    public void validateErrorMessageUI() {
        CostRecordsController ctrller = new CostRecordsController();
        boolean result = ctrller
                .validateNoRecordsMessage("No records found to display, Refine your search in the filters");
        org.testng.Assert.assertTrue(result, "Error message not found or does not match!");

    }

}
