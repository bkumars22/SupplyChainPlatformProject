/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;

public class FunctionalGroupView extends MTCMView {

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

    // @Override
    // public void setContext(){
    // //String[] frames = new String[1];
    // String[] frames =
    // {"iframe[name='contentFrame']","iframe[id='mainModalFrame']"};
    // setFrame(frames);
    // }

    @Override
    public String[] homeMenu() {
        return null;
    }

    public WebElement getRespColumn() {
        return get(By.xpath("//tr[1]//div[@class='eto-grid-expand__container']/ancestor::td"));
    }

    public WebElement groupName() {
        return get(By.id("functionalGroupName"));
    }

    public List<WebElement> getSelectedItemTypes() {
        return getList(By.xpath("//span[@class='eto-tag__label']"));
    }

    public List<WebElement> getFGNames() {
        return getList(By.xpath("//a[contains(@href,'goEditGroup')]"));
    }

    public WebElement searchGroupName() {
        return get(By.xpath("//input[contains(@name,'functionalGroupName')]"));
    }

    public WebElement getParentName(String row, String col) {
        return get(By.xpath("//tr[" + row + "]//td[" + col + "]//a"));
    }

    public WebElement saveAllButton() {
        List<WebElement> els = browser().findElements(By.id("saveButton"));
        return els.isEmpty() ? null : els.get(0);
    }

    public List<WebElement> getGroupItems() {
        return getList(By.xpath("//td[1]//a[contains(@onclick,'openPopOver')]"));
    }

    public List<WebElement> getRows() {
        return getList(By.name("items"));
    }

    public List<WebElement> getRemoveItemsImg() {
        return getList(By.xpath("//a[contains(@onclick,'deleteItemFromGroup')]"));
    }

    public WebElement getSearchGrpName() {
        return get(By.xpath("//input[contains(@name,'functionalGroupName')]"));
    }

    public List<WebElement> getTamAvailabilityField() {
        return getList(By.xpath("//div[@id='functionalGroup']//tr//td[7]"));
    }

    public WebElement getPopupMsg() {
        return get(By.className("simplemodal-body"));
    }

    @Override
    public WebElement getPageJump() {
        return get(By.xpath("//label[contains(text(),'of')]"));
    }

    public WebElement saveAllAndExitButton() {
        List<WebElement> els = browser().findElements(By.id("saveAndReturnButton"));
        return els.isEmpty() ? null : els.get(0);
    }

    public List<WebElement> getPopupTableElements(String text) {
        if (text.equals("Responsibility"))
            return getList(By.xpath("//td/b[text()='" + text + "']/ancestor::td/following-sibling::td//li"));
        // else if(text.equals("Item Business"))
        // return
        // getList(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//td[3][@class='']"));
        return getList(By.xpath("//td/b[text()='" + text + "']/ancestor::td/following-sibling::td"));
    }

    public WebElement getSaveParentNameBtn() {
        return get(By.xpath("//button[contains(@onclick,'saveParentFunctionGroup')]"));
    }

    public WebElement getParentGroupDeleteButton() {
        // Specifically targets the button that calls removeParent() (not
        // removeParentItem)
        List<WebElement> els = browser().findElements(
                By.xpath(
                        "//button[contains(@onclick,'removeParent()') and not(contains(@onclick,'removeParentItem'))]"));
        return els.isEmpty() ? null : els.get(0);
    }

    public WebElement parentGroupName() {
        // On the FG parentModal popup: id="parentName"
        List<WebElement> els = browser().findElements(By.id("parentName"));
        if (!els.isEmpty())
            return els.get(0);
        // On the Parent Group create/edit page: id="parentFunctionalGroupName"
        els = browser().findElements(By.id("parentFunctionalGroupName"));
        if (!els.isEmpty())
            return els.get(0);
        return null;
    }

    public List<WebElement> getFGUnderParentDetails() {
        return getList(By.xpath("//table//td[2]"));
    }

    public WebElement fgstatus() {
        return getValue(By.xpath("//*[@name='fgstatus' and @disabled]"));
    }

    public WebElement getValue(By by) {
        WebElement obj = null;
        try {
            obj = browser().findElement(by);
        } catch (NoSuchElementException e) {
            JLog.error(null, e, TakeScreenshot.True);
        }
        return obj;
    }

    public boolean getDisabledFGStatusValue() {
        FunctionalGroupView view = new FunctionalGroupView();
        try {
            WebElement ele = view.get(
                    By.xpath("//input[@name='fgstatus']"));
        } catch (Exception e) {
            if (e.toString().contains("Element not enabled")) {
                return true;
            }
        }
        return false;
    }

}
