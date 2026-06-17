/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.modelViewController.view.PageImpl;

public class SCPlatformSearchResultsController extends SCPlatformController {
    private SCPlatformSearchResultsPage searchResults;

    @Override
    public String getErrorMessage() {
        SCPlatformPage page = new SCPlatformPage();
        return page.getErrorMessage();
    }

    @Override
    public PageImpl getView() {
        if (searchResults == null) {
            searchResults = new SCPlatformSearchResultsPage();
        }
        return searchResults;
    }

    public void select(String text, By tableLocator) {
        SCPlatformSearchResultsPage searchResults = new SCPlatformSearchResultsPage();
        WebElement row = searchResults.findRow(text, tableLocator);
        row.findElement(By.name("selectRowButton")).click();
    }

    public void check(String text, By tableLocator) {
        SCPlatformSearchResultsPage searchResults = new SCPlatformSearchResultsPage();
        WebElement row = searchResults.findRow(text, tableLocator);
        row.findElement(By.name("selectedPageKeys")).click();
    }

    public void setPageSize(int size) {
        SCPlatformSearchResultsPage searchResults = new SCPlatformSearchResultsPage();
        setValue(searchResults.pageSize(), Integer.toString(size));
    }

}
