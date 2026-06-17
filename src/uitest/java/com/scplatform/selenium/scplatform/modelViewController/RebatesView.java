/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class RebatesView extends MTCMView {

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
    
    
    public List<WebElement> getEditIconList() {
        return getList(By.xpath("//a[@title='Edit']"));
    }
    
    public List<WebElement> getRebatesItemAmounts() {
        return getList(By.xpath("//input[contains(@name,'itemAmount')]"));
    }

    public List<WebElement> getItemListUnderRulesList() {
        return getList(By.xpath("//table[@id='rebateRules-details']//tr//td[4]//li"));
    }

    public List<WebElement> getItemListUnderViewResults() {
        return getList(By.xpath("//table[@id='previewTable']//tr/[1]/td[1]"));
    }

    public List<WebElement> getBusinessListUnderViewResults() {
        return getList(By.xpath("//table[@id='previewTable']//tr/[1]/td[4]"));
    }

    public WebElement getPricingTab() {
        return get(By.id("PRICINGTAB"));
    }

    public List<WebElement> getFromToDateColumn(String date) {
        return getList(By.xpath("//td[text()='Active']/following-sibling::td[contains(@id,'item" + date + "Date')]"));
    }

    public WebElement getEndDate(String row) {
        return get(By.name("itemToDate[" + row + "]"));
    }

    public List<WebElement> getAmountFields() {
        return getList(By.xpath("//input[@onblur='checkRebateAmount(this)']"));
    }

    public List<WebElement> getCalendarDays() {
        By by = null;
        // if(month.contains("current"))
        // by = By.xpath("//tr[@class='daysrow']//td[contains(@class,'day') and
        // not(contains(@class,'othermonth'))]");
        // else
        // by =By.xpath("//tr[@class='daysrow']//td[contains(@class,'day') and
        // contains(@class,'othermonth')]");
        by = By.xpath("//tr[@class='daysrow']//td[contains(@class,'day')]");
        return getList(by);
    }

    public WebElement getCalendarDay(String day) {
        By by = By.xpath("//tr[@class='daysrow']//td[contains(@class,'day') and text()='" + day
                + "' and not(contains(@class,'day othermonth'))]");
        if (waitForExistence(by, 60))
            return get(by);
        return null;
    }
}
