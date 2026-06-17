/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class PageTei extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData data) {
	String type = null;
	String property = data.getAttributeString("property");
	if ("application".equalsIgnoreCase(property)) {
	    type = "jakarta.servlet.ServletContext";
	} else if ("config".equalsIgnoreCase(property)) {
	    type = "jakarta.servlet.ServletConfig";
	} else if ("request".equalsIgnoreCase(property)) {
	    type = "jakarta.servlet.ServletRequest";
	} else if ("response".equalsIgnoreCase(property)) {
	    type = "jakarta.servlet.ServletResponse";
	} else if ("session".equalsIgnoreCase(property)) {
	    type = "jakarta.servlet.http.HttpSession";
	} else {
	    type = "java.lang.Object";
	}

	return new VariableInfo[] { new VariableInfo(data.getAttributeString("id"), type, true, 1) };
    }
}