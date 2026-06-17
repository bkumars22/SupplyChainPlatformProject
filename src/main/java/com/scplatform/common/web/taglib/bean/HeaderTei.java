/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class HeaderTei extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData data) {
	String className = null;
	if (data.getAttribute("multiple") == null) {
	    className = "java.lang.String";
	} else {
	    className = "java.lang.String[]";
	}

	return new VariableInfo[] { new VariableInfo(data.getAttributeString("id"), className, true, 1) };
    }
}