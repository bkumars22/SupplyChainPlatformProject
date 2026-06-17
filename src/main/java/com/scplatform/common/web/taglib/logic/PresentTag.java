/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import java.security.Principal;
import java.util.StringTokenizer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;

public class PresentTag extends ConditionalTagBase {
    public static final String ROLE_DELIMITER = ",";

    protected boolean condition() throws JspException {
	return this.condition(true);
    }

    protected boolean condition(boolean desired) throws JspException {
	boolean present = false;
	HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
	if (this.cookie != null) {
	    present = this.isCookiePresent(request);
	} else {
	    String e;
	    if (this.header != null) {
		e = request.getHeader(this.header);
		present = e != null;
	    } else if (this.name != null) {
		present = this.isBeanPresent();
	    } else if (this.parameter != null) {
		e = request.getParameter(this.parameter);
		present = e != null;
	    } else if (this.role != null) {
		for (StringTokenizer e1 = new StringTokenizer(this.role, ",", false); !present
			&& e1.hasMoreTokens(); present = request.isUserInRole(e1.nextToken())) {
		    ;
		}
	    } else {
		if (this.user == null) {
		    JspException e3 = new JspException(messages.getMessage("logic.selector"));
		    TagUtils.getInstance().saveException(this.pageContext, e3);
		    throw e3;
		}

		Principal e2 = request.getUserPrincipal();
		present = e2 != null && this.user.equals(e2.getName());
	    }
	}

	return present == desired;
    }

    protected boolean isBeanPresent() {
	Object value = null;

	try {
	    if (this.property != null) {
		value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, this.scope);
	    } else {
		value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.scope);
	    }
	} catch (JspException arg2) {
	    value = null;
	}

	return value != null;
    }

    protected boolean isCookiePresent(HttpServletRequest request) {
	Cookie[] cookies = request.getCookies();
	if (cookies == null) {
	    return false;
	} else {
	    for (int i = 0; i < cookies.length; ++i) {
		if (this.cookie.equals(cookies[i].getName())) {
		    return true;
		}
	    }

	    return false;
	}
    }
}