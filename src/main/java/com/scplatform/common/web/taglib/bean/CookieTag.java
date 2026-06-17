/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import java.util.ArrayList;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import com.scplatform.common.web.taglib.MessageResources;
import com.scplatform.common.web.taglib.TagUtils;

public class CookieTag extends TagSupport {
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
        ArrayList values = new ArrayList();
        Cookie[] cookies = ((HttpServletRequest) this.pageContext.getRequest()).getCookies();
        if (cookies == null) {
            cookies = new Cookie[0];
        }
        for (int cookie = 0; cookie < cookies.length; ++cookie) {
            if (this.name.equals(cookies[cookie].getName())) {
                values.add(cookies[cookie]);
            }
        }
        if (values.size() < 1 && this.value != null) {
            values.add(new Cookie(this.name, this.value));
        }
        if (values.size() < 1) {
            JspException arg4 = new JspException(messages.getMessage("cookie.get", this.name));
            TagUtils.getInstance().saveException(this.pageContext, arg4);
            throw arg4;
        } else {
            if (this.multiple == null) {
                Cookie arg3 = (Cookie) values.get(0);
                this.pageContext.setAttribute(this.id, arg3);
            } else {
                cookies = new Cookie[values.size()];
                this.pageContext.setAttribute(this.id, values.toArray(cookies));
            }
            return 0;
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