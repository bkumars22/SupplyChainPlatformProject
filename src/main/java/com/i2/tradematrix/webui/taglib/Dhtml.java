/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

public class Dhtml extends JavaScriptLinkTag {
	boolean bIncludePadSupport = false;
	boolean includeDatePickerSupport = false;
	String locale = null;

	public void setPadsupport(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.bIncludePadSupport = true;
		}

	}

	public void setDatepickersupport(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.includeDatePickerSupport = true;
		}

	}

	public void setLocale(String value) {
		this.locale = value;
	}

	public int doStartTag() throws JspException {
		try {
			Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
			super.setPath(settings.getJavascriptDirectory() + "/i2uitaglib.js");
			super.setAbsolute("yes");
			super.doStartTag();
			StringBuffer result = new StringBuffer();
			result.append("<SCRIPT language=\"javascript\">i2uiSetImageDirectory('");
			result.append(settings.getImageDirectory());
			result.append("');i2uiDatePickerSetJspDir('");
			result.append(settings.getJavascriptDirectory());
			result.append("');</SCRIPT>");
			this.pageContext.getOut().write(result.toString());
			result.append("<SCRIPT language=\"javascript\">i2uiSetBrowserType('");
			result.append(settings.getBrowserType());
			result.append("');</SCRIPT>");
			this.pageContext.getOut().write(result.toString());
			if (this.bIncludePadSupport) {
				super.setPath(settings.getJavascriptDirectory() + "/i2uipad.js");
				super.setAbsolute("yes");
				super.doStartTag();
			}

			if (this.includeDatePickerSupport) {
				if (this.locale == null) {
					this.locale = settings.getLocale().toString();
				}

				if (this.locale != null) {
					super.setPath(settings.getJavascriptDirectory() + "/i2uidatepicker.jsp?locale=" + this.locale);
				} else {
					super.setPath(settings.getJavascriptDirectory() + "/i2uidatepicker.jsp");
				}

				super.setAbsolute("yes");
				super.doStartTag();
			}

			return 0;
		} catch (Exception var3) {
			var3.printStackTrace();
			throw new JspException(var3.getMessage());
		}
	}

	public void release() {
		super.release();
		this.bIncludePadSupport = false;
		this.includeDatePickerSupport = false;
		this.locale = null;
	}
}