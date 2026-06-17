/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.SCPlatformSearchPage;

public class CommodityManagementPage extends SCPlatformSearchPage {
    public WebElement commodityName() {
        return getElement(By.name("value(categoryName)"));
    }

    public WebElement multipleCommodityNames() {
        return getElement(By.name("value(categories)"));
    }

    public WebElement contextType() {
        return getElement(By.name("value(contextType)"));
    }

    public WebElement contextName() {
        return getElement(By.name("value(contextId)"));
    }

}
