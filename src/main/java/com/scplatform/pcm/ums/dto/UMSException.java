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

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

// RuntimeException subclass — @Getter/@Setter only (not @Data to avoid
// conflicting equals/hashCode with Throwable)
@Getter
@Setter
public class UMSException extends RuntimeException {

    private Throwable rootCause;
    private Throwable cause;
    private String message;

    public UMSException() {
    }

    public UMSException(String message) {
        setMessage(message);
    }

    public UMSException(Throwable t) {
        this(t, "");
    }

    public UMSException(Throwable t, String message) {
        setRootCause(ExceptionUtils.getRootCause(t));
        setCause(t.getCause());
        setMessage(message + "(" + t.toString() + ")");
        setStackTrace(t.getStackTrace());
    }
}