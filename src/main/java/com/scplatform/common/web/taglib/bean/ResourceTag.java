/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class ResourceTag extends TagSupport {
    protected static final int BUFFER_SIZE = 256;
    protected static MessageResources messages = MessageResources.getMessageResources("com.scplatform.common.web.taglib.bean.LocalStrings");
    protected String id = null;
    protected String input = null;
    protected String name = null;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int doStartTag() throws JspException {
        InputStream stream = this.pageContext.getServletContext().getResourceAsStream(this.name);
        if (stream == null) {
            JspException e1 = new JspException(messages.getMessage("resource.get", this.name));
            TagUtils.getInstance().saveException(this.pageContext, e1);
            throw e1;
        } else if (this.input != null) {
            this.pageContext.setAttribute(this.id, stream);
            return 0;
        } else {
            try {
                StringBuffer e = new StringBuffer();
                InputStreamReader reader = new InputStreamReader(stream);
                char[] buffer = new char[256];
                boolean n = false;
                while (true) {
                    int n1 = reader.read(buffer);
                    if (n1 < 1) {
                        reader.close();
                        this.pageContext.setAttribute(this.id, e.toString());
                        return 0;
                    }
                    e.append(buffer, 0, n1);
                }
            } catch (IOException arg5) {
                TagUtils.getInstance().saveException(this.pageContext, arg5);
                throw new JspException(messages.getMessage("resource.get", this.name));
            }
        }
    }

    public void release() {
        super.release();
        this.id = null;
        this.input = null;
        this.name = null;
    }
}