/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

public class ResetTag extends SubmitTag {
    protected String getElementOpen() {
	return "<input type=\"reset\"";
    }

    protected String prepareName() throws JspException {
	return this.property;
    }

    protected String getDefaultValue() {
	return "Reset";
    }
}