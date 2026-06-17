/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController.CostRecords;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.google.common.base.Verify;

public class WAPCostRecordsController extends MTCMController {

    WAPCostRecordsView view;

    @Override
    public PageImpl getView() {
        view = new WAPCostRecordsView();
        return view;
    }

    public void setMaterialOnRow(String val, String row) {
        view = new WAPCostRecordsView();
        WebElement ele = view.getMaterial(row);
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].value='" + val + "';", ele);
        JLog.write("Succesfully set Material on row - " + row + " with value= " + val);

    }

    // public void setGFOnRow(String fg, String row) {
    // view = new WAPCostRecordsView();
    // WebElement ele = view.getMaterial(row);
    // view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    // view.executeJavaScript("arguments[0].value='" + val + "';", ele);
    // JLog.write("Succesfully set Material on row - " + row + " with value= " +
    // val);
    //
    // }

    public boolean getAndVerifyedit(String button) {
        boolean isPresent = false;
        try {
            // WebElement ele = view.get(By.xpath("//i[contains(text(),'" +
            // fileDownload + "')]"));
        } catch (NoSuchElementException e) {
            isPresent = false;
            JLog.write("Successfully disable edit button on XWAP cost record UI");
        }
        return isPresent;

    }

    public void setFunctionalIdOnRow(String val, String row) {
        view = new WAPCostRecordsView();
        WebElement ele = view.getFGId(row);
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].click();", ele);
        ele.clear();
        ele.sendKeys(val);
        JLog.write("Succesfully set FunctionalID on row - " + row + " with value= " + val);
    }

    public void closePopup() {

        List<WebElement> elements = view.getList(By.xpath("//button[@id='popup_modal_okButton']"));
        for (WebElement ele : elements) {
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.executeJavaScript("arguments[0].click();", ele);
        }
    }

    public void getAndVerifyMaterialStatus(String row, String status) throws Throwable {

        CostRecordsView view = new CostRecordsView();
        int rows = Integer.parseInt(row);
        WebElement ele = view.getList(By.xpath("//tbody[@id='crTableBody']//tr//td[14]")).get(rows - 1);
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        String s = ele.getAttribute("innerText");
        List<WebElement> elements = view.getList(By.xpath("//span[text()='info']"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(elements.size() - 1));
        JLog.screenCapture();
        JLog.write("Status found is " + s);
        Verify.verify(s.contains(status), "Status is incorrect for the new CR material cost");
    }

    public void verifyMaterialValue(String val, String row) {

        view = new WAPCostRecordsView();
        WebElement element = view.getMaterial(row);
        String actualVal = element.getText();
        if (actualVal.equals("")) {
            actualVal = element.getAttribute("value");
        }
        Verify.verify(actualVal.contains(val), "Unable to verify material value on row - " + row);
        JLog.write("Successfully verified material value on row -" + row);
        // String[] dotSub = actualVal.split("\\.|,");
        // if (dotSub.length == 2) {
        // if (dotSub[1].length() <= 2) {
        // JLog.write("Actual Value found = " + actualVal);
        // } else {
        // JLog.write("Actual Value is not found and displayed value is = " +
        // actualVal);
        // }
        // }

    }

    public void verifyFGIdValue(String val, String row) {

        view = new WAPCostRecordsView();
        WebElement element = view.getFGId(row);
        String actualVal = element.getText();
        if (actualVal.equals("")) {
            actualVal = element.getAttribute("value");
        }
        Verify.verify(actualVal.contains(val), "Unable to verify FGId value on row - " + row);
        JLog.write("Successfully verified FGID value on row -" + row);
    }

    @Override
    public void clickButton(String buttonName) {
        view = new WAPCostRecordsView();
        WebElement ele = view.getedit(buttonName);
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.write("Clicked on " + buttonName + " button.");
    }

    public void setXLOBFGNamefield(String value) {
        view = new WAPCostRecordsView();
        WebElement ele = view.setXLOBFGNameField();
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].click();", ele);
        ele.clear();
        ele.sendKeys(value);
        JLog.write("Succesfully set XLOB FG Name on cost record search filters" + value);
    }

    public void getAndVerifySearchFilterResultXLOBFGLOB(String status) {
        view = new WAPCostRecordsView();

    }

    public void getAndVerifySearchFilterResultXLOBPlatform(String status) {
        view = new WAPCostRecordsView();

    }

    public void getAndVerifySearchFilterResultXLOBFGName(String status) {
        view = new WAPCostRecordsView();

    }
}