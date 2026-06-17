/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.steps.General;
import com.test.selenium.scplatform.steps.HarmonyLoginUI;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.google.common.base.Verify;

public class SearchController extends MTCMController {

  SearchView view;
  HarmonyLoginUI ui = new HarmonyLoginUI();
  Prop prop = Prop.getInstance();
  String parentForLinkDetails;

  @Override
  public PageImpl getView () {
    view = new SearchView();
    return view;
  }

  public void assignRoleAndResp (String existingRole) {
    view = new SearchView();
    WebElement ele = view.getRowForResp(existingRole);
    view.executeJavaScript("arguments[0].click();", ele);
  }

  public void verifyItemAVLDetails (String item) throws ParseException {
    view = new SearchView();
    List<WebElement> ele = view.getItemNumbersList();
    for (WebElement e : ele) {
      String s = e.getText();
      if (s.equals("")) {
        s = e.getAttribute("innerText");
      }
      Verify.verify(e.getText().contains(item), "Unable to verify Item Number on serach results");
    }
    JLog.write("Successfully verified item AVL number on serach results");
    // WebElement e = view.getTabItemDetails(1, 16);
    // String s = e.getText();
    // if (s.equals("")) {
    // s = e.getAttribute("innerText");
    // }
    General gen = new General();
    gen.isCoulmnValueVisible("Functional Groups", "CFG");
    // Verify.verify(s.contains("CFG"), "unable to verify CFG Name");
    JLog.write("Successfully verified Item AVL Details");
  }

  public void getAndSaveItemDetailsFromSearchFilterGrid () throws FileNotFoundException {
    Properties p = new Properties();
    FileOutputStream fr =
        new FileOutputStream(prop.getRootDir() + "scplatform/data/properties/itemDetails.properties");
    // p.setProperty("ItemNumber", item);
    int columnNum = 0;
    String s = "";
    List<WebElement> columns = view.getHeaderColumns();
    for (int i = 0; i < columns.size(); i++) {
      // s= columns.get(i).getAttribute("innerText");
      if (columns.get(i).getText().equals("Item Type")) {
        columnNum = i + 1;
        break;
      }
    }
    p.setProperty("ItemType", getItemDetails(1, columnNum)); // ItemType
    columnNum = 0;
    for (int i = 0; i < columns.size(); i++) {
      if (columns.get(i).getText().equals("Item Description")) {
        columnNum = i + 1;
        break;
      }
    }
    p.setProperty("ItemDescp", getItemDetails(1, columnNum)); // ItemDescp
    columnNum = 0;
    for (int i = 0; i < columns.size(); i++) {
      if (columns.get(i).getText().equals("Item Business")) {
        columnNum = i + 1;
        break;
      }
    }
    p.setProperty("ItemBusiness", getItemDetails(1, columnNum)); // Item
                                                                 // Business

    view.executeJavaScript(
        "arguments[0].scrollIntoView(true);",
        getItemDetails(1, columns.size() - 10));
    columnNum = 0;
    for (int i = 0; i < columns.size(); i++) {
      s = columns.get(i).getText();
      if (s.equals("")) {
        s = columns.get(i).getAttribute("innerText");
      }
      if (s.equals("")) {
        s = columns.get(i).getAttribute("innerHTML");
      }
      if (columns.get(i).getText().contains("Classification")) {
        columnNum = i + 1;
        break;
      }
    }
    if (columnNum != 0)
      p.setProperty("Classification", getItemDetails(1, columnNum)); //
    // classification

    columnNum = 0;
    for (int i = 0; i < columns.size(); i++) {
      s = columns.get(i).getText();
      if (s.equals("")) {
        s = columns.get(i).getAttribute("innerText");
      }
      if (s.equals("Responsibility")) {
        columnNum = i + 1;
        break;
      }
    }
    p.setProperty("Responsibility", getItemDetails(1, columnNum)); // Responsibility

    view.executeJavaScript(
        "arguments[0].scrollIntoView(true);",
        getItemDetails(1, columns.size() - 3));

    columnNum = 0;
    for (int i = 0; i < columns.size(); i++) {
      s = columns.get(i).getText();
      if (s.equals("")) {
        s = columns.get(i).getAttribute("innerText");
      }
      if (columns.get(i).getText().equals("Source System")) {
        columnNum = i + 1;
        break;
      }
    }
    p.setProperty("sourceSystem", getItemDetails(1, columnNum)); // Source
                                                                 // System

    p.save(fr, "");
    JLog.write(p.toString());
  }

  public void assignToSelfSelection (String action) throws Throwable {
    view = new SearchView();
    WebElement ele = view.getEleByID("BD_ITEM_ASSIGNMENT_AssignSelf");
    boolean status = ele.isSelected();
    if (action.equals("check") && (!status)) {
      view.executeJavaScript("arguments[0].click();", ele);
      JLog.write("Checkbox checked.");
    } else if (action.equals("uncheck") && status) {
      view.executeJavaScript("arguments[0].click();", ele);
      JLog.write("Checkbox unchecked.");
    }
  }

  public void unAssignOtherUsers (String action) throws Throwable {
    view = new SearchView();
    WebElement ele = view.getEleByID("BD_ITEM_ASSIGNMENT_UnassignOthers");
    boolean status = ele.isSelected();
    if (action.equals("check") && (!status)) {
      view.executeJavaScript("arguments[0].click();", ele);
      JLog.write("Checkbox checked.");
    } else if (action.equals("uncheck") && status) {
      view.executeJavaScript("arguments[0].click();", ele);
      JLog.write("Checkbox unchecked.");
    }
  }

  public void setDropDownValue (String drpDn, String value) {
    view = new SearchView();
    WebElement ele = view.getElement(
        By.xpath(
            "//label[text()='" + drpDn + "']/parent::div//select[@class='eto-select__field']"));
    Select select = new Select(ele);
    select.selectByIndex(2);
    JLog.write("Set dropdown " + drpDn + " with value " + value);
  }


  public void verifySearchItem (String action) throws Throwable {
    JLog.screenCapture();
    UploadController uC = new UploadController();
    MTCMController c = new MTCMController();
    String item;
    String subMenu;
    if (action.contains("ItemAVL")) {
      item = "itemAVL" + uC.getTimeStamp();
      subMenu = "Item AVL";
    } else {
      subMenu = "Items";
      item = "item" + uC.getTimeStamp();
    }
    General g = new General();
    ui.navHarmonyMTCM("Search", subMenu);
    g.waitTillPageLoads("20");
    c = new MTCMController();
    c.clickButton("Clear");
    g.enterTextFieldVal(item, "itemNumber");
    c.clickButton("Apply");
    g.waitTillPageLoads("15");
    g.verifySearchFilterResults();
    clickItemSearchRes(item);
    // g.clickPopupBtn("Ok");
    JLog.write("Succcessfully verified item " + item + " on Search Items page");
  }

  public void clickItemSearchResult (String linkText) throws FileNotFoundException {
    MTCMController controller = new MTCMController();
    parentForLinkDetails = controller.browser().getWindowHandle();
    controller.clickName(linkText);
    ArrayList<String> newTab = new ArrayList<>(controller.browser().getWindowHandles());
    newTab.remove(parentForLinkDetails);
    controller.browser().switchTo().window(newTab.get(0));
    MTCMController.setOverrideContext("null");
    controller = new MTCMController();
    JLog.write("Succesfully launced popup with Item details.");
  }

  public void clickItemSearchRes (String linkText) throws FileNotFoundException {
    MTCMController controller = new MTCMController();
    // parentForLinkDetails = controller.browser().getWindowHandle();
    controller.clickName(linkText);
    // ArrayList<String> newTab = new
    // ArrayList<String>(controller.browser().getWindowHandles());
    // newTab.remove(parentForLinkDetails);
    // controller.browser().switchTo().window(newTab.get(0));
    // MTCMController.setOverrideContext("null");
    controller = new MTCMController();
    controller.isVisible("Item Id");
    JLog.write("Succesfully launced popup with Item details.");
  }

  public void verifyItemResponsibility (String expResp, String action, String item) {
    String actResp = getItemDetails(1, 7);
    JLog.write("ActResp on UI=" + actResp);
    JLog.write("Expected resp =" + expResp);
    JLog.screenCapture();
    Verify.verify(
        actResp.contains(expResp),
        "Unable to verify " + expResp + action + " ed to the item" + item);
  }

  public void verifyItemResponsibilityon2Rows (String expResp, String action, String item) {
    String actResp = "";
    view = new SearchView();
    JLog.write("Expected Resp = " + expResp);
    List<WebElement> elements = view.getList(By.xpath("//tr//td[7]"));
    for (int i = 0; i < 2; i++) {
      actResp = elements.get(i).getText();
      if (actResp.equals("")) {
        actResp = elements.get(i).getAttribute("innerText");
      }
      JLog.write("Actual Resp = " + actResp);
      Verify.verify(
          actResp.contains(expResp),
          "Unable to verify " + expResp + action + " ed to the item" + item);
    }

    Verify.verify(
        actResp.contains(expResp),
        "Unable to verify " + expResp + action + " ed to the item" + item);
  }

  public void verifyItemResponsibilityonAnyRow (String expResp, String action, String item) {
    String actResp = "";
    view = new SearchView();
    JLog.write("Expected Resp = " + expResp);
    List<WebElement> elements = view.getList(By.xpath("//tr//td[7]"));
    boolean status = false;
    for (int i = 0; i < elements.size(); i++) {
      actResp = elements.get(i).getText();
      if (actResp.equals("")) {
        actResp = elements.get(i).getAttribute("innerText");
      }
      JLog.write("Expected Resp = " + actResp);
      // Verify.verify(actResp.contains(expResp), "Unable to verify " +
      // expResp + action + " ed to the item" + item);
      if (actResp.contains(expResp)) {
        status = true;
        JLog.write("Successfully verified item assignment.");
        return;
      }
    }

    Verify.verify(status, "Unable to verify " + expResp + action + " ed to the item" + item);
  }

  public void verifyItemResponsibilityonAllRows (String expResp, String action, String item) {
    String actResp = "";
    view = new SearchView();
    List<WebElement> elements = view.getList(By.xpath("//tr//td[7]"));
    for (WebElement e : elements) {
      actResp = e.getText();
      if (actResp.equals("")) {
        actResp = e.getAttribute("innerText");
      }
      Verify.verify(
          actResp.contains(expResp),
          "Unable to verify " + expResp + action + " ed to the item" + item);
    }

    Verify.verify(
        actResp.contains(expResp),
        "Unable to verify " + expResp + action + " ed to the item" + item);
  }

  public void verifyCommodityResponsibility (String assignedTo, String resp, String commodity) {
    MTCMController ctrller = new MTCMController();
    String actResp = ctrller.getItemBusinessDetails(3);
    Verify.verify(
        actResp.contains(assignedTo),
        "Unable to verify value assigned to the Commodity-" + commodity);
    actResp = getItemDetails(1, 4);
    Verify.verify(
        actResp == null || actResp.contains(resp),
        "Unable to verify responsibility assigned to the Commodity-" + commodity);
  }

  public String getItemManagedByData (int rowCount, int col, String val) {
    view = new SearchView();
    WebElement element = null;
    if (val.equals("DELL"))
      element = view.getItemManagedByDetails(rowCount, col, val);
    else
      element = view.getTabItemDetails(rowCount, col);
    view.executeJavaScript("arguments[0].scrollIntoView(true);", element);
    AbstractPage.sleep(3);
    String s = element.getText();
    if (s.equals("")) {
      s = element.getAttribute("innerHTML");
    }
    AbstractPage.sleep(1);
    return s;
  }

  public void setDeselectVerifyManagedTo (String expSetManagedTo, String commodity) {
    boolean status = false;
    WebElement ele = null;
    By by = By.xpath("//td[contains(text(),'" + expSetManagedTo + "')]");
    if (expSetManagedTo.equals("EM")) {
      status = view.get(by).isDisplayed();
      Verify.verify(status, "Set Managed is not set to EM");
      return;
    } else if (expSetManagedTo.equals("")) {
      ele = view.get(
          By.xpath(
              "//th[@data-column='Commodity Name']//ancestor::table//tbody//tr//td[2][contains(text(),'')]"));
      status = ele.isDisplayed();
      Verify.verify(status, "Set Managed is not reset to null.");
      return;
    }
    String actualSetManaged = getItemManagedByData(1, 2, expSetManagedTo);
    JLog.write("Exp value= " + expSetManagedTo);
    JLog.write("Act value= " + actualSetManaged);
    // if (expSetManagedTo.equals(""))
    // actualSetManaged = "";
    Verify.verify(
        actualSetManaged.contains(expSetManagedTo),
        "Unable to verify value assigned to the Commodity-" + commodity);
  }
}
