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

public class Tree extends BodyTagSupport {
	String id = null;
	String width = null;
	int treecellCount = 0;
	Settings settings;

	public Settings getSettings() {
		return this.settings;
	}

	public void setId(String value) {
		this.id = value;
	}

	public void setWidth(String value) {
		this.width = value;
	}

	public String getId() {
		return this.id;
	}

	public StringBuffer generateTreeKey(int depth, boolean spanning) {
		int mykey = depth * 10;
		if (spanning) {
			mykey += 5;
		} else {
			++this.treecellCount;
		}

		return new StringBuffer("" + mykey + "." + this.treecellCount);
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		if (this.getParent() != null && this.getParent() == findAncestorWithClass(this, Container.class)) {
			((Container) this.getParent()).setHasTable();
		}

		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		if (this.width == null) {
			this.width = "100%";
		}

		result.append("<TABLE width=\"" + this.width
				+ "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" class=\"tableBorder\" id=\"" + this.id
				+ "\">");

		try {
			this.pageContext.getOut().write(result.toString());
		} catch (IOException var4) {
			throw new JspException("IO Error: " + var4.getMessage());
		}

		this.treecellCount = 0;
		return 2;
	}

	public int doEndTag() throws JspException {
		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}

		try {
			this.pageContext.getOut().write("</TABLE>");
			return 6;
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public void release() {
		super.release();
		this.id = null;
		this.treecellCount = 0;
		this.settings = null;
		this.width = null;
	}
}