/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.login;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.Configuration;
import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.browser.InternetExplorerBrowser;
import com.test.selenium.common.login.AbstractLogin;
import com.test.selenium.common.users.User;
import com.test.selenium.scplatform.profile.Banner;

public class LoginSCPlatform extends AbstractLogin {

    public LoginSCPlatform() {
        setUrl(propertiesUrl("mtcm.url"));
        this.frames = null;
    }

    @Override
    public boolean logout() {
        Banner banner = new Banner();
        return banner.exit();
    }

    @Override
    public void login(User user) {
        boolean direct = Configuration.getProperty("mtcm.url.direct", "false").equalsIgnoreCase("true");
        if (direct) {
            loginDirect(user);
        } else {
            super.login(user);

        }
    }

    public void loginDirect(User user) {
        boolean success = false;
        int errorCount = JLog.getErrorCount();
        AbstractPage page = new AbstractPage();

        String url = Configuration.getProperty("mtcm.url");
        String userID = Configuration.getProperty("mtcm.url.user");
        navigateToURL(url);

        JLog.write("Login Directly as " + userID);
        setUserID(userID);
        clickSubmit();

        AbstractPage.sleep(waitAfterLogin);

        if (!this.skipValidation) {
            InternetExplorerBrowser.waitIfIE(10);
            JLog.write("Validating login..");
            switchToAvailableWindow();
            getMainFrame();
            success = page.waitForExistence(getLoginValidatedBy(), waitForLogin);
        } else if (errorCount == JLog.getErrorCount()) {
            success = true;
        }

        if (!success) {
            JLog.error("Login failed!", TakeScreenshot.True);
        }
    }

    private void switchToAvailableWindow() {
        try {
            AbstractPage.browserSession.getDriver().getWindowHandle();
        } catch (NoSuchWindowException e) {
            JLog.write("Login: current window closed after redirect, switching to available window");
            Set<String> handles = AbstractPage.browserSession.getDriver().getWindowHandles();
            if (!handles.isEmpty()) {
                AbstractPage.browserSession.getDriver().switchTo().window(handles.iterator().next());
                JLog.write("Switched to window: " + AbstractPage.browserSession.getDriver().getWindowHandle());
            }
        }
    }

    private void setUserID(String userID) {
        page.get(By.name("username")).sendKeys(userID);
    }

    private void clickSubmit() {
        page.get(By.name("Login")).click();
    }
}
