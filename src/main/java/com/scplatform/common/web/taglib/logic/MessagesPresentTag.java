/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import java.util.Iterator;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.common.web.taglib.TagUtils;

public class MessagesPresentTag extends ConditionalTagBase {
    protected String message = null;

    public MessagesPresentTag() {
	this.name = "com.scplatform.pcm.ERROR";
    }

    public String getMessage() {
	return this.message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    protected boolean condition() throws JspException {
	return this.condition(true);
    }

    protected boolean condition(boolean desired) throws JspException {
	UiMessages messages = null;
	String key = this.name;
	if (this.message != null && "true".equalsIgnoreCase(this.message)) {
	    key = "com.scplatform.pcm.SUCCESS_MESSAGE";
	}

	try {
	    messages = TagUtils.getInstance().getMessages(this.pageContext, key);
	} catch (JspException arg4) {
	    TagUtils.getInstance().saveException(this.pageContext, arg4);
	    throw arg4;
	}

	Iterator iterator = this.property == null ? messages.get() : messages.get(this.property);
	return iterator.hasNext() == desired;
    }

    public void release() {
	super.release();
	this.name = "com.scplatform.pcm.ERROR";
	this.message = null;
    }
}