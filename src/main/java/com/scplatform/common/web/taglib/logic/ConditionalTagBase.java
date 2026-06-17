/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.scplatform.common.web.taglib.MessageResources;

public abstract class ConditionalTagBase extends TagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.logic.LocalStrings");
    protected String cookie = null;
    protected String header = null;
    protected String name = null;
    protected String parameter = null;
    protected String property = null;
    protected String role = null;
    protected String scope = null;
    protected String user = null;

    public String getCookie() {
	return this.cookie;
    }

    public void setCookie(String cookie) {
	this.cookie = cookie;
    }

    public String getHeader() {
	return this.header;
    }

    public void setHeader(String header) {
	this.header = header;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getParameter() {
	return this.parameter;
    }

    public void setParameter(String parameter) {
	this.parameter = parameter;
    }

    public String getProperty() {
	return this.property;
    }

    public void setProperty(String property) {
	this.property = property;
    }

    public String getRole() {
	return this.role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public String getScope() {
	return this.scope;
    }

    public void setScope(String scope) {
	this.scope = scope;
    }

    public String getUser() {
	return this.user;
    }

    public void setUser(String user) {
	this.user = user;
    }

    public int doStartTag() throws JspException {
	return this.condition() ? 1 : 0;
    }

    public int doEndTag() throws JspException {
	return 6;
    }

    public void release() {
	super.release();
	this.cookie = null;
	this.header = null;
	this.name = null;
	this.parameter = null;
	this.property = null;
	this.role = null;
	this.scope = null;
	this.user = null;
    }

    protected abstract boolean condition() throws JspException;
}
