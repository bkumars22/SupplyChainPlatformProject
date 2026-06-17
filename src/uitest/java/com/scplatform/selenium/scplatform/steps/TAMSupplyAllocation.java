/*
 * TAMSupplyAllocation.java
 * Created on Mar 19, 2021
 *
 * Copyright (c) 2021 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.steps;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.test.selenium.common.FileHelper;

import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.FunctionalGroupController;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.modelViewController.SupplyAllocationController;
import com.test.selenium.scplatform.ui.main.download.SCPlatformDownloadController;
import com.test.selenium.scplatform.ui.main.upload.PriceTAMUploadController;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.google.common.base.Verify;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TAMSupplyAllocation {

  static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
  SupplyAllocationController tamController;
  String recordsCount;
  String fgName;
  String fgTYpe;
  HarmonyLoginUI ui;
  General gn;
  MTCMController mc;
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

  @Then("I verify the tam {string} checkbox status as {string}")
  public void verifyCheckBoxStatus(String val, String status) {
    tamController = new SupplyAllocationController();
    boolean s;
    s = tamController.isTAMCheckboxChecked(val);
    JLog.write("Found status is " + s);
    JLog.screenCapture();
    if (status.equals("checked"))
      Verify.verify(s, "Checkbox is not selected.");
    else
      Verify.verify(!(s), "Checkbox is unselected.");
  }

  @When("I {string} the tam {string} checkbox")
  public void selectCheckBox(String selection, String name) {
    tamController = new SupplyAllocationController();
    if (selection.equals("check") && (!tamController.isTAMCheckboxChecked(name))) {
      tamController.checkUncheckTamOptions(name);
      JLog.write("Checkbox checked.");
    } else if (selection.equals("uncheck") && tamController.isTAMCheckboxChecked(name)) {
      tamController.checkUncheckTamOptions(name);
      JLog.write("Checkbox unchecked.");
    }
  }

  @And("I set {string} as today on Start Date Calendar")
  public void setDatePickerDate(String label) {
    tamController = new SupplyAllocationController();
    tamController.setStartDateOnSuppExceptionReportPage();
    JLog.write("Selected today's date as start Date.");
    checkForErrors();
  }

  @When("I click {string} on the warning popup with message as {string}")
  public void clickWarningPopup(String btnName, String msg) {
    JLog.screenCapture();
    AbstractPage page = new AbstractPage();
    By by = By.xpath("//section[@class='eto-modal__body']//p");
    tamController = new SupplyAllocationController();
    WebElement e = page.get(by);
    String s = e.getText();
    if (s.equals(""))
      s = e.getAttribute("innerText");
    JLog.write("Message found on popup is - " + s);
    Verify.verify(tamController.verifyWarningPopupMsg(msg), "Unable to verify the warning popup msg " + msg);
    e = page.get(By.xpath(
        "//button[contains(@class,'eto-btn') and contains(text(),'" + btnName + "') and @data-modal-close]"));
    page.executeJavaScript("arguments[0].click();", e);
    JLog.write("Successfully verified validation msg and clicked on " + btnName + " button.");
    checkForErrors();

  }

  @Then("I verify total alloc value font is bold")
  public void verifyAllocValFont() throws Throwable {
    tamController = new SupplyAllocationController();
    tamController.verifyTotalSuppAllocFont();
    checkForErrors();
  }

  /**
   * Reads the CustomerItemIdentifier (column A, first data row) from the given xlsx data file
   * and enters it into the specified filter field on the Allocation Management page.
   * This avoids hardcoding item numbers in feature files or step definitions.
   */
  @And("I enter item number from {string} xlsx on {string} textfield")
  public void enterItemNumberFromXlsx(String fileName, String fieldName) throws Exception {
    tamController = new SupplyAllocationController();
    File xlsxFile = FileHelper.getResourceFile("com/scplatform/selenium/scplatform/data/" + fileName + ".xlsx");
    try (FileInputStream fis = new FileInputStream(xlsxFile);
         XSSFWorkbook wb = new XSSFWorkbook(fis)) {
      // Row 2 (index 2) = first data row; Col 0 = CustomerItemIdentifier
      String itemNumber = wb.getSheetAt(0).getRow(2).getCell(0).getStringCellValue();
      JLog.write("Item number read from xlsx [" + fileName + "]: " + itemNumber);
      tamController.setEleWithValue(fieldName, itemNumber);
    }
    checkForErrors();
  }

  @And("I set start date on Allocation Management History page")
  public void setStartDateOnAllocMangmnt() throws Throwable {
    tamController = new SupplyAllocationController();
    tamController.setStartDate();
    JLog.write("Start date set as 1st of previous month");
    checkForErrors();
  }

  @And("I click on {string} TAM Planner")
  public void selectRegionTAM(String tabName) throws Throwable {
    tamController = new SupplyAllocationController();
    tamController.clickTabBtn(tabName);
    AbstractPage.sleep(2);
    JLog.write("I selected " + tabName + " TAM Planner");
    checkForErrors();
  }

  // @And("I click on Site TAM Planner")
  // public void selectSiteTAM() throws Throwable {
  // MTCMController.setOverrideContext("contentFrame");
  // SupplyAllocationController tamController = new
  // SupplyAllocationController();
  // tamController.clickSiteTabBtn();
  // AbstractPage.sleep(2);
  // JLog.write("I selected Site TAM Planner");
  // checkForErrors();
  // }

  @And("I select filter {string} on Price Tam")
  public void selectFilter(String text) {
    tamController = new SupplyAllocationController();
    tamController.selectFilterOnPriceTam(text);
    checkForErrors();
  }

  @And("I set Supply Allocation Value {string}")
  public void setGlobalSupplyAllocationValue(String allocationValue) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.setSupplyAllocationvalue(allocationValue);
    JLog.write("Set the supplier allocation value" + allocationValue);
    checkForErrors();
  }

  @And("I set Supply Allocation Value on complete row {string} with {string}")
  public void setSupplyAllocationValueOnFullRow(String row, String allocationValue) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.setSupplyAllocationvalueOnFullRow(row, allocationValue);
    JLog.write("Set the supplier allocation value as " + allocationValue + " on row" + row);
    checkForErrors();
  }

  @Then("I verify Supply Allocation Value {string} on all fields")
  public void verifyGlobalSupplyAllocationValue(String allocationValue) throws Exception {
    tamController = new SupplyAllocationController();
    JLog.screenCapture();
    boolean status = tamController.verifySupplyAllocationvalue(allocationValue);
    Verify.verify(status, "Failed to verify Supply allocation value.");
    JLog.write("Verified supplier allocation value" + allocationValue);
    checkForErrors();
  }

  @Then("I verify Inherit checkbox with name {string} status as {string} on all fields")
  public void verifyInheritCheckBoxStatus(String name, String status) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.verifyInheritCheckBoxStatus(name, status);
    JLog.write("Succesfully verified the checkbox status of inherit checkbox");
    checkForErrors();
  }

  @And("I click on {string} dropdown icon")
  public void clickDrpDwnIcon() throws Exception {
    tamController = new SupplyAllocationController();
    tamController.clickDownloadExpandBtn();
    AbstractPage.sleep(2);
    checkForErrors();
  }

  @Then("I verify select button {string} status as {string}")
  public void verifySelectBtnStatus(String btnName, String status) throws Exception {
    tamController = new SupplyAllocationController();
    boolean btnStatus = tamController.isBtnSelected(btnName);
    if (status.equals("checked"))
      Verify.verify(btnStatus, "Failed to verify button status as " + status);
    else
      Verify.verify(!btnStatus, "Failed to verify button status as " + status);
    JLog.write("Successfully verified btn " + btnName + "status as " + status);
    checkForErrors();
  }

  @And("I set Item Allocation Value {string}")
  public void setGlobalItemAllocationValue(String allocationValue) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.setItemAllocationvalue(allocationValue);
    JLog.write("Set the Item allocation value" + allocationValue);
    checkForErrors();
  }

  @When("I set multiple Supplier Allocation value {string} on row {string} and col {string}")
  public void setMultipleSuppAllocValOnRowColumn(String val, String row, String col) {
    tamController = new SupplyAllocationController();
    int c = Integer.parseInt(col);
    tamController.setMultipleSupplyAllocValue(val, row, c);
    JLog.screenCapture();
    checkForErrors();
  }

  @Then("I verify the search results has MRP Site value displayed")
  public void verifyResultsName() {
    tamController = new SupplyAllocationController();
    tamController.verifySearchResultsMRPSiteValue();
    JLog.write("Successfully verified MRP Site values.");
  }

  @When("I set multiple Item Allocation value {string} on row {string}")
  public void setMultipleItemAllocValOnRowColumn(String val, String row) {
    tamController = new SupplyAllocationController();
    int col = 1;
    tamController.setMultipleItemAllocValue(val, row, col);
    JLog.screenCapture();
    checkForErrors();
  }

  @When("I verify multiple Supplier Allocation value {string} on row {string}")
  public void verifyMultipleSuppAllocValOnRowColumn(String val, String row) {
    tamController = new SupplyAllocationController();
    tamController.verifyMultipleSupplyAllocValue(val, row);
    JLog.screenCapture();
    checkForErrors();
  }

  @Then("I verify Item Allocation Value {string} on column {string}")
  public void verifyItemAllocationValueOnColumn(String allocationValue, String col) throws Exception {
    tamController = new SupplyAllocationController();
    int column = Integer.parseInt(col);
    JLog.screenCapture();
    tamController.verifyItemAllocationvalueOnCol(allocationValue, column);
    JLog.write("Verified Item allocation value " + allocationValue + " on column=" + col);
    checkForErrors();
  }

  @Then("I verify Item Allocation Value {string} on row {string}")
  public void verifyItemAllocationValueOnRow(String allocationValue, String row) throws Exception {
    tamController = new SupplyAllocationController();
    JLog.screenCapture();
    tamController.verifyItemAllocationvalueOnRow(allocationValue, row);
    JLog.write("Verified Item allocation value " + allocationValue + " on row=" + row);
    checkForErrors();
  }

  @Then("I verify Item Allocation Value {string} on all rows and columns")
  public void verifyItemAllocOnAllRowsColumn(String allocationValue) throws Exception {
    tamController = new SupplyAllocationController();
    JLog.screenCapture();
    tamController.verifyItemAllocationvalueOnCol(allocationValue);
    JLog.write("Verified Item allocation value " + allocationValue + "  on allrows and columns");
    checkForErrors();
  }

  @When("I click on close button on delete screen")
  public void clickCloseBtnOnDelScreen() throws Exception {
    tamController = new SupplyAllocationController();
    JLog.screenCapture();
    tamController.clickCloseBtnOnDeleteScreen();
    checkForErrors();
  }

  public void clearCheckBoxForBuyerRole() throws Throwable {
    ui = new HarmonyLoginUI();
    ui.logout_mtcm();
    // InputStream fo1 = new FileInputStream(prop.getRootDir() +
    // "scplatform/data/loginUserDetails.properties");
    // Properties p1 = new Properties();
    // p1.load(fo1);
    // String un = p1.getProperty("login");
    ui.login_harmony_mtcm("mtcmUser");
    ui.navHarmonyMTCM("Administration", "Manage Roles");
    gn = new General();
    gn.clickName("BUYER");
    Admin ad = new Admin();
    ad.clickTab("Business Document", "Manage Roles");
    // gn.selectCheckBox("uncheck", "accessRights(SUPPLY_ALLOC)");
    selectCheckBox("uncheck", "Delete-global");
    selectCheckBox("uncheck", "Delete-region");
    selectCheckBox("uncheck", "Delete-site");
    gn.clickSaveButton();
    ad.clickTab("Workflow", "Manage Roles");
    ad.clickTab("Business Document", "Manage Roles");
    verifyCheckBoxStatus("Delete-global", "unchecked");
    verifyCheckBoxStatus("Delete-region", "unchecked");
    verifyCheckBoxStatus("Delete-site", "unchecked");
    ui.logout_mtcm();
  }

  @Then("I verify Supply Allocation Value {string} on column {string}")
  public void verifySupplyAllocationValueOnColumn(String allocationValue, String col) throws Exception {
    tamController = new SupplyAllocationController();
    int column = Integer.parseInt(col);
    JLog.screenCapture();
    tamController.verifySupplyAllocationvalue(allocationValue);
    JLog.write("Verified supplier allocation value " + allocationValue + " on column=" + col);
    checkForErrors();
  }

  @Then("I verify Item Allocation Value {string} on all fields")
  public void verifyGlobalItemAllocationValue(String allocationValue) throws Exception {
    AbstractPage.sleep(1);
    tamController = new SupplyAllocationController();
    JLog.screenCapture();
    boolean status = tamController.verifyItemAllocationvalueOnAllFields(allocationValue);
    Verify.verify(status, "Failed to verify Item allocation value.");
    JLog.write("Verified Item allocation value" + allocationValue);
    checkForErrors();
  }

  @When("I enter itemNumbers on Multiple ItemNumber textfield")
  public void setMultipleItemNumbers() throws Exception {
    tamController = new SupplyAllocationController();
    tamController.setMultipleItemValues();
    checkForErrors();
  }

  @When("I enter itemNumbers on Multiple ItemNumber textfield for item alloc testData creation")
  public void setMultipleItemNumber() throws Exception {
    tamController = new SupplyAllocationController();
    tamController.setMultipleItemValue();
    checkForErrors();
  }

  @And("I verify the fields got cleared on allocation page")
  public void verifyDataClearedOnALlocation() throws Exception {
    tamController = new SupplyAllocationController();
    Verify.verify(tamController.getElementValue("groupName").equals(""), "Text field groupName is not cleared");
    Verify.verify(tamController.getElementValue("itemNumber").equals(""), "Item Number is not cleared");
    JLog.write("Succesfully verfied clear button action.");
    checkForErrors();
  }

  @And("I set {string} dropdown with Value {string}")
  public void setDropdownValue(String dropDwnLabelName, String value) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.setDropDown(dropDwnLabelName, value);
    JLog.screenCapture();
    AbstractPage.sleep(5);
    checkForErrors();
  }

  @And("I click on Supply Allocation delete Button")
  public void clickDelAlloc() throws Exception {
    tamController = new SupplyAllocationController();
    tamController.clickAllocDeletionBtn();
    JLog.screenCapture();
    checkForErrors();
  }

  @And("I create testData for single suppItem delete scenario on {string} level")
  public void testDataSingleSuppItem(String level) throws Throwable {
    removeAllExistingAllocFromALlLevels();
    if (level.contains("global"))
      setGlobalLevel();
    else if (level.contains("region"))
      setRegionLevel();
    else if (level.contains("AllocDelOnGlob"))
      setRegSiteAlloc();
    else if (level.contains("site"))
      setSiteLevel();
  }

  @And("I clear testData for multiple item alloc on {string} level")
  public void clearTestDataMultipleItemAlloc(String level) throws Throwable {
    removeAllExistingAllocAndSetTestData("AutoMultipleItemGroupAlloc");
  }

  @And("I clear testData by deleting tam alloc for {string} from {string} level")
  public void clearMultipleSuppItemAlloc(String grp, String level) throws Throwable {
    removeAllExistingAlloc(grp);
  }

  @And("I enter the groupName {string} on {string} Field")
  public void enterTextFieldElementValue(String val, String textFieldName) {
    tamController = new SupplyAllocationController();
    if (val.contains("TestFG")) {
      val = val + FunctionalGroup.timeStamp;
      JLog.write("Getting timestamp from FG");
    }
    if (!val.contains("AUTO_AIR_AUS") && !val.contains("TestFG") && !val.contains("SEBRING")
        && !val.contains("CYCFG_TEST004") && !val.contains("fg-update") && !val.contains("CFG 2")
        && !val.contains("0084") && !val.contains("UI201905241439") && !val.contains("00T2N_CFG")) {
      UploadController uC = new UploadController();
      val = val + uC.getTimeStamp();
      JLog.write("Getting timestamp from upload controller");
    }
    tamController.setEleWithValue(textFieldName, val);
    JLog.write("Successfully set " + textFieldName + " with value== " + val);
    checkForErrors();
  }

  public void changeFGToActive(String fName) throws Throwable {
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
    gn.clickButton("Clear");
    FunctionalGroup fg = new FunctionalGroup();
    fg.setGroupName(fName);
    gn.clickButton("Apply");
    gn.clickName(fName);
    gn.selectCheckBox("uncheck", "status");
    fg.createFG("update", fName, "Save All");
  }

  public void setRegionLevel() throws Throwable {
    ui = new HarmonyLoginUI();
    gn = new General();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.clickButton("Clear");
    selectRegionTAM("Global");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    JLog.write("******************checking for any supplier item issue*******************");
    JLog.screenCapture();
    mc = new MTCMController();
    if (mc.getSuccessMessage().contains("FG Item")) {
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      gn.clickButton("Clear");
      FunctionalGroup grp = new FunctionalGroup();
      grp.setGroupName("AutoItemGroupDelete");
      gn.clickButton("Apply");
      gn.clickName("AutoItemGroupDelete");
      JLog.write("*************After Getting No supplier message *********************");
      JLog.screenCapture();
      ui.navHarmonyMTCM("Search", "Item AVL");
      gn.clickButton("Clear");
      gn.setItemNumber("AutoItemAllocDel");
      gn.clickButton("Apply");
      mc.scrollHorizontally(270);
      AbstractPage.sleep(3);
      JLog.write("********************After searching the item on search AVL page************************");
      JLog.screenCapture();
      JLog.fail("Failed due to no supplier message");
    }
    JLog.write("***********No supplier item issue found.*****************************");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    setGlobalSupplyAllocationValue("100");
    setGlobalItemAllocationValue("100");
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("100");
    verifyGlobalItemAllocationValue("100");
    // changing fg to active
    changeFGToActive("AutoItemGroupDelete");
    // for region
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    selectRegionTAM("Region");
    setDropdownValue("Region", "APCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    setGlobalSupplyAllocationValue("160");
    setGlobalItemAllocationValue("100");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("160");
    verifyGlobalItemAllocationValue("100");
    // for site
    // ui = new HarmonyLoginUI();
    // ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.expandFilter();
    gn.clickButton("Clear");
    selectRegionTAM("Site");
    setDropdownValue("Region", "APCC");
    setDropdownValue("Site", "APCC-APCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    JLog.write("******************checking for any supplier item issue*******************");
    JLog.screenCapture();
    mc = new MTCMController();
    if (mc.getSuccessMessage().contains("FG Item")) {
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      gn.clickButton("Clear");
      FunctionalGroup grp = new FunctionalGroup();
      grp.setGroupName("AutoItemGroupDelete");
      gn.clickButton("Apply");
      gn.clickName("AutoItemGroupDelete");
      JLog.write("*************After Getting No supplier message *********************");
      JLog.screenCapture();
      ui.navHarmonyMTCM("Search", "Item AVL");
      gn.clickButton("Clear");
      gn.setItemNumber("AutoItemAllocDel");
      gn.clickButton("Apply");
      mc.scrollHorizontally(270);
      AbstractPage.sleep(3);
      JLog.write("********************After searching the item on search AVL page************************");
      JLog.screenCapture();
      JLog.fail("Failed due to no supplier message");
    }
    JLog.write("***********No supplier item issue found.*****************************");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("uncheck", "inheritValue");
    // gn.selectCheckBox("check", "inheritItemValue");
    // setGlobalSupplyAllocationValue("160");
    // setGlobalItemAllocationValue("100");
    selectAllSupplierIcon("1");
    // selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("160");
    verifyGlobalItemAllocationValue("100");
    // setting other region with some default value
    // ui = new HarmonyLoginUI();
    // ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.expandFilter();
    gn.clickButton("Clear");
    selectRegionTAM("Region");
    setDropdownValue("Region", "BCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    setGlobalSupplyAllocationValue("110");
    setGlobalItemAllocationValue("100");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("110");
    verifyGlobalItemAllocationValue("100");
    // above region's site
    gn.expandFilter();
    gn.clickButton("Clear");
    selectRegionTAM("Site");
    setDropdownValue("Region", "BCC");
    setDropdownValue("Site", "BCC-BFC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    gn.waitTillPageLoads("30");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("check", "inheritValue");
    // gn.selectCheckBox("check", "inheritItemValue");
    // setGlobalSupplyAllocationValue("110");
    // setGlobalItemAllocationValue("100");
    selectAllSupplierIcon("1");
    // selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("110");
    verifyGlobalItemAllocationValue("100");
  }

  public void setSiteLevel() throws Throwable {
    ui = new HarmonyLoginUI();
    gn = new General();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.clickButton("Clear");
    selectRegionTAM("Global");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    setGlobalSupplyAllocationValue("90");
    setGlobalItemAllocationValue("100");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("90");
    verifyGlobalItemAllocationValue("100");
    // changing fg to active
    changeFGToActive("AutoItemGroupDelete");
    // for region
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    selectRegionTAM("Region");
    setDropdownValue("Region", "APCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    setGlobalSupplyAllocationValue("80");
    setGlobalItemAllocationValue("100");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("80");
    verifyGlobalItemAllocationValue("100");
    // for site
    // ui = new HarmonyLoginUI();
    // ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.expandFilter();
    gn.clickButton("Clear");
    selectRegionTAM("Site");
    setDropdownValue("Region", "APCC");
    setDropdownValue("Site", "APCC-APCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    setGlobalSupplyAllocationValue("140");
    setGlobalItemAllocationValue("100");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("140");
    verifyGlobalItemAllocationValue("100");
    // setting other region - site with default value
    // ui = new HarmonyLoginUI();
    // ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.expandFilter();
    gn.clickButton("Clear");
    selectRegionTAM("Region");
    setDropdownValue("Region", "BCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("uncheck", "inheritValue");
    gn.selectCheckBox("uncheck", "inheritItemValue");
    // setGlobalSupplyAllocationValue("100");
    // setGlobalItemAllocationValue("100");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("90");
    verifyGlobalItemAllocationValue("100");
    // site
    gn.expandFilter();
    gn.clickButton("Clear");
    selectRegionTAM("Site");
    setDropdownValue("Region", "BCC");
    setDropdownValue("Site", "BCC-BFC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("uncheck", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    // setGlobalSupplyAllocationValue("100");
    // setGlobalItemAllocationValue("100");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("90");
    verifyGlobalItemAllocationValue("100");
  }

  public void setRegSiteAlloc() throws Throwable {
    ui = new HarmonyLoginUI();
    gn = new General();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.clickButton("Clear");
    selectRegionTAM("Global");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    setGlobalSupplyAllocationValue("200");
    setGlobalItemAllocationValue("100");
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("200");
    verifyGlobalItemAllocationValue("100");
    // changing fg to active
    changeFGToActive("AutoItemGroupDelete");
    // for region
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    selectRegionTAM("Region");
    setDropdownValue("Region", "APCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("uncheck", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("200");
    verifyGlobalItemAllocationValue("100");
    // for site
    // ui = new HarmonyLoginUI();
    // ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.expandFilter();
    gn.clickButton("Clear");
    selectRegionTAM("Site");
    setDropdownValue("Region", "APCC");
    setDropdownValue("Site", "APCC-APCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("200");
    verifyGlobalItemAllocationValue("100");
  }

  public void setGlobalLevel() throws Throwable {
    ui = new HarmonyLoginUI();
    gn = new General();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.clickButton("Clear");
    selectRegionTAM("Global");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    setGlobalSupplyAllocationValue("200");
    setGlobalItemAllocationValue("100");
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("200");
    verifyGlobalItemAllocationValue("100");
    // changing fg to active
    changeFGToActive("AutoItemGroupDelete");
    // for region
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    selectRegionTAM("Region");
    setDropdownValue("Region", "APCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("uncheck", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("200");
    verifyGlobalItemAllocationValue("100");
    // for site
    // ui = new HarmonyLoginUI();
    // ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.expandFilter();
    gn.clickButton("Clear");
    selectRegionTAM("Site");
    setDropdownValue("Region", "APCC");
    setDropdownValue("Site", "APCC-APCC");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    tamController = new SupplyAllocationController();
    tamController.selectButton("select", "allowHedging");
    gn = new General();
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("200");
    verifyGlobalItemAllocationValue("100");
  }

  public void removeAllExistingAlloc(String fName) throws Throwable {
    ui = new HarmonyLoginUI();
    gn = new General();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.clickButton("Clear");
    selectRegionTAM("Global");
    enterTextFieldElementValue(fName, "groupName");
    gn.clickButton("Apply");
    gn.clickButton("Delete");
    // MTCMController controller = new MTCMController();
    mc = new MTCMController();
    boolean status = mc.verifyWarningPopupMsgs("There is no TAM for selected FG.");
    JLog.screenCapture();
    JLog.write("Successfully verified warning message-" + "There is no TAM for selected FG.");
    boolean noTamStatus = mc.verifyWarningPopupMsgs("Total Alloc");
    JLog.screenCapture();
    JLog.write("Successfully verified warning message-" + "There is no TAM for selected FG.");
    if (status || noTamStatus) {
      mc.clickButton("Ok");
    } else {
      delAlloc("both");
      gn.selectCheckBox("check", "selectAllDeleteSite");
      clickDelAlloc();
      gn.verifyWarningMsgsAndConfirmOrReject("Are you sure you want to delete", "Yes");
      AbstractPage page = new AbstractPage();
      MTCMController.setOverrideContext("null");
      mc = new MTCMController();
      if (page.exists(By.xpath("//section[@class='eto-modal__body']//p"))) {
        gn.clickWarningPopup("OK", "No Records Selected");
      }
      verifyGlobalSupplyAllocationValue("");
      verifyGlobalItemAllocationValue("");
      JLog.write("***********************After deleting all allocations:********************");
      JLog.screenCapture();
      gn.verifyMsg("You are editing TAM against an Inactive Functional Group");
      JLog.write("Successfully cleared test data from Global level");
    }
  }

  public void removeAllExistingAllocAndSetTestData(String fName) throws Throwable {
    // global
    ui = new HarmonyLoginUI();
    gn = new General();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.clickButton("Clear");
    selectRegionTAM("Global");
    enterTextFieldElementValue(fName, "groupName");
    gn.clickButton("Apply");
    gn.clickButton("Delete");
    // MTCMController controller = new MTCMController();
    mc = new MTCMController();
    boolean status = mc.verifyWarningPopupMsgs("There is no TAM for selected FG.");
    JLog.screenCapture();
    JLog.write("Successfully verified warning message-" + "There is no TAM for selected FG.");
    boolean noTamStatus = mc.verifyWarningPopupMsgs("Total Alloc");
    JLog.screenCapture();
    JLog.write("Successfully verified warning message-" + "There is no TAM for selected FG.");
    if (status || noTamStatus) {
      mc.clickButton("Ok");
    } else {
      delAlloc("both");
      gn.selectCheckBox("check", "selectAllDeleteSite");
      clickDelAlloc();
      gn.verifyWarningMsgsAndConfirmOrReject("Are you sure you want to delete", "Yes");
      AbstractPage page = new AbstractPage();
      MTCMController.setOverrideContext("null");
      mc = new MTCMController();
      if (page.exists(By.xpath("//section[@class='eto-modal__body']//p"))) {
        gn.clickWarningPopup("OK", "No Records Selected");
      }
      verifyGlobalSupplyAllocationValue("");
      verifyGlobalItemAllocationValue("");
      JLog.write("***********************After deleting all allocations:********************");
      JLog.screenCapture();
      gn.verifyMsg("You are editing TAM against an Inactive Functional Group");
      JLog.write("Successfully cleared test data from Global level");
    }
    // global
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.clickButton("Clear");
    selectRegionTAM("Global");
    enterTextFieldElementValue(fName, "groupName");
    JLog.screenCapture();
    gn.clickButton("Apply");
    mc = new MTCMController();
    JLog.write("************checking for any supplier item issue *********************.");
    JLog.screenCapture();
    if (mc.getSuccessMessage().contains("FG Item")) {
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      gn.clickButton("Clear");
      FunctionalGroup grp = new FunctionalGroup();
      grp.setGroupName(fName);
      gn.clickButton("Apply");
      JLog.screenCapture();
      gn.clickName(fName);
      JLog.write("*************After Getting No supplier message *********************");
      JLog.screenCapture();
      ui.navHarmonyMTCM("Search", "Item AVL");
      gn.clickButton("Clear");
      gn.setItemNumber("AutoItemAllocDel");
      gn.clickButton("Apply");
      mc.scrollHorizontally(270);
      AbstractPage.sleep(3);
      JLog.write("********************After searching the item on search AVL page************************");
      JLog.screenCapture();
      JLog.fail("Failed due to no supplier message");
    }
    JLog.write("************No supplier item issue found.****************");
    if (fName.contains("Multi")) {
      selectAllowheding("select");
      setGlobalSupplyAllocationValue("100");
      setMultipleItemAllocValOnRowColumn("100", "2");
      setMultipleItemAllocValOnRowColumn("100", "4");
      // setMultipleItemAllocValOnRowColumn("100", "6");
    } else {
      setGlobalSupplyAllocationValue("100");
      setGlobalItemAllocationValue("100");
    }
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    if (fName.contains("Multi")) {
      verifyGlobalSupplyAllocationValue("100");
      tamController = new SupplyAllocationController();
      tamController.verifyItemAllocationvalueOnAllFields("100");
      // verifyItemAllocationValueOnRow("100", "1");
      // verifyItemAllocationValueOnRow("100", "2");
      // verifyItemAllocationValueOnRow("100", "3");
    } else {
      verifyGlobalSupplyAllocationValue("100");
      verifyGlobalItemAllocationValue("100");
    }

    changeFGToActive(fName);

  }

  public void removeAllExistingAllocFromALlLevels() throws Throwable {
    removeAllExistingAlloc("AutoItemGroupDelete");

    // resetting to 100 (to ensure all are getting deleted)
    // global
    ui = new HarmonyLoginUI();
    gn = new General();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
    gn.clickButton("Clear");
    selectRegionTAM("Global");
    enterTextFieldElementValue("AutoItemGroupDelete", "groupName");
    gn.clickButton("Apply");
    setGlobalSupplyAllocationValue("100");
    setGlobalItemAllocationValue("100");
    gn.selectCheckBox("check", "inheritValue");
    gn.selectCheckBox("check", "inheritItemValue");
    selectAllSupplierIcon("1");
    selectAllItemIcon("1");
    gn.clickButton("Save");
    gn.verifySuccessMsg("Allocation Group Saved");
    verifyGlobalSupplyAllocationValue("100");
    verifyGlobalItemAllocationValue("100");

    changeFGToActive("AutoItemGroupDelete");

  }

  @Then("I verify the fields got cleared on {string} allocation page")
  public void verifyDataCleared(String name) throws Exception {
    tamController = new SupplyAllocationController();
    Verify.verify(tamController.getElementValue("groupName").equals(""), "Text field groupName is not cleared");
    Verify.verify(tamController.getElementValue("itemNumber").equals(""), "Item Number is not cleared");
    String actVal = tamController.getAndVerifyComboByName(name);
    JLog.write("Act Value=" + actVal);
    Verify.verify(actVal.equals(""), name + " dropdown is not set back to default after clear action.");
    if (name.equals("siteDescription"))
      Verify.verify(actVal.equals(""), name + " dropdown is not set back to default after clear action.");
    JLog.write("Succesfully verfied clear button action.");
    checkForErrors();
  }

  // @And("I click to save the TAM SupplyAllocation values")
  // public void saveTAM() throws Exception {
  // //MTCMController.setOverrideContext(null);
  // AbstractPage.sleep(8);
  // SupplyAllocationController STAM = new SupplyAllocationController();
  // STAM.saveTam();
  // AbstractPage.sleep(2);
  // checkForErrors();
  // }

  // @And("I click on copy allocation to all sites")
  // public void copyAllocationTAM() throws Exception {
  // AbstractPage.sleep(8);
  // SupplyAllocationController copyAll= new SupplyAllocationController();
  // copyAll.copyAllocation();
  // AbstractPage.sleep(2);
  // checkForErrors();
  // }
  //
  //
  // @And("I set the Region {string} value")
  // public void regionValue(String regionvalue) throws Exception {
  // AbstractPage.sleep(2);
  // tamController = new SupplyAllocationController();
  // tamController.regionselection(regionvalue);
  // //MTCMController.setOverrideContext("contentFrame");
  // AbstractPage.sleep(10);
  // checkForErrors();
  // }
  //
  // @And("I set the Region {string} and site value{string}")
  // public void siteValue(String regionValue, String siteValue) throws
  // Exception {
  // AbstractPage.sleep(2);
  // tamController = new SupplyAllocationController();
  // tamController.siteSelection(regionValue, siteValue);
  // //MTCMController.setOverrideContext("contentFrame");
  // AbstractPage.sleep(10);
  // checkForErrors();
  // }
  //

  @And("I should see {string} item is {string} on global page")
  public void siteValue(String itemNumber, String visibility) throws Exception {
    AbstractPage.sleep(2);
    tamController = new SupplyAllocationController();
    boolean status = tamController.isElementByNameVisible("itemLabel");
    if (!visibility.contains("not")) {
      status = !status; // keeping status to false for true visibility
      // checking, so that itemNumber
      // assert validation can be done within this if condition check,
      // which will become true on assert validation for true visibility
      // and false for 'not displayed' condition
      String item = tamController.getItemValue("itemLabel");
      PriceTAMUploadController priceTAMCtrl = new PriceTAMUploadController();
      Verify.verify((itemNumber + priceTAMCtrl.getTimeStamp()).equals(item),
          "Item Number is not matching on Global Allocation Page");
    }
    Verify.verify(!status, "Wrong item visibility with respect to Hide Items Action.");
    checkForErrors();
  }

  @And("I {string} AllowHeding range between 80 to 200 Percentage")
  public void selectAllowheding(String action) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.selectButton(action, "allowHedging");
    JLog.screenCapture();
    checkForErrors();
  }

  @And("I {string} Hide Items action")
  public void selectHideItems(String action) throws Exception {
    tamController = new SupplyAllocationController();
    JLog.screenCapture();
    tamController.selectButton(action, "hideItem");
    JLog.screenCapture();
    checkForErrors();
  }

  @And("I select All Supplier allocation icon with value from the column {string}")
  public void selectAllSupplierIcon(String col) throws Exception {
    int column = Integer.parseInt(col);
    tamController = new SupplyAllocationController();
    tamController.selectSuppAllocateIcon(column);
    JLog.screenCapture();
    checkForErrors();
  }

  @And("I select All Item allocation icon with value from the column {string}")
  public void selectAllItemIcon(String col) throws Exception {
    int column = Integer.parseInt(col);
    tamController = new SupplyAllocationController();
    tamController.selectItemAllocateIcon(column);
    JLog.screenCapture();
    checkForErrors();
  }

  // @And("I set Supplier Allocation value {string} on column {string}")
  // public void setSuppAllocationValOnColumn(String val,String col) throws
  // Exception {
  // int column = Integer.parseInt(col);
  // SupplyAllocationController allowRng = new SupplyAllocationController();
  // allowRng.setSupplyAllocationvalueOnColumn(val, column);
  // JLog.screenCapture();
  // checkForErrors();
  // }

  @When("I set Supplier Allocation value {string} on column {string}")
  public void setSuppAllocationValOnColumn(String val, String col) {
    int column = Integer.parseInt(col);
    tamController = new SupplyAllocationController();
    tamController.setSupplyAllocationvalueOnColumn(val, column);
    JLog.screenCapture();
    checkForErrors();
  }

  @When("I click on {string} from dropdown on column {string}")
  public void clickAllocDrpDownActions(String action, String col) {
    int column = Integer.parseInt(col);
    String allocTo = "";
    if (action.contains("&"))
      allocTo = "SupplierAndItem";
    else if (action.contains("supplier"))
      allocTo = "Supplier";
    else if (action.contains("item"))
      allocTo = "Item";
    action = action.substring(5, action.length());
    tamController = new SupplyAllocationController();
    tamController.clickDropDwnActionsForAlloc(allocTo, action, column);
    JLog.screenCapture();
    checkForErrors();
  }

  @When("I set Item Allocation value {string} on column {string}")
  public void setItemAllocationValOnColumn(String val, String col) throws Exception {
    int column = Integer.parseInt(col);
    tamController = new SupplyAllocationController();
    tamController.setItemAllocationvalueOnColumn(val, column);
    JLog.screenCapture();
    checkForErrors();
  }

  @When("I select {string} values on allocation management")
  public void setCurrentPastValues(String val) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.setCurrentPastValues(val);
    JLog.screenCapture();
    checkForErrors();
  }

  @And("I create the testData for MassUpdate")
  public void createTestData() throws Throwable {
    // removing parent from fgs to create parent from massupdate page
    // as the same testData cfgs might have already a parent created from
    // othercases
    FunctionalGroup fg = new FunctionalGroup();
    fg.removeParent("massFG1");
    fg.removeParent("massFG2");
  }

  @And("I clear the testData for MassUpdate multipleSupplier")
  public void removeTestData() throws Throwable {
    // removing parent from fgs to create parent from massupdate page
    // as the same testData cfgs might have already a parent created from
    // othercases
    FunctionalGroup fg = new FunctionalGroup();
    fg.removeParent("CFG20200722%AZ");
    fg.removeParent("SEBRING");
  }

  @When("I {string} parent {string} from allocation page")
  public void parentFromAllocPage(String action, String parentName) throws Exception {
    if (action.contains("create")) {
      UploadController up = new UploadController();
      parentName = parentName + up.getTimeStamp();

      FunctionalGroupController.setOverrideContext("contentFrame");
      FunctionalGroupController fgCtrller = new FunctionalGroupController();
      AbstractPage page = new AbstractPage();

      WebElement insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame' and @id='mainModalFrame']"));
      page.browserSession.getDriver().switchTo().frame(insideFrame);

      fgCtrller.setElementValue("parentGroupName", parentName);
      JLog.write("Set parent name as " + parentName);

      fgCtrller.clickAddFGBtn();

      FunctionalGroupController.setOverrideContext("contentFrame");
      fgCtrller = new FunctionalGroupController();
      page = new AbstractPage();
      insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame' and @id='mainModalFrame']"));
      page.browserSession.getDriver().switchTo().frame(insideFrame);

      insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame' and @id='basicmainModalFrame']"));
      page.browserSession.getDriver().switchTo().frame(insideFrame);
      fgCtrller.setFindPopupWithSearchData("massFG2" + up.getTimeStamp());
      fgCtrller.clickButton("Search");

      fgCtrller.selectListItemsFromPopup("confirm", 1);

      // FunctionalGroupController.setOverrideContext("contentFrame",
      // "mainModalFrame");
      // fgCtrller = new FunctionalGroupController();

      page.browserSession.getDriver().switchTo().defaultContent();

      FunctionalGroupController.setOverrideContext("contentFrame");
      fgCtrller = new FunctionalGroupController();
      page = new AbstractPage();

      // insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame'
      // and @id='basicmainModalFrame']"));
      // page.browserSession.getDriver().switchTo().frame(insideFrame);

      insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame' and @id='mainModalFrame']"));
      page.browserSession.getDriver().switchTo().frame(insideFrame);

      fgCtrller.saveParentGroup(parentName);
      FunctionalGroupController.setOverrideContext("null");
      fgCtrller = new FunctionalGroupController();
      Verify.verify(fgCtrller.getSuccessMessage().contains("Parent Group Saved"),
          "Unable to verify Parent saved message");
      JLog.write("Successfully created and added parent name.");
      JLog.screenCapture();
      fgCtrller = new FunctionalGroupController();
      fgCtrller.clickCloseBtnOnMainFrame();

    } else if (action.contains("Assign")) {
      UploadController up = new UploadController();
      if (!parentName.contains("PG-CFG202010JulyA")) {
        parentName = parentName + up.getTimeStamp();
      }
      String cfgName = "massFG2" + up.getTimeStamp();
      if (parentName.contains("PG-CFG202010JulyA")) {
        cfgName = "CFG20200722%AZ";
      }
      // FunctionalGroupController.setOverrideContext("contentFrame",
      // "mainModalFrame");
      // FunctionalGroupController fgCtrller = new
      // FunctionalGroupController();
      // fgCtrller.setElementValue("parentGroupName", parentName);
      // JLog.write("Set parent name as " + parentName);
      // fgCtrller.clickButton("Add Functional Group");
      // General g = new General();

      FunctionalGroupController.setOverrideContext("contentFrame");
      FunctionalGroupController fgCtrller = new FunctionalGroupController();
      AbstractPage page = new AbstractPage();

      WebElement insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame' and @id='mainModalFrame']"));
      page.browserSession.getDriver().switchTo().frame(insideFrame);

      fgCtrller.clickAddFGBtn();

      FunctionalGroupController.setOverrideContext("contentFrame");
      fgCtrller = new FunctionalGroupController();
      page = new AbstractPage();
      insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame' and @id='mainModalFrame']"));
      page.browserSession.getDriver().switchTo().frame(insideFrame);

      insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame' and @id='basicmainModalFrame']"));
      page.browserSession.getDriver().switchTo().frame(insideFrame);
      fgCtrller.setFindPopupWithSearchData(cfgName);
      fgCtrller.clickButton("Search");
      //
      // MTCMController.setOverrideContext("contentFrame",
      // "mainModalFrame");
      // controller = new MTCMController();

      fgCtrller.selectListItemsFromPopup("confirm", 1);

      // FunctionalGroupController.setOverrideContext("contentFrame",
      // "mainModalFrame");
      // fgCtrller = new FunctionalGroupController();

      page.browserSession.getDriver().switchTo().defaultContent();

      FunctionalGroupController.setOverrideContext("contentFrame");
      fgCtrller = new FunctionalGroupController();
      page = new AbstractPage();

      // insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame'
      // and @id='basicmainModalFrame']"));
      // page.browserSession.getDriver().switchTo().frame(insideFrame);

      insideFrame = page.get(By.xpath("//iframe[@name='mainModalFrame' and @id='mainModalFrame']"));
      page.browserSession.getDriver().switchTo().frame(insideFrame);

      fgCtrller.saveParentGroup(parentName);
      FunctionalGroupController.setOverrideContext("null");
      fgCtrller = new FunctionalGroupController();
      Verify.verify(fgCtrller.getSuccessMessage().contains("Parent Group Saved"),
          "Unable to verify Parent saved message");
      JLog.write("Successfully created and added parent name.");
      JLog.screenCapture();
      fgCtrller = new FunctionalGroupController();
      fgCtrller.clickCloseBtnOnMainFrame();
    }
    checkForErrors();
  }

  @Then("I get the {string} Allocation result count from UI")
  public void getCountFromUIForAllocResults(String typeOfDownload) throws Exception {
    tamController = new SupplyAllocationController();
    recordsCount = tamController.getRecordCountFromUI(typeOfDownload);
    fgName = tamController.getTextFieldValue("functionalGroupName");
    fgTYpe = tamController.getComboSelectedOptionByName("groupType"); // "CFG";
    JLog.write("Successfully retrieved records count from UI.");
  }

  @And("I click on {string} Button and verify the result for {string} for {string}")
  public void clickDownloadButton(String btnName, String ui, String action) throws Throwable {
    tamController = new SupplyAllocationController();
    String dwnloadedFile = "";
    SCPlatformDownloadController dwnCtrller;
    if ((btnName.contains("supplier") || btnName.contains("Supplier")) && action.contains("Verify")) {
      dwnloadedFile = tamController.getDownloadedFileForDateVerification("supplierDownload");
      if (action.contains("downloadForUploadVerify")) {
        File source = new File(dwnloadedFile);
        String s = prop.getProjectDir();
        s = prop.getProjectName();
        s = prop.getTopLevelDirectory();
        File des = new File(prop.getWorkingDir()
            + "src/test/resources/com/scplatform/selenium/scplatform/data/TamSupplierAllocation.xlsx");
        Files.copy(source.toPath(), des.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return;
      }
      dwnCtrller = new SCPlatformDownloadController();
      dwnCtrller.verifySuppDownloadDataToVerifyDate(dwnloadedFile, action);
      JLog.write("Successfully verified downloaded file on " + ui + " page.");
      return;
    }
    // if (btnName.toLowerCase().contains("supplier") ||
    // btnName.toLowerCase().contains("item"))
    // tamController.clickButton(btnName);
    // JLog.write("Clicked on " + btnName + " button.");
    dwnloadedFile = tamController.getDownloadedFile(btnName);
    dwnCtrller = new SCPlatformDownloadController();

    if (dwnloadedFile == null) {
      JLog.error("Download failed - file not found for button: " + btnName);
      com.google.common.base.Verify.verify(false, "Downloaded file is null for button: " + btnName);
      return;
    }

    // Fallback: if ChromeDownloader returned UnknownDownload or the file path no longer exists,
    // search the working directory and Downloads directory for the latest recently-modified xlsx file
    if (dwnloadedFile.contains("UnknownDownload") || !new File(dwnloadedFile).exists()) {
      JLog.write("[9944 Fix] Downloaded file not found at: " + dwnloadedFile + ". Searching for latest xlsx...");
      long recentThreshold = System.currentTimeMillis() - 10L * 60 * 1000; // 10 minutes ago
      File foundFile = null;

      // Try working directory first (Chrome downloads SA xlsx here)
      String workingDir = Prop.getInstance().getWorkingDir();
      File wDir = new File(workingDir);
      File[] wDirFiles = wDir.listFiles((d, name) -> name.toLowerCase().endsWith(".xlsx"));
      if (wDirFiles != null && wDirFiles.length > 0) {
        java.util.Arrays.sort(wDirFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        if (wDirFiles[0].lastModified() >= recentThreshold) {
          foundFile = wDirFiles[0];
          JLog.write("[9944 Fix] Using latest xlsx from working dir: " + foundFile.getAbsolutePath());
        }
      }

      // Also try ~/Downloads
      if (foundFile == null) {
        String downloadsDir = System.getProperty("user.home") + File.separator + "Downloads";
        File dir = new File(downloadsDir);
        File[] xlsxFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xlsx"));
        if (xlsxFiles != null && xlsxFiles.length > 0) {
          java.util.Arrays.sort(xlsxFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
          if (xlsxFiles[0].lastModified() >= recentThreshold) {
            foundFile = xlsxFiles[0];
            JLog.write("[9944 Fix] Using latest xlsx from Downloads dir: " + foundFile.getAbsolutePath());
          }
        }
      }

      if (foundFile != null) {
        dwnloadedFile = foundFile.getAbsolutePath();
      } else {
        JLog.error("[9944 Fix] No recent xlsx found in working dir or Downloads after timeout for: " + btnName);
        com.google.common.base.Verify.verify(false, "Downloaded file not found for: " + btnName);
        return;
      }
    }

    if (btnName.toLowerCase().contains("supplier") && action.contains("Validation"))
      dwnCtrller.verifySuppDownloadData(dwnloadedFile, action, recordsCount, fgName, fgTYpe);
    else if (action.startsWith("destinationSite"))
      dwnCtrller.downloadVerification(dwnloadedFile, ui, action);
    else
      dwnCtrller.verifyItemDownloadData(dwnloadedFile, action, recordsCount, fgName, fgTYpe);
    JLog.write("Successfully verified downloaded file on " + ui + " page.");
  }

  @And("I get and verify allocation details on UI")
  public void verifyAllocationDetailsFromUI() throws Exception {
    tamController = new SupplyAllocationController();
    tamController.getAndVerifyFGName("TAM_CFG");
    tamController.getAndVerifyFGType("CFG");
    fgName = "TAM_CFG";
    fgTYpe = "CFG";
    JLog.write("Successfully verified UI records.");
  }

  @And("I verify fgName as {string} besides filter expand button")
  public void verifyFGLabel(String fgName) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.verifyFGNameNearFilter(fgName);
    JLog.write("Successfully verified fgName label near filter expand button");
  }

  @And("I verify allocation scope as {string} {string} besides filter expand button")
  public void verifyAllocLevel(String level, String alloc) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.verifyAllocLevelNearFilter(level, alloc);
    JLog.write("Successfully verified tam alloc label near filter expand button");
  }

  @And("I verify the cell near supp alloc and above item alloc are not clickable")
  public void verifyCellClickability() throws Exception {
    tamController = new SupplyAllocationController();
    tamController.isCellClickable();
    JLog.write("Successfully verified cells near Item and supp allocations are non clickable.");
  }

  public void clearTestData(Scenario scenario) throws Throwable {
    String status = scenario.getStatus().toString();
    String name = scenario.getName();
    JLog.write("..............Clearing Test Data.............");
    JLog.write("For test with name " + name + "!");
    // General gen = new General();
    gn = new General();
    HarmonyLoginUI ui = new HarmonyLoginUI();
    // SupplyAllocationController controller = new
    // SupplyAllocationController();
    FunctionalGroup grp = new FunctionalGroup();
    if (name.contains("mass update without sibling FG") || name.contains("fgs without parent")) {
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      gn.clickButton("Clear");
      grp.setGroupName("massFG1");
      gn.clickButton("Apply");
      gn.clickName("massFG1");
      grp.parentAction("delete");
      grp.saveGrp();
      gn.verifySuccessMsg("Functional Group Saved");
      grp.verifyComboSelection("none", "Parent Functional Group");
      JLog.screenCapture();
    }
    checkForErrors();
  }

  @And("I select {string} on Delete Allocations Option")
  public void delAlloc(String option) throws Exception {
    AbstractPage.sleep(2);
    tamController = new SupplyAllocationController();
    tamController.selectAllocDeletionOption(option);
    JLog.write("Successfully selected " + option + " option on delete allocations");
    checkForErrors();
  }

  @And("I delete {string} on Delete Allocations from site level")
  public void deleteAllocFromSite(String option) throws Exception {
    tamController = new SupplyAllocationController();
    tamController.browser().switchTo().frame("deleteFrame");
    tamController.selectAllocDeletionOption(option);
    JLog.write("Successfully selected " + option + " option on delete allocations");
    tamController = new SupplyAllocationController();
    tamController.clickAllocDeletionBtnOnSiteLevel();
    checkForErrors();
  }

  //

}
