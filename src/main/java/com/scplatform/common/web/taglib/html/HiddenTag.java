/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;

public class HiddenTag extends BaseFieldTag {
    protected boolean write = false;

    public HiddenTag() {
	this.type = "hidden";
    }

    public boolean getWrite() {
	return this.write;
    }

    public void setWrite(boolean write) {
	this.write = write;
    }

    public int doStartTag() throws JspException {
	super.doStartTag();
	if (!this.write) {
	    return 2;
	} else {
	    String results = null;
	    if (this.value != null) {
		results = TagUtils.getInstance().filter(this.value);
	    } else {
		Object value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, (String) null);
		if (value == null) {
		    results = "";
		} else {
		    results = TagUtils.getInstance().filter(value.toString());
		}
	    }

	    TagUtils.getInstance().write(this.pageContext, results);
	    return 2;
	}
    }

    public void release() {
	super.release();
	this.write = false;
    }
}