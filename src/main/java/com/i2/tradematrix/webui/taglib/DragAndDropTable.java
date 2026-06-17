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

public class DragAndDropTable extends BodyTagSupport {
	String title = null;
	String id = null;
	String width = null;
	int rowCount = 0;
	Settings settings;

	public Settings getSettings() {
		return this.settings;
	}

	public void setTitle(String value) {
		this.title = value;
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

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		if (this.getParent() != null && this.getParent() == findAncestorWithClass(this, Container.class)) {
			((Container) this.getParent()).setHasTable();
		}

		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		if (this.id == null) {
			this.id = "dragAndDropTable";
		}

		if (this.width == null) {
			this.width = "100%";
		}

		if (this.title != null) {
			result.append("<table width=\"" + this.width
					+ "\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tableBorder\">");
			result.append("<tr><td class=\"tableHeader\">&nbsp;" + this.title + "</td></tr>");
			result.append("<tr><td>");
		}

		if (this.title != null) {
			result.append(
					"<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tableBorder\"");
		} else {
			result.append("<table width=\"" + this.width
					+ "\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tableBorder\"");
		}

		if (this.id != null) {
			result.append(" id=\"" + this.id + "\"");
		}

		result.append(">");

		try {
			this.pageContext.getOut().write(result.toString());
		} catch (IOException var4) {
			throw new JspException("IO Error: " + var4.getMessage());
		}

		this.rowCount = 0;
		return 2;
	}

	public int incrCount() throws JspException {
		++this.rowCount;
		return this.rowCount;
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
			if (this.title != null) {
				this.pageContext.getOut().write("</table></td></tr></table>");
			} else {
				this.pageContext.getOut().write("</table>");
			}

			return 6;
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public void release() {
		super.release();
		this.title = null;
		this.id = null;
		this.rowCount = 0;
		this.settings = null;
		this.width = null;
	}
}