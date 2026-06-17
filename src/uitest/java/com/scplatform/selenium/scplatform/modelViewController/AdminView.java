/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AdminView extends MTCMView {

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

    public List<WebElement> getRegionCheckBoxList() {
        return getList(By.xpath("//span[@class='eto-checkbox__label']"));
    }

    public List<WebElement> getAlternateNamesDisplayed() {
        return getList(By.xpath("//div[@id='populateTags']//span[@class='eto-tag__label']"));
    }

    public WebElement getBusDocLabel(String id, String labelTxt) {
        String xpath;
        if (id == null || id.trim().isEmpty()) {
            // When id is empty, search only by label text without id filter
            xpath = "//label[text()='" + labelTxt + "']";
        } else {
            // When id is provided, use the full XPath with id filter
            xpath = "//input[contains(@id,'" + id + "')]//ancestor::label//following-sibling::label[text()='" + labelTxt + "']";
        }
        return get(By.xpath(xpath));
    }

    public WebElement getBusDocCheckBox(String id) {
        return get(By.xpath("//input[@id='" + id + "' and @type='checkbox']"));
    }

    public WebElement getCheckBoxWitNameNValue(String name, String value) {
        return get(By.xpath("//input[@name='" + name + "' and @value='" + value + "']"));
    }

    public WebElement getResultLinkFromRow1(String userID) {
        return get(By.xpath("//tr//td//a[contains(text(),'" + userID + "')]"));
    }

    public List<WebElement> getBusinessEntitiesAfterSearch() {
        return getList(By.xpath("//a[contains(@href,'goSelectBusinessCallback')]"));
    }

    public WebElement getStatus() {
        return get(
                By.xpath("//section[@id='loadJobHeader']//div[contains(text(),'Status')]/following-sibling::div//b"));
    }

    public List<WebElement> getSelectedAgent() {
        return getList(By.xpath("//div[@id='populateTags']//span[@class='eto-tag__label']"));
    }

    public WebElement getItemData(String tr, String td) {
        return get(By.xpath("//tr[" + tr + "]//td[" + td + "]"));
    }

    public WebElement getResultValues(String col, String value) {
        return get(By.xpath("//tr//td[" + col + "][contains(text(),'" + value + "')]"));
    }

    public List<WebElement> getRolesLinks() {
        return getList(By.xpath("//a[contains(@href,'goSelectRole')]"));
    }

    public List<WebElement> getDashboardsNewsValues() {
        return getList(By.xpath("//tr//td//span[contains(@id,'title')]"));
    }

    public List<WebElement> getMyWorkspaceDisplayedNews() {
        return getList(By.xpath("//a//p"));
    }

    public WebElement getSelectElementToEditDel(String row) {
        return get(By.xpath("//tr[" + row + "]//a[@class='eto-dropdown__toggle']//span"));
    }

    public WebElement getActionEle(String action) {
        // return getList(By.xpath("//a[@data-action='"+action+"']/span"));
        return get(By.xpath(
                "//div[@id='basic-modal-example']//following-sibling::ul//li//a[@data-action='" + action + "']//span"));
    }

}
