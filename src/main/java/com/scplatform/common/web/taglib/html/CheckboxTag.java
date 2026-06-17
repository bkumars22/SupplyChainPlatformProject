/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class CheckboxTag extends BaseHandlerTag {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String name = "com.scplatform.common.web.taglib.html.BEAN";
    protected String property = null;
    protected String text = null;
    protected String value = null;

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getProperty() {
	return this.property;
    }

    public void setProperty(String property) {
	this.property = property;
    }

    public String getValue() {
	return this.value == null ? "on" : this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public int doStartTag() throws JspException {
	StringBuffer results = new StringBuffer("<input type=\"checkbox\"");
	this.prepareAttribute(results, "name", this.prepareName());
	this.prepareAttribute(results, "accesskey", this.getAccesskey());
	this.prepareAttribute(results, "tabindex", this.getTabindex());
	this.prepareAttribute(results, "value", this.getValue());
	if (this.isChecked()) {
	    results.append(" checked=\"checked\"");
	}

	results.append(this.prepareEventHandlers());
	results.append(this.prepareStyles());
	this.prepareOtherAttributes(results);
	results.append(this.getElementClose());
	TagUtils.getInstance().write(this.pageContext, results.toString());
	this.text = null;
	return 2;
    }

    protected boolean isChecked() throws JspException {
	Object result = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, (String) null);
	if (result == null) {
	    result = "";
	}

	String result1 = result.toString();
	String checked = (String) result1;
	return checked.equalsIgnoreCase(this.value) || checked.equalsIgnoreCase("true")
		|| checked.equalsIgnoreCase("yes") || checked.equalsIgnoreCase("on");
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
	if (this.text != null) {
	    TagUtils.getInstance().write(this.pageContext, this.text);
	}

	return 6;
    }

    protected String prepareName() throws JspException {
	if (this.property == null) {
	    return null;
	} else if (this.indexed) {
	    StringBuffer results = new StringBuffer();
	    this.prepareIndex(results, this.name);
	    results.append(this.property);
	    return results.toString();
	} else {
	    return this.property;
	}
    }

    public void release() {
	super.release();
	this.name = "com.scplatform.common.web.taglib.html.BEAN";
	this.property = null;
	this.text = null;
	this.value = null;
    }
}
