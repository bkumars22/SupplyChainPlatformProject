/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientCacheTag extends TagSupport {
	private static final Logger logCategory_ = LoggerFactory.getLogger(ClientCacheTag.class);
	private boolean cacheEnabled_ = false;
	private String cacheAge_ = null;

	public ClientCacheTag() {
		this.resetCustomAttributes();
	}

	public void setEnabled(String cacheEnabled) {
		this.cacheEnabled_ = "yes".equalsIgnoreCase(cacheEnabled);
	}

	public String getEnabled() {
		return this.cacheEnabled_ ? "yes" : "no";
	}

	public void setAge(String cacheAge) {
		this.cacheAge_ = cacheAge;
	}

	public String getAge() {
		return this.cacheAge_;
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.cacheEnabled_ = false;
		this.cacheAge_ = null;
	}

	public int doStartTag() throws JspException {
		if (logCategory_.isDebugEnabled()) {
			String msg = "Turning cache " + (this.cacheEnabled_ ? "on" : "off") + " in "
					+ this.pageContext.getPage().getClass().getName();
			logCategory_.debug(msg);

			try {
				JspWriter out = this.pageContext.getOut();
				out.write("<!--");
				out.write(msg);
				out.write("-->");
			} catch (Exception var5) {
			}
		}

		HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
		if (!this.cacheEnabled_) {
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Cache-Control", "no-store");
			response.setDateHeader("Expires", 0L);
			response.setHeader("Pragma", "no-cache");
		} else {
			int deltaSeconds = this.computeCacheAge();
			response.setHeader("Cache-Control", "max-age=" + deltaSeconds);
			long dateMillis = System.currentTimeMillis() + (long) (deltaSeconds * 1000);
			response.setDateHeader("Expires", dateMillis);
		}

		return 0;
	}

	protected int computeCacheAge() {
		int deltaSeconds = 0;
		if (this.cacheAge_ != null) {
			try {
				deltaSeconds = Integer.parseInt(this.cacheAge_);
			} catch (Exception var3) {
				logCategory_.warn("Unable to parse cache age: " + this.cacheAge_ + ".  Output will not be cached.");
			}
		} else {
			HttpSession session = this.pageContext.getSession();
			deltaSeconds = session.getMaxInactiveInterval();
		}

		return deltaSeconds;
	}

}