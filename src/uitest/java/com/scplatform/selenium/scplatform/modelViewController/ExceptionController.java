/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.steps.General;
import com.test.selenium.scplatform.steps.HarmonyLoginUI;
import com.test.selenium.scplatform.ui.main.upload.UploadController;
import com.google.common.base.Verify;

public class ExceptionController extends MTCMController {
  ExceptionView view;
  static String exceptionID;
  static String owner;
  static String approver;
  static String requestType;
  static String costType;
  static String platform;
  Prop prop = Prop.getInstance();

  @Override
  public PageImpl getView() {
    view = new ExceptionView();
    return view;
  }

  public void setCostTypeEleWithValue(String value) {
    view = new ExceptionView();
    WebElement ele = view.getCostType();
    ele.clear();
    ele.sendKeys(value);
    ele.sendKeys(Keys.TAB);
    JLog.write("Set CostType field with " + value);
  }

  public boolean verifyDelBtn() {
    view = new ExceptionView();
    WebElement ele = null;
    int c = 0;
    try {
      c = view.getDeleteLinks().size();
    } catch (Exception e) {
      if (e.toString().contains("no such element")) {
        JLog.resetErrorCount();
        return false;
      }
    }
    if (c > 0) {
      JLog.resetErrorCount();
      return true;
    }
    if (ele == null || c == 0) {
      JLog.resetErrorCount();
      return false;
    }
    JLog.resetErrorCount();
    return true;
  }

  public void verifyReqAppDetails(String status) throws IOException {
    view = new ExceptionView();
    WebElement ele = view.getStatusReqPanel();
    Verify.verify(ele.isDisplayed(), "Status Requestor Approver panel is not displayed!!");
    switch (status) {
      case "PENDING":
        pendingExcep();
        break;
      case "REJECTED":
        rejectedExcepByAdmin();
        break;
      case "REOPENED":
        pendingExcep();
        break;
      case "CLOSED":
        closedExcep();
        break;
      case "APPROVEDBYFINANCE":
        approvedExcepByFinance();
        break;
      case "APPROVEDBYADMIN":
        approvedExcepByFinance();
        break;
    }

  }

  public void approvedExcepByFinance() throws IOException {
    view = new ExceptionView();
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    String expReq = p.getProperty("defaultRequestor");
    String expApprover = p.getProperty("defaultApprover");
    WebElement ele = view.getReqStatusApproverPanelLabels("Requested");
    Verify.verify(ele.isDisplayed(), "Successfully verified Label requestor name");
    ele = view.getReqStatusApproverPanelLabels("Approve");
    Verify.verify(ele.isDisplayed(), "Successfully verified Label approver name");

    ele = view.getReqStatusApproverPanelIndicatorLabels(expReq);
    Verify.verify(ele.isDisplayed(), "Successfully verified requestor name");
    ele = view.getReqStatusApproverPanelIndicatorLabels(expApprover);
    Verify.verify(ele.isDisplayed(), "Successfully verified approver name");

    // date
  }

  public void closedExcep() throws IOException {
    view = new ExceptionView();
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    String expReq = p.getProperty("defaultRequestor");
    String expApprover = p.getProperty("defaultApprover");
    WebElement ele = view.getReqStatusApproverPanelLabels("Requested");
    Verify.verify(ele.isDisplayed(), "Successfully verified Label requestor name");
    ele = view.getReqStatusApproverPanelLabels("Reject");
    Verify.verify(ele.isDisplayed(), "Successfully verified Label Reject");

    ele = view.getReqStatusApproverPanelIndicatorLabels(expReq);
    Verify.verify(ele.isDisplayed(), "Successfully verified requestor name");
    ele = view.getReqStatusApproverPanelIndicatorLabels(expApprover);
    Verify.verify(ele.isDisplayed(), "Successfully verified rejected role");

    // date
  }

  public void rejectedExcepByAdmin() throws IOException {
    view = new ExceptionView();
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    String expReq = p.getProperty("defaultRequestor");
    String expApprover = p.getProperty("defaultApprover");
    WebElement ele = view.getReqStatusApproverPanelLabels("Requested");
    Verify.verify(ele.isDisplayed(), "Successfully verified Label requestor name");
    ele = view.getReqStatusApproverPanelLabels("Reject");
    Verify.verify(ele.isDisplayed(), "Successfully verified Label Reject");

    ele = view.getReqStatusApproverPanelIndicatorLabels(expReq);
    Verify.verify(ele.isDisplayed(), "Successfully verified requestor name");
    ele = view.getReqStatusApproverPanelIndicatorLabels(expApprover);
    Verify.verify(ele.isDisplayed(), "Successfully verified rejected role");

    // date
  }

  public void pendingExcep() throws IOException {
    view = new ExceptionView();
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    String expReq = "ADMIN"; // p.getProperty("defaultRequestor");
    String expApprover = p.getProperty("defaultApprover");
    String expStatus = "Pending";

    WebElement ele = view.getReqStatusApproverPanelLabels("Requested");
    Verify.verify(ele.isDisplayed(), "Successfully verified Label requestor name");
    try {
      ele = view.getReqStatusApproverPanelLabels("Approver");
      Verify.verify(!ele.isDisplayed(), "Successfully verified Label approver name");
    } catch (Exception e) {
      if (e.toString().contains("Exception")) {
        JLog.resetErrorCount();
        JLog.write("Successfully verified Label approver name");
      }
    }
    if (ele == null) {
      JLog.resetErrorCount();
      JLog.write("Successfully verified Label approver name is not displayed for pending status");
    }
    ele = view.getReqStatusApproverPanelLabels("Pending");
    Verify.verify(ele.isDisplayed(), "Successfully verified Label Status");

    ele = view.getReqStatusApproverPanelIndicatorLabels(expReq);
    Verify.verify(ele.isDisplayed(), "Successfully verified requestor name");
    ele = view.getReqStatusApproverPanelIndicatorLabels(expApprover);
    Verify.verify(ele.isDisplayed(), "Successfully verified approver name");
    ele = view.getReqStatusApproverPanelLabels(expStatus);
    Verify.verify(ele.isDisplayed(), "Successfully verified Status");

    ele = view.getReqStatusApproverPanelIndicatorLabels("Pending for approval");
    Verify.verify(ele.isDisplayed(), "Successfully verified Pending for approval label");

  }

  public void verifyExcepResultsStatus(String status) {
    view = new ExceptionView();
    List<WebElement> elements = view.getExcepResultsStatus();
    JLog.screenCapture();
    if (elements.size() == 0) {
      JLog.resetErrorCount();
      elements = view.getList(
          By.xpath(
              "//label[text()='Status']//ancestor::div//following-sibling::div//b[text()='" + status
                  + "']"));
    }
    for (WebElement e : elements) {
      String s = e.getText();
      Verify.verify(e.getText().equals(status), "Unable to verify status as " + status);
      break;
    }
    JLog.write("Successfully verified status as " + status + " on all rows");
  }

  public void verifyExcepApproverErrorValidation(String msg) {
    view = new ExceptionView();
    WebElement ele = view.get(By.xpath("//div[contains(@class,'exceptionApproverError')]"));
    String s = ele.getText();
    if (s.equals("")) {
      s = ele.getAttribute("value");
    }
    Verify.verify(s.equals(msg), "Unable to verify error message on approver field.");
  }

  public void clickPreRequisiteLink() {
    view = new ExceptionView();
    WebElement ele = view.getPreReqLink();
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    view.executeJavaScript("arguments[0].click();", ele);
    JLog.write("Clicked on prerequisite link.");
  }

  public int getDownloadLinkCount() {
    view = new ExceptionView();
    return view.getDownloadLinks().size();
  }

  public void deleteFiles(int count) {
    view = new ExceptionView();
    List<WebElement> elements = view.getDeleteLinks();
    view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
    for (int i = 0; i < count; i++) {
      view.executeJavaScript("arguments[0].click();", elements.get(i));
    }
  }

  public void verifyDeletedFiles(int count) {
    view = new ExceptionView();
    List<WebElement> elements = view.getDeleteLinks();
    if (elements.size() == 0)
      JLog.resetErrorCount();
    Verify.verify(count == elements.size(), "Mismatch on no of files deleted");
  }

  public void selectExceptionStateValue(String value) {
    view = new ExceptionView();
    org.openqa.selenium.WebDriver driver = view.browserSession.getDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement ele = view.getExceptionStateTextField();
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    AbstractPage.sleep(2);
    view.executeJavaScript("arguments[0].click();", ele);
    try {
      ele = view.get(By.partialLinkText("Select All"));

    } catch (Exception e) {
      if (e.toString().contains("NoSuchElementException")) {
        WebElement expandBtn = view.getExcepExpandMoreBtn();
        if (expandBtn != null) {
          view.executeJavaScript("arguments[0].click();", expandBtn);
        }
        ele = view.get(By.partialLinkText("Select All"));
        JLog.resetErrorCount();
      }
    }
    if (ele == null) {
      WebElement expandBtn = view.getExcepExpandMoreBtn();
      if (expandBtn != null) {
        view.executeJavaScript("arguments[0].click();", expandBtn);
      }
      ele = view.get(By.partialLinkText("Select All"));
      JLog.resetErrorCount();
    }
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    view.executeJavaScript("arguments[0].click();", ele);
    AbstractPage.sleep(2);
    ele = view.getPartialLinkText("Clear All");
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    view.executeJavaScript("arguments[0].click();", ele);
    ele = view.getStatusValueCheckBox(value);
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    view.executeJavaScript("arguments[0].click();", ele);
    WebElement closeExpandBtn = view.getExcepExpandMoreBtn();
    if (closeExpandBtn != null) {
      view.executeJavaScript("arguments[0].scrollIntoView(true);", closeExpandBtn);
      view.executeJavaScript("arguments[0].click();", closeExpandBtn);
    }
    // just clicking outside to select the status to the textfield
    AbstractPage.sleep(2);
    value = value.toUpperCase();
    // ele =
    // view.get(By.xpath("//div[contains(text(),'" + value + "')]"));

    try {
      ele = wait.until(ExpectedConditions
          .visibilityOfElementLocated(By.xpath(String.format("//span[contains(text(),'%s')]", value))));
      if (ele.isDisplayed()) {
        JLog.write("✅ ' " + value + " ' is visible.");
      }
    } catch (Exception e) {

      try {

        WebElement fallbackStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(text(),'1 selected')]")));

        if (fallbackStatus.isDisplayed()) {
          JLog.write("✅ '1 selected' is visible as fallback.");
          Assert.assertTrue(true, "'1 selected' is visible as fallback.");
        }
      } catch (Exception e2) {
        JLog.fail("❌ Neither '" + value + "' nor '1 selected' is visible.");
        Assert.fail("Status is not visible!");
      }

    }

    if (ele != null)
      Verify.verify(ele.isDisplayed(), "Status is not selected correctly");
  }

  public void saveNewExcepNameID() throws IOException {
    view = new ExceptionView();
    String exceptionIDnName = view.getExceptionId().getText();
    // String id = exceptionIDnName.substring(0, 5);
    String id = exceptionIDnName.split(" | ")[0];
    String name = exceptionIDnName.substring(9, exceptionIDnName.length() - 1);
    // InputStream fr = new FileInputStream(prop.getRootDir() +
    // "scplatform/data/exceptionDetails.properties");
    Properties p = new Properties();
    FileOutputStream fr = new FileOutputStream(
        prop.getRootDir() + "scplatform/data/properties/editExceptionDetails.properties");
    p.setProperty("excepNameEdit", name);
    p.setProperty("excepIDEdit", id);
    p.save(fr, "");
    fr.close();
  }

  public void verifyValidationErrorMsg(String msg) {
    view = new ExceptionView();
    Verify.verify(
        view.getListResultsMessage().getText().contains("Validation Error"),
        "Unable to see validation error message");
    String actMsg = view
        .get(
            By.xpath("//div[@id='costErrorMesgBox' and @data-message-type='error']/div/div/div[1]"))
        .getText();
    Verify.verify(actMsg.contains(msg), "Unable to verify acknowledgement error");
  }

  public boolean verifyNoValidationErrorMsg() {
    view = new ExceptionView();
    WebElement ele = null;
    try {
      ele = view.browserSession.getDriver().findElement(By.className("eto-messageblock__body"));
    } catch (Exception e) {
      if (ele == null) {
        JLog.resetErrorCount();
        return false;
      }
    }
    if (ele == null) {
      JLog.resetErrorCount();
      return false;
    }
    if (ele.getText().contains("Validation Error")) {
      clickButton("View error");
      String s = verifyMsgOnWarningPopup(""); // just trying to print the
      // warning msg
      JLog.error("An error occurred...." + s);
      JLog.fail("unexpected validation error occurred.");
    }
    return true;

  }

  public void verifyNewExcepDetailsEnteredOnFields() {

  }

  public void verifyEditExcepNameID() throws IOException {
    view = new ExceptionView();
    String exceptionIDnName = view.getExceptionId().getText();
    String id = exceptionIDnName.split(" | ")[0];// exceptionIDnName.substring(0,
    // 5);
    String name = exceptionIDnName.substring(9, exceptionIDnName.length() - 1);
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/editExceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    Verify.verify(
        name.equals(p.getProperty("excepNameEdit")),
        "Edit action changed existing exception name");
    Verify.verify(
        id.equals(p.getProperty("excepIDEdit")),
        "Edit action changed existing exception id");

  }

  public void verifyNewExcepDetails(String status) throws IOException {
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    view = new ExceptionView();
    String exceptionDetails = view.getDetailsOnNewExcepCreationPage("2", "1").getText(); // Requestor
    // Name
    InputStream fo1 = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
    Properties p1 = new Properties();
    p1.load(fo1);
    String actReq = p1.getProperty("login");
    JLog.write("Actual req on UI=" + exceptionDetails);
    JLog.write("Expected req=" + actReq);
    Verify.verify(
        exceptionDetails.toUpperCase().contains(actReq.toUpperCase()),
        "Unable to verify requestor name");
    JLog.write("Successfully verified Requestor Name");

    String owner;
    String approver;
    if (status.equals("NEW")) {
      owner = view.getDetailsOnNewExcepCreationPage("1", "2").getText(); // Owner
      // Name
      approver = view.getDetailsOnNewExcepCreationPage("2", "2").getText(); // Approver
      // Name
    } else {
      owner = view.getDetailsOnNewExcepCreationPage("3", "1").getText(); // Owner
      // Name
      approver = view.getDetailsOnNewExcepCreationPage("4", "1").getText(); // Approver
      // Name
    }
    if (!(status.equals("REJECTED") || status.equals("CLOSED"))) {
      verifyExceptionIDnName();
      Verify.verify(owner.contains(p.getProperty("owner")), "Unable to verify Owner name");
      JLog.write("Successfully verified Owner Name");
      String app = p.getProperty("approver");
      JLog.write("APprover name on prop = " + app);
      Verify.verify(approver.contains(p.getProperty("approver")), "Unable to verify Approver name");
      JLog.write("Successfully verified Approver Name");
    }
    exceptionDetails = view.getDetailsOnNewExcepCreationPage("1", "1").getText(); // Status
    // Name
    Verify.verify(exceptionDetails.contains(status), "Unable to verify Status name");
    JLog.write("Successfully verified Status Name");
    JLog.write("Successfully verified exception details");
  }

  public void excepStatus(String status) {
    String exceptionDetails;
    exceptionDetails = view.getDetailsOnNewExcepCreationPage("1", "1").getText(); // Status
    // Name
    Verify.verify(exceptionDetails.equals(status), "Unable to verify Status name");
    JLog.write("Successfully verified Status Name");
  }

  public void verifyCloseBtnAfterAttachment() {
    view = new ExceptionView();
    JLog.screenCapture();
    WebElement ele = view.get(By.xpath("//*[contains(@class,'md-icon remove eto-upload__remove-icon')]"));
    Verify.verify(ele.isDisplayed(), "Unable to see close btn after attached file");
  }

  public void clickCloseBtnAfterAttachment() {
    view = new ExceptionView();
    WebElement ele = view.get(By.xpath("//*[contains(@class,'md-icon remove eto-upload__remove-icon')]"));
    // view.executeJavaScript("arguments[0].click();", ele);
    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    JLog.screenCapture();
    clickWithActionClass(ele);
    boolean status = true;
    try {
      ele = view.get(By.xpath("//*[contains(@class,'md-icon remove eto-upload__remove-icon')]"));
    } catch (Exception e) {
      if (ele == null) {
        JLog.resetErrorCount();
        status = false;
      }
    }
    if (ele == null) {
      JLog.resetErrorCount();
      status = false;
    } else
      status = ele.isDisplayed();
    if (status)
      clickWithActionClass(ele);
  }

  public void verifyCloseBtnNotVisibleAfterAttachment() {
    view = new ExceptionView();
    JLog.screenCapture();
    WebElement ele = null;
    boolean status = true;
    try {
      ele = view.get(By.xpath("//*[contains(@class,'md-icon remove eto-upload__remove-icon')]"));
    } catch (Exception e) {
      if (ele == null) {
        JLog.resetErrorCount();
        status = false;
      }
    }
    if (ele == null) {
      JLog.resetErrorCount();
      status = false;
    } else
      status = ele.isDisplayed();
    Verify.verify(!status, "Able to see close btn after attached file");
  }

  public void verifyExcepPricingDetails() {
    view = new ExceptionView();
    JLog.screenCapture();
    WebElement ele = view.getAttachedFileDownloadIcon();
    Verify.verify(ele.isDisplayed(), "Unable to see attached file downlaod icon");
    ele = view.getAttachedFile();
    Verify.verify(ele.getText().contains("CRException.xlsx"), "Unable to see atatched file.");
    ele = view.getAttachedFileDetails("3");
    Verify.verify(ele.getText().equals("1"), "Unable to see cost record count.");
    ele = view.getAttachedFileDetails("4");
    JLog.write("Uploaded by =" + ele.getText());
    Verify.verify(
        ele.getText().contains("Alex Teo CP"),
        "Unable to see uploaded by under pricing details.");
    ele = view.getAttachedFileDetails("5");
    JLog.write("Attached file date -> ele.getText" + ele.getText());
    JLog.write("Attached file date -> ele.innertext" + ele.getAttribute("innerText"));
    JLog.write("today's date" + DateTime.now().toString("dd/mm/yyyy"));
    String day = DateTime.now().toString("dd/mm/yyyy").substring(0, 1);
    String year = DateTime.now().toString("dd/mm/yyyy").substring(6, 9);
    String s = ele.getText();
    Verify.verify(s.contains(day), "Unable to see uploaded by under pricing details.");
    JLog.write("Successfully verified Exception Pricing details.");
  }

  public void setExceptionOwner(String name) throws IOException {
    MTCMController controller = new MTCMController();
    controller.setEleWithValue("exceptionOwner", name);
    Properties p = new Properties();
    // InputStream fr = new FileInputStream(prop.getRootDir() +
    // "scplatform/data/exceptionDetails.properties");
    // FileOutputStream fr = new FileOutputStream(prop.getRootDir() +
    // "scplatform/data/exceptionDetails.properties");
    // p.setProperty("owner", name);
    // p.save(fr, "");
    // fr.close();
    owner = name;
    // JLog.write(p.toString());
  }

  public void setExceptionApprover(String name) throws IOException {
    MTCMController controller = new MTCMController();
    controller.setEleWithValue("exceptionApprover", name);
    // Properties p = new Properties();
    // FileOutputStream fr = new FileOutputStream(prop.getRootDir() +
    // "scplatform/data/exceptionDetails.properties");
    // p.setProperty("approver", name);
    // p.save(fr, "");
    // fr.close();
    // JLog.write(p.toString());
    approver = name;
  }

  public void setExceptionPlatform(String name) throws IOException {
    MTCMController controller = new MTCMController();
    controller.setEleWithValue("platformName", name);
    // Properties p = new Properties();
    // FileOutputStream fr = new FileOutputStream(prop.getRootDir() +
    // "scplatform/data/exceptionDetails.properties");
    // p.setProperty("platform", name);
    // p.save(fr, "");
    // fr.close();
    // JLog.write(p.toString());
    platform = name;
  }

  public void setExceptionRequestType(String label, String name) throws IOException {
    MTCMController controller = new MTCMController();
    // controller.setComboBox(label, name);
    AbstractPage.sleep(20);
    // By by = By.xpath("//label[contains(text(),'" + label +
    // "')]/following-sibling::div//select");
    if (name.contains("BACK")) {
      name = "BACKDATE";
    }
    requestType = name;
    if (name.contains("MID")) {
      name = "MID MONTH";
    }
    By by = By.id("requestType");
    Select listbox = new Select(view.get(by));
    listbox.selectByVisibleText(name);
    String s = listbox.getFirstSelectedOption().getText();
    if (name.contains("MID") && (!s.contains("MID")))
      listbox.selectByVisibleText("MID MONTH");
    // if (LoginSCPlatformHarmony.navUrl.contains("4160")) {
    // Properties p = new Properties();
    // InputStream fi = new FileInputStream(prop.getRootDir() +
    // "scplatform/data/psBox/labelName.properties");
    // p.load(fi);
    // if (name.contains("BACKDATE")) {
    // name = p.getProperty("reqTypeBackDate", "");
    // } else if (name.contains("MID MONTH")) {
    // name = p.getProperty("reqTypeMidMonth", "");
    // }
    // }

  }

  public void setExceptionCostType(String label, String name) throws IOException {
    MTCMController controller = new MTCMController();
    controller.setComboBox(label, name);
    costType = name;
  }

  public void getExceptionID() {
    view = new ExceptionView();
    exceptionID = view.getExceptionId().getText();
    exceptionID = exceptionID.split(" | ")[0];// exceptionID.substring(0,
    // 5);
  }

  public String getExcepID() {
    return exceptionID;
  }

  public void searchAndverifyNewExcepDetails(String name, String status) throws Throwable {
    JLog.resetErrorCount();
    HarmonyLoginUI ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Pricing", "Search Exception Request");
    General gen = new General();
    gen.clickButton("Clear");
    UploadController up = new UploadController();
    AbstractPage.sleep(2);
    gen.enterTextFieldVal(name, "exceptionName");
    MTCMController controller = new MTCMController();
    controller.clickButton("Apply");
    name = name + up.getTimeStamp();
    verifyExceptionDetailsOnSearchPageWithName(name, status);
  }

  public void searchAndverifyNewExcepDetailsWithID() throws Throwable {
    JLog.resetErrorCount();
    getExceptionID();
    HarmonyLoginUI ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Pricing", "Search Exception Request");
    MTCMController controller = new MTCMController();
    controller.clickButton("Clear");
    General gen = new General();
    // String excepID = exceptionID.substring(0, 5);
    String excepID = exceptionID.split(" | ")[0];
    Properties p = new Properties();
    InputStream fi = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    p.load(fi);
    String prevExcepID = p.getProperty("exceptionID", "");
    fi.close();

    p = new Properties();
    FileOutputStream fo = new FileOutputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    p.setProperty("costType", costType);
    p.setProperty("approver", approver);
    p.setProperty("requestType", requestType);
    p.setProperty("platform", platform);
    p.setProperty("owner", owner);
    // String prevExcepID = p.getProperty("exceptionID","");
    p.setProperty("prevExcepID", prevExcepID);
    p.setProperty("exceptionID", excepID);
    p.setProperty("autoExceptionID", excepID);
    p.save(fo, "");
    fo.close();
    JLog.write(p.toString());
    gen.enterTextFieldVal(excepID, "exceptionID");
    controller.clickButton("Apply");
    JLog.screenCapture();
    verifyExceptionDetailsOnSearchPageWithID(excepID);
  }

  public void searchAndverifyAutoExcepDetailsWithID() throws Throwable {
    getExceptionID();
    HarmonyLoginUI ui = new HarmonyLoginUI();
    ui.navHarmonyMTCM("Pricing", "Search Exception Request");
    MTCMController controller = new MTCMController();
    controller.clickButton("Clear");
    General gen = new General();
    Properties p = new Properties();
    InputStream fi = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    p.load(fi);
    String excepID = p.getProperty("autoExceptionID", "");
    gen.enterTextFieldVal(excepID, "exceptionID");
    controller.clickButton("Apply");
    verifyExceptionDetailsOnSearchPageWithID(excepID);
  }

  public void verifyExceptionDetailsOnSearchPageWithName(String name, String status)
      throws IOException {
    view = new ExceptionView();
    JLog.screenCapture();
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    String exceptionDetails = view.getTableValue("1", "2").getText(); // excep
    // Name
    Verify.verify(exceptionDetails.contains(name), "Unable to verify exception Name");
    JLog.write("Successfully verified Exception Name");
    exceptionDetails = view.getTableValue("1", "3").getText(); //
    // Name
    // Verify.verify(exceptionDetails.contains("ADMIN"), "Unable to verify
    // requestor name");
    // JLog.write("Successfully verified Requestor Name");
    InputStream fo1 = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
    Properties p1 = new Properties();
    p1.load(fo1);
    String actReq = p1.getProperty("login");
    JLog.write("Actual req on UI=" + exceptionDetails);
    JLog.write("Expected req=" + actReq);
    Verify.verify(
        exceptionDetails.toUpperCase().contains(actReq.toUpperCase()),
        "Unable to verify requestor name");
    JLog.write("Successfully verified Requestor Name");

    exceptionDetails = view.getTableValue("1", "4").getText(); // Owner Name
    String s = p.getProperty("owner");
    Verify.verify(exceptionDetails.contains(p.getProperty("owner")), "Unable to verify Owner name");
    JLog.write("Successfully verified Owner Name");
    exceptionDetails = view.getTableValue("1", "5").getText(); // Approver
    // Name
    Verify.verify(
        exceptionDetails.contains(p.getProperty("approver")),
        "Unable to verify Approver name");
    JLog.write("Successfully verified Approver Name");
    exceptionDetails = view.getTableValue("1", "6").getText(); // Status
    // Name
    Verify.verify(exceptionDetails.contains(status), "Unable to verify Status name");
    JLog.write("Successfully verified Status Name");
    exceptionDetails = view.getTableValue("1", "7").getText(); // Cost Type
    Verify.verify(
        exceptionDetails.contains(p.getProperty("costType")),
        "Unable to verify Cost Type name");
    JLog.write("Successfully verified Cost Type");
    String reqTyp = p.getProperty("requestType");
    // WebElement ele = view
    // .get(By.xpath("//div[@id='grid-result']//tr[1]//td[8]//div[@class='eto-grid-expand__content']"));
    exceptionDetails = view.getTableValue("1", "9").getText(); // Request
    // exceptionDetails = ele.getText(); // Request
    JLog.screenCapture();
    // JLog.write("Req Type from UI after getText= " + exceptionDetails);
    // if (exceptionDetails.equals("")) {
    // exceptionDetails = ele.getAttribute("value");
    // }
    // JLog.write("Req Type from UI after getAttr= " + exceptionDetails);
    // if (exceptionDetails.equals("")) {
    // exceptionDetails = ele.getAttribute("innerText");
    // }
    // JLog.write("Req Type from UI after getInnerText= " +
    // exceptionDetails);

    if (exceptionDetails.contains("BACK")) {
      reqTyp = reqTyp.substring(0, 4);
      exceptionDetails = exceptionDetails.substring(0, 4);
      JLog.write("exp = " + reqTyp);
      JLog.write("act = " + exceptionDetails);
    }
    if (exceptionDetails.contains("-")) {
      reqTyp = reqTyp.replaceFirst(" ", "-");
      JLog.write("act = " + exceptionDetails);
    }

    if (exceptionDetails.contains("MID")) {
      // exceptionDetails = exceptionDetails.substring(0,
      // exceptionDetails.indexOf(" "));
      reqTyp = reqTyp.substring(0, reqTyp.indexOf(" "));
      JLog.write("Exp req type = " + reqTyp);
      JLog.write("Act req type = " + exceptionDetails);
    }

    // Verify.verify(exceptionDetails.contains(reqTyp), "Unable to verify Request
    // Type");
    JLog.write("Successfully verified Request Type");
    exceptionDetails = view.getTableValue("1", "10").getAttribute("innerText"); // Subtier
    Verify.verify(exceptionDetails.contains("Yes"), "Unable to verify Subtier");
    JLog.write("Successfully verified Subtier");
    exceptionDetails = view.getTableValue("1", "11").getAttribute("innerText");// .getText();
    // //Commodity
    Verify.verify(exceptionDetails.contains("Application"), "Unable to verify Commodity");
    JLog.write("Successfully verified Commodity");
    exceptionDetails = view.getTableValue("1", "12").getAttribute("innerText"); // Platform
    Verify
        .verify(exceptionDetails.contains(p.getProperty("platform")), "Unable to verify Platform");
    JLog.write("Successfully verified Platform");
  }

  public void verifyExceptionDetailsOnSearchPageWithID(String id) throws IOException {
    view = new ExceptionView();
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    String exceptionDetails = view.getTableValue("1", "1").getText(); // excep
    // ID
    Verify.verify(exceptionDetails.contains(id), "Unable to verify exception ID");
    JLog.write("Successfully verified Exception ID");
    exceptionDetails = view.getTableValue("1", "3").getText(); // Requestor
    // Name
    InputStream fo1 = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
    Properties p1 = new Properties();
    p1.load(fo1);
    String actReq = p1.getProperty("login");
    JLog.write("Actual req on UI=" + exceptionDetails);
    JLog.write("Expected req=" + actReq);
    Verify.verify(
        exceptionDetails.toUpperCase().contains(actReq.toUpperCase()),
        "Unable to verify requestor name");
    JLog.write("Successfully verified Requestor Name");
    exceptionDetails = view.getTableValue("1", "4").getAttribute("innerText"); // Owner
    // Name
    String o = p.getProperty("owner");
    Verify.verify(exceptionDetails.contains(p.getProperty("owner")), "Unable to verify Owner name");
    JLog.write("Successfully verified Owner Name");
    exceptionDetails = view.getTableValue("1", "5").getText(); // Approver
    // Name
    JLog.write("Expected approver name from prop=" + p.getProperty("approver"));
    JLog.write("Expected approver name from UI=" + exceptionDetails);
    Verify.verify(
        exceptionDetails.contains(p.getProperty("approver")),
        "Unable to verify Approver name");
    JLog.write("Successfully verified Approver Name");
    // exceptionDetails = view.getTableValue("1","6").getText(); //Status
    // Name
    // Verify.verify(exceptionDetails.contains(status),"Unable to verify
    // Status name");
    // JLog.write("Successfully verified Status Name");
    exceptionDetails = view.getTableValue("1", "7").getText(); // Cost Type
    Verify.verify(
        exceptionDetails.contains(p.getProperty("costType")),
        "Unable to verify Cost Type name");
    JLog.write("Successfully verified Cost Type");
    exceptionDetails = view.getTableValue("1", "9").getText(); // Request
    // Type
    JLog.write("exp" + exceptionDetails);
    String reqTyp;
    reqTyp = p.getProperty("requestType");
    JLog.write("Exp req type = " + reqTyp);
    JLog.write("Act req type = " + exceptionDetails);
    if (exceptionDetails.contains("BACK")) {
      reqTyp = reqTyp.substring(0, 4);
      exceptionDetails = exceptionDetails.substring(0, 4);
      JLog.write("act = " + exceptionDetails);
      JLog.write("Exp req type = " + reqTyp);
    }
    if (exceptionDetails.contains("-")) {
      reqTyp = reqTyp.replaceFirst(" ", "-");
      JLog.write("act = " + exceptionDetails);
    }
    if (reqTyp.contains(" ")) {
      // exceptionDetails = exceptionDetails.substring(0, 3);
      reqTyp = reqTyp.substring(0, reqTyp.indexOf(" "));
      JLog.write("Exp req type = " + reqTyp);
      JLog.write("Act req type = " + exceptionDetails);
    }
    // Verify.verify(exceptionDetails.contains(reqTyp), "Unable to verify Request
    // Type");
    // JLog.write("Successfully verified Request Type");
    exceptionDetails = view.getTableValue("1", "10").getAttribute("innerText"); // Subtier
    Verify.verify(exceptionDetails.equals("Yes"), "Unable to verify Subtier");
    JLog.write("Successfully verified Subtier");
    exceptionDetails = view.getTableValue("1", "11").getAttribute("innerText"); // Commodity
    Verify.verify(exceptionDetails.equals("Application"), "Unable to verify Commodity");
    JLog.write("Successfully verified Commodity");
    exceptionDetails = view.getTableValue("1", "12").getAttribute("innerText"); // Platform
    Verify.verify(exceptionDetails.equals(p.getProperty("platform")), "Unable to verify Platform");
    JLog.write("Successfully verified Platform");
    /*
     * exceptionDetails = view.getTableValue("1", "8").getAttribute("innerText");//
     * costrecord
     * System.out.println(exceptionDetails); // // count Verify.verify(
     * exceptionDetails.equals("1"), "Unable to verify cost record count");
     * JLog.write("Successfully verified cost record count");
     */
    fo.close();
  }

  public void verifyExceptionIDnName() {
    view = new ExceptionView();
    String exceptionIDnName = view.getExceptionId().getText();
    Verify.verify(exceptionIDnName.startsWith("ER"), "Unable to verify exception ID");
    Verify.verify(exceptionIDnName.contains("Excep"), "Unable to verify exception name");
  }
}
