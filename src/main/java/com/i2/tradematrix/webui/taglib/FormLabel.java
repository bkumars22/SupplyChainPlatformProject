/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class FormLabel extends BodyTagSupport {
	boolean required = false;

	public void setRequired(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.required = true;
		}

	}

	public int doEndTag() throws JspException {
		new StringBuffer();

		try {
			this.pageContext.getOut().write("<TD nowrap=\"yes\" class=\"formLabel\">");
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}

			if (this.required) {
				this.pageContext.getOut().write("<SPAN class=\"requiredIndicator\">*</SPAN>");
			}

			this.pageContext.getOut().write("</TD>");
			return 6;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}

	public void release() {
		super.release();
		this.required = false;
	}
}