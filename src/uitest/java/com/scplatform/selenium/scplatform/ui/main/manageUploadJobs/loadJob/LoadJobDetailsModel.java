/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class LoadJobDetailsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Type")
    private String type;

    @DisplayName("Message")
    private String message;

    @DisplayName("Location")
    private String location;

    @DisplayName("Date Loaded")
    private String dateLoaded;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

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

}
