/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.pcm.util.message.SCPlatformMessages;

public class MessageTag extends TagSupport {
    protected static final String DEFAULT_BUNDLE = "com.test.controller.action.MESSAGE";
    protected static final String DEFAULT_LOCALE_KEY = "com.scplatform.pcm.LOCALE";
    protected String arg0 = null;
    protected String arg1 = null;
    protected String arg2 = null;
    protected String arg3 = null;
    protected String arg4 = null;
    protected String bundle = null;
    protected String key = null;
    protected String name = null;
    protected String property = null;
    protected String scope = null;
    protected String localeKey = "com.scplatform.pcm.LOCALE";

    public String getArg0() {
	return this.arg0;
    }

    public void setArg0(String arg0) {
	this.arg0 = arg0;
    }

    public String getArg1() {
	return this.arg1;
    }

    public void setArg1(String arg1) {
	this.arg1 = arg1;
    }

    public String getArg2() {
	return this.arg2;
    }

    public void setArg2(String arg2) {
	this.arg2 = arg2;
    }

    public String getArg3() {
	return this.arg3;
    }

    public void setArg3(String arg3) {
	this.arg3 = arg3;
    }

    public String getArg4() {
	return this.arg4;
    }

    public void setArg4(String arg4) {
	this.arg4 = arg4;
    }

    public String getBundle() {
	return this.bundle;
    }

    public void setBundle(String bundle) {
	this.bundle = bundle;
    }

    public String getKey() {
	return this.key;
    }

    public void setKey(String key) {
	this.key = key;
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

    public String getScope() {
	return this.scope;
    }

    public void setScope(String scope) {
	this.scope = scope;
    }

    public String getLocale() {
	return this.localeKey;
    }

    public void setLocale(String localeKey) {
	this.localeKey = localeKey;
    }

    public int doStartTag() throws JspException {
    String resolvedKey = this.key;
    if (resolvedKey == null) {
        Object lookupValue = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, this.scope);
        if (lookupValue != null && !(lookupValue instanceof String)) {
        JspException exception = new JspException(
            "bean:message expected a String key but found " + lookupValue.getClass().getName());
        TagUtils.getInstance().saveException(this.pageContext, exception);
        throw exception;
        }

        resolvedKey = (String) lookupValue;
    }

    Object[] args = new Object[] { this.arg0, this.arg1, this.arg2, this.arg3, this.arg4 };
    Locale locale = resolveLocale();
    String message = resolveMessage(resolvedKey, args, locale);
    if (message == null || isMissingMessage(message, resolvedKey)) {
        String localeValue = locale == null ? "default locale" : locale.toString();
        String bundleName = this.bundle == null ? DEFAULT_BUNDLE : this.bundle;
        JspException exception = new JspException(
            "No message found for key '" + resolvedKey + "' in bundle '" + bundleName + "' and locale "
                + localeValue);
        TagUtils.getInstance().saveException(this.pageContext, exception);
        throw exception;
    }

    TagUtils.getInstance().write(this.pageContext, message);
    return 0;
    }

    private Locale resolveLocale() {
    Object localeAttribute = null;
    if (this.localeKey != null) {
        localeAttribute = this.pageContext.findAttribute(this.localeKey);
    }

    if (localeAttribute instanceof Locale) {
        return (Locale) localeAttribute;
    }

    if (localeAttribute instanceof String localeString && !localeString.isBlank()) {
        return Locale.forLanguageTag(localeString.replace('_', '-'));
    }

    HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
    Locale requestLocale = request.getLocale();
    return requestLocale != null ? requestLocale : Locale.getDefault();
    }

    private String resolveMessage(String messageKey, Object[] args, Locale locale) {
    if (messageKey == null || messageKey.isBlank()) {
        return null;
    }

    if (isAuditBundle()) {
        return SCPlatformMessages.INSTANCE.getAuditMessage(messageKey, args, locale);
    }

    return SCPlatformMessages.INSTANCE.getMessage(messageKey, args, locale);
    }

    private boolean isAuditBundle() {
    if (this.bundle == null || this.bundle.isBlank()) {
        return false;
    }

    String normalizedBundle = this.bundle.toLowerCase(Locale.ROOT);
    return normalizedBundle.contains("audit");
    }

    private boolean isMissingMessage(String message, String messageKey) {
    return message.equals("???" + messageKey + "???");
    }

    public void release() {
	super.release();
	this.arg0 = null;
	this.arg1 = null;
	this.arg2 = null;
	this.arg3 = null;
	this.arg4 = null;
    this.bundle = DEFAULT_BUNDLE;
	this.key = null;
	this.name = null;
	this.property = null;
	this.scope = null;
    this.localeKey = DEFAULT_LOCALE_KEY;
    }
}