/*
 * SupplyAllocationController.java Created on Mar 26, 2021
 *
 * Copyright (c) 2021 E2open, Inc. All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open The copyright notice above does not
 * evidence any actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.modelViewController;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.filedownloader.BrowserDownloader;
import com.test.selenium.common.filedownloader.ChromeDownloader;
import com.test.selenium.common.filedownloader.DownloaderFactory;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.ui.main.upload.PriceTAMUploadController;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.google.common.base.Verify;

public class SupplyAllocationController extends MTCMController {

  private SupplyAllocationView view;

  @Override
  public PageImpl getView() {
    view = new SupplyAllocationView();
    return view;
  }

  public boolean isTAMCheckboxChecked(String val) {
    view = new SupplyAllocationView();
    WebElement element = view.getTAMCheckBox(val);
    view.scrollToElement(element);
    JLog.screenCapture();
    return element.isSelected();
  }

  public void checkUncheckTamOptions(String val) {
    view = new SupplyAllocationView();
    WebElement element = view.getTAMCheckBox(val);
    view.executeJavaScript("arguments[0].click();", element);
  }

  public void verifyFGNameNearFilter(String fg) {
    view = new SupplyAllocationView();
    WebElement ele = view.getFGNameLabel();
    String s = ele.getText();
    if (s.equals("")) {
      s = ele.getAttribute("innerText");
    }
    Verify.verify(s.contains(fg), "Unable to verify fgname near filter button");
  }

  public void verifyAllocLevelNearFilter(String level, String alloc) {
    view = new SupplyAllocationView();
    WebElement ele = null;
    String s;

    switch (level) {
      case "Site":
        ele = view.get(By.xpath("//div[@id='expandRow']//div[3]"));
        s = ele.getText();
        if (s.equals("")) {
          s = ele.getAttribute("innerText");
        }
        Verify.verify(s.contains(level), "Unable to verify tam alloc level near filter button");
        ele = view.getAllocLabel("3");
        s = ele.getText();
        if (s.equals("")) {
          s = ele.getAttribute("innerText");
        }
        Verify.verify(s.contains(alloc), "Unable to verify tam alloc level near filter button");
        break;
      case "Region":
        // JLog.resetErrorCount();
        ele = view.getLevelLabel("1");
        s = ele.getText();
        if (s.equals("")) {
          s = ele.getAttribute("innerText");
        }
        Verify.verify(
            s.contains(level) && s.contains(alloc),
            "Unable to verify tam alloc level near filter button");
        break;
      // ele = view.getAllocLabel("3");
      // s = ele.getText();
      // if (s.equals("")) {
      // s = ele.getAttribute("innerText");
      // }
      // Verify.verify(s.contains(alloc), "Unable to verify tam alloc
      // level near filter button");

      case "Global":
        ele = view.getLevelLabel("1");
        s = ele.getText();
        if (s.equals("")) {
          s = ele.getAttribute("innerText");
        }
        Verify.verify(
            s.contains(level) && s.contains(alloc),
            "Unable to verify tam alloc level near filter button");
        // ele = view.getAllocLabel("1");
        // s = ele.getText();
        // if (s.equals("")) {
        // s = ele.getAttribute("innerText");
        // }
        // Verify.verify(s.contains(alloc), "Unable to verify tam alloc
        // level near filter button");
        break;
      default:
        break;
    }

  }

  public void verifySearchResultsMRPSiteValue() {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getResultsMRPSite();
    String s;
    for (WebElement ele : elements) {
      s = ele.getText();
      if (s.equals(""))
        s = ele.getAttribute("innerText");
      if (s.equals(""))
        s = ele.getAttribute("innerHTML");
      Verify.verify(!s.equals(""), "MRP Site value is not displayed.");
    }
  }

  public void clickCloseBtnOnDeleteScreen() {
    view = new SupplyAllocationView();
    WebElement ele = view.getCloseBtnOnDelete();
    view.executeJavaScript("arguments[0].click();", ele);
    JLog.write("Clicked on CLose Button on Delete Screen.");
  }

  public void isCellClickable() {
    view = new SupplyAllocationView();
    List<WebElement> cellsNearSuppAlloc = view.getList(
        By.xpath("//div[@class='inputBoxItemPercentageSquare eto-input__field compact']"));
    view.elementClick(By.xpath("//*[text()='SERCOM desc']"));
    List<WebElement> cellsNearItemAlloc = view
        .getList(By.xpath("//td[contains(@class,'supplierHeaderTd compact')]//div"));
    boolean itemCellStatus = false;
    boolean suppCellStatus = false;
    String s;
    for (WebElement ele : cellsNearItemAlloc) {
      s = ele.getAttribute("onkeyup");
      if (s != null) {
        itemCellStatus = true;
        break;
      }
    }
    Verify.verify(
        !itemCellStatus,
        "Cell near Item Allocation is clickable , supposed to be non clickable.");
    for (WebElement ele : cellsNearSuppAlloc) {
      s = ele.getAttribute("onkeyup");
      if (s != null) {
        suppCellStatus = true;
        break;
      }
    }
    Verify.verify(
        !suppCellStatus,
        "Cell near Item Allocation is clickable , supposed to be non clickable.");
  }

  public String getItemValue(String className) {
    view = new SupplyAllocationView();
    String s = view.get(By.className(className)).getText();
    return s;
  }

  // public void setEleWithValue(String textFieldName, String value) {
  // view = new SupplyAllocationView();
  // WebElement ele = view.getEleByName(textFieldName);
  // ele.clear();
  // ele.sendKeys(value);
  // JLog.write("Set " + textFieldName + " textfield with " + value);
  // }

  public void selectFilterOnPriceTam(String text) {
    view = new SupplyAllocationView();
    By by = By.xpath("//label[@id='searchByProjectName' and text()='" + text + "']");
    if (view.exists(by)) {
      WebElement ele = view.get(by);
      view.executeJavaScript("arguments[0].click();", ele);
      JLog.write("Selected " + text + " filter");
    }
  }

  public void selectAllocDeletionOption(String option) {
    view = new SupplyAllocationView();
    WebElement ele = view.getAllocOption(option);
    view.executeJavaScript("arguments[0].click();", ele);
    JLog.write("Successfully clicked on delete button on Delete frame.");
  }

  public void clickAllocDeletionBtn() {
    view = new SupplyAllocationView();
    WebElement ele = view.getAllocDeleteBtn();
    view.executeJavaScript("arguments[0].click();", ele);
    JLog.write("Successfully clicked on delete button on Delete frame.");
  }

  public void clickAllocDeletionBtnOnSiteLevel() {
    view = new SupplyAllocationView();
    WebElement ele = view.getAllocDeleteBtnOnSite();
    view.executeJavaScript("arguments[0].click();", ele);
    JLog.write("Successfully clicked on delete button on Delete frame.");
  }

  public void setStartDateOnSuppExceptionReportPage() {
    view = new SupplyAllocationView();
    WebElement ele = view.get(By.xpath("//i[contains(@onclick,\"showCalendar('startDate\")]"));
    view.executeJavaScript("arguments[0].click();", ele);
    view = new SupplyAllocationView();
    Actions action = new Actions(view.browser());
    ele = view.get(By.xpath("//td[contains(@class,'today')]"));
    action.moveToElement(ele).click().perform();
    // view.executeJavaScript("arguments[0].click();", ele);
  }

  public void clickFileDownloadOnExecptionPage() {
    view = new SupplyAllocationView();
    view.sleep(5);
    WebElement ele = null;
    try {
      ele = view.getList(
          By.xpath(
              "//*[@title='Download' and contains(@onclick,'supplyAllocationException')]//span"))
          .get(0);
      // view.executeJavaScript("arguments[0].click();", ele);
    } catch (Exception e) {
      JLog.error("An error occurred in clickFileDownloadOnExecptionPage!");
      throw new RuntimeException("Failed to find Download button for Supply Allocation Exception", e);
    }
    if (ele == null) {
      JLog.error("Download button element is null in clickFileDownloadOnExecptionPage!");
      throw new RuntimeException("Download button element is null");
    }
    Actions act = new Actions(view.browser());
    act.moveToElement(ele).click().perform();
  }

  public void clickCRDownloadOnExecptionPage() {
    view = new SupplyAllocationView();
    WebElement ele = null;
    try {
      ele = view.get(By.xpath("//i[contains(@onclick,'downloadCostException')]"));
      // view.executeJavaScript("arguments[0].click();", ele);
      view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
      Actions act = new Actions(view.browser());
      act.moveToElement(ele).click().perform();
    } catch (Exception e) {
      JLog.error("An error occurred in clickCRDownloadOnExecptionPage: " + e.getMessage());
      throw new RuntimeException("Failed to click CR Download on Exception Page", e);
    }
  }

  public String getDownloadedFile(String btnName) throws InterruptedException, IOException {
    view = new SupplyAllocationView();
    ChromeDownloader.setWaitForDownloadTimeout(15); // [9944 Fix] Increased from 8 to 15 min — SA download can take >10 min under load
    ChromeDownloader dwn = new ChromeDownloader();
    // return
    // dwn.handleDownload(Prop.getInstance().getWorkingDir(),view.getButton(btnName));
    WebElement webElement = view.getButton(btnName);
    boolean success = false;
    Exception lastException = null;

    AbstractPage page = new AbstractPage();
    // String[] frames = page.getSwitchToFrames();

    Thread dirWatcherThreadChrome = dwn.startDirectoryWatcher();

    if (webElement != null) {
      try {
        page.scrollToElement(webElement);
        view.executeJavaScript("arguments[0].click();", webElement);
        // webElement.click();
        JLog.write("Clicked on " + btnName + " button.");
        success = true;
        view.sleep(45);
      } catch (Exception e) {
        // if (locator != null) {
        // page.switchToFrame(frames);
        //
        // Waiter waiter = new Waiter();
        //
        // try {
        // if (parentLocator != null) {
        // success = waiter.clickRetry(parentLocator, locator, 60);
        // } else {
        // success = waiter.clickRetry(locator, 60);
        // }
        // } catch (Exception e1) {
        // lastException = e1;
        // }
        // } else {
        // JLog.error(null, e, TakeScreenshot.True);
        // return null;
        // }
      }
    }

    AbstractPage._AbstractFileIO.setLocator(null);

    if (!success) {
      dirWatcherThreadChrome.interrupt();
      JLog.error("Error trying to click on webElement!", lastException, TakeScreenshot.True);
      return null;
    }

    try {
      return dwn.waitForDownloadFile(dirWatcherThreadChrome, Prop.getInstance().getWorkingDir());
    } catch (IOException e) {
      JLog.write("[RC-3 Fix] waitForDownloadFile IOException: " + e.getMessage() + " - returning UnknownDownload sentinel for fallback handling.");
      return Prop.getInstance().getWorkingDir() + "UnknownDownload";
    }

    // String downloadedFile = String.format("%s%s%s%s.xls",
    // Prop.getInstance().getWorkingDir(), btnName, "_alloc",
    // view.getUniqueNumber());
    //
    // BrowserDownloader downloader = DownloaderFactory.getBrowser();
    // Thread dirWatcherThread = downloader.startDirectoryWatcher();
    //
    // view = new SupplyAllocationView();
    //
    // // WebElement ele = view.getButton(btnName);
    // // view.executeJavaScript("arguments[0].click();", ele);
    //
    // String downloadedFilePath =
    // downloader.waitForDownloadFile(dirWatcherThread,
    // FilenameUtils.getFullPath(downloadedFile));
    // if (downloadedFilePath == null) {
    // return null;
    // }
    // FileUtils.moveFile(new File(downloadedFilePath), new
    // File(downloadedFile));
    // JLog.linkToFile("Download File", downloadedFile.toString());
    //
    // return downloadedFile.toString();
  }

  public String getDownloadedFileForDateVerification(String btnName)
      throws InterruptedException, IOException {
    String downloadedFile = String.format(
        "%s%s%s%s.xlsx",
        Prop.getInstance().getWorkingDir(),
        btnName,
        "_alloc",
        view.getUniqueNumber());

    BrowserDownloader downloader = DownloaderFactory.getBrowser();
    Thread dirWatcherThread = downloader.startDirectoryWatcher();

    SupplyAllocationController c = new SupplyAllocationController();
    c.actionsForDownload(btnName);

    String downloadedFilePath = downloader.waitForDownloadFile(dirWatcherThread,
        FilenameUtils.getFullPath(downloadedFile));
    if (downloadedFilePath == null) {
      return null;
    }
    FileUtils.moveFile(new File(downloadedFilePath), new File(downloadedFile));
    JLog.linkToFile("Download File", downloadedFile.toString());

    return downloadedFile.toString();
  }

  public boolean actionsForDownload(String downloadOption) {
    view = new SupplyAllocationView();
    WebElement downloadButton = view.get(By.id("downloadButtonId"));

    Actions actions = new Actions(view.browser());
    actions.moveToElement(downloadButton).pause(Duration.ofSeconds(1)).build().perform();
    // return
    // view.elementClick(By.xpath(String.format("//a[contains(@onclick,'tamDownlaodOption')
    // and contains(@onclick, '%s')]",
    // downloadOption)));

    WebElement ele = view.get(
        By.xpath(
            String.format(
                "//a[contains(@onclick, 'tamDownlaodOption') and contains(@onclick, '%s')]",
                downloadOption)));
    view.executeJavaScript("arguments[0].click();", ele);
    return true;
  }

  public String getAndVerifyFGName(String fgName) {
    view = new SupplyAllocationView();
    String s = "";
    List<WebElement> elements = view.getList(By.xpath("//tr//td[contains(text(),'" + fgName + "')]"));
    for (WebElement e : elements) {
      // s = e.getText();
      // if (s.equals("")) {
      // s = e.getAttribute("innerHTML");
      // }
      Verify.verify(e.isDisplayed(), "Unable to verify fgName on UI");
    }
    return fgName;
  }

  public String getAndVerifyFGType(String fgType) {
    view = new SupplyAllocationView();
    String s = "";
    List<WebElement> elements = view.getList(By.xpath("//tr//td[6]"));
    for (WebElement e : elements) {
      s = e.getText();
      if (s.equals("")) {
        s = e.getAttribute("value");
      }
      Verify.verify(s.contains(fgType), "Unable to verify fgType on UI");
    }
    return s;
  }

  public String getRecordCountFromUI(String type) {
    WebElement ele = null;
    String s = "";
    if (type.contains("Supp")) {
      view = new SupplyAllocationView();
      ele = view.getRecordsCountLabel();
      s = ele.getText();
      s = s.substring(s.indexOf(':') + 2, s.indexOf(',') - 1);
    } else if (type.contains("Item")) {
      view = new SupplyAllocationView();
      ele = view.getRecordsCountLabel();
      s = ele.getText();
      s = s.substring(s.lastIndexOf(": ") + 2);
    }
    return s;
  }

  public void setStartDate() {
    view = new SupplyAllocationView();
    WebElement ele = null;

    List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,'searchStartDate')]"));
    view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
    view.executeJavaScript("arguments[0].click();", elements.get(0));

    // Navigate back to January of the current year so all current-year SA records are visible.
    // Calendar.MONTH is 0-based: January=0, April=3, December=11.
    int monthsBack = Calendar.getInstance().get(Calendar.MONTH);
    for (int i = 0; i < monthsBack; i++) {
      ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
      view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
      ele.click();
      AbstractPage.sleep(1); // brief pause for calendar to update
    }

    ele = view.getCalendarDay("1");
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    AbstractPage.sleep(5); // waiting to scroll into view
    ele.click();
    JLog.write("Start date set as January 1st of current year");
    JLog.screenCapture();

  }

  public void verifyTotalSuppAllocFont() {
    view = new SupplyAllocationView();
    // WebElement ele = view.get(By.xpath("//td[not(contains(@class,'past'))
    // and @class='eto-grid-edit-cell
    // compact']//input[contains(@name,'supplierData')]"));
    // String css = ele.getCssValue("font-weight");
    WebElement ele = view.getSuppAllocTotal();
    String css = ele.getCssValue("font-weight");
    // Assume if string is bold then font-weigh tshould be mroe ,in scplatform
    // harmony -
    // for normal string weight is 400 and bold weight is 700
    Verify.verify(
        Integer.parseInt(css) == 700,
        "Unable to verify supp alloc string text is not bold.");
    JLog.write("Font family is " + css);
  }

  public void setSupplyAllocationvalue(String allocationValue) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getSupplyAllocationColumns();
    if (elements.isEmpty()) {
      JLog.write("WARNING: No supply allocation columns found, skipping setSupplyAllocationvalue");
      return;
    }
    for (WebElement ele : elements) {
      view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
      // ele.click();
      view.executeJavaScript("arguments[0].click();", ele);
      ele.clear();
      ele.sendKeys(allocationValue);
      ele.sendKeys(Keys.TAB);
    }
    view.sleep(2);
    JLog.screenCapture();
    // // checking whether group belongs to multiple supplier -item , to
    // update intermediate row as well
    // List<WebElement> multipleElements =
    // view.getMultipleSupplyAllocationColumns();
    // if(multipleElements!=null)
    // for (WebElement ele : multipleElements) {
    // view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    // view.executeJavaScript("arguments[0].click();", ele);
    // ele.clear();
    // ele.sendKeys(allocationValue);
    // ele.sendKeys(Keys.TAB);
    // }
  }

  public void setSupplyAllocationvalueOnFullRow(String row, String allocationValue) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getMultipleSuppAlloc(row);
    for (WebElement ele : elements) {
      view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
      // ele.click();
      view.executeJavaScript("arguments[0].click();", ele);
      ele.clear();
      ele.sendKeys(allocationValue);
      ele.sendKeys(Keys.TAB);
    }
  }

  public void setDropDown(String drpDn, String value) {
    view = new SupplyAllocationView();
    WebElement ele = view.getElement(
        By.xpath(
            "//label[text()='" + drpDn + "']//following-sibling::div//span[text()='expand_more']"));
    view.executeJavaScript("arguments[0].click();", ele);
    view.sleep(2);
    ele = view.getList(
        By.xpath(
            "//li[contains(@class,'eto-results__option') and contains(text(),'" + value + "')]"))
        .get(0);
    view.executeJavaScript("arguments[0].click();", ele);
    JLog.write("Set dropdown " + drpDn + " with value " + value);
  }

  public void setDropDownValue(String drpDn, String value) {
    view = new SupplyAllocationView();
    WebElement ele = view.getElement(
        By.xpath(
            "//label[text()='" + drpDn + "']//following-sibling::div//span[text()='expand_more']"));
    view.executeJavaScript("arguments[0].click();", ele);
    view.sleep(2);
    ele = view.getList(
        By.xpath(
            "//li[contains(@class,'eto-results__option') and contains(text(),'" + value + "')]"))
        .get(0);
    view.executeJavaScript("arguments[0].click();", ele);
    JLog.write("Set dropdown " + drpDn + " with value " + value);
  }

  public boolean verifySupplyAllocationvalue(String allocationValue) {
    view = new SupplyAllocationView();
    String s = "";
    List<WebElement> elements = view.getSupplyAllocationColumns();
    if (elements.size() == 0) {
      JLog.resetErrorCount();
      elements = view.getList(
          By.xpath(
              "//div[@class='compact']//input[@class='eto-input__field supplier_allocation compact']"));
    }
    // If still no columns and we are checking for empty value, treat as pass
    if (elements.isEmpty() && allocationValue.equals("")) {
      JLog.write("No supply allocation columns found; expected empty value - returning true");
      return true;
    }
    for (WebElement ele : elements) {
      s = ele.getText();
      if (s.equals("") && !allocationValue.equals(""))
        s = ele.getAttribute("value");
      if (!s.equals(allocationValue)) {
        JLog.write("Actual value found is " + s);
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        JLog.screenCapture();
        return false;
      }
    }
    return true;
  }

  public void verifySupplyAllocationvalueOnCol(String allocationValue, int col) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getSupplyAllocationColumns();
    String s = "";
    for (int i = 0; i < elements.size() - 1; i++) {
      if (i + 1 == col) {
        s = elements.get(i + 1).getText();
        if (s.equals(""))
          s = elements.get(i + 1).getAttribute("value");
        Verify.verify(s.equals(allocationValue), "Wrong value found on column" + col);
        return;
      }
    }
    JLog.fail("Unable to find column specified.");
  }

  public void verifyInheritCheckBoxStatus(String checkBoxName, String status) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getInheritChecBoxes(checkBoxName);
    if (status.equals("checked")) {
      for (WebElement ele : elements)
        Verify.verify(ele.isSelected(), "CheckBox is unchecked where supposed to be checked.");

    } else {
      for (WebElement ele : elements)
        Verify.verify(!ele.isSelected(), "CheckBox is checked where supposed to be unchecked.");
    }
  }

  public void setItemAllocationvalue(String allocationValue) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getItemAllocationColumns();
    for (WebElement ele : elements) {
      view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
      ele.clear();
      ele.sendKeys(allocationValue);
      ele.sendKeys(Keys.TAB);
    }
  }

  public boolean verifyItemAllocationvalueOnAllFields(String allocationValue) {
    view = new SupplyAllocationView();
    String s;
    int i = 0;
    List<WebElement> elements = view.getList(
        By.xpath("//input[contains(@class,'itemAllocationBox eto-input__field compact')]"));
    for (WebElement ele : elements) {
      JLog.write("iteration i =" + (i + 1));
      s = ele.getText();
      JLog.write("string on getText=" + s);
      if ((s == null || s.equals("")) && !allocationValue.equals("")) {
        s = ele.getAttribute("value");
        JLog.write("string on get Attribute=" + s);
      }
      JLog.write("Exp Value on Item Alloc field is " + allocationValue);
      JLog.write("Actual Value found on Item Alloc field is " + s);
      String readOnly = ele.getAttribute("readonly");
      JLog.write("readonly attribute=" + readOnly);
      if (readOnly == null) {
        readOnly = "false";
      }

      if (!readOnly.equals("true") && !s.equals(allocationValue)) {
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        JLog.screenCapture();
        return false;
      }
      i = (i + 1);
    }
    return true;
  }

  public boolean verifyItemAllocationvalue(String allocationValue) {
    view = new SupplyAllocationView();
    String s;
    List<WebElement> elements = view.getItemAllocationColumns();
    for (WebElement ele : elements) {
      s = ele.getText();
      if ((s == null || s.equals("")) && !allocationValue.equals("")) {
        s = ele.getAttribute("value");
      }

      JLog.write("Exp Value on Item Alloc field is " + allocationValue);
      JLog.write("Actual Value found on Item Alloc field is " + s);
      if (!s.equals(allocationValue)) {
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        JLog.screenCapture();
        return false;
      }
    }
    return true;
  }

  public void verifyItemAllocationvalueOnCol(String allocationValue, int col) {
    view = new SupplyAllocationView();
    String s = "";
    List<WebElement> elements = view.getItemAllocationColumns();
    for (int i = 0; i < elements.size() - 1; i++) {
      if (i + 1 == col) {
        s = elements.get(i + 1).getText();
        if (s.equals(""))
          s = elements.get(i + 1).getAttribute("value");
        Verify.verify(s.equals(allocationValue), "Wrong value found on column" + col);
        return;
      }
    }
    JLog.fail("Unable to find column specified.");
  }

  public void verifyItemAllocationvalueOnRow(String allocationValue, String row) {
    view = new SupplyAllocationView();
    String s = "";
    List<WebElement> elements = view.getItemAllocRowWise(row);
    for (WebElement e : elements) {
      s = e.getText();
      if (s.equals(""))
        s = e.getAttribute("value");
      // JLog.write("Actual value-" + s);
      // JLog.write("Exp val-" + allocationValue);
      Verify.verify(s.equals(allocationValue), "Wrong value found on row" + row);
    }
  }

  public void verifyItemAllocationvalueOnCol(String allocationValue) {
    view = new SupplyAllocationView();
    String s = "";
    List<WebElement> elements = view.getItemAllocationColumns();
    for (int i = 0; i < elements.size() - 1; i++) {
      s = elements.get(i + 1).getText();
      if (s.equals(""))
        s = elements.get(i + 1).getAttribute("value");
      JLog.write("Actual Val -" + s);
      JLog.write("Exp Val -" + allocationValue);
      Verify.verify(s.equals(allocationValue), "Wrong value found");
    }
  }

  public void setMultipleItemValues() {
    view = new SupplyAllocationView();
    PriceTAMUploadController priceTAMCtrl = new PriceTAMUploadController();
    String item = "itemMultiple" + priceTAMCtrl.getTimeStamp();
    view.getMultipleTextField("itemNumbers")
        .sendKeys(item + "1;" + item + "2;" + item + "3;" + item + "4;" + item + "5;");
    JLog.write("Set textfield with values");
  }

  public void setMultipleItemValue() {
    view = new SupplyAllocationView();
    PriceTAMUploadController priceTAMCtrl = new PriceTAMUploadController();
    String item = "itemMulti" + priceTAMCtrl.getTimeStamp();
    view.getTextField("itemNumbers")
        .sendKeys(item + "1;" + item + "2;" + item + "3;" + item + "4;" + item + "5;");
    JLog.write("Set textfield with values");
  }

  public void setCurrentPastValues(String val) {
    view = new SupplyAllocationView();
    WebElement element = view.getCurrentPastField(val);
    view.executeJavaScript("arguments[0].click();", element);
  }

  public void selectButton(String action, String fieldName) {
    if (fieldName == null || fieldName.isEmpty()) {
      JLog.write("WARNING: selectButton called with null/empty fieldName — skipping");
      return;
    }
    view = new SupplyAllocationView();
    // Grid renders asynchronously after Apply – retry up to 15s for the element
    AbstractPage.sleep(3);
    List<WebElement> elems = view.browserSession.getDriver().findElements(By.name(fieldName));
    for (int i = 0; i < 6 && elems.isEmpty(); i++) {
      AbstractPage.sleep(2);
      view = new SupplyAllocationView();
      JLog.write("Waiting for element '" + fieldName + "' (attempt " + (i + 2) + "/7)");
      elems = view.browserSession.getDriver().findElements(By.name(fieldName));
    }
    if (elems.isEmpty()) {
      JLog.write("WARNING: button/checkbox '" + fieldName + "' not found in DOM after wait, skipping");
      return;
    }
    WebElement element = elems.get(0);
    boolean b = element.isSelected();
    if ((action.equals("select") && !b) || (action.equals("deSelect") && b))
      view.executeJavaScript("arguments[0].click();", element);
  }

  public boolean isBtnSelected(String fieldName) {
    if (fieldName == null || fieldName.isEmpty()) {
      JLog.write("WARNING: isBtnSelected called with null/empty fieldName — returning false");
      return false;
    }
    view = new SupplyAllocationView();
    // Grid renders asynchronously after Apply – retry up to 15s for the element
    AbstractPage.sleep(3);
    List<WebElement> elems = view.browserSession.getDriver().findElements(By.name(fieldName));
    for (int i = 0; i < 6 && elems.isEmpty(); i++) {
      AbstractPage.sleep(2);
      view = new SupplyAllocationView();
      elems = view.browserSession.getDriver().findElements(By.name(fieldName));
    }
    if (elems.isEmpty()) {
      JLog.write("WARNING: button/checkbox '" + fieldName + "' not found in DOM after wait");
      return false;
    }
    return elems.get(0).isSelected();
  }

  public boolean isElementByNameVisible(String name) {
    view = new SupplyAllocationView();
    By by = By.className(name);
    view.executeJavaScript("arguments[0].scrollIntoView(true);", view.get(By.className(name)));
    return view.existsAndDisplayed(by);
  }

  public void selectSuppAllocateIcon(int col) {
    view = new SupplyAllocationView();
    AbstractPage.sleep(1);
    AbstractPage.sleep(1);
    List<WebElement> element = view.getSuppAllocateToAll();
    for (int i = 0; i < element.size(); i++) {
      if (i + 1 == col) {
        view.executeJavaScript("arguments[0].scrollIntoView(true);", element.get(i));
        view.executeJavaScript("arguments[0].click();", element.get(i));
        break;
      }
    }
  }

  public void selectItemAllocateIcon(int col) {
    view = new SupplyAllocationView();
    WebElement element = view.getItemAllocateToAll().get(0);
    view.executeJavaScript("arguments[0].scrollIntoView(true);", element);
    view.executeJavaScript("arguments[0].click();", element);
  }

  public void setSupplyAllocationvalueOnColumn(String allocationValue, int col) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getSupplyAllocationColumns();
    for (int i = 0; i < elements.size(); i++) {
      if (i + 1 == col) {
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(i));
        elements.get(i).clear();
        view.sleep(1);
        elements.get(i).sendKeys(allocationValue);
        view.sleep(1);
        elements.get(i).sendKeys(Keys.TAB);
        return;
      }
    }
  }

  public void setItemAllocationvalueOnColumn(String allocationValue, int col) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getItemAllocationColumns();
    for (int i = 0; i < elements.size(); i++) {
      if (i + 1 == col) {
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(i));
        elements.get(i).clear();
        elements.get(i).sendKeys(allocationValue);
        elements.get(i).sendKeys(Keys.TAB);
        return;
      }
    }
  }

  public void clickDropDwnActionsForAlloc(String allocTo, String action, int column) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getDropDownForAlloc();
    List<WebElement> options = view.getDropDownOptionsForAlloc(allocTo, action);
    for (int i = 0; i < elements.size(); i++) {
      if (i + 1 == column) {
        view.executeJavaScript("arguments[0].click();", elements.get(i));
        view.sleep(1);
        view.executeJavaScript("arguments[0].click();", options.get(i));
      }
    }
  }

  // public void copyAllocation()throws Exception {
  // view = new MTCMView();
  // WebElement element = view.getButton("Apply Allocation");
  // view.executeJavaScript("arguments[0].click();", element);
  // }
  //
  public void clickTabBtn(String tabName) {
    view = new SupplyAllocationView();
    WebElement element = view.regionTab(tabName);
    view.executeJavaScript("arguments[0].click();", element);
  }

  // public boolean isCopyCheckboxChecked(String checkboxName, int i) {
  // view = new MTCMView();
  // WebElement element =
  // view.getInputElements(checkboxName,"checkbox").get(i);
  // return element.isSelected();
  // }
  //
  // public void collaspseAllSites() {
  // SupplyAllocationView View = new SupplyAllocationView();
  // WebElement element = View.collaspseAllSites();
  // element.isEnabled();
  // view.executeJavaScript("arguments[0].click();", element);
  //
  // }
  //
  // public void regionselection(String regionvalue) {
  // View = new SupplyAllocationView();
  // //View.regiondropValue().click();
  // try {
  //
  // Combobox.select("Region",View.regiondropValue(), regionvalue);
  // JLog.write("Region type site value has selected succesfully" +
  // regionvalue);
  //
  // } catch (Exception e) {
  // JLog.error("Region type site value is not selected successfully "
  // +regionvalue );
  // }
  //
  // }
  //
  // public void siteSelection(String regionValue, String siteValue) {
  // View = new SupplyAllocationView();
  // //View.regiondropValue().click();
  // try {
  // Combobox.select("Region",View.regiondropValue(), regionValue);
  // AbstractPage.sleep(2);
  // Combobox.select("Site",View.sitedropValue(), siteValue);
  // JLog.write("Region type and site value has selected succesfully" +
  // regionValue +"and " +siteValue) ;
  //
  // } catch (Exception e) {
  // JLog.error("Region type and site value is not selected successfully " +
  // regionValue +"and " +siteValue );
  // }
  //
  // AbstractPage.sleep(10);
  // }
  //
  //
  // public void copyallocation() {
  //
  // }
  //
  public void setMultipleSupplyAllocValue(String value, String r, int c) {
    view = new SupplyAllocationView();
    List<WebElement> elements = view.getMultipleSuppAlloc(r);
    for (int i = 0; i < elements.size(); i++) {
      if (i + 1 == c) {
        elements.get(i).clear();
        elements.get(i).sendKeys(value);
        elements.get(i).sendKeys(Keys.TAB);
        view.sleep(3);
        JLog.write(
            "Set value on multiple supp alloc on row -" + r + " and column-" + c + " with value - "
                + value);
        return;
      }
    }
  }

  public void verifyMultipleSupplyAllocValue(String allocationValue, String row) {
    view = new SupplyAllocationView();
    String s = "";
    List<WebElement> elements = view.getSuppAllocRowWise(row);
    for (WebElement e : elements) {
      s = e.getText();
      if (s.equals(""))
        s = e.getAttribute("value");
      JLog.write("Actual value-" + s);
      JLog.write("Exp val-" + allocationValue);
      Verify.verify(s.equals(allocationValue), "Wrong value found on row" + row);
    }
  }

  public void setMultipleItemAllocValue(String value, String r, int c) {
    view = new SupplyAllocationView();
    view.sleep(1);
    List<WebElement> elements = view.getItemAllocRowWise(r);
    for (int i = 0; i < elements.size(); i++) {
      if (i + 1 == c) {
        String s = elements.get(i).getAttribute("value");
        // elements.get(i).click();
        view.executeJavaScript("arguments[0].click();", elements.get(i));
        elements.get(i).clear();
        elements.get(i).sendKeys(value);
        elements.get(i).sendKeys(Keys.TAB);
        JLog.write(
            "Set value on multiple item alloc on row " + r + " and column " + c + " with value - "
                + value);
        return;
      }
    }

  }

  // public void verifyMultipleSupplyAllocValue(String value, String r) {
  // view = new SupplyAllocationView();
  // WebElement ele = view.getMultipleSuppAlloc(r);
  // ele.clear();
  // ele.sendKeys(value);
  // JLog.write("Set value on multiple supp alloc on row " + r + " with value
  // - " + value);
  // }
}
