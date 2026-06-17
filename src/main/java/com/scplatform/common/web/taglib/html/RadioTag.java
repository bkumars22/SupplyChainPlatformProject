/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class RadioTag extends BaseHandlerTag {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String name = "com.scplatform.common.web.taglib.html.BEAN";
    protected String property = null;
    protected String text = null;
    protected String value = null;
    protected String idName = null;

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
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public String getIdName() {
	return this.idName;
    }

    public void setIdName(String idName) {
	this.idName = idName;
    }

    public int doStartTag() throws JspException {
	String radioTag = this.renderRadioElement(this.serverValue(), this.currentValue());
	TagUtils.getInstance().write(this.pageContext, radioTag);
	this.text = null;
	return 2;
    }

    private String serverValue() throws JspException {
	if (this.idName == null) {
	    return this.value;
	} else {
	    String serverValue = this.lookupProperty(this.idName, this.value);
	    return serverValue == null ? "" : serverValue;
	}
    }

    private String currentValue() throws JspException {
	String current = this.lookupProperty(this.name, this.property);
	return current == null ? "" : current;
    }

    protected String renderRadioElement(String serverValue, String checkedValue) throws JspException {
	StringBuffer results = new StringBuffer("<input type=\"radio\"");
	this.prepareAttribute(results, "name", this.prepareName());
	this.prepareAttribute(results, "accesskey", this.getAccesskey());
	this.prepareAttribute(results, "tabindex", this.getTabindex());
	this.prepareAttribute(results, "value", TagUtils.getInstance().filter(serverValue));
	if (serverValue.equals(checkedValue)) {
	    results.append(" checked=\"checked\"");
	}

	results.append(this.prepareEventHandlers());
	results.append(this.prepareStyles());
	this.prepareOtherAttributes(results);
	results.append(this.getElementClose());
	return results.toString();
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
	this.idName = null;
	this.name = "com.scplatform.common.web.taglib.html.BEAN";
	this.property = null;
	this.text = null;
	this.value = null;
    }
}
