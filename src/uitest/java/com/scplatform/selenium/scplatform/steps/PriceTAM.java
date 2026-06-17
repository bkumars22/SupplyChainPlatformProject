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
import com.test.selenium.scplatform.modelViewController.FunctionalGroupModel;
import com.test.selenium.scplatform.modelViewController.PriceTAMController;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.google.common.base.Verify;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PriceTAM {

    FunctionalGroupController controller;
    FunctionalGroupModel model;
    PriceTAMController pController;
    General genObj;
    static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
    String multipleSearchParent;
    String itemOrFG;
    String itemBuisness;
    String itemNumber;
    String resp;
    String groupType;
    String pName;
    String fgName;
    String[] pgData;
    public static String fg = "XLOBPriceTAM" + timeStamp;
    public static String eMCfg = "EMCXLOBPriceTAM" + timeStamp;
    public static String dellfg = "DELLXLOBPriceTAM" + timeStamp;
    public static String xlobFG = "XLOBPriceTAMCommodity" + timeStamp;
    public static String dellfgMonthly = "DELLXLOBPriceTAMMonthly" + timeStamp;
    public static String dellfgeuarterly = "DELLXLOBPriceTAMEQuarterly" + timeStamp;
    public static String dellfguarterly = "DELLXLOBPriceTAMQuarterly" + timeStamp;
    public static String xlobfgDelttemp = "XLOBFGDT" + timeStamp;
    public static String xlobfgDelttemp1 = "XLOBFGDT1" + timeStamp;
    public static String xlobfgPirceDelttemp = "XLOBPriceFGDT" + timeStamp;

    String fgNameTimestamp;
    HarmonyLoginUI ui;
    Prop prop = Prop.getInstance();
    // = new HarmonyLoginUI();

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

    @And("I {string} the items to the XLOB Functional Group {string} with {string}")
    public void createFG(String action, String fg, String btnName) throws Throwable {
        controller = new FunctionalGroupController();
        // model = new FunctionalGroupModel();
        UploadController c = new UploadController();

        if ((fg.contains("XLOBPriceTAM")) || (fg.contains("EMCXLOBPriceTAM")) || (fg.contains("DELLXLOBPriceTAM"))
                || (fg.contains("XLOBPriceFGDT")) || (fg.contains("DELLXLOBPriceTAMMonthly"))
                || (fg.contains("DELLXLOBPriceTAMEQuarterly")) || (fg.contains("DELLXLOBPriceTAMQuarterly"))
                || (fg.contains("XLOBFGDT")) || (fg.contains("XLOBFGDT1"))) {

            fg = fg + timeStamp;

        }

        // model.setGroupName(fg);
        // controller.setModel(model);
        PriceTAMController Gpty = new PriceTAMController();
        Gpty.setGroupType("XLOB");
        JLog.screenCapture();
        AbstractPage.sleep(10);
        PriceTAMController platform = new PriceTAMController();
        platform.setPlatformType("AGILE");
        JLog.screenCapture();
        AbstractPage.sleep(10);
        PriceTAMController LOB = new PriceTAMController();
        LOB.setLOBType("MANUFACTURING");
        JLog.screenCapture();
        AbstractPage.sleep(10);
        controller = new FunctionalGroupController();
        String s = controller.getGrpNameValue();
        if (s.equals("")) {
            controller = new FunctionalGroupController();
            controller.setGrpName(fg);
            // controller.saveGroup();
        }

        controller.saveGroup();
//        controller.createAssignGroup(action, btnName, fg);
        controller = new FunctionalGroupController();
        AbstractPage.sleep(3);
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
//            controller = new FunctionalGroupController();
//            String s = controller.getGrpNameValue();
//            if (s.equals("")) {
//                controller = new FunctionalGroupController();
//                controller.setGrpName(fg);
//                // controller.saveGroup();
//            }
//
//            controller.saveGroup();
//        }
        if (action.contains("assign")) {
            controller = new FunctionalGroupController();
            String s1 = controller.getGrpNameValue();
            if (s1.equals("")) {
                controller = new FunctionalGroupController();
                controller.setGrpNameToAssign(fg);
                controller.saveGroup();
            }
        }
        controller.saveGroup();
        checkForErrors();
        JLog.write("Successfully done with " + action + " items to the group");
        }
    }

    @And("I {string} the items to the XLOB Price TAM Commodity Functional Group {string} with {string}")
    public void createXLOBFG(String action, String xlobFG, String btnName) throws Throwable {
        controller = new FunctionalGroupController();
        // model = new FunctionalGroupModel();
        PriceTAMController Gpty = new PriceTAMController();
        Gpty.setGroupType("XLOB");
        JLog.screenCapture();
        AbstractPage.sleep(3);
        PriceTAMController platform = new PriceTAMController();
        platform.setPlatformType("AGILE");
        AbstractPage.sleep(2);
        PriceTAMController LOB = new PriceTAMController();
        LOB.setLOBType("MANUFACTURING");
        JLog.screenCapture();
        UploadController c = new UploadController();

        if (xlobFG.contains("XLOBPriceTAMCommodity")) {

            xlobFG = xlobFG + timeStamp;

        }

        controller.createAssignGroup(action, btnName, xlobFG);
        AbstractPage.sleep(3);
//        PriceTAMController Gpty = new PriceTAMController();
//        Gpty.setGroupType("XLOB");
//        JLog.screenCapture();
//        AbstractPage.sleep(3);
        controller = new FunctionalGroupController();
//        AbstractPage.sleep(1);
//        boolean status = controller.verifyWarningPopupMsg("Do you want to assign responsibility to the items");
//        JLog.screenCapture();
//        if (status) {
//            controller.clickButton("Yes");
//            controller = new FunctionalGroupController();
//            controller.assignResp();
//            JLog.write("After assignResp");
//            JLog.screenCapture();
//            AbstractPage.sleep(3);
//            controller = new FunctionalGroupController();
//            // controller.clickButton("Close");
//            controller.clickCloseBtnOnAssign();
//            JLog.screenCapture();
//            JLog.write("Screen after clicking close");
//            AbstractPage.sleep(2);
//            // if (action.equals("assign")) {
//            // controller = new FunctionalGroupController();
//            // controller.setGrpName(fg);
//            // }
//            controller = new FunctionalGroupController();
//            String s = controller.getGrpNameValue();
//            if (s.equals("")) {
//                controller = new FunctionalGroupController();
//                controller.setGrpName(xlobFG);
//                // controller.saveGroup();
//            }
//
//            controller.saveGroup();
//        }
//        if (action.contains("assign")) {
//            controller = new FunctionalGroupController();
//            String s = controller.getGrpNameValue();
//            if (s.equals("")) {
//                controller = new FunctionalGroupController();
//                controller.setGrpNameToAssign(xlobFG);
//                controller.saveGroup();
//            }
//        }
        checkForErrors();
        JLog.write("Successfully done with " + action + " items to the group");
    }

    @And("I set FG name as {string}")
    public void setFGroupName(String fg) throws Exception {
        controller = new FunctionalGroupController();
        if ((fg.contains("EMCXLOBPriceTAM"))) {
            UploadController c = new UploadController();
            fg = fg + timeStamp;
        }
        controller.setGrpName(fg);
        checkForErrors();
    }

    @And("I enter {string} and {string} on Multiple {string} textfield on FG page")
    public void xlobdeleteitems(String va1, String va2, String textField) {
        UploadController uC = new UploadController();
        va1 = UploadController.xlobdeltitem1;
        va2 = UploadController.xlobdeltitem2;
        pController = new PriceTAMController();
        pController.setmultipleItemfield(va1, va2, textField);
        checkForErrors();
    }

    @Then("I verify {string} itemNumber on search filter results")
    public void verifypricetamitemnumber(String itemnumber) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySearchFilterResultpriceStatus(itemnumber);
        JLog.write("Successfully verified the itemnumber on price tam UI as " + itemnumber);
        checkForErrors();
    }

    @Then("I verify {string} FGname on search filter results")
    public void verifypricetamfgname(String cfgName) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySearchFilterResultfgStatus(cfgName);
        JLog.write("Successfully verified the itemnumber on price tam UI as " + cfgName);
        checkForErrors();
    }

    @Then("I verify {string} supplier on search filter results")
    public void verifypricetamsupplier(String supplier) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySearchFilterResultsupplierStatus(supplier);
        JLog.write("Successfully verified the supplier on price tam UI as " + supplier);
        checkForErrors();
    }

    @Then("I verify {string} mpn on search filter results")
    public void verifympnvalue(String mpn) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySearchFilterResultpriceStatus(mpn);
        JLog.write("Successfully verified the mpn on price tam UI as " + mpn);
        checkForErrors();
    }

    @Then("I verify {string} FG name on price TAM results page")
    public void verifyStatusOnSearchFilterResults(String FGName) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifyFGNameonPriceTAM(FGName);
        JLog.write("Successfully verified the FG name on Price tam results as " + FGName);
    }

    @Then("I verify {string} itemNumber on price TAM results page")
    public void verifyStatusOnitemnumber(String itemnumber) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySearchFilterResultpriceStatus(itemnumber);
        JLog.write("Successfully verified the itemnumber on Price tam results as " + itemnumber);
    }

    @Then("I verify {string} MPN Values on price TAM results page")
    public void verifyStatusOnmpn1(String mpn) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySearchFilterResultMPNStatus(mpn);
        JLog.write("Successfully verified the mpn on Price tam results as " + mpn);
    }

    @Then("I verify {string} Supplier on price TAM results page")
    public void verifySupplier(String Supplier) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySearchFilterResultsupplierStatus(Supplier);
        JLog.write("Successfully verified the Supplier on Price tam results as " + Supplier);
    }

    @Then("I verify {string} CostType on price TAM results page")
    public void verifyCostType(String CostType) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifyCostType(CostType);
        JLog.write("Successfully verified the CostType on Price tam results as " + CostType);
    }

    @Then("I verify {string} Destination on price TAM results page")
    public void verifyDestination(String Destination) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifyDestination(Destination);
        JLog.write("Successfully verified the Destination on Price tam results as " + Destination);
    }

    @Then("I verify {string} SiteTAM on price TAM results page")
    public void verifySiteTAM(String SiteTAM) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySiteTAM(SiteTAM);
        JLog.write("Successfully verified the SiteTAM on Price tam results as " + SiteTAM);
    }

    @Then("I verify {string} PriceValue on price TAM results page")
    public void verifyPriceValue(String PriceValue) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifyPriceValue(PriceValue);
        JLog.write("Successfully verified the PriceValue on Price tam results as " + PriceValue);
    }

    @Then("I verify {string} Pricebuckets on price TAM results page")
    public void verifyPriceBuckets(String value) throws Throwable {
        pController = new PriceTAMController();
        pController.getAndVerifySearchFilterResultpricebuckets(value);
        JLog.write("Successfully verified the PriceValue on Price tam results as " + value);
    }

    @Then("I verify {string} Price TAM download icon disbaled on UI page")
    public void verifyPriceTAMDownloadIcon(String fileDownload) throws Throwable {
        PriceTAMController pController = new PriceTAMController();
        pController.getAndVerifyfiledownload(fileDownload);
        JLog.write("Successfully verfied the download icon is disbaled on priceTAM Page");

    }

    @When("I set TAMSite value on search filters {string}")
    public void setField(String site) throws Throwable {
        AbstractPage.sleep(20);
        PriceTAMController pController = new PriceTAMController();
        pController.setTAMSite(site);
        JLog.write("set TAM site value " + site);
        checkForErrors();
    }

    @Then("I verify {string} tamsite details on results page")
    public void tamsite(String tamsite) throws Throwable {
        AbstractPage.sleep(20);
        PriceTAMController pController = new PriceTAMController();
        pController.setTAMSite(tamsite);
        JLog.write("Verified TAM site on results page " + tamsite);
        checkForErrors();
    }

    @Then("I verify {string} rows listed without selection option on Price Tam Page")
    public void verifyRowsWithoutCheckbox(String rowCount) {
        int rows = Integer.parseInt(rowCount);
        JLog.write("Exp Rows=" + rows);
        PriceTAMController pController = new PriceTAMController();
        int actRows = pController.getSearchResultRows();
        JLog.write("Act Rows=" + actRows);
        Verify.verify(rows == actRows, "No of rows not matching");
        checkForErrors();
    }

    @And("I set the functionalgroup name as {string}")
    public void setGroupName(String cfg) throws Throwable {
        ForecastController fctrller = new ForecastController();
        if (cfg.contains("XLOBFGDT")) {
            cfg = cfg + timeStamp;
            fctrller.setGrpName(xlobfgDelttemp);
            JLog.write("Fucntional group name set successfully on parent search page + " + xlobfgDelttemp);
            return;
        }
        if (cfg.contains("XLOBFGDT1")) {
            cfg = cfg + timeStamp;
            fctrller.setGrpName(xlobfgDelttemp1);
            JLog.write("Fucntional group name set successfully on parent search page + " + xlobfgDelttemp1);
            return;
        }
        fctrller.setGrpName(cfg);
    }

    @And("I select the tam exists as {string}")
    public void tamexists(String val) throws Throwable {
        AbstractPage.sleep(20);
        PriceTAMController pController = new PriceTAMController();
        pController.setTAMExists(val);
        JLog.write("Verified TAM exists on results page " + val);
        checkForErrors();
    }

}