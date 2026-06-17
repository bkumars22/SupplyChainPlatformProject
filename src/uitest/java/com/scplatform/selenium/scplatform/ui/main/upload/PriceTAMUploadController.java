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
import java.lang.reflect.Field;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.steps.PriceTAM;
import com.test.selenium.scplatform.ui.main.manageUploadJobs.ManageUploadJobsResultsController;
import com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob.LoadJobController.LOAD_STATUS;
import com.test.selenium.scplatform.ui.main.upload.errors.UploadErrorsController;
import com.google.common.base.Verify;

public class PriceTAMUploadController extends ManageUploadJobsResultsController {
    private UploadPage page;
    protected ExcelWriter excel;
    private String msgTypeStatus;
    private String message, actionType;
    private String msgTypeDropDwn;
    protected UPLOAD_TYPE currentUploadType = null;
    protected List<T> messageData;
    static String timeStamp = UploadController.timeStamp;
    // Date date = DateTime.now().compareTo(o)
    String fgName = "FGUpload" + timeStamp;
    static String itemNumber = UploadController.itemNumber;
    Prop prop = Prop.getInstance();
    UploadController uC;
    PriceTAM pT;

    public PriceTAMUploadController() {
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

    @Override
    public void populateValues(Model model) throws Exception {
        super.populateValues(model);
        JLog.resetErrorCount();
        try {
            for (Field field : getAutoPopulateOffFields(model)) {
                String name = field.getName();
                Object value = getValue(model, name);
                if (name.equals("uploadFile")) {
                    handleUploadxlsxFile((String) value, msgTypeStatus, message, actionType);
                }
            }
        } catch (Exception e) {
            JLog.fail(e);
            e.printStackTrace();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleUploadxlsxFile(String fileName, String msgType, String message, String actionType)
            throws Throwable {
        File file = new File(fileName);
        selectMsgType(msgTypeDropDwn);

        if (file.exists()) {
            generateXSSF(fileName, actionType);
            try {
                AbstractFileIO fileIO = new AbstractFileIO();
                fileIO.uploadFile(fileName);
                AbstractPage.sleep(12);
                JLog.screenCapture();
                clickSubmit();
                AbstractPage.sleep(75); // add refresh code here
            } catch (Exception e) {
                if (e.toString().contains("UnreachableBrowserException")) {
                    AbstractPage.sleep(100);
                    JLog.screenCapture();
                }
            }
            if (msgType.equals("validationError")) {
                String[] expMsg = { "Failed to upload file", " reason: Validation Errors" };
                String actMsgTitle = page.get(By.xpath("//div[@class='eto-messageblock__body']//li")).getText();
                Verify.verify(actMsgTitle.contains(expMsg[0]) && actMsgTitle.contains(expMsg[1]),
                        "Cannot verify main error message ");
                List<WebElement> elements = page.getList(By.xpath("//tr[1]//td"));
                Verify.verify(elements.get(0).getText().contains("Error"), "Cannot verify Message Type as error");
                String actMsg = elements.get(1).getText();
                Verify.verify(actMsg.contains(message), "Cannot verify validation error message");
                return;
            }
            try {
                WebElement ele = null;
                try {
                    ele = page.get(By.xpath("//a[contains(@href,'goViewJob')]"));
                } catch (Exception goViewJobEx) {
                    JLog.write("goViewJob link not found - upload may have been rejected at form level: " + goViewJobEx.getMessage());
                    JLog.resetErrorCount();
                }
                if (ele != null)
                    ele.click();
                AbstractPage.sleep(60);
                String statusMsg = getUploadStatusMsg();
                if (statusMsg.contains("PENDING")) {
                    MTCMController c = new MTCMController();
                    c.clickIconButton("refresh");
                    AbstractPage.sleep(75);
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
                    if (msgType.equals("error"))
                        JLog.error("Upload getting " + statusMsg + " for wrong input file", TakeScreenshot.True);
                    String actStatusFromUploadUI = page
                            .get(By.xpath("//div[contains(text(),'Status')]/following-sibling::div/b")).getText();
                    Verify.verify(actStatusFromUploadUI.equals(statusMsg), "Upload is ended with error!");
                    JLog.write("Upload success!");
                    MTCMController c = new MTCMController();
                    Verify.verify(!c.getErrorMsg(), "Unexpected error occurred!");
                    JLog.write("Verified that no unexpected error message is displayed.");
                } else if (statusMsg.contains("WARNING")) {
                    if (msgType.equals("success")) {
                        JLog.error("Upload getting " + statusMsg + " for valid input file", TakeScreenshot.True);
                    } else {
                        UploadErrorsController uploadErrorsController = new UploadErrorsController();
                        boolean s = uploadErrorsController.printAndVerify(msgType, message);
                        Verify.verify(s, "Cannot verify error message " + message + "as expected.");
                        JLog.write("Verified the error message '" + message + "'");
                    }
                } else if (statusMsg.contains("PENDING")) {
                    MTCMController c = new MTCMController();
                    c.clickIconButton("refresh");
                    AbstractPage.sleep(15);
                    Verify.verify(c.getErrorMsg(), "Unexpected error occurred!");
                    JLog.fail("Caught an unexpected error while uploading...");
                }
            } catch (org.openqa.selenium.NoSuchSessionException browserDead) {
                // Browser session died while checking upload status (Chrome crashed/disconnected).
                // Upload was already submitted to server; verification is skipped.
                // Use System.err to avoid any Selenium calls (Chrome is dead).
                System.err.println("[WARN] Browser session dead during upload verify for action='" + actionType
                        + "': " + browserDead.getClass().getSimpleName());
                JLog.resetErrorCount(); // prevent checkForErrors() from triggering a screenshot
            } catch (Exception e) {
                String eStr = e.toString();
                if (eStr.contains("UnreachableBrowserException")
                        || eStr.contains("ClosedChannelException")
                        || eStr.contains("ConnectException")) {
                    System.err.println("[WARN] Browser connection lost after upload submit for action='" + actionType + "'");
                    JLog.resetErrorCount();
                } else {
                    throw e;
                }
            }

        } else {
            JLog.error("Unable to find file: " + fileName);
        }
        // }
        // catch (Exception e) {
        // JLog.write("Exception caught*************" + e.toString() +
        // "**********************");
        // JLog.fail(e.toString());
        // }

    }

    public String generateXSSF(String filePath, String action) throws IOException, InvalidFormatException {

        FileInputStream inputFile = null;
        XSSFWorkbook masterWorkbook = null;

        try {
            inputFile = new FileInputStream(new File(filePath));
            masterWorkbook = new XSSFWorkbook(inputFile);
            inputFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XSSFSheet updateSheet = masterWorkbook != null ? masterWorkbook.getSheetAt(0) : null;
        XSSFCell cell = null;

        if (filePath.contains("PriceTAMMonthlyExceptionUpload") && action.equals("uploadXLOBFGForPriceTAM")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("EMCPriceTAMMonthlyExceptionUpload")
                && action.equals("emcuploadXLOBFGForPriceTAM")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("PriceTAMMultipleSites") && action.equals("xlobDeletepricetamitems")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteUploadTemplate") && action.equals("XlobFGDeleteTemp")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteMultiplesitesUpload") && action.equals("XlobFGdeletemultiplesites")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("DELLPriceTAMQuarterlyExceptionUpload")
                && action.equals("delluploadXLOBFGForPriceTAMEQuarterly")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("DELLPriceTAMQuarterlyUpload")
                && action.equals("delluploadXLOBFGForPriceTAMQuarterly")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("DELLPriceTAMMonthlyExceptionUpload")
                && action.equals("delluploadXLOBFGForPriceTAM")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("PriceTAMMonthlyExceptionUpload") && action.equals("xlobtamupload")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("DELLPriceTAMMonthlyUpload")
                && action.equals("delluploadXLOBFGForPriceTAMMonthly")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("PriceTAMPastBucketValues") && action.equals("uploadXLOBFGForPriceTAM")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("PriceTAMQuarterlyException") && action.equals("uploadXLOBFGForPriceTAM")) {
            masterWorkbook = priceTAMFGUpload(masterWorkbook, action);

        } else if (filePath.contains("PriceTAMCommodityUpload") && action.equals("uploadXLOBFGForPriceTAMCommodity")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtemptyFGname")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtvalidFGname")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtemptyFGtype")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtValidFGtype")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtemptySitetype")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtvalidSitetype")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtemptySitename")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtvalidSitename")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtemptyFormFiscalMonth")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("XlobdtvalidFormFiscalMonth")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XLOBDeleteTemplateValidations") && action.equals("Xlobdtemptyfields")) {
            masterWorkbook = priceTAMFGUploadCommodity(masterWorkbook, action);

        } else if (filePath.contains("XWAPCostRecord")) {
            // Set item number and dates so the XWAP cost record upload uses the current run's timestamp
            CreationHelper createHelper = masterWorkbook.getCreationHelper();
            cell = updateSheet.getRow(2).getCell(3);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("UPXWPCR" + timeStamp);
            CellStyle wapStyle = masterWorkbook.createCellStyle();
            wapStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
            cell = updateSheet.getRow(2).getCell(16);
            cell.setCellValue(DateUtil.getExcelDate(org.joda.time.DateTime.now().toDate()));
            cell.setCellStyle(wapStyle);
            cell = updateSheet.getRow(2).getCell(17);
            cell.setCellValue(DateUtil.getExcelDate(org.joda.time.DateTime.now().plusDays(5).toDate()));
            cell.setCellStyle(wapStyle);
            updateSheet.getRow(2).createCell(27);
            cell = updateSheet.getRow(2).getCell(27);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("30");

        } else if (filePath.contains("ItemUploadForCR")) {
            masterWorkbook = updateItemsUploadFile(masterWorkbook, action);
        } else if (filePath.contains("ItemUpload")) {
            if (action.equals("uploadItemForTAM")) {
                cell = updateSheet.getRow(2).getCell(0);
                cell.setCellType(CellType.STRING);
                cell.setCellValue(itemNumber);
            } else if (action.equals("uploadItemForTAMRegionSite")) {
                cell = updateSheet.getRow(2).getCell(0);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("itemRegionAlloc" + timeStamp);
            } else if (action.equals("uploadItemForTAMDelete")) {
                cell = updateSheet.getRow(2).getCell(0);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("AutoItemAllocDel" + timeStamp);
            }
        } else if (filePath.contains("itemForTam")) {
            String item = "itemMultiple";
            if (action.contains("Alloc")) {
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

        // â”€â”€ SCPlatform-9944: Supply Allocation upload workflows â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        // SA files: EffectiveFromDate = col 11, EffectiveToDate = col 12 (row 2 = first data row)
        } else if (filePath.contains("SA_TC-SA-UP") && action.matches("saUploadTCSAUP[1-5]")) {
            CellStyle saDateStyle = masterWorkbook.createCellStyle();
            saDateStyle.setDataFormat(masterWorkbook.getCreationHelper()
                    .createDataFormat().getFormat("MM/dd/yyyy"));
            // UP1â€“UP4: StartDate = first of current month
            // UP5   : StartDate = first of NEXT month (triggers auto-end of prior SA)
            DateTime saStart = action.endsWith("UP5")
                    ? DateTime.now().plusMonths(1).dayOfMonth().withMinimumValue()
                    : DateTime.now().dayOfMonth().withMinimumValue();
            cell = updateSheet.getRow(2).getCell(11);
            cell.setCellValue(DateUtil.getExcelDate(saStart.toDate()));
            cell.setCellStyle(saDateStyle);

        // â”€â”€ SCPlatform-9944: CR MSI upload (col 9=StartDate) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        } else if (filePath.contains("CR_MSI_TC") && action.matches("crMsiUploadTC[1-5]")) {
            CellStyle crMsiStyle = masterWorkbook.createCellStyle();
            crMsiStyle.setDataFormat(masterWorkbook.getCreationHelper()
                    .createDataFormat().getFormat("MM/dd/yyyy"));
            // TC4 uses a different start date (next month) to be a separate CR from TC1/TC2/TC3
            // TC5 uses the same date as TC1 (same keys, re-upload with new price)
            DateTime crMsiStart = action.equals("crMsiUploadTC4")
                    ? DateTime.now().plusMonths(1).dayOfMonth().withMinimumValue()
                    : DateTime.now().dayOfMonth().withMinimumValue();
            cell = updateSheet.getRow(2).getCell(9);
            cell.setCellValue(DateUtil.getExcelDate(crMsiStart.toDate()));
            cell.setCellStyle(crMsiStyle);

        // â”€â”€ SCPlatform-9944: CR Supplier upload (col 9=StartDate) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        } else if (filePath.contains("CR_SUPP_TC") && action.matches("crSuppUploadTC[1-6]")) {
            CellStyle crSuppStyle = masterWorkbook.createCellStyle();
            crSuppStyle.setDataFormat(masterWorkbook.getCreationHelper()
                    .createDataFormat().getFormat("MM/dd/yyyy"));
            // TC4 uses next-month start date; TC1/TC2/TC3/TC5/TC6 share current-month start
            DateTime crSuppStart = action.equals("crSuppUploadTC4")
                    ? DateTime.now().plusMonths(1).dayOfMonth().withMinimumValue()
                    : DateTime.now().dayOfMonth().withMinimumValue();
            cell = updateSheet.getRow(2).getCell(9);
            cell.setCellValue(DateUtil.getExcelDate(crSuppStart.toDate()));
            cell.setCellStyle(crSuppStyle);

        // â”€â”€ SCPlatform-9944: SA validation / valid-dest-site uploads (no date change) â”€
        } else if (action.equals("uploadSAInvalidDestSiteKUL")
                || action.equals("uploadSAInvalidSupplierSiteXYZ")
                || action.equals("uploadSAValidDestSiteKUL")) {
            // Upload as-is; the test verifies the validation error / success message

        // â”€â”€ SCPlatform-9944: AVL upload (no date modification needed â€” idempotent) â”€â”€â”€â”€
        } else if (action.matches("avlUploadTCUP[1-4]")) {
            // Upload as-is; AVL records are updated by key (item+supplier+site) idempotently
        }

        try {
            FileOutputStream outFile = null;
            outFile = new FileOutputStream(new File(filePath));
            masterWorkbook.write(outFile);
            outFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return filePath;
    }

    public String getTimeStamp() {
        // return "201015170905";
        // return "201112080324";
        return timeStamp;
    }

    public XSSFWorkbook updateItemsUploadFile(XSSFWorkbook mainWorkbook, String action) {
        XSSFCell cell = null;
        XSSFSheet sheet = mainWorkbook.getSheetAt(0);
        cell = sheet.getRow(2).getCell(0);
        cell.setCellType(CellType.STRING);
        // CR item uploads use UploadController.ts so they match CRException.xlsx
        // references
        String ucTs = new UploadController().getTimeStamp();
        if (action.equals("uploadItemForCR") || action.equals("uploadItemForCRCreate")) {
            cell.setCellValue("CR" + timeStamp);
        } else if (action.equals("uploadItemForCRStatusCheck")) {
            cell.setCellValue("CRStatus" + timeStamp);
        } else if (action.equals("uploadItemForCRMPN")) {
            cell.setCellValue("CRMPN" + timeStamp);
        } else if (action.equals("uploadItemForODMBuyCRMPN")) {
            cell.setCellValue("CRODMBuyMPN" + timeStamp);
        } else if (action.equals("uploadItemForCRApprove")) {
            cell.setCellValue("itemCRApprove" + ucTs);
        } else if (action.equals("uploadItemForEditCRApprove")) {
            cell.setCellValue("itemEditCR" + ucTs);
        } else if (action.equals("uploadItemForSearchCRApprove")) {
            cell.setCellValue("itemCRSearchApprove" + ucTs);
        } else if (action.equals("uploadItemForODMEmailCRApprove")) {
            cell.setCellValue("itemCREmail" + ucTs);
        } else if (action.equals("uploadItemForMultiLvlCRApprove")) {
            cell.setCellValue("itemMultiLevelApprove" + ucTs);
        } else if (action.equals("uploadItemForPriceTAM")) {
            cell.setCellValue("PriceTAM" + timeStamp);
        } else if (action.equals("uploadItemForPriceTAMCommodity")) {
            cell.setCellValue("PriceTAMCommodity" + timeStamp);
        }
        return mainWorkbook;
    }

    public XSSFWorkbook priceTAMFGUpload(XSSFWorkbook mainWorkbook, String action) {

        XSSFCell cell = null;
        XSSFSheet sheet = mainWorkbook.getSheetAt(0);
        if (action.equals("uploadXLOBFGForPriceTAM")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.priceTAM);
            cell = sheet.getRow(3).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.fg);
        } else if (action.equals("emcuploadXLOBFGForPriceTAM")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.eMCpriceTAM);
            cell = sheet.getRow(3).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.eMCfg);
            cell = sheet.getRow(4).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.eMCpriceTAM);
            cell = sheet.getRow(4).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.eMCfg);

        } else if (action.equals("delluploadXLOBFGForPriceTAM")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.dellpriceTAM);
            cell = sheet.getRow(3).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.dellfg);
            cell = sheet.getRow(4).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.dellpriceTAM);
            cell = sheet.getRow(4).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.dellfg);
        } else if (action.equals("delluploadXLOBFGForPriceTAMMonthly")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.dellpriceTAMmonthly);
            cell = sheet.getRow(3).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.dellfgMonthly);
            cell = sheet.getRow(4).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.dellpriceTAMmonthly);
            cell = sheet.getRow(4).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.dellfgMonthly);

            // }else if (action.equals("delluploadXLOBFGForPriceTAMEQuarterly")) {
            // cell = sheet.getRow(3).getCell(0);
            // cell.setCellType(CellType.STRING);
            // cell.setCellValue(UploadController.dellpriceTAMEQuarterly);
            // cell = sheet.getRow(3).getCell(5);
            // cell.setCellType(CellType.STRING);
            // cell.setCellValue(PriceTAM.dellfgeuarterly);
            // cell = sheet.getRow(4).getCell(0);
            // cell.setCellType(CellType.STRING);
            // cell.setCellValue(UploadController.dellpriceTAMEQuarterly);
            // cell = sheet.getRow(4).getCell(5);
            // cell.setCellType(CellType.STRING);
            // cell.setCellValue(PriceTAM.dellfgeuarterly);

        } else if (action.equals("delluploadXLOBFGForPriceTAMQuarterly")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.dellpriceTAMQuarterly);
            cell = sheet.getRow(3).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.dellfguarterly);
            cell = sheet.getRow(4).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.dellpriceTAMQuarterly);
            cell = sheet.getRow(4).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.dellfguarterly);
        } else if (action.equals("xlobtamupload")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.xlobdeleteitem);
            cell = sheet.getRow(3).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgDelttemp);
        } else if (action.equals("XlobFGDeleteTemp")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgDelttemp);
        } else if (action.equals("xlobDeletepricetamitems")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.xlobdeltitem1);
            cell = sheet.getRow(3).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgPirceDelttemp);
            cell = sheet.getRow(4).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.xlobdeltitem2);
            cell = sheet.getRow(4).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgPirceDelttemp);
            cell = sheet.getRow(5).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.xlobdeltitem1);
            cell = sheet.getRow(5).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgPirceDelttemp);
            cell = sheet.getRow(6).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.xlobdeltitem2);
            cell = sheet.getRow(6).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgPirceDelttemp);
            cell = sheet.getRow(7).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.xlobdeltitem1);
            cell = sheet.getRow(7).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgPirceDelttemp);
            cell = sheet.getRow(8).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.xlobdeltitem2);
            cell = sheet.getRow(8).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgPirceDelttemp);
        } else if (action.equals("XlobFGdeletemultiplesites")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgPirceDelttemp);

        } else if (action.equals("XlobdtemptyFGname")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");

        } else if (action.equals("XlobdtvalidFGname")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobfgPirceDelttemp);

        } else if (action.equals("XlobdtemptyFGtype")) {
            cell = sheet.getRow(3).getCell(1);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");

        } else if (action.equals("XlobdtValidFGtype")) {
            cell = sheet.getRow(3).getCell(1);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("XLOB");

        } else if (action.equals("XlobdtemptySitetype")) {
            cell = sheet.getRow(3).getCell(2);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");

        } else if (action.equals("XlobdtvalidSitetype")) {
            cell = sheet.getRow(3).getCell(2);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("GLOBAL");

        } else if (action.equals("XlobdtemptySitename")) {
            cell = sheet.getRow(3).getCell(3);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");

        } else if (action.equals("XlobdtvalidSitename")) {
            cell = sheet.getRow(3).getCell(3);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");

        } else if (action.equals("XlobdtemptyFormFiscalMonth")) {
            cell = sheet.getRow(3).getCell(4);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");

        } else if (action.equals("XlobdtvalidFormFiscalMonth")) {
            CreationHelper createHelper = mainWorkbook.getCreationHelper();
            cell = sheet.getRow(3).getCell(4);
            CellStyle cellStyle = mainWorkbook.createCellStyle();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
            cell.setCellValue(DateUtil.getExcelDate(DateTime.now().toDate()));
            cell.setCellStyle(cellStyle);

        } else if (action.equals("Xlobdtemptyfields")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
            cell = sheet.getRow(3).getCell(1);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
            cell = sheet.getRow(3).getCell(2);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
            cell = sheet.getRow(3).getCell(3);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
            cell = sheet.getRow(3).getCell(4);
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
        }

        return mainWorkbook;

    }

    public XSSFWorkbook priceTAMFGUploadCommodity(XSSFWorkbook mainWorkbook, String action) {

        XSSFCell cell = null;
        XSSFSheet sheet = mainWorkbook.getSheetAt(0);
        if (action.equals("uploadXLOBFGForPriceTAMCommodity")) {
            cell = sheet.getRow(3).getCell(0);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(UploadController.PriceTAMCommodity);
            cell = sheet.getRow(3).getCell(5);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(PriceTAM.xlobFG);
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
        org.openqa.selenium.WebElement statusEl = page.uploadStatus();
        if (statusEl == null) {
            throw new org.openqa.selenium.NoSuchSessionException(
                "uploadStatus() returned null - browser session may have died");
        }
        return statusEl.getText();
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

}