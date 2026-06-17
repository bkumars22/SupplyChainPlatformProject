/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;

public class ForecastView extends MTCMView {

    @Override
    public void setContext() {
        if (getOverrideContext() == null) {
            String[] frames = new String[1];
            frames[0] = "iframe[id='contentFrame']";
            setFrame(frames);
        }
        // else if(getOverrideContext()[0].equals("No Frame")){
        // browserSession.getDriver().switchTo().defaultContent();
        // }
        else {
            String[] frames = { "iframe[name='contentFrame']", "iframe[id='mainModalFrame']" };
            setFrame(frames);
        }

    }

    @Override
    public String[] homeMenu() {
        return null;
    }

    public WebElement nextButton() {
        return get(By.xpath("//button[contains(text(),'Next')]"));
    }

    public WebElement fcValue(String row) {
        return get(By.xpath("//tr[" + row + "]//input[@id='forecastValue_C_0_12']"));

    }

    public WebElement arrowforward() {
        return get(By.xpath(
                "//div[@class='eto-grid-scroll scrollableTable']//tr[1]//td[25]//i[contains(text(),'arrow_forward')]"));
    }

    public WebElement RegionDrop() {
        return get(By.xpath("//div[@class='eto-grid-scroll scrollableTable']//tr[1]//td[3]//select"));
    }

    public WebElement selectMultiCheckBox(String name) {
        return get(By.xpath(
                "//table[@id='fcTableCUR_data']//thead[contains(@class,'scrollableTableHeader')]//span[contains(@class,'eto-checkbox__box')]"));
    }

    public WebElement getSearchResultsStatusFC() {
        return get(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//td[11]"));
    }

    public WebElement getSearchResultsExtendedFC() {
        return get(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//td[12]"));
    }

    public WebElement getSearchcfgName() {
        return get(By.xpath("//input[@name='value(parentName)']"));
    }

    public WebElement getSearchcfgNameonparent() {
        return get(By.xpath("//input[contains(@name,'functionalGroupName')]"));
    }

    public WebElement getSearchcforecastcfgName() {
        return get(By.xpath("//input[@name='value(cfgName)']"));
    }

    public WebElement getcreateparent() {
        return get(By.xpath("//button[contains(text(),'Create Parent Group')]"));
    }

    public WebElement getassignparent() {
        return get(By.xpath("//button[contains(text(),'Assign to Parent Group')]"));
    }

    public WebElement setparentName() {
        return get(By.xpath("//input[@name='parentGroupName']"));
    }

    @Override
    public WebElement getTextField(String name) {
        return get(By.name("value(" + name + ")"));
    }

    public WebElement HeaderStatus(String status) {
        return get(By.xpath("//div[@class='eto-grid-column__container']//a[contains(text(),'Status')]"));
    }

    public WebElement getStatusEle(String status) {
        WebElement e = get(
                By.xpath("//a[contains(text(),'Status')]/ancestor::div[1]//div/input[@value='" + status + "']"));
        if (e == null) {
            e = get(By.xpath("//a[contains(text(),'Status')]/ancestor::div[1]//input[@value='" + status + "']"));
            JLog.resetErrorCount();
        }
        return e;
    }

    public WebElement getErrorMessages(String ValidationError) {
        return get(By.xpath("//td[contains(text(),'" + ValidationError + "')]"));
    }

    @Override
    public WebElement saveButton() {
        WebElement e = getElement(By.id("saveButton"));
        if (e == null) {
            e = getElement(By.id("saveButton"));
            JLog.resetErrorCount();
        }
        return e;
    }

}