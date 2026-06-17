/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.io.IOException;
import java.util.StringTokenizer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import org.owasp.encoder.Encode;

import com.scplatform.common.web.taglib.MessageResources;

import com.scplatform.common.web.taglib.TagUtils;

public class BaseTag extends TagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected final String REF_SITE = "site";
    protected final String REF_PAGE = "page";
    protected String server = null;
    protected String target = null;
    protected String ref = "page";

    public String getRef() {
	return this.ref;
    }

    public void setRef(String ref) {
	if (ref == null) {
	    throw new IllegalArgumentException("Ref attribute cannot be null");
	} else {
	    ref = ref.toLowerCase();
	    if (!ref.equals("page") && !ref.equals("site")) {
		throw new IllegalArgumentException("Ref attribute must either be \'page\' or \'site\'");
	    } else {
		this.ref = ref;
	    }
	}
    }

    public String getTarget() {
	return this.target;
    }

    public void setTarget(String target) {
	this.target = target;
    }

    public int doStartTag() throws JspException {
	HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
	String serverName = this.server == null ? this.escapeHtmlAttribute(request.getServerName())
		: this.escapeHtmlAttribute(this.server);
	int port = request.getServerPort();
	String headerHost = request.getHeader("Host");
	if (serverName == null && headerHost != null) {
	    StringTokenizer baseTag = new StringTokenizer(headerHost, ":");
	    serverName = this.escapeHtmlAttribute(baseTag.nextToken());
	    if (baseTag.hasMoreTokens()) {
		String out = baseTag.nextToken();

		try {
		    port = Integer.parseInt(out);
		} catch (Exception arg8) {
		    port = 80;
		}
	    } else {
		port = 80;
	    }
	}

	String baseTag1 = this.renderBaseElement(request.getScheme(), serverName, port,
		this.escapeHtmlAttribute(request.getRequestURI()));
	JspWriter out1 = this.pageContext.getOut();

	try {
	    out1.write(baseTag1);
	    return 1;
	} catch (IOException arg7) {
	    this.pageContext.setAttribute("com.test.controller.action.EXCEPTION", arg7, 2);
	    throw new JspException(messages.getMessage("common.io", arg7.toString()));
	}
    }

    protected String renderBaseElement(String scheme, String serverName, int port, String uri) {
	StringBuffer tag = new StringBuffer("<base href=\"");
	if (this.ref.equals("site")) {
	    StringBuffer contextBase = new StringBuffer(
		    ((HttpServletRequest) this.pageContext.getRequest()).getContextPath());
	    contextBase.append("/");
	    tag.append(this.createServerUriStringBuffer(scheme, serverName, port, contextBase.toString())
		    .toString());
	} else {
	    tag.append(this.createServerUriStringBuffer(scheme, serverName, port, uri).toString());
	}

	tag.append("\"");
	if (this.target != null) {
	    tag.append(" target=\"");
	    tag.append(this.escapeHtmlAttribute(this.target));
	    tag.append("\"");
	}

	if (TagUtils.getInstance().isXhtml(this.pageContext)) {
	    tag.append(" />");
	} else {
	    tag.append(">");
	}

	return tag.toString();
    }

    private StringBuffer createServerStringBuffer(String scheme, String server, int port) {
	StringBuffer url = new StringBuffer();
	if (port < 0) {
	    port = 80;
	}

	url.append(scheme);
	url.append("://");
	url.append(server);
	if (scheme.equals("http") && port != 80 || scheme.equals("https") && port != 443) {
	    url.append(':');
	    url.append(port);
	}

	return url;
    }

    private StringBuffer createServerUriStringBuffer(String scheme, String server, int port, String uri) {
	StringBuffer serverUri = this.createServerStringBuffer(scheme, server, port);
	serverUri.append(uri);
	return serverUri;
    }

    public String getServer() {
	return this.server;
    }

    public void setServer(String server) {
	this.server = server;
    }

    private String escapeHtmlAttribute(String value) {
	return value == null ? null : Encode.forHtmlAttribute(value);
    }
}
