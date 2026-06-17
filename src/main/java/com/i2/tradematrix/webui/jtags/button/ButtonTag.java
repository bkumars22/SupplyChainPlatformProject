/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.button;

import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.TagRendererFactory;
import com.scplatform.testing.webui.taglib.Settings;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonTag extends BodyTagSupport {
	private static final Logger log_ = LoggerFactory.getLogger(ButtonTag.class);
	String name_;
	Button model_;

	public ButtonTag() {
		this.resetCustomAttributes();
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.name_ = null;
		this.model_ = null;
	}

	public void setName(String value) {
		this.name_ = value;
	}

	public int doStartTag() throws JspException {
		this.model_ = (Button) this.pageContext.findAttribute(this.name_);
		if (this.model_ == null) {
			log_.error("Missing button model for " + this.name_);
			return 0;
		} else {
			return 2;
		}
	}

	public int doEndTag() throws JspException {
		if (this.model_ == null) {
			return 6;
		} else {
			try {
				Settings settings = (Settings) this.pageContext.findAttribute("i2.settings");
				TagRenderer renderer = TagRendererFactory.getRenderer("button");
				renderer.render(this.model_, this.bodyContent, this.pageContext.getOut(), settings);
			} catch (Exception var3) {
				log_.error(var3.getMessage(), var3);
			}

			return 6;
		}
	}

}