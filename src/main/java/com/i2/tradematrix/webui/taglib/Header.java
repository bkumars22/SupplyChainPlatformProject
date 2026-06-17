/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Header extends BodyTagSupport {
	boolean rightAlign = true;

	public void setAlign(String value) {
		if (value.toLowerCase().equals("left")) {
			this.rightAlign = false;
		}

	}

	public int doStartTag() throws JspException {
		return 2;
	}

	public int doEndTag() throws JspException {
		BodyContent body = this.getBodyContent();
		Container container = (Container) findAncestorWithClass(this, Container.class);
		if (container != null && container == this.getParent()) {
			container.setComplexHeader(body.getString());
			container.setIsHeaderRightAlign(this.rightAlign);
		} else {
			TabbedContainer tabbedcontainer = (TabbedContainer) findAncestorWithClass(this, TabbedContainer.class);
			if (tabbedcontainer != null && tabbedcontainer == this.getParent()) {
				tabbedcontainer.setComplexHeader(body.getString());
			}
		}

		body.clearBody();
		return 6;
	}
}