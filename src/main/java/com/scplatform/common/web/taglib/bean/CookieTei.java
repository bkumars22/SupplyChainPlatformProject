/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class CookieTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		String className = null;
		if (data.getAttribute("multiple") == null) {
			className = "jakarta.servlet.http.Cookie";
		} else {
			className = "jakarta.servlet.http.Cookie[]";
		}

		return new VariableInfo[]{new VariableInfo(
				data.getAttributeString("id"), className, true, 1)};
	}
}