/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class Img extends TagSupport {
	String id = null;
	String onclick = null;
	boolean disabled = false;
	String height = null;
	String width = null;
	String alt = null;
	String src = null;
	String border = null;
	String cssClass = null;

	public void setOnclick(String value) {
		this.onclick = value;
	}

	public void setHeight(String value) {
		this.height = value;
	}

	public void setWidth(String value) {
		this.width = value;
	}

	public void setAlt(String value) {
		this.alt = value;
	}

	public void setBorder(String value) {
		this.border = value;
	}

	public void setSrc(String value) {
		this.src = value;
	}

	public void setId(String value) {
		this.id = value;
	}

	public void setDisabled(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.disabled = true;
			this.cssClass = "disabled";
		} else {
			this.disabled = false;
			this.cssClass = null;
		}

	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		if (this.src.startsWith("/")) {
			this.src = settings.getImageDirectory() + this.src;
		}

		if (this.onclick != null && !this.disabled) {
			result.append("<A href=\"javascript:void 0\" onclick=\"" + this.onclick + ";return false;\">");
		}

		result.append("<IMG hspace=\"1\" src=\"" + this.src + "\"");
		if (this.id != null) {
			result.append(" id=\"" + this.id + "\" ");
		}

		if (this.width != null) {
			result.append(" width=\"" + this.width + "\" ");
		}

		if (this.height != null) {
			result.append(" height=\"" + this.height + "\" ");
		}

		if (this.border != null) {
			result.append(" border=\"" + this.border + "\" ");
		} else {
			result.append(" border=\"0px\" ");
		}

		if (this.disabled) {
			result.append(" class=\"" + this.cssClass + "\"");
		}

		if (this.alt != null) {
			result.append(" title=\"" + this.alt + "\" alt=\"" + this.alt + "\" ");
		}

		if (this.onclick != null && !this.disabled) {
			result.append(" onMouseOver=\"javascript:this.style.cursor='pointer'\" ");
		}

		result.append(">");
		if (this.onclick != null && !this.disabled) {
			result.append("</A>");
		}

		try {
			this.pageContext.getOut().write(result.toString());
			return 0;
		} catch (IOException var4) {
			throw new JspException(var4.getMessage());
		}
	}

	public void release() {
		super.release();
		this.onclick = null;
		this.id = null;
		this.src = null;
		this.disabled = false;
		this.height = null;
		this.width = null;
		this.alt = null;
		this.border = null;
		this.cssClass = null;
	}
}