/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;

public class TextareaTag extends BaseInputTag {
    public TextareaTag() {
	this.doReadonly = true;
    }

    public int doStartTag() throws JspException {
	TagUtils.getInstance().write(this.pageContext, this.renderTextareaElement());
	return 2;
    }

    protected String renderTextareaElement() throws JspException {
	StringBuffer results = new StringBuffer("<textarea");
	this.prepareAttribute(results, "name", this.prepareName());
	this.prepareAttribute(results, "accesskey", this.getAccesskey());
	this.prepareAttribute(results, "tabindex", this.getTabindex());
	this.prepareAttribute(results, "cols", this.getCols());
	this.prepareAttribute(results, "rows", this.getRows());
	results.append(this.prepareEventHandlers());
	results.append(this.prepareStyles());
	this.prepareOtherAttributes(results);
	results.append(">");
	results.append(this.renderData());
	results.append("</textarea>");
	return results.toString();
    }

    protected String renderData() throws JspException {
	String data = this.value;
	if (data == null) {
	    data = this.lookupProperty(this.name, this.property);
	}

	return data == null ? "" : TagUtils.getInstance().filter(data);
    }

    public void release() {
	super.release();
	this.name = "com.scplatform.common.web.taglib.html.BEAN";
    }
}