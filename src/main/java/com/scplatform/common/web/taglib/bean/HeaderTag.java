/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import java.util.ArrayList;
import java.util.Enumeration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class HeaderTag extends TagSupport {
    protected static MessageResources messages = MessageResources.getMessageResources("com.scplatform.common.web.taglib.bean.LocalStrings");
    protected String id = null;
    protected String multiple = null;
    protected String name = null;
    protected String value = null;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int doStartTag() throws JspException {
        if (this.multiple == null) {
            this.handleSingleHeader();
        } else {
            this.handleMultipleHeaders();
        }
        return 0;
    }

    protected void handleMultipleHeaders() throws JspException {
        ArrayList values = new ArrayList();
        Enumeration items = ((HttpServletRequest) this.pageContext.getRequest()).getHeaders(this.name);
        while (items.hasMoreElements()) {
            values.add(items.nextElement());
        }
        if (values.isEmpty() && this.value != null) {
            values.add(this.value);
        }
        String[] headers = new String[values.size()];
        if (headers.length == 0) {
            JspException e = new JspException(messages.getMessage("header.get", this.name));
            TagUtils.getInstance().saveException(this.pageContext, e);
            throw e;
        } else {
            this.pageContext.setAttribute(this.id, values.toArray(headers));
        }
    }

    protected void handleSingleHeader() throws JspException {
        String value = ((HttpServletRequest) this.pageContext.getRequest()).getHeader(this.name);
        if (value == null && this.value != null) {
            value = this.value;
        }
        if (value == null) {
            JspException e = new JspException(messages.getMessage("header.get", this.name));
            TagUtils.getInstance().saveException(this.pageContext, e);
            throw e;
        } else {
            this.pageContext.setAttribute(this.id, value);
        }
    }

    public void release() {
        super.release();
        this.id = null;
        this.multiple = null;
        this.name = null;
        this.value = null;
    }
}