/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class MTCMTei extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData data) {
	String type = null;
	if (data.getAttribute("formBean") != null) {
	    type = "com.test.controller.action.ActionFormBean";
	} else if (data.getAttribute("forward") != null) {
	    type = "com.test.controller.action.ActionForward";
	} else if (data.getAttribute("mapping") != null) {
	    type = "com.test.controller.action.ActionMapping";
	} else {
	    type = "java.lang.Object";
	}

	return new VariableInfo[] { new VariableInfo(data.getAttributeString("id"), type, true, 1) };
    }
}