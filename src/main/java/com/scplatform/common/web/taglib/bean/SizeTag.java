/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import com.scplatform.common.web.taglib.MessageResources;
import com.scplatform.common.web.taglib.TagUtils;

public class SizeTag extends TagSupport {
    protected static MessageResources messages = MessageResources.getMessageResources("com.scplatform.common.web.taglib.bean.LocalStrings");
    protected Object collection = null;
    protected String id = null;
    protected String name = null;
    protected String property = null;
    protected String scope = null;

    public Object getCollection() {
        return this.collection;
    }

    public void setCollection(Object collection) {
        this.collection = collection;
    }

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

    public int doStartTag() throws JspException {
        Object value = this.collection;
        if (value == null) {
            if (this.name == null) {
                JspException size2 = new JspException(messages.getMessage("size.noCollectionOrName"));
                TagUtils.getInstance().saveException(this.pageContext, size2);
                throw size2;
            }
            value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, this.scope);
        }
        boolean size = false;
        JspException e;
        if (value == null) {
            e = new JspException(messages.getMessage("size.collection"));
            TagUtils.getInstance().saveException(this.pageContext, e);
            throw e;
        } else {
            int size1;
            if (value.getClass().isArray()) {
                size1 = Array.getLength(value);
            } else if (value instanceof Collection) {
                size1 = ((Collection) value).size();
            } else {
                if (!(value instanceof Map)) {
                    e = new JspException(messages.getMessage("size.collection"));
                    TagUtils.getInstance().saveException(this.pageContext, e);
                    throw e;
                }
                size1 = ((Map) value).size();
            }
            this.pageContext.setAttribute(this.id, new Integer(size1), 1);
            return 0;
        }
    }

    public void release() {
        super.release();
        this.collection = null;
        this.id = null;
        this.name = null;
        this.property = null;
        this.scope = null;
    }
}