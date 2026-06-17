/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class RedirectTag extends TagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.logic.LocalStrings");
    protected String anchor = null;
    protected String forward = null;
    protected String href = null;
    protected String name = null;
    protected String page = null;
    protected String action = null;
    protected String module = null;
    protected String paramId = null;
    protected String paramName = null;
    protected String paramProperty = null;
    protected String paramScope = null;
    protected String property = null;
    protected String scope = null;
    protected boolean transaction = false;
    protected boolean useLocalEncoding = false;

    public String getAnchor() {
	return this.anchor;
    }

    public void setAnchor(String anchor) {
	this.anchor = anchor;
    }

    public String getForward() {
	return this.forward;
    }

    public void setForward(String forward) {
	this.forward = forward;
    }

    public String getHref() {
	return this.href;
    }

    public void setHref(String href) {
	this.href = href;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPage() {
	return this.page;
    }

    public void setPage(String page) {
	this.page = page;
    }

    public String getAction() {
	return this.action;
    }

    public void setAction(String action) {
	this.action = action;
    }

    public String getModule() {
	return this.module;
    }

    public void setModule(String module) {
	this.module = module;
    }

    public String getParamId() {
	return this.paramId;
    }

    public void setParamId(String paramId) {
	this.paramId = paramId;
    }

    public String getParamName() {
	return this.paramName;
    }

    public void setParamName(String paramName) {
	this.paramName = paramName;
    }

    public String getParamProperty() {
	return this.paramProperty;
    }

    public void setParamProperty(String paramProperty) {
	this.paramProperty = paramProperty;
    }

    public String getParamScope() {
	return this.paramScope;
    }

    public void setParamScope(String paramScope) {
	this.paramScope = paramScope;
    }

    public String getProperty() {
	return this.property;
    }

    public void setProperty(String property) {
	this.property = property;
    }

    public String getScope() {
	return this.scope;
    }

    public void setScope(String scope) {
	this.scope = scope;
    }

    public boolean getTransaction() {
	return this.transaction;
    }

    public void setTransaction(boolean transaction) {
	this.transaction = transaction;
    }

    public boolean isUseLocalEncoding() {
	return this.useLocalEncoding;
    }

    public void setUseLocalEncoding(boolean b) {
	this.useLocalEncoding = b;
    }

    public int doStartTag() throws JspException {
	return 0;
    }

    public int doEndTag() throws JspException {
	this.doRedirect(this.generateRedirectURL());
	return 5;
    }

    protected String generateRedirectURL() throws JspException {
	Map params = TagUtils.getInstance().computeParameters(this.pageContext, this.paramId, this.paramName,
		this.paramProperty, this.paramScope, this.name, this.property, this.scope, this.transaction);
	String url = null;

	try {
	    url = TagUtils.getInstance().computeURLWithCharEncoding(this.pageContext, this.forward, this.href,
		    this.page, this.action, this.module, params, this.anchor, true, this.useLocalEncoding);
	    return url;
	} catch (MalformedURLException arg3) {
	    TagUtils.getInstance().saveException(this.pageContext, arg3);
	    throw new JspException(messages.getMessage("redirect.url", arg3.toString()));
	}
    }

    protected void doRedirect(String url) throws JspException {
	HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();

	try {
	    response.sendRedirect(url);
	} catch (IOException arg3) {
	    TagUtils.getInstance().saveException(this.pageContext, arg3);
	    throw new JspException(arg3.getMessage());
	}
    }

    public void release() {
	super.release();
	this.anchor = null;
	this.forward = null;
	this.href = null;
	this.name = null;
	this.page = null;
	this.action = null;
	this.paramId = null;
	this.paramName = null;
	this.paramProperty = null;
	this.paramScope = null;
	this.property = null;
	this.scope = null;
    }
}
