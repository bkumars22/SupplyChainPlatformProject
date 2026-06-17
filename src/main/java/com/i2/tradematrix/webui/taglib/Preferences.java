/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Preferences extends BodyTagSupport {
	boolean renew = false;
	boolean javascriptInline = false;

	public void release() {
		super.release();
		this.renew = false;
		this.javascriptInline = false;
	}

	public void setRenew(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.renew = true;
		}

	}

	public void setJavascriptInline(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.javascriptInline = true;
		}

	}

	public int doStartTag() throws JspException {
		Settings.settingsFactory((HttpServletRequest) this.pageContext.getRequest(), this.renew, this.javascriptInline);
		return 2;
	}
}