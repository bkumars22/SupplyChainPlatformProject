/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTag extends TagSupport {
	private static final Logger logCategory_ = LoggerFactory.getLogger(BaseTag.class);
	public static final String BASE_PATH_KEY = "i2BasePath";
	protected String basePath_ = null;
	protected String includePort_;

	public BaseTag() {
		this.resetCustomAttributes();
	}

	public void setPath(String basePath) {
		this.basePath_ = basePath;
	}

	public String getPath() {
		return this.basePath_;
	}

	public void setIncludeport(String includePort) {
		this.includePort_ = includePort;
	}

	public String getIncludeport() {
		return this.includePort_;
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.basePath_ = null;
		this.includePort_ = null;
	}

	public int doStartTag() throws JspException {
		PageContext var10003;
		try {
			JspWriter out = this.pageContext.getOut();
			StringBuffer baseTag = new StringBuffer("<base href=\"");
			String path = this.computePath();
			baseTag.append(path);
			baseTag.append("\">");
			out.write(baseTag.toString());
			var10003 = this.pageContext;
			this.pageContext.setAttribute("i2BasePath", path, 1);
		} catch (IOException var4) {
			logCategory_.warn("Failed to output HTML base tag " + this.pageContext.getPage().getClass().getName());
			var10003 = this.pageContext;
			this.pageContext.setAttribute("i2BasePath", "", 1);
		}

		return 0;
	}

	protected String computePath() {
		StringBuffer path = new StringBuffer();
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		path.append(getBasePath(request, this.includePort_));
		if (this.basePath_ != null) {
			if ("request".equalsIgnoreCase(this.basePath_)) {
				String servletPath = request.getRequestURI();
				int lastSlash = servletPath.lastIndexOf(47) + 1;
				int startPos = request.getContextPath().length() + 1;
				path.append(servletPath.substring(startPos, lastSlash));
			} else {
				path.append(this.basePath_);
				if (!this.basePath_.endsWith("/")) {
					path.append("/");
				}
			}
		}

		return path.toString();
	}

	public static String getBasePath(HttpServletRequest request, String includePort) {
		StringBuffer path = new StringBuffer(request.getScheme());
		path.append("://");
		path.append(request.getServerName());
		int port = request.getServerPort();
		if (!"no".equalsIgnoreCase(includePort) && ("yes".equalsIgnoreCase(includePort) || port != 80)) {
			path.append(":");
			path.append(port);
		}

		path.append(request.getContextPath());
		path.append("/");
		return path.toString();
	}

}