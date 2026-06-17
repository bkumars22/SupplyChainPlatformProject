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

public class ParameterTag extends TagSupport {
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
        JspException e;
        if (this.multiple == null) {
            String values1 = this.pageContext.getRequest().getParameter(this.name);
            if (values1 == null && this.value != null) {
                values1 = this.value;
            }
            if (values1 == null) {
                e = new JspException(messages.getMessage("parameter.get", this.name));
                TagUtils.getInstance().saveException(this.pageContext, e);
                throw e;
            } else {
                this.pageContext.setAttribute(this.id, values1);
                return 0;
            }
        } else {
            String[] values = this.pageContext.getRequest().getParameterValues(this.name);
            if ((values == null || values.length == 0) && this.value != null) {
                values = new String[]{this.value};
            }
            if (values != null && values.length != 0) {
                this.pageContext.setAttribute(this.id, values);
                return 0;
            } else {
                e = new JspException(messages.getMessage("parameter.get", this.name));
                TagUtils.getInstance().saveException(this.pageContext, e);
                throw e;
            }
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