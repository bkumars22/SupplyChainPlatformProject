/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.util.Iterator;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class ErrorsTag extends TagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String bundle = null;
    protected String locale = "com.test.controller.action.LOCALE";
    protected String name = "com.test.controller.action.ERROR";
    protected String property = null;
    protected String header = null;
    protected String footer = null;
    protected String prefix = null;
    protected String suffix = null;

    public String getBundle() {
	return this.bundle;
    }

    public void setBundle(String bundle) {
	this.bundle = bundle;
    }

    public String getLocale() {
	return this.locale;
    }

    public void setLocale(String locale) {
	this.locale = locale;
    }

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

    public String getHeader() {
	return this.header == null ? "errors.header" : this.header;
    }

    public void setHeader(String header) {
	this.header = header;
    }

    public String getFooter() {
	return this.footer == null ? "errors.footer" : this.footer;
    }

    public void setFooter(String footer) {
	this.footer = footer;
    }

    public String getPrefix() {
	return this.prefix == null ? "errors.prefix" : this.prefix;
    }

    public void setPrefix(String prefix) {
	this.prefix = prefix;
    }

    public String getSuffix() {
	return this.suffix == null ? "errors.suffix" : this.suffix;
    }

    public void setSuffix(String suffix) {
	this.suffix = suffix;
    }

    public int doStartTag() throws JspException {
	UiMessages errors = null;

	try {
	    errors = TagUtils.getInstance().getMessages(this.pageContext, this.name);
	} catch (JspException arg10) {
	    TagUtils.getInstance().saveException(this.pageContext, arg10);
	    throw arg10;
	}

	if (errors != null && !errors.isEmpty()) {
	    boolean headerPresent = TagUtils.getInstance().present(this.pageContext, this.bundle, this.locale,
		    this.getHeader());
	    boolean footerPresent = TagUtils.getInstance().present(this.pageContext, this.bundle, this.locale,
		    this.getFooter());
	    boolean prefixPresent = TagUtils.getInstance().present(this.pageContext, this.bundle, this.locale,
		    this.getPrefix());
	    boolean suffixPresent = TagUtils.getInstance().present(this.pageContext, this.bundle, this.locale,
		    this.getSuffix());
	    StringBuffer results = new StringBuffer();
	    boolean headerDone = false;
	    String message = null;
	    Iterator reports = this.property == null ? errors.get() : errors.get(this.property);

	    while (reports.hasNext()) {
		String report = (String) reports.next();
		if (!headerDone) {
		    if (headerPresent) {
			message = TagUtils.getInstance().message(this.pageContext, this.bundle, this.locale,
				this.getHeader());
			results.append(message);
		    }

		    headerDone = true;
		}

		if (prefixPresent) {
		    message = TagUtils.getInstance().message(this.pageContext, this.bundle, this.locale,
			    this.getPrefix());
		    results.append(message);
		}

		message = report;

		if (message != null) {
		    results.append(message);
		}

		if (suffixPresent) {
		    message = TagUtils.getInstance().message(this.pageContext, this.bundle, this.locale,
			    this.getSuffix());
		    results.append(message);
		}
	    }

	    if (headerDone && footerPresent) {
		message = TagUtils.getInstance().message(this.pageContext, this.bundle, this.locale, this.getFooter());
		results.append(message);
	    }

	    TagUtils.getInstance().write(this.pageContext, results.toString());
	    return 1;
	} else {
	    return 1;
	}
    }

    public void release() {
	super.release();
	this.bundle = "com.test.controller.action.MESSAGE";
	this.locale = "com.test.controller.action.LOCALE";
	this.name = "com.test.controller.action.ERROR";
	this.property = null;
	this.header = null;
	this.footer = null;
	this.prefix = null;
	this.suffix = null;
    }
}
