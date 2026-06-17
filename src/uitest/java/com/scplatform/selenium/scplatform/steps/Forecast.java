/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import org.joda.time.DateTime;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.ForecastController;
import com.test.selenium.scplatform.modelViewController.FunctionalGroupController;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.ui.main.upload.UploadController;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Forecast {

    HarmonyLoginUI ui = new HarmonyLoginUI();
    MTCMController c;
    UploadController uC;
    ForecastController fctrller;
    int recordsCount;
    Prop prop = Prop.getInstance();
    static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
    public static String fggcm = "ForecastFGGCM" + timeStamp;
    public static String fggcm1 = "ForecastFGGCM1" + timeStamp;
    public static String fgSuperGCM = "ForecastFGSuperGCM" + timeStamp;
    public static String fgSuperGCM1 = "ForecastFGSuperGCM1" + timeStamp;
    public static String fgadm = "ForecastFGAdmin" + timeStamp;
    public static String fgadm1 = "ForecastFGAdmin1" + timeStamp;
    public static String parentfggcm = "ForecastPFGGCM" + timeStamp;
    public static String parentfgSgcm = "ForecastPFGSuperGCM" + timeStamp;
    public static String parentfgadm = "ForecastPFGAdm" + timeStamp;

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

    @When("I verify {string} FC status on search filter results")
    public void verifyStatusOnSearchFilterResults(String status) throws Throwable {
        ForecastController fctrller = new ForecastController();
        fctrller.getAndVerifySearchFilterResultFCStatus(status);
        JLog.write("Successfully verified the Forecast record status as " + status);
        checkForErrors();
    }

    @Then("I verify scroll bar is visible under Forecast")
    public void verifyScrollBar() {
        ForecastController controller = new ForecastController();
        JLog.screenCapture();
        controller.isScrollBarVisibleUnderForecast();
        JLog.write("Verified that scroll bar is visible.");
        checkForErrors();
    }

    @Then("I verify {string} FC Extend forecast value on search filter results")
    public void verifyFCExtendforecastterms(String extended) throws Throwable {
        ForecastController fctrller = new ForecastController();
        fctrller.getAndVerifyExtendedFC(extended);
        JLog.write("Successfully verified the Forecast record Extended forecast term as " + extended);
        checkForErrors();
    }

    @Then("I verify {string} and {string} and {string} on FC itemNumbers on search filter results")
    public void verifyItemNumbersOnSearchFilterResults(String fcItem1, String fcItem2, String fcItem3) {
        ForecastController fcController = new ForecastController();
        fcController.getAndVerifySearchFilterResultFCItemnumbers(fcItem1, fcItem2, fcItem3);
        JLog.write("Successfully verified the Forecast Multiple Itemnumbers forecast UI  as " + fcItem1 + "and"
                + fcItem2 + "and" + fcItem3);
    }

    @Then("I verify {string} and {string} on PFC itemNumbers on search filter results")
    public void verifyPFGItemNumbersOnSearchFilterResults(String fcItem4, String fcItem5) {
        ForecastController fcController = new ForecastController();
        fcController.getAndVerifySearchFilterResultPFCItemnumbers(fcItem4, fcItem5);
        JLog.write("Successfully verified the Forecast Multiple PFGItemnumbers forecast UI  as " + fcItem4 + "and"
                + fcItem5);
    }

    @When("I set forecast value {string} on row {string}")
    public void setField(String val, String row) throws Throwable {
        AbstractPage.sleep(20);
        fctrller = new ForecastController();
        fctrller.setfcvalue(val, row);
        JLog.write("set forecast value " + val + " on row - " + row);
        checkForErrors();
    }

    @Then("I click on forecast arrow forward icon to copy the values in to eleven buckets")
    public void clickArrowFrd() {
        AbstractPage.sleep(20);
        fctrller = new ForecastController();
        fctrller.forecastarrowfrd();
        JLog.write("Successfully clicked on Forecast Arrow Forward ");
        checkForErrors();
    }

    @When("I enter {string} on extended Forecast Term textfield")
    public void enterExtendedForecast(String val) throws Throwable {
        AbstractPage.sleep(20);
        fctrller = new ForecastController();
        fctrller.setExtendedForecast(val);
        JLog.write("Set the extended Forecast Value " + val);
        checkForErrors();
    }

    @And("I click on the save button on forecast page")
    public void clickSaveButton() {
        fctrller = new ForecastController();
        fctrller.clickSaveButton();
        JLog.write("Clicked on Save Button");
        checkForErrors();
    }

    @When("I select {string} on Region Site Combobox")
    public void setDestnSiteCombo(String val) throws Throwable {
        fctrller = new ForecastController();
        fctrller.setRegionSite(val);
        checkForErrors();
    }

    @Then("I verify the {string} error message displayed on UploadPage")
    public void verifyMoreThanOneWarningMsg(String validationError) {
        fctrller = new ForecastController();
        fctrller.getAndVerifyErrmsg(validationError);
        checkForErrors();
    }

    @And("I enter {string} and {string} and {string} on Multiple {string} textfield on GCM role")
    public void enterthreeitemValuesGCM(String val1, String val2, String val3, String textField) {
        uC = new UploadController();
        val1 = UploadController.fc1;
        val2 = UploadController.fc2;
        val3 = UploadController.fc3;

        fctrller = new ForecastController();
        fctrller.setItemMultipleValFieldsGCM(val1, val2, val3, textField);
        checkForErrors();
    }

    @And("I enter {string} and {string} and {string} on Multiple {string} textfield on SuperGCM role")
    public void enterthreeitemValuesSuperGCM(String val4, String val5, String val6, String textField) {
        uC = new UploadController();
        val4 = UploadController.fc4;
        val5 = UploadController.fc5;
        val6 = UploadController.fc6;
        fctrller = new ForecastController();
        fctrller.setItemMultipleValFieldsSuperGCM(val4, val5, val6, textField);
        checkForErrors();
    }

    @And("I enter {string} and {string} and {string} on Multiple {string} textfield on adm role")
    public void enterthreeitemValuesAdm(String val7, String val8, String val9, String textField) {
        uC = new UploadController();
        val7 = UploadController.fc7;
        val8 = UploadController.fc8;
        val9 = UploadController.fc9;
        fctrller = new ForecastController();
        fctrller.setItemMultipleValFieldsadm(val7, val8, val9, textField);
        checkForErrors();
    }

    @And("I enter {string} and {string} on Multiple item {string} textfield on GCM role")
    public void entertwoitemValuesGCM(String val10, String val11, String textField) {
        uC = new UploadController();
        val10 = UploadController.fc10;
        val11 = UploadController.fc11;
        fctrller = new ForecastController();
        fctrller.setMultipleValFields(val10, val11, textField);
        checkForErrors();
    }

    @And("I enter {string} and {string} on Multiple item {string} textfield on SuperGCM role")
    public void entertwoitemValuesSGCM(String val12, String val13, String textField) {
        uC = new UploadController();
        val12 = UploadController.fc12;
        val13 = UploadController.fc13;
        fctrller = new ForecastController();
        fctrller.setMultipleValFields(val12, val13, textField);
        checkForErrors();
    }

    @And("I enter {string} and {string} on Multiple item {string} textfield on Adm role")
    public void entertwoitemValuesadm(String val14, String val15, String textField) {
        uC = new UploadController();
        val14 = UploadController.fc14;
        val15 = UploadController.fc15;
        fctrller = new ForecastController();
        fctrller.setMultipleValFields(val14, val15, textField);
        checkForErrors();
    }

    @And("I {string} the items to the Functional Group for Forecast {string} with {string}")
    public void createFG(String action, String fg, String btnName) throws Throwable {
        FunctionalGroupController controller = new FunctionalGroupController();
        // model = new FunctionalGroupModel();
        UploadController c = new UploadController();

        if ((fg.contains("ForecastFGGCM"))
                || (fg.contains("ForecastFGSuperGCM") || fg.contains("ForecastFGAdmin") || fg.contains("ForecastFGGCM1")
                        || fg.contains("ForecastFGSuperGCM1") || fg.contains("ForecastFGAdmin1"))) {
            fg = fg + timeStamp;
            JLog.write("Timestamp from Upload page");
        }
        // model.setGroupName(fg);
        // controller.setModel(model);
        controller.createAssignGroup(action, btnName, fg);

        JLog.screenCapture();
        AbstractPage.sleep(3);
        controller = new FunctionalGroupController();
        AbstractPage.sleep(1);
        boolean status = controller.verifyWarningPopupMsg("Do you want to assign responsibility to the items");
        JLog.screenCapture();
        if (status) {
            controller.clickButton("Yes");
            controller = new FunctionalGroupController();
            controller.assignResp();
            JLog.write("After assignResp");
            JLog.screenCapture();
            AbstractPage.sleep(3);
            controller = new FunctionalGroupController();
            // controller.clickButton("Close");
            controller.clickCloseBtnOnAssign();
            JLog.screenCapture();
            JLog.write("Screen after clicking close");
            AbstractPage.sleep(2);
            // if (action.equals("assign")) {
            // controller = new FunctionalGroupController();
            // controller.setGrpName(fg);
            // }
            controller = new FunctionalGroupController();
            String s = controller.getGrpNameValue();
            if (s.equals("")) {
                controller = new FunctionalGroupController();
                controller.setGrpName(fg);
                // controller.saveGroup();
            }

            controller.saveGroup();
        }
        if (action.contains("assign")) {
            controller = new FunctionalGroupController();
            String s = controller.getGrpNameValue();
            if (s.equals("")) {
                controller = new FunctionalGroupController();
                controller.setGrpNameToAssign(fg);
                controller.saveGroup();
            }
        }
        checkForErrors();
        JLog.write("Successfully done with " + action + " items to the group");
    }

    @And("I set FGgroup name as {string}")
    public void setGroupName(String cfg) throws Throwable {
        ForecastController fctrller = new ForecastController();

        if (cfg.contains("ForecastFGGCM")) {
            cfg = cfg + timeStamp;
            if (cfg.equalsIgnoreCase(fggcm))
                fctrller.setGrpName(fggcm);
            JLog.write("Fucntional group name set successfully on parent search page + " + fggcm);
        }

        if (cfg.contains("ForecastFGGCM1")) {
            cfg = cfg + timeStamp;
            if (cfg.equalsIgnoreCase(fggcm1))
                JLog.write("Timestamp from Upload page");
            fctrller.setGrpName(fggcm1);
            JLog.write("Fucntional group name set successfully on parent search page + " + fggcm1);

        }

        if (cfg.contains("ForecastFGSuperGCM")) {
            cfg = cfg + timeStamp;
            if (cfg.equalsIgnoreCase(fgSuperGCM))
                JLog.write("Timestamp from Upload page");
            fctrller.setGrpName(fgSuperGCM);
            JLog.write("Fucntional group name set successfully on parent search page + " + fgSuperGCM);

        }

        if (cfg.contains("ForecastFGSuperGCM1")) {
            cfg = cfg + timeStamp;
            if (cfg.equalsIgnoreCase(fgSuperGCM1))
                JLog.write("Timestamp from Upload page");
            fctrller.setGrpName(fgSuperGCM1);
            JLog.write("Fucntional group name set successfully on parent search page + " + fgSuperGCM1);

        }

        if (cfg.contains("ForecastFGAdmin")) {
            cfg = cfg + timeStamp;
            if (cfg.equalsIgnoreCase(fgadm))
                JLog.write("Timestamp from Upload page");
            fctrller.setGrpName(fgadm);
            JLog.write("Fucntional group name set successfully on parent search page + " + fgadm);

        }

        if (cfg.contains("ForecastFGAdmin1")) {
            cfg = cfg + timeStamp;
            if (cfg.equalsIgnoreCase(fgadm1))
                JLog.write("Timestamp from Upload page");
            fctrller.setGrpName(fgadm1);
            JLog.write("Fucntional group name set successfully on parent search page + " + fgadm1);

        }

        checkForErrors();
    }

    @And("I click on Create parent group")
    public void createparentFG() {
        ForecastController fctrller = new ForecastController();
        fctrller.getCreateparent();
        JLog.write("Create parent button clicked successfully");

    }

    @When("I save FGparent name as {string}")
    public void setParentNamegcm(String name) throws Exception {
        ForecastController fctrller = new ForecastController();
        if (name.equalsIgnoreCase("ForecastPFGGCM")) {
            name = name + timeStamp;
            fctrller.setParentName(name);
            JLog.write("Set parent name as " + name);
            JLog.write("Successfully created and added parent name.");

        } else if (name.equalsIgnoreCase("ForecastPFGSuperGCM")) {
            name = name + timeStamp;
            fctrller.setParentName(name);
            JLog.write("Set parent name as " + name);
            JLog.write("Successfully created and added parent name.");

        } else if (name.equalsIgnoreCase("ForecastPFGAdm")) {
            name = name + timeStamp;
            fctrller.setParentName(name);
            JLog.write("Set parent name as " + name);
            JLog.write("Successfully created and added parent name.");
        }

        checkForErrors();
    }

    @And("I click on Assign parent group")
    public void assignparentFG() {
        ForecastController fctrller = new ForecastController();
        fctrller.getAssignparent();
        JLog.write("Assign parent button clicked successfully");

    }

    @And("I set PFgroup name on parent edit page {string}")
    public void setassignparentName(String name) {
        ForecastController fctrller = new ForecastController();
        if (name.equalsIgnoreCase("ForecastPFGGCM"))
            name = name + timeStamp;
        fctrller.setGrpNameToAssign(name);
        JLog.write("Assigned parent group edited successfully");
    }

    @And("I set functional group name on forecast UI page {string}")
    public void getandVerifyCFGnameonForecastUI(String fg) {
        ForecastController fctrller = new ForecastController();

        // if ((cfg.contains("ForecastFG")) || ((cfg.contains("ForecastFG1"))))
        // {
        // cfg = cfg + timeStamp;
        // if (cfg.equalsIgnoreCase(fg))
        // JLog.write("Timestamp from Upload page");
        // }

        if ((fg.contains("ForecastFGGCM"))
                || (fg.contains("ForecastFGSuperGCM") || fg.contains("ForecastFGAdmin") || fg.contains("ForecastFGGCM1")
                        || fg.contains("ForecastFGSuperGCM1") || fg.contains("ForecastFGAdmin1"))) {
            fg = fg + timeStamp;
        }

        fctrller.setGrpNameonforecastview(fg);
        JLog.write("CFG details are successfully displayed on forecast UI " + fg);

    }

}