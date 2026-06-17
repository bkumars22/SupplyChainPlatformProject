/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class SkinTEI extends TagExtraInfo {
	VariableInfo[] info_ = new VariableInfo[1];

	public SkinTEI() {
		this.info_[0] = new VariableInfo("i2Skin", "com.scplatform.testing.webui.plaf.Skin", true, 2);
	}

	public VariableInfo[] getVariableInfo(TagData data) {
		return this.info_;
	}
}