/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.steps.General;
import com.test.selenium.scplatform.steps.HarmonyLoginUI;
import com.google.common.base.Verify;

public class AdminController extends MTCMController {

  AdminView view;
  HarmonyLoginUI ui = new HarmonyLoginUI();
  Prop prop = Prop.getInstance();

  @Override
  public PageImpl getView () {
    view = new AdminView();
    return view;
  }

  public void verifyRegionList () {
    view = new AdminView();
    setComboBox("Responsibility", "Regional");
    view = new AdminView();
    WebElement ele = view.getExpandMoreBtn("complexSelectregions");
    view.executeJavaScript("arguments[0].click();", ele);
    view = new AdminView();
    List<WebElement> elements = view.getRegionCheckBoxList();
    String[] s = new String[elements.size()];
    String[] newSortedArray = new String[elements.size()];

    for (int i = 0; i < elements.size(); i++) {
      newSortedArray[i] = s[i] = elements.get(i).getText();
    }
    Arrays.sort(s);
    boolean status = true;
    for (int i = 0; i < elements.size(); i++) {
      if (newSortedArray[i] != s[i]) {
        status = false;
        break;
      }
    }
    Verify.verify(status, "Unable to verify that region list is on sorted order.");
    JLog.write("Successfully verified that region list is on sorted order!");
  }

  public void setAndVerifyAlternateNames (String val1, String val2) {
    view = new AdminView();
    WebElement ele = view.getEleByID("alternateInput");
    ele.sendKeys(val1);
    view.sleep(2);
    ele.sendKeys(Keys.ENTER);
    JLog.screenCapture();
    ele.sendKeys(val1);
    view.sleep(2);
    ele.sendKeys(Keys.ENTER);
    JLog.write("Successfully set alternate names" + val1 + " and " + val2);
    List<WebElement> elements = view.getAlternateNamesDisplayed();
    Verify.verify(elements.size() == 2, "Unable to see added alternatives");
  }

  public void setAndVerifyAlternateName (String val1) {
    view = new AdminView();
    WebElement ele = view.getEleByID("alternateInput");
    ele.sendKeys(val1);
    view.sleep(2);
    ele.sendKeys(Keys.ENTER);
    JLog.screenCapture();
    JLog.write("Successfully set alternate name" + val1);
  }

  public void deleteAddedAlternateNames () {
    view = new AdminView();
    // MTCMController ctrller = new MTCMController();
    clickIconButtons("close");
    General gen = new General();
    gen.clickSaveButton();
    List<WebElement> elements = null;
    try {
      elements = view.getAlternateNamesDisplayed();
    } catch (Exception e) {
      JLog.resetErrorCount();
    }
    Verify.verify(
        elements == null || elements.size() == 0,
        "Alternatives names are still displayed after delete.");
    JLog.resetErrorCount();
  }

  public void verifyStatus (String expStatus) {
    view = new AdminView();
    WebElement e = view.getStatus();
    String actStatus = e.getText();
    Verify.verify(actStatus.equals(expStatus), "Unable to verify status value.");
  }

  public String getItemDetails (String td) {
    view = new AdminView();
    WebElement ele = view.getItemData("1", td);
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    AbstractPage.sleep(3);
    String s = ele.getText();
    if (s.equals("")) {
      s = ele.getAttribute("innerHTML");
    }
    AbstractPage.sleep(1);
    return s;
  }

  public void editUser (String action) {
    view = new AdminView();
    MTCMController controller = new MTCMController();
    String names[];
    if (action.equals("Add")) {
      // String parent = controller.browser().getWindowHandle();
      view.executeJavaScript("arguments[0].click();", view.getButton("Add"));
      view.sleep(2);
      // ArrayList<String> newTab = new
      // ArrayList<>(controller.browser().getWindowHandles());
      // newTab.remove(parent);
      // view.browser().switchTo().window(newTab.get(0));
      MTCMController.setOverrideContext("contentFrame", "mainModalFrame");
      controller = new MTCMController();
      view = new AdminView();
      List<WebElement> businessEntityElements = view.getBusEntityFromPopup();
      String busEntities = businessEntityElements.get(0).getText();
      names = controller.selectListItemsFromPopup("confirm", 1);
      names[0] = busEntities + "-" + names[0];
      controller = new MTCMController();
      List<WebElement> elements = view.getSelectedAgent();
      for (WebElement ele : elements) {
        String s = ele.getText();
        s = s.substring(s.lastIndexOf("- ") + 2);
        Verify.verify(
            names[0].contains(s) && names[0].contains(busEntities),
            "Cannot verify the selected Agent Name on User Details page.");
      }
    } else if (action.equals("Delete")) {
      // String parent = controller.browser().getWindowHandle();
      // view.executeJavaScript("arguments[0].click();",
      // view.getButton("Add"));
      // view.sleep(2);
      // ArrayList<String> newTab = new
      // ArrayList<String>(controller.browser().getWindowHandles());
      // newTab.remove(parent);
      // view.browser().switchTo().window(newTab.get(0));
      // MTCMController.setOverrideContext(null);
      // controller = new MTCMController();
      // names = controller.selectListItemsFromPopup(parent, "confirm",
      // 1);
      // controller= new MTCMController();
      // List<WebElement> elements = view.getSelectedAgent();
      // for(WebElement ele: elements) {
      // String s = ele.getText();
      // s= s.substring(s.lastIndexOf("- ")+2);
      // Verify.verify(names[0].contains(s),"Cannot verify the selected
      // Agent Name on User Details page.");
      // }
      controller = new MTCMController();
      controller.clickCloseIconButton("close");
    }
  }

  public void getCloseBtnVisibility (String visibility) {
    view = new AdminView();
    if (view.exists(By.xpath("//i[text()='close']"))) {
      WebElement iconEle = view.getIconBtn("close");
      if (visibility.contains("not") && iconEle == null) {
        JLog.resetErrorCount();
        return;
      }
      Verify.verify(
          visibility.contains("not") && iconEle == null,
          "Close button is still displayed after deleting it.");
      return;
    }
    Verify.verify(
        !view.exists(By.xpath("//i[text()='close']")),
        "Close button is still displayed after deleting it.");
  }

  public void clickResultLinkFromRow1 (String userID) {
    view = new AdminView();
    WebElement ele = view.getResultLinkFromRow1(userID);
    view.executeJavaScript("arguments[0].click();", ele);
    JLog.write("Clicked on userID " + userID);
  }

  public void editContactDetails () throws ParseException {
    view = new AdminView();
    General gen = new General();
    gen.enterTextFieldValue("test address1", "selectedContact.addressL1");
    gen.enterTextFieldValue("test address2", "selectedContact.addressL2");
    gen.enterTextFieldValue("test address1", "selectedContact.addressL3");
    gen.enterTextFieldValue("testContact@gmail.com", "selectedContact.email");
    gen.enterTextFieldValue("test Region", "selectedContact.region");
    gen.enterTextFieldValue("560093", "selectedContact.postalCode");
  }

  public boolean getBusinessEntitiesAndVerifySorted () {
    view = new AdminView();
    List<String> entities = new ArrayList<>();
    List<WebElement> elements = view.getBusinessEntitiesAfterSearch();
    for (WebElement e : elements) {
      entities.add(e.getText());
    }
    MTCMController controller = new MTCMController();
    boolean status = controller.checkSorting(entities);
    return status;
  }

  public void deleteContactDetails () throws ParseException {
    view = new AdminView();
    General gen = new General();
    gen.enterTextFieldValue("", "selectedContact.addressL1");
    gen.enterTextFieldValue("", "selectedContact.addressL2");
    gen.enterTextFieldValue("", "selectedContact.addressL3");
    gen.enterTextFieldValue("", "selectedContact.email");
    gen.enterTextFieldValue("", "selectedContact.region");
    gen.enterTextFieldValue("", "selectedContact.postalCode");
  }

  public void verifyAddedContactDetails (String col, String expVal) {
    view = new AdminView();
    boolean status = view.getResultValues(col, expVal).isDisplayed();
    Verify.verify(status, "Unable to verify added conatct details");
  }

  public boolean getDisabledContactNameValue (String val, String page) {
    view = new AdminView();
    try {
      WebElement ele = view.get(
          By.xpath(
              "//input[@name='selected" + page + ".contact.contactName' and contains(@value,'" + val
                  + "')]"));
    } catch (Exception e) {
      if (e.toString().contains("Element not enabled")) {
        return true;
      }
    }
    return false;
  }

  public boolean getDisplayedDetails (String text) {
    view = new AdminView();
    return (view.getElement(By.xpath("//b[text()='" + text + "']")) != null) ? true : false;
  }

  public boolean isEnterpriseTextVisible (String e) {
    view = new AdminView();
    return view.getElement(By.xpath("//span//b[text()='" + e + "']")).isDisplayed();
    // !=null)? true : false;
  }

  public String getItemNumberLink () {
    view = new AdminView();
    WebElement ele = view.get(By.xpath("//a[contains(@href,'goSelectItem')]"));
    return ele.getText();
  }

  public void clickTab (String tab) {
    view = new AdminView();
    WebElement ele = view.get(By.xpath("//span[text()='" + tab + "']"));
    view.executeJavaScript("arguments[0].click();", ele);
  }

  public void verifyBusDoc (String id, String label) {
    view = new AdminView();
    WebElement ele = null;
    boolean status = true;
    try {
      ele = view.getBusDocLabel(id, label);
    } catch (Exception e) {
      if (e.toString().contains("Element not enabled")
          || e.toString().contains("NoSuchElementException")) {
        JLog.screenCapture();
        status = false;
        Verify.verify(status, "Business Document is listing " + label);
        JLog.write("Successful verified that " + label + " is not listed under Business Doc list.");
        JLog.resetErrorCount();
        return;
      }
    }
    if (ele == null) {
      status = false;
    } else {
      status = ele.isDisplayed();
    }
    Verify.verify(!status, "Business Document is listing " + label);
    JLog.resetErrorCount();
    JLog.write("Successful verified that " + label + " is not listed under Business Doc list.");

  }

  public void verifyBusDocAvailability (String id, String label) {
    view = new AdminView();
    WebElement ele = view.getBusDocLabel(id, label);
    Verify.verify(ele.isDisplayed(), "Business Document is listing " + label);
    JLog.write("Successful verified that " + label + " is not listed under Business Doc list.");

  }

  public void verifyLink (String roleID, String status) {
    view = new AdminView();
    String eleStatus = "not visible";
    String constant = "visible";
    List<WebElement> elements = view.getRolesLinks();
    for (WebElement ele : elements) {
      if (status.equals(constant)) {
        if (ele.getText().contains(roleID)) {
          eleStatus = constant;
          break;
        }
      }
      if (status.equals(constant)) {
        if (ele.getText().contains(roleID)) {
          eleStatus = constant;
          break;
        }
      }
    }
    Verify
        .verify(status.equals(eleStatus), "Role Link should be " + status + " but is " + eleStatus);
  }

  public boolean isCheckboxWitNameNValChecked (String checkboxName, String value) {
    view = new AdminView();
    WebElement element = view.getCheckBoxWitNameNValue(checkboxName, value);
    return element.isSelected();
  }

  public void selectRoleCheckBox (String roleID) {
    view = new AdminView();
    WebElement ele = view.get(
        By.xpath(
            "//a[contains(text(),'" + roleID + "')]/ancestor::td/preceding-sibling::td//input"));
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    view.executeJavaScript("arguments[0].click();", ele);
  }

  public void selectPermissionCheckBox (String selection, String name, String value) {
    view = new AdminView();
    WebElement ele = null;
    if (selection.equals("check") && (!isCheckboxWitNameNValChecked(name, value))) {
      ele = view.getCheckBoxWitNameNValue(name, value);
      view.executeJavaScript("arguments[0].click();", ele);
      JLog.write("Checkbox checked.");
    } else if (selection.equals("uncheck") && isCheckboxWitNameNValChecked(name, value)) {
      ele = view.getCheckBoxWitNameNValue(name, value);
      view.executeJavaScript("arguments[0].click();", ele);
      JLog.write("Checkbox unchecked.");
    }
  }

  public void setValue (String val, String id) {
    view = new AdminView();
    WebElement ele = view.getEleByID(id);
    ele.clear();
    ele.sendKeys(val);
    view.sleep(1);
    JLog.write("Set " + id.substring(4, id.length() - 1) + " textfield with " + val);
  }

  public boolean verifyDashBoardNewsCreated (String news, String page) {
    view = new AdminView();
    boolean status = false;
    List<WebElement> elements = null;
    if (page.equals("Dashboard")) {
      elements = view.getMyWorkspaceDisplayedNews();
    } else {
      elements = view.getDashboardsNewsValues();
    }
    for (WebElement ele : elements) {
      if (ele.getText().equals(news)) {
        JLog.write("Act value=" + ele.getText());
        status = true;
        break;
      }
    }
    return status;
  }

  public void editDashboardNewsRole (String title) {
    view = new AdminView();
    List<WebElement> elements = view.getDashboardsNewsValues();
    int rowCount = 0;
    int len = elements.size();
    for (int i = 0; i < len; i++) {
      if (elements.get(i).getText().contains(title)) {
        rowCount = i + 1;
        break;
      }
    }
    WebElement selectToEdit = view.getSelectElementToEditDel(Integer.toString(rowCount));
    view.executeJavaScript("arguments[0].click();", selectToEdit);
    view.sleep(2);
    WebElement element = view.getActionEle("edit");
    // List<WebElement> ele = view.getActionEle("edit");
    // for (WebElement e : ele) {
    // if (e.isDisplayed()) {
    // element = e;
    // break;
    // }
    // }
    view.executeJavaScript("arguments[0].click();", element);
    setComboBox("Restrict to Role", "Buyer");
    clickSaveButton();
  }

  public void selectTitleToEditOrDelete (String title, String newTitle, String action) {
    view = new AdminView();
    List<WebElement> elements = view.getDashboardsNewsValues();
    int rowCount = 0;
    int len = elements.size();
    for (int i = 0; i < len; i++) {
      JLog.write("Dashboards news title on chage dashboard news page:" + elements.get(i).getText());
      if (elements.get(i).getText().contains(title)) {
        rowCount = i + 1;
        break;
      }
    }
    JLog.write("Row on which Dashboards news title is found:" + rowCount);
    WebElement selectToEditOrDel = view.getSelectElementToEditDel(Integer.toString(rowCount));
    view.executeJavaScript("arguments[0].click();", selectToEditOrDel);
    view.sleep(2);
    JLog.screenCapture();
    WebElement element = view.getActionEle(action);
    // List<WebElement> ele = view.getActionEle(action);
    // for (WebElement e : ele) {
    // if (e.isDisplayed()) {
    // element = e;
    // break;
    // }
    // }
    view.executeJavaScript("arguments[0].click();", element);
    JLog.screenCapture();
    if (action.equals("edit")) {
      setValue(newTitle, "alertTitle");
      clickSaveButton();
    }
  }
}
