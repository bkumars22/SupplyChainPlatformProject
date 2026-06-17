/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class SizeTei extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData data) {
	return new VariableInfo[] { new VariableInfo(data.getAttributeString("id"), "java.lang.Integer", true, 1) };
    }
}