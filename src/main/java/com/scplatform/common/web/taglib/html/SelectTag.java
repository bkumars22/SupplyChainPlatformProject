/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.lang.reflect.InvocationTargetException;

import jakarta.servlet.jsp.JspException;

import org.apache.commons.beanutils.BeanUtils;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class SelectTag extends BaseHandlerTag {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String[] match = null;
    protected String multiple = null;
    protected String name = "com.scplatform.common.web.taglib.html.BEAN";
    protected String property = null;
    protected String saveBody = null;
    protected String size = null;
    protected String value = null;

    public String getMultiple() {
	return this.multiple;
    }

    public void setMultiple(String multiple) {
	this.multiple = multiple;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getSize() {
	return this.size;
    }

    public void setSize(String size) {
	this.size = size;
    }

    public boolean isMatched(String value) {
	if (this.match != null && value != null) {
	    for (int i = 0; i < this.match.length; ++i) {
		if (value.equals(this.match[i])) {
		    return true;
		}
	    }

	    return false;
	} else {
	    return false;
	}
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

    public int doStartTag() throws JspException {
	TagUtils.getInstance().write(this.pageContext, this.renderSelectStartElement());
	this.pageContext.setAttribute("com.scplatform.common.web.taglib.html.SELECT", this);
	this.calculateMatchValues();
	return 2;
    }

    protected String renderSelectStartElement() throws JspException {
	StringBuffer results = new StringBuffer("<select");
	this.prepareAttribute(results, "name", this.prepareName());
	this.prepareAttribute(results, "accesskey", this.getAccesskey());
	if (this.multiple != null) {
	    results.append(" multiple=\"multiple\"");
	}

	this.prepareAttribute(results, "size", this.getSize());
	this.prepareAttribute(results, "tabindex", this.getTabindex());
	results.append(this.prepareEventHandlers());
	results.append(this.prepareStyles());
	this.prepareOtherAttributes(results);
	results.append(">");
	return results.toString();
    }

    private void calculateMatchValues() throws JspException {
	if (this.value != null) {
	    this.match = new String[1];
	    this.match[0] = this.value;
	} else {
	    Object bean = TagUtils.getInstance().lookup(this.pageContext, this.name, (String) null);
	    if (bean == null) {
		JspException e = new JspException(messages.getMessage("getter.bean", this.name));
		TagUtils.getInstance().saveException(this.pageContext, e);
		throw e;
	    }

	    try {
		this.match = BeanUtils.getArrayProperty(bean, this.property);
		if (this.match == null) {
		    this.match = new String[0];
		}
	    } catch (IllegalAccessException arg3) {
		TagUtils.getInstance().saveException(this.pageContext, arg3);
		throw new JspException(messages.getMessage("getter.access", this.property, this.name));
	    } catch (InvocationTargetException arg4) {
		Throwable t = arg4.getTargetException();
		TagUtils.getInstance().saveException(this.pageContext, t);
		throw new JspException(messages.getMessage("getter.result", this.property, t.toString()));
	    } catch (NoSuchMethodException arg5) {
		TagUtils.getInstance().saveException(this.pageContext, arg5);
		throw new JspException(messages.getMessage("getter.method", this.property, this.name));
	    }
	}

    }

    public int doAfterBody() throws JspException {
	if (this.bodyContent != null) {
	    String value = this.bodyContent.getString();
	    if (value == null) {
		value = "";
	    }

	    this.saveBody = value.trim();
	}

	return 0;
    }

    public int doEndTag() throws JspException {
	this.pageContext.removeAttribute("com.scplatform.common.web.taglib.html.SELECT");
	StringBuffer results = new StringBuffer();
	if (this.saveBody != null) {
	    results.append(this.saveBody);
	    this.saveBody = null;
	}

	results.append("</select>");
	TagUtils.getInstance().write(this.pageContext, results.toString());
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
	this.match = null;
	this.multiple = null;
	this.name = "com.scplatform.common.web.taglib.html.BEAN";
	this.property = null;
	this.saveBody = null;
	this.size = null;
	this.value = null;
    }
}
