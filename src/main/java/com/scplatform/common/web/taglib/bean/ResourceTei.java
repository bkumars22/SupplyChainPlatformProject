/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class ResourceTei extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData data) {
	String type = null;
	if (data.getAttribute("input") == null) {
	    type = "java.lang.String";
	} else {
	    type = "java.io.InputStream";
	}

	return new VariableInfo[] { new VariableInfo(data.getAttributeString("id"), type, true, 1) };
    }
}