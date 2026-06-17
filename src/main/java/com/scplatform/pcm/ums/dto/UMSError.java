/*
 * Copyright (c) 2014 E2open Inc. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2014, by E2open Inc. All rights reserved.
 */

package com.scplatform.pcm.ums.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UMSError {
    AccessGroup("AccessGroup {0} not found"), AccessGroupRole("AccessGroup {0} not found"), UserName(
            "UserName {0} not found"), Role("AccessControl {0} not found"), Internal("{0}"), NotImplemented(
            "This api is not implemented for MTCM");

    private String message;

    /**
     * @param message
     * @param messageArgs
     */
    private UMSError(String message) {
        this.message = message;
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
     * Replace a single argument
     * @param messageArg
     * @return
     */
    public String getError(String messageArg) {
        String error = this.message.replace("{0}", messageArg);

        return error;
    }
    
    /**
     * Replace multiple arguments
     * @param messageArgs
     * @return
     */
    public String getError(String[] messageArgs) {
        String error = this.message;
        for(int i = 0; i < messageArgs.length; i++) {
            error = error.replace("{"+i+"}", messageArgs[i]);
        }

        return error;
    }

}
