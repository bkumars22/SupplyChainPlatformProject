/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.wizard;

import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.TagRendererFactory;
import com.scplatform.testing.webui.taglib.Settings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WizardTag extends BodyTagSupport {
	private static final Logger log_ = LoggerFactory.getLogger(WizardTag.class);
	protected String name_ = null;
	protected String currentStep_ = null;
	protected Wizard model_;

	public WizardTag() {
		this.resetCustomAttributes();
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.name_ = null;
		this.currentStep_ = null;
		this.model_ = null;
	}

	public void setName(String name) {
		this.name_ = name;
	}

	public void setStep(String currentStep) {
		this.currentStep_ = currentStep;
	}

	public int doStartTag() throws JspException {
		this.model_ = (Wizard) this.pageContext.findAttribute(this.name_);
		if (this.model_ == null) {
			return 0;
		} else {
			if (this.currentStep_.equalsIgnoreCase("summary")) {
				this.model_.setSummaryStep(true);
			} else if (this.currentStep_.equalsIgnoreCase("finish")) {
				this.model_.setFinishStep(true);
			} else if (this.currentStep_.equalsIgnoreCase("cancel")) {
				this.model_.setCancelStep(true);
			} else {
				this.model_.setSummaryStep(false);
				this.model_.setFinishStep(false);
				this.model_.setCancelStep(false);

				try {
					this.model_.setCurrentStep(Integer.parseInt(this.currentStep_));
				} catch (Exception var2) {
					log_.warn("Unable to assign wizard step", var2);
					throw new JspException(var2.getMessage());
				}
			}

			return 2;
		}
	}

	public int doEndTag() throws JspException {
		if (this.model_ == null) {
			return 6;
		} else {
			try {
				Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
				TagRenderer renderer = TagRendererFactory.getRenderer("wizard");
				renderer.render(this.model_, this.bodyContent, this.pageContext.getOut(), settings);
			} catch (Exception var3) {
				log_.error(var3.getMessage(), var3);
			}

			return 6;
		}
	}

}