/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.scplatform.common.web.taglib.MessageResources;
import com.scplatform.common.web.taglib.TagUtils;

public class DefineTag extends BodyTagSupport {
    private static final Log log;
    protected static MessageResources messages;
    protected String body = null;
    protected String id = null;
    protected String name = null;
    protected String property = null;
    protected String scope = null;
    protected String toScope = null;
    protected String type = null;
    protected String value = null;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getToScope() {
        return this.toScope;
    }

    public void setToScope(String toScope) {
        this.toScope = toScope;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int doStartTag() throws JspException {
        return 2;
    }

    public int doAfterBody() throws JspException {
        if (this.bodyContent != null) {
            this.body = this.bodyContent.getString();
            if (this.body != null) {
                this.body = this.body.trim();
            }
            if (this.body.length() < 1) {
                this.body = null;
            }
        }
        return 0;
    }

    public int doEndTag() throws JspException {
        int n = 0;
        if (this.body != null) {
            ++n;
        }
        if (this.name != null) {
            ++n;
        }
        if (this.value != null) {
            ++n;
        }
        if (n > 1) {
            JspException arg5 = new JspException(messages.getMessage("define.value", this.id));
            TagUtils.getInstance().saveException(this.pageContext, arg5);
            throw arg5;
        } else {
            Object value = this.value;
            if (value == null && this.name != null) {
                value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, this.scope);
            }
            if (value == null && this.body != null) {
                value = this.body;
            }
            if (value == null) {
                JspException arg6 = new JspException(messages.getMessage("define.null", this.id));
                TagUtils.getInstance().saveException(this.pageContext, arg6);
                throw arg6;
            } else {
                int inScope = 1;
                try {
                    if (this.toScope != null) {
                        inScope = TagUtils.getInstance().getScope(this.toScope);
                    }
                } catch (JspException arg4) {
                    log.warn("toScope was invalid name so we default to PAGE_SCOPE", arg4);
                }
                this.pageContext.setAttribute(this.id, value, inScope);
                return 6;
            }
        }
    }

    public void release() {
        super.release();
        this.body = null;
        this.id = null;
        this.name = null;
        this.property = null;
        this.scope = null;
        this.toScope = "page";
        this.type = null;
        this.value = null;
    }

    static {
        log = LogFactory.getLog(DefineTag.class);
        messages = MessageResources.getMessageResources("com.scplatform.common.web.taglib.bean.LocalStrings");
    }
}