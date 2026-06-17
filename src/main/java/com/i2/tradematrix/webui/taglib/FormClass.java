/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class FormClass extends TagSupport {
	String objectType = null;
	Settings settings;

	public void setType(String value) {
		this.objectType = value.toLowerCase();
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		if (this.objectType.equals("inputfield")) {
			result.append("inputField");
		} else if (this.objectType.equals("smallinputfield")) {
			result.append("smallinputField");
		} else if (this.objectType.equals("displayfield")) {
			result.append("displayField");
		} else if (this.objectType.equals("smalldisplayfield")) {
			result.append("smalldisplayField");
		} else if (this.objectType.equals("pulldown")) {
			result.append("pulldown");
		} else if (this.objectType.equals("pulldownmultiple")) {
			result.append("pulldownMultiple");
		} else if (this.objectType.equals("listdisplayfield")) {
			result.append("listDisplayField");
		} else {
			result.append("bodyText");
		}

		try {
			this.pageContext.getOut().write(result.toString());
			return 0;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}

	public void release() {
		super.release();
		this.objectType = null;
	}
}