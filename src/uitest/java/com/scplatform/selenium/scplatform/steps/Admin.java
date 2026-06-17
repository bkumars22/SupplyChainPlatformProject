/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import java.text.ParseException;

import org.joda.time.DateTime;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.AdminController;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.google.common.base.Verify;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Admin {

  HarmonyLoginUI ui = new HarmonyLoginUI();
  static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
  AdminController controller;

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

  @And("I enter and verify alternate names {string} and {string}")
  public void setAlternateVals (String val1, String val2) {
    controller = new AdminController();
    controller.setAndVerifyAlternateNames(val1, val2);
    checkForErrors();
  }

  @And("I verify Region List is on sorted Order")
  public void verifyRegionList () {
    controller = new AdminController();
    controller.verifyRegionList();
    checkForErrors();
  }

  @And("I enter and verify alternate name {string}")
  public void setAlternateVal (String val1) {
    controller = new AdminController();
    controller.setAndVerifyAlternateName(val1);
    checkForErrors();
  }

  @And("I delete and verify alternate names")
  public void deleteAlternateVals () {
    controller = new AdminController();
    controller.deleteAddedAlternateNames();
    checkForErrors();
  }

  @Then("I verify the fields got cleared on User Commodity page")
  public void verifyFieldsClearedOnUserCommodityPage () {
    controller = new AdminController();
    Verify.verify(
        controller.getTextFieldValue("profileName").equals(""),
        "name field is not cleared");
    Verify.verify(
        controller.getTextFieldValue("companyItemType").equals(""),
        "companyItemType field is not cleared");
    Verify.verify(
        controller.getTextFieldValue("categoryName").equals(""),
        "categoryName field is not cleared");
    JLog.write("Verified that fields are cleared after clicking clear button");
  }

  @Then("I verify the fields are cleared on Manage Contacts page")
  public void verifyFieldsClearedOnManageContacts () {
    controller = new AdminController();
    Verify.verify(
        controller.getTextFieldValue("contactName").equals(""),
        "contactName field is not cleared");
    Verify.verify(
        controller.getTextFieldValue("businessName").equals(""),
        "businessName field is not cleared");
    JLog.write("Verified that fields are cleared after clicking clear button");
  }

  @Then("I verify list is sorted in ascending order")
  public void verifyListSorted () {
    controller = new AdminController();
    boolean status = controller.getBusinessEntitiesAndVerifySorted();
    Verify.verify(status, "Business Entities list is not in sorted order.");
    JLog.write("Verified that list is sorted in ascending order.");
  }

  @When("I verify functionalGroup name as {string} on groupName Column")
  public void verifyFGName (String expName) {
    controller = new AdminController();
    JLog.write("Exp FG name =" + expName);
    String actName = controller.getItemDetails("6");
    JLog.write("Act parent name =" + actName);
    Verify.verify(actName.contains(expName), "fgName mismatch on Manage Items page.");
    JLog.write("Successfully verified FGName on Manage Itemss");
  }

  @When("I verify parentName as {string} on parentName column")
  public void verifyParentName (String expName) {
    controller = new AdminController();
    JLog.write("Exp parent name =" + expName);
    String actName = controller.getItemDetails("7");
    JLog.write("Act parent name =" + actName);
    Verify.verify(actName.contains(expName), "parentName mismatch on Manage Items page.");
    JLog.write("Successfully verified parentName on Manage Itemss");
  }

  @Then("I verify {string} with checkbox {string} is not listed on Business Document on Manage Roles page")
  public void verifyBusDocLabel (String label, String id) throws Throwable {
    controller = new AdminController();
    controller.verifyBusDoc(id, label);
    checkForErrors();
  }

  @Then("I verify {string} with checkbox {string} is listed on Business Document on Manage Roles page")
  public void verifyBusDocLabelIsDIsplayed (String label, String id) throws Throwable {
    controller = new AdminController();
    controller.verifyBusDocAvailability(id, label);
    checkForErrors();
  }

  @Then("I verify the fields are cleared on Manage Users page")
  public void verifyFieldsClearedOnManageUsers () {
    controller = new AdminController();
    Verify.verify(controller.getTextFieldValue("userId").equals(""), "userId field is not cleared");
    Verify.verify(
        controller.getTextFieldValue("businessName").equals(""),
        "businessName field is not cleared");
    Verify.verify(controller.getTextFieldValue("roleId").equals(""), "roleId field is not cleared");
    JLog.write("Verified that fields are cleared after clicking clear button");
  }

  @Then("I verify the fields are cleared on Manage Business Entities page")
  public void verifyFieldsClearedOnManageBusinessEntities () {
    controller = new AdminController();
    Verify.verify(
        controller.getTextFieldValue("businessName").equals(""),
        "businessName field is not cleared");
    Verify.verify(
        controller.getTextFieldValue("businessId").equals(""),
        "businessId field is not cleared");
    JLog.write("Verified that fields are cleared after clicking clear button");
  }

  @When("I select the {string} from the results")
  public void selectResult (String userID) {
    controller = new AdminController();
    if (userID.contains("CR")) {
      UploadController uC = new UploadController();
      userID = "CR" + uC.getTimeStamp();
    }
    controller.clickResultLinkFromRow1(userID);
  }

  @Then("I verify the {string} details on the User Details page")
  public void verifyUserDetails (String user) {
    controller = new AdminController();
    String expUserName = "";
    String expEmail = "";
    if (user.contains("bruce_aug_scm")) {
      expUserName = "  Bruce (SCM) Aug";
      expEmail = "bruce_aug@dell.com";
    } else if (user.contains("mary_steinocher_scm")) {
      expUserName = "  Mary (SCM) Steinocher";
      expEmail = "mary_steinocher@dell.com";
    }
    Verify.verify(
        controller.getElementValue("selectedUser.userName").equals(expUserName),
        "User Name value is not matching");
    Verify.verify(
        controller.getElementValue("selectedUser.emailAddress").equals(expEmail),
        "email field is not matching");
    JLog.write("Verified the user details of " + user);
  }

  @When("I edit the users by {string} Agent Of Business on the User Details page")
  public void editUserDetails (String action) {
    controller = new AdminController();
    controller.editUser(action);
    checkForErrors();
  }

  @Then("I verify the {string} displayed on details page.")
  public void verifyStatus (String expStatus) {
    controller = new AdminController();
    controller.verifyStatus(expStatus);
    JLog.write("Successfully verified status");
    checkForErrors();
  }

  @When("I select the {string} role checkBox to delete the role")
  public void selectRoleCheckBox (String roleIDName) {
    controller = new AdminController();
    roleIDName = roleIDName + timeStamp;
    controller.selectRoleCheckBox(roleIDName);
    checkForErrors();
  }

  @When("I {string} the {string} checkBox to grant {string} permissions")
  public void selectDeselectPermissionsCheckBox (String permission, String name, String value) {
    controller = new AdminController();
    controller.selectPermissionCheckBox(permission, name, value);
    checkForErrors();
  }

  @When("I edit the Contact Details of {string}")
  public void editContactDetails (String contact) throws ParseException {
    controller = new AdminController();
    controller.editContactDetails();
    JLog.write("Successfully edited contact details of " + contact);
    checkForErrors();
  }

  @When("I delete the edited details of {string}")
  public void deleteContactDetails (String contact) throws ParseException {
    controller = new AdminController();
    controller.deleteContactDetails();
    JLog.write("Successfully deleted edited contact details of " + contact);
    checkForErrors();
  }

  @When("I enter the value {string} on {string} field")
  public void enterValue (String value, String textFieldId) {
    controller = new AdminController();
    if (value.contains("Role")) {
      value = value + timeStamp;
    }
    controller.setValueOnTextFieldWithID(value, textFieldId);
    JLog.write("Set value " + value + " on " + textFieldId + " field");
    checkForErrors();
  }

  @Then("I verify the added contact details")
  public void verifyAddedContactDetails () {
    controller = new AdminController();
    General gen = new General();
    String t = gen.getTimeStampFromGeneral();
    controller.verifyAddedContactDetails("2", "contactBusinessName" + t);
    JLog.write("Successfully verified added email on contact details");
    controller.verifyAddedContactDetails("4", "testEmail@email.com");
    JLog.write("Successfully verified added email on contact details");
    checkForErrors();
  }

  @Then("I verify the {string} details on Contact Details from {string} page")
  public void verifyContactDetailsOnBuisinessEntitiesPage (String contact, String page) {
    controller = new AdminController();
    Verify.verify(
        controller.getDisabledContactNameValue(contact, page),
        "contactName field is not matching");
    if (!contact.contains("contactName")) {
      Verify.verify(
          controller.getElementValue("selected" + page + ".contact.businessName").contains(contact),
          "businessName field is not matching");
      Verify.verify(
          controller.getElementValue("selected" + page + ".contact.contactId")
              .equals("NL-GLOBAL BRAND MANUFACTURING SHE-USD"),
          "Contact Id field is not matching");
      Verify.verify(
          controller.getElementValue("selected" + page + ".contact.department").equals("test"),
          "Department field is not matching");
    } else {
      Verify.verify(
          controller.getElementValue("selected" + page + ".contact.businessName")
              .contains("contactBusinessName"),
          "businessName field is not matching");
    }
    JLog.write("Verified the Contact Details of " + contact + page + " page.");
    checkForErrors();
  }

  @Then("I verify the {string} details on Business Entities page")
  public void verifyDetailsOnBuisinessEntitiesPage (String contact) {
    controller = new AdminController();
    Verify.verify(controller.getDisplayedDetails(contact), "businessName field is not matching");
    Verify.verify(
        controller.getDisplayedDetails("DELL"),
        "business Id field is not matching");
    JLog.write("Verified the Details of " + contact);
    checkForErrors();
  }

  @Then("I verify the {string} details on Contact Details page")
  public void verifyContactDetailsPage (String contact) {
    controller = new AdminController();
    if (contact.contains("edited") || contact.contains("deleted"))
      contact = contact.substring(0, contact.lastIndexOf('-'));
    Verify.verify(
        controller.getElementValue("selectedContact.contactName").equals(contact),
        "contactName field is not matching");
    Verify.verify(
        controller.getElementValue("selectedContact.businessName").equals(contact),
        "businessName field is not matching");
    Verify.verify(
        controller.getElementValue("selectedContact.contactId")
            .equals("NL-GLOBAL BRAND MANUFACTURING SHE-USD"),
        "Contact Id field is not matching");
    Verify.verify(
        controller.getElementValue("selectedContact.department").equals("test"),
        "Department field is not matching");
    if (contact.contains("edited")) {
      Verify.verify(
          controller.getElementValue("selectedContact.addressL1").equals("test address1"),
          "Address1 field is not matching");
      Verify.verify(
          controller.getElementValue("selectedContact.addressL2").equals("test address2"),
          "Address2 field is not matching");
      Verify.verify(
          controller.getElementValue("selectedContact.addressL3").equals("test address3"),
          "Address3 field is not matching");
      Verify.verify(
          controller.getElementValue("selectedContact.email").equals("testContact@gmail.com"),
          "Address3 field is not matching");
      Verify.verify(
          controller.getElementValue("selectedContact.region").equals("test Region"),
          "Region field is not matching");
      Verify.verify(
          controller.getElementValue("selectedContact.postalCode").equals("560093"),
          "Postal code field is not matching");
    }
    if (contact.contains("deleted")) {
      Verify.verify(
          controller.getElementValue("selectedContact.addressL1").equals(""),
          "Address1 field is not deleted");
      Verify.verify(
          controller.getElementValue("selectedContact.addressL2").equals(""),
          "Address2 field is not deleted");
      Verify.verify(
          controller.getElementValue("selectedContact.addressL3").equals(""),
          "Address3 field is not deleted");
      Verify.verify(
          controller.getElementValue("selectedContact.region").equals(""),
          "Region field is not deleted");
      Verify.verify(
          controller.getElementValue("selectedContact.postalCode").equals(""),
          "Postal code field is not deleted");
    }
    JLog.write("Verified the Contact Details of " + contact);
    checkForErrors();
  }

  @When("I click on Change Contact Button")
  public void clickChangeContact () {
    controller = new AdminController();
    // String parent = controller.browser().getWindowHandle();
    controller.clickButton("Change Contact");
    // ArrayList<String> newTab = new
    // ArrayList<>(controller.browser().getWindowHandles());
    // newTab.remove(parent);
    // AdminController.setOverrideContext("null");
    // controller = new AdminController();
    // controller.browser().switchTo().window(newTab.get(0));
    JLog.write("Clicked on changed contact Button");
    checkForErrors();
  }

  public void clearTestData (Scenario scenario) throws Throwable {
    String status = scenario.getStatus().toString();
    String name = scenario.getName();
    String user = "admin";
    // if (name.contains("GCM")) {
    // user = "ajay_arjunan@dell.com";
    // } else if (name.contains("Supplier")) {
    // user = "adele_ding_amd";
    // } else if (name.contains("Buyer")) {
    // user = "aaron_lee";
    // }
    JLog.write("..............Clearing Test Data.............");
    JLog.write("For test with name " + name + "!");
    ui = new HarmonyLoginUI();
    // ui.logout_mtcm();
    // // ui.login_harmony_mtcm_role("mtcmUser", user);
    ui.login_harmony_mtcm("mtcmUser");
    // // sometimes all menus are not displaying, so logging in again after
    // // logout
    // ui.logout_mtcm();
    // ui.login_harmony_mtcm("mtcmUser");
    General gen = new General();
    gen.waitTillPageLoads("10");
    controller = new AdminController();
    if (name.contains("edit the user details by adding agent")
        || (name.contains("edit the user details by deleting the agent")
            && status.equals("FAILED"))) {
      ui.navHarmonyMTCM("Administration", "Manage Users");
      controller = new AdminController();
      String expUserName = "";
      if (user.contains("bruce_aug_scm")) {
        expUserName = "  Bruce (SCM) Aug";
      } else if (user.contains("mary_steinocher_scm")) {
        expUserName = "  Mary (SCM) Steinocher";
      }
      controller.setValField(expUserName, "userId");
      gen.clickButton("Apply");
      controller.clickResultLinkFromRow1(expUserName);
      controller = new AdminController();
      controller.clickIconButton("close");
    } else if (name.contains("Manage Contacts edit")) {
      ui.navHarmonyMTCM("Administration", "Manage Contacts");
      controller.setElementValue("19417", "contactName");
      gen.clickButton("Apply");
      controller.clickResultLinkFromRow1("19417");
      controller.setElementValue("selectedContact.addressL1", "");
      controller.setElementValue("selectedContact.addressL2", "");
      controller.setElementValue("selectedContact.addressL3", "");
      controller.setElementValue("selectedContact.email", "");
      controller.setElementValue("selectedContact.region", "");
      controller.setElementValue("selectedContact.postalCode", "");
      gen.clickSaveAndExitButton();
    } else if (name.contains("Manage Contacts edit delete and verify")) {
      ui.navHarmonyMTCM("Administration", "Manage Contacts");
      controller.setElementValue("Test Data", "contactName");
      controller.clickButton("Apply");
      controller.clickResultLinkFromRow1("Test Data");
      controller.setElementValue("selectedContact.addressL1", "");
      controller.setElementValue("selectedContact.addressL2", "");
      controller.setElementValue("selectedContact.addressL3", "");
      controller.setElementValue("selectedContact.region", "");
      controller.setElementValue("selectedContact.postalCode", "");
      gen.clickSaveAndExitButton();
    } else if (name.contains("Create Contact on Manage Contacts")) {
      ui.navHarmonyMTCM("Administration", "Manage Contacts");
      controller.setElementValue("contactName" + gen.getTimeStampFromGeneral(), "contactName");
      gen.clickButton("Apply");
      gen.selectRows("1", "selectedPageKeys", "checkbox");
      gen.clickButton("Delete Contact");
      gen.clickDynamicButton("Yes, Delete");
      gen.verifySuccessMsg("Contact(s) has been deleted successfully");
    } else if (name.contains("Create and Delete Contact") && status.equals("FAILED")) {
      ui.navHarmonyMTCM("Administration", "Manage Contacts");
      controller
          .setElementValue("testDeletecontactName" + gen.getTimeStampFromGeneral(), "contactName");
      gen.clickButton("Apply");
      gen.selectRows("1", "selectedPageKeys", "checkbox");
      gen.clickButton("Delete Contact");
      gen.clickDynamicButton("Yes, Delete");
      gen.verifySuccessMsg("Contact(s) has been deleted successfully");
    } else if (name.contains("Create new news") || name.contains("Verify new news")
        || name.contains("Edit news")
        || (name.contains("Delete news") && status.equals("FAILED"))) {
      ui.navHarmonyMTCM("Administration", "Change Dashboard News");
      String title = null;
      if (name.contains("Create")) {
        title = "testDashboardNews";
      } else if (name.contains("Edit")) {
        title = "NewsForEdit";
      } else if (name.contains("Delete")) {
        title = "testDashboardNewsForDelete";
      } else if (name.contains("Verify new news addded with role as Admin user")) {
        title = "testAdminNews";
      } else if (name.contains("Verify new news added with role as GCM user")) {
        title = "testGCMNews";
      } else if (name.contains("Verify new news added with role as Supplier")) {
        title = "testSuppNews";
      } else if (name.contains("Edit news for a specific role")) {
        title = "NewsForEdit";
      }
      editDeleteDashboardNews("delete", title);
      verifyDashBoardNewsCreated(title, "not displayed");
      ui = new HarmonyLoginUI();
      ui.logout_mtcm();
    } else if (name.contains("on Manage Roles page")
        || (name.contains("Delete and Verify new roles") && status.equals("FAILED"))) {
      ui.navHarmonyMTCM("Administration", "Manage Roles");
      String roleID = "";
      if (name.contains("Copy role button")) {
        roleID = "copyAndCreateRole";
      } else if (name.contains("create role button")) {
        roleID = "createRole";
      } else if (name.contains("Delete")) {
        roleID = "createRoleForDelete";
      } else if (name.contains("duplicate")) {
        roleID = "createDupRole";
      }
      selectRoleCheckBox(roleID);
      gen.clickButton("Delete Role");
      gen.verifyAndConfirmOrReject("Are you sure you want to delete this role(s) ?", "Yes");
      gen.verifySuccessMsg("Role(s) has been deleted successfully");
      verifyLink("createRoleForDelete", "not visible");
    }
    JLog.write("Successfully deleted test data!");
  }

  @When("I click on the {string} to verify the details")
  public void clickItemLink (String linkText) throws Exception {
    controller = new AdminController();
    String itemNumber = linkText;
    // String itemDescp;
    String commodity;
    String enterprise;
    if (linkText.contains("itemNumber")) {
      linkText = controller.getItemNumberLink();
    }
    // itemDescp = controller.getItemDetails(1, 3);
    commodity = controller.getItemDetails(1, 5);
    enterprise = controller.getItemDetails(1, 8); // item Business
    controller.clickName(linkText);
    // Properties p = new Properties();
    // FileOutputStream fr = new FileOutputStream(prop.getRootDir() +
    // "scplatform/data/itemDetails.properties");
    // p.setProperty("ItemNumber", linkText);
    // p.setProperty("Responsibility", controller.getItemDetails(1, 7)); //
    // Responsibility
    // p.setProperty("ItemBusiness", controller.getItemDetails(1, 3)); //
    // Item
    // p.save(fr, ""); // Business
    // JLog.write(p.toString());
    // saved the item details to a properties file
    General gen = new General();
    gen.verifyTitle(linkText);
    controller = new AdminController();
    Verify.verify(controller.isItemLinkVisible(linkText), "ItemNumber link is not visible");
    Verify.verify(
        controller.isEnterpriseTextVisible(enterprise),
        "Item Business detail is not visible");
    Verify.verify(
        controller.getComboSelectedOptionByName("itemCategories").contains(commodity),
        "itemCategories is not visible");
    if (!itemNumber.contains("noPopup")) {
      gen.clickLinkText(linkText);
      // ArrayList<String> newTab = new
      // ArrayList<String>(controller.browser().getWindowHandles());
      // newTab.remove(controller.browser().getWindowHandle());
      // controller.browser().switchTo().window(newTab.get(0));
      // AdminController.setOverrideContext("null");
      controller = new AdminController();
      controller.isVisible("Item Id");
      JLog.write("Succesfully launced popup with Item details.");
      checkForErrors();
    }
    JLog.resetErrorCount();
    checkForErrors();
  }

  @When("I click on the {string} tab on {string} page")
  public void clickTab (String tab, String page) throws Exception {
    controller = new AdminController();
    controller.clickTab(tab);
    JLog.write("Clicked on " + tab + " tab on " + page + " page.");
    checkForErrors();
  }

  @When("I verify {string} link is {string} on Manage Roles page")
  public void verifyLink (String roleID, String status) throws Exception {
    controller = new AdminController();
    roleID = roleID + timeStamp;
    controller.verifyLink(roleID, status);
    checkForErrors();
  }

  @When("I enter the {string} as {string}")
  public void enterLink (String val, String id) throws Exception {
    controller = new AdminController();
    id = "alert" + id;
    val = val + timeStamp;
    controller.setValue(val, id);
    checkForErrors();
  }

  @Then("I verify the Agent of Business is deleted")
  public void verifyAgentOfBusinesDeleted () throws Exception {
    controller = new AdminController();
    controller.getCloseBtnVisibility("not displayed");
    JLog.write("Verified that agent of business is deleted");
    checkForErrors();
  }

  @Then("I verify the {string} {string} on Change Dashboard News page")
  public void verifyDashBoardNewsCreated (String val, String visibility) throws Exception {
    controller = new AdminController();
    val = val + timeStamp;
    if (visibility.contains("not")) {
      Verify.verify(
          !controller.verifyDashBoardNewsCreated(val, "Change News"),
          "Verified dashboard news alert not deleted on Change dashboard news page");
      JLog.resetErrorCount();
      return;
    }
    JLog.write("Exp value= " + val);
    Verify.verify(
        controller.verifyDashBoardNewsCreated(val, "Change News"),
        "Verified created dashboard news alert not displayed on Change dashboard news page");
    checkForErrors();
  }

  @When("I {string} the dashboard news with title {string}")
  public void editDeleteDashboardNews (String action, String title) throws Exception {
    controller = new AdminController();
    title = title + timeStamp;
    String newTitle;
    if (action.equals("edit") && title.contains("test")) {
      newTitle = title.substring(4, title.length());
      controller.selectTitleToEditOrDelete(title, newTitle, action);
      JLog.write("Successfully " + action + "ed dashboard news.");
      return;
    } else if (action.equals("edit") && !title.contains("test")) {
      controller.editDashboardNewsRole(title);
      JLog.write("Successfully " + action + "ed dashboard news.");
      return;
    }
    JLog.screenCapture();
    controller.selectTitleToEditOrDelete(title, "", action);
    JLog.write("Successfully " + action + "d dashboard news.");
    checkForErrors();
  }

  @Then("I verify the {string} link on Dashboard page")
  public void verifyDashBoardDisplayed (String val) throws Exception {
    controller = new AdminController();
    val = val + timeStamp;
    Verify.verify(
        controller.verifyDashBoardNewsCreated(val, "Dashboard"),
        "Verified created dashboard news alert on Dashboard page");
    checkForErrors();
  }

  @Then("I verify the {string} link not on Dashboard page")
  public void verifyNoLinkOnDashBoard (String val) throws Exception {
    controller = new AdminController();
    val = val + timeStamp;
    JLog.screenCapture();
    Verify.verify(
        !controller.verifyDashBoardNewsCreated(val, "Dashboard"),
        "Found dashboard news alert displayed on Dashboard page which should not be.");
    checkForErrors();
  }
}
