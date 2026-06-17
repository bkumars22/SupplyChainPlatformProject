/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class Region extends TagSupport {
	Settings settings;

	public int doStartTag() throws JspException {
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());

		try {
			this.pageContext.getOut().write("<DIV id=\"i2uiregion\" class=\"region\">");
			return 1;
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		try {
			this.pageContext.getOut().write("</DIV>");
			return 6;
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public void release() {
		super.release();
		this.settings = null;
	}
}