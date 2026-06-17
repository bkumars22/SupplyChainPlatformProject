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

public class FormFieldGap extends TagSupport {
	String rowspan = null;

	public void setRowspan(String value) {
		this.rowspan = value;
	}

	public int doStartTag() throws JspException {
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());

		try {
			this.pageContext.getOut().write("<td><img src=" + settings.getImageDirectory()
					+ "/1pixel.png width='4px' height='7px' alt='' border='0px'></td>");
			return 0;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}

	public void release() {
		super.release();
		this.rowspan = null;
	}
}