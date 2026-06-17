/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController.CostRecords;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.modelViewController.MTCMView;

public class CostRecordsView extends MTCMView {

    @Override
    public void setContext() {
        if (getOverrideContext() == null) {
            String[] frames = new String[1];
            frames[0] = "iframe[id='contentFrame']";
            setFrame(frames);
        } else {
            String[] frames = { "iframe[name='contentFrame']", "iframe[id='mainModalFrame']" };
            setFrame(frames);
        }
    }

    public WebElement getCRCheckBox(String val) {
        return get(By.xpath("//input[@name='accessRights(UPLOAD_TYPE)' and contains(@value,'" + val + "')]"));
    }

    public WebElement getWarningImg(String warningMsg) {
        return get(By.xpath("//img[contains(@onclick,'" + warningMsg + "')]"));
    }

    public List<WebElement> getStatusListUnderHideByStatus() {
        return getList(By.xpath("//ul[@id='addstatustypes']//li[contains(@onclick,'Status')]"));
    }

    public List<WebElement> getColumnsListUnderHideByColumns() {
        return getList(By.xpath("//ul[@id='divadddd']//li[contains(@onclick,'Column')]"));
    }

    public WebElement getHideColumnEle() {
        return get(By.xpath("//button[@type='button' and contains(text(),'Hide Columns')]"));
    }

    public WebElement getStatusEle(String status) {
        WebElement e = null;
        try {
            e = browserSession.getDriver().findElement(

                    By.xpath(
                            "//label[contains(text(),'Status')]/ancestor::div[1]//div/input[@value='" + status + "']"));
        } catch (Exception e1) {
            if (e == null) {
                e = browserSession.getDriver().findElement(By
                        .xpath("//label[contains(text(),'Status')]/ancestor::div[1]//input[@value='" + status + "']"));
                JLog.resetErrorCount();
                return e;
            }
        }
        if (e == null) {
            e = browserSession.getDriver().findElement(
                    By.xpath("//label[contains(text(),'Status')]/ancestor::div[1]//input[@value='" + status + "']"));
            JLog.resetErrorCount();
        }
        return e;
    }

    public WebElement getDisplayedCountEle(String title) {
        return get(By.xpath("//label[contains(text(),'" + title + "')]/following-sibling::label//span"));
    }

    public WebElement getTab(String tab) {
        return get(By.xpath("//a[contains(text(),'" + tab + "')]"));
    }

    public List<WebElement> getStatusFieldEles() {
        return getList(By.xpath("//div[@id='grid-result']//div[@class='eto-grid-scroll']//tr//td[1]"));
    }

    public List<WebElement> getStatusFieldsUnderTab() {
        return getList(By.xpath("//tbody[@id='crTableBody']//tr//td[2]"));
    }

    public List<WebElement> getCalendarDay(String day) {
        // return
        // get(By.xpath("//tr[@class='daysrow']//td[contains(@class,'day') and
        // text()='" + day + "']"));
        JLog.screenCapture();
        // By by = By.xpath("//tr[@class='daysrow']//td[contains(@class,'day')
        // and text()='" + day
        // + "' and not(contains(@class,'day othermonth'))]");
        // By by = By.xpath("//tr[@class='daysrow']//td[text()='" + day + "' and
        // not(contains(@class,'day othermonth'))]");
        By by = By.xpath("//tr//td[text()='" + day + "']");
        JLog.write("***************Locator= " + by);
        return getList(by);
    }

    public WebElement getCalendarDay(String day, String row) {
        // return
        // get(By.xpath("//tr[@class='daysrow']//td[contains(@class,'day') and
        // text()='" + day + "']"));

        By by = By.xpath("//tr[@class='daysrow'][" + row + "]//td[contains(@class,'day') and text()='" + day
                + "' and not(contains(@class,'day othermonth'))]");
        JLog.write("***************Locator= " + by);
        // WebDriverWait wait = new WebDriverWait(browserSession.getDriver(),
        // 30);
        // wait.until(ExpectedConditions.elementToBeClickable(by));
        return get(by);
    }

    public WebElement getNameField(String name) {
        return get(By.xpath("//input[contains(@name,'" + name + "')]"));
    }

    public List<WebElement> getDatesField(String name) {
        return getList(By.xpath("//input[contains(@name,'" + name + "')]"));
    }

    public WebElement getProjectName(String row) {
        return get(By.xpath("//tr[" + row + "]//input[contains(@id,'projectName')]"));
    }

    public WebElement getReasonCodeonRow(String row) {
        return get(By.xpath("//tbody[@id='crTableBody']//tr[" + row + "]//select[contains(@name,'reasonCode')]"));
    }

    public WebElement getMPNonRow(String row) {
        return get(By.xpath("//tbody[@id='crTableBody']//tr[" + row
                + "]//input[contains(@name,'attributeData(stringAttribute2)')]"));
    }

    public WebElement getSystemActionOnRow(String row) {
        return get(By.xpath("//tbody[@id='crTableBody']//tr[" + row + "]//input[contains(@name,'systemAction')]"));
    }

    public List<WebElement> getExcepIDonRows() {
        return getList(
                By.xpath("//tbody[@id='crTableBody']//tr//input[contains(@name,'attributeData(stringAttribute7)')]"));
    }

    public WebElement getRecSourceonRow(String row) {
        return get(By.xpath("//tbody[@id='crTableBody']//tr[" + row
                + "]//input[contains(@name,'attributeData(stringAttribute5)')]"));
    }

    public List<WebElement> getMPNonAllRows() {
        return getList(
                By.xpath("//tbody[@id='crTableBody']//tr//input[contains(@name,'attributeData(stringAttribute2)')]"));
    }

    public List<WebElement> getExcepIDs(String expID) {
        return getList(By.xpath("//tr//td[46][contains(text(),'" + expID + "')]"));
    }

    public List<WebElement> getItemLinks() {
        return getList(By.xpath("//a[@data-popover='#item-popover']"));
    }

    public WebElement getSearchResultsStatusField() {
        return get(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//td[1]"));
    }

    public List<WebElement> getSearchResultsStatusField(String status) {
        return getList(By.xpath("//div[@class='eto-grid-scroll']//tr//td[contains(text(),'" + status + "')]"));
    }

    public List<WebElement> getSearchColumn(String col) {
        return getList(By.xpath("//tr//td[" + col + "]"));
    }

    public WebElement getCRUploadPanel() {
        return getElement(
                By.xpath("//div[@id='upload-exception-cost-records' and @class='eto-upload disabled']//input"));
    }
    
    public WebElement getMutipleItemNumber() {
        return getElement(
                By.xpath("//input[@id='searchField4' and contains(@class, 'eto-autocomplete__field')]"));
    }
    
    public WebElement getMutipleItemNumberSearchPopup() {
        return getElement(By.xpath("//input[@id='searchField4' and @type='text']//..//span[@class='md-icon' and text()='search']"));

    }
    
    public WebElement getMutipleItemNumberValues() {
        return getElement(By.xpath("//div[@id='autocomplte_tag_itemNumbers']//span[contains(@class,'eto-tag__label')]"));

    }
  //div[@id='badgeitemNumbers']//a[contains(.,'View all tags')]
    
  //button[contains(@class, "eto-btn") and contains(@class, "eto-btn--link") and @id="badgeCountitemNumbers"]
    public WebElement getInputField(String inputField) {
    	return getElement(By.xpath("//div[@id='" + inputField + "']/input")); // Update as needed
    }

    public List<WebElement> getDisplayedTags(String textField) {
        return getList(By.xpath("//div[@id='autocomplte_tag_" + textField + "']//span[contains(@class,'eto-tag__label')]"));
    }

    public WebElement getViewAllTagsButton() {
        return getElement(By.xpath("//div[@id='badgecategories']//a[contains(text(),'View all tags')]"));
    }

    public List<WebElement> getInvalidTags() {
        return getList(By.xpath("//span[contains(@class,'eto-tag--error')]//span[contains(@class,'eto-tag__label')]"));
    }
    
    public WebElement getNoRecordsMessageElement() {
        return getElement(By.xpath("//div[@class='eto-messageblock__body' and contains(text(),'No records found to')]"));
    }
    
    public WebElement clearButton() {
        return getElement(By.xpath("//button[@id='searchClearButton' and contains(normalize-space(),'Clear')]"));
    }
    
  
}

