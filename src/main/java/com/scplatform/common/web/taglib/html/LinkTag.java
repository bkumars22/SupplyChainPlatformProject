/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class LinkTag extends BaseHandlerTag {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String text = null;
    protected String anchor = null;
    protected String forward = null;
    protected String href = null;
    protected String linkName = null;
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
    protected String target = null;
    protected boolean transaction = false;
    protected Map parameters = new HashMap();
    protected String indexId = null;
    protected boolean useLocalEncoding = false;

    public LinkTag() {
	this.doDisabled = false;
    }

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

    public String getLinkName() {
	return this.linkName;
    }

    public void setLinkName(String linkName) {
	this.linkName = linkName;
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

    public String getTarget() {
	return this.target;
    }

    public void setTarget(String target) {
	this.target = target;
    }

    public boolean getTransaction() {
	return this.transaction;
    }

    public void setTransaction(boolean transaction) {
	this.transaction = transaction;
    }

    public String getIndexId() {
	return this.indexId;
    }

    public void setIndexId(String indexId) {
	this.indexId = indexId;
    }

    public boolean isUseLocalEncoding() {
	return this.useLocalEncoding;
    }

    public void setUseLocalEncoding(boolean b) {
	this.useLocalEncoding = b;
    }

    public int doStartTag() throws JspException {
	this.text = null;
	this.parameters.clear();
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
	StringBuffer results = new StringBuffer("<a");
	this.prepareAttribute(results, "name", this.getLinkName());
	if (this.getLinkName() == null || this.getForward() != null || this.getHref() != null || this.getPage() != null
		|| this.getAction() != null) {
	    this.prepareAttribute(results, "href", this.calculateURL());
	}

	this.prepareAttribute(results, "target", this.getTarget());
	this.prepareAttribute(results, "accesskey", this.getAccesskey());
	this.prepareAttribute(results, "tabindex", this.getTabindex());
	results.append(this.prepareStyles());
	results.append(this.prepareEventHandlers());
	this.prepareOtherAttributes(results);
	results.append(">");
	if (this.text != null) {
	    results.append(this.text);
	}

	results.append("</a>");
	TagUtils.getInstance().write(this.pageContext, results.toString());
	return 6;
    }

    public void release() {
	super.release();
	this.anchor = null;
	this.forward = null;
	this.href = null;
	this.linkName = null;
	this.name = null;
	this.page = null;
	this.action = null;
	this.module = null;
	this.paramId = null;
	this.paramName = null;
	this.paramProperty = null;
	this.paramScope = null;
	this.parameters = null;
	this.property = null;
	this.scope = null;
	this.target = null;
	this.text = null;
	this.transaction = false;
	this.indexId = null;
	this.useLocalEncoding = false;
    }

    protected String calculateURL() throws JspException {
	Object params = TagUtils.getInstance().computeParameters(this.pageContext, this.paramId, this.paramName,
		this.paramProperty, this.paramScope, this.name, this.property, this.scope, this.transaction);
	if (!this.parameters.isEmpty()) {
	    if (params == null) {
		params = new HashMap();
	    }

	    ((Map) params).putAll(this.parameters);
	}

	if (this.indexed) {
	    int url = this.getIndexValue();
	    if (params == null) {
		params = new HashMap();
	    }

	    if (this.indexId != null) {
		((Map) params).put(this.indexId, Integer.toString(url));
	    } else {
		((Map) params).put("index", Integer.toString(url));
	    }
	}

	String url1 = null;

	try {
	    url1 = TagUtils.getInstance().computeURLWithCharEncoding(this.pageContext, this.forward, this.href,
		    this.page, this.action, this.module, (Map) params, this.anchor, false, this.useLocalEncoding);
	    return url1;
	} catch (MalformedURLException arg3) {
	    TagUtils.getInstance().saveException(this.pageContext, arg3);
	    throw new JspException(messages.getMessage("rewrite.url", arg3.toString()));
	}
    }

    public void addParameter(String paramName, Object paramValue) {
	this.parameters.put(paramName, paramValue);
    }
}
