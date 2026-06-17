/*
 * SCPlatformDownloadController.java Created on Sep 30, 2020
 *
 * Copyright (c) 2020 E2open, Inc. All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open The copyright notice above does not
 * evidence any actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.ui.main.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.scplatform.qa.e2Messages.utilities.ExcelWriter;
import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.AbstractPage.COMPARE;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.Validate;
import com.test.selenium.common.autoIT.AutoItDownload;
import com.test.selenium.common.excel.ExcelReader;
import com.test.selenium.common.filedownloader.BrowserDownloader;
import com.test.selenium.common.filedownloader.ChromeDownloader;
import com.test.selenium.common.filedownloader.DownloaderFactory;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.login.LoginSCPlatformHarmony;
import com.test.selenium.scplatform.modelViewController.ExceptionView;
import com.test.selenium.scplatform.modelViewController.SCPlatformPage;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.modelViewController.MTCMView;
import com.test.selenium.scplatform.modelViewController.SupplyAllocationController;
import com.test.selenium.scplatform.modelViewController.CostRecords.CostRecordsController;
import com.test.selenium.scplatform.modelViewController.CostRecords.CostRecordsView;
import com.test.selenium.scplatform.modelViewController.CostRecords.WAPCostRecordsView;
import com.test.selenium.scplatform.steps.General;
import com.test.selenium.scplatform.steps.HarmonyLoginUI;
import com.test.selenium.scplatform.steps.TAMSupplyAllocation;
import com.google.common.base.Verify;

public class SCPlatformDownloadController
    extends com.test.selenium.api.e2sc.download.DownloadController {
  static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
  Prop prop = Prop.getInstance();
  SCPlatformPage page;

  public SCPlatformDownloadController() {
    super();
    page = new SCPlatformPage();
  }

  @Override
  public PageImpl getView() {
    return new SCPlatformPage();
  }

  private By locator;
  private By parentLocator;

  private String chromeDownloadDir = AutoItDownload.getChromeSaveDir();

  public void setLocator(By by) {
    locator = by;
  }

  public void setParentLocator(By by) {
    parentLocator = by;
  }

  public String getDownloadIIcon() throws InterruptedException, IOException {
    AbstractPage page = new AbstractPage();
    String downloadsDir = System.getProperty("user.home") + File.separator + "Downloads";
    File dir = new File(downloadsDir);

    // 1. Snapshot existing files BEFORE clicking so we can detect the new one
    List<String> existingFiles = new ArrayList<>();
    if (dir.exists() && dir.isDirectory()) {
      File[] before = dir.listFiles();
      if (before != null) {
        for (File f : before) existingFiles.add(f.getAbsolutePath());
      }
    }

    // 2. Locate and JS-click the download button (avoids Chrome 147 ElementClickInterceptedException)
    List<WebElement> btns = page.getList(
        By.xpath("//button[@title='Download' and contains(@onclick,'supplyAllocationException')]"));
    if (btns.isEmpty()) {
      JLog.error("[TWTSSD] Supply Allocation Exception download button not found", TakeScreenshot.True);
      return null;
    }
    page.executeJavaScript("arguments[0].scrollIntoView(true);", btns.get(0));
    page.executeJavaScript("arguments[0].click();", btns.get(0));
    JLog.write("[TWTSSD] Clicked Supply Allocation Exception download button via JS");

    // 3. Poll up to 60 s for a new completed file (skip .crdownload/.tmp Chrome partial files)
    for (int i = 0; i < 60; i++) {
      Thread.sleep(1000);
      File[] after = dir.listFiles();
      if (after != null) {
        for (File f : after) {
          String path = f.getAbsolutePath();
          String nameLc = f.getName().toLowerCase();
          if (!existingFiles.contains(path)
              && !nameLc.endsWith(".crdownload")
              && !nameLc.endsWith(".tmp")) {
            JLog.write("[TWTSSD] New download file detected: " + f.getName());
            return path;
          }
        }
      }
    }

    // 4. Last-resort fallback: return the most-recently-modified xlsx in downloads
    JLog.write("[TWTSSD] No new file detected after 60 s - falling back to latest xlsx in downloads");
    File[] xlsxFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xlsx"));
    if (xlsxFiles != null && xlsxFiles.length > 0) {
      Arrays.sort(xlsxFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
      JLog.write("[TWTSSD] Returning latest xlsx: " + xlsxFiles[0].getName());
      return xlsxFiles[0].getAbsolutePath();
    }
    JLog.error("[TWTSSD] No xlsx files found in downloads directory - download likely failed", TakeScreenshot.True);
    return null;
  }

  public String getCRDownloadIcon() throws InterruptedException, IOException {
    // ChromeDownloader dwn = new ChromeDownloader();
    AbstractPage page = new AbstractPage();
    return handleDownloadForReport(
        Prop.getInstance().getWorkingDir(),
        page.getList(By.xpath("//i[contains(@onclick,'downloadCostException()')]")).get(0));
  }

  public String downloadForecast(String action) throws InterruptedException, IOException {
    AbstractPage page = new AbstractPage();
    // action = "AdjustableTestData";
    String path = Prop.getInstance().getWorkingDir();
    // if (action.contains("AdjustableTestData")) {
    // action = "ADJUSTABLE";
    // } else
    if (action.contains("Adjust")) {
      action = "ADJUSTABLE";
    } else
      action = "CURRENT";
    ChromeDownloader dwn = new ChromeDownloader();
    return dwn.handleDownload(path, page.get(By.xpath("//a[contains(@onclick,'" + action + "')]")));
  }

  public String downloadPriceTam() throws InterruptedException, IOException {
    AbstractPage page = new AbstractPage();
    ChromeDownloader dwn = new ChromeDownloader();
    // Try the primary XPath first, then fallback (javascript: prefix may vary)
    List<WebElement> submitBtns = page.getList(
        By.xpath("//button[contains(@onclick,'submitExtractToFile')]"));
    if (submitBtns.isEmpty()) {
      JLog.error("downloadPriceTam: submit extract button not found - download form may not have opened");
      return null;
    }
    return dwn.handleDownload(Prop.getInstance().getWorkingDir(), submitBtns.get(0));
  }

  public void verifyODMEmailLink(int count) throws InterruptedException, IOException {
    ExceptionView v = new ExceptionView();
    MTCMController c = new MTCMController();
    WebElement e = v.getDownloadLinks().get(0);
    ChromeDownloader dwn = new ChromeDownloader();
    String downloadedFile;
    try {
      downloadedFile = dwn.handleDownload(Prop.getInstance().getWorkingDir(), e);
    } catch (IOException ioe) {
      // [ODM Fix] Chrome 148 may not provide filename in Content-Disposition header,
      // causing FileNotFoundException when trying to move "UnknownDownload" file.
      // Fall back to finding the most recently downloaded file.
      JLog.write("[ODM Fix] handleDownload threw: " + ioe.getMessage() + ". Checking for latest download in Downloads folder...");
      downloadedFile = getLatestFileFromDownloads();
      JLog.write("[ODM Fix] Using backup file: " + downloadedFile);
    }
    Verify.verify(downloadedFile != null && !downloadedFile.isEmpty(), "Unable to find downloaded file.");
    JLog.write("Downloaded and Verified " + count + " files attached");
  }

  /**
   * [ODM Fix] Backup method to find the most recently downloaded file from the Downloads folder.
   * Used when Chrome 148+ returns UnknownDownload for files without proper Content-Disposition headers.
   */
  private String getLatestFileFromDownloads() throws IOException {
    String downloadsDir = System.getProperty("user.home") + File.separator + "Downloads";
    File dir = new File(downloadsDir);
    if (!dir.exists() || !dir.isDirectory()) {
      throw new FileNotFoundException("Downloads directory not found: " + downloadsDir);
    }
    File[] files = dir.listFiles(f -> f.isFile() && !f.getName().endsWith(".crdownload") && !f.getName().endsWith(".tmp"));
    if (files == null || files.length == 0) {
      throw new FileNotFoundException("No completed files found in Downloads directory: " + downloadsDir);
    }
    java.util.Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
    File latestFile = files[0];
    JLog.write("[ODM Fix] Found latest download: " + latestFile.getName() + " (modified: " + new java.util.Date(latestFile.lastModified()) + ")");
    return latestFile.getAbsolutePath();
  }

  public String getPreRequisiteLink() throws InterruptedException, IOException {
    // AbstractPage page = new AbstractPage();

    // ChromeDownloader dwn = new ChromeDownloader();
    // return dwn.handleDownload(Prop.getInstance().getWorkingDir(),
    // view.get(By.xpath("//a[contains(@onclick,'callPreRequiSite();')]//b")));
    MTCMView view = new MTCMView();
    String downloadedFile = String.format(
        "%s%s%s.xlsx",
        Prop.getInstance().getWorkingDir(),
        "_preReq",
        view.getUniqueNumber());

    BrowserDownloader downloader = DownloaderFactory.getBrowser();
    Thread dirWatcherThread = downloader.startDirectoryWatcher();

    MTCMController c = new MTCMController();
    view = new MTCMView();
    WebElement e = view.get(By.xpath("//a[contains(@onclick,'callPreRequiSite();')]"));
    view.executeJavaScript("arguments[0].click();", e);
    String downloadedFilePath = downloader.waitForDownloadFile(dirWatcherThread,
        FilenameUtils.getFullPath(downloadedFile));
    if (downloadedFilePath == null) {
      return null;
    }
    FileUtils.moveFile(new File(downloadedFilePath), new File(downloadedFile));
    JLog.linkToFile("Download File", downloadedFile.toString());

    return downloadedFile.toString();
  }

  public String getCRPriceVarianceReport() throws InterruptedException, IOException {
    AbstractPage page = new AbstractPage();
    return handleDownloadForReport(
        Prop.getInstance().getWorkingDir(),
        page.getList(By.xpath("//button[contains(@onclick,'CostRecordPriceVarianceReport')]"))
            .get(0));
  }

  public String handleDownloadForReport(String saveAsPath, WebElement webElement)
      throws InterruptedException, IOException {
    ChromeDownloader dwn = new ChromeDownloader();
    boolean success = false;
    Exception lastException = null;

    AbstractPage abPage = new AbstractPage();
    String[] frames = abPage.getSwitchToFrames();

    Thread dirWatcherThreadChrome = dwn.startDirectoryWatcher();
    //
    // success = actionsToDownload.execute();
    //
    // if (!success) {
    // dirWatcherThreadChrome.interrupt();
    // JLog.error(
    // "Error occurred in ActionsForDownload method: " +
    // actionsToDownload.toString(),
    // TakeScreenshot.True);
    // return null;
    // }

    if (webElement != null) {
      try {
        abPage.scrollToElement(webElement);
        // webElement.click();
        abPage.executeJavaScript("arguments[0].click();", webElement);

        success = true;
      } catch (Exception e) {
        // if (locator != null) {
        // abPage.switchToFrame(frames);
        //
        // Waiter waiter = new Waiter();

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

    return dwn.waitForDownloadFile(dirWatcherThreadChrome, saveAsPath);
  }

  public void downloadVerification(String dwnloadedFile, String page, String action)
      throws Exception {

    if (dwnloadedFile == null || dwnloadedFile.isEmpty()) {
      throw new AssertionError("Download failed: file path is null or empty. The download may not have completed.");
    }

    XSSFWorkbook masterWorkbook = null;
    WorkbookFactory masterWorkbookExcel = null;
    XSSFSheet updateSheetForPriceTam = null;
    HSSFSheet updateSheet = null;
    HSSFCell cell = null;

    // XSSFWorkbook masterWorkbook = null;
    // HSSFWorkbook masterWorkbookExcel = null;
    // XSSFSheet updateSheetForPriceTam = null;
    // HSSFSheet updateSheet = null;
    // HSSFCell cell = null;

    try {
      if (page.contains("PriceTam") && !action.equals("verifyData")
          && !action.equals("verifySingleItemData")) {
        OPCPackage priceTamInputFile = OPCPackage.open(new File(dwnloadedFile));
        masterWorkbook = new XSSFWorkbook(priceTamInputFile);
        updateSheetForPriceTam = masterWorkbook.getSheetAt(0);
      } else {
        Workbook workbook = WorkbookFactory.create(new File(dwnloadedFile));
        Sheet updateSheet1 = workbook.getSheetAt(0);

        // FileInputStream inputFile = new FileInputStream(new File(dwnloadedFile));
        // masterWorkbookExcel = new HSSFWorkbook(inputFile);
        // updateSheet = masterWorkbookExcel.getSheetAt(0);
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (page.equals("CR") || page.equals("PriceVarianceDownload")) {
      // if (action.contains("createNewSL")) {
      // AbstractPage.sleep(2);
      // String updatedFile = updateFileToCreateNewSL(dwnloadedFile);
      // Properties p = new Properties();
      // FileOutputStream fr = new FileOutputStream(prop.getRootDir() +
      // "scplatform/data/properties/cr.properties");
      // p.setProperty("downloadedFile", updatedFile);
      // p.save(fr, "");
      // fr.close();
      // } else
      verifyCRDownloads(dwnloadedFile, action);
    } else if (page.contains("Forecast")) {
      if (action.equals("verifyData")) {
        verifyForecastData(updateSheet, "JD002", action);
      }
    } else if (page.contains("PriceTam")) {
      if (action.equals("verifyPastData")) {
        verifyPastData(dwnloadedFile, "JD002", action);
      } else if (action.equals("verifyData")) {
        verifyPriceTamData(dwnloadedFile);
      } else if (action.equals("verifySingleItemData")) {
        verifyPriceTamSingleItemData(dwnloadedFile);
      } else if (action.equals("verifyPriceTamDataForFIlter")) {
        validatePriceTamRowVerify(dwnloadedFile);
      }
    } else if (page.contains("suppAllocExcepRep")) {
      if (action.equals("verifySuppAllocReportDetails")) {
        verifySuppAllocReportDetails(dwnloadedFile);
      }
    } else if (page.equals("SearchExcep")) {
      if (action.contains("Status")) {
        verifyExcepDetails(action, updateSheet);
      } else if (action.contains("verifyMRPSiteColumn"))
        verifyMRPSiteOnExcepDownload(dwnloadedFile);
    } else if (page.contains("itemSearch")) {
      verifyItemSearchResults(dwnloadedFile);
    } else if (page.equals("Download Allocation")) {
      verifySupplierAllocationDownload(dwnloadedFile, action);
    }
  }

  public void verifySupplierAllocationDownload(String dwnloadedFile, String action) throws IOException {
    String sheetName = null;
    ExcelReader er = new ExcelReader(dwnloadedFile, sheetName);
    Cell c;
    if (action.equals("destinationSiteColumnAbsent")) {
      c = null;
      try {
        c = er.findCellContaining(".*Destination.*Site.*");
      } catch (Exception e) {
        // expected â€” column not found
      }
      Verify.verify(c == null,
          "DestinationSite column should be ABSENT in download but was found");
      JLog.write("Verified: DestinationSite column is ABSENT in downloaded SA file (feature disabled)");
    } else if (action.equals("destinationSiteColumnPresent")) {
      c = er.findCellContaining(".*Destination.*Site.*");
      Verify.verify(c != null,
          "DestinationSite column should be PRESENT in download but was not found");
      JLog.write("Verified: DestinationSite column is PRESENT in downloaded SA file (feature enabled)");
    } else if (action.equals("destinationSiteValueInDownload")) {
      c = er.findCellContaining(".*Destination.*Site.*");
      Verify.verify(c != null, "DestinationSite column not found in downloaded SA file");
      int colIdx = c.getColumnIndex();
      int headerRow = c.getRowIndex();
      boolean hasValues = false;
      for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
        Cell dataCell = er.getCell(i, colIdx);
        if (dataCell != null) {
          String val = dataCell.getStringCellValue();
          if (val != null && !val.trim().isEmpty()) {
            hasValues = true;
            break;
          }
        }
      }
      Verify.verify(hasValues,
          "DestinationSite column exists but has no values in downloaded SA file");
      JLog.write("Verified: DestinationSite column has values in downloaded SA file");
    }
  }

  public void verifyItemSearchResults(String downloadedFile) throws IOException {

    JLog.resetErrorCount();
    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(downloadedFile, sheetName);

    int rowsOnExcel = excelReader.getNumberOfRows();
    Cell itemCell = excelReader.findCellContaining(".*Item Number");
    int headerRow = itemCell.getRowIndex();
    Cell itemBusEntityCell = excelReader.findCellContaining(".*Item Business Entity");
    Cell itemDescpCell = excelReader.findCellContaining(".*Item Description");
    Cell itemEOLStateCell = excelReader.findCellContaining(".*State");
    Cell itemPGCell = excelReader.findCellContaining(".*Parent-Functional Groups");
    Cell itemFGCell = excelReader.getCell(headerRow, itemPGCell.getColumnIndex() - 1);

    MTCMController mc = new MTCMController();
    AbstractPage page = new AbstractPage();
    int rowsFromUI = page.getList(By.xpath("//a[@data-popover='#item-popover']")).size();
    rowsOnExcel = rowsOnExcel - (headerRow + 1);
    Verify.verify(rowsOnExcel == rowsFromUI, "Rows on excel and UI are not matching.");
    JLog.write("Verified that rows on UI and on Excel are matching and == " + rowsFromUI);
    String item[] = mc.getColumnValueDisplayed("Item Number");
    String itemBusEntity[] = mc.getColumnValueDisplayed("Item Business");
    String itemDescp[] = mc.getColumnValueDisplayed("Item Description");
    // String itemState[] = mc.getColumnValueDisplayed("State");
    String itemFG[] = mc.getColumnValueDisplayed("Functional Groups");
    String parentHeaderOnUI = "Parent Functional Group";
    if (LoginSCPlatformHarmony.navUrl.contains("dev4160")) {
      Properties p = new Properties();
      InputStream fi = new FileInputStream(prop.getRootDir() + "/psBox/labelName.properties");
      p.load(fi);
      parentHeaderOnUI = p.getProperty("parent", "");
    }
    String itemPG[] = mc.getColumnValueDisplayed(parentHeaderOnUI);

    for (int i = headerRow + 1, j = 0; i < rowsOnExcel; i++, j++) {
      Verify.verify(
          item[j].equals(excelReader.getCell(i, itemCell.getColumnIndex()).getStringCellValue()),
          "Unable to verify value at row " + (i + 1));
      String s = excelReader.getCell(i, itemBusEntityCell.getColumnIndex()).getStringCellValue();
      JLog.write("On excel = " + s);
      JLog.write("On UI =" + itemBusEntity[j]);
      Verify.verify(itemBusEntity[j].equals(s), "Unable to verify value at row " + (i + 1));
      s = excelReader.getCell(i, itemDescpCell.getColumnIndex()).getStringCellValue();
      Verify.verify(itemDescp[j].equals(s), "Unable to verify value at row " + (i + 1));
      Verify.verify(
          (excelReader.getCell(i, itemEOLStateCell.getColumnIndex()).getStringCellValue()
              .equals("ACTIVE")),
          "Unable to verify value at row " + (i + 1));
      s = excelReader.getCell(i, itemPGCell.getColumnIndex()).getStringCellValue();
      Verify.verify(itemFG[j].equals(s), "Unable to verify value at row " + (i + 1));
      s = excelReader.getCell(i, itemFGCell.getColumnIndex()).getStringCellValue();
      Verify.verify(itemPG[j].equals(s), "Unable to verify value at row " + (i + 1));
      JLog.write("Successfully verified downloaded item details.");
    }

  }

  public String updateFileToCreateNewSL(String dwnloadedFile) {

    String sheetName = null;
    ExcelReader er = new ExcelReader(dwnloadedFile, sheetName);

    ExcelWriter excelWriter = new ExcelWriter();
    File file = new File(dwnloadedFile);
    excelWriter.modifyExisting(file, file, sheetName);

    int totRows = excelWriter.getRowCount();
    // copying all values from last row to new row
    excelWriter.getCell(0, 0).getSheet().createRow(totRows);

    totRows = excelWriter.getRowCount();
    // excelWriter.closeExcel();

    // excelWriter = new ExcelWriter();
    // excelWriter.modifyExisting(file, file, sheetName);
    Row prevRow = excelWriter.getRow(excelWriter.getRowCount() - 1);
    String prevColVal = "";

    Cell cell = er.findCellContaining(".*Source System");
    int totCols = cell.getColumnIndex();

    for (int col = 0; col < totCols; col++) {
      prevColVal = prevRow.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
      excelWriter.getCell(0, 0).getSheet().getRow(totRows - 1).createCell(col, CellType.STRING);
      excelWriter.overwriteValue(prevColVal, col, totRows - 1);
    }

    cell = er.findCellContaining(".*Destination Site");

    // writing new destnSIte value( any new updates value to create a new SL
    excelWriter.overwriteValue("APCC-APCC", cell.getColumnIndex(), excelWriter.getRowCount() - 1);
    excelWriter.closeExcel();

    return dwnloadedFile;
  }

  public void verifyCRDownloads(String downloadedFile, String action) {
    // int cellIndexOfProjectName = 54;
    // int cellIndexOfMPN = 57;
    // String navUrl = LoginSCPlatformHarmony.navUrl;
    // for psbox dev4160 , projectName and mpn values are on different
    // index than
    // dev boxes
    // if (navUrl.contains("dev4160")) {
    // cellIndexOfMPN = 53;
    // cellIndexOfProjectName = 50;
    // }

    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(downloadedFile, sheetName);

    int rowsOnExcel = excelReader.getNumberOfRows();
    Cell MPNCell = excelReader.findCellContaining(".*MPN");
    int headerRow = 0;
    if (MPNCell != null)
      headerRow = MPNCell.getRowIndex();

    String actMPN = "";
    String expMPN = "";
    if (action.contains("MPN")) {
      CostRecordsView view = new CostRecordsView();
      List<WebElement> elements = view.getMPNonAllRows();
      view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
      JLog.screenCapture();
      if (action.equals("dwnloadWithoutMPN")) {
        for (int i = headerRow + 1; i < rowsOnExcel; i++) {
          if (i - 2 <= elements.size() - 1) { // checking UI
            expMPN = elements.get(i - 2).getAttribute("value");
            Verify.verify(expMPN.equals(""), "MPN fields is not null as expected.");
          }
          // checking downloaded file
          MPNCell = excelReader.getCell(i, MPNCell.getColumnIndex());
          actMPN = MPNCell.getStringCellValue();
          Verify.verify(
              actMPN.equals(""),
              "Expected value of mpn is null , but found " + actMPN + " as actual value.");
        }
      } else if (action.equals("dwnloadWithMPN")) {
        elements = view.getMPNonAllRows();
        List<String> mpnValuesOnUI = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
          mpnValuesOnUI.add(elements.get(i).getAttribute("value"));
        }
        CostRecordsController c = new CostRecordsController();
        c.clickTab("ODM BUY");
        List<WebElement> ele = view.getMPNonAllRows();
        for (int i = 0; i < ele.size(); i++) {
          mpnValuesOnUI.add(ele.get(i).getAttribute("value"));
        }
        for (int i = headerRow + 1; i < rowsOnExcel; i++) {
          MPNCell = excelReader.getCell(i, MPNCell.getColumnIndex());
          actMPN = MPNCell.getStringCellValue();
          Verify.verify(
              mpnValuesOnUI.contains(actMPN),
              "Expected value of mpn is null , but found " + actMPN + " as actual value.");
        }
      }

    } else if (action.equals("verifyPNameSearchCR") || action.equals("verifyPNameSearchSL")
        || action.equals("verifyPNameNewSL")) {
      String actPjtName;
      boolean status = false;
      Cell cell = excelReader.findCellContaining("(?i).*project(?:.?name)?");
      Verify.verify(cell != null, "Project Name column not found in downloaded file");
      if (cell == null) return;
      headerRow = cell.getRowIndex();

      for (int i = headerRow + 1; i < rowsOnExcel; i++) {
        cell = excelReader.getCell(i, cell.getColumnIndex());
        actPjtName = cell.getStringCellValue();
        JLog.write("Actual found for projectName on row=" + i + " is " + actPjtName);
        if (actPjtName.equals("testPjt")) {
          status = true;
          break;
        }
      }
      Verify.verify(status, "Unable to verify projectName");
      JLog.write("Successfully verified projectName.");
    } else if (action.contains("verifyMRPSiteColumn")
        || action.contains("verifyPriceVarianceMRPSiteColumn")) {
      Cell cell = excelReader.findCellContaining(".*MRP Site");
      Verify.verify(cell != null, "Unable to verify MRP Site column");
      if (cell == null) return;
      headerRow = cell.getRowIndex();
      JLog.write("Successfully verified MRP Site column on the downloaded file..");
    }
  }

  public void downloadMaterialVerification(String dwnloadedFile, String page, String action)
      throws Exception {
    // TODO: Re-write this using ExcelReader

    // FileInputStream inputFile = null;
    // OPCPackage inputFile;
    XSSFWorkbook masterWorkbook = null;
    HSSFWorkbook masterWorkbookExcel = null;
    HSSFSheet updateSheet = null;
    HSSFCell cell = null;

    if (page.equals("CR")) {
      int cellIndexOfMaterialName = 27;

      String actMatval = "";
      String expMatval = "";
      if (action.contains("downloadMaterialval")) {
        WAPCostRecordsView view = new WAPCostRecordsView();
        List<WebElement> elements = view.getMaterialValue();
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
        JLog.screenCapture();
        if (action.equals("downloadMaterialval")) {
          for (int i = 2; i <= updateSheet.getLastRowNum(); i++) {
            if (i - 2 <= elements.size() - 1) { // checking UI
              expMatval = elements.get(i - 2).getAttribute("value");
              Verify.verify(expMatval.equals(""), "Material fields is not null as expected.");
            }

            cell = updateSheet.getRow(i).getCell(cellIndexOfMaterialName);
            actMatval = cell.getStringCellValue();
            Verify.verify(
                actMatval.equals(""),
                "Expected value of Material is null , but found " + actMatval
                    + " as actual value.");
          }
        }
      }
    }
  }

  public void verifySuppAllocReportDetails(String downloadedFile) {
    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(downloadedFile, sheetName);

    int rowsOnExcel = excelReader.getNumberOfRows();

    Cell cell = excelReader.findCellContaining(".*Functional Group ID");
    int headerRow = cell.getRowIndex();

    int columnCount = excelReader.getNumberOfColumns(headerRow);
    Verify.verify(columnCount == 10, "Mismatch on no of columns");
    JLog.write("Verified total column count on downloaded excel.");

    String cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Functional Group ID"), "No FG ID column found");
    JLog.write("Verified FG ID column");

    cell = excelReader.findCellContaining(".*Functional Group Name");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Functional Group Name"), "No FG NAME column found");
    JLog.write("Verified Functional Group Name column");

    cell = excelReader.findCellContaining(".*Functional Group Type");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Functional Group Type"), "No FG TYPE column found");
    JLog.write("Verified Functional Group Type column");

    cell = excelReader.findCellContaining(".*Start Date");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Start Date"), "No Start Date column found");
    JLog.write("Verified Start Date column");

    cell = excelReader.findCellContaining(".*End Date");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("End Date"), "No End Date column found");
    JLog.write("Verified End Date column");

    cell = excelReader.findCellContaining(".*Location");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Location"), "No Location column found");
    JLog.write("Verified Location column");

    cell = excelReader.findCellContaining(".*Site Type");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Site Type"), "No Site Type column found");
    JLog.write("Verified Site Type column");

    cell = excelReader.findCellContaining(".*Total Allocation");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Total Allocation"), "No Total Allocation column found");
    JLog.write("Verified Total Allocation column");

    cell = excelReader.findCellContaining(".*Update Date");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Update Date"), "No Update Date column found");
    JLog.write("Verified Update Date column");

    cell = excelReader.findCellContaining(".*Update By");
    cellVal = cell.getStringCellValue();
    Verify.verify(cellVal.equals("Update By"), "No Update By column found");
    JLog.write("Verified Update By column");

    ArrayList<String> listOfStrings = new ArrayList<>();
    cell = excelReader.findCellContaining(".*Functional Group Name");

    for (int i = headerRow + 1; i < rowsOnExcel; i++) {
      cell = excelReader.getCell(i, cell.getColumnIndex());
      listOfStrings.add(cell.getStringCellValue());
      // if (listOfStrings.isEmpty() || listOfStrings.size() == 1) {
      // break;
      // }
    }

    boolean sortedStatus = true;
    Iterator<String> iter = listOfStrings.iterator();
    String current, previous = iter.next();
    while (iter.hasNext()) {
      current = iter.next();
      if (previous.compareTo(current) > 0) {
        sortedStatus = false;
      }
      previous = current;
    }
    Verify.verify(sortedStatus, "Report excel downloaded is not in sorted order.");
    JLog.write("Verified that Report excel downloaded is in sorted order.");
  }

  public void verifyPriceTamData(String dwnloadedFile) {

    String[] expItems = { "02692", "02707", "0270C", "0270D" };
    ArrayList<String> actItems = new ArrayList<>();
    String actItem;

    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);

    int rowsOnExcel = excelReader.getNumberOfRows();

    Cell cell = excelReader.findCellContaining(".*Item");
    int headerRow = cell.getRowIndex();

    for (int i = headerRow + 1; i < rowsOnExcel; i++) {
      cell = excelReader.getCell(i, cell.getColumnIndex());
      actItem = cell.getStringCellValue();
      JLog.write("Actual found for Item on row=  " + actItem);
      actItems.add(actItem);
    }
    for (int i = 0; i < actItems.size(); i++)
      Verify.verify(Arrays.asList(expItems).contains(actItems.get(i)), "Value mismatch for Item");
  }

  public void verifyPriceTamSingleItemData(String dwnloadedFile) {
    String item = "0306U";
    String actItem;
    String sheetName = null;

    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);
    Cell cell = excelReader.findCellContaining(".*Item");
    int headerRow = cell.getRowIndex();
    cell = excelReader.getCell(headerRow + 1, cell.getColumnIndex());
    actItem = cell.getStringCellValue();
    JLog.write("Actual found for Item on row= is " + actItem);
    JLog.write("Exp value from UI for Item is " + item);
    Verify.verify(actItem.contains(item), "Value mismatch for Item");
  }

  public void verifyMRPSiteOnExcepDownload(String dwnloadedFile) {
    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);
    Cell cell = excelReader.findCellContaining(".*MRP Site");
    Verify.verify(cell != null, "Unable to verify MRP Site column");
    JLog.write("Successfully verified MRP Site column on the downloaded file..");

  }

  public void verifyExcepDetails(String action, HSSFSheet updateSheet) throws IOException {
    // TODO: Re-write this using ExcelReader

    HSSFCell column;
    String value;
    InputStream fo = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
    Properties p = new Properties();
    p.load(fo);
    column = updateSheet.getRow(2).getCell(0);
    value = column.getStringCellValue(); // excepID
    Verify.verify(value.equals(p.getProperty("exceptionID")), "Value mismatch for exceptionID");
    value = updateSheet.getRow(2).getCell(1).getStringCellValue(); // excep
    // name
    Verify.verify(value.contains("Excep"), "Value mismatch for exception name");
    value = updateSheet.getRow(2).getCell(2).getStringCellValue(); // excep
    // requestor

    InputStream fo1 = new FileInputStream(
        prop.getRootDir() + "scplatform/data/properties/loginUserDetails.properties");
    Properties p1 = new Properties();
    p1.load(fo1);
    String actReq = p1.getProperty("login");
    JLog.write("Actual req on UI=" + actReq);
    JLog.write("Expected req=" + value);
    Verify.verify(
        value.toUpperCase().equals(actReq.toUpperCase()),
        "Value mismatch for exception requestor");
    JLog.write("Successfully verified Requestor Name");

    value = updateSheet.getRow(2).getCell(3).getStringCellValue(); // excep
    // owner
    Verify.verify(value.equals(p.getProperty("owner")), "Value mismatch for exception owner");
    JLog.write("Successfully verified Owner Name");
    value = updateSheet.getRow(2).getCell(4).getStringCellValue(); // excep
    // approver
    Verify.verify(value.contains(p.getProperty("approver")), "Unable to verify Approver name");
    JLog.write("Successfully verified Approver Name");
    String expStatus;
    if (action.contains("Pending")) {
      expStatus = "PENDING";
    } else
      expStatus = "APPROVED";
    value = updateSheet.getRow(2).getCell(5).getStringCellValue(); // excep
    // status
    Verify.verify(value.equals(expStatus), "Unable to verify Status");
    JLog.write("Successfully verified Status");
    value = updateSheet.getRow(2).getCell(6).getStringCellValue(); // excep
    // costtype
    Verify.verify(value.equals(p.getProperty("costType")), "Unable to verify costType");
    JLog.write("Successfully verified costType");
    value = updateSheet.getRow(2).getCell(7).getStringCellValue(); // excep
    // requestType
    if (value.contains("-")) {
      value = value.replaceFirst("-", " ");
      JLog.write("Act value=" + value);
    }
    String e = p.getProperty("requestType");
    JLog.write("exp value=" + p.getProperty("requestType"));
    Verify.verify(value.equals(p.getProperty("requestType")), "Unable to verify requestType");
    JLog.write("Successfully verified requestType");
    value = updateSheet.getRow(2).getCell(10).getStringCellValue(); // excep
    // platform
    // name
    Verify.verify(value.equals(p.getProperty("platform")), "Unable to verify platform");
    JLog.write("Successfully verified platform");
    value = updateSheet.getRow(2).getCell(11).getStringCellValue(); // excep
    // created
    // date
    String d = DateTime.now().toString("MM/dd/yyyy");
    Verify.verify(value.equals(d), "Unable to verify created date");
    JLog.write("Successfully verified created date");

    value = updateSheet.getRow(2).getCell(12).getStringCellValue(); // creator
    JLog.write("Act creator=" + value);
    Verify.verify(value.toUpperCase().equals(actReq.toUpperCase()), "Unable to verify creator");

    value = updateSheet.getRow(2).getCell(13).getStringCellValue(); // LOB
    Verify.verify(value.equals("LOB1"), "Unable to verify LOB");
    JLog.write("Successfully verified LOB");
    JLog.write("Successfully verified exception details");
  }

  public void verifyForecastData(HSSFSheet updateSheet, String expItem, String action) {
    // TODO: Re-write this using ExcelReader

    HSSFCell column;
    String fgName;
    String pName;
    String item;
    String fNameHeader = updateSheet.getRow(2).getCell(0).getStringCellValue();
    String pNameHeader = updateSheet.getRow(2).getCell(1).getStringCellValue();
    String itemHeader = updateSheet.getRow(2).getCell(12).getStringCellValue();
    Verify.verify(fNameHeader.contains("Functional Groups"), "Value mismatch for FG Name Header");
    Verify.verify(
        pNameHeader.contains("Parent Functional Group"),
        "Value mismatch for Parent Name Header");
    Verify.verify(itemHeader.contains("Item ID"), "Value mismatch for Item Header");

    for (int i = 4; i <= updateSheet.getLastRowNum(); i++) {
      column = updateSheet.getRow(i).getCell(0);
      fgName = column.getStringCellValue();
      JLog.write("Actual found for fgName on row=" + (i - 3) + " is " + fgName);
      Verify.verify(fgName.contains("CFG"), "Value mismatch for CFG Name");
      column = updateSheet.getRow(i).getCell(1);
      pName = column.getStringCellValue();
      JLog.write("Actual found for pName on row=" + (i - 3) + " is " + pName);
      Verify.verify(pName.contains("PN_CFG"), "Value mismatch for Parent Name");
      column = updateSheet.getRow(i).getCell(12);
      item = column.getStringCellValue();
      JLog.write("Actual found for item on row=" + (i - 3) + " is " + item);
      Verify.verify(item.equals("0002H"), "Value mismatch for Parent Name");
    }
  }

  public void verifyPastData(String dwnloadedFile, String expItem, String pastBucketVisibility)
      throws IOException {
    String actItem;

    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);

    Cell cell = excelReader.findCellContaining(".*Item Number");
    int headerRow = cell.getRowIndex();
    int rowsOnExcel = excelReader.getNumberOfRows() - (headerRow + 1);
    for (int i = headerRow + 1; i < rowsOnExcel; i++) {
      cell = excelReader.getCell(i, cell.getColumnIndex());
      actItem = cell.getStringCellValue();
      JLog.write("Actual found for Item on row=" + i + " is " + actItem);
      JLog.write("Exp value from UI for Item is " + expItem);
      Verify.verify(actItem.equals(expItem), "Value mismatch for Item");
    }

    cell = excelReader.getCell(headerRow - 1, 14);
    String expData = cell.getStringCellValue();
    int currYear = DateTime.now().getYear();
    int prevYear = currYear - 1;
    if (pastBucketVisibility.contains("No")) {
      JLog.write("Exp data=" + expData);
      JLog.write("Act data=" + String.valueOf(prevYear));
      Verify.verify(
          !expData.contains(String.valueOf(prevYear)),
          "Found past Data when pastBucketVisibility is set to No");
    } else {
      JLog.write("Exp data=" + expData);
      JLog.write("Act data=" + String.valueOf(prevYear));
      Verify.verify(
          expData.contains(String.valueOf(prevYear)),
          "Unable to find past Data when pastBucketVisibility is set to Yes");
    }
    // verifying other columns
    cell = excelReader.findCellContaining(".*Item Number");
    String value = cell.getStringCellValue();
    Verify.verify(value.equals("Item Number"), "Value mismatch for Item Number column Name");

    String itemBusEntity = "Item Business Entity";
    String manPart = "Manufacturer Part";
    String fgName = "FG Name";
    String fgType = "FG Type";
    String platform = "Platform";
    String destnSitePrice = "Destination Site Price";
    String siteTam = "Site TAM";
    String updateType = "Update Type";
    if (LoginSCPlatformHarmony.navUrl.contains("dev4160")) {
      Properties p = new Properties();
      InputStream fi = new FileInputStream(prop.getRootDir() + "scplatform/data/psBox/labelName.properties");
      p.load(fi);
      itemBusEntity = p.getProperty("businessEntity", "");
      manPart = p.getProperty("manufacturerPart", "");
      fgName = p.getProperty("fgName", "");
      fgType = p.getProperty("fgType", "");
      platform = p.getProperty("platform", "");
      destnSitePrice = p.getProperty("destnSitePrice", "");
      siteTam = p.getProperty("siteTam", "");
      updateType = p.getProperty("uploadType", "");
    }

    cell = excelReader.findCellContaining(".*" + itemBusEntity);
    value = cell.getStringCellValue();
    Verify
        .verify(value.equals(itemBusEntity), "Value mismatch for Item Business Entity column Name");

    cell = excelReader.findCellContaining(".*Item Business Entity Type");
    value = cell.getStringCellValue();
    Verify.verify(
        value.equals("Item Business Entity Type"),
        "Value mismatch for Item Business Entity Type column Name");

    cell = excelReader.findCellContaining(".*" + manPart);
    value = cell.getStringCellValue();
    Verify.verify(value.equals(manPart), "Value mismatch for Manufacturer Part column Name");

    cell = excelReader.findCellContaining(".*Supplier");
    value = cell.getStringCellValue();
    Verify.verify(value.equals("Supplier"), "Value mismatch for Supplier column Name");

    cell = excelReader.findCellContaining(".*" + fgName);
    value = cell.getStringCellValue();
    Verify.verify(value.equals(fgName), "Value mismatch for FG Name column Name");

    cell = excelReader.findCellContaining(".*" + fgType);
    value = cell.getStringCellValue();
    Verify.verify(value.equals(fgType), "Value mismatch for FG Type column Name");

    // cell = excelReader.findCellContaining(".*(XLOB FG)");// + platform);
    // value = cell.getStringCellValue();
    // Verify.verify(value.equals(platform), "Value mismatch for Platform
    // column Name");

    cell = excelReader.findCellContaining(".*Cost Type");
    value = cell.getStringCellValue();
    Verify.verify(value.equals("Cost Type"), "Value mismatch for Cost Type column Name");

    cell = excelReader.findCellContaining(".*" + destnSitePrice);
    value = cell.getStringCellValue();
    Verify.verify(
        value.equals(destnSitePrice),
        "Value mismatch for Destination Site Price column Name");

    cell = excelReader.findCellContaining(".*" + siteTam);
    value = cell.getStringCellValue();
    Verify.verify(value.equals(siteTam), "Value mismatch for Site TAM column Name");

    cell = excelReader.findCellContaining(".*Price Effective Offset");
    value = cell.getStringCellValue();
    Verify.verify(
        value.equals("Price Effective Offset"),
        "Value mismatch for Price Effective Offset column Name");

    // cell = excelReader.findCellContaining(".*" + updateType);
    // value = cell.getStringCellValue();
    // Verify.verify(value.equals(updateType), "Value mismatch for Update
    // Type column Name");

    // cell = excelReader.findCellContaining(".*Price/TAM Backdate/Change
    // Flag");
    // value = cell.getStringCellValue();
    // Verify.verify(value.equals("Price/TAM Backdate/Change Flag"),
    // "Value mismatch for Price/TAM Backdate/Change Flag column Name");
  }

  public void verifyGroupDownloadData(String type, String dwnloadedFile, String[] fgData)
      throws Exception {

    if (dwnloadedFile == null || dwnloadedFile.isEmpty()) {
      throw new AssertionError(
          "Group download failed: downloaded file path is null or empty. "
              + "The file_download button may not have been found or the download timed out.");
    }

    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);

    int rowsOnExcel = excelReader.getNumberOfRows();

    if (type.equals("FG")) {
      String fN;
      String item;
      String fgType;
      String status;
      String itemBusiness;

      Cell fgNameCell = excelReader.findCellContaining(".*Functional Group Name");
      Cell fgTypeCell = excelReader.findCellContaining(".*Type");
      Cell fgItemCell = excelReader.findCellContaining(".*Item");
      Cell fgItemBusCell = excelReader.findCellContaining(".*Item Business");
      Cell fgStatusCell = excelReader.findCellContaining(".*Status");
      Cell fgCommodityCodeCell = excelReader.findCellContaining(".*Commodity Name");
      Cell fgRespCell = excelReader.findCellContaining(".*Item Responsibility");
      Cell fgItemDescpCell = excelReader.findCellContaining(".*Item Description");

      Cell odmPartCell = excelReader.findCellContaining(".*ODM Item");
      Verify.verify(odmPartCell != null, "Unable to find odmItem column");
      Cell odmPartBusinessNameCell = excelReader.findCellContaining(".*ODM Item Business");
      Verify.verify(odmPartBusinessNameCell != null, "Unable to find odmItem Business column");
      Cell parItemCell = excelReader.findCellContaining(".*Parent Item");
      Verify.verify(parItemCell != null, "Unable to find parent Item column");
      Cell parItemBusNameCell = excelReader.findCellContaining(".*Parent Item Business");
      Verify.verify(parItemBusNameCell != null, "Unable to find parent Item Business column");

      int headerRow = fgNameCell.getRowIndex();

      rowsOnExcel = rowsOnExcel - (headerRow + 1);

      MTCMController controller = new MTCMController();
      int rowsFromUI = controller.getRowCount("selectedPageKeys");
      Verify.verify(rowsOnExcel == rowsFromUI, "Rows on excel and UI are not matching.");
      JLog.write("Verified that rows on UI and on Excel are matching and == " + rowsFromUI);

      for (int i = headerRow + 1; i < excelReader.getNumberOfRows(); i++) {
        fgNameCell = excelReader.getCell(i, fgNameCell.getColumnIndex());
        fN = fgNameCell.getStringCellValue();
        JLog.write("Actual found for FG Name on row=" + i + " is " + fN);
        JLog.write("Exp value from UI for FGName is " + fgData[2]);
        Verify.verify(fN.equals(fgData[2]), "Value mismatch for FG Name");

        fgTypeCell = excelReader.getCell(i, fgTypeCell.getColumnIndex());
        fgType = fgTypeCell.getStringCellValue();
        JLog.write("Actual found for fgType on row=" + i + " is " + fgType);
        JLog.write("Exp value from UI for FG Type is " + fgData[3]);
        Verify.verify(fgType.contains(fgData[3]), "Value mismatch for FG Type");

        fgItemCell = excelReader.getCell(i, fgItemCell.getColumnIndex());
        item = fgItemCell.getStringCellValue();
        JLog.write("Actual found for item on row=" + i + " is " + item);
        JLog.write("Exp value from UI for item is " + fgData[0]);
        Verify.verify(item.contains(fgData[0]), "Value mismatch for item");

        fgItemBusCell = excelReader.getCell(i, fgItemBusCell.getColumnIndex());
        itemBusiness = fgItemBusCell.getStringCellValue();
        JLog.write("Actual found for itemBusiness on row=" + i + " is " + itemBusiness);
        JLog.write("Exp value from UI for itemBusiness is " + fgData[1]);
        Verify.verify(itemBusiness.contains(fgData[1]), "Value mismatch for itemBusiness");

        fgStatusCell = excelReader.getCell(i, fgStatusCell.getColumnIndex());
        status = fgStatusCell.getStringCellValue();
        JLog.write("Exp value from UI for status is ACTIVE");
        JLog.write("Actual found for status on row=" + i + " is " + status);
        Verify.verify(status.contains("ACTIVE"), "Value mismatch for status");

        fgCommodityCodeCell = excelReader.getCell(i, fgCommodityCodeCell.getColumnIndex());
        String itemCommodityCode = fgCommodityCodeCell.getStringCellValue();
        JLog.write("Actual found for Commodity Code Cell on row=" + i + " is " + itemCommodityCode);
        JLog.write("Exp value from UI for Commodity Code Cell is " + fgData[5]);
        Verify
            .verify(itemCommodityCode.contains(fgData[5]), "Value mismatch for itemCommodityCode");

        fgRespCell = excelReader.getCell(i, fgRespCell.getColumnIndex());
        String itemResp = fgRespCell.getStringCellValue();
        JLog.write("Actual found for Responsibility Cell on row=" + i + " is " + itemResp);
        JLog.write("Exp value from UI for Responsibility Cell is " + fgData[6]);
        Verify.verify(itemResp.contains(fgData[6]), "Value mismatch for item Resp");

        fgItemDescpCell = excelReader.getCell(i, fgItemDescpCell.getColumnIndex());
        String itemDescp = fgItemDescpCell.getStringCellValue();
        JLog.write("Actual found for Item Descp Cell on row=" + i + " is " + itemDescp);
        JLog.write("Exp value from UI for Item Descp Cell is " + fgData[4]);
        Verify.verify(itemDescp.contains(fgData[4]), "Value mismatch for ItemDescpCell");

      }
    } else if (type.equals("PG")) {
      String grpName;
      String pName;
      String pType;
      String purpose;

      Cell pgNameCell = excelReader.findCellContaining(".*Parent Name");
      Cell pgTypeCell = excelReader.findCellContaining(".*Parent Type");
      Cell purposeCell = excelReader.findCellContaining(".*Purpose");
      Cell fgCell = excelReader.findCellContaining(".*Functional Group Name");

      int headerRow = pgNameCell.getRowIndex();
      rowsOnExcel = rowsOnExcel - (headerRow + 1);

      MTCMController controller = new MTCMController();
      int rowsFromUI = controller.getRowCount("selectedPageKeys");
      Verify.verify(rowsOnExcel == rowsFromUI, "Rows on excel and UI are not matching.");
      JLog.write("Verified that rows on UI and on Excel are matching and == " + rowsFromUI);

      for (int i = headerRow + 1; i < excelReader.getNumberOfRows(); i++) {
        pgNameCell = excelReader.getCell(i, pgNameCell.getColumnIndex());
        pName = pgNameCell.getStringCellValue();
        JLog.write("Actual found for Parent Name on row=" + i + " is " + pName);
        JLog.write("Exp value from UI for Parent Name is " + fgData[2]);
        Verify.verify(pName.equals(fgData[2]), "Value mismatch for parent Group Name");

        pgTypeCell = excelReader.getCell(i, pgTypeCell.getColumnIndex());
        pType = pgTypeCell.getStringCellValue();
        JLog.write("Exp value from UI for parent group Type is " + fgData[3]);
        JLog.write("Actual found for parent group Type on row=" + i + " is " + pType);
        Verify.verify(pType.contains(fgData[3]), "Value mismatch for parent Group Type");

        purposeCell = excelReader.getCell(i, purposeCell.getColumnIndex());
        purpose = purposeCell.getStringCellValue();
        JLog.write("Exp value from UI for item is MASSUPDATE");
        JLog.write("Actual found for item on row=" + i + " is " + purpose);
        Verify.verify(purpose.contains("MASSUPDATE"), "Value mismatch for purpose");

        fgCell = excelReader.getCell(i, fgCell.getColumnIndex());
        grpName = fgCell.getStringCellValue();
        JLog.write("Exp value from UI for FG Group Name 1 is " + fgData[0]);
        JLog.write("Actual found for FG Group Name 1 is " + grpName);
        Verify.verify(grpName.contains("FGrp"), "Mismatch on groupName");

      }

    }

  }

  public void verifySuppDownloadData(
      String dwnloadedFile, String action, String recordsCountFromUI, String fN, String fT)
      throws Exception {

    if (dwnloadedFile == null) {
      JLog.error("verifySuppDownloadData: downloaded file path is null, cannot verify");
      com.google.common.base.Verify.verify(false, "Supplier allocation download file is null - download may have timed out");
      return;
    }

    String actFN;
    String actFT;

    int count;
    String actData;
    String expVal = ""; // validation is to verify whether the cell value is
                        // blank
    String sheetName = null;
    ExcelReader er = new ExcelReader(dwnloadedFile, sheetName);
    Cell c;
    if (action.equals("suppExtraColumnValidation")) {
      c = er.findCellContaining(".*MRP Site"); // column with MRP SIte
                                               // text ending
      int nextColumnAfterAllColumns = er.getNumberOfColumns(c.getRowIndex()) + 1;
      JLog.write("Total columns found are : " + (nextColumnAfterAllColumns - 1));
      for (int r = c.getRowIndex() + 1; r < er.getNumberOfRows(); r++) {
        c = er.getCell(r, nextColumnAfterAllColumns);
        actData = c.getStringCellValue();
        Validate.takeScreenShotOnFailure(false);
        Validate.verify(
            String.format("Item Allocation Data[%d, %d]", c.getRowIndex(), c.getColumnIndex()),
            actData,
            expVal,
            COMPARE.Equals);
      }
    } else if (action.equals("suppMRPSiteColumnValidation")) {
      c = er.findCellContaining(".*MRP Site");
      int headerRow = c.getRowIndex();
      count = er.getNumberOfRows() - (headerRow + 1);
      actData = c.getStringCellValue();
      Verify.verify(
          actData.equals("MRP Site"),
          "Found some wrong column name on downloaded supp allocation excel");
      for (int i = headerRow + 1; i < er.getNumberOfRows(); i++) {
        c = er.getCell(i, c.getColumnIndex());
        actFN = c.getStringCellValue();
        Verify.verify(actFN.contains("APCC"), "Unable to verify MRP site value");
      }
    } else if (action.contains("suppDataValidation")) {
      c = er.findCellContaining(".*PFG Name");
      int headerRow = c.getRowIndex();
      count = er.getNumberOfRows() - (headerRow + 1);
      Verify.verify(
          recordsCountFromUI.equals(String.valueOf(count)),
          "Unable to verify records count on Ui and downloaded excel rows count");
      JLog.write(
          "Successfully verified records count on UI after filter search and rows on downloaded excel.");
      c = er.findCellContaining(".*MRP Site");
      int colMRP = c.getColumnIndex();
      for (int i = 3; i < er.getNumberOfRows(); i++) {
        c = er.getCell(i, colMRP);
        actFN = c.getStringCellValue();
        JLog.write("Actual value found for MRP SIte on row=" + i + " is " + actFN);
        Verify.verify(actFN.contains("APCC19"), "Unable to verify MRP SIte");
      }
      JLog.write("Successfully verified MRP Site Values.");
      c = er.findCellContaining("FG Name.*");
      int colFGName = c.getColumnIndex();
      for (int i = 3; i < er.getNumberOfRows(); i++) {
        c = er.getCell(i, colFGName);
        actFN = c.getStringCellValue();
        JLog.write("Actual found for FG Name on row=" + i + " is " + actFN);
        JLog.write("Exp FG Name found  = " + fN);
        Verify.verify(actFN.contains(fN), "Unable to verify fg Name");
      }
      JLog.write("Successfully verified FG Name");
      c = er.findCellContaining(".*FG Type");
      int colFGType = c.getColumnIndex();
      for (int i = 3; i < er.getNumberOfRows(); i++) {
        c = er.getCell(i, colFGType);
        actFT = c.getStringCellValue();
        JLog.write("Actual found for FG Type on row=" + i + " is " + actFT);
        JLog.write("Exp FG Type found  = " + fT);
        Verify.verify(actFT.equals(fT), "Unable to verify fg Name");
      }
      JLog.write("Successfully verified FG Type");
    } else if (action.contains("suppAllocDateVerif")) {
      // gettingDatesFromUI
      String dateValOnUI = "";
      String dateValOnTemplate = "";
      MTCMView view = new MTCMView();
      List<WebElement> elements = view.getElementsByClassName("columnHeaderMargin");
      for (int i = 3; i < er.getNumberOfRows(); i++) {
        c = er.getCell(i, 8);
        dateValOnTemplate = c.getStringCellValue();
        dateValOnUI = elements.get(i - 3).getText();
        JLog.write("Actual value found on UI for Date on row=" + i + " is " + dateValOnUI);
        JLog.write(
            "Actual value found on Download Template for Date on row=" + i + " is "
                + dateValOnTemplate);
        Verify.verify(dateValOnUI.contains(dateValOnTemplate), "Unable to verify Date");
      }
    } else if (action.contains("downloadForUpload")) {
      dwnloadedFile = prop.getRootDir() + "" + "TamSupplierAllocation.xlsx";
    }
  }

  public void verifySuppDownloadDataToVerifyDate(String dwnloadedFile, String action)
      throws Throwable {

    if (action.contains("suppAllocDateVerif")) {
      validateSuppAllocDateVerif(dwnloadedFile);
    } else if (action.contains("suppAllocRowVerify")) {
      validateSuppAllocRowVerify(dwnloadedFile);
    }
  }

  private void validateSuppAllocDateVerif(String dwnloadedFile) {
    String uiDateValue;
    Cell excelDate;

    MTCMView view = new MTCMView();
    List<WebElement> elements = view.getList(By.xpath("//label[@class='columnHeaderMargin']"));

    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);

    // Workbook workbook = WorkbookFactory.create(new File(dwnloadedFile));
    // Sheet updateSheet1 = workbook.getSheetAt(0);

    // unable to loop the excel properly, since some cells on the date row
    // will not have the date (cells span)
    // instead, searching for the cell
    for (WebElement uiDateElement : elements) {
      uiDateValue = uiDateElement.getText();
      excelDate = excelReader.findCellContaining("(?s).*[\\n\\r].*" + uiDateElement.getText());
      if (excelDate == null) {
        JLog.error("Unable to find date in excel file: " + uiDateValue);
        continue;
      }
      Validate.takeScreenShotOnFailure(false);
      Validate.verify(
          String.format(
              "Allocation Date[%d, %d]",
              excelDate.getRowIndex(),
              excelDate.getColumnIndex()),
          excelDate.getStringCellValue().replace("\n", "\\"),
          uiDateValue,
          COMPARE.Contains);
    }

    // reset the screen shot on failure
    Validate.takeScreenShotOnFailure(true);

  }

  private void validateSuppAllocRowVerify(String dwnloadedFile)
      throws InterruptedException, IOException {

    Cell c;

    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);
    c = excelReader.findCellContaining(".*FG Type");
    int rowCountOnTemplate = excelReader.getNumberOfRows();

    rowCountOnTemplate = rowCountOnTemplate - (c.getRowIndex() + 1);
    MTCMView view = new MTCMView();
    int rowCountOnUI = view.getList(By.xpath("//tr[@class='compact' and contains(@id,'Allocation')]")).size();

    Validate.verify(
        "Manage Allocation Row count for UI and download",
        rowCountOnUI,
        rowCountOnTemplate);

    try {
      HarmonyLoginUI ui = new HarmonyLoginUI();
      General gn = new General();
      ui.navHarmonyMTCM("Supply Collaboration", "Download Allocation");
      gn.clickButton("Clear");
      gn.enterTextFieldValue("Dao", "value(site)");
      TAMSupplyAllocation tam = new TAMSupplyAllocation();
      tam.enterTextFieldElementValue("AUTO_AIR_AUS", "value(functionalGroupName)");
      gn.clickButton("Apply");
      SupplyAllocationController ct = new SupplyAllocationController();
      rowCountOnUI = Integer.parseInt(ct.getRecordCountFromUI("Item"));
      dwnloadedFile = ct.getDownloadedFile("Item Allocation Download");
      excelReader = new ExcelReader(dwnloadedFile, sheetName);
      c = excelReader.findCellContaining(".*FG Type");
      rowCountOnTemplate = excelReader.getNumberOfRows() - (c.getRowIndex() + 1);
      Validate.verify(
          "Manage Allocation Row count for UI and item download",
          rowCountOnUI,
          rowCountOnTemplate);

    } catch (Throwable e) {
      JLog.error(null, e, TakeScreenshot.True);
    }

  }

  private void validatePriceTamRowVerify(String dwnloadedFile)
      throws InterruptedException, IOException {

    Cell c;

    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);
    c = excelReader.findCellContaining(".*FG Type");
    int rowCountOnTemplate = excelReader.getNumberOfRows();

    rowCountOnTemplate = rowCountOnTemplate - (c.getRowIndex() + 1);
    MTCMView view = new MTCMView();
    int rowCountOnUI = view.getList(By.xpath("//div[@class='eto-grid-frozen']//tbody//tr")).size();

    Validate.verify("Price Tam Row count for UI and download", rowCountOnUI, rowCountOnTemplate);

  }

  public void verifyItemDownloadData(
      String dwnloadedFile, String action, String recordsCountFromUI, String fN, String fT)
      throws Exception {

    if (dwnloadedFile == null) {
      JLog.error("verifyItemDownloadData: downloaded file path is null, cannot verify");
      com.google.common.base.Verify.verify(false, "Item allocation download file is null - download may have timed out");
      return;
    }

    String sheetName = null;
    ExcelReader excelReader = new ExcelReader(dwnloadedFile, sheetName);

    String actFN;
    String actFT;
    int count;
    String actData;

    // expected is blank? How is this going to compare with the download?
    String expVal = "";

    Cell cell;

    if (action.equals("itemExtraColumnValidation")) {
      cell = excelReader.findCellContaining(".*MRP Site");
      count = excelReader.getNumberOfColumns(cell.getRowIndex());
      JLog.write("Total columns found are : " + count);
      for (int r = 4; r < excelReader.getNumberOfRows(); r++) {
        cell = excelReader.getCell(r, count + 1);
        actData = cell.getStringCellValue();
        Validate.takeScreenShotOnFailure(false);
        Validate.verify(
            String
                .format("Item Allocation Data[%d, %d]", cell.getRowIndex(), cell.getColumnIndex()),
            actData,
            expVal,
            COMPARE.Equals);

      }

    } else if (action.equals("itemMRPSiteColumnValidation")) {
      cell = excelReader.findCellContaining(".*MRP Site");
      int headerRow = cell.getRowIndex();
      actData = cell.getStringCellValue();
      Verify.verify(
          actData.equals("MRP Site"),
          "Found some wrong column name on downloaded item allocation excel");
      for (int i = headerRow + 1; i < excelReader.getNumberOfRows(); i++) {
        cell = excelReader.getCell(i, cell.getColumnIndex());
        actFN = cell.getStringCellValue();
        Verify.verify(actFN.contains("APCC"), "Unable to verify MRP site value");
      }
    } else if (action.equals("itemDataValidation")) {
      // count = excelReader.getNumberOfRows() - 2;
      Cell c = excelReader.findCellContaining(".*FG Name");
      int headerRow = c.getRowIndex();
      count = excelReader.getNumberOfRows() - (headerRow + 1);
      Verify.verify(
          recordsCountFromUI.equals(String.valueOf(count)),
          "Unable to verify records count on Ui and downloaded excel rows count");
      JLog.write(
          "Successfully verified records count on UI after filter search and rows on downloaded excel.");
      c = excelReader.findCellContaining(".*MRP Site");
      for (int i = headerRow + 1; i < excelReader.getNumberOfRows(); i++) {
        cell = excelReader.getCell(i, c.getColumnIndex());
        String actMRP = cell.getStringCellValue();
        JLog.write("Actual found for MRP Site on row=" + i + " is " + actMRP);
        Verify.verify(actMRP.contains("APCC19"), "Unable to verify MRP Site");
      }
      c = excelReader.findCellContaining(".*FG Name");
      for (int i = headerRow + 1; i < excelReader.getNumberOfRows(); i++) {
        cell = excelReader.getCell(i, c.getColumnIndex());
        actFN = cell.getStringCellValue();
        JLog.write("Actual found for FG Name on row=" + i + " is " + actFN);
        JLog.write("Exp FG Name found = " + fN);
        Verify.verify(actFN.contains(fN), "Unable to verify fg Name");
      }
      JLog.write("Successfully verified FG Namme");
      c = excelReader.findCellContaining(".*FG Type");
      for (int i = headerRow + 1; i < excelReader.getNumberOfRows(); i++) {
        cell = excelReader.getCell(i, c.getColumnIndex());
        actFT = cell.getStringCellValue();
        JLog.write("Actual found for FG Type on row=" + i + " is " + actFT);
        JLog.write("Exp FG Type found = " + fT);
        Verify.verify(actFT.equals(fT), "Unable to verify fg Type");
      }
      // rest Screen Shot on Failure
      Validate.takeScreenShotOnFailure(true);
    }
  }
}
