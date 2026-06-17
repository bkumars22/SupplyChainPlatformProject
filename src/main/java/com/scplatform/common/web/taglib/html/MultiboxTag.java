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

public class MultiboxTag extends BaseHandlerTag {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String constant = null;
    protected String name = "com.scplatform.common.web.taglib.html.BEAN";
    protected String property = null;
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
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public int doStartTag() throws JspException {
	this.constant = null;
	return 2;
    }

    public int doAfterBody() throws JspException {
	if (this.bodyContent != null) {
	    this.constant = this.bodyContent.getString().trim();
	}

	if ("".equals(this.constant)) {
	    this.constant = null;
	}

	return 0;
    }

    public int doEndTag() throws JspException {
	StringBuffer results = new StringBuffer("<input type=\"checkbox\"");
	this.prepareAttribute(results, "name", this.prepareName());
	this.prepareAttribute(results, "accesskey", this.getAccesskey());
	this.prepareAttribute(results, "tabindex", this.getTabindex());
	String value = this.prepareValue(results);
	this.prepareChecked(results, value);
	results.append(this.prepareEventHandlers());
	results.append(this.prepareStyles());
	this.prepareOtherAttributes(results);
	results.append(this.getElementClose());
	TagUtils.getInstance().write(this.pageContext, results.toString());
	return 6;
    }

    protected String prepareName() throws JspException {
	return this.property;
    }

    protected String prepareValue(StringBuffer results) throws JspException {
	String value = this.value == null ? this.constant : this.value;
	if (value == null) {
	    JspException e = new JspException(messages.getMessage("multiboxTag.value"));
	    this.pageContext.setAttribute("com.test.controller.action.EXCEPTION", e, 2);
	    throw e;
	} else {
	    this.prepareAttribute(results, "value", TagUtils.getInstance().filter(value));
	    return value;
	}
    }

    protected void prepareChecked(StringBuffer results, String value) throws JspException {
	Object bean = TagUtils.getInstance().lookup(this.pageContext, this.name, (String) null);
	String[] values = null;
	if (bean == null) {
	    throw new JspException(messages.getMessage("getter.bean", this.name));
	} else {
	    try {
		values = BeanUtils.getArrayProperty(bean, this.property);
		if (values == null) {
		    values = new String[0];
		}
	    } catch (IllegalAccessException arg6) {
		throw new JspException(messages.getMessage("getter.access", this.property, this.name));
	    } catch (InvocationTargetException arg7) {
		Throwable t = arg7.getTargetException();
		throw new JspException(messages.getMessage("getter.result", this.property, t.toString()));
	    } catch (NoSuchMethodException arg8) {
		throw new JspException(messages.getMessage("getter.method", this.property, this.name));
	    }

	    for (int i = 0; i < values.length; ++i) {
		if (value.equals(values[i])) {
		    results.append(" checked=\"checked\"");
		    break;
		}
	    }

	}
    }

    public void release() {
	super.release();
	this.constant = null;
	this.name = "com.scplatform.common.web.taglib.html.BEAN";
	this.property = null;
	this.value = null;
    }
}
