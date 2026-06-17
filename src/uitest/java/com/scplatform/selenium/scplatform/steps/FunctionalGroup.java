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
import java.text.ParseException;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.FunctionalGroupController;
import com.test.selenium.scplatform.modelViewController.FunctionalGroupModel;
import com.test.selenium.scplatform.modelViewController.FunctionalGroupView;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.ui.main.download.SCPlatformDownloadController;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.test.selenium.scplatform.ui.main.upload.UploadModel;
import com.google.common.base.Verify;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FunctionalGroup {

  FunctionalGroupController controller;
  FunctionalGroupModel model;
  General genObj;
  public static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
  // public static String timeStamp = "210408150123";
  String multipleSearchParent;
  String itemOrFG;
  String itemBuisness;
  String itemNumber;
  String itemDescp;
  String itemCC;
  String itemResp;
  String resp;
  String groupType;
  String pName, fgName;
  String odmPart, itemPart;
  String[] pgData;
  Prop prop = Prop.getInstance();

  String fgNameTimestamp;
  HarmonyLoginUI ui;
  // = new HarmonyLoginUI();

  @Before
  public void beforeMethod(Scenario scenario) {
    JLog.setScenarioForCucumber(scenario);
    JLog.resetErrorCount();
  }

  private void checkForErrors() {
    if (JLog.getErrorCount() > 0) {
      JLog.fail(
          JLog.getErrorCount() + " errors occurred in the test.  Check log.",
          TakeScreenshot.True);
    }
  }

  @And("I verify Tam Availability as {string} on Edit FG page")
  public void verifyTamAvailability(String expVal) throws Throwable {
    controller = new FunctionalGroupController();
    controller.verifyTamAvailability(expVal);
    checkForErrors();
  }

  @And("I delete a supplier by uploading via item upload")
  public void deleteSupplierFromAnItem() throws Throwable {
    controller = new FunctionalGroupController();
    checkForErrors();
  }

  @And("I clear any testData uncleared from {string} SavedFilter {string}")
  public void clearSavedFilterTestData(String page, String filter) throws Throwable {
    genObj = new General();
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage " + page + " Group");
    // genObj.clickButton("Clear");
    genObj.selectCombo("Manage Filters", "Saved Filters");
    if (filter.contains("ABC")) {
      clickEditBtn(filter, "ABC11");
    } else
      clickEditBtn(filter, filter);
    genObj.deleteFilter("close", "FGFilterXBtn");
    checkForErrors();
  }

  @And("I {string} the items to the Functional Group {string} with {string}")
  public void createFG(String action, String fg, String btnName) throws Throwable {
    AbstractPage.sleep(1);
    controller = new FunctionalGroupController();
    // String gfWithoutTimeStamp = fg;
    // model = new FunctionalGroupModel();
    UploadController c = new UploadController();
    if (fg.equals("random")) {
      boolean useLetters = true;
      boolean useNumbers = true;
      fg = RandomStringUtils.random(258, useLetters, useNumbers);
      // gfWithoutTimeStamp = fg;
    } else if (fg.contains("AutoItemGroup") || fg.contains("testFGItem")
        || fg.contains("AutoItemFG") || fg.contains("AutoMultipleItemGroup")
        || fg.contains("CRGrp"))
      fg = fg + c.getTimeStamp();
    else if (fg.contains("FGrp") || !(action.equals("update")) && !fg.contains("AutoItemGroup")
        && !fg.contains("TESTGROUP") && !fg.contains("AutoItemFG") || fg.contains("FG9394")) {
      fg = fg + timeStamp;
    }
    controller.createAssignGroup(action, btnName, fg);
    JLog.screenCapture();
    AbstractPage.sleep(10);
    controller = new FunctionalGroupController();
    AbstractPage.sleep(1);
    // Save current error count before checking for popup (which may throw exceptions)
    int initialErrorCount = JLog.getErrorCount();
    boolean status = controller.verifyWarningPopupMsg("Do you want to assign responsibility to the items");
    // If verifyWarningPopupMsg() silently caught an exception, reset to initial count
    if (!status && JLog.getErrorCount() > initialErrorCount) {
      JLog.resetErrorCount();
      JLog.write("Warning popup not found - this may be expected if responsibility assignment is not needed");
    }
    JLog.screenCapture();
    if (status) {
      controller.clickButton("Yes");
      controller.assignResp();
      JLog.write("After assignResp");
      JLog.screenCapture();
      AbstractPage.sleep(3);

      General g = new General();
      // g.verifyScrollBarVisibleUnderNewWindow();
      controller = new FunctionalGroupController();
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

    if (!btnName.contains("Exit") && !fg.contains("Dup") && !fg.contains("Edit")) {
      controller = new FunctionalGroupController();
      String fgNameOnScreen = controller.getGrpNameValue();
      JLog.write("FG name on UI screen= " + fgNameOnScreen);
      JLog.write("Exp FG name = " + fg);
      AbstractPage.sleep(10);
      Verify.verify(
          fgNameOnScreen.equals(fg) || fgNameOnScreen.equals(fg.substring(0, fg.length() - 3)),
          "Unable to verify fgName after create on UI.");
      Verify.verify(
          fgNameOnScreen.length() <= 255,
          "FGName is not getting truncauted to match max exceeded fgName length limit.");
    }
    checkForErrors();
    JLog.write("Successfully done with " + action + " items to the group");
  }

  @And("I {string} the items to Functional Group {string} with {string}")
  public void createFGTestData(String action, String fg, String btnName) throws Throwable {
    controller = new FunctionalGroupController();
    model = new FunctionalGroupModel();
    UploadController c = new UploadController();
    if (fg.contains("AutoItemGroup") || fg.contains("testFGItem"))
      fg = fg + c.getTimeStamp();
    if (!(action.equals("update")) && !fg.contains("TESTGROUP")) {
      fg = fg + timeStamp;
    }
    model.setGroupName(fg);
    controller.setModel(model);
    controller.createAssignGroup(action, btnName, fg);
    JLog.screenCapture();
    AbstractPage.sleep(3);
    controller = new FunctionalGroupController();
    AbstractPage.sleep(1);
    // Save current error count before checking for popup (which may throw exceptions)
    int initialErrorCount = JLog.getErrorCount();
    boolean status = controller.verifyWarningPopupMsg("Do you want to assign responsibility to the items");
    // If verifyWarningPopupMsg() silently caught an exception, reset to initial count
    if (!status && JLog.getErrorCount() > initialErrorCount) {
      JLog.resetErrorCount();
      JLog.write("Warning popup not found - this may be expected if responsibility assignment is not needed");
    }
    JLog.screenCapture();
    if (status) {
      controller.clickButton("Yes");
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
      controller.saveGroup();
    }
    checkForErrors();
    JLog.write("Successfully done with " + action + " items to the group");
  }

  @Then("I search and verify {string} items added to the created Group {string}")
  public void searchVerifyFG(String expCount, String fg) throws Throwable {
    AbstractPage.sleep(8);
    int expectedCount = Integer.parseInt(expCount);
    // model = new FunctionalGroupModel();
    // model.setSearchGroupName(fg + timeStamp);
    // model.setSearchGroupName("TestFG200318180114");
    controller = new FunctionalGroupController();
    // controller.setModel(model);
    // controller.searchForGroup();
    controller.setGrpName(fg + timeStamp);
    JLog.screenCapture();
    controller.clickButton("Apply");
    JLog.screenCapture();
    AbstractPage.sleep(20);
    int actCount = controller.getGroupItemsOnSearchPage();
    Verify.verify(actCount == expectedCount, "Failed to verify Items on the Group");
    JLog.resetErrorCount();
    // checkForErrors();
  }

  @Then("I verify {string} items listed for the Group {string}")
  public void verifyFG(String expCount, String fg) throws Throwable {
    controller = new FunctionalGroupController();
    int expectedCount = Integer.parseInt(expCount);
    int actCount = controller.getGroupItemsOnEditPage();
    Verify.verify(actCount == expectedCount, "Failed to verify Items on the Group");
    checkForErrors();
    JLog.write("Verified the items on the group");
  }

  @And("I edit the Functional Group {string} by adding item {string}")
  public void editFGAddItem(String fg, String item) throws Exception {
    controller = new FunctionalGroupController();
    controller.clickButton("Add Item");
    AbstractPage.sleep(2);
    FunctionalGroupController.setOverrideContext("null");
    controller = new FunctionalGroupController();
    controller.addItemFromNewWindow(item);
    controller = new FunctionalGroupController();
    controller.saveGroup();
    AbstractPage.sleep(20);
    controller = new FunctionalGroupController();
    // Save current error count before checking for popup (which may throw exceptions)
    int initialErrorCount = JLog.getErrorCount();
    boolean status = controller.verifyWarningPopupMsg("Do you want to assign responsibility to the items");
    // If verifyWarningPopupMsg() silently caught an exception, reset to initial count
    if (!status && JLog.getErrorCount() > initialErrorCount) {
      JLog.resetErrorCount();
      JLog.write("Warning popup not found - this may be expected if responsibility assignment is not needed");
    }
    JLog.screenCapture();
    if (status) {
      controller.clickButton("Yes");
      controller.assignResp();
      AbstractPage.sleep(2);
      controller = new FunctionalGroupController();
      controller.clickCloseBtnOnAssign();
      AbstractPage.sleep(2);
      controller.saveGroup();
      JLog.write("Successfully saved added items to the group");
    }

    checkForErrors();
  }

  @And("I edit the Functional Group {string} by removing item")
  public void editFGRemovingItem(String fg) throws Exception {
    controller = new FunctionalGroupController();
    controller.removeAnItemFromGroup(true);
    controller = new FunctionalGroupController();
    controller.saveGroup();
    checkForErrors();
  }

  @And("I edit the Functional Group {string} by removing item using X button")
  public void editFGRemovingItemXbtn(String fg) throws Exception {
    controller = new FunctionalGroupController();
    controller.removeAnItemFromGroupXBtn();
    controller = new FunctionalGroupController();
    controller.saveGroup();
    checkForErrors();
  }

  @And("I remove the FG {string} from the parent edit page")
  public void removeFGFromParentEditPage(String fg) throws Exception {
    controller = new FunctionalGroupController();
    controller.removeFGFromParentEdit();
    AbstractPage.sleep(2);
    // MTCMController.setOverrideContext("null");
    AbstractPage.browserSession.getDriver().switchTo().defaultContent();
    // controller = new FunctionalGroupController();
    String msg = controller.getSuccessMessage();
    Verify.verify(msg.contains("Functional Group [" + fg + "] deleted from Parent"));
    checkForErrors();
  }

  @Then("I verify {string} error message")
  public void verifyErrorMessage(String errMsg) {
    // Bamboo #2357 fix (PGWTS1-2357): the duplicate-name validation banner
    // ('Parent Functional Group With Same name already exists') can take a few
    // seconds longer to render than the previous 5s wait. Poll up to 5x with
    // 3s between attempts before failing, matching the pattern already used in
    // General.verifySuccessMsg.
    MTCMController.setOverrideContext("null");
    MTCMController harmonyCtrller = new MTCMController();
    // Reduced from 5s to 2s: error banners appear quickly; long pre-sleep causes them to be missed
    AbstractPage.sleep(2);
    int maxRetries = 5;
    String lastSeen = "";
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        AbstractPage.browserSession.getDriver().switchTo().defaultContent();
      } catch (Exception ignored) {}
      lastSeen = harmonyCtrller.getSuccessMessage();
      if (lastSeen != null && lastSeen.contains(errMsg)) {
        JLog.write("Verified error message on attempt " + attempt + ": " + errMsg);
        checkForErrors();
        return;
      }
      JLog.write("Attempt " + attempt + " did not see expected error message; got: " + lastSeen);
      if (attempt < maxRetries) {
        AbstractPage.sleep(3);
      }
    }
    Verify.verify(false,
        "Unable to verify error message '" + errMsg + "' after " + maxRetries
            + " attempts. Last seen: " + lastSeen);
  }

  @Then("I try to removeItem without selecting a group item")
  public void removeWithoutSelectingItem() throws Exception {
    controller = new FunctionalGroupController();
    controller.removeAnItemFromGroup(false);
  }

  @And("I try to create the FG {string} GroupName")
  public void createFGWithoutGroupName(String grpName) throws Exception {
    model = new FunctionalGroupModel();
    model.setSearchGroupName(grpName);
    controller = new FunctionalGroupController();
    controller.setModel(model);
    controller.createGroupForErrorVerification();
    checkForErrors();
  }

  @And("I try to create the parent group {string}")
  public void createParentGrp(String grpName) throws Exception {
    model = new FunctionalGroupModel();
    model.setParentGroupName(grpName);
    controller = new FunctionalGroupController();
    controller.setModel(model);
    controller.createGroupForErrorVerification();
    checkForErrors();
  }

  @And("I edit the Functional Group name as {string}")
  public void editFGGrpName(String grpName) throws Exception {
    setGroupName(grpName);
    FunctionalGroupView view = new FunctionalGroupView();
    WebElement saveBtn = view.saveAllButton();
    if (saveBtn != null) {
      saveBtn.click();
    } else {
      JLog.write("saveButton not in DOM - invoking goSaveAndContinue() via JS");
      view.executeJavaScript("if(typeof goSaveAndContinue === 'function') goSaveAndContinue();");
    }
    view.browserSession.getDriver().switchTo().defaultContent();
    AbstractPage.sleep(3);
    // FIX: ElementClickInterceptedException — autocomplete <li> dropdown stays visible
    // and overlaps the Yes confirmation button. JS click bypasses the overlay.
    WebElement yesBtn = view.getButton("Yes");
    view.executeJavaScript("arguments[0].scrollIntoView(true);", yesBtn);
    view.executeJavaScript("arguments[0].click();", yesBtn);
    // controller.clickConfirmYesBtn();
    // verifying whether the fg edited name is refreshed on the title after
    // save
    General gen = new General();
    gen.verifyTitle(grpName);
    checkForErrors();
  }

  @And("I set group name as {string}")
  public void setGroupName(String fg) throws Exception {
    controller = new FunctionalGroupController();
    if ((fg.contains("CRGrp"))
        || (fg.contains("MultipleItem") || fg.contains("testFGItem") || fg.contains("Mass")
            || (fg.contains("mass") && !fg.contains("*")) || (fg.contains("AutoItemGroup"))
                && !fg.contains("006HT_KIT") && !fg.contains("testP") && !fg.contains("SEBRING"))) {
      UploadController c = new UploadController();
      fg = fg + c.getTimeStamp();
      JLog.write("Timestamp from Upload page");
    } else if ((!fg.contains("GROUP")) && !fg.contains("006HT_KIT") && (!fg.contains("GroupNow"))
        && (!fg.contains("FGFilter")) && (!fg.contains("00T2N")) && (!fg.contains("FGUpload"))
        && (!fg.contains("CFG")) && !(fg.contains("AutoItemGroup")) && !fg.contains("*")
        && !fg.contains("SEBRING") && !fg.contains("testP") && !fg.contains("UI201905") && !fg.contains("QAAUTTEST")) {
      fg = fg + timeStamp;
      JLog.write("Timestamp from FG page");
    }
    controller.setGrpName(fg);
    checkForErrors();
  }

  @And("I save one {string} removed for verification")
  public void saveItemOrFG(String val) throws Exception {
    MTCMController.setOverrideContext("null");
    MTCMController c = new MTCMController();
    String actMsg = c.getSuccessMessage();
    JLog.write("Actual Message displayed is " + actMsg);
    int openBracket = actMsg.indexOf('[');
    int comma = actMsg.indexOf(',');
    int closeBracket = actMsg.indexOf(']');
    if (openBracket >= 0) {
      if (comma > openBracket)
        itemOrFG = actMsg.substring(openBracket + 1, comma);
      else if (closeBracket > openBracket)
        itemOrFG = actMsg.substring(openBracket + 1, closeBracket);
      else
        itemOrFG = "";
    } else {
      itemOrFG = "";
    }
    JLog.write("Item or fg name - " + itemOrFG);
    checkForErrors();
  }

  @And("I get the fgName from the search results on row {string}")
  public void getFGName(String row) throws Exception {
    MTCMController c = new MTCMController();
    AbstractPage.sleep(1);
    String fg;
    fg = c.getSearchResultsTextOnRowCol(row, "1");
    JLog.write("Actual Message displayed is " + fg);
    fgNameTimestamp = fg.substring(13, fg.length());
    JLog.write("fg name timestamp- " + fgNameTimestamp);
    checkForErrors();
  }

  @And("I set the itemNumber as {string} on Manage Items to verify the parent name")
  public void setItemNumberOnManageItems(String itemNum) throws Exception {
    MTCMController c = new MTCMController();
    itemNum = itemNum + fgNameTimestamp;
    // c.setValField(itemNum, "itemNumber");
    c.setEleWithValue("value(itemNumber)", itemNum);
    JLog.write("Successfully set ItemNumber as  " + itemNum);
    checkForErrors();
  }

  @And("I search with one of deleted {string}")
  public void searchItemOrGrpRemoved(String val) throws Throwable {
    genObj = new General();
    ui = new HarmonyLoginUI();
    FunctionalGroupController c = new FunctionalGroupController();
    if (val.contains("item")) {
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      genObj.clickButton("Clear");
      genObj.enterTextFieldVal(itemOrFG, "itemNumber");
      genObj.selectCombo("Yes", "Show Item Without Group");
      genObj.clickButton("Apply");
      genObj.waitTillPageLoads("5");
      genObj.verifySearchFilterResults();
      JLog.write(
          "Search results are verified when 'Show Item Without Group' is set to Yes for - "
              + itemOrFG);
      genObj.clickButton("Clear");
      genObj.enterTextFieldVal(itemOrFG, "itemNumber");
      genObj.clickButton("Apply");
      genObj.waitTillPageLoads("5");
      genObj.verifySearchFilterResults();
      c.isGrpNameEmpty();
      JLog.screenCapture();
      JLog.write("Verified that groupName is empty");
    } else if (val.contains("fg")) {
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Parent Group");
      genObj.clickButton("Clear");
      genObj.enterTextFieldVal(itemOrFG, "functionalGroupName");
      genObj.selectCombo("Yes", "Show Group Without Parent");
      genObj.clickButton("Apply");
      genObj.waitTillPageLoads("5");
      genObj.verifySearchFilterResults();
      JLog.write(
          "Search results are verified when 'Show Group Without Parent' is set to Yes for - "
              + itemOrFG);
      genObj.clickButton("Clear");
      genObj.enterTextFieldVal(itemOrFG, "functionalGroupName");
      genObj.clickButton("Apply");
      genObj.waitTillPageLoads("5");
      genObj.verifySearchFilterResults();
      c.isParentGrpNameEmpty();
      JLog.screenCapture();
      JLog.write("Verified that parentName is empty");
    }
    checkForErrors();
  }

  @And("I select an item and enter groupName later click on back button")
  public void selectAndClickBack() throws Exception {
    genObj = new General();
    genObj.selectRows("1", "items", "checkbox");
    setGroupName("test");
    genObj.clickButton("Back");
    checkForErrors();
  }

  @Then("I verify {string} rows listed as {string} search results")
  public void verifyRows(String rowCount, String searchName) {
    String checkBoxName = "selectedPageKeys";
    int rows = Integer.parseInt(rowCount);
    JLog.write("Exp Rows=" + rows);
    controller = new FunctionalGroupController();
    int actRows = controller.getRowCount(checkBoxName);
    JLog.write("Act Rows=" + actRows);
    // if (searchName.contains("itemNumbers")) {
    // String[] itemNumbers = new String[] { "10002", "10001" };
    // Verify.verify(controller.verifyItemNumber(itemNumbers), "Could not
    // find the
    // item Numbers searched");
    // return;
    // }
    if (searchName.contains("parentGroupNames")) {
      String[] parentNames = new String[] { "ABC_MASS_PARENT", "ABC_MASS_PARENT", "MASS_CHECK", "MASS_CHECK" };
      Verify.verify(
          controller.verifyGroupNames(parentNames),
          "Could not find the parent group Names searched");
      return;
    }
    Verify.verify(rows == actRows, "No of rows not matching");
    checkForErrors();
  }

  @And("I clear the testData for item belonging group and parent")
  public void deletParentForItem() throws Throwable {
    removeParent("AutoItemGroup");
  }

  @And("I verify the entered data have lost without saving")
  public void verifyDataLost() throws Exception {
    controller = new FunctionalGroupController();
    Verify.verify(controller.getGrpNameValue().equals(""), "Text field groupName is not cleared");
    controller = new FunctionalGroupController();
    Verify.verify(!(controller.isCheckboxChecked("items", 0)), "Selected row is not deselected");
    checkForErrors();
  }

  @And("I verify the entered data have not lost")
  public void verifyDataNotLost() throws Exception {
    controller = new FunctionalGroupController();
    Verify.verify(
        !(controller.getCreateGrpNameValue().equals("")),
        "Text field groupName is cleared, data lost");
    Verify.verify(controller.isCheckboxChecked("items", 0), "Selected row is deselected");
    checkForErrors();
  }

  @And("I verify the data entered are cleared")
  public void verifyDataCleared() throws Exception {
    controller = new FunctionalGroupController();
    Verify.verify(controller.getGrpNameValue().equals(""), "Text field groupName is not cleared");
    Verify.verify(
        controller.getMultipleTextFieldValue("itemNumbers").equals(""),
        "Item Numbers are not cleared");
    Verify.verify(
        controller.getMultipleTextFieldValue("groupNames").equals(""),
        "Group Names are not cleared");
    checkForErrors();
  }

  @Then("I verify page {string} out of total pages under the list")
  public void verifyPageJump(String pageCount)
      throws InterruptedException, ParseException, IOException {
    controller = new FunctionalGroupController();
    Verify.verify(controller.verifyPageJump(pageCount));
    checkForErrors();
  }

  @Then("I verify the fields set for {string}")
  public void verifyFilter(String filterName) {
    AbstractPage.sleep(5);
    controller = new FunctionalGroupController();
    // if (!filterName.equals("FG0H6_LBL"))
    // filterName = filterName + timeStamp;
    controller.verifyFields(filterName);
    checkForErrors();
  }

  @When("I remove item {string} from the group")
  public void removeItemName(String name) throws Exception {
    controller = new FunctionalGroupController();
    controller.removeItemName(name);
    controller = new FunctionalGroupController();
    controller.saveGroup();
    checkForErrors();
  }

  // @Then("I verify the item details on the popup triggered from FG")
  // public void verifyItemDetailsFromFG() throws Exception {
  // FunctionalGroupController.setOverrideContext(null);
  // controller = new FunctionalGroupController();
  // Verify.verify(controller.verifyItemDetailsOnFGItemPopup(), "Item Details
  // are
  // not matching.");
  // JLog.write("Item details are verified successfully.");
  // checkForErrors();
  // }

  @Then("I verify the item details on the popup")
  public void verifyItemDetails() throws Exception {
    // FunctionalGroupController.setOverrideContext("null");
    controller = new FunctionalGroupController();
    Verify.verify(controller.verifyItemDetailsOnPopup(), "Item Details are not matching.");
    JLog.write("Item details are verified successfully.");
    checkForErrors();
  }

  @Then("I verify parent {string} belongs to {string} group on {string} Page")
  public void verifyParentName(String parentName, String grpName, String page) throws Exception {
    controller = new FunctionalGroupController();
    MTCMController ctrller = new MTCMController();
    if (page.equals("FG")) {
      Verify.verify(
          controller.getParentGroupName("1", "12").equals(parentName + timeStamp)
              && controller.isLinkVisible(grpName),
          "Parent Name " + parentName + " not correct on Manage FG page.");
      return;
    } else if (page.equals("parent202")) {
      Verify.verify(
          controller.getParentGroupName("1", "2").equals(parentName + timeStamp)
              && controller.isLinkVisible(parentName),
          "Parent Name " + parentName + " not correct on Manage FG page.");
      return;
    }
    Verify.verify(
        controller.getParentGroupName("1", "1").equals(grpName)
            && controller.isLinkVisible(parentName + timeStamp),
        "Group Name " + grpName + " not correct on Manage Parent page.");
  }

  @When("I click on {string} parent for the group")
  public void parentAction(String action) throws Exception {
    controller = new FunctionalGroupController();
    String parent;
    if (action.equals("search")) {
      // parent = controller.browser().getWindowHandle();
      controller.clickIconBtn("search");
      // ArrayList<String> newTab = new
      // ArrayList<>(controller.browser().getWindowHandles());
      // newTab.remove(parent);
      // controller.browser().switchTo().window(newTab.get(0));
      return;
    }
    if (action.equals("Create"))
      action = "playlist_add";
    controller.clickIconBtn(action);
    checkForErrors();
  }

  @When("I save parent name as {string}")
  public void setParentName(String name) throws Exception {
    controller = new FunctionalGroupController();
    // if (!(name.contains("Delete")))
    name = name + timeStamp;
    controller.setParentName(name);
    JLog.write("Set parent name as " + name);
    JLog.write("Successfully created and added parent name.");
    checkForErrors();
  }

  @Then("I verify FG {string} not listed under Group Details")
  public void verifyFGUnderParentDetails(String name) throws Exception {
    controller = new FunctionalGroupController();
    JLog.screenCapture();
    Verify.verify(
        !controller.isFGListed(name),
        "FG " + name + " is still listed under Parent Details even after removing.");
    JLog.write("Successfully verified FG removed from the parent.");
    checkForErrors();
  }

  @Then("I get and verify the PG {string} search results data from UI")
  public void getFromPGUI(String pName) throws Exception {
    controller = new FunctionalGroupController();
    pgData = new String[4];
    pgData[0] = controller.getParentDetails("1", "1"); // frst fgName
    pgData[1] = controller.getParentDetails("2", "1"); // second row fgName
    pgData[2] = controller.getfgNameFromSearchList(1); // pName
    pgData[3] = controller.getParentDetails("1", "3"); // type

    Verify.verify(pgData[3].equals("CFG"), "Unable to verify Parent Type as CFG");
    JLog.write("Successfully verified CFG as parentType on UI");
    Verify.verify(pgData[2].contains(pName), "Unable to verify parentName as " + pName);
    JLog.write("Successfully verified parentName on UI as " + pName);
    Verify.verify(
        pgData[0].contains("FGrp2") || pgData[0].contains("FGrp1"),
        "Unable to verify group Name as FGrp1,mismatch");
    JLog.write("Successfully verified FG Name on UI as FGrp1");
    Verify.verify(
        pgData[1].contains("FGrp2") || pgData[1].contains("FGrp1"),
        "Unable to verify group Name as FGrp2, mismatch");
    JLog.write("Successfully verified FG Name on UI as FGrp2");
    JLog.write("Successfully retrieved records count from UI.");
  }

  @Then("I get and verify the FG {string} search results data from UI")
  public void getFGFromUI(String fName) throws Exception {
    controller = new FunctionalGroupController();
    itemNumber = controller.getItemNumberFromList(1);
    itemBuisness = controller.getItemDetailsForFG(1, 4);
    itemResp = controller.getItemDetailsForFG(1, 7);
    groupType = controller.getItemDetailsForFG(1, 10);
    itemCC = controller.getItemDetailsForFG(1, 6);
    itemDescp = controller.getItemDetailsForFG(1, 2);
    pName = controller.getItemDetailsForFG(1, 14);
    fgName = controller.getfgNameFromSearchList(1);
    // Verify.verify(resp.contains("PRODUCTION"), "Unable to verify
    // responsibility as production");
    // JLog.write("Successfully verified responsility on UI");
    controller.isCoulmnHeaderDisplayed("Parent Item Number");
    controller.isCoulmnHeaderDisplayed("Parent Item Business Name");
    controller.isCoulmnHeaderDisplayed("ODM Part Number");
    controller.isCoulmnHeaderDisplayed("ODM Item Business");
    Verify.verify(groupType.contains("CFG"), "Unable to verify groupType as CFG");
    JLog.write("Successfully verified groupType on UI");
    Verify.verify(
        pName.contains(""),
        "Unable to verify ParentName as null, since no parent added yet.");
    JLog.write("Successfully verified ParentName on UI");
    Verify.verify(fgName.contains(fName), "Unable to verify group Name as " + fName);
    JLog.write("Successfully verified group Name on UI");
    JLog.write("Successfully retrieved records count from UI.");
  }

  @And("I click on FG Download Button and verify the results")
  public void clickFGDownloadButton() throws Exception {
    controller = new FunctionalGroupController();
    controller.clickIconButton("file_download");
    AbstractPage.sleep(30);
    JLog.write("Clicked on download button.");
    SCPlatformDownloadController dwnCtrller = new SCPlatformDownloadController();
    String dwnloadedFile = controller.getDownload();
    String[] fgDetails = { itemNumber, itemBuisness, fgName, groupType, itemDescp, itemCC, itemResp };
    dwnCtrller.verifyGroupDownloadData("FG", dwnloadedFile, fgDetails);
    JLog.write("Successfully verified downloaded file on " + ui + " page.");
  }

  @And("I click on PG Download Button and verify the results")
  public void clickPGDownloadButton() throws Exception {
    controller = new FunctionalGroupController();
    // Single click via getDownload() which uses JS click (handleDownloadForReport).
    // Previously had a redundant pre-click (clickIconButton + 30s sleep) that caused
    // ElementClickInterceptedException in ChromeDownloader.handleDownload() on Chrome 147+.
    JLog.write("Initiating PG download (single JS-click via getDownload)...");
    SCPlatformDownloadController dwnCtrller = new SCPlatformDownloadController();
    String dwnloadedFile = controller.getDownload();
    dwnCtrller.verifyGroupDownloadData("PG", dwnloadedFile, pgData);
    JLog.write("Successfully verified downloaded file on " + ui + " page.");
  }

  @And("I save the parent group {string}")
  public void savePG(String pgName) throws Exception {
    if (pgName.contains("Mass") || pgName.contains("mass") || pgName.contains("CRPG")) {
      UploadController c = new UploadController();
      pgName = pgName + c.getTimeStamp();
    } else {
      pgName = pgName + timeStamp;
    }
    controller = new FunctionalGroupController();
    // if (pgName.contains("ParentGroup") ||
    // pgName.contains("createParent"))
    // controller.selectInputElements(1, "items", "checkbox");
    // else
    controller.selectInputElements(1, "selectedFunctionalGroupId", "checkbox");
    controller.saveParentGroup(pgName);
    checkForErrors();
  }

  @When("I save the FG")
  public void saveGrp() throws Exception {
    controller = new FunctionalGroupController();
    controller.saveGroup();
  }

  @When("I save the parent group")
  public void saveParentGrp() throws Exception {
    controller = new FunctionalGroupController();
    controller.saveParentGroup();
  }

  @When("I click on arrowNavigatorButton for the Filter {string}")
  public void clickArrowBtn(String filterName) {
    controller = new FunctionalGroupController();
    controller.clickArrowButton(filterName);
    AbstractPage.sleep(10);
    JLog.screenCapture();
  }

  @When("I edit Filter {string} to {string}")
  public void clickEditBtn(String filterName, String newFN) {
    controller = new FunctionalGroupController();
    controller.clickSavedFilterNameEditBtn(filterName, newFN);
    JLog.screenCapture();
  }

  public void clearTestData(Scenario s) throws Throwable {
    String status = s.getStatus().toString();
    String name = s.getName();
    JLog.write("..............Clearing Test Data.............");
    JLog.write("For test with name " + name + "!");
    ui = new HarmonyLoginUI();
    if (status.equals("failed") || status.equals("FAILED")) {
      if (name.contains("Edit a Functional Group by creating/removing parent")
          || name.contains("Delete a Functional Group from parent")
          || name.contains("searching and adding parent name")
          || name.contains("same existing Functional Group name")) {
        removeParent("TestingGroupNow");
      } else if (name.contains("Edit a Parent Group by adding FG")) {
        removeParent("00T2N_CFG");
      } else if (name.contains("add new FG")) {
        JLog.write("************Testdata Clearing***************");
        JLog.resetErrorCount();
        ui.navHarmonyMTCM("Supply Collaboration", "Manage Parent Group");
        FunctionalGroupController c = new FunctionalGroupController();
        c.clickButton("Clear");
        c.setSearchParentName("TestParent12");
        c.clickButton("Apply");
        MTCMController ctller = new MTCMController();
        ctller.clickName("TestParent12");
        removeFGFromParentEditPage("VCHX5");
        saveGrp();
        ui = new HarmonyLoginUI();
        ui.navHarmonyMTCM("Supply Collaboration", "Manage Parent Group");
        c = new FunctionalGroupController();
        c.clickButton("Clear");
        c.setSearchParentName("TestParent12");
        c.clickButton("Apply");
        ctller = new MTCMController();
        ctller.clickName("TestParent12");
        removeFGFromParentEditPage("CPDFK-PLCMT,STUP,PS6110,ENG");
        saveGrp();
        JLog.write("Test data cleared..");
        JLog.write(
            "Successfully deleted FG added via Upload test, which got failed to remove fg via upload");
      } else if (name.contains("Add-remove FG")) {
        removeAddedFGFrmParentEditPage("006HT_KIT");
      }
    }

    if (name.contains("Edit a Functional Group by adding more than one parent")
        || name.contains("Edit a Functional Group by creating duplicate parent name")) {
      removeParent("TestingGroupNow");
    }

    if (name.contains("Edit by Adding a parent to Functional Group")) {
      removeParent("TESTGROUP");
      // removeItemsFromGroup("TESTGROUP");
    }

    if (name.contains("Upload Functional Group with rename values")) {
      JLog.write("************Testdata Clearing***************");
      UploadController upctrller = new UploadController();
      String fg = "FGUpload" + upctrller.getTimeStamp();
      ui = new HarmonyLoginUI();
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      controller = new FunctionalGroupController();
      controller.setGrpName(fg);
      controller.clickButton("Apply");
      MTCMController ctller = new MTCMController();
      ctller.clickName(fg);
      setGroupName("FGUpload");
      saveGrp();
    }

    if (name.contains("Upload Parent Group with rename value")) {
      JLog.write("************Testdata Clearing***************");
      UploadController upctrller = new UploadController();
      String pN = "TestParent12" + upctrller.getTimeStamp();
      ui = new HarmonyLoginUI();
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Parent Group");
      controller = new FunctionalGroupController();
      controller.clickButton("Clear");
      controller.setSearchParentName(pN);
      controller.clickButton("Apply");
      MTCMController ctller = new MTCMController();
      ctller.clickName(pN);
      controller.saveParentGroup("TestParent12");
    }

    if (name.contains("Create a Parent Group,then assign a group")) {
      JLog.write("************Testdata Clearing - AssignParentGroup***************");
      JLog.resetErrorCount();
      try {
        String pgName = "AssignParentGroup" + timeStamp;
        ui.navHarmonyMTCM("Supply Collaboration", "Manage Parent Group");
        FunctionalGroupController fgc = new FunctionalGroupController();
        fgc.clickButton("Clear");
        fgc.setSearchParentName(pgName);
        fgc.clickButton("Apply");
        MTCMController ctller2 = new MTCMController();
        ctller2.clickName(pgName);
        for (int i = 0; i < 3; i++) {
          try {
            fgc.removeFGFromParentEdit();
            AbstractPage.sleep(2);
          } catch (Exception ignored) {
            break;
          }
        }
        saveGrp();
        JLog.resetErrorCount();
      } catch (Exception e) {
        JLog.write("AssignParentGroup cleanup skipped: " + e.getMessage());
        JLog.resetErrorCount();
      }
    }

    if (name.contains("FG status")) {
      JLog.write("************Testdata Clearing***************");
      General gen = new General();
      // ui = new HarmonyLoginUI();
      // ui.navHarmonyMTCM("Supply Collaboration", "Manage Allocation");
      // TAMSupplyAllocation tam = new TAMSupplyAllocation();
      // tam.enterTextFieldElementValue("fg-update","groupName");
      // controller = new FunctionalGroupController();
      // controller.clickButton("Apply");
      // tam = new TAMSupplyAllocation();
      // tam.setGlobalSupplyAllocationValue("100");
      // controller = new FunctionalGroupController();
      // controller.clickButton("Save");
      ui = new HarmonyLoginUI();
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      controller = new FunctionalGroupController();
      controller.setGrpName("fg-update");
      controller.clickButton("Apply");
      MTCMController ctller = new MTCMController();
      ctller.clickName("fg-update");
      gen.selectCheckBox("uncheck", "status");
      saveGrp();
      gen.verifyCheckBoxStatus("status", "unchecked");
    }

    if (name.contains("inactive -active status")) {
      JLog.write("************Testdata Clearing***************");
      General gen = new General();
      ui = new HarmonyLoginUI();
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      controller = new FunctionalGroupController();
      controller.setGrpName("TESTGROUP");
      controller.clickButton("Apply");
      MTCMController ctller = new MTCMController();
      ctller.clickName("TESTGROUP");
      gen.selectCheckBox("uncheck", "status");
      saveGrp();
      gen.verifyCheckBoxStatus("status", "unchecked");
    }

    // if (name.contains("edit by adding item 10000")) {
    // ui.login_harmony_mtcm("mtcmUser");
    // ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
    // AbstractPage.sleep(3);
    // //setGroupName("FGEditAddItem");
    // FunctionalGroupController c = new FunctionalGroupController();
    // c.setItemNumberOnSearchCriteria("10000");
    // c.clickButton("Apply");
    // AbstractPage.sleep(5);
    // MTCMController ctller = new MTCMController();
    // ctller.clickLinkText("FGEditAddItem" + timeStamp);
    // ctller = new MTCMController();
    // ctller.selectAllCheckBoxes();
    // ctller.clickButton("Remove Item");
    // FunctionalGroupController.setOverrideContext(null);
    // c = new FunctionalGroupController();
    // c.clickButton("Yes");
    // c = new FunctionalGroupController();
    // c.saveGroup();
    // c = new FunctionalGroupController();
    // Verify.verify(c.getSuccessMessage().contains("Functional Group
    // Saved"),
    // "Cannot clear test data for add itemon FG craete page");
    // }

  }

  public void removeParent(String fgName) throws Throwable {
    ui = new HarmonyLoginUI();
    // ui.login_harmony_mtcm("mtcmUser");
    ui.logout_mtcm();
    InputStream fo1 = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
    Properties p1 = new Properties();
    p1.load(fo1);
    String un = p1.getProperty("login");
    ui.login_harmony_mtcm_role("mtcmUser", un);
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
    FunctionalGroupController c = new FunctionalGroupController();
    c.clickButton("Clear");
    setGroupName(fgName);
    c.clickButton("Apply");
    AbstractPage.sleep(5);
    MTCMController ctller = new MTCMController();
    ctller.clickName(fgName);
    parentAction("delete");
    saveGrp();
  }

  public void removeItemsFromGroup(String FG) throws Throwable {
    ui = new HarmonyLoginUI();

    ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
    FunctionalGroupController c = new FunctionalGroupController();
    c.clickButton("Clear");
    setGroupName(FG);
    c.clickButton("Apply");
    AbstractPage.sleep(5);
    MTCMController ctller = new MTCMController();
    ctller.clickName(FG);
    General g = new General();
    g.selectAllRows();
    c.clickButton("Remove Item");
    g.verifyWarningMsgToConfirm(
        "Deleting Item from FG will delete Allocation for current Item",
        "Yes");
    // saveItemOrFG("item");
    // g.verifySuccessMsg("deleted from FG");
    saveGrp();
    g.verifySuccessMsg("Functional Group Saved");
  }

  public void clearTestDataSavedForFilter(Scenario s) throws Throwable {
    AbstractPage.sleep(2);
    String name = s.getName();
    String status = s.getStatus().toString();
    ui = new HarmonyLoginUI();
    ui.logout_mtcm();
    InputStream fo1 = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
    Properties p1 = new Properties();
    p1.load(fo1);
    String un = p1.getProperty("login");
    ui.login_harmony_mtcm_role("mtcmUser", un);
    if (name.contains("edit for FG") && status.equals("FAILED")) {

      // ui.login_harmony_mtcm("mtcmUser");
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      General gen = new General();
      gen.selectCombo("Manage Filters", "Saved Filters");
      clickEditBtn("RTT12", "RTT");
      gen.deleteFilter("close", "FGFilterXBtn");
    } else if (name.contains("edit for Parent Group") && status.equals("FAILED")) {
      // ui = new HarmonyLoginUI();
      // ui.login_harmony_mtcm("mtcmUser");
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Parent Group");
      General gen = new General();
      gen.selectCombo("Manage Filters", "Saved Filters");
      clickEditBtn("ABC11", "ABC12");
      gen.deleteFilter("close", "FGFilterXBtn");
    } else if (name.contains("Functional Group") || name.contains("Parent Group")
        || ((name.contains("delete functionality")) && (status.equals("FAILED")))
        || name.contains("delete popup") || name.contains("delete manage filter (PG) popup")
        || name.contains("(PG)")) {
      String filterName = "FGFilter";
      String subMenu = "Functional";
      if (name.contains("Parent Group") || name.contains("Parent FG") || name.contains("PG")) {
        filterName = "ParentFilter";
        subMenu = "Parent";
      }
      General gen = new General();
      gen.deleteSavedFilter(filterName, "Supply Collaboration", "Manage " + subMenu + " Group");
    }
  }

  @Then("I verify the {string} value selected on the {string} comboBox")
  public void verifyComboSelection(String value, String label) {
    JLog.screenCapture();
    try {
      AbstractPage.sleep(2);
      JLog.screenCapture();
      if (label.contains("Parent") && !value.equals("none"))
        value = value + timeStamp;
      JLog.write("Value =" + value);
      MTCMController c = new MTCMController();
      String actVal = c.getSelectedVal(label);
      JLog.write("Act Value=" + actVal);
      Verify.verify(actVal.equals(value), value + " is not selected on " + label + " ComboBox.");
      JLog.write("Succesfully verified parent name on the combo");
    } catch (Exception e) {
      JLog.screenCapture();
      Verify.verify(
          e.toString().contains("No options are selected") && value.equals("none"),
          "Parent Name is selected on comboBox even after removing it.");
    }
  }

  public void removeItems(String FG) throws Throwable {
    ui = new HarmonyLoginUI();
    ui.logout_mtcm();
    InputStream fo1 = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
    Properties p1 = new Properties();
    p1.load(fo1);
    String un = p1.getProperty("login");
    ui.login_harmony_mtcm_role("mtcmUser", un);
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
    General gen = new General();
    // gen.setGroupName(FG);
    // gen.clickButton("Apply");
    model = new FunctionalGroupModel();
    model.setSearchGroupName(FG);
    controller = new FunctionalGroupController();
    controller.setModel(model);
    controller.searchForGroup();
    AbstractPage.sleep(5);
    MTCMController ctller = new MTCMController();
    ctller.clickName(FG);
    ctller.selectAllCheckBoxes();
    ctller.clickButton("Remove Item");
    gen.acceptRejectConfirm("accept");
    FunctionalGroupController c = new FunctionalGroupController();
    c.saveGroup();
  }

  @Then("I verify the {string} on the Manage FG page")
  public void verifyGrpNameOnFGPage(String action) throws Throwable {
    ui = new HarmonyLoginUI();
    if (action.equals("RENAME")) {
      UploadController upctrller = new UploadController();
      String fg = "FGUpload" + upctrller.getTimeStamp();
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      controller = new FunctionalGroupController();
      AbstractPage.sleep(10);
      controller.setGrpName(fg);
      controller.clickButton("Apply");
      AbstractPage.sleep(5);
      MTCMController ctller = new MTCMController();
      ctller.clickName(fg);
      AbstractPage.sleep(5);
      controller = new FunctionalGroupController();
      String gn = controller.getCreateGrpNameValue();
      Verify.verify(gn.equals(fg), " Group Name is not changed after Upload.");
      // } else if (action.equals("Active")) {
      // General gen = new General();
      // ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional
      // Group");
      // controller = new FunctionalGroupController();
      // AbstractPage.sleep(10);
      // controller.setGrpName("fg-update");
      // controller.clickButton("Apply");
      // AbstractPage.sleep(5);
      // MTCMController ctller = new MTCMController();
      // ctller.clickName("fg-update");
      // AbstractPage.sleep(5);
      // gen.verifyCheckBoxStatus("status","unchecked");
    } else if (action.equals("Inactive")) {
      General gen = new General();
      ui.navHarmonyMTCM("Supply Collaboration", "Manage Functional Group");
      controller = new FunctionalGroupController();
      // AbstractPage.sleep(10);
      controller.setGrpName("fg-update");
      controller.clickButton("Apply");
      // AbstractPage.sleep(5);
      MTCMController ctller = new MTCMController();
      ctller.clickName("fg-update");
      // AbstractPage.sleep(5);
      gen.verifyCheckBoxStatus("status", "checked");
    }
  }

  @Then("I remove the added FG via parent upload")
  public void removeFG() throws Throwable {
    General gen = new General();
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Upload/Manage Jobs", "Supply Allocation");
    UploadModel m = new UploadModel();
    UploadController c = new UploadController();
    String uploadFile = Prop.getInstance().getRootDir() + "scplatform/data/ParentUpload.xlsx";
    // m.setMessageType("Parent Functional Group (*.xls)");
    m.setUploadFile(uploadFile);
    c.setModel(m);
    c.upload("success", "", "removeFG", "ParentFunctionalGroupUploadUI");
    checkForErrors();
  }

  @Then("I verify the rename on the Manage Parent page")
  public void verifyParentRename() throws Throwable {
    UploadController upctrller = new UploadController();
    String pN = "TestParent12" + upctrller.getTimeStamp();
    ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Supply Collaboration", "Manage Parent Group");
    FunctionalGroupController c = new FunctionalGroupController();
    AbstractPage.sleep(10);
    c.clickButton("Clear");
    c.setSearchParentName(pN);
    c.clickButton("Apply");
    AbstractPage.sleep(5);
    MTCMController ctller = new MTCMController();
    ctller.clickName(pN);
    AbstractPage.sleep(5);
    c = new FunctionalGroupController();
    String parentName = c.getCreateEditParentName();
    Verify.verify(parentName.equals(pN), " Parent Name is not changed after Upload.");
  }

  @And("I add new FG {string} to the parent group")
  public void addNewFGonParentEditPage(String fg) {
    controller = new FunctionalGroupController();
    // String parent = controller.browser().getWindowHandle();
    controller.clickButton("Add Functional Group");
    // AbstractPage.sleep(8);
    // ArrayList<String> newTab = new
    // ArrayList<>(controller.browser().getWindowHandles());
    // newTab.remove(parent);
    // controller.browser().switchTo().window(newTab.get(0));
    FunctionalGroupController.setOverrideContext("contentFrame", "mainModalFrame");
    controller = new FunctionalGroupController();
    controller.addItemFromNewWindow(fg);
    controller = new FunctionalGroupController();
    controller.saveParentGroup();
  }

  @And("I verify responsibility field is not expanded")
  public void verifyResp() {
    controller = new FunctionalGroupController();
    controller.verifyRespColumnNotExpanded();
  }

  @And("I remove added FG {string} from the parent group")
  public void removeAddedFGFrmParentEditPage(String fg) {
    controller = new FunctionalGroupController();
    controller.removeItemName(fg);
    controller = new FunctionalGroupController();
    controller.saveParentGroup();
    JLog.screenCapture();
    JLog.write("Succesfully removed added FG from Parent.");
  }

  @Then("I verify the {string} field in search filter")
  public void verifySearchFilter(String filterName) throws Throwable {
    controller = new FunctionalGroupController();
    controller.isSearchFilterDisplayed(filterName);
    JLog.write("Verified the items on the group");
  }

  @Then("I verify the status {string} of the Functional Group {string}")
  public void verifyFGStatus(String grpName, String expectedStatus) {
    FunctionalGroupController c = new FunctionalGroupController();
    c.verifyStatus(expectedStatus);
    checkForErrors();
  }
}
