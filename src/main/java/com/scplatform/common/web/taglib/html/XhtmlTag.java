/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class XhtmlTag extends TagSupport {
    public int doEndTag() throws JspException {
	this.pageContext.setAttribute("com.test.controller.globals.XHTML", "true", 1);
	return 6;
    }
}