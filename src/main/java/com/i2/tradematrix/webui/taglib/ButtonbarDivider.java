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

public class ButtonbarDivider extends TagSupport {
	public int doAfterBody() throws JspException {
		return 0;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		Buttonbar owner = null;
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		owner = (Buttonbar) findAncestorWithClass(this, Buttonbar.class);
		if (owner != null) {
			owner.setPreviousIsDivider();
			result.append("<TD width=\"2px\" style=\"font-size:1px\" nowrap=\"yes\">&nbsp;</TD>");
			result.append("<TD nowrap=\"yes\">");
			result.append("<IMG src=\"" + settings.getImageDirectory() + "/blue_divider.png\">");
			result.append("</TD>");
			result.append("<TD width=\"2px\" style=\"font-size:1px\" nowrap=\"yes\">&nbsp;</TD>");

			try {
				this.pageContext.getOut().write(result.toString());
			} catch (IOException var5) {
				throw new JspException(var5.getMessage());
			}
		}

		return 0;
	}

	public void release() {
		super.release();
	}
}