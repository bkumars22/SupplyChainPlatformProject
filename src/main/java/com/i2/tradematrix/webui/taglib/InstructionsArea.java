/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class InstructionsArea extends TagSupport {
	public int doStartTag() throws JspException {
		try {
			this.pageContext.getOut().write("<P class=\"instructionsArea\">");
			return 1;
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		try {
			this.pageContext.getOut().write("</P>");
			return 6;
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public void release() {
		super.release();
	}
}