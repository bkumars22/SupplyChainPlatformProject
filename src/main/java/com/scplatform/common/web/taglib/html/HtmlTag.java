/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.util.Locale;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class HtmlTag extends TagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected boolean xhtml = false;
    protected boolean lang = false;

    public boolean getXhtml() {
	return this.xhtml;
    }

    public void setXhtml(boolean xhtml) {
	this.xhtml = xhtml;
    }

    public boolean getLang() {
	return this.lang;
    }

    public void setLang(boolean lang) {
	this.lang = lang;
    }

    public int doStartTag() throws JspException {
	TagUtils.getInstance().write(this.pageContext, this.renderHtmlStartElement());
	return 1;
    }

    protected String renderHtmlStartElement() {
	StringBuffer sb = new StringBuffer("<html");
	String language = null;
	String country = "";
	Locale currentLocale = TagUtils.getInstance().getUserLocale(this.pageContext,
		"com.test.controller.action.LOCALE");
	language = currentLocale.getLanguage();
	country = currentLocale.getCountry();
	boolean validLanguage = this.isValidRfc2616(language);
	boolean validCountry = this.isValidRfc2616(country);
	if (this.xhtml) {
	    this.pageContext.setAttribute("com.test.controller.globals.XHTML", "true", 1);
	    sb.append(" xmlns=\"http://www.w3.org/1999/xhtml\"");
	}

	if ((this.lang || this.xhtml) && validLanguage) {
	    sb.append(" lang=\"");
	    sb.append(language);
	    if (validCountry) {
		sb.append("-");
		sb.append(country);
	    }

	    sb.append("\"");
	}

	if (this.xhtml && validLanguage) {
	    sb.append(" xml:lang=\"");
	    sb.append(language);
	    if (validCountry) {
		sb.append("-");
		sb.append(country);
	    }

	    sb.append("\"");
	}

	sb.append(">");
	return sb.toString();
    }

    public int doEndTag() throws JspException {
	TagUtils.getInstance().write(this.pageContext, "</html>");
	return 6;
    }

    public void release() {
	this.xhtml = false;
	this.lang = false;
    }

    private boolean isValidRfc2616(String value) {
	if (value != null && value.length() != 0) {
	    for (int i = 0; i < value.length(); ++i) {
		char c = value.charAt(i);
		if (!Character.isLetter(c) && c != 45) {
		    return false;
		}
	    }

	    return true;
	} else {
	    return false;
	}
    }
}
