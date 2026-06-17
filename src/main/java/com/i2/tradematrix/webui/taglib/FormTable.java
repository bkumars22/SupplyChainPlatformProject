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

public class FormTable extends BodyTagSupport {
	boolean readonly = false;
	String classid = "";
	Settings settings;
	String id = null;

	public void setReadonly(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.readonly = true;
		}

	}

	public void setId(String value) {
		this.id = value;
	}

	public String getId() {
		return this.id;
	}

	public int doStartTag() throws JspException {
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		String classname = "formTable";
		String classid = "";
		if (this.readonly) {
			classid = " id=\"readOnly\"";
		}

		try {
			String attributeID = "";
			if (this.id != null) {
				attributeID = " id=\"" + this.id + "\"";
			}

			this.pageContext.getOut().write("<DIV class=\"" + classname + "\"" + classid
					+ "><TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\"" + attributeID + ">");
			return 2;
		} catch (IOException var4) {
			throw new JspException("IO Error: " + var4.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}

			this.pageContext.getOut().write("</TABLE>");
			this.pageContext.getOut().write("</DIV>");
			return 6;
		} catch (IOException var2) {
			throw new JspException(var2.getMessage());
		}
	}

	public void release() {
		super.release();
		this.readonly = false;
		this.id = null;
	}
}