/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.upload;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class UploadPage extends SCPlatformPage {

    public WebElement messageType() {
        return getElement(By.name("messageType(1)"));
    }

    public WebElement button_Submit() {
        return getElement(By.xpath("//button[contains(text(),'Submit')]"));
    }

    public WebElement getManageUploadJobDetails(String text) {
        return getElement(By.xpath("//div[contains(text(),'" + text + "')]/following-sibling::div//b"));
    }

    public WebElement button_Cancel() {
        return getElement(By.partialLinkText("Cancel"));
    }

    private By uploadStatusLocator() {
        return By.xpath("//div[contains(text(),'Status')]//ancestor::div[1]//b");
    }

    public WebElement uploadStatus() {
        return getElement(uploadStatusLocator());
    }

    private By uploadStatusErrorLocator() {
        return By.xpath("//div[contains(@class, 'errorMessage')]/li");
    }

    public WebElement uploadStatusError() {
        return getElement(uploadStatusErrorLocator());
    }

    public boolean isUploadStatusError() {
        return exists(uploadStatusErrorLocator());
    }

    public WebElement transactionID() {
        return getElement(By.xpath("//div[@class='instructionsArea']/li/a"));
    }

    public WebElement getFileField() {
        WebElement ele = null;
        try {
            ele = browserSession.getDriver().findElement(By.id("uploadFile"));
        } catch (Exception e) {
            if (e.toString().contains("Exception")) {
                JLog.resetErrorCount();
                ele = browserSession.getDriver()
                        .findElement(By.xpath("//input[@multiple='multiple' and @name='uploadFile']"));
            }
        }
        if (ele == null) {
            JLog.resetErrorCount();
            ele = browserSession.getDriver()
                    .findElement(By.xpath("//input[@multiple='multiple' and @name='uploadFile']"));
        }
        return ele;
    }
}
