/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class SubmitTag extends BaseHandlerTag {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String property = null;
    protected String text = null;
    protected String value = null;

    public String getProperty() {
	return this.property;
    }

    public void setProperty(String property) {
	this.property = property;
    }

    public String getValue() {
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public int doStartTag() throws JspException {
	this.text = null;
	return 2;
    }

    public int doAfterBody() throws JspException {
	if (this.bodyContent != null) {
	    String value = this.bodyContent.getString().trim();
	    if (value.length() > 0) {
		this.text = value;
	    }
	}

	return 0;
    }

    public int doEndTag() throws JspException {
	StringBuffer results = new StringBuffer();
	results.append(this.getElementOpen());
	this.prepareAttribute(results, "name", this.prepareName());
	this.prepareButtonAttributes(results);
	results.append(this.prepareEventHandlers());
	results.append(this.prepareStyles());
	this.prepareOtherAttributes(results);
	results.append(this.getElementClose());
	TagUtils.getInstance().write(this.pageContext, results.toString());
	return 6;
    }

    protected String getElementOpen() {
	return "<input type=\"submit\"";
    }

    protected String prepareName() throws JspException {
	if (this.property == null) {
	    return null;
	} else if (this.indexed) {
	    StringBuffer results = new StringBuffer();
	    results.append(this.property);
	    this.prepareIndex(results, (String) null);
	    return results.toString();
	} else {
	    return this.property;
	}
    }

    protected void prepareButtonAttributes(StringBuffer results) throws JspException {
	this.prepareAttribute(results, "accesskey", this.getAccesskey());
	this.prepareAttribute(results, "tabindex", this.getTabindex());
	this.prepareValue(results);
    }

    protected void prepareValue(StringBuffer results) {
	String label = this.value;
	if (label == null && this.text != null) {
	    label = this.text;
	}

	if (label == null || label.length() < 1) {
	    label = this.getDefaultValue();
	}

	this.prepareAttribute(results, "value", label);
    }

    protected String getDefaultValue() {
	return "Submit";
    }

    public void release() {
	super.release();
	this.property = null;
	this.text = null;
	this.value = null;
    }
}
