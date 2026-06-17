/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class OptionTag extends BodyTagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String text = null;
    protected String bundle = "com.test.controller.action.MESSAGE";
    protected boolean disabled = false;
    protected boolean filter = false;
    protected String key = null;
    protected String locale = "com.test.controller.action.LOCALE";
    private String style = null;
    private String styleClass = null;
    protected String styleId = null;
    private String lang = null;
    private String dir = null;
    protected String value = null;

    public String getBundle() {
	return this.bundle;
    }

    public void setBundle(String bundle) {
	this.bundle = bundle;
    }

    public boolean getDisabled() {
	return this.disabled;
    }

    public void setDisabled(boolean disabled) {
	this.disabled = disabled;
    }

    public boolean getFilter() {
	return this.filter;
    }

    public void setFilter(boolean filter) {
	this.filter = filter;
    }

    public String getKey() {
	return this.key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getLocale() {
	return this.locale;
    }

    public void setLocale(String locale) {
	this.locale = locale;
    }

    public String getStyle() {
	return this.style;
    }

    public void setStyle(String style) {
	this.style = style;
    }

    public String getStyleClass() {
	return this.styleClass;
    }

    public void setStyleClass(String styleClass) {
	this.styleClass = styleClass;
    }

    public String getStyleId() {
	return this.styleId;
    }

    public void setStyleId(String styleId) {
	this.styleId = styleId;
    }

    public String getValue() {
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public String getLang() {
	return this.lang;
    }

    public void setLang(String lang) {
	this.lang = lang;
    }

    public String getDir() {
	return this.dir;
    }

    public void setDir(String dir) {
	this.dir = dir;
    }

    public int doStartTag() throws JspException {
	this.text = null;
	return 2;
    }

    public int doAfterBody() throws JspException {
	if (this.bodyContent != null) {
	    String text = this.bodyContent.getString();
	    if (text != null) {
		text = text.trim();
		if (text.length() > 0) {
		    this.text = text;
		}
	    }
	}

	return 0;
    }

    public int doEndTag() throws JspException {
	TagUtils.getInstance().write(this.pageContext, this.renderOptionElement());
	return 6;
    }

    protected String renderOptionElement() throws JspException {
	StringBuffer results = new StringBuffer("<option value=\"");
	if (this.filter) {
	    results.append(TagUtils.getInstance().filter(this.value));
	} else {
	    results.append(this.value);
	}

	results.append("\"");
	if (this.disabled) {
	    results.append(" disabled=\"disabled\"");
	}

	if (this.selectTag().isMatched(this.value)) {
	    results.append(" selected=\"selected\"");
	}

	if (this.style != null) {
	    results.append(" style=\"");
	    results.append(this.style);
	    results.append("\"");
	}

	if (this.styleId != null) {
	    results.append(" id=\"");
	    results.append(this.styleId);
	    results.append("\"");
	}

	if (this.styleClass != null) {
	    results.append(" class=\"");
	    results.append(this.styleClass);
	    results.append("\"");
	}

	if (this.dir != null) {
	    results.append(" dir=\"");
	    results.append(this.dir);
	    results.append("\"");
	}

	if (this.lang != null) {
	    results.append(" lang=\"");
	    results.append(this.lang);
	    results.append("\"");
	}

	results.append(">");
	results.append(this.text());
	results.append("</option>");
	return results.toString();
    }

    private SelectTag selectTag() throws JspException {
	SelectTag selectTag = (SelectTag) this.pageContext.getAttribute("com.scplatform.common.web.taglib.html.SELECT");
	if (selectTag == null) {
	    JspException e = new JspException(messages.getMessage("optionTag.select"));
	    TagUtils.getInstance().saveException(this.pageContext, e);
	    throw e;
	} else {
	    return selectTag;
	}
    }

    public void release() {
	super.release();
	this.bundle = "com.test.controller.action.MESSAGE";
	this.dir = null;
	this.disabled = false;
	this.key = null;
	this.lang = null;
	this.locale = "com.test.controller.action.LOCALE";
	this.style = null;
	this.styleClass = null;
	this.text = null;
	this.value = null;
    }

    protected String text() throws JspException {
	String optionText = this.text;
	if (optionText == null && this.key != null) {
	    optionText = TagUtils.getInstance().message(this.pageContext, this.bundle, this.locale, this.key);
	}

	if (optionText == null) {
	    optionText = this.value;
	}

	return optionText;
    }
}
