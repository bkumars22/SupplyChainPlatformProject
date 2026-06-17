/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.scplatform.qa.e2Messages.utilities.MessageWriter.UPLOAD_TYPE;
import com.scplatform.qa.iris.serialization.excel.ExcelWriter;
import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.abstractHelpers.AbstractFileIO;
import com.test.selenium.common.abstractHelpers.SystemInformation;
import com.test.selenium.common.excel.ExcelReader;
import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.businessEntities.BusinessEntitiesView;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.steps.Forecast;
import com.test.selenium.scplatform.steps.General;
import com.test.selenium.scplatform.steps.HarmonyLoginUI;
import com.test.selenium.scplatform.ui.main.manageUploadJobs.ManageUploadJobsResultsController;
import com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob.LoadJobController.LOAD_STATUS;
import com.test.selenium.scplatform.ui.main.upload.errors.UploadErrorsController;
import com.google.common.base.Verify;

public class UploadController extends ManageUploadJobsResultsController {
  private UploadPage page;
  protected ExcelWriter excel;
  private String msgTypeStatus;
  private String message, actionType;
  private String msgTypeDropDwn;
  protected UPLOAD_TYPE currentUploadType = null;
  protected List<T> messageData;
  static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
  // Date date = DateTime.now().compareTo(o)
  String fgName = "FGUpload" + timeStamp;
  static String itemNumber = "AutoItem" + timeStamp;
  public static String fc1 = "fcItem1" + timeStamp;
  public static String fc2 = "fcItem2" + timeStamp;
  public static String fc3 = "fcItem3" + timeStamp;
  public static String fc4 = "fcItem4" + timeStamp;
  public static String fc5 = "fcItem5" + timeStamp;
  public static String fc6 = "fcItem6" + timeStamp;
  public static String fc7 = "fcItem7" + timeStamp;
  public static String fc8 = "fcItem8" + timeStamp;
  public static String fc9 = "fcItem9" + timeStamp;
  public static String fc10 = "fcItem10" + timeStamp;
  public static String fc11 = "fcItem11" + timeStamp;
  public static String fc12 = "fcItem12" + timeStamp;
  public static String fc13 = "fcItem13" + timeStamp;
  public static String fc14 = "fcItem14" + timeStamp;
  public static String fc15 = "fcItem15" + timeStamp;
  Prop prop = Prop.getInstance();
  public static String priceTAM = "PriceTAM" + timeStamp;
  public static String PriceTAMCommodity = "PriceTAMCommodity" + timeStamp;
  public static String eMCpriceTAM = "EMCPriceTAM" + timeStamp;
  public static String dellpriceTAM = "DELLPriceTAM" + timeStamp;
  public static String dellpriceTAMmonthly = "DELLPriceTAMMonthly" + timeStamp;
  public static String dellpriceTAMQuarterly = "DELLPriceTAMQuarterly" + timeStamp;
  public static String xlobdeleteitem = "XLOBDeleteItem" + timeStamp;
  public static String xlobdeltitem1 = "XLOBDeleteItem1" + timeStamp;
  public static String xlobdeltitem2 = "XLOBDeleteItem2" + timeStamp;

  public UploadController() {
    super();
    page = new UploadPage();
  }

  @Override
  public PageImpl getView() {
    return new UploadPage();
  }

  public void selectMsgType(String value) {
    page.setContext();
    WebElement ele = page.get(By.id("messageType"));
    if (ele == null) {
      JLog.error("Cannot find messageType dropdown on upload page", TakeScreenshot.True);
      return;
    }
    Select select = new Select(ele);
    select.selectByValue(value);
  }

  public void upload(String status, String msg, String action, String msgTyp) throws Exception {
    super.assertModelsExist();
    msgTypeStatus = status;
    message = msg;
    actionType = action;
    msgTypeDropDwn = msgTyp;
    for (Model model : models) {
      populateValues(model);
    }
  }

  public void uploadWithoutBtn(String action) throws Exception {
    super.assertModelsExist();
    actionType = action;
    for (Model model : models) {
      populateValues(model);
    }
  }

  // protected boolean waitForCompletion() {
  // List<ManageUploadJobsResultsModel> searchResults = page.parseResults();
  // String status = searchResults.get(0).getStatus();
  //
  // boolean timedOut = false;
  // long startTime = System.currentTimeMillis();
  // long t = 0;
  // int TIMEOUT = timeoutMinutes * 60 * 1000; // convert to milliseconds
  //
  // while ((this.inProgressStates.contains(status)) && (!timedOut)) {
  // t = System.currentTimeMillis();
  // if (t - startTime > TIMEOUT) {
  // timedOut = true;
  // } else {
  // page.sleep(10);
  // clickAndCheckForPOSTError(page.searchButton());
  // t
  // searchResults.clear();
  // searchResults = page.parseResults();
  // status = searchResults.get(0).getStatus();
  // }
  // }
  //
  // boolean success = true;
  // if (this.inProgressStates.contains(status)) {
  // success = false;
  //
  // if (timedOut) {
  // JLog.error(
  // this.getClass().getSimpleName() + ".waitForCompletion(): Timeout occurred
  // after "
  // + timeoutMinutes + " minutes waiting for file to process. Current status
  // is
  // '" + status
  // + "'. In Progress status's set to: " + inProgressStates.toString(),
  // TakeScreenshot.True);
  // }
  // }
  // return success;
  // }
  //
  // protected void initializeInProgressStates() {
  // inProgressStates = new ArrayList<String>();
  // inProgressStates.add(LOAD_STATUS.PENDING.toString());
  // }

  @Override
  public void populateValues(Model model) throws Exception {
    super.populateValues(model);
    JLog.resetErrorCount();
    try {
      for (Field field : getAutoPopulateOffFields(model)) {
        String name = field.getName();
        Object value = getValue(model, name);
        if (name.equals("uploadFile")) {
          handleUploadFile((String) value, msgTypeStatus, message, actionType);
        }
      }
    } catch (Exception e) {
      // JLog.fail(e);
      e.printStackTrace();
    } catch (Throwable e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void handleUploadFileWithoutButtonAction(Model model, String fileName, String actionType) throws Throwable {
    super.populateValues(model);
    AbstractPage.sleep(1);
    JLog.resetErrorCount();
    File file = new File(fileName);
    if (file.exists() && fileName.contains(".xlsx")) {
      generate(fileName, actionType);
    }
    // } else if (file.exists() && !fileName.contains(".png")) {
    // generate(fileName, actionType);
    // } else {
    // JLog.error("Unable to find file: " + fileName);
    // }
    try {
      // file upload
      UploadPage page1 = new UploadPage();
      if (page1.getFileField() == null) {
        JLog.error("Unable to Find Upload Button!", TakeScreenshot.True);
        return;
      }
      new SystemInformation();
      String os = SystemInformation.getOperatingSystem();
      if (os.contains("Windows")) {
        fileName = fileName.replace("/", "\\");
      }

      JLog.linkToFile("Uploading File", fileName);
      if (fileName.contains("ODM")) {
        WebElement ele = null;
        try {
          ele = page1.get(By.id("uploadODMEmail"));
          if (ele == null) {
            JLog.resetErrorCount();
            ele = page.get(By
                .xpath("//div[@id='upload-odm-emails']//input[@type='file' and @multiple='multiple']"));
          }
          ele.sendKeys(fileName);
        } catch (Exception e) {
          JLog.resetErrorCount();
          ele = page.get(
              By.xpath("//div[@id='upload-odm-emails']//input[@type='file' and @multiple='multiple']"));
          ele.sendKeys(fileName);
        }
      } else
        page1.getFileField().sendKeys(fileName);

      AbstractPage.sleep(2);
    } catch (Exception e) {
      if (e.toString().contains("UnreachableBrowserException")) {
        AbstractPage.sleep(100);
        JLog.screenCapture();
      }
    }
  }

  public void handleUploadFileForODMEmailAttach(Model model, String fileName, String actionType) throws Exception {
    // super.populateValues(model);
    // JLog.resetErrorCount();
    if (fileName.contains("CREmailException")) {
      fileName = fileName + ".xlsx";
    }
    // if (new File(fileName).exists()) {
    // generate(fileName, actionType);
    // } else {
    // JLog.error("Unable to find file: " + fileName);
    // }
    try {
      // file upload
      UploadPage page1 = new UploadPage();
      if (page1.getFileField() == null) {
        JLog.error("Unable to Find Upload Button!", TakeScreenshot.True);
        return;
      }
      new SystemInformation();
      String os = SystemInformation.getOperatingSystem();
      if (os.contains("Windows")) {
        fileName = fileName.replace("/", "\\");
      }

      JLog.linkToFile("Uploading File", fileName);
      if (fileName.contains("Email")) {
        // fileName = fileName.split(".xlsx")[0];
        // fileName = fileName.contains("html") ? fileName + ".html" :
        // fileName + ".zip";
        page1 = new UploadPage();
        WebElement e = page1.get(By.xpath("//div[@id='upload-odm-emails']//input[@type='file']"));
        e.clear();
        e.sendKeys(fileName);
      } else {
        page1.getFileField().clear();
        page1.getFileField().sendKeys(fileName);
      }

      AbstractPage.sleep(2);
    } catch (Exception e) {
      if (e.toString().contains("UnreachableBrowserException")) {
        AbstractPage.sleep(100);
        JLog.screenCapture();
      }
    }
  }

  private void handleUploadFile(String fileName, String msgType, String message, String actionType) throws Throwable {
    File file = new File(fileName);
    selectMsgType(msgTypeDropDwn);

    if (file.exists()) {
      generate(fileName, actionType);
      try {
        AbstractFileIO fileIO = new AbstractFileIO();
        fileIO.uploadFile(fileName);
        AbstractPage.sleep(2);

        clickSubmit();
        AbstractPage.sleep(15);
        // add refresh code here
        // AbstractPage.sleep(30);

      } catch (Exception e) {
        if (e.toString().contains("UnreachableBrowserException")) {
          AbstractPage.sleep(100);
          JLog.screenCapture();
        }
      }
      if (msgType.equals("validationError")) {
        page.browserSession.getDriver().switchTo().defaultContent();
        MTCMController c = new MTCMController();
        String actMsgTitle = c.getSuccessMessage();
        JLog.write("Upload validation message found: " + actMsgTitle);
        // Check for error indication in toast notification or upload error div
        boolean hasErrorInToast = !actMsgTitle.equals("No Message Found")
            && (actMsgTitle.contains("Failed to upload file") || actMsgTitle.contains("Error")
                || actMsgTitle.contains("error") || actMsgTitle.contains("Validation"));
        boolean hasUploadError = page.isUploadStatusError();
        // Check error details in the validation results table
        List<WebElement> elements = page.getList(By.xpath("//tr[1]//td"));
        WebElement errorEle = page.uploadStatusError();
        boolean hasValidationRows = !elements.isEmpty()
            && elements.size() > 1
            && elements.get(0).getText().toLowerCase().contains("error");
        boolean hasErrorDiv = errorEle != null && !errorEle.getText().trim().isEmpty();
        // Treat upload as correctly rejected if ANY of: toast/error-div/validation-table indicates error.
        // (Previously only the toast/uploadStatusError were checked, causing spurious failures
        // when the UI silently rendered the validation table without a toast banner.)
        Verify.verify(hasErrorInToast || hasUploadError || hasValidationRows || hasErrorDiv,
            "Cannot verify main error message - no error indication found (toast='" + actMsgTitle
                + "', uploadStatusError=" + hasUploadError + ", validationRows=" + elements.size()
                + ", errorDiv=" + (errorEle != null) + ")");
        if (!elements.isEmpty()) {
          if (elements.size() > 1) {
            Verify.verify(elements.get(0).getText().toLowerCase().contains("error"),
                "Cannot verify Message Type as error: " + elements.get(0).getText());
            String actMsg = elements.get(1).getText();
            Verify.verify(message == null || message.isEmpty() || actMsg.contains(message),
                "Cannot verify validation error message: " + actMsg);
          }
        } else if (errorEle != null) {
          String errorText = errorEle.getText();
          Verify.verify(message == null || message.isEmpty() || errorText.contains(message),
              "Cannot verify validation error message: " + errorText);
        }
        return;
      }
      WebElement ele = null;
      try {
        ele = page.get(By.xpath("//a[contains(@href,'goViewJob')]"));
      } catch (Exception goViewJobEx) {
        JLog.write("goViewJob link not found - upload may have been rejected at form level: " + goViewJobEx.getMessage());
        JLog.resetErrorCount();
      }
      if (ele != null) {
        ele.click();
        AbstractPage.sleep(20);
      } else {
        // Upload rejected at form level (no job created): check validation errors on form
        page.browserSession.getDriver().switchTo().defaultContent();
        MTCMController mtcm = new MTCMController();
        String actMsgTitle = mtcm.getSuccessMessage();
        JLog.write("Upload form-level rejection (no goViewJob). Message: " + actMsgTitle);
        boolean hasErrorInToast = !actMsgTitle.equals("No Message Found")
            && (actMsgTitle.contains("Failed to upload file") || actMsgTitle.contains("Error")
                || actMsgTitle.contains("error") || actMsgTitle.contains("Validation"));
        boolean hasUploadError = page.isUploadStatusError();
        JLog.resetErrorCount();
        if (msgType.equals("success")) {
          if (hasErrorInToast || hasUploadError) {
            JLog.error("Upload expected to succeed but was rejected with error: " + actMsgTitle, TakeScreenshot.True);
          }
        } else {
          // Expected to fail - verify error details from the validation results table
          List<WebElement> elements = page.getList(By.xpath("//tr[1]//td"));
          JLog.resetErrorCount();
          if (elements.size() > 1) {
            if (!message.isEmpty()) {
              String typeCell = elements.get(0).getText();
              if (!typeCell.contains("Error")) {
                JLog.error("Cannot verify Message Type as error (got: " + typeCell + ")", TakeScreenshot.True);
              }
              String actMsg = elements.get(1).getText();
              if (!actMsg.contains(message)) {
                JLog.error("Cannot verify validation error message: got '" + actMsg + "', expected '" + message + "'", TakeScreenshot.True);
              } else {
                JLog.write("Verified error message '" + message + "'");
              }
            } else {
              JLog.write("Upload correctly ended with form-level error (no message to verify).");
            }
          } else {
            // Fallback: check uploadStatusError div
            WebElement errorEle = page.uploadStatusError();
            JLog.resetErrorCount();
            if (errorEle != null) {
              String errorText = errorEle.getText();
              if (!message.isEmpty()) {
                if (!errorText.contains(message)) {
                  JLog.error("Cannot verify validation error: got '" + errorText + "', expected '" + message + "'", TakeScreenshot.True);
                } else {
                  JLog.write("Verified error message from uploadStatusError div.");
                }
              }
            } else if (!message.isEmpty() && hasErrorInToast) {
              if (!actMsgTitle.contains(message)) {
                JLog.error("Cannot verify error in toast: got '" + actMsgTitle + "', expected '" + message + "'", TakeScreenshot.True);
              } else {
                JLog.write("Verified error message from toast.");
              }
            } else if (!message.isEmpty()) {
              JLog.error("Cannot verify error message '" + message + "': no error table, no error div, no error toast found.", TakeScreenshot.True);
            }
          }
        }
        return;
      }

      String statusMsg = getUploadStatusMsg();
      if (statusMsg.contains("PENDING")) {
        MTCMController c = new MTCMController();
        c.clickIconButton("refresh");
        AbstractPage.sleep(10);
      }

      statusMsg = getUploadStatusMsg();
      if (statusMsg.contains("ERROR")) {
        if (msgType.equals("success"))
          JLog.error(statusMsg, TakeScreenshot.True);
        UploadErrorsController uploadErrorsController = new UploadErrorsController();
        boolean s = uploadErrorsController.printAndVerify(msgType, message);
        Verify.verify(s, "Cannot verify error message " + message + "as expected.");
        JLog.write("Verified the error message '" + message + "'");
      } else if (statusMsg.contains("SUCCESS")) {
        JLog.write(statusMsg);
        if (msgType.equals("error")) {
          JLog.error("Upload getting " + statusMsg + " for wrong input file", TakeScreenshot.True);
          JLog.fail("Upload getting " + statusMsg + " for wrong input file");
        }
        String actStatusFromUploadUI = page
            .get(By.xpath("//div[contains(text(),'Status')]/following-sibling::div/b")).getText();
        Verify.verify(actStatusFromUploadUI.equals(statusMsg), "Upload is ended with error!");
        JLog.write("Upload success!");
        MTCMController c = new MTCMController();
        Verify.verify(!c.getErrorMsg(), "Unexpected error occurred!");
        JLog.write("Verified that no unexpected error message is displayed.");
      } else if (statusMsg.contains("WARNING")) {
        if (!msgType.equals("warning")) {
          JLog.error(statusMsg);
        }
        WebElement e = page.get(By.xpath("//tr//td[3]"));
        String actWarningMsgFromUI = e.getText();
        if (actWarningMsgFromUI.equals("")) {
          actWarningMsgFromUI = e.getDomAttribute("innerText");
        }
        Verify.verify(actWarningMsgFromUI.equals(statusMsg), "Upload is ended with error!");
      } else if (statusMsg.contains("PENDING")) {
        MTCMController c = new MTCMController();
        c.clickIconButton("refresh");
        AbstractPage.sleep(10);
        Verify.verify(c.getErrorMsg(), "Unexpected error occurred!");
        JLog.fail("Caught an unexpected error while uploading...");
      }
      UploadPage page = new UploadPage();
      WebElement e = page.getManageUploadJobDetails("Job ID");
      if (e == null) {
        JLog.write("Job ID element not found on page - skipping Manage Upload Jobs verification.");
        return;
      }
      String jobID = e.getText();
      if (jobID.equals("")) {
        jobID = e.getDomAttribute("innerText");
      }
      HarmonyLoginUI ui = new HarmonyLoginUI();
      ui.navHarmonyMTCM("Upload/Manage Jobs", "Manage Upload Jobs");
      General gen = new General();
      gen.clickButton("Clear");
      gen.enterTextFieldVal(jobID, "jobId");
      gen.clickButton("Apply");
      gen.verifyRows("1", "selectedPageKeys");
      gen.isCoulmnValueVisible("Status", msgType.toUpperCase());
      gen.isCoulmnValueVisible("State", "COMPLETED");
      InputStream fo1 = new FileInputStream(
          prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
      Properties p1 = new Properties();
      p1.load(fo1);
      String logUN = p1.getProperty("login");
      gen.isCoulmnValueVisible("Loaded By", logUN);
      gen.isCoulmnValueVisible("Job ID", jobID);
      gen.verifyDeleteBtnDisplayedEnabled();
      gen.verifyEditBtnEnabledState();
      JLog.write("Successfully verified uploaded jobs details on Manage Upload page.");
      gen.checkForErrors();
    } else {
      JLog.error("Unable to find file: " + fileName);
    }

  }

  public String generate(String filePath, String action) throws Throwable {

    FileInputStream inputFile = null;
    XSSFWorkbook masterWorkbook = null;

    try {
      inputFile = new FileInputStream(new File(filePath));
      masterWorkbook = new XSSFWorkbook(inputFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (inputFile != null) {
        try { inputFile.close(); } catch (IOException e) { /* ignore */ }
      }
    }

    XSSFSheet updateSheet = masterWorkbook != null ? masterWorkbook.getSheetAt(0) : null;
    XSSFCell cell = null;

    try {

      if (filePath.contains("FGUPload")) {
        masterWorkbook = updateFGUploadFile(masterWorkbook, action);
      } else if (filePath.contains("uploadFG") && action.contains("fgDup")) {
        masterWorkbook = fgForDupFGCheck(masterWorkbook, action);
      } else if (filePath.contains("uploadFG") && action.contains("createDupFG")) {
        masterWorkbook = createDupFG(masterWorkbook, action);
      } else if (filePath.contains("uploadFG") && action.contains("existingFGWithNonExistingItem")) {
        masterWorkbook = existingFGWithNonExistingItem(masterWorkbook, action);
      } else if (filePath.contains("uploadFG") && !action.contains("fgDup")) {
        masterWorkbook = fgForNoItemPart(masterWorkbook, action);
      } else if (filePath.contains("uploadFG") && action.contains("caseSensitivefgDup")) {
        masterWorkbook = caseSensitivefgDup(masterWorkbook, action);
      } else if (filePath.contains("massUpdateCRFGInvalid") && action.contains("massUpdateCRBy")) {
        masterWorkbook = updateMassUpdateCRFGPGInvalid(filePath, masterWorkbook, action);
      } else if (filePath.contains("massUploadCRSell") && action.contains("Currency")) {
        masterWorkbook = updateMassUpdateCRInvalidCurrency(filePath, masterWorkbook, action);
      } else if (filePath.contains("massUploadCRSell") && action.contains("massUpdateCRValid")) {
        masterWorkbook = updateMassUpdateCRValid(filePath, masterWorkbook, action);
      } else if (filePath.contains("massUploadCRSell") && action.contains("massUpdateCRCurrInvalid")) {
        masterWorkbook = massUpdateCRCurrInvalid(filePath, masterWorkbook, action);
      } else if (filePath.contains("massUploadCRBuy") && action.equals("massUpdateSuperGCMCR")) {
        masterWorkbook = massUpdateSuperGCMCR(filePath, masterWorkbook, action);
      } else if (filePath.contains("massUploadCRBuy") && action.contains("massUpdateCRValid")) {
        masterWorkbook = updateMassUpdateBuyCRValid(filePath, masterWorkbook, action);
      } else if (filePath.contains("ParentUpload")) {
        masterWorkbook = updatePGUploadFile(masterWorkbook, action);
      } else if (filePath.contains("ApprovedForecast") && action.equals("uploadItemForForecastbyPFGGCM")) {
        masterWorkbook = uploadapprovedforecastbyparentFGGcm(masterWorkbook, action);

      } else if (filePath.contains("XWAPCostRecord") && action.equals("uploadForXWAPcostrecord")) {
        masterWorkbook = uploadCRWAP(masterWorkbook, action);

      } else if (filePath.contains("ApprovedForecast") && action.equals("uploadItemForForecastbyPFGSGCM")) {
        masterWorkbook = uploadapprovedforecastbyparentFGSGcm(masterWorkbook, action);

      } else if (filePath.contains("ApprovedForecast") && action.equals("uploadItemForForecastbyPFGadm")) {
        masterWorkbook = uploadapprovedforecastbyparentFGadm(masterWorkbook, action);

      } else if (filePath.contains("ApprovedForecast") && action.equals("uploadItemForForecastbyFGGCM")) {
        masterWorkbook = uploadapprovedforecastbyFGGCM(masterWorkbook, action);

      } else if (filePath.contains("ApprovedForecast") && action.equals("uploadItemForForecastbyFGSuperGCM")) {
        masterWorkbook = uploadapprovedforecastbyFGSuperGCM(masterWorkbook, action);

      } else if (filePath.contains("ApprovedForecast") && action.equals("uploadItemForForecastbyFGadm")) {
        masterWorkbook = uploadapprovedforecastbyFGadm(masterWorkbook, action);

      } else if (filePath.contains("MultipleitemUplaodforForecast")
          && action.equals("uploadmultipleItemForForecast")) {
        masterWorkbook = uploadmultipleitemforforecast(masterWorkbook, action);

      } else if (filePath.contains("ItemUploadForCommodity") && action.equals("uploadItemForPriceTAMCommodity")) {
        masterWorkbook = uploaditemforXLOBPriceTAMCommodity(masterWorkbook, action);
      }

      else if (filePath.contains("EMCItemUpload") && action.equals("emcuploadItemForPriceTAM")) {
        masterWorkbook = uploaditemforXLOBPriceTAM(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && action.equals("delluploadItemForPriceTAMQuarterly")) {
        masterWorkbook = uploaditemforXLOBPriceTAM(masterWorkbook, action);
      }

      else if (filePath.contains("ItemUploadForCR") && action.equals("delluploadItemForPriceTAMMonthly")) {
        masterWorkbook = uploaditemforXLOBPriceTAM(masterWorkbook, action);
      }

      else if (filePath.contains("ItemUploadForCR") && action.equals("delluploadItemForPriceTAM")) {
        masterWorkbook = uploaditemforXLOBPriceTAM(masterWorkbook, action);

      } else if (filePath.contains("ItemUploadforXLOB") && action.equals("XLOBDeleteTemplateItems")) {
        masterWorkbook = uploaditemforXLOBPriceTAM(masterWorkbook, action);
      }

      else if (filePath.contains("ItemUploadForCR") && action.equals("uploadItemForPriceTAM")) {
        masterWorkbook = uploaditemforXLOBPriceTAM(masterWorkbook, action);
      }

      else if (filePath.contains("ItemUploadForCR") && action.equals("uploadItemForXWAPCR")) {
        masterWorkbook = uploaditemforCRXWAP(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && action.equals("uploadItemForWAPCR")) {
        masterWorkbook = uploaditemforCRXWAP(masterWorkbook, action);
      } else if (filePath.contains("ItemForSuppCR") && action.contains("updateItemsForSupplierCreate")) {
        masterWorkbook = updateItemsForSupplierCRCreate(masterWorkbook, action);
      } else if (filePath.contains("ItemForEMCR") && action.contains("updateItemsForEMCreate")) {
        masterWorkbook = updateItemsForEMCRCreate(masterWorkbook, action);
      } else if (filePath.contains("CRWAPNewRecord") && action.equals("uploadPendingWAPCR")) {
        masterWorkbook = uploadCRWAP(masterWorkbook, action);
      } else if (filePath.contains("CRWAPApprovedRecord") && action.equals("uploadApprovedWAPCR")) {
        masterWorkbook = uploadCRWAP(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && (action.contains("CRApprove"))) {
        masterWorkbook = updateItemsForCRException(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && (!action.contains("CRCreate")
          && (!action.contains("StatusCheck")) && (!action.contains("MPN")))) {
        masterWorkbook = updateItemsUploadFile(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && action.contains("CRCreate")
          && (!action.contains("MPN"))) {
        masterWorkbook = updateItemsForCRCreate(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && action.contains("StatusCheck")
          && (!action.contains("MPN"))) {
        masterWorkbook = updateItemsForCRStatus(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && action.equals("massUpdateCRs")) {
        masterWorkbook = updateItemsForCRStatus(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && action.contains("uploadItemForMultipleCRMPN")) {
        masterWorkbook = uploadMultipleItemForCRMPN(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && action.contains("uploadItemForCRMPN")) {
        masterWorkbook = uploadItemForCRMPN(filePath, masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForCR") && action.contains("uploadItemForODMBuyCRMPN")) {
        masterWorkbook = uploadItemForCRMPN(filePath, masterWorkbook, action);
      } else if (filePath.contains("mpnUpload") && action.contains("uploadMultipleItem")) {
        masterWorkbook = uploadMultipleItemForCRMPN(masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForSearchItem")
          && !filePath.contains("ItemUploadForSearchItemAVL")) {
        masterWorkbook = updateItemsForSearchItemScreen(filePath, masterWorkbook, action);
      } else if (filePath.contains("phantomItem")) {
        uploadPhantomItem(filePath, masterWorkbook, action);
      } else if (filePath.contains("ItemUploadForSearchItemAVL")) {
        masterWorkbook = updateItemsForSearchItemAVLScreen(masterWorkbook, action);
      } else if (filePath.contains("itemsForMassUpdate")) {
        masterWorkbook = updateItemsForMassUpdate(masterWorkbook, action);
      } else if (filePath.contains("fgsForMassUpdate")) {
        masterWorkbook = updatFGForMassUpdate(masterWorkbook, action);
      } else if (filePath.contains("parentForMassUpdate")) {
        masterWorkbook = updateParentForMassUpdate(masterWorkbook, action);
      } else if (filePath.contains("ItemUpload")) {
        if (action.equals("uploadItemForTAM")) {
          cell = updateSheet.getRow(2).getCell(0);
          if (cell == null) {
            cell = updateSheet.getRow(2).createCell(0);
          }
          cell.setCellType(CellType.STRING);
          cell.setCellValue(itemNumber);
        } else if (action.equals("uploadItemForTAMRegionSite")) {
          cell = updateSheet.getRow(2).getCell(0);
          if (cell == null) {
            cell = updateSheet.getRow(2).createCell(0);
          }
          cell.setCellType(CellType.STRING);
          cell.setCellValue("itemRegionAlloc" + timeStamp);
        } else if (action.equals("uploadItemForTAMDelete")) {
          cell = updateSheet.getRow(2).getCell(0);
          if (cell == null) {
            cell = updateSheet.getRow(2).createCell(0);
          }
          cell.setCellType(CellType.STRING);
          cell.setCellValue("AutoItemAllocDel" + timeStamp);
        }
      } else if (filePath.contains("ApprovedForecast") && !(filePath.contains("_Validations"))) {
        if (action.equals("uploadItemForForecast")) {
          cell = updateSheet.getRow(3).getCell(13);
          if (cell == null) {
            cell = updateSheet.getRow(3).createCell(13);
          }
          cell.setCellType(CellType.STRING);
          cell.setCellValue(itemNumber);
          for (int j = 20; j <= 31; j++) {
            cell = updateSheet.getRow(3).getCell(j);
            if (cell == null) {
              cell = updateSheet.getRow(3).createCell(j);
            }
            cell.setCellType(CellType.STRING);
            cell.setBlank();
          }
          for (int j = 32; j <= 44; j++) {
            cell = updateSheet.getRow(3).getCell(j);
            if (cell == null) {
              cell = updateSheet.getRow(3).createCell(j);
            }
            cell.setCellType(CellType.STRING);
            cell.setCellValue("100");
          }
          // if (filePath.contains("_Validations")) {
          // for (int j = 18; j <= 42; j++) {
          // cell = updateSheet.getRow(3).getCell(j);
          // cell.setCellType(CellType.STRING);
          // cell.setBlank();
          // }
          // }
        }

      } else if (filePath.contains("ApprovedForecast") && (filePath.contains("_Validations"))) {
        if (action.equals("uploadItemForForecast")) {
          cell = updateSheet.getRow(3).getCell(13);
          if (cell == null) {
            cell = updateSheet.getRow(3).createCell(13);
          }
          cell.setCellType(CellType.STRING);
          cell.setCellValue(itemNumber);
          for (int j = 20; j <= 31; j++) {
            cell = updateSheet.getRow(3).getCell(j);
            if (cell == null) {
              cell = updateSheet.getRow(3).createCell(j);
            }
            cell.setCellType(CellType.STRING);
            cell.setBlank();
          }
          if (filePath.contains("_Validations")) {
            for (int j = 18; j <= 42; j++) {
              cell = updateSheet.getRow(3).getCell(j);
              if (cell == null) {
                cell = updateSheet.getRow(3).createCell(j);
              }
              cell.setCellType(CellType.STRING);
              cell.setBlank();
            }
          }
        }

      } else if (filePath.contains("AdjustableForecast")) {
        if (action.equals("uploadAdjustableForecast")) {
          cell = updateSheet.getRow(3).getCell(12);
          if (cell == null) {
            cell = updateSheet.getRow(3).createCell(12);
          }
          cell.setCellType(CellType.STRING);
          cell.setCellValue("25");
          cell = updateSheet.getRow(3).getCell(13);
          if (cell == null) {
            cell = updateSheet.getRow(3).createCell(13);
          }
          cell.setCellType(CellType.STRING);
          cell.setCellValue(itemNumber);
        }
      } else if (filePath.contains("itemForTam")) {
        String item = "itemMultiple";
        if (actionType.contains("Alloc")) {
          item = "itemMulti";
        }
        for (int i = 0; i < 5; i++) {
          cell = updateSheet.getRow(i + 2).getCell(0);
          cell.setCellType(CellType.STRING);
          cell.setCellValue(item + timeStamp + (i + 1));
        }
        cell = updateSheet.getRow(4).getCell(13);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("PROCESSOR");
        cell = updateSheet.getRow(5).getCell(13);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("PROCESSOR");
        cell = updateSheet.getRow(6).getCell(13);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("PROCESSOR");
      } else if (filePath.contains("groupForTam")) {
        for (int i = 0; i < 5; i++) {
          cell = updateSheet.getRow(i + 2).getCell(0);
          cell.setCellType(CellType.STRING);
          cell.setCellValue("AutoMultipleItemFG" + timeStamp);
          cell = updateSheet.getRow(i + 2).getCell(3);
          cell.setCellType(CellType.STRING);
          cell.setCellValue("itemMultiple" + timeStamp + i + 1);
          updateSheet.getRow(i + 2).createCell(8);
          cell = updateSheet.getRow(i + 2).getCell(8);
          cell.setCellType(CellType.STRING);
          cell.setCellValue("ADD");
        }
      } else if (filePath.contains("newSL")) {
        uploadNewSL(filePath, masterWorkbook, action);
      } else if (filePath.contains("uploadCRs") && action.contains("uploadCostRecords"))
        addNewCRsForGCM(filePath, action, masterWorkbook);
      else if (filePath.contains("massApproveRejectCRsAsGCM") && action.equals("approveCR"))
        massApproveRejectCRsAsGCM(filePath, masterWorkbook, action);
      else if (filePath.contains("newCR")) {
        uploadNewCR(filePath, masterWorkbook, action);
      } else if (filePath.contains("massApproveCR")) {
        massApproveCR(filePath, masterWorkbook, action);
      } else if (filePath.contains("CRException")) {
        CellStyle cellStyle = masterWorkbook.createCellStyle();
        CreationHelper createHelper = masterWorkbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));

        String sheetName = null;
        XSSFSheet sheet = masterWorkbook.getSheetAt(0);
        ExcelReader er = new ExcelReader(filePath, sheetName);

        Cell itemCell = er.findCellContaining(".*Item");
        Cell costTypeCell = er.findCellContaining(".*Cost Type");
        Cell resp = er.findCellContaining(".*PRODUCTION Responsibility");
        // Cell supplier = er.findCellContaining(".*Source Site");
        Cell supplier = er.findCellContaining(".*Supplier");
        Cell reasonCodeCell = er.findCellContaining(".*Reason Code");
        Cell startDate = er.findCellContaining(".*Start Date");
        // Cell endDate = er.findCellContaining(".*End Date");

        if (action.contains("uploadExcepForNoExistingCR")
            || action.contains("uploadExcepForExistingCRSameDateRange")) {

          // String item;
          // item = ;
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue("itemCRApprove" + getTimeStamp());

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);
            cell.setCellValue("ODMBUY");

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();
          }
        } else if (action.contains("uploadExcepForSearchCRApprove")) {
          String item;
          item = "itemCRSearchApprove" + getTimeStamp();

          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);
            cell.setCellValue("ODMBUY");
          }
        } else if (action.contains("uploadExcepForSearchCRForWithDraw")) {
          String item;
          item = "itemCREmail" + getTimeStamp();

          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellType(CellType.STRING);
            if (!action.contains("NonAllowableCT")) {

              cell.setCellValue("ODMBUY");
            } else {
              cell.setCellValue("BUY");

            }
          }

        } else if (action.contains("uploadExcepForSearchCRWithDraw")) {
          String item;
          item = "itemCRSearchApprove" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellType(CellType.STRING);
            cell.setCellValue("ODMBUY");

          }

        } else if (action.contains("uploadExcepForNoReasonCode")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();

          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(reasonCodeCell.getColumnIndex());
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
          }

        } else if (action.contains("uploadExcepForItem")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();

          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(resp.getColumnIndex());
            cell.setCellType(CellType.STRING);
            if (action.contains("NoResp")) {
              cell.setCellValue("");
            } else if (action.contains("NoProd")) {
              cell.setCellValue("jessica_sh_ho_gcm");
            } else if (action.contains("WithProd")) {
              cell.setCellValue("mike_quick@dell.com");
            }
          }
        } else if (action.contains("uploadExcepForInvalidCostType")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();

          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);
            cell.setCellValue("ODM");
          }
        } else if (action.contains("uploadExcepForDiffCostType")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();

          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);
            cell.setCellValue("WAP");
          }
        } else if (action.contains("uploadExcepForDiffTypeCR")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            // cell =
            // sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            // cell.setCellStyle(cellStyle);
            // cell.setCellValue("ODMBUY");

            cell = sheet.getRow(i).getCell(reasonCodeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);
            cell.setCellValue("OTHERS");
          }

        } else if (action.contains("uploadExcepForNoExistingItem")) {
          String item;
          item = "noExistingItem";
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();
          }

        } else if (action.contains("uploadExcepForEmptyDates")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);
          }

        } else if (action.contains("uploadExcepForWrongCT")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);
            cell.setCellValue("BUY");
          }

        } else if (action.contains("uploadExcepForWrongSupp")) {
          String item;
          // Use the base item name (not timestamp-based) so the item exists in the DB
          // and the server validates the source site, not item existence
          item = "itemCRApprove";
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);

            if (action.contains("SuppBUY"))
              cell.setCellValue("BUY");
            else
              cell.setCellValue("ODMBUY");
          }

        } else if (action.contains("uploadExcepForCREnhancedValidation")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);
            cell.setCellValue("BUY");
          }

        } else if (action.contains("uploadExcepForExistingCRWithinDateRange")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            // cell =
            // sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            // cell.setCellStyle(cellStyle);
            // cell.setCellValue("BUY");
          }

        } else if (action.contains("uploadExcepForExistingCROutsideDateRange")) {
          String item;
          item = "itemCRApprove" + getTimeStamp();

          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            LocalDate today = LocalDate.now();
            cell.setCellValue(today.minusDays(2));
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(today.minusDays(1));
          }
        } else if (action.contains("uploadExcepForNewCRApprove")) {
          String item;
          item = "itemEditCR" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();
          }
        } else if (action.contains("uploadMultiLevelExcepForEmailAttach")) {
          String item;
          item = "itemMultiLevelApprove" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();

            cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
            cell.setCellStyle(cellStyle);
            cell.setCellValue("ODMBUY");
          }
        }
        if (action.contains("uploadExcepForEmailAttach")) {
          String item;
          item = "itemCREmail" + getTimeStamp();
          for (int i = itemCell.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
            cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
            cell.setCellValue(item);

            if (action.contains("Sell")) {
              cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
              cell.setCellStyle(cellStyle);
              cell.setCellValue("SELL");

              cell = sheet.getRow(i).getCell(supplier.getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*SLC").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*WARRANTY").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*COM").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*TRANSFORMATION").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*PACKAGING").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*NRE").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*TOOLING").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*COM").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*ROIC").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*OTHER").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i)
                  .getCell(er.findCellContaining(".*Supplier Entity Type").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*Source Site").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*Air %").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*AIR_Value").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*Land Value").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*Land %").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*Sea Value").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

              cell = sheet.getRow(i).getCell(er.findCellContaining(".*Sea %").getColumnIndex());
              cell.setCellStyle(cellStyle);
              // cell.setCellValue("");
              cell.setBlank();

            } else {
              cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
              cell.setCellStyle(cellStyle);
              cell.setCellValue("ODMBUY");
            }
            cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);

            cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
            cell.setCellStyle(cellStyle);
            cell.setBlank();
          }
        }
      } else if (filePath.contains("approveRejectCRs")
          && (action.equals("ApproveCR") || action.equals("RejectCR"))) {
        approveRejectCR(filePath, masterWorkbook, action);
      } else if (filePath.contains("TamSupplierAlloc")) {
        suppAlloc(filePath, masterWorkbook, action);
      }
    } catch (Exception e) {
      JLog.fail("Error occurred -> " + e.toString());
    }

    FileOutputStream outFile = null;
    try {
      outFile = new FileOutputStream(new File(filePath));
      masterWorkbook.write(outFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e1) {
      e1.printStackTrace();
    } finally {
      if (outFile != null) {
        try { outFile.close(); } catch (IOException e) { /* ignore */ }
      }
    }
    return filePath;
  }

  public String getTimeStamp() {
    // return "220220182106";
    // return "220731182646";
    return timeStamp;
  }

  public void suppAlloc(String filepth, XSSFWorkbook mainWorkBook, String action)
      throws InvalidFormatException, IOException {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkBook.getSheetAt(0);
    String item = null;
    String sheetName = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);

    Cell c = er.findCellContaining(".*Item");

    if (action.contains("oneRow")) {
      for (int i = c.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
        cell = sheet.getRow(i).getCell(c.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item + getTimeStamp());
      }
    }
  }

  public void uploadNewSL(String filepth, XSSFWorkbook mainWorkBook, String action)
      throws InvalidFormatException, IOException {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkBook.getSheetAt(0);
    String item = null;
    String destnSite = "APCC";

    if (action.contains("Admin")) {
      item = "CRMPN";
    } else if (action.contains("SuperGCM")) {
      item = "CRStatus";
    } else if (action.contains("CRGCM")) {
      item = "CRODMBuyMPN";
    } else if (action.contains("CRSupp")) {
      item = "CRSuppCreate";
    }
    String sheetName = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);

    Cell c = er.findCellContaining(".*Item");
    Cell destn = er.findCellContaining(".*Destination Site");
    for (int i = c.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(c.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue(item + getTimeStamp());
      cell = sheet.getRow(i).getCell(destn.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue(destnSite);
    }

  }

  public void uploadNewCR(String filepth, XSSFWorkbook mainWorkBook, String action) throws Throwable {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkBook.getSheetAt(0);
    String item = null;
    String costType = "BUY";
    String destnSite = "APCC";
    if (action.contains("SLAdmin")) {
      item = "CRMPN";
    } else if (action.contains("SuperGCM")) {
      item = "CR";
      costType = "ODMBUY";
      destnSite = "CCC";
    } else if (action.contains("ServiceGCM")) {
      item = "CRCreate";
      costType = "SERVICE";
      destnSite = "CCC-CCC";
    } else if (action.contains("CRBusAdmin")) {
      item = "CRCreate";
      costType = "BUY";
      destnSite = "CCC-CCC";
    } else if (action.contains("CRSupplier")) {
      item = "CRSuppCreate";
      costType = "BUY";
    } else if (action.contains("CREM")) {
      item = "CREMCreate";
      costType = "BUY";
    } else if (action.contains("CRGCM")) {
      // item = "CRODMBuyMPN";
      addNewCRsForGCM(filepth, action, mainWorkBook);
      return;
    } else if (action.contains("ForGCM")) {
      item = "CRODMBuyMPN";
    }
    String sheetName = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);

    Cell c = er.findCellContaining(".*Item");
    Cell costTypeCell = er.findCellContaining(".*Cost Type");
    Cell destn = er.findCellContaining(".*Destination Site");
    Cell costStatus = er.findCellContaining(".*Cost Status");
    Cell supplier = er.findCellContaining(".*Source Site");
    Cell sourceSite = er.findCellContaining(".*Supplier");
    Cell currency = er.findCellContaining(".*Currency");

    for (int i = c.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(c.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue(item + getTimeStamp());
      cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue(costType);
      cell = sheet.getRow(i).getCell(destn.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue(destnSite);

      if (action.contains("CREM")) {
        cell = sheet.getRow(i).getCell(supplier.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WISTRON (ACER PERIPHERALS)");
        cell = sheet.getRow(i).getCell(sourceSite.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("NL-WISTRON-CNY CNY");
        cell = sheet.getRow(i).getCell(destn.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("DOMOCSite1 desc");
        cell = sheet.getRow(i).getCell(currency.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("CNY");
      }
      if (action.contains("CRSupplier")) {
        cell = sheet.getRow(i).getCell(supplier.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("DSSMITH");
        cell = sheet.getRow(i).getCell(sourceSite.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("NL-DSSMITH-PLN PLN");
        cell = sheet.getRow(i).getCell(destn.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("DOMOCSite1 desc");
        cell = sheet.getRow(i).getCell(currency.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("PLN");
        // cell = sheet.getRow(i).getCell(costStatus.getColumnIndex());
        // cell.setCellType(CellType.STRING);
        // cell.setCellValue("PENDING");
      }
    }
  }

  public void addNewCRsForGCM(String filepth, String action, XSSFWorkbook mainWorkBook) throws Throwable {
    String item = "";
    String sheetName = null;
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkBook.getSheetAt(0);
    ExcelReader er = new ExcelReader(filepth, sheetName);

    Cell c = er.findCellContaining(".*Item");
    Cell costTypeCell = er.findCellContaining(".*Cost Type");
    Cell destn = er.findCellContaining(".*Destination Site");
    Cell costStatus = er.findCellContaining(".*Cost Status");
    Cell itemDescp = er.findCellContaining(".*Item Description");
    Cell supplier = er.findCellContaining(".*Supplier");
    Cell sourceSite = er.findCellContaining(".*Source Site");
    Cell startDate = er.findCellContaining(".*Start Date");
    // Cell endDate = er.findCellContaining(".*End Date");
    item = "CRODMBuyMPN";
    String[] destnSites = { "CCC", "CCC-CCC", "APCC-1234", "BCC" };

    if (action.contains("ForGCM")) {

      for (int i = c.getRowIndex() + 1, j = 0; i < er.getNumberOfRows(); i++, j++) {
        cell = sheet.getRow(i).getCell(c.getColumnIndex());
        cell.setCellValue(item + getTimeStamp());
        cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("BUY");
        cell = sheet.getRow(i).getCell(destn.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue(destnSites[j]);
        cell = sheet.getRow(i).getCell(costStatus.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("PENDING");

        // start date
        cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
        cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
        // end date
        cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
        cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(2).toDate()));
      }
    } else if (action.contains("Admin")) {

      // for (int i = c.getRowIndex() + 1, j = 0; i <
      // er.getNumberOfRows(); i++, j++) {
      int i = c.getRowIndex() + 1;
      cell = sheet.getRow(i).getCell(c.getColumnIndex());
      cell.setCellValue(item + "1" + getTimeStamp());
      cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("BUY");
      cell = sheet.getRow(i).getCell(destn.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("WW");
      // cell = sheet.getRow(i).getCell(costStatus.getColumnIndex());
      // cell.setCellType(CellType.STRING);
      // cell.setCellValue("PENDING");

      cell = sheet.getRow(i).getCell(supplier.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("HITACHI");
      cell = sheet.getRow(i).getCell(sourceSite.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("NL-HITACHI-USD");

      cell = sheet.getRow(i).getCell(itemDescp.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("SI,CUS,DIMM,256MB,PANASONIC");
      // start date
      cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      // end date
      HarmonyLoginUI ui = new HarmonyLoginUI();
      MTCMController mc = new MTCMController();
      String dateInString = mc.getFiscalMonthEndDate();
      ui.navHarmonyMTCM("Upload/Manage Jobs", "Pricing");
      mc = new MTCMController();
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
      Date date = formatter.parse(dateInString);
      cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
      cell.setCellValue(date);
      Row row;
      for (i = i + 1; i < er.getNumberOfRows(); i++) {
        row = sheet.getRow(i);
        sheet.removeRow(row);
        JLog.write("Removed row = " + row);
      }
    } else if (action.contains("ODMBUY")) {

      // for (int i = c.getRowIndex() + 1, j = 0; i <
      // er.getNumberOfRows(); i++, j++) {
      int i = c.getRowIndex() + 1;
      cell = sheet.getRow(i).getCell(c.getColumnIndex());
      cell.setCellValue(item + "2" + getTimeStamp());
      cell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ODMBUY");
      cell = sheet.getRow(i).getCell(destn.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("WW");
      // cell = sheet.getRow(i).getCell(costStatus.getColumnIndex());
      // cell.setCellType(CellType.STRING);
      // cell.setCellValue("PENDING");

      cell = sheet.getRow(i).getCell(supplier.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("HITACHI");
      cell = sheet.getRow(i).getCell(sourceSite.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("NL-HITACHI-USD");

      cell = sheet.getRow(i).getCell(itemDescp.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("SI,CUS,DIMM,256MB,PANASONIC");
      // start date
      cell = sheet.getRow(i).getCell(startDate.getColumnIndex());
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      // end date
      HarmonyLoginUI ui = new HarmonyLoginUI();
      MTCMController mc = new MTCMController();
      String dateInString = mc.getFiscalMonthEndDate();
      ui.navHarmonyMTCM("Upload/Manage Jobs", "Pricing");
      mc = new MTCMController();
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
      Date date = formatter.parse(dateInString);
      cell = sheet.getRow(i).getCell(startDate.getColumnIndex() + 1);
      cell.setCellValue(DateUtil.getExcelDate(date));
      Row row;
      for (i = i + 1; i < er.getNumberOfRows(); i++) {
        row = sheet.getRow(i);
        sheet.removeRow(row);
        JLog.write("Removed row = " + row);
      }
    }
  }

  public void massApproveRejectCRsAsGCM(String filepth, XSSFWorkbook mainWorkBook, String action) {
    String item = "CRODMBuyMPN";
    String sheetName = null;
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkBook.getSheetAt(0);
    ExcelReader er = new ExcelReader(filepth, sheetName);

    Cell c = er.findCellContaining(".*Item");
    Cell costStatus = er.findCellContaining(".*Cost Status");
    Cell costAction = er.findCellContaining(".*Cost Record Action");
    Cell destn = er.findCellContaining(".*Destination Site");
    Cell startDateCell = er.findCellContaining(".*Start Date");

    for (int i = c.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(c.getColumnIndex());
      cell.setCellValue(item + getTimeStamp());

      cell = sheet.getRow(i).getCell(costStatus.getColumnIndex());
      cell.setCellType(CellType.STRING);

      costAction = sheet.getRow(i).getCell(costAction.getColumnIndex());

      cell = sheet.getRow(i).getCell(destn.getColumnIndex());
      cell.setCellType(CellType.STRING);
      // cell.setCellValue("CCC");
      if (cell.getStringCellValue().contains("CCC")) {
        costStatus.setCellValue("PENDING");
        costAction.setCellValue("Approve");
      } else {
        costStatus.setCellValue("PENDING");
        costAction.setCellValue("Reject");
      }
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex());
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));

      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex() + 1);
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(2).toDate()));
    }
  }

  public XSSFWorkbook approveRejectCR(String filepth, XSSFWorkbook mainWorkBook, String action)
      throws InvalidFormatException, IOException {

    XSSFSheet sheet = mainWorkBook.getSheetAt(0);
    String sheetName = null;
    XSSFCell cell = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    UploadController up = new UploadController();
    Cell itemCell = er.findCellContaining(".*Item");
    Cell startDateCell = er.findCellContaining(".*Start Date");
    int headerRow = itemCell.getRowIndex();
    for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
      cell.setCellValue("CRCreate" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex());
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));

      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex() + 1);
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(2).toDate()));

      Cell c = er.findCellContaining(".*Parent Functional Group");
      // cell = sheet.getRow(i).getCell(c.getColumnIndex());
      // cell.setCellValue("testFGItemSupp" + up.getTimeStamp()); // fg

      cell = sheet.getRow(i).getCell(c.getColumnIndex() + 1);
      cell.setCellValue("testFGItemSupp" + up.getTimeStamp()); // fg

      Cell costTypeCell = er.findCellContaining(".*Cost Type");
      costTypeCell = sheet.getRow(i).getCell(costTypeCell.getColumnIndex());
      // costTypeCell.setCellType(CellType.STRING);
      costTypeCell.setCellValue("BUY");
      Cell costStatus = er.findCellContaining(".*Cost Status");
      costStatus = sheet.getRow(i).getCell(costStatus.getColumnIndex());
      Cell costAction = er.findCellContaining(".*Cost Record Action");
      costAction = sheet.getRow(i).getCell(costAction.getColumnIndex());
      Cell destn = er.findCellContaining(".*Destination Site");
      Cell mrpSite = er.findCellContaining(".*MRP Site");

      if (action.contains("Approve")) {
        costStatus.setCellValue("PENDING");
        costAction.setCellValue("Approve");
        cell = sheet.getRow(i).getCell(destn.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("CCC-CCC");
        cell = sheet.getRow(i).getCell(mrpSite.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("CCC");
        cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex() + 1);
        cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(2).toDate()));
      } else if (action.contains("Reject")) {
        costStatus.setCellValue("PENDING");
        costAction.setCellValue("Reject");
        cell = sheet.getRow(i).getCell(destn.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("CCC");
      }
    }
    return mainWorkBook;
  }

  public XSSFWorkbook massApproveCR(String filepth, XSSFWorkbook mainWorkbook, String action) {
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    String sheetName = null;
    XSSFCell cell = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    UploadController up = new UploadController();
    Cell itemCell = er.findCellContaining(".*Item");
    Cell startDateCell = er.findCellContaining(".*Start Date");
    int headerRow = itemCell.getRowIndex();
    for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
      cell.setCellValue("CRSuppCreate" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex());
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex() + 1);
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(2).toDate()));
      if (action.contains("Approve")) {
        Cell c = er.findCellContaining(".*Parent Functional Group");
        cell = sheet.getRow(i).getCell(c.getColumnIndex() + 1);
        cell.setCellValue("CRSuppGrp" + up.getTimeStamp()); // fg
      }
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateMassUpdateCRFGPGInvalid(String filepth, XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    String sheetName = null;
    XSSFCell cell = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    UploadController up = new UploadController();
    Cell itemCell = er.findCellContaining(".*Item");
    Cell pgCell = er.findCellContaining(".*Parent Functional Group");
    int headerRow = itemCell.getRowIndex();
    for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
      cell.setCellValue("CR" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(pgCell.getColumnIndex());
      cell.setCellValue("CRPG" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(pgCell.getColumnIndex() + 1);
      cell.setCellValue("CRGrp" + up.getTimeStamp());
    }

    return mainWorkbook;
  }

  public XSSFWorkbook updateMassUpdateCRInvalidCurrency(String filepth, XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    String sheetName = null;
    XSSFCell cell = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    UploadController up = new UploadController();
    Cell itemCell = er.findCellContaining(".*Item");
    Cell pgCell = er.findCellContaining(".*Parent Functional Group");
    Cell currCell = er.findCellContaining(".*Currency");
    int headerRow = itemCell.getRowIndex();
    for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
      cell.setCellValue("CR" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(pgCell.getColumnIndex());
      cell.setCellValue("CRPG" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(pgCell.getColumnIndex() + 1);
      cell.setCellValue("CRGrp" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(currCell.getColumnIndex() + 1);
      cell.setCellValue("EUR");
    }

    return mainWorkbook;
  }

  public XSSFWorkbook massUpdateCRCurrInvalid(String filepth, XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    String sheetName = null;
    XSSFCell cell = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    UploadController up = new UploadController();
    Cell itemCell = er.findCellContaining(".*Item");
    Cell pgCell = er.findCellContaining(".*Parent Functional Group");
    Cell currCell = er.findCellContaining(".*Currency");
    Cell startDateCell = er.findCellContaining(".*Start Date");
    int headerRow = itemCell.getRowIndex();
    for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
      cell.setCellValue("CR" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(pgCell.getColumnIndex());
      cell.setCellValue("CRPG" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(pgCell.getColumnIndex() + 1);
      cell.setCellValue("CRGrp" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(currCell.getColumnIndex());
      cell.setCellValue("EUR");
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex());
      // CreationHelper createHelper = mainWorkbook.getCreationHelper();
      // ((CellStyle)
      // cell).setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex() + 1);
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
    }

    return mainWorkbook;
  }

  public XSSFWorkbook updateMassUpdateCRValid(String filepth, XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    String sheetName = null;
    XSSFCell cell = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    UploadController up = new UploadController();
    Cell itemCell = er.findCellContaining(".*Item");
    Cell pgCell = er.findCellContaining(".*Parent Functional Group");
    Cell currCell = er.findCellContaining(".*Currency");
    Cell startDateCell = er.findCellContaining(".*Start Date");
    int headerRow = itemCell.getRowIndex();
    for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
      cell.setCellValue("CR" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(pgCell.getColumnIndex());
      cell.setCellValue("CRPG" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(pgCell.getColumnIndex() + 1);
      cell.setCellValue("CRGrp" + up.getTimeStamp());
      cell = sheet.getRow(i).getCell(currCell.getColumnIndex());
      cell.setCellValue("USD");
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex());
      // CreationHelper createHelper = mainWorkbook.getCreationHelper();
      // ((CellStyle)
      // cell).setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex() + 1);
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
    }

    return mainWorkbook;
  }

  public XSSFWorkbook updateMassUpdateBuyCRValid(String filepth, XSSFWorkbook mainWorkbook, String action)
      throws Throwable {
    HarmonyLoginUI ui = new HarmonyLoginUI();
    MTCMController mc = new MTCMController();
    String fiscalDate = mc.getFiscalMonthEndDate();
    ui.navHarmonyMTCM("Upload/Manage Jobs", "Pricing");
    mc = new MTCMController();

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    String sheetName = null;
    XSSFCell cell = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    Cell itemCell = er.findCellContaining(".*Item");
    Cell pgCell = er.findCellContaining(".*Parent Functional Group");
    Cell currCell = er.findCellContaining(".*Currency");
    Cell ctCell = er.findCellContaining(".*Cost Type");

    Cell startDateCell = er.findCellContaining(".*Start Date");
    Cell materialCell = er.findCellContaining(".*MATERIAL*");

    int headerRow = itemCell.getRowIndex();
    UploadController up = new UploadController();
    // cell.setCellValue(mc.getFiscalMonthEndDate());
    // ui.navHarmonyMTCM("Main", "Upload");
    // mc = new MTCMController();
    for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
      if (action.contains("NonDell"))
        cell.setCellValue("#857CM-0052");
      else if (action.contains("MultipleCR"))
        cell.setCellValue("CRODMBuyMPN" + up.getTimeStamp());
      else if (action.contains("Admin")) {
        cell.setCellValue("CRODMBuyMPN1" + up.getTimeStamp());
      } else if (action.contains("ODMBUY")) {
        cell.setCellValue("CRODMBuyMPN2" + up.getTimeStamp());
      }
      // cell = sheet.getRow(i).getCell(pgCell.getColumnIndex());
      // cell.setCellValue("");
      if (action.contains("ODMBUY")) {
        cell = sheet.getRow(i).getCell(pgCell.getColumnIndex() + 1);
        cell.setCellValue("UI201905241439");

        cell = sheet.getRow(i).getCell(pgCell.getColumnIndex());
        cell.setCellValue("ODMBUYParent");

        cell = sheet.getRow(i).getCell(ctCell.getColumnIndex());
        cell.setCellValue("ODMBUY");

      } else {
        cell = sheet.getRow(i).getCell(pgCell.getColumnIndex() + 1);
        cell.setCellValue("CYCFG_TEST004");

        cell = sheet.getRow(i).getCell(pgCell.getColumnIndex());
        cell.setCellValue("ParentTest202");
      }
      // cell = sheet.getRow(i).getCell(pgCell.getColumnIndex() + 1);
      // cell.setCellValue("CYCFG_TEST004");
      cell = sheet.getRow(i).getCell(currCell.getColumnIndex());
      cell.setCellValue("USD");
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex());
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));

      String dateInString = fiscalDate;
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
      Date date = formatter.parse(dateInString);
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex() + 1);
      cell.setCellValue(date);

      cell = sheet.getRow(i).getCell(materialCell.getColumnIndex());
      cell.setCellValue(10);
      // cell = sheet.getRow(i).getCell(materialCell.getColumnIndex()-1);
      // cell.

    }
    mainWorkbook.setForceFormulaRecalculation(true);
    return mainWorkbook;
  }

  public XSSFWorkbook massUpdateSuperGCMCR(String filepth, XSSFWorkbook mainWorkbook, String action)
      throws Throwable {
    // HarmonyLoginUI ui = new HarmonyLoginUI();
    // MTCMController mc = new MTCMController();
    // String fiscalDate = mc.getFiscalMonthEndDate();
    // ui.navHarmonyMTCM("Main", "Upload");
    // mc = new MTCMController();

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    String sheetName = null;
    XSSFCell cell = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    Cell itemCell = er.findCellContaining(".*Item");
    Cell costType = er.findCellContaining(".*Cost Type");
    Cell costStatus = er.findCellContaining(".*Cost Status");
    Cell costAction = er.findCellContaining(".*Cost Record Action");
    Cell currCell = er.findCellContaining(".*Currency");
    Cell startDateCell = er.findCellContaining(".*Start Date");
    int headerRow = itemCell.getRowIndex();
    UploadController up = new UploadController();
    for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
      cell = sheet.getRow(i).getCell(itemCell.getColumnIndex());
      cell.setCellValue("CRODMBuyMPN" + up.getTimeStamp());

      cell = sheet.getRow(i).getCell(costType.getColumnIndex());
      cell.setCellValue("BUY");

      cell = sheet.getRow(i).getCell(currCell.getColumnIndex());
      cell.setCellValue("USD");

      cell = sheet.getRow(i).getCell(costStatus.getColumnIndex());
      cell.setCellValue("CLOSED");

      // Cell itemDescp = er.findCellContaining(".*Item Description");
      // cell = sheet.getRow(i).getCell(itemDescp.getColumnIndex());
      // cell.setCellValue("Changed as part if BUY Mass Update");
      // if ((j + 1) == 1 || (j + 1) == 2) {
      //
      // } else {
      // cell.setCellValue("Reject");
      // }

      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex());
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell = sheet.getRow(i).getCell(startDateCell.getColumnIndex() + 1);
    }
    return mainWorkbook;
  }

  // public HSSFWorkbook updateMassUpdateCRPGInvalid(String filepth,
  // HSSFWorkbook mainWorkbook, String action) {
  //
  // HSSFSheet sheet = mainWorkbook.getSheetAt(0);
  // HSSFCell cell = null;
  // cell = sheet.getRow(2).getCell(2);
  // cell.setCellValue(action);
  // sheet.getRow(2).createCell(8);
  // cell = sheet.getRow(2).getCell(8);
  // cell.setCellValue("UPDATE");
  // sheet.removeRow(sheet.getRow(3));
  //
  // return mainWorkbook;
  // }

  public XSSFWorkbook fgForDupFGCheck(XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    XSSFCell cell = null;
    if (action.equals("fgDup")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellValue("testfgitemedit" + getTimeStamp());
      cell = sheet.getRow(2).getCell(3);
      cell.setCellValue("FGITEM1" + getTimeStamp());
      cell = sheet.getRow(2).getCell(6);
      cell.setCellValue("ACTIVE");
      sheet.getRow(2).createCell(8);
      cell = sheet.getRow(2).getCell(8);
      cell.setCellValue("ADD");
      // sheet.removeRow(sheet.getRow(3));
    }
    return mainWorkbook;
  }

  public XSSFWorkbook createDupFG(XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    XSSFCell cell = null;

    cell = sheet.getRow(2).getCell(0);
    cell.setCellValue("testFGItemEdit" + getTimeStamp());
    cell = sheet.getRow(2).getCell(3);
    cell.setCellValue("FGITEM" + getTimeStamp());
    cell = sheet.getRow(2).getCell(6);
    cell.setCellValue("ACTIVE");
    sheet.getRow(2).createCell(8);
    cell = sheet.getRow(2).getCell(8);
    cell.setCellValue("ADD");
    return mainWorkbook;
  }

  public XSSFWorkbook existingFGWithNonExistingItem(XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    XSSFCell cell = null;
    if (action.equals("fgDup")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellValue("testFGItemEdit" + getTimeStamp());
      cell = sheet.getRow(2).getCell(3);
      cell.setCellValue("FGITEM1" + getTimeStamp());
      cell = sheet.getRow(2).getCell(6);
      cell.setCellValue("ACTIVE");
      sheet.getRow(2).createCell(8);
      cell = sheet.getRow(2).getCell(8);
      cell.setCellValue("ADD");
      // sheet.removeRow(sheet.getRow(3));
    }
    return mainWorkbook;
  }

  public XSSFWorkbook caseSensitivefgDup(XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    XSSFCell cell = null;
    cell = sheet.getRow(2).getCell(0);
    cell.setCellValue("testFGItemEdit" + getTimeStamp());
    cell = sheet.getRow(2).getCell(3);
    cell.setCellValue("JD004");
    sheet.getRow(2).createCell(8);
    cell = sheet.getRow(2).getCell(8);
    cell.setCellValue(action);
    // sheet.removeRow(sheet.getRow(3));

    return mainWorkbook;
  }

  public XSSFWorkbook fgForNoItemPart(XSSFWorkbook mainWorkbook, String action) {

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    XSSFCell cell = null;
    cell = sheet.getRow(2).getCell(0);
    cell.setCellValue("uploadFG" + getTimeStamp());
    cell = sheet.getRow(2).getCell(3);
    cell.setCellValue("ITEMNOTEXISTS");
    sheet.getRow(2).createCell(8);
    cell = sheet.getRow(2).getCell(8);
    cell.setCellValue(action);
    // sheet.removeRow(sheet.getRow(3));

    return mainWorkbook;
  }

  public XSSFWorkbook updateFGUploadFile(XSSFWorkbook mainWorkbook, String action) {
    // FileInputStream inputFile = null;
    // HSSFWorkbook masterWorkbook = null;
    //
    // try {
    // inputFile = new FileInputStream(new File(filePath));
    // masterWorkbook = new HSSFWorkbook(inputFile);
    //
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    XSSFCell cell = null;
    if (action.equals("NFG") || action.equals("XLOB")) {
      cell = sheet.getRow(2).getCell(2);
      cell.setCellValue(action);
      sheet.getRow(2).createCell(8);
      cell = sheet.getRow(2).getCell(8);
      cell.setCellValue("UPDATE");
      sheet.removeRow(sheet.getRow(3));
    } else if (action.contains("No item")) {
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.BLANK);
      cell = sheet.getRow(3).getCell(3);
      cell.setCellType(CellType.BLANK);
      sheet.getRow(2).createCell(8);
      cell = sheet.getRow(2).getCell(8);
      cell.setCellValue("UPDATE");
      sheet.getRow(3).createCell(8);
      cell = sheet.getRow(3).getCell(8);
      cell.setCellValue("UPDATE");
    } else if (action.contains("RENAME")) {
      sheet.getRow(2).createCell(8);
      cell = sheet.getRow(2).getCell(8);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(action);
      sheet.getRow(2).createCell(9);
      cell = sheet.getRow(2).getCell(9);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(fgName);
      sheet.getRow(3).createCell(8);
      cell = sheet.getRow(3).getCell(8);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(action);
      sheet.getRow(3).createCell(9);
      cell = sheet.getRow(3).getCell(9);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(fgName);
    } else if (action.equals("Inactive")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellValue("fg-update");
      cell = sheet.getRow(2).getCell(3);
      cell.setCellValue("12345");
      cell = sheet.getRow(2).getCell(6);
      cell.setCellValue(action);
      sheet.getRow(2).createCell(8);
      cell = sheet.getRow(2).getCell(8);
      cell.setCellValue("UPDATE");
      sheet.removeRow(sheet.getRow(3));
    } else if (action.equals("AddDupItem")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellValue("testFGItemEdit" + getTimeStamp());
      cell = sheet.getRow(2).getCell(3);
      cell.setCellValue("FGITEM" + getTimeStamp());
      cell = sheet.getRow(2).getCell(6);
      cell.setCellValue(action);
      sheet.getRow(2).createCell(8);
      cell = sheet.getRow(2).getCell(8);
      cell.setCellValue("ADD");
      sheet.removeRow(sheet.getRow(3));
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateItemsUploadFile(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForCR")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CR" + timeStamp);
    }
    return mainWorkbook;
  }

  public void uploadPhantomItem(String filepth, XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);

    String sheetName = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);

    Cell c = er.findCellContaining(".*Item Number");
    cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex());
    cell.setCellType(CellType.STRING);
    cell.setCellValue("phantomItem" + timeStamp);
  }

  public XSSFWorkbook updateItemsForSearchItemScreen(String filepth, XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);

    String sheetName = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);
    Cell c = er.findCellContaining(".*Item Number");
    Cell busEntity = er.findCellContaining(".*Item Business Entity");
    Cell busEntityType = er.findCellContaining(".*Item Business Entity Type");
    Cell revision = er.findCellContaining(".*Revision");
    Cell code = er.findCellContaining(".*Commodity Code");
    Cell state = er.findCellContaining(".*State");
    Cell source = er.findCellContaining(".*Source System");

    // for (int i = c.getRowIndex() + 1; i < er.getNumberOfRows(); i++) {
    //// cell = sheet.getRow(i).getCell(c.getColumnIndex());
    // cell.setCellType(CellType.STRING);
    // cell.setCellValue(item + getTimeStamp());

    if (action.equals("uploadItemForItem")) {
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("item" + timeStamp);
    } else if (action.equals("uploadItemForValidation")) {
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("FGITEM" + timeStamp);
      cell = sheet.getRow(busEntityType.getRowIndex() + 1).getCell(busEntityType.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ENTERPRISE");
      for (int i = c.getRowIndex() + 2; i < c.getRowIndex() + 6; i++) {
        // sheet.getRow(i).
        cell = sheet.getRow(i).createCell(c.getColumnIndex());
        // cell.getCell(c.getColumnIndex(),CellType.STRING);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FGITEM" + (i - 2) + timeStamp);
        cell = sheet.getRow(i).createCell(busEntityType.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("ENTERPRISE");
        cell = sheet.getRow(i).createCell(busEntity.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("DELL");
        cell = sheet.getRow(i).createCell(revision.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("*");
        cell = sheet.getRow(i).createCell(code.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Packaging Material");
        cell = sheet.getRow(i).createCell(state.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("ACTIVE");
        cell = sheet.getRow(i).createCell(source.getColumnIndex());
        cell.setCellType(CellType.STRING);
        cell.setCellValue("MCM");
      }
    } else if (action.equals("uploadItemForFGTestData")) {
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("FG179");
      cell = sheet.getRow(busEntityType.getRowIndex() + 1).getCell(busEntityType.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ENTERPRISE");
    } else if (action.equals("uploadItemForFG")) {
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemFG" + timeStamp);
      cell = sheet.getRow(busEntityType.getRowIndex() + 1).getCell(busEntityType.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ENTERPRISE");
    } else if (action.equals("uploadItemWithMissingName")) {
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("");
    } else if (action.equals("uploadItemWithMissingBusiness")) {
      cell = sheet.getRow(busEntityType.getRowIndex() + 1).getCell(busEntityType.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("");
    } else if (action.equals("uploadItemWithUpdatedBusiness")) {
      cell = sheet.getRow(busEntityType.getRowIndex()).getCell(busEntityType.getColumnIndex());
      cell.setCellType(CellType.STRING);
      cell.setCellValue("SUPPLIER");
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateItemsForSearchItemAVLScreen(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForItemAVL")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemAVL" + timeStamp);
    } else if (action.equals("uploadItemWithMissingName")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("");
    } else if (action.equals("uploadItemWithMissingBusiness")) {
      cell = sheet.getRow(2).getCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("");
    } else if (action.equals("uploadItemWithUpdatedBusiness")) {
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Test Description");
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateItemsForMassUpdate(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("itemMass")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass11" + timeStamp);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass12" + timeStamp);
      cell = sheet.getRow(4).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass21" + timeStamp);
      sheet.getRow(5).createCell(0);
      cell = sheet.getRow(5).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass22" + timeStamp);
      cell = sheet.getRow(2).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass11" + timeStamp);
      cell = sheet.getRow(3).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass12" + timeStamp);
      cell = sheet.getRow(4).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass21" + timeStamp);
      sheet.getRow(5).createCell(4);
      cell = sheet.getRow(5).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass22" + timeStamp);
    } else if (action.equals("itemCRMass")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemCRMass1" + timeStamp);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemCRMass2" + timeStamp);
      cell = sheet.getRow(4).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemCRMass3" + timeStamp);
      sheet.getRow(5).createCell(0);
      cell = sheet.getRow(5).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemCRMass4" + timeStamp);
      cell = sheet.getRow(2).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("F3763");
      cell = sheet.getRow(3).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("F3763");
      cell = sheet.getRow(4).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("F3763");
      sheet.getRow(5).createCell(4);
      cell = sheet.getRow(5).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("F3763");
    } else if (action.equals("itemMassValidation")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem11" + timeStamp);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem12" + timeStamp);
      cell = sheet.getRow(4).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem21" + timeStamp);
      sheet.getRow(5).createCell(0);
      cell = sheet.getRow(5).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem22" + timeStamp);
      cell = sheet.getRow(2).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem11" + timeStamp);
      cell = sheet.getRow(3).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem12" + timeStamp);
      cell = sheet.getRow(4).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem21" + timeStamp);
      sheet.getRow(5).createCell(4);
      cell = sheet.getRow(5).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem22" + timeStamp);
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updatFGForMassUpdate(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("fgMass")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fgMass1" + timeStamp);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fgMass1" + timeStamp);
      cell = sheet.getRow(4).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fgMass2" + timeStamp);
      sheet.getRow(5).createCell(0);
      cell = sheet.getRow(5).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fgMass2" + timeStamp);
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass11" + timeStamp);
      cell = sheet.getRow(3).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass12" + timeStamp);
      cell = sheet.getRow(4).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass21" + timeStamp);
      sheet.getRow(5).createCell(3);
      cell = sheet.getRow(5).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMass22" + timeStamp);
    } else if (action.equals("fgMassValidation")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massFG1" + timeStamp);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massFG1" + timeStamp);
      cell = sheet.getRow(4).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massFG2" + timeStamp);
      sheet.getRow(5).createCell(0);
      cell = sheet.getRow(5).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massFG2" + timeStamp);
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem11" + timeStamp);
      cell = sheet.getRow(3).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem12" + timeStamp);
      cell = sheet.getRow(4).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem21" + timeStamp);
      sheet.getRow(5).createCell(3);
      cell = sheet.getRow(5).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("massItem22" + timeStamp);
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateParentForMassUpdate(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("parentMass")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("parentMassUpdate" + timeStamp);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("parentMassUpdate" + timeStamp);
      cell = sheet.getRow(2).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fgMass1" + timeStamp);
      cell = sheet.getRow(3).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fgMass2" + timeStamp);
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateItemsForCRCreate(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForCRCreate")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRCreate" + timeStamp);
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateItemsForCRException(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForCRApprove")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemCRApprove" + timeStamp);
    } else if (action.equals("uploadItemForEditCRApprove")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemEditCR" + timeStamp);
    } else if (action.equals("uploadItemForSearchCRApprove")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemCRSearchApprove" + timeStamp);
    } else if (action.equals("uploadItemForODMEmailCRApprove")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemCREmail" + timeStamp);
    } else if (action.equals("uploadItemForMultiLvlCRApprove")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("itemMultiLevelApprove" + timeStamp);
    }
    return mainWorkbook;
  }

  public XSSFWorkbook uploaditemforXLOBPriceTAM(XSSFWorkbook mainWorkbook, String action) {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForPriceTAM")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("PriceTAM" + timeStamp);
    } else if (action.equals("emcuploadItemForPriceTAM")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("EMCPriceTAM" + timeStamp);
    } else if (action.equals("delluploadItemForPriceTAM")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("DELLPriceTAM" + timeStamp);
    } else if (action.equals("delluploadItemForPriceTAMMonthly")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("DELLPriceTAMMonthly" + timeStamp);
    } else if (action.equals("delluploadItemForPriceTAMQuarterly")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("DELLPriceTAMQuarterly" + timeStamp);
    } else if (action.equals("XLOBDeleteTemplateItems")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("XLOBDeleteItem" + timeStamp);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("XLOBDeleteItem1" + timeStamp);
      cell = sheet.getRow(4).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("XLOBDeleteItem2" + timeStamp);
    }
    return mainWorkbook;
  }

  public XSSFWorkbook uploaditemforXLOBPriceTAMCommodity(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForPriceTAMCommodity")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("PriceTAMCommodity" + timeStamp);
    }
    return mainWorkbook;

  }

  public XSSFWorkbook updateItemsForCRStatus(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForCRStatusCheck")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRStatus" + timeStamp);
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateItemsForMassUpdateCR(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForCRStatusCheck")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRStatus" + timeStamp);
    }
    return mainWorkbook;
  }

  public XSSFWorkbook uploaditemforCRXWAP(XSSFWorkbook mainWorkbook, String action) {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForXWAPCR")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRXWP" + timeStamp);
    }
    if (action.equals("uploadItemForWAPCR")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRWP" + timeStamp);
    }
    // if (action.equals("uploadForXWAPcostrecord")) {
    // cell = sheet.getRow(3).getCell(3);
    // cell.setCellType(CellType.STRING);
    // cell.setCellValue("CRWP" + timeStamp);
    // }

    // if (action.equals("uploadForXWAPcostrecord")) {
    // cell = sheet.getRow(2).getCell(3);
    // cell.setCellType(CellType.STRING);
    // cell.setCellValue("UPXWPCR" + timeStamp);
    // }

    if (action.equals("uploadItemForXWAPCR")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("UPXWPCR" + timeStamp);
    }

    return mainWorkbook;

  }

  public XSSFWorkbook uploadmultipleitemforforecast(XSSFWorkbook mainWorkbook, String action) throws Throwable {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    HarmonyLoginUI ui = new HarmonyLoginUI();
    MTCMController mc = new MTCMController();
    String dateInString = mc.getFiscalMonthEndDate();
    ui.navHarmonyMTCM("Upload/Manage Jobs", "Admin");
    mc = new MTCMController();

    if (action.equals("uploadmultipleItemForForecast")) {
      // ui.navHarmonyMTCM("Upload/Manage Jobs", "Admin");
      // mc = new MTCMController();

      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem1" + timeStamp);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem2" + timeStamp);
      cell = sheet.getRow(4).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem3" + timeStamp);
      cell = sheet.getRow(5).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem4" + timeStamp);
      cell = sheet.getRow(6).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem5" + timeStamp);
      cell = sheet.getRow(7).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem6" + timeStamp);
      cell = sheet.getRow(8).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem7" + timeStamp);
      cell = sheet.getRow(9).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem8" + timeStamp);
      cell = sheet.getRow(10).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem9" + timeStamp);
      cell = sheet.getRow(11).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem10" + timeStamp);
      cell = sheet.getRow(12).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem11" + timeStamp);
      cell = sheet.getRow(13).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem12" + timeStamp);
      cell = sheet.getRow(14).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem13" + timeStamp);
      cell = sheet.getRow(15).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem14" + timeStamp);
      cell = sheet.getRow(16).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem15" + timeStamp);
    }

    return mainWorkbook;

  }

  public XSSFWorkbook uploadapprovedforecastbyFGGCM(XSSFWorkbook mainWorkbook, String action) {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForForecastbyFGGCM")) {
      cell = sheet.getRow(3).getCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.fggcm);
      cell = sheet.getRow(3).getCell(13);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem1" + timeStamp);
    }
    for (int j = 32; j <= 44; j++) {
      cell = sheet.getRow(3).getCell(j);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("100");
    }

    return mainWorkbook;

  }

  public XSSFWorkbook uploadapprovedforecastbyFGSuperGCM(XSSFWorkbook mainWorkbook, String action) {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForForecastbyFGSuperGCM")) {
      cell = sheet.getRow(3).getCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.fgSuperGCM);
      cell = sheet.getRow(3).getCell(13);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem4" + timeStamp);
    }
    for (int j = 32; j <= 44; j++) {
      cell = sheet.getRow(3).getCell(j);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("100");
    }

    return mainWorkbook;

  }

  public XSSFWorkbook uploadapprovedforecastbyFGadm(XSSFWorkbook mainWorkbook, String action) {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForForecastbyFGadm")) {
      cell = sheet.getRow(3).getCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.fgadm);
      cell = sheet.getRow(3).getCell(13);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem7" + timeStamp);
    }
    for (int j = 32; j <= 44; j++) {
      cell = sheet.getRow(3).getCell(j);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("100");
    }

    return mainWorkbook;

  }

  public XSSFWorkbook uploadapprovedforecastbyparentFGGcm(XSSFWorkbook mainWorkbook, String action) {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForForecastbyPFGGCM")) {
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.parentfggcm);
      cell = sheet.getRow(3).getCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.fggcm);
      cell = sheet.getRow(3).getCell(13);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem1" + timeStamp);
    }
    for (int j = 32; j <= 44; j++) {
      cell = sheet.getRow(3).getCell(j);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("100");
    }
    return mainWorkbook;

  }

  public XSSFWorkbook uploadapprovedforecastbyparentFGSGcm(XSSFWorkbook mainWorkbook, String action) {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForForecastbyPFGSGCM")) {
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.parentfgSgcm);
      cell = sheet.getRow(3).getCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.fgSuperGCM);
      cell = sheet.getRow(3).getCell(13);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem4" + timeStamp);
    }

    return mainWorkbook;

  }

  public XSSFWorkbook uploadapprovedforecastbyparentFGadm(XSSFWorkbook mainWorkbook, String action) {

    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForForecastbyPFGadm")) {
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.parentfgadm);
      cell = sheet.getRow(3).getCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(Forecast.fgadm);
      cell = sheet.getRow(3).getCell(13);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("fcItem7" + timeStamp);
    }
    for (int j = 32; j <= 44; j++) {
      cell = sheet.getRow(3).getCell(j);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("100");
    }
    return mainWorkbook;

  }

  public XSSFWorkbook uploadItemForCRMPN(String filepth, XSSFWorkbook mainWorkbook, String action) {
    // HSSFCell cell = null;
    // HSSFSheet sheet = mainWorkbook.getSheetAt(0);
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);

    String sheetName = null;
    ExcelReader er = new ExcelReader(filepth, sheetName);

    Cell c = er.findCellContaining(".*Item Number");
    cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex());
    cell.setCellType(CellType.STRING);

    if (action.equals("uploadItemForCRMPN")) {

      cell.setCellValue("CRMPN" + timeStamp);
    }
    if (action.equals("uploadItemForODMBuyCRMPN")) {
      cell.setCellValue("CRODMBuyMPN" + timeStamp);
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex() + 4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("F3763");
    }

    if (action.equals("uploadItemForODMBuyCRMPN1")) {
      cell.setCellValue("CRODMBuyMPN1" + timeStamp);
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex() + 4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("UN815");
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex() + 5);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("HITACHI");
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex() + 13);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("MISCELLANEOUS");
    }

    if (action.equals("uploadItemForODMBuyCRMPN2")) {
      cell.setCellValue("CRODMBuyMPN2" + timeStamp);
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex() + 4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("UN815");
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex() + 5);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("HITACHI");
      cell = sheet.getRow(c.getRowIndex() + 1).getCell(c.getColumnIndex() + 13);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("MISCELLANEOUS");
    }

    return mainWorkbook;
  }

  public XSSFWorkbook uploadMultipleItemForCRMPN(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("uploadItemForMultipleCRMPN")) {
      cell = sheet.getRow(2).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRMPNUpload" + timeStamp);
    } else if (action.equals("uploadMultipleItemForCRMPN")) {
      CreationHelper createHelper = mainWorkbook.getCreationHelper();
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRMPNUpload" + timeStamp);
      cell = sheet.getRow(2).getCell(16);
      CellStyle cellStyle = mainWorkbook.createCellStyle();
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell.setCellStyle(cellStyle);
      cell = sheet.getRow(2).getCell(17);
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(5).toDate()));
      cell.setCellStyle(cellStyle);
      sheet.getRow(2).createCell(56);
      cell = sheet.getRow(2).getCell(56);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("3");
    } else if (action.equals("uploadMultipleItemDiffMPN")) {
      CreationHelper createHelper = mainWorkbook.getCreationHelper();
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRMPNUpload" + timeStamp);
      cell = sheet.getRow(2).getCell(16);
      CellStyle cellStyle = mainWorkbook.createCellStyle();
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell.setCellStyle(cellStyle);
      cell = sheet.getRow(2).getCell(17);
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(5).toDate()));
      cell.setCellStyle(cellStyle);
      sheet.getRow(2).createCell(56);
      cell = sheet.getRow(2).getCell(56);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("30");
    } else if (action.equals("uploadMultipleItemDiffDateMPN")) {
      CreationHelper createHelper = mainWorkbook.getCreationHelper();
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRMPNUpload" + timeStamp);
      cell = sheet.getRow(2).getCell(16);
      CellStyle cellStyle = mainWorkbook.createCellStyle();
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell.setCellStyle(cellStyle);
      cell = sheet.getRow(2).getCell(17);
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(6).toDate()));
      cell.setCellStyle(cellStyle);
      sheet.getRow(2).createCell(21);
      cell = sheet.getRow(2).getCell(21);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ODMBUY");
      sheet.getRow(2).createCell(56);
      cell = sheet.getRow(2).getCell(56);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("40");
    } else if (action.equals("uploadMultipleItemDiffDate&MPN")) {
      CreationHelper createHelper = mainWorkbook.getCreationHelper();
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRMPNUpload" + timeStamp);
      cell = sheet.getRow(2).getCell(16);
      CellStyle cellStyle = mainWorkbook.createCellStyle();
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell.setCellStyle(cellStyle);
      cell = sheet.getRow(2).getCell(17);
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(7).toDate()));
      cell.setCellStyle(cellStyle);
      sheet.getRow(2).createCell(21);
      cell = sheet.getRow(2).getCell(21);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ODMBUY");
      sheet.getRow(2).createCell(56);
      cell = sheet.getRow(2).getCell(56);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("50");
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updateItemsForSupplierCRCreate(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    cell = sheet.getRow(2).getCell(0);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("CRSuppCreate" + timeStamp);
    cell = sheet.getRow(2).getCell(4);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("CRSuppCreate" + timeStamp);
    return mainWorkbook;
  }

  public XSSFWorkbook updateItemsForEMCRCreate(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    cell = sheet.getRow(2).getCell(0);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("CREMCreate" + timeStamp);
    cell = sheet.getRow(2).getCell(4);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("CREMCreate" + timeStamp);
    return mainWorkbook;
  }

  public XSSFWorkbook uploadCRWAP(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);

    if (action.equals("uploadPendingWAPCR")) {
      CreationHelper createHelper = mainWorkbook.getCreationHelper();
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRWP" + timeStamp);
      cell = sheet.getRow(2).getCell(16);
      CellStyle cellStyle = mainWorkbook.createCellStyle();
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell.setCellStyle(cellStyle);
      cell = sheet.getRow(2).getCell(17);
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(5).toDate()));
      cell.setCellStyle(cellStyle);
      sheet.getRow(2).createCell(27);
      cell = sheet.getRow(2).getCell(27);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("30");
    }

    else if (action.equals("uploadApprovedWAPCR")) {

      CreationHelper createHelper = mainWorkbook.getCreationHelper();
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CRWP" + timeStamp);
      cell = sheet.getRow(2).getCell(16);
      CellStyle cellStyle = mainWorkbook.createCellStyle();
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell.setCellStyle(cellStyle);
      cell = sheet.getRow(2).getCell(17);
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(5).toDate()));
      cell.setCellStyle(cellStyle);
      sheet.getRow(2).createCell(27);
      cell = sheet.getRow(2).getCell(27);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("30");

    } else if (action.equals("uploadForXWAPcostrecord")) {

      CreationHelper createHelper = mainWorkbook.getCreationHelper();
      cell = sheet.getRow(2).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("UPXWPCR" + timeStamp);
      cell = sheet.getRow(2).getCell(16);
      CellStyle cellStyle = mainWorkbook.createCellStyle();
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
      cell.setCellStyle(cellStyle);
      cell = sheet.getRow(2).getCell(17);
      cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
      cell.setCellValue(DateUtil.getExcelDate(DateTime.now().plusDays(5).toDate()));
      cell.setCellStyle(cellStyle);
      sheet.getRow(2).createCell(27);
      cell = sheet.getRow(2).getCell(27);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("30");
    }
    return mainWorkbook;
  }

  public XSSFWorkbook updatePGUploadFile(XSSFWorkbook mainWorkbook, String action) {
    XSSFCell cell = null;
    XSSFSheet sheet = mainWorkbook.getSheetAt(0);
    if (action.equals("groupType")) {
      sheet.createRow(3).compareTo(sheet.getRow(2));
      sheet.getRow(3).createCell(0);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("TestParent12");
      sheet.getRow(3).createCell(2);
      cell = sheet.getRow(3).getCell(2);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("XLOB");
      sheet.getRow(3).createCell(3);
      cell = sheet.getRow(3).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("MASSUPDATE");
      sheet.getRow(3).createCell(4);
      cell = sheet.getRow(3).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ABAY_R191G");
      sheet.getRow(3).createCell(5);
      cell = sheet.getRow(3).getCell(5);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ADD");
    }

    else if (action.equals("addFG")) {
      sheet.createRow(3).compareTo(sheet.getRow(2));
      sheet.getRow(3).createCell(0);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("TestParent12");
      sheet.getRow(3).createCell(2);
      cell = sheet.getRow(3).getCell(2);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CFG");
      sheet.getRow(3).createCell(3);
      cell = sheet.getRow(3).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("MASSUPDATE");
      sheet.getRow(3).createCell(4);
      cell = sheet.getRow(3).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("VCHX5");
      sheet.getRow(3).createCell(5);
      cell = sheet.getRow(3).getCell(5);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("ADD");
    }

    else if (action.equals("removeFG")) {
      sheet.createRow(3).compareTo(sheet.getRow(2));
      sheet.getRow(3).createCell(0);
      cell = sheet.getRow(3).getCell(0);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("TestParent12");
      sheet.getRow(3).createCell(2);
      cell = sheet.getRow(3).getCell(2);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("CFG");
      sheet.getRow(3).createCell(3);
      cell = sheet.getRow(3).getCell(3);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("MASSUPDATE");
      sheet.getRow(3).createCell(4);
      cell = sheet.getRow(3).getCell(4);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("VCHX5");
      sheet.getRow(3).createCell(5);
      cell = sheet.getRow(3).getCell(5);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("DELETE");
    } else if (action.equals("RENAME")) {
      sheet.getRow(2).createCell(5);
      cell = sheet.getRow(2).getCell(5);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(action);
      sheet.getRow(2).createCell(6);
      cell = sheet.getRow(2).getCell(6);
      cell.setCellType(CellType.STRING);
      cell.setCellValue("TestParent12" + timeStamp);
      // sheet.removeRow(sheet.getRow(3));
    }
    return mainWorkbook;

  }

  // private String parseTransactionID(String statusMsg) {
  // int beginIndex = statusMsg.indexOf('>') + 1;
  // String transactionID = statusMsg.substring(beginIndex);
  //
  // int endIndex = transactionID.indexOf('<');
  // transactionID = transactionID.substring(0, endIndex).trim();
  //
  // return transactionID;
  // }

  private String getUploadStatusMsg() {
    // if (page.isUploadStatusError()) {
    // return page.uploadStatusError().getText();
    // }
    WebElement statusEle = page.uploadStatus();
    if (statusEle == null) {
      JLog.error("getUploadStatusMsg: upload status element not found on page");
      return "";
    }
    return statusEle.getText();
  }

  public void clickSubmit() {
    page.setElementValue(page.button_Submit(), true);
  }

  public void clickCancel() {
    clickAndCheckForPOSTError(page.button_Cancel());
  }

  /**
   * @param status
   *               The expected status of the upload. Default is SUCCESS
   */
  public void setExpectedStatus(String status) {
    this.expectedStatus = status;
  }

  /**
   * @param status
   *               The expected status of the upload. Default is SUCCESS
   */
  public void setExpectedStatus(LOAD_STATUS status) {
    this.expectedStatus = status.toString();
  }

  public void uploadXMLFile(File fileName) {
    BusinessEntitiesView view = new BusinessEntitiesView();
    WebElement fileInput = view.getFileInput();
    JLog.write("Successfully selected the file " + fileName);
    WebElement uploadBtn = view.getSubmitButton();
    JLog.write("Successfully clicked on the submit button ");

    if (fileInput == null) {
      JLog.fail("File input element not found on the page!", com.test.selenium.common.TakeScreenshot.True);
      throw new NullPointerException("File input element is null");
    }
    if (uploadBtn == null) {
      // Debug: log all button elements on the page
      List<WebElement> allButtons = view.browserSession.getDriver().findElements(By.tagName("button"));
      JLog.write("DEBUG: Listing all <button> elements on the page:");
      for (WebElement btn : allButtons) {
        JLog.write("Button text: '" + btn.getText() + "', id: '" + btn.getAttribute("id") + "', class: '"
            + btn.getAttribute("class") + "', name: '" + btn.getAttribute("name") + "'");
      }
      JLog.fail("Upload button element not found on the page!",
          com.test.selenium.common.TakeScreenshot.True);
      throw new NullPointerException("Upload button element is null");
    }
    fileInput.sendKeys(fileName.getAbsolutePath());
    uploadBtn.click();
  }

}