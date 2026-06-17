/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocTypeTag extends TagSupport {
	private static final Logger logCategory_ = LoggerFactory.getLogger(DocTypeTag.class);
	protected String docType_ = null;

	public DocTypeTag() {
		this.resetCustomAttributes();
	}

	public void setType(String docType) {
		if (docType != null) {
			this.docType_ = docType.toLowerCase();
		}

	}

	public String getType() {
		return this.docType_;
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.docType_ = "standards";
	}

	public int doStartTag() throws JspException {
		String uri = "";
		String dtd = "";
		if ("loose".equals(this.docType_)) {
			uri = " Transitional";
			dtd = "loose";
		} else if ("frameset".equals(this.docType_)) {
			uri = " Frameset";
			dtd = "frameset";
		} else if ("strict".equals(this.docType_)) {
			uri = "";
			dtd = "strict";
		}

		try {
			JspWriter out = this.pageContext.getOut();
			if (!this.docType_.equals("standards")) {
				out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01");
				out.write(uri);
				out.write("//EN\" \"http://www.w3.org/TR/html4/");
				out.write(dtd);
				out.write(".dtd\">");
			} else {
				out.write("<!DOCTYPE html>");
			}
		} catch (IOException var4) {
			logCategory_.warn("Unable to generate document type for page.");
		}

		return 0;
	}

}