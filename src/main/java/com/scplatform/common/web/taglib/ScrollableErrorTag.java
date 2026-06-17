/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.util.Iterator;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.UiMessages;

@SuppressWarnings("serial")
public class ScrollableErrorTag extends EscapedErrorTag {

	private int maxErrors = 4;
	private String styleClass = null;
	private String styleId = null;
	private String style = null;

	@Override
	public void release() {
		maxErrors = 4;
		styleClass = null;
		styleId = null;
		style = null;
		super.release();
	}

	@Override
	public int doStartTag() throws JspException {
		UiMessages errors = null;
		try {
			errors = TagUtils.getInstance().getMessages(pageContext, name);
		} catch (JspException e) {
			TagUtils.getInstance().saveException(pageContext, e);
			throw e;
		}
		int iteratorSize = 0;
		if (errors != null && !errors.isEmpty()) {
			int size = getMaxErrors() * 30;
			if (size < 50) {
				size = 50;
			}
			
			TagUtils.getInstance().write(pageContext,
					"<div class='eto-messageblock' data-message-type='error' id='error-message-block'>");
			TagUtils.getInstance().write(pageContext, "<div class='eto-messageblock__body'>");
			Iterator reports = (property == null) ? errors.get() : errors.get(property);
			while (reports.hasNext()) {
				reports.next();
				iteratorSize++;
	        }			
			if (iteratorSize > 1) {
			TagUtils.getInstance().write(pageContext, "<b class='eto-messageblock__title'>Errors</b>");
			TagUtils.getInstance().write(pageContext, " <ul style='list-style:none;'>");
			}
			else {
		    TagUtils.getInstance().write(pageContext, "<span class=\"eto-messageblock__title\">Errors</span>");
			}
		}

		int rc = super.doStartTag();
		if (errors != null && !errors.isEmpty()) {
			if (iteratorSize > 1) {
			TagUtils.getInstance().write(pageContext, "</ul>");
			}
			TagUtils.getInstance().write(pageContext, "</div>");
			TagUtils.getInstance().write(pageContext,
					"<a href='javascript:void(0)' role='button' class='eto-messageblock__close'></a>");
			TagUtils.getInstance().write(pageContext, "</div>");
			TagUtils.getInstance().write(pageContext, "<script>");
			  TagUtils.getInstance().write(pageContext,
			"if(typeof parent !== 'undefined' && parent.mcmApp && typeof parent.mcmApp.toast !== 'undefined') { "
			+ "parent.mcmApp.toast.addToast(new eto.MessageBlock({ el: document.querySelector('#error-message-block') }),'error',100000); "
			+ "} else if(typeof parent !== 'undefined' && typeof parent.parent !== 'undefined' && parent.parent.mcmApp && typeof parent.parent.mcmApp.toast !== 'undefined') { "
			+ "parent.parent.mcmApp.toast.addToast(new eto.MessageBlock({ el: document.querySelector('#error-message-block') }),'error',100000); "
			+ "} else { console.warn('Toast notification unavailable - mcmApp not found'); }");
			TagUtils.getInstance().write(pageContext, "</script>");
		}
		return rc;
	}

	public void setMaxErrors(int maxErrors) {
		this.maxErrors = maxErrors;
	}

	public int getMaxErrors() {
		return maxErrors;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
