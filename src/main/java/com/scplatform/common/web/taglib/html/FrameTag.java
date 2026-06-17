/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;

public class FrameTag extends LinkTag {
    protected String frameborder = null;
    protected String frameName = null;
    protected String longdesc = null;
    protected Integer marginheight = null;
    protected Integer marginwidth = null;
    protected boolean noresize = false;
    protected String scrolling = null;

    public String getFrameborder() {
	return this.frameborder;
    }

    public void setFrameborder(String frameborder) {
	this.frameborder = frameborder;
    }

    public String getFrameName() {
	return this.frameName;
    }

    public void setFrameName(String frameName) {
	this.frameName = frameName;
    }

    public String getLongdesc() {
	return this.longdesc;
    }

    public void setLongdesc(String longdesc) {
	this.longdesc = longdesc;
    }

    public Integer getMarginheight() {
	return this.marginheight;
    }

    public void setMarginheight(Integer marginheight) {
	this.marginheight = marginheight;
    }

    public Integer getMarginwidth() {
	return this.marginwidth;
    }

    public void setMarginwidth(Integer marginwidth) {
	this.marginwidth = marginwidth;
    }

    public boolean getNoresize() {
	return this.noresize;
    }

    public void setNoresize(boolean noresize) {
	this.noresize = noresize;
    }

    public String getScrolling() {
	return this.scrolling;
    }

    public void setScrolling(String scrolling) {
	this.scrolling = scrolling;
    }

    public int doEndTag() throws JspException {
	StringBuffer results = new StringBuffer("<frame");
	this.prepareAttribute(results, "src", this.calculateURL());
	this.prepareAttribute(results, "name", this.getFrameName());
	if (this.noresize) {
	    results.append(" noresize=\"noresize\"");
	}

	this.prepareAttribute(results, "scrolling", this.getScrolling());
	this.prepareAttribute(results, "marginheight", this.getMarginheight());
	this.prepareAttribute(results, "marginwidth", this.getMarginwidth());
	this.prepareAttribute(results, "frameborder", this.getFrameborder());
	this.prepareAttribute(results, "longdesc", this.getLongdesc());
	results.append(this.prepareStyles());
	this.prepareOtherAttributes(results);
	results.append(this.getElementClose());
	TagUtils.getInstance().write(this.pageContext, results.toString());
	return 6;
    }

    public void release() {
	super.release();
	this.frameborder = null;
	this.frameName = null;
	this.longdesc = null;
	this.marginheight = null;
	this.marginwidth = null;
	this.noresize = false;
	this.scrolling = null;
    }
}