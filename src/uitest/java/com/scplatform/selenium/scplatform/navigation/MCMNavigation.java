/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.navigation;

import org.openqa.selenium.By;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.navigation.Navigation;
import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class SCPlatformNavigation extends Navigation {

    @Override
    protected String[] getFrames() {
        return null;
    }

    @Override
    protected String[] getTopLevelFrames() {
        return null;
    }

    // --------------------------------------------
    // MAIN NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Main -> Dashboard
     */
    public boolean Dashboard() {
        String[] nav = { "Main", "Dashboard" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Main -> Upload
     */
    public boolean Upload() {
        String[] nav = { "Main", "Upload" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Main -> Manage Upload Jobs
     */
    public boolean ManageUploadJobs() {
        String[] nav = { "Main", "Manage Upload Jobs" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Main -> Review Alerts
     */
    public boolean ReviewAlerts() {
        String[] nav = { "Main", "Review Alerts" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // COSTING NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Costing -> New Sourcing Lane
     */
    public boolean NewSourcingLane() {
        String[] nav = { "Costing", "New Sourcing Lane" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Costing -> Search Sourcing Lane
     */
    public boolean SearchSourcingLane() {
        String[] nav = { "Costing", "Search Sourcing Lane" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Costing -> Search Cost Records
     */
    public boolean SearchCostRecords() {
        String[] nav = { "Costing", "Search Cost Records" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Costing -> Search Commodity Code Cost Records
     */
    public boolean SearchCommodityCodeCostRecords() {
        String[] nav = { "Costing", "Search Commodity Code Cost Records" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // REBATES NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Rebates -> New Rebate Program
     */
    public boolean NewRebateProgram() {
        String[] nav = { "Rebates", "New Rebate Program" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Rebates -> Search Rebate Program
     */
    public boolean SearchRebateProgram() {
        String[] nav = { "Rebates", "Search Rebate Program" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // SUPPLY ALLOCATION NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Supply Allocation -> New Supply Allocation
     */
    public boolean NewSupplyAllocation() {
        String[] nav = { "Supply Allocation", "New Supply Allocation" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Supply Allocation -> Search Supply Allocation
     */
    public boolean SearchSupplyAllocation() {
        String[] nav = { "Supply Allocation", "Search Supply Allocation" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // MASTER DATA MANAGEMENT NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Master Data Management -> Item Assignment
     */
    public boolean ItemAssignment() {
        String[] nav = { "Master Data Management", "Item Assignment" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Master Data Management -> Commodity Management
     */
    public boolean CommodityManagement() {
        String[] nav = { "Master Data Management", "Commodity Management" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Master Data Management -> BOM Management
     */
    public boolean BOMManagement() {
        String[] nav = { "Master Data Management", "BOM Management" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // FORECAST NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Forecast -> New Forecast
     */
    public boolean NewForecast() {
        String[] nav = { "Forecast", "New Forecast" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Forecast -> Search Forecast
     */
    public boolean SearchForecast() {
        String[] nav = { "Forecast", "Search Forecast" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // REPORTS NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Reports -> Submit/View Reports
     */
    public boolean SubmitViewReports() {
        String[] nav = { "Reports", "Submit/View Reports" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Reports -> Sell Price Report
     */
    public boolean SellPriceReport() {
        String[] nav = { "Reports", "Sell Price Report" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // SEARCH NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Search -> Items
     */
    public boolean SearchItems() {
        String[] nav = { "Search", "Items" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Search -> Item AVL
     */
    public boolean SearchItemAVL() {
        String[] nav = { "Search", "Item AVL" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Search -> BOMs
     */
    public boolean SearchBOMs() {
        String[] nav = { "Search", "BOMs" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // ADMINISTRATION NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Administration -> Edit Profile
     */
    public boolean EditProfile() {
        String[] nav = { "Administration", "Edit Profile" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Change Dashboard News
     */
    public boolean ChangeDashboardNews() {
        String[] nav = { "Administration", "Change Dashboard News" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Manage Alerts
     */
    public boolean ManageAlerts() {
        String[] nav = { "Administration", "Manage Alerts" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Manage Items
     */
    public boolean ManageItems() {
        String[] nav = { "Administration", "Manage Items" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Manage Roles
     */
    public boolean ManageRoles() {
        String[] nav = { "Administration", "Manage Roles" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Manage Contacts
     */
    public boolean ManageContacts() {
        String[] nav = { "Administration", "Manage Contacts" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Manage Business Entities
     */
    public boolean ManageBusinessEntities() {
        String[] nav = { "Administration", "Manage Business Entities" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Manage Users
     */
    public boolean ManageUsers() {
        String[] nav = { "Administration", "Manage Users" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Audit History
     */
    public boolean AuditHistory() {
        String[] nav = { "Administration", "Audit History" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Administration -> Admin Upload
     */
    public boolean AdminUpload() {
        String[] nav = { "Administration", "Admin Upload" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Opens a navigation path and link
     *
     * @param nav
     *            String array of the navigation path. The last element of the
     *            array has to be the navigation link name.
     *            <p>
     *            <b>Example Call:</b><br>
     *
     *            <pre>
     *            public void procDiscreteOrderSearch() {
     *                String[] nav = new String[3];
     *                nav[0] = "Order Management (Buy Item)"; // textContents
     *                nav[1] = "Discrete Order"; // textContents
     *                nav[2] = "procDiscreteOrderSearch"; // DomLink name
     *                this.open(nav);
     *            }
     *            </pre>
     *            </p>
     */
    @Override
    public boolean open(String[] nav) {
        this.sleepTime = 0.5;
        boolean success = true;
        String trClass;

        AbstractPage page = new AbstractPage();
        page.switchToFrame(null);
        setSleepTime();

        JLog.write("Navigation: " + join(nav, " --> "));

        for (int i = 0; i < nav.length; i++) {
            if ((i == 0) && (skipFirstNavigation)) {
                continue;
            }

            trClass = (i == 0) ? "applicationPadContent0" : "applicationPadContent1";
            By leafNode = By
                    .xpath("//table[@id='MCM']/tbody/tr[@class='" + trClass + "']/td/a[contains(.,'" + nav[i] + "')]");
            page.get(leafNode).click();

            AbstractPage.sleep(sleepTime);
        }

        checkForError();
        return success;

    }

    protected void checkForError() {
        AbstractPage.sleep(4);
        SCPlatformPage page = new SCPlatformPage();
        String msg = page.getErrorMessage();

        if (msg != null) {

            JLog.fail(msg);
        }
        return;
    }

    private boolean skipFirstNavigation = false;

    public void setSkipFirstNavigation(boolean skip) {
        skipFirstNavigation = true;
    }

}
