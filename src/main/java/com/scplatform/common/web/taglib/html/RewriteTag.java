/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;

public class RewriteTag extends LinkTag {
    public int doEndTag() throws JspException {
	Object params = TagUtils.getInstance().computeParameters(this.pageContext, this.paramId, this.paramName,
		this.paramProperty, this.paramScope, this.name, this.property, this.scope, this.transaction);
	if (!this.parameters.isEmpty()) {
	    if (params == null) {
		params = new HashMap();
	    }

	    ((Map) params).putAll(this.parameters);
	}

	String url = null;

	try {
	    url = TagUtils.getInstance().computeURLWithCharEncoding(this.pageContext, this.forward, this.href,
		    this.page, this.action, this.module, (Map) params, this.anchor, false, this.isXhtml(),
		    this.useLocalEncoding);
	} catch (MalformedURLException arg3) {
	    TagUtils.getInstance().saveException(this.pageContext, arg3);
	    throw new JspException(messages.getMessage("rewrite.url", arg3.toString()));
	}

	TagUtils.getInstance().write(this.pageContext, url);
	return 6;
    }
}