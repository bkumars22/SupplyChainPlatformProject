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

public class PopupMenuDivider extends TagSupport {
	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		result.append("<TR class=\"menuUnhighlighted\">");
		result.append("<TD nowrap=\"yes\" class=\"menuText\">");
		result.append("<IMG width=\"100%\" height=\"6px\" src=\"" + settings.getImageDirectory()
				+ "/popup_menu_divider.png\">");
		result.append("</TD>");
		result.append("</TR>");

		try {
			this.pageContext.getOut().write(result.toString());
			return 0;
		} catch (IOException var4) {
			throw new JspException(var4.getMessage());
		}
	}

	public void release() {
		super.release();
	}
}