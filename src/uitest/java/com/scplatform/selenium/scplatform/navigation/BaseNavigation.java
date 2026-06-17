/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.navigation;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.navigation.Navigation;

public class BaseNavigation extends Navigation {

    // --------------------------------------------
    // HEADER FRAME NAVIGATION
    // --------------------------------------------
    /**
     * Top Frame Navigation: About
     */
    public boolean about() {
        String[] nav = new String[1];

        nav[0] = "About";

        boolean sucess = this.openTopNav(nav[0]);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);
        return sucess;
    }

    /**
     * Top Frame Navigation: Bookmark
     */
    public boolean bookmark() {
        String[] nav = new String[1];

        this.openTopNav(nav[0]);

        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;

    }

    /**
     * Top Frame Navigation: Help
     */
    public boolean help() {
        String[] nav = new String[1];

        nav[0] = "Help";

        boolean sucess = this.openTopNav(nav[0]);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;

    }

    /**
     * Top Frame Navigation: Feedback
     */
    public boolean feedback() {
        String[] nav = new String[1];

        nav[0] = "Feedback";

        boolean sucess = this.openTopNav(nav[0]);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);
        return sucess;
    }

    // --------------------------------------------
    // COMMON NAVIGATIONS
    // --------------------------------------------
    /**
     * Navigation: Home
     */
    public boolean home() {
        String[] nav = new String[1];

        nav[0] = "serviceHome";

        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        AbstractPage.sleep(3);
        return sucess;
    }

    /**
     * Navigation: My Workspace
     */
    public boolean myWorkspace() {
        String[] nav = new String[1];

        nav[0] = "thePortalPage";

        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // MASTER DATA ADMIN NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: Upload/Download -> Uploads -> Master Data Admin --> Master
     * Data Upload
     */
    public boolean ioMasterUpload() {
        String[] nav = new String[4];

        nav[0] = "Upload/Download";
        nav[1] = "Uploads";
        nav[2] = "Master Data Admin";
        nav[3] = "ioMasterUpload";

        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Upload/Download -> Downloads -> Master Data Admin --> Collab
     * Attribute (Admin)
     */
    public boolean ioCollabAttrDnload() {
        String[] nav = { "Upload/Download", "Downloads", "Master Data Admin", "ioCollabAttrDnload" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: Upload/Download -> Downloads -> Master Data Admin -> Admin
     * Download Group -> Supply Forecast Inventory Admin Download Group ->
     * ioProcAdminForecastDnload
     */
    public boolean ioProcAdminForecastDnload() {
        String[] nav = { "Upload/Download", "Downloads", "Master Data Admin", "Admin Download Group",
                "Supply Forecast Inventory Admin Download Group", "ioProcAdminForecastDnload" };
        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    // --------------------------------------------
    // MY PROFILE NAVIGATION
    // --------------------------------------------

    /**
     * Navigation: My Profile -> Change Role
     */
    public boolean changeRole() {
        String[] nav = new String[2];

        nav[0] = "My Profile";
        nav[1] = "changeRole";

        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

    /**
     * Navigation: My Profile -> Email Alert Subscription
     */
    public boolean userPreferences() {
        String[] nav = new String[2];

        nav[0] = "My Profile";
        nav[1] = "userPreferences";

        boolean sucess = this.open(nav);
        if ((!sucess) && (failOnError))
            JLog.fail(errorMsg);

        return sucess;
    }

}
