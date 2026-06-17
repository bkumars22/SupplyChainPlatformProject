/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.dto;


import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Base form that all forms of the PCM workflows should subclass Provides basic
 * support for handling various business process related activities;
 */
@SuppressWarnings("serial")
public class PcmBaseForm {
    private String requestType;
    private String defaultRequestType;
    private ApplicationContext appContext = null;

    public void setRequestType(String requestType) {
	this.requestType = StringUtils.trimToNull(requestType);
    }

    public String getRequestType() {
	return (requestType != null) ? requestType : defaultRequestType;
    }

    public void setDefaultRequestType(String defaultRequestType) {
	this.defaultRequestType = defaultRequestType;
    }

    public String getDefaultRequestType() {
	return defaultRequestType;
    }

    public void reset(HttpServletRequest request) {
        try {
            appContext = AppContextHelper.getValidContext(request);
        } catch (InvalidUserContext iuc) {
            appContext = null;
        }
    }

    public ApplicationContext getAppContext() {
	return appContext;
    }

    public String getCurrentDateFormat() {
	return appContext.getCurrentDateFormat();
    }

    public String getTimeFormat() {
	return appContext.getCurrentTimeFormat();
    }

    public Locale getCurrentLocale() {
	return appContext.getCurrentLocale();
    }
}
