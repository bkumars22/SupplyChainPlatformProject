/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

public class CancelTag extends SubmitTag {
    public CancelTag() {
	this.property = "com.scplatform.common.web.taglib.html.CANCEL";
    }

    public String getOnclick() {
	return super.getOnclick() == null ? "bCancel=true;" : super.getOnclick();
    }

    protected String getElementOpen() {
	return "<input type=\"submit\"";
    }

    protected String prepareName() throws JspException {
	return this.property;
    }

    protected String getDefaultValue() {
	return "Cancel";
    }

    public void release() {
	super.release();
	this.property = "com.scplatform.common.web.taglib.html.CANCEL";
    }
}