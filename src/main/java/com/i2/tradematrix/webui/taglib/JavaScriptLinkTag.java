/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaScriptLinkTag extends TagSupport {
	private static final Logger logCategory_ = LoggerFactory.getLogger(JavaScriptLinkTag.class);
	protected String linkPath_ = null;
	protected String version_ = "";
	protected String isInline_ = "";
	protected boolean isAbsolute_ = false;
	protected String cacheProperty_ = null;

	public JavaScriptLinkTag() {
		this.resetCustomAttributes();
	}

	public void setPath(String path) {
		this.linkPath_ = path;
	}

	public String getPath() {
		return this.linkPath_;
	}

	public void setCacheProperty(String cacheProperty) {
		this.cacheProperty_ = cacheProperty;
	}

	public String getCacheProperty() {
		if (this.cacheProperty_ != null) {
			return "?cacheProperty=" + this.cacheProperty_;
		} else {
			Object buildNumber = this.pageContext.getSession().getAttribute("cacheProperty");
			return buildNumber == null ? "" : "?cacheProperty=" + (String) buildNumber;
		}
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

	public void setVersion(String version) {
		if ("1.1".equals(version) || "1.2".equals(version) || "1.3".equals(version) || "1.4".equals(version)
				|| "1.5".equals(version)) {
			this.version_ = version;
		}

	}

	public String getVersion() {
		return this.version_;
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.linkPath_ = null;
		this.version_ = "";
		this.isInline_ = "";
		this.isAbsolute_ = false;
	}

	public int doStartTag() throws JspException {
		boolean inline = this.doInline();
		String thePath = this.getJavaScriptPath(inline);

		try {
			JspWriter out = this.pageContext.getOut();
			if (inline) {
				out.write("<script language=\"javascript");
				out.write(this.version_);
				out.println("\" type=\"text/javascript\">");
				this.pageContext.include(thePath);
				out.println("</script>");
			} else {
				HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
				out.write("<script language=\"javascript");
				out.write(this.version_);
				out.write("\" type=\"text/javascript\" src=\"");
				if (thePath != null && thePath.endsWith(".jsp")) {
					thePath = response.encodeURL(thePath);
				}

				out.write(thePath + this.getCacheProperty());
				out.println("\"></script>");
			}

			return 0;
		} catch (Exception var5) {
			String msg = null;
			if (inline) {
				msg = "Unable to inline JavaScript for " + thePath;
			} else {
				msg = "Unable to generate JavaScript link for " + thePath;
			}

			logCategory_.warn(msg);
			throw new JspException(msg);
		}
	}

	protected String getJavaScriptPath(boolean inline) {
		String thePath = this.linkPath_;
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		String contextPath = settings.getClientRootPath();
		if (inline) {
			if (thePath.startsWith(contextPath)) {
				thePath = thePath.substring(contextPath.length());
			}
		} else if (this.isRelativeToRoot()) {
			thePath = contextPath + this.linkPath_;
		}

		return thePath;
	}

	protected boolean isRelativeToRoot() {
		return this.linkPath_.startsWith("/") && !this.isAbsolute_;
	}

	protected boolean doInline() {
		if ("yes".equalsIgnoreCase(this.isInline_)) {
			return true;
		} else if ("no".equalsIgnoreCase(this.isInline_)) {
			return false;
		} else {
			Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
			return settings != null ? settings.getJavascriptInline() : false;
		}
	}

}