/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;

public abstract class BaseFieldTag extends BaseInputTag {
    protected String accept = null;
    protected boolean redisplay = true;
    protected String type = null;

    public String getAccept() {
	return this.accept;
    }

    public void setAccept(String accept) {
	this.accept = accept;
    }

    public boolean getRedisplay() {
	return this.redisplay;
    }

    public void setRedisplay(boolean redisplay) {
	this.redisplay = redisplay;
    }

    public int doStartTag() throws JspException {
	TagUtils.getInstance().write(this.pageContext, this.renderInputElement());
	return 2;
    }

    protected String renderInputElement() throws JspException {
	StringBuffer results = new StringBuffer("<input");
	this.prepareAttribute(results, "type", this.type);
	this.prepareAttribute(results, "name", this.prepareName());
	this.prepareAttribute(results, "accesskey", this.getAccesskey());
	this.prepareAttribute(results, "accept", this.getAccept());
	this.prepareAttribute(results, "maxlength", this.getMaxlength());
	this.prepareAttribute(results, "size", this.getCols());
	this.prepareAttribute(results, "tabindex", this.getTabindex());
	this.prepareValue(results);
	results.append(this.prepareEventHandlers());
	results.append(this.prepareStyles());
	this.prepareOtherAttributes(results);
	results.append(this.getElementClose());
	return results.toString();
    }

    protected void prepareValue(StringBuffer results) throws JspException {
	results.append(" value=\"");
	if (this.value != null) {
	    results.append(this.formatValue(this.value));
	} else if (this.redisplay || !"password".equals(this.type)) {
	    Object value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, (String) null);
	    results.append(this.formatValue(value));
	}

	results.append('\"');
    }

    protected String formatValue(Object value) throws JspException {
	return value == null ? "" : TagUtils.getInstance().filter(value.toString());
    }

    public void release() {
	super.release();
	this.accept = null;
	this.name = "com.scplatform.common.web.taglib.html.BEAN";
	this.redisplay = true;
    }
}