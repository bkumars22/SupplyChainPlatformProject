/*
 * @Banner.java@
 * Created on May 25, 2018
 *
 * Copyright (c) 2018 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
/**
 *
 */
package com.test.selenium.scplatform.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.browser.BrowserType;
import com.test.selenium.clp.CLP;
import com.test.selenium.clp.userMenu.UserMenu;
import com.test.selenium.scplatform.login.LoginSCPlatformHarmony;
import com.test.selenium.scplatform.navigation.BaseNavigation;

/**
 * @author dgenrich
 *
 */
public class Banner {

    private AbstractPage page;

    private String username = null;
    private String userRole = null;

    /**
     * Instantiates a new role selection.
     */
    public Banner() {
        super();
        page = new AbstractPage();
        page.switchToFrame(getHeaderFrames());
    }

    protected String[] getHeaderFrames() {
        return null;
    }

    /**
     * Click on role.
     *
     * @param role
     *            the role
     * @param ignoreMissing
     *            the ignore missing
     * @return true, if successful
     */
    public boolean clickRole(String role, boolean ignoreMissing) {
        boolean isClicked = false;

        findRole(role);

        if (!page.exists(By.linkText(role)) && (ignoreMissing)) {
            JLog.warning("Role is not found: " + role, TakeScreenshot.True);
            return true;
        }

        WebElement link = page.get(By.linkText(role));
        if (link == null) {
            if (!ignoreMissing) {
                JLog.fail("Unable to find link for role: " + role);
            }
        } else {
            JLog.write("Selecting role: " + role);
            link.click();
            isClicked = true;
        }
        return isClicked;
    }

    /**
     * Change role.
     *
     * @param role
     *            the role
     * @param nav
     *            the nav
     * @return true, if successful
     */
    public boolean changeRole(String role, BaseNavigation nav) {

        String currentRole = getCurrentUserRole();
        if (currentRole.equals(role)) {
            JLog.write("Change role to: " + role + "; Already on role, skipping role change.");
            return true;
        }

        JLog.section("Change Role: " + role);
        nav.changeRole();
        boolean success = clickRole(role, true);
        AbstractPage.sleep(4);
        AbstractPage.sleepIfBrowserType(BrowserType.INTERNETEXPLORER, 10);
        JLog.blank();
        return success;
    }

    /**
     * Find role.
     *
     * @param role
     *            the role
     */
    public void findRole(String role) {
        if (role.length() > 34) {
            // if role length is greater than 34 characters, the role isn't
            // found.
            // By having a wildcard, we can then find the role.
            role = role.substring(0, 33) + "*";
        }
        page.setElementValue(By.name("searchString"), role);
        page.elementClick(By.xpath("//a[contains(text(),'Find')]"));
        AbstractPage.sleep(2);
    }

    /**
     *
     * Searches for the specified role and Returns the list of roles in the role
     * selection page
     *
     * @param role
     * @return
     */
    public List<String> getRoles(String role) {
        findRole(role);
        List<String> roles = new ArrayList<>();

        WebElement listingTable = page.get(By.xpath(
                "//table[@id='roles']/tbody[@id='_containerBody']/tr[@vAlign='top']/td[@class='containerBody']/DIV[@id=roles_scroller]/table[@class='tableBorder']"));
        if (listingTable == null) {
            JLog.fail(this.getClass() + ".getRoles() Unable to find table!");
            return null;
        }

        List<WebElement> rowObjects = listingTable.findElements(By.tagName("tr"));
        int rowCount = rowObjects.size();

        // First row is domain org info, second is header, third is role data
        for (int i = 2; i < rowCount; i++) {
            List<WebElement> cells = rowObjects.get(i).findElements(By.tagName("td"));
            List<String> rowText = new ArrayList<>();
            for (int col = 0; col < cells.size(); col++) {
                rowText.add(cells.get(col).getText());
            }
            JLog.write("Row " + i + ":" + rowText.toString());
            roles.add(rowText.get(0));
        }

        return roles;
    }

    /**
     * @return The user name as displayed in the top navigation
     */
    public String getCurrentUserName() {
        if (username == null) {
            parseUserInfo();
        }
        return username;
    }

    /**
     * @return The user role as displayed in the top navigation
     */
    public String getCurrentUserRole() {
        if (userRole == null) {
            parseUserInfo();
        }
        return userRole;
    }

    /**
     * Parses the user info.
     */
    private void parseUserInfo() {

        WebDriver topFrame = page.switchToFrame(getHeaderFrames());
        WebElement shellUsername = null;
        try {
            shellUsername = topFrame.findElement(By.id("shellUsername"));
        } catch (NoSuchElementException e) {
            shellUsername = null;
        }
        if (shellUsername == null) {
            JLog.warning(this.getClass()
                    + ".parseUserInfo(): Unable to find shellUsername element.  Can not parse username and role!",
                    TakeScreenshot.True);

        } else {

            String innerHTML = shellUsername.getAttribute("innerHTML");

            username = parseUsername(innerHTML);
            userRole = paserUserRole(innerHTML);
        }

        page.switchToFrame(getHeaderFrames());
    }

    /**
     * Parses the username.
     *
     * @param innerHTML
     *            the inner HTML
     * @return the string
     */
    private String parseUsername(String innerHTML) {
        // User: <span title="otech">otech</span>&nbsp;<span
        // title="sspqa">sspqa</span> <br>
        String userHTML = innerHTML.split("<br>")[0];
        String firstNameHTML = userHTML.split("</span>")[0];
        String lastNameHTML = userHTML.split("</span>")[1];

        String firstName = getTitleFromHTML(firstNameHTML);
        String lastName = getTitleFromHTML(lastNameHTML);

        return firstName + " " + lastName;
    }

    /**
     * Paser user role.
     *
     * @param innerHTML
     *            the inner HTML
     * @return the string
     */
    private String paserUserRole(String innerHTML) {
        // <br> Role: <span title="scplatform_super_role">scplatform_super_role</span>
        String roleHTML = innerHTML.split("<br>")[1];

        return getTitleFromHTML(roleHTML);

    }

    /**
     * Gets the title from HTML.
     *
     * @param html
     *            the html
     * @return the title from HTML
     */
    private String getTitleFromHTML(String html) {
        // User: <span title="otech">otech
        String title = null;
        Pattern p = Pattern.compile("<span[^>]+title\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher m = p.matcher(html);
        if (m.find()) {
            title = m.group(1);
        }

        // this handles the case for IE8
        if (StringUtils.isBlank(title)) {
            p = Pattern.compile("<span[^>]+title\\s*=\\s*([^'\"]+)[^>]*>");
            m = p.matcher(html);
            if (m.find()) {
                title = m.group(1);
            }
        }
        return title;
    }

    public boolean exit() {
        boolean success = page.elementClick(By.xpath("//a[@href='logout.do']"));

        if ((success) && (CLP.isLogout())) {
            UserMenu userMenu = new UserMenu();
            userMenu.exit();
        }
        return success;
    }

    public boolean exitHarmony() {
        By user = By.xpath("//a[@title='User']");
        // boolean adminLogin = page.elementClick(user);
        page.executeJavaScript("arguments[0].click();", page.get(user));

        boolean success;
        WebElement ele = page.get(By.xpath("//a//i[contains(text(),'exit')]"));
        page.scrollToElement(ele);
        page.executeJavaScript("arguments[0].click();", ele);
        success = true;

        if ((success) && (CLP.isLogout()) && !LoginSCPlatformHarmony.navUrl.contains("dev4160")) {
            UserMenu userMenu = new UserMenu();
            userMenu.exit();
        }
        return success;
    }

    public boolean exitHarmonyAsRole(String role) {
        boolean adminLogin = page.elementClick(By.xpath("//a[@title='User']"));
        boolean success = false;
        if (adminLogin) {
            success = page.elementClick(By.xpath("//a[contains(@href,'MenuModule.logout')]"));
        }

        if ((success) && (CLP.isLogout())) {
            UserMenu userMenu = new UserMenu();
            userMenu.exit();
        }
        return success;
    }
}
