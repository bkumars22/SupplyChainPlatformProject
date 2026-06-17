/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.MessageResources;

public abstract class BaseInputTag extends BaseHandlerTag {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String cols = null;
    protected String maxlength = null;
    protected String property = null;
    protected String rows = null;
    protected String value = null;
    protected String name = "com.scplatform.common.web.taglib.html.BEAN";

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getCols() {
	return this.cols;
    }

    public void setCols(String cols) {
	this.cols = cols;
    }

    public String getMaxlength() {
	return this.maxlength;
    }

    public void setMaxlength(String maxlength) {
	this.maxlength = maxlength;
    }

    public String getProperty() {
	return this.property;
    }

    public void setProperty(String property) {
	this.property = property;
    }

    public String getRows() {
	return this.rows;
    }

    public void setRows(String rows) {
	this.rows = rows;
    }

    public String getSize() {
	return this.getCols();
    }

    public void setSize(String size) {
	this.setCols(size);
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

    public int doEndTag() throws JspException {
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
	this.cols = null;
	this.maxlength = null;
	this.property = null;
	this.rows = null;
	this.value = null;
    }
}
