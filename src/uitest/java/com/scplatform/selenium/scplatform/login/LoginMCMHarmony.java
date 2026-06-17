/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.login;

import java.io.IOException;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.Configuration;
import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.browser.InternetExplorerBrowser;
import com.test.selenium.common.login.AbstractLogin;
import com.test.selenium.common.users.User;
import com.test.selenium.scplatform.profile.Banner;

public class LoginSCPlatformHarmony extends AbstractLogin {

    public static String navUrl = Configuration.getProperty("mtcm.url");

    public LoginSCPlatformHarmony() {
        setUrl(propertiesUrl("mtcm.url"));
        this.frames = null;
    }

    @Override
    public boolean logout() {
        Banner banner = new Banner();
        return banner.exitHarmony();
    }

    // @Override
    public void login(User user, String role) throws InterruptedException, IOException {
        boolean direct = Configuration.getProperty("mtcm.url.direct", "false").equalsIgnoreCase("true");
        if (direct) {
            loginDirect(user, role);
        } else {
            super.login(user);

        }
    }

    public boolean logoutFromRole(String role) {
        Banner banner = new Banner();
        return banner.exitHarmony();
    }

    public void loginDirect(User user, String role) throws InterruptedException, IOException {
        boolean success = false;
        int errorCount = JLog.getErrorCount();
        AbstractPage page = new AbstractPage();

        String url = Configuration.getProperty("mtcm.url");
        String userID = null;
        if (role.equals("admin"))
            userID = Configuration.getProperty("mtcm.url.user");
        else
            userID = role;

        navigateToURL(url);

        JLog.write("Login Directly as " + userID);
        if (page.exists(By.name("username"))) {
            setUserID(userID);
            clickSubmit();

            AbstractPage.sleep(waitAfterLogin);

            // for ps box accepting terms and conditions
            acceptTermsAndConditions();

            // Switch to the available window in case the login redirect closed the original
            // window
            switchToAvailableWindow();

            if (!this.skipValidation) {
                InternetExplorerBrowser.waitIfIE(10);
                JLog.write("Validating login..");
                getMainFrame();
                success = page.waitForExistence(loginValidatedBy(), waitForLogin);
            } else if (errorCount == JLog.getErrorCount()) {
                success = true;
            }

            if (!success) {
                JLog.error("Login failed!", TakeScreenshot.True);
            }
        }
    }

    private void switchToAvailableWindow() {
        try {
            // Check if the current window handle is still valid
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

    public void acceptTermsAndConditions() {
        String devBox = Configuration.getProperty("mtcm.url",
                "http://dev4160.dev.scplatform.local:11080/scplatform/authenticate.do");
        if (devBox.contains("dev4160")) {
            page = new AbstractPage();
            page.browserSession.getDriver().switchTo().defaultContent();
            boolean status = page.existsAndDisplayed(By.xpath("//h2[contains(text(),'Confidential - Restricted')]"));
            WebElement ele = null;
            if (status) {
                ele = page.get(By.xpath("//input[@type='radio' and @name ='agreement' and @value='yes']"));
                page.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                page.executeJavaScript("arguments[0].click();", ele);
                page.sleep(2);
                ele = page.get(By.id("submitButton"));
                page.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                page.executeJavaScript("arguments[0].click();", ele);
                JLog.write("Accepted Terms and Conditions");
            }
        }
    }

    private void clickSubmit() {
        WebElement ele = null;
        try {
            ele = page.browserSession.getDriver().findElement(By.name("submit"));
            ele.click();
        } catch (Exception e) {
            JLog.resetErrorCount();
            try {
                ele = page.get(By.name("Login"));
                ele.click();
            } catch (Exception e1) {
                JLog.fail("Login button missing.");
            }
        }
    }

    private By loginValidatedBy() {
        By logoutBy = By.xpath("//a[@title='User']//span[@class='eto-header__user-info-name']");
        return logoutBy;
    }
}
