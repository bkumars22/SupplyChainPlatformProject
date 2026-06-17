/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.Tag;

import com.scplatform.common.web.taglib.MessageResources;

public class ParamTag extends BodyTagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String name = null;
    protected String value = null;

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getValue() {
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public int doStartTag() throws JspException {
	return 2;
    }

    public int doAfterBody() throws JspException {
	if (this.bodyContent != null) {
	    String value = this.bodyContent.getString().trim();
	    if (value.length() > 0) {
		this.value = value;
	    }
	}

	return 0;
    }

    public int doEndTag() throws JspException {
	Tag tag = findAncestorWithClass(this, LinkTag.class);
	if (tag != null) {
	    ((LinkTag) tag).addParameter(this.name, this.value);
	    return 6;
	} else {
	    throw new JspException(messages.getMessage("linkParamTag.linkParam"));
	}
    }

    public void release() {
	super.release();
	this.name = null;
	this.value = null;
    }
}
