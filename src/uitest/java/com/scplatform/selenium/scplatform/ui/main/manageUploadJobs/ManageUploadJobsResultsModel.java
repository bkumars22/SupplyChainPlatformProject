/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class ManageUploadJobsResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Date Loaded")
    private String dateLoaded;

    @DisplayName("Status")
    private String status;

    @DisplayName("State")
    private String state;

    @DisplayName("Loaded By")
    private String loadedBy;

    @DisplayName("Upload Type")
    private String uploadType;

    @DisplayName("Job ID")
    private String jobID;

    @DisplayName("File Loaded")
    private String fileLoaded;

    /**
     * @return the dateLoaded
     */
    public String getDateLoaded() {
        return dateLoaded;
    }

    /**
     * @param dateLoaded
     *            the dateLoaded to set
     */
    public void setDateLoaded(String dateLoaded) {
        this.dateLoaded = dateLoaded;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the loadedBy
     */
    public String getLoadedBy() {
        return loadedBy;
    }

    /**
     * @param loadedBy
     *            the loadedBy to set
     */
    public void setLoadedBy(String loadedBy) {
        this.loadedBy = loadedBy;
    }

    /**
     * @return the uploadType
     */
    public String getUploadType() {
        return uploadType;
    }

    /**
     * @param uploadType
     *            the uploadType to set
     */
    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    /**
     * @return the jobID
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * @param jobID
     *            the jobID to set
     */
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    /**
     * @return the fileLoaded
     */
    public String getFileLoaded() {
        return fileLoaded;
    }

    /**
     * @param fileLoaded
     *            the fileLoaded to set
     */
    public void setFileLoaded(String fileLoaded) {
        this.fileLoaded = fileLoaded;
    }

}
