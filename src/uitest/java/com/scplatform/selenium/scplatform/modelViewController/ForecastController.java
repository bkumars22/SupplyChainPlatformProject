/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.google.common.base.Verify;

public class ForecastController extends MTCMController {

    ForecastView view;

    @Override
    public PageImpl getView() {
        view = new ForecastView();
        return view;
    }

    public void getAndVerifySearchFilterResultFCStatus(String status) {
        view = new ForecastView();

    }

    public void getAndVerifySearchFilterResultFCItemnumbers(String fcItem1, String fcItem2, String fcItem3) {
        view = new ForecastView();

    }

    public void getAndVerifySearchFilterResultPFCItemnumbers(String fcItem4, String fcItem5) {
        view = new ForecastView();

    }

    public void setGrpName(String fg) {
        view = new ForecastView();
        WebElement element = view.getSearchcfgNameonparent();
        element.clear();
        element.sendKeys(fg);
        JLog.write("******After entering***********");
        JLog.screenCapture();
        JLog.write("Group name set to " + fg);
    }

    public void isScrollBarVisibleUnderForecast() {
        view = new ForecastView();
        WebElement element = null;
        try {
            element = view.get(By.id("fcTableCUR"));//
            view.executeJavaScript("arguments[0].scrollIntoView(true);", element);
            Verify.verify(element.isDisplayed(), "Scroll grid is not displayed");
            element = view.get(By.className("scrollableTableHeader"));//
            view.executeJavaScript("arguments[0].scrollIntoView(true);", element);
            Verify.verify(element.isDisplayed(), "Scroll grid is not displayed");
        } catch (Exception e) {
            JLog.error("Unable to see the scrollable part on forecast");
        }

    }

    public void setGrpNameonParent(String fg) {
        view = new ForecastView();
        WebElement element = view.getSearchcfgNameonparent();
        element.clear();
        element.sendKeys(fg);
        JLog.write("******After entering***********");
        JLog.screenCapture();
        JLog.write("Group name set to " + fg);

    }

    public void setGrpNameonforecastview(String cfg) {
        view = new ForecastView();
        WebElement element = view.getSearchcforecastcfgName();
        element.clear();
        element.sendKeys(cfg);
        JLog.write("******After entering***********");
        JLog.screenCapture();
        JLog.write("Group name set to " + cfg);

    }

    public void setExtendedForecast(String val) {
        view = new ForecastView();
        WebElement ele = view.getList(By.xpath("//input[contains(@name,'extendPeriods')]")).get(0);
        ele.clear();
        ele.sendKeys(val);
    }

    public void forecastarrowfrd() {
        view = new ForecastView();
        WebElement ele = view.arrowforward();
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.write("Succesfully clicked on Arrow button" + Keys.ARROW_RIGHT);
    }

    public void setfcvalue(String val, String row) {
        view = new ForecastView();
        WebElement ele = view.fcValue(row);
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].value='" + val + "';", ele);
        JLog.write("Succesfully set Material on row - " + row + " with value= " + val);

    }

    @Override
    public void clickSaveButton() {
        view = new ForecastView();
        WebElement ele = view.saveButton();
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.resetErrorCount();
    }

    public void setRegionSite(String val) {
        view = new ForecastView();
        WebElement ele = view.RegionDrop();
        Select select = new Select(ele);
        if (val.equals("APCC"))
            select.selectByValue("8365");
        JLog.write("Selected" + val + " on Region Site");
    }

    public void setItemMultipleValFieldsGCM(String fc1, String fc2, String fc3, String textField) {
        view = new ForecastView();
        view.getTextField(textField).sendKeys(fc1 + ";" + fc2 + ";" + fc3);
        JLog.write("Set " + textField + " textfield with " + fc1 + ";" + fc2 + ";" + fc3);
    }

    public void setItemMultipleValFieldsSuperGCM(String fc4, String fc5, String fc6, String textField) {
        view = new ForecastView();
        view.getTextField(textField).sendKeys(fc4 + ";" + fc5 + ";" + fc6);
        JLog.write("Set " + textField + " textfield with " + fc4 + ";" + fc5 + ";" + fc6);
    }

    public void setItemMultipleValFieldsadm(String fc7, String fc8, String fc9, String textField) {
        view = new ForecastView();
        view.getTextField(textField).sendKeys(fc7 + ";" + fc8 + ";" + fc9);
        JLog.write("Set " + textField + " textfield with " + fc7 + ";" + fc8 + ";" + fc9);
    }

    public void setMultipleValFieldsGCM(String fc10, String fc11, String textField) {
        view = new ForecastView();
        view.getTextField(textField).sendKeys(fc10 + ";" + fc11);
        JLog.write("Set " + textField + " textfield with " + fc10 + ";" + fc11);
    }

    public void setMultipleValFieldsSuperGCM(String fc12, String fc13, String textField) {
        view = new ForecastView();
        view.getTextField(textField).sendKeys(fc12 + ";" + fc13);
        JLog.write("Set " + textField + " textfield with " + fc12 + ";" + fc13);
    }

    public void getAndVerifyCRStatus(String row, String status) throws Throwable {
        view = new ForecastView();
        int rows = Integer.parseInt(row);
        WebElement ele = view
                .getList(
                        By.xpath("//table[@id='fcTableCUR_data']//th[@class='fixedColumn'][contains(text(),'Status')]"))
                .get(rows - 1);
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        String s = ele.getAttribute("ng-init");
        Verify.verify(s.contains(status), "Status is incorrect for the new CR");
    }

    // public void getErrorMessageonUploadUI(String validationError) {
    //
    // view = new ForecastView();
    // WebElement ele = view.getErrorMessages(validationError);
    // view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
    //
    // }

    public void getAndVerifyErrmsg(String validationError) {
        view = new ForecastView();

    }

    public void getAndVerifyExtendedFC(String extended) {
        view = new ForecastView();

    }

    public void getCreateparent() {
        view = new ForecastView();
        WebElement ele = view.getcreateparent();
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.write("Succesfully clicked on Create parent button" + ele);
    }

    public void getAssignparent() {
        view = new ForecastView();
        WebElement ele = view.getassignparent();
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.write("Succesfully clicked on Assign parent button" + ele);
    }

    public void setParentName(String name) {
        MTCMView view = new MTCMView();
        WebElement ele = view.getEleByName("parentGroupName");
        ele.clear();
        ele.sendKeys(name);
        FunctionalGroupView view1 = new FunctionalGroupView();
        view1.saveAllButton().click();
    }

    public void setGrpNameToAssign(String name) {
        view = new ForecastView();
        WebElement element = view.setparentName();
        element.clear();
        element.sendKeys(name);
        view.get(By.xpath("//ul//li[contains(text(),'" + name + "')]")).click();
        JLog.write("Group name set to " + name);
        FunctionalGroupView view1 = new FunctionalGroupView();
        view1.saveAllButton().click();
    }
}