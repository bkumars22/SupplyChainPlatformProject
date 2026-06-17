/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob;

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;

public class LoadJobController extends SCPlatformController {
    protected LoadJobPage page;
    protected List<String> inProgressStates;
    protected int timeoutMinutes = 7;

    /**
     * Status values
     */
    public enum LOAD_STATUS {
        All, PENDING, SUCCESS, WARNING, ERROR
    };

    public LoadJobController() {
        super();
        page = new LoadJobPage();
        initializeInProgressStates();
    }

    @Override
    public PageImpl getView() {
        return new LoadJobPage();
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
        JLog.section("Load Job");

        LoadJobModel model = page.getHeader();

        write(model.getDisplayName("dateLoaded"), model.getDateLoaded());
        write(model.getDisplayName("loadedBy"), model.getLoadedBy());
        write(model.getDisplayName("state"), model.getState());
        write(model.getDisplayName("jobID"), model.getJobID());
        write(model.getDisplayName("uploadType"), model.getUploadType());
        success = verify(model.getDisplayName("status"), model.getStatus(), status);
        write(model.getDisplayName("fileLoaded"), model.getFileLoaded());

        LoadJobDetailsController loadJobDetailsController = new LoadJobDetailsController();
        loadJobDetailsController.print();

        return success;
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

    public void clickShowHistory() {
        clickAndCheckForPOSTError(page.button_ShowHistory());
    }

    public void clickRefreshJob() {
        clickAndCheckForPOSTError(page.button_RefreshJob());
    }

    protected void initializeInProgressStates() {
        inProgressStates = new ArrayList<String>();
        inProgressStates.add(LOAD_STATUS.PENDING.toString());
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
    protected boolean waitForCompletion() {
        String status = page.getElementValue(page.status()).toString();

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
                clickRefreshJob();
                status = page.getElementValue(page.status()).toString();
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

    protected void write(String label, String value) {
        JLog.write(label + " : " + value);
    }

}
