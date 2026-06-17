/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SearchView extends MTCMView {

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

    public WebElement getItemManagedByDetails(int rowCount, int column, String value) {
        return get(By.xpath("//tr[" + rowCount + "]//td[" + column + "][contains(text(),'" + value + "')]"));
        // [@class='']"));
    }

    public WebElement getRowForResp(String role) {
        return get(By.xpath("//input[@name='selectedPageKeys' and contains(@value,'" + role + "')]"));
    }

    @Override
    public List<WebElement> getHeaderColumns() {
        return getList(By.xpath("//th//a[@class='eto-grid-column__label']"));
    }
}
