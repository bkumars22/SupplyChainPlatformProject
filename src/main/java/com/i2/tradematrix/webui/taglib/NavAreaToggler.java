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

public class NavAreaToggler extends TagSupport {
	String location = null;
	String name = null;
	Settings settings;

	public void setLocation(String value) {
		this.location = value;
	}

	public void setName(String value) {
		this.name = value;
	}

	public int doStartTag() throws JspException {
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		StringBuffer result = new StringBuffer();
		result.append(
				"<a id=\"navareatoggler\" href=\"javascript:if (document.getElementById){ var obj = document.getElementById('navareatogglericon'); if (obj.src.indexOf('close') > 0){obj.src='"
						+ this.settings.getImageDirectory() + "/nav_pad_norgie_open.png';} else{ obj.src='"
						+ this.settings.getImageDirectory() + "/nav_pad_norgie_close.png';} " + this.location
						+ ".i2uiToggleNavarea('" + this.name + "');}\">");
		result.append("<img id=\"navareatogglericon\" src=\"" + this.settings.getImageDirectory()
				+ "/nav_pad_norgie_close.png\" border=\"0\">");
		result.append("</a>");

		try {
			this.pageContext.getOut().write(result.toString());
			return 0;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}

	public void release() {
		super.release();
		this.settings = null;
		this.name = null;
		this.location = null;
	}
}