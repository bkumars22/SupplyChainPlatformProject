/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class PageTag extends TagSupport {
    protected static MessageResources messages = MessageResources.getMessageResources("com.scplatform.common.web.taglib.bean.LocalStrings");
    protected String id = null;
    protected String property = null;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int doStartTag() throws JspException {
        Object object = null;
        if ("application".equalsIgnoreCase(this.property)) {
            object = this.pageContext.getServletContext();
        } else if ("config".equalsIgnoreCase(this.property)) {
            object = this.pageContext.getServletConfig();
        } else if ("request".equalsIgnoreCase(this.property)) {
            object = this.pageContext.getRequest();
        } else if ("response".equalsIgnoreCase(this.property)) {
            object = this.pageContext.getResponse();
        } else {
            if (!"session".equalsIgnoreCase(this.property)) {
                JspException e = new JspException(messages.getMessage("page.selector", this.property));
                TagUtils.getInstance().saveException(this.pageContext, e);
                throw e;
            }
            object = this.pageContext.getSession();
        }
        this.pageContext.setAttribute(this.id, object);
        return 0;
    }

    public void release() {
        super.release();
        this.id = null;
        this.property = null;
    }
}