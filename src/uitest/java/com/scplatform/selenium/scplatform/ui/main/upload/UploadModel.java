/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.upload;

import com.test.selenium.common.modelViewController.annotations.AutoPopulateOff;
import com.test.selenium.common.modelViewController.model.Model;

public class UploadModel extends Model {

    private static final long serialVersionUID = 1L;

    private String messageType;
    @AutoPopulateOff
    private String uploadFile;
    private String status;
    private String message;

    /**
     * @return the messageType
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * @param messageType
     *            the messageType to set
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param messageType
     *            the messageType to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the uploadFile
     */
    public String getUploadFile() {
        return uploadFile;
    }

    /**
     * @param uploadFile
     *            the uploadFile to set
     */
    public void setUploadFile(String uploadFile) {
        this.uploadFile = uploadFile;
    }

}
