/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import org.owasp.encoder.Encode;


public class FormTag extends BodyTagSupport {
    private static final String FORM_SCOPE_KEY = "com.scplatform.common.web.taglib.html.FORM";
    private static final String ORIGINAL_URI_KEY = "com.test.controller.globals.ORIGINAL_URI_KEY";
	private static final String DEFAULT_FORM_NAME = "form";

    private String action;
    private String method;
    private String style;
	private String formName;

    public String getAction() {
	return this.action;
    }

    public void setAction(String action) {
	this.action = action;
    }

    public String getMethod() {
	return this.method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    public String getStyle() {
	return this.style;
    }

    public void setStyle(String style) {
	this.style = style;
    }

    public String getBeanName() {
	return this.formName;
    }

    @Override
    public int doStartTag() throws JspException {
	String resolvedAction = this.resolveAction();
	this.formName = this.resolveFormName(resolvedAction);

	StringBuilder results = new StringBuilder("<form");
	if (this.formName != null && !this.formName.isBlank()) {
	    results.append(" name=\"");
	    results.append(Encode.forHtmlAttribute(this.formName));
	    results.append("\"");
	}

	results.append(" method=\"");
	results.append(Encode.forHtmlAttribute(this.method == null || this.method.isBlank() ? "post" : this.method));
	results.append("\"");
	this.renderAction(results, resolvedAction);
	if (this.style != null && !this.style.isBlank()) {
	    results.append(" style=\"");
	    results.append(Encode.forHtmlAttribute(this.style));
	    results.append("\"");
	}
	results.append(">");

	try {
	    this.pageContext.getOut().write(results.toString());
	} catch (IOException e) {
	    throw new JspException(e);
	}

	this.pageContext.setAttribute(FORM_SCOPE_KEY, this, PageContext.REQUEST_SCOPE);
	return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
	this.pageContext.removeAttribute(FORM_SCOPE_KEY, PageContext.REQUEST_SCOPE);
	try {
	    this.pageContext.getOut().write("</form>");
	} catch (IOException e) {
	    throw new JspException(e);
	}
	return EVAL_PAGE;
    }

	protected void renderAction(StringBuilder results, String resolvedAction) {
	HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
	results.append(" action=\"");
	String url = resolvedAction;
	if (url == null || url.isBlank()) {
	    url = request.getRequestURI();
	}
	if (url.startsWith(request.getContextPath() + "/")) {
	    url = url.substring((request.getContextPath() + "/").length());
	}
	results.append(Encode.forHtmlAttribute(response.encodeURL(url)));

	results.append("\"");
    }

    @Override
    public void release() {
	super.release();
	this.action = null;
	this.method = null;
	this.style = null;
	this.formName = null;
    }

    private String resolveAction() {
	if (this.action != null && !this.action.isBlank()) {
	    return this.action;
	}

	Object originalUri = ((HttpServletRequest) this.pageContext.getRequest()).getAttribute(ORIGINAL_URI_KEY);
	if (originalUri instanceof String) {
	    return (String) originalUri;
	}

	return ((HttpServletRequest) this.pageContext.getRequest()).getRequestURI();
    }

	private String resolveFormName(String resolvedAction) {
	if (resolvedAction == null || resolvedAction.isBlank()) {
	    return DEFAULT_FORM_NAME;
	}

	String formName = resolvedAction;
	int queryIndex = formName.indexOf('?');
	if (queryIndex >= 0) {
	    formName = formName.substring(0, queryIndex);
	}

	int fragmentIndex = formName.indexOf('#');
	if (fragmentIndex >= 0) {
	    formName = formName.substring(0, fragmentIndex);
	}

	int slashIndex = formName.lastIndexOf('/');
	if (slashIndex >= 0 && slashIndex < formName.length() - 1) {
	    formName = formName.substring(slashIndex + 1);
	}

	int extensionIndex = formName.lastIndexOf('.');
	if (extensionIndex > 0) {
	    formName = formName.substring(0, extensionIndex);
	}

	formName = formName.replaceAll("[^A-Za-z0-9_]", "_");
	if (formName.isBlank()) {
	    return DEFAULT_FORM_NAME;
	}

	if (Character.isDigit(formName.charAt(0))) {
	    return DEFAULT_FORM_NAME + "_" + formName;
	}

	return formName;
    }

}
