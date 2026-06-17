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

public class PopupMenu extends BodyTagSupport {
	String name = null;
	int count = 0;
	boolean isRemoveOnClick = false;
	Settings settings;

	public void setName(String value) {
		this.name = value;
	}

	public String getName() {
		return this.name;
	}

	public boolean isRemoveOnClick() {
		return this.isRemoveOnClick;
	}

	public void setIsRemoveOnClick(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.isRemoveOnClick = true;
		}

	}

	public String getChildName() {
		++this.count;
		return this.name + "_" + this.count;
	}

	public int doAfterBody() throws JspException {
		return 0;
	}

	public int doStartTag() throws JspException {
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		StringBuffer result = new StringBuffer();
		result.append("<div id=\"" + this.name
				+ "\" class=\"menu\" style=\"position:absolute;left:1px;top:1px;visibility:hidden;\">");
		result.append(
				"<TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\"><TR class=\"menuUnHighlighted\"><TD class=\"menuShadow\">");
		result.append("<TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\">");

		try {
			this.pageContext.getOut().write(result.toString());
			return 2;
		} catch (IOException var3) {
			throw new JspException("IO Error: " + var3.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer();
		result.append("<TR class=\"menuUnHighlighted\"><TD colspan=\"2px\" style=\"font-size:1px\">&nbsp;</TD></TR>");
		result.append("</TABLE></TD></TR></TABLE></div>");

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
		this.name = null;
		this.count = 0;
		this.settings = null;
	}
}