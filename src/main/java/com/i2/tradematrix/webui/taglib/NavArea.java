/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class NavArea extends BodyTagSupport {
	Settings settings;

	public int doStartTag() throws JspException {
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		StringBuffer result = new StringBuffer();
		result.append("<DIV id=\"i2uinavarea\" style=\"overflow:auto;\">");

		try {
			this.pageContext.getOut().write(result.toString());
			return 2;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer();
		result.append("</DIV>");

		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}

			this.pageContext.getOut().write(result.toString());
			return 6;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}

	public void release() {
		super.release();
		this.settings = null;
	}
}