/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Tabform extends BodyTagSupport {
	String action = null;
	String method = null;
	String name = null;
	String target = null;

	public void setName(String value) {
		this.name = new String(value);
	}

	public void setMethod(String value) {
		this.method = new String(value);
	}

	public void setTarget(String value) {
		this.target = new String(value);
	}

	public void setAction(String value) {
		this.action = new String(value);
	}

	public int doStartTag() throws JspException {
		return 2;
	}

	public int doEndTag() throws JspException {
		BodyContent body = this.getBodyContent();
		Tab tab = (Tab) findAncestorWithClass(this, Tab.class);
		if (tab != null) {
			StringBuffer result = new StringBuffer("<form");
			if (this.action != null) {
				result.append(" action=\"" + this.action + "\"");
			}

			if (this.method != null) {
				result.append(" method=\"" + this.method + "\"");
			}

			if (this.name != null) {
				result.append(" name=\"" + this.name + "\"");
			}

			if (this.target != null) {
				result.append(" target=\"" + this.target + "\"");
			}

			tab.setForm(result + ">" + body.getString() + "</form>");
		}

		body.clearBody();
		return 6;
	}

	public void release() {
		super.release();
		this.action = null;
		this.method = null;
		this.name = null;
		this.target = null;
	}
}