/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;

public class MatchTag extends ConditionalTagBase {
    protected String location = null;
    protected String value = null;

    public String getLocation() {
	return this.location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public String getValue() {
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public void release() {
	super.release();
	this.location = null;
	this.value = null;
    }

    protected boolean condition() throws JspException {
	return this.condition(true);
    }

    protected boolean condition(boolean desired) throws JspException {
	String variable = null;
	JspException arg7;
	if (this.cookie != null) {
	    Cookie[] matched = ((HttpServletRequest) this.pageContext.getRequest()).getCookies();
	    if (matched == null) {
		matched = new Cookie[0];
	    }

	    for (int e = 0; e < matched.length; ++e) {
		if (this.cookie.equals(matched[e].getName())) {
		    variable = matched[e].getValue();
		    break;
		}
	    }
	} else if (this.header != null) {
	    variable = ((HttpServletRequest) this.pageContext.getRequest()).getHeader(this.header);
	} else if (this.name != null) {
	    Object arg4 = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, this.scope);
	    if (arg4 != null) {
		variable = arg4.toString();
	    }
	} else {
	    if (this.parameter == null) {
		arg7 = new JspException(messages.getMessage("logic.selector"));
		TagUtils.getInstance().saveException(this.pageContext, arg7);
		throw arg7;
	    }

	    variable = this.pageContext.getRequest().getParameter(this.parameter);
	}

	if (variable == null) {
	    arg7 = new JspException(messages.getMessage("logic.variable", this.value));
	    TagUtils.getInstance().saveException(this.pageContext, arg7);
	    throw arg7;
	} else {
	    boolean arg5 = false;
	    if (this.location == null) {
		arg5 = variable.indexOf(this.value) >= 0;
	    } else if (this.location.equals("start")) {
		arg5 = variable.startsWith(this.value);
	    } else {
		if (!this.location.equals("end")) {
		    JspException arg6 = new JspException(messages.getMessage("logic.location", this.location));
		    TagUtils.getInstance().saveException(this.pageContext, arg6);
		    throw arg6;
		}

		arg5 = variable.endsWith(this.value);
	    }

	    return arg5 == desired;
	}
    }
}