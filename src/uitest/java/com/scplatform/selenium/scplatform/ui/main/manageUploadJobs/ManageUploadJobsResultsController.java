/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob.LoadJobController.LOAD_STATUS;
import com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob.LoadJobDetailsController;

public class ManageUploadJobsResultsController extends SCPlatformSearchResultsController {
    public List<String> inProgressStates;
    public int timeoutMinutes = 5;
    public ManageUploadJobsResultsPage page;
    protected String expectedStatus = null;

    public ManageUploadJobsResultsController() {
        super();
        page = new ManageUploadJobsResultsPage();
        initializeInProgressStates();
    }

    @Override
    public PageImpl getView() {
        return new ManageUploadJobsResultsPage();
    }

    /**
     * Verifies the displayed status matches expectedStatus. Logs the header and
     * Event Table details.
     *
     * @param status
     *            the expected status
     * @return True if the actual status matches the expectedStatus. If false, a
     *         error is logged and screen capture taken.
     *
     * @see #verify()
     * @see #verify(LOAD_STATUS)
     */
    public boolean verify(String status) {
        boolean success = false;

        waitForCompletion();
        JLog.section("Manage Upload Jobs");

        List<ManageUploadJobsResultsModel> searchResults = page.parseResults();
        ManageUploadJobsResultsModel model = searchResults.get(0);

        write(model.getDisplayName("dateLoaded"), model.getDateLoaded());
        success = verify(model.getDisplayName("status"), model.getStatus(), status);
        write(model.getDisplayName("state"), model.getState());
        write(model.getDisplayName("loadedBy"), model.getLoadedBy());
        write(model.getDisplayName("uploadType"), model.getUploadType());
        write(model.getDisplayName("jobID"), model.getJobID());
        write(model.getDisplayName("fileLoaded"), model.getFileLoaded());

        select(model.getJobID());

        LoadJobDetailsController loadJobDetailsController = new LoadJobDetailsController();
        loadJobDetailsController.print();

        return success;
    }

    public void select(String jobID) {
        WebElement row = page.findRow(jobID, page.tableLocator());
        row.findElement(By.name("selectedLoadJobKey")).click();
    }

    /**
     * Verifies the displayed status matches expectedStatus. Logs the header and
     * Event Table details.
     *
     * @param status
     *            the expected status ({@link LOAD_STATUS})
     * @return True if the actual status matches the expectedStatus. If false, a
     *         error is logged and screen capture taken.
     *
     * @see #verify()
     * @see #verify(String)
     */
    public boolean verify(LOAD_STATUS status) {
        return verify(status.toString());
    }

    /**
     * Verifies the displayed status is SUCCESS (default status). Logs the
     * header and Event Table details.
     *
     * @return True if the actual status is SUCCESS. If false, a error is logged
     *         and screen capture taken.
     *
     * @see #verify(LOAD_STATUS)
     * @see #verify(String)
     */
    public boolean verify() {
        return verify(LOAD_STATUS.SUCCESS);
    }

    /**
     * Sets the In Processing States. The default is "PENDING". Setting this
     * will clear the default value and replace with values in states. You can
     * use enum {@link LOAD_STATUS} to get the states (just added .toString() to
     * the end to convert from enum to string)
     *
     * @param states
     *            List of String for the desired In Processing States
     */
    public void setInProgressStates(List<String> states) {
        inProgressStates.clear();
        inProgressStates.addAll(states);
    }

    public void initializeInProgressStates() {
        inProgressStates = new ArrayList<>();
        inProgressStates.add(LOAD_STATUS.PENDING.toString());
    }

    /**
     * Waits for the Status value to be a value that is NOT in the
     * inProgressStates states. Will execute {@link #clickRefreshJob()} until
     * status changes or a timeout occurs. <br>
     * Error is logged only if a timeout occurs.
     *
     * @return true if the status is not one from inProgressStates.
     *
     * @see #setInProgressStates(List)
     */
    public boolean waitForCompletion() {
        List<ManageUploadJobsResultsModel> searchResults = page.parseResults();
        String status = searchResults.get(0).getStatus();

        boolean timedOut = false;
        long startTime = System.currentTimeMillis();
        long t = 0;
        int TIMEOUT = timeoutMinutes * 60 * 1000; // convert to milliseconds

        while ((this.inProgressStates.contains(status)) && (!timedOut)) {
            t = System.currentTimeMillis();
            if (t - startTime > TIMEOUT) {
                timedOut = true;
            } else {
                page.sleep(10);
                clickAndCheckForPOSTError(page.searchButton());

                searchResults.clear();
                searchResults = page.parseResults();
                status = searchResults.get(0).getStatus();
            }
        }

        boolean success = true;
        if (this.inProgressStates.contains(status)) {
            success = false;

            if (timedOut) {
                JLog.error(
                        this.getClass().getSimpleName() + ".waitForCompletion(): Timeout occurred after "
                                + timeoutMinutes + " minutes waiting for file to process.  Current status is '" + status
                                + "'. In Progress status's set to: " + inProgressStates.toString(),
                        TakeScreenshot.True);
            }
        }
        return success;
    }

    public void write(String label, String value) {
        JLog.write(label + " : " + value);
    }
}
