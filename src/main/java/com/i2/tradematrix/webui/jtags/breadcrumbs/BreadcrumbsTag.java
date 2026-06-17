/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.breadcrumbs;

import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.TagRendererFactory;
import com.scplatform.testing.webui.taglib.Settings;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BreadcrumbsTag extends TagSupport {
	private static final Logger log_ = LoggerFactory.getLogger(BreadcrumbsTag.class);
	protected String name_;
	protected String autoinit_;
	protected BreadcrumbList model_;

	public BreadcrumbsTag() {
		this.resetCustomAttributes();
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void setName(String beanName) {
		this.name_ = beanName;
	}

	public void setAutoinit(String autoinit) {
		this.autoinit_ = autoinit;
	}

	public void resetCustomAttributes() {
		this.name_ = null;
		this.autoinit_ = null;
		this.model_ = null;
	}

	public int doStartTag() throws JspException {
		this.model_ = (BreadcrumbList) this.pageContext.findAttribute(this.name_);
		if (this.model_ == null) {
			log_.error("Missing breadcrumbs model " + this.name_);
		}

		if (this.autoinit_ != null && !"true".equalsIgnoreCase(this.autoinit_)) {
			this.model_.setAutoinit(false);
		} else {
			this.model_.setAutoinit(true);
		}

		return 0;
	}

	public int doEndTag() throws JspException {
		if (this.model_ == null) {
			return 6;
		} else {
			try {
				Settings settings = (Settings) this.pageContext.findAttribute("i2.settings");
				TagRenderer renderer = TagRendererFactory.getRenderer("breadcrumbs");
				renderer.render(this.model_, (BodyContent) null, this.pageContext.getOut(), settings);
			} catch (Exception var3) {
				log_.error(var3.getMessage(), var3);
			}

			return 6;
		}
	}

}