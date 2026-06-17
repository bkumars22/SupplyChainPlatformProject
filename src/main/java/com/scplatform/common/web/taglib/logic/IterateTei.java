/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

public class IterateTei extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData data) {
	VariableInfo[] variables = new VariableInfo[2];
	int counter = 0;
	String id = data.getAttributeString("id");
	String type = data.getAttributeString("type");
	if (id != null) {
	    if (type == null) {
		type = "java.lang.Object";
	    }

	    variables[counter++] = new VariableInfo(data.getAttributeString("id"), type, true, 0);
	}

	String indexId = data.getAttributeString("indexId");
	if (indexId != null) {
	    variables[counter++] = new VariableInfo(indexId, "java.lang.Integer", true, 0);
	}

	VariableInfo[] result;
	if (counter > 0) {
	    result = new VariableInfo[counter];
	    System.arraycopy(variables, 0, result, 0, counter);
	} else {
	    result = new VariableInfo[0];
	}

	return result;
    }
}