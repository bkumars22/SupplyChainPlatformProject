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

public class Vr extends TagSupport {
	String rowspan = "";
	String width = null;

	public void setRowspan(String value) {
		this.rowspan = "rowspan=\"" + value + "\" ";
	}

	public void setWidth(String value) {
		this.width = value;
	}

	public int doStartTag() throws JspException {
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		StringBuffer result = new StringBuffer();
		if (this.width == null) {
			this.width = "1px";
		}

		result.append("<td " + this.rowspan + "width=\"" + this.width
				+ "\" valign=\"top\" align=\"center\" style=\"background-position:center;background-image:url("
				+ settings.getImageDirectory()
				+ "/1x1_grey.png);background-repeat:repeat-y;font-size:2px;\">&nbsp;</td>");

		try {
			this.pageContext.getOut().write(result.toString());
			return 0;
		} catch (IOException var4) {
			throw new JspException(var4.getMessage());
		}
	}

	public void release() {
		super.release();
		this.rowspan = "";
		this.width = null;
	}
}