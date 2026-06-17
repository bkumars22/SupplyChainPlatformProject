/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.dto.SearchParameter;

import java.util.Map;

public interface SearchParameterInitializer
{
	public static final String REQUEST_TYPE = "REQUEST_TYPE";
	public static final String APP_CONTEXT = "APP_CONTEXT";
	public static final String ROLE = "ROLE";
	public static final String ACTIVE_BE = "ACTIVE_BE_KEY";
	public static final String ENTERPRISE_BE = "ENTERPRISE_BE_KEY";
	boolean initializeParameter(SearchParameter parameter, Map context);
	void setInitialData(String data);
}

