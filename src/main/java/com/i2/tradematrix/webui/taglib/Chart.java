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

public class Chart extends BodyTagSupport {
	String id = null;
	String src = null;

	public void setId(String value) {
		this.id = value;
	}

	public void setSrc(String value) {
		this.src = value;
	}

	public int doStartTag() throws JspException {
		return 2;
	}

	public int doEndTag() throws JspException {
		try {
			Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
			this.pageContext.getOut().write("<embed type=\"image/svg-xml\" src=\"" + settings.getSVGDirectory()
					+ "/i2uichart.svg\" id=\"" + this.id + "\"></embed>");
			if (this.src != null) {
				this.pageContext.getOut().write("<xml src=\"" + this.src + "\" id=\"" + this.id + "_xml\"></xml>");
			} else if (this.bodyContent != null) {
				this.pageContext.getOut()
						.write("<xml id=\"" + this.id + "_xml\">" + this.getBodyContent().getString() + "</xml>");
			}

			return 6;
		} catch (IOException var2) {
			throw new JspException(var2.getMessage());
		}
	}

	public void release() {
		super.release();
		this.id = null;
		this.src = null;
	}
}