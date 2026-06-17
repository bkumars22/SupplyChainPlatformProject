/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Footer extends BodyTagSupport {
	public int doStartTag() throws JspException {
		return 2;
	}

	public int doEndTag() throws JspException {
		BodyContent body = this.getBodyContent();
		FooterSupporter fs = (FooterSupporter) findAncestorWithClass(this, FooterSupporter.class);
		if (fs != null && fs == this.getParent()) {
			fs.setComplexFooter(body.getString());
		}

		body.clearBody();
		return 6;
	}
}