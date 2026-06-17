/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.util.List;
import java.util.Iterator;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class MessagesTag extends BodyTagSupport {
    protected static MessageResources messageResources = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected Iterator iterator = null;
    protected boolean processed = false;
    protected String id = null;
    protected String bundle = null;
    protected String locale = "com.scplatform.pcm.LOCALE";
    protected String name = "com.scplatform.pcm.ERROR";
    protected String property = null;
    protected String header = null;
    protected String footer = null;
    protected String message = null;

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

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
	return this.header;
    }

    public void setHeader(String header) {
	this.header = header;
    }

    public String getFooter() {
	return this.footer;
    }

    public void setFooter(String footer) {
	this.footer = footer;
    }

    public String getMessage() {
	return this.message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public int doStartTag() throws JspException {
	this.processed = false;
	UiMessages messages = null;
	String name = this.name;
	if (this.message != null && "true".equalsIgnoreCase(this.message)) {
	    name = "com.scplatform.pcm.SUCCESS_MESSAGE";
	}

	try {
	    messages = TagUtils.getInstance().getMessages(this.pageContext, name);
	} catch (JspException arg3) {
	    TagUtils.getInstance().saveException(this.pageContext, arg3);
	    throw arg3;
	}

	List<String> reportMessages = this.property == null ? messages.getMessages() : messages.getMessages(this.property);
	this.iterator = reportMessages.iterator();
	if (!this.iterator.hasNext()) {
	    return 0;
	} else {
	    boolean isListOfMessages = reportMessages.size() > 1;
	    String messageHeader = "";

	    if (isListOfMessages) {
		messageHeader = (String) this.iterator.next();
	    }

	    if (this.iterator.hasNext()) {
		this.processMessage((String) this.iterator.next());
	    }

	    if (this.header != null && this.header.length() > 0) {
		String headerMessage = TagUtils.getInstance().message(this.pageContext, this.bundle, this.locale,
			this.header);
		if (headerMessage != null) {
		    TagUtils.getInstance().write(this.pageContext, headerMessage);
		}
	    }
	    this.processed = true;
	    if (this.message != null && "true".equalsIgnoreCase(this.message)) {
		TagUtils.getInstance().write(pageContext,
			"<div class='eto-messageblock' data-message-type='success' id='success-message-block'>");
		TagUtils.getInstance().write(pageContext, "<div class='eto-messageblock__body'>");
		if (isListOfMessages) {
		    TagUtils.getInstance().write(pageContext,
			    "<b class = 'eto-messageblock__title'>" + messageHeader + "</b>");
		    TagUtils.getInstance().write(pageContext, "<ul>");
		} else {
		    TagUtils.getInstance().write(pageContext, "<ul style='list-style:none;'>");
		}
	    }
	    return 2;
	}
    }

    public int doAfterBody() throws JspException {
	if (this.bodyContent != null) {
	    TagUtils.getInstance().writePrevious(this.pageContext, this.bodyContent.getString());
	    this.bodyContent.clearBody();
	}

	if (this.iterator.hasNext()) {
	    this.processMessage((String) this.iterator.next());
	    return 2;
	} else {
	    return 0;
	}
    }

    private void processMessage(String messageText) throws JspException {
	if (messageText == null) {
	    this.pageContext.removeAttribute(this.id);
	} else {
	    this.pageContext.setAttribute(this.id, messageText);
	}

    }

    public int doEndTag() throws JspException {
	if (this.processed && this.footer != null && this.footer.length() > 0) {
	    String footerMessage = TagUtils.getInstance().message(this.pageContext, this.bundle, this.locale,
		    this.footer);
	    if (footerMessage != null) {
		TagUtils.getInstance().write(this.pageContext, footerMessage);
	    }
	}
	if (this.message != null && "true".equalsIgnoreCase(this.message)) {
	    TagUtils.getInstance().write(pageContext, "</ul>");
	    TagUtils.getInstance().write(pageContext, "</div>");
	    TagUtils.getInstance().write(pageContext,
		    "<a href='javascript:void(0)' role='button' class='eto-messageblock__close'></a>");
	    TagUtils.getInstance().write(pageContext, "<script>");
	    TagUtils.getInstance().write(pageContext,
		    "if(parent.parent.mcmApp.toast !='undefined') { parent.parent.mcmApp.toast.addToast(new eto.MessageBlock({ el: document.querySelector('#success-message-block') }),'success',100000);}"
		    + "else { parent.mcmApp.toast.addToast(new eto.MessageBlock({ el: document.querySelector('#success-message-block') }),'success',100000);}");
	    TagUtils.getInstance().write(pageContext, "</script>");
	    TagUtils.getInstance().write(pageContext, "</div>");
	}
	if (this.processed && this.footer != null && this.footer.length() > 0) {
	    TagUtils.getInstance().write(pageContext, "</div>");
	}
	return 6;
    }

    public void release() {
	super.release();
	this.iterator = null;
	this.processed = false;
	this.id = null;
	this.bundle = null;
	this.locale = "com.scplatform.pcm.LOCALE";
	this.name = "com.scplatform.pcm.ERROR";
	this.property = null;
	this.header = null;
	this.footer = null;
	this.message = null;
    }
}
