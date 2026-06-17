/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class MessagesTei extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData data) {
	String type = "java.lang.String";
	return new VariableInfo[] { new VariableInfo(data.getAttributeString("id"), type, true, 0) };
    }
}