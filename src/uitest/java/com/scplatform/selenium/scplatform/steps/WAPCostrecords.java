/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.CostRecords.WAPCostRecordsController;
import com.test.selenium.scplatform.ui.main.upload.UploadController;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class WAPCostrecords {

    HarmonyLoginUI ui = new HarmonyLoginUI();
    UploadController c;
    WAPCostRecordsController wapctrller;
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

    @When("I set material value {string} on row {string}")
    public void setMaterialOnRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        wapctrller = new WAPCostRecordsController();
        wapctrller.setMaterialOnRow(value, row);
        // wapctrller.closePopup();
        JLog.write("set material with value - " + value + " on row - " + row);
    }

    @When("I set FunctionalGroup ID as {string} on row {string}")
    public void setProjectNameOnRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        // UploadController up = new UploadController();
        wapctrller = new WAPCostRecordsController();
        if (!value.contains("SOLESOURCE"))
            value = value + PriceTAM.timeStamp;
        // value = value + up.getTimeStamp();
        wapctrller.setFunctionalIdOnRow(value, row);
        JLog.write("set FunctionalGroup ID with value - " + value + " on row - " + row);
    }

    @And("I set the XLOBFG name {string} on cost record search filters")
    public void setXLOBFGNameonCostsearchfilters(String value) throws Throwable {
        JLog.screenCapture();
        wapctrller = new WAPCostRecordsController();
        wapctrller.setXLOBFGNamefield(PriceTAM.fg);
        JLog.write("set XLOBFG name on cost search filters" + value);
    }

    @When("I verify row {string} cost records material value as {string}")
    public void verifyCRStatus(String row, String status) throws Throwable {
        wapctrller = new WAPCostRecordsController();
        JLog.screenCapture();
        wapctrller.getAndVerifyMaterialStatus(row, status);
        JLog.write("Successfully verified the cost record material value as " + status);
    }

    @Then("I verify Material value {string} on row {string}")
    public void verifyMPNonRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        wapctrller = new WAPCostRecordsController();
        wapctrller.verifyMaterialValue(value, row);
        JLog.write("Succesfully verified Material with value= " + value);
    }

    @Then("I verify FunctionalGroup ID value {string} on row {string}")
    public void verifyFGIdonRow(String value, String row) throws Throwable {
        JLog.screenCapture();
        wapctrller = new WAPCostRecordsController();
        wapctrller.verifyFGIdValue(value, row);
        JLog.write("Succesfully verified FGId with value= " + value);
    }

    @And("I click on SL {string} Button")
    public void clickButtonAndSwitchSourcingLane(String buttonName) throws InterruptedException {
        wapctrller = new WAPCostRecordsController();
        // parentForLinkDetails = controller.browser().getWindowHandle();
        wapctrller.clickButton(buttonName);
        checkForErrors();
    }

    @And("I verify {string} button diabled XWAP cost record page")
    public void nobuttonsonUI(String button) throws InterruptedException {
        wapctrller = new WAPCostRecordsController();
        // parentForLinkDetails = controller.browser().getWindowHandle();
        wapctrller.getAndVerifyedit(button);
        checkForErrors();
    }

    @Then("I verify XLOB LOB value {string}")
    public void verifyXLOBFlexLOBOnSearchFilterResults(String status) throws Throwable {
        JLog.screenCapture();
        wapctrller = new WAPCostRecordsController();
        wapctrller.getAndVerifySearchFilterResultXLOBFGLOB(status);
        JLog.write("Successfully verified the cost record XLOB Flex LOB as" + status);

    }

    // @And("I set the Functional GroupName as {string on row {string}")
    // public void setFGNameOnSL(String fgName, row) throws InterruptedException
    // {
    // wapctrller = new WAPCostRecordsController();
    // // parentForLinkDetails = controller.browser().getWindowHandle();
    // wapctrller.
    // checkForErrors();
    // }

    @Then("I verify XLOB Platform value {string}")
    public void verifyXLOBFlexPlotformOnSearchFilterResults(String status) throws Throwable {
        JLog.screenCapture();
        wapctrller = new WAPCostRecordsController();
        wapctrller.getAndVerifySearchFilterResultXLOBPlatform(status);
        JLog.write("Successfully verified the cost record XLOB Flex Platform as " + status);
    }

    @Then("I verify XLOB FGName value {string}")
    public void verifyXLOBFGNameOnSearchFilterResults(String status) throws Throwable {
        JLog.screenCapture();
        wapctrller = new WAPCostRecordsController();
        wapctrller.getAndVerifySearchFilterResultXLOBFGName(status);
        JLog.write("Successfully verified the cost record XLOB Flex FGName as " + status);
    }

}
