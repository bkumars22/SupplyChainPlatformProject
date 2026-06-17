/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StyleSheetLinkTag extends TagSupport {
	private static final Logger logCategory_ = LoggerFactory.getLogger(StyleSheetLinkTag.class);
	protected String styleSheetPath_ = null;
	protected String isInline_ = "";
	protected boolean isAbsolute_ = false;
	protected String isDefault_;

	public StyleSheetLinkTag() {
		this.resetCustomAttributes();
	}

	public void setPath(String styleSheetPath) {
		this.styleSheetPath_ = styleSheetPath;
	}

	public String getPath() {
		return this.styleSheetPath_;
	}

	public void setInline(String isInline) {
		this.isInline_ = isInline;
	}

	public String getInline() {
		return this.isInline_;
	}

	public void setAbsolute(String isAbsolute) {
		if ("yes".equalsIgnoreCase(isAbsolute)) {
			this.isAbsolute_ = true;
		}

	}

	public String getAbsolute() {
		return this.isAbsolute_ ? "yes" : "no";
	}

	public void setDefault(String isDefault) {
		this.isDefault_ = isDefault;
	}

	public String getDefault() {
		return this.isDefault_;
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.styleSheetPath_ = null;
		this.isAbsolute_ = false;
		this.isInline_ = "";
		this.isDefault_ = null;
	}

	public int doStartTag() throws JspException {
		JspWriter out = this.pageContext.getOut();
		String thePath = this.getCSSPath();

		try {
			out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			out.write(thePath + this.getCacheProperty());
			out.write("\"/>");
		} catch (Exception var5) {
			String msg = "Unable to generate CSS link for " + thePath;
			logCategory_.warn(msg);
			throw new JspException(msg);
		}

		if ("yes".equalsIgnoreCase(this.isDefault_)) {
			HttpServletRequest req = (HttpServletRequest) this.pageContext.getRequest();
			Settings settings = Settings.getSessionSettings(req);
			if (settings == null) {
				logCategory_.warn("Settings undefined.  Unable to set default stylesheet.");
			}
		}

		return 0;
	}

	protected String getCSSPath() {
		String thePath = this.styleSheetPath_;
		if (this.isRelativeToRoot()) {
			Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
			if (settings != null) {
				thePath = settings.getCSSDirectory() + this.styleSheetPath_;
			} else {
				logCategory_.warn("CSS directory is undefined.  Defaulting CSS path to " + thePath);
			}
		}

		return thePath;
	}

	public String getCacheProperty() {
		Object buildNumber = this.pageContext.getSession().getAttribute("cacheProperty");
		return buildNumber == null ? "" : "?cacheProperty=" + (String) buildNumber;
	}

	protected boolean isRelativeToRoot() {
		return this.styleSheetPath_.startsWith("/") && !this.isAbsolute_;
	}

	protected boolean doInline() {
		if ("no".equalsIgnoreCase(this.isInline_)) {
			return false;
		} else {
			return "yes".equalsIgnoreCase(this.isInline_);
		}
	}

	protected void inlineFile(String path) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		Settings settings = Settings.getSessionSettings(request);
		String contextPath = settings.getClientRootPath();
		if (path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		if (logCategory_.isDebugEnabled()) {
			logCategory_.debug("Inlining (via PageContext.include()) the path = " + path);
		}

		this.pageContext.include(path);
	}

}