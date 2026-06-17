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
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.modelViewController.SearchController;
import com.google.common.base.Verify;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Search {
  MTCMController c;
  SearchController ctrller;
  Prop prop = Prop.getInstance();

  @Before
  public void beforeMethod (Scenario scenario) {
    JLog.setScenarioForCucumber(scenario);
    JLog.resetErrorCount();
  }

  private void checkForErrors () {
    if (JLog.getErrorCount() > 0) {
      JLog.fail(
          JLog.getErrorCount() + " errors occurred in the test.  Check log.",
          TakeScreenshot.True);
    }
  }

  @Then("I {string} AssignToSelf checkBox")
  public void assignToSelfSelection (String action) throws Throwable {
    ctrller = new SearchController();
    ctrller.assignToSelfSelection(action);
    checkForErrors();
  }

  @Then("I {string} unAssign OtherUsers checkBox")
  public void unAssignOtherUsers (String action) throws Throwable {
    ctrller = new SearchController();
    ctrller.unAssignOtherUsers(action);
    checkForErrors();
  }

  @Then("I click and verify details of {string} link on the popup")
  public void clickLinkToVerifyPopupDetails (String linkText) throws Exception {
    ctrller = new SearchController();
    String itemDetails;
    ctrller.getAndSaveItemDetailsFromSearchFilterGrid();
    ctrller.clickName(linkText);
    AbstractPage.sleep(2);
    InputStream fr =
        new FileInputStream(prop.getRootDir() + "scplatform/data/properties/itemDetails.properties");
    Properties p = new Properties();
    p.load(fr);
    itemDetails = ctrller.getValueOnTextFieldWithID("popupItemKey");// itemID
    Verify.verify(!itemDetails.equals(""), "itemID is empty");

    itemDetails = ctrller.getValueOnTextFieldWithID("popupItemItemType"); // item
                                                                          // type
    Verify.verify(itemDetails.equals(p.getProperty("ItemType")), "Unable to verify item type");
    // itemDetails =
    // ctrller.getValueOnTextFieldWithID("popupItemDataSource"); // source
    // // SYstem
    // Verify.verify(itemDetails.equals(p.getProperty("SourceSystem")),
    // "Unable to verify SourceSystem");

    itemDetails = ctrller.getValueOnTextFieldWithID("popupItemDescription"); // item
                                                                             // Descp
    Verify.verify(
        itemDetails != null ? itemDetails.equals(p.getProperty("ItemDescp")) : true,
        "Unable to verify ItemDescp");
    // itemDetails = ctrller.getValueOnTextFieldWithID("popupItemBusiness");
    // // item
    // // business
    // Verify.verify(itemDetails != null ?
    // itemDetails.equals(p.getProperty("ItemBusiness")) : true,
    // "Unable to verify ItemBusiness");

    itemDetails = ctrller.getValueOnTextFieldWithID("popupItemEOLState"); // item
                                                                          // state
    Verify.verify(itemDetails.equals("ACTIVE"), "Unable to verify item eol state");
    JLog.write("Succesfully launced popup with Item details and verified the data.");
    checkForErrors();
  }

  @Then("I verify itemDetails displayed on popup")
  public void vreifyItemData () throws Throwable {
    ctrller = new SearchController();
    // ctrller.unAssignOtherUsers();
    checkForErrors();
  }

  @Then("I verify the {string} on the Search Items page")
  public void verifyItemScreen (String action) throws Throwable {
    ctrller = new SearchController();
    ctrller.verifySearchItem(action);
    checkForErrors();
  }

  @Then("I verify the responsibility {string} {string} to {string}")
  public void verifyItemResp (String responsibiltyValue, String respAction, String item)
      throws Throwable {
    ctrller = new SearchController();
    ctrller.verifyItemResponsibility(responsibiltyValue, respAction, item);
    JLog.write("Verified the item " + item + "is " + respAction + " with " + responsibiltyValue);
    checkForErrors();
  }

  @And("I set {string} dropdown with Value {string} for Items")
  public void setDropdownValue (String dropDwnLabelName, String value) throws Exception {
    ctrller = new SearchController();
    ctrller.setDropDownValue(dropDwnLabelName, value);
    JLog.screenCapture();
    AbstractPage.sleep(5);
    checkForErrors();
  }

  @Then("I verify the responsibility {string} {string} to {string} on any row")
  public void verifyItemRespOnAnyRow (String responsibiltyValue, String respAction, String item)
      throws Throwable {
    ctrller = new SearchController();
    ctrller.verifyItemResponsibilityonAnyRow(responsibiltyValue, respAction, item);
    JLog.write("Verified the item " + item + "is " + respAction + " with " + responsibiltyValue);
    checkForErrors();
  }

  @Then("I verify the responsibility {string} {string} to {string} on all rows")
  public void verifyItemRespOnRows (String responsibiltyValue, String respAction, String item)
      throws Throwable {
    ctrller = new SearchController();
    ctrller.verifyItemResponsibilityonAllRows(responsibiltyValue, respAction, item);
    JLog.write("Verified the item " + item + "is " + respAction + " with " + responsibiltyValue);
    checkForErrors();
  }

  @Then("I verify the responsibility {string} {string} to {string} on first 2 rows")
  public void verifyItemRespOn2Rows (String responsibiltyValue, String respAction, String item)
      throws Throwable {
    ctrller = new SearchController();
    ctrller.verifyItemResponsibilityon2Rows(responsibiltyValue, respAction, item);
    JLog.write("Verified the item " + item + "is " + respAction + " with " + responsibiltyValue);
    checkForErrors();
  }

  @Then("I verify the responsibility {string} and {string} assigned for commodity {string}")
  public void verifyCommodityResp (String responsibiltyValue, String assignedTo, String commodity)
      throws Throwable {
    ctrller = new SearchController();
    ctrller.verifyCommodityResponsibility(assignedTo, responsibiltyValue, commodity);
    JLog.write(
        "Verified the commodity " + commodity + "is assigned with " + assignedTo
            + " and responsibilty set to " + responsibiltyValue);
    checkForErrors();
  }

  @Then("I verify the fields set for Search Item Assignment are cleared")
  public void verifSearchItemAssigment () throws Throwable {
    ctrller = new SearchController();
    Verify.verify(
        ctrller.getTextFieldValue("itemNumber").equals(""),
        "Item Number field is not cleared");
    Verify.verify(
        ctrller.getTextFieldValue("business").equals(""),
        "Item Business field is not cleared");
    Verify.verify(
        ctrller.getMultipleTextFieldValue("itemNumbers").equals(""),
        "Item Numbers are not cleared");
    JLog.write("Verified the fields set for Search Item Assignment are cleared.");
    checkForErrors();
  }

  @Then("I verify the fields set for Search Item AVL are cleared")
  public void verifSearchItemAVL () throws Throwable {
    ctrller = new SearchController();
    Verify.verify(
        ctrller.getTextFieldValue("itemNumber").equals(""),
        "Item Number field is not cleared");
    Verify.verify(
        ctrller.getTextFieldValue("itemClassification").equals(""),
        "Item Classification field is not cleared");
    Verify.verify(
        ctrller.getMultipleTextFieldValue("itemNumbers").equals(""),
        "Mulitple Item Numbers are not cleared");
    JLog.write("Verified the fields set for Search Item AVL are cleared.");
    checkForErrors();
  }

  @Then("I verify the fields set for Search BOMs are cleared")
  public void verifSearchBOMs () throws Throwable {
    ctrller = new SearchController();
    Verify.verify(
        ctrller.getTextFieldValue("itemNumber").equals(""),
        "Item Number field is not cleared");
    Verify.verify(
        ctrller.getTextFieldValue("description").equals(""),
        "BOM description field is not cleared");
    Verify.verify(
        ctrller.getMultipleTextFieldValue("itemNumbers").equals(""),
        "Mulitple Item Numbers are not cleared");
    JLog.write("Verified the fields set for Search BOMs are cleared.");
    checkForErrors();
  }

  @Then("I verify the fields set for Search Items are cleared")
  public void verifSearchItems () throws Throwable {
    ctrller = new SearchController();
    Verify.verify(
        ctrller.getTextFieldValue("itemNumber").equals(""),
        "Item Number field is not cleared");
    Verify.verify(
        ctrller.getTextFieldValue("itemDescription").equals(""),
        "Item Description field is not cleared");
    Verify.verify(
        ctrller.getMultipleTextFieldValue("categoryNames").equals(""),
        "Multiple Commodity Names are not cleared");
    JLog.write("Verified the fields set for Search Items are cleared.");
    checkForErrors();
  }

  @Then("I verify the fields set for Commodity Management are cleared")
  public void verifyCommodityManagement () throws Throwable {
    ctrller = new SearchController();
    Verify.verify(
        ctrller.getTextFieldValue("categoryName").equals(""),
        "Item Number field is not cleared");
    JLog.write("Verified the fields set for Search Items are cleared.");
    checkForErrors();
  }

  @Then("I verify CFG and other result details for {string}")
  public void verifyCFGDetails (String item) throws Throwable {
    ctrller = new SearchController();
    ctrller.verifyItemAVLDetails(item);
    JLog.write("Verified the fields ITEM AVL Search Results.");
    checkForErrors();
  }

  @When("I click {string} on the warning popup on assign resp page with message {string}")
  public void clickWarningPopupOnAssignResp (String btnName, String msg) {
    JLog.screenCapture();
    AbstractPage page = new AbstractPage();
    page.sleep(10);
    By by = By.xpath("//section[@class='eto-modal__body']//p[contains(text(),'" + msg + "')]");
    WebElement element = page.get(by);
    boolean status = element.isDisplayed();
    page.sleep(3);
    Verify.verify(status, "Unable to verify the warning popup msg " + msg);
    ctrller = new SearchController();
    ctrller.clickButton(btnName);
    checkForErrors();

  }

  @When("I {string} {string} responsibility to {string} assigned with {string} as existing role")
  public void assignToResp (String action, String newRole, String respUser, String existingRole)
      throws Exception {
    // MTCMController controller = new MTCMController();
    ctrller = new SearchController();
    ctrller.assignRoleAndResp(existingRole);
    if (action.equals("assign")) {
      ctrller.clickButton("Assign Responsibility");
      ctrller = new SearchController();
      ctrller.setElementValue("assignmentUserId", respUser);
      ctrller.setComboByName("assignmentResponsibility", newRole);
    } else if (action.equals("unassign")) {
      ctrller.clickButton("Unassign Responsibility");
    }
    ctrller.clickDynamicBtn("");
    // ctrller = new MTCMController();
    // AbstractPage.sleep(1);
    // boolean status = controller.verifyWarningPopupMsgs("The Production
    // responsibility has already been assigned");
    // JLog.screenCapture();
    // if (status) {
    // controller.clickButton("Yes");
    // }
    checkForErrors();
    JLog.write(
        action + "ed " + newRole + " reponsibility to " + respUser + " for the item selected");
  }

  @When("I {string} responsibility {string} to the item {string} selected")
  public void assignUnAssignResp (String action, String respUser, String item) throws Exception {
    MTCMController controller = new MTCMController();
    if (item.equals("all")) {
      controller.clickAllRows();
    } else if (item.equals("2"))
      controller.selectInputElements(2, "selectedPageKeys", "checkbox");
    else
      controller.selectInputElements(1, "selectedPageKeys", "checkbox");
    if (action.equals("assign")) {
      controller.clickButton("Assign Responsibility");
      controller = new MTCMController();
      controller.setElementValue("assignmentUserId", respUser);
      // controller = new MTCMController();
      String actVal = "";
      WebElement respCombo = controller.getEleByID("responsibilityList");
      Select select = new Select(respCombo);
      actVal = select.getFirstSelectedOption().getAttribute("value");
      JLog.write("Act Value=" + actVal);
      Verify.verify(!actVal.equals(""), "Responsibility combobox is null");
      JLog.write("Succesfully verified that Responsibility combobox is not null");
    } else if (action.equals("unassign")) {
      controller.clickButton("Unassign Responsibility");
    }
    controller.clickDynamicBtn("");
    controller = new MTCMController();
    AbstractPage.sleep(1);
    boolean status = controller
        .verifyWarningPopupMsgs("The Production responsibility has already been assigned");
    JLog.screenCapture();
    if (status) {
      controller.clickButton("Yes");
    }
    checkForErrors();
    JLog.write(action + "ed " + respUser + " to the item- " + item);
  }

  @When("I verify the managed by set to {string} for commodity {string}")
  public void setManagedBy (String setManagedToValue, String commodity) throws Exception {
    ctrller = new SearchController();
    ctrller.setDeselectVerifyManagedTo(setManagedToValue, commodity);
    JLog.write(
        "Verified the Set Managed By for Commodity -" + commodity + " as " + setManagedToValue);
    checkForErrors();
  }

  // deleteAddedAlternateNames
}
