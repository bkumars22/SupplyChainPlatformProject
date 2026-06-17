/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Button extends BodyTagSupport {
	protected String id = null;
	protected String onclick = null;
	protected String htmlonclick = null;
	protected String target = null;
	protected boolean disabled = false;
	protected boolean emphasized = false;
	protected boolean regular = true;
	protected boolean small = false;
	protected boolean nopadding = false;
	protected boolean hidden = false;
	protected Buttonbar owner = null;
	protected Settings settings;
	protected String styleExtra = "";
	protected StringBuffer styleClassname = null;

	public void setOnclick(String value) {
		this.onclick = value;
	}

	public void setHtmlonclick(String value) {
		this.htmlonclick = value;
	}

	public void setTarget(String value) {
		this.target = value;
	}

	public void setId(String value) {
		this.id = value;
	}

	public void setDisabled(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.disabled = true;
			this.styleExtra = "Disabled";
		}

	}

	public void setEmphasized(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.emphasized = true;
			this.regular = false;
			this.styleExtra = "Emphasized";
		}

	}

	public void setRegular(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.emphasized = false;
			this.regular = true;
		}

	}

	public void setSmall(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.small = true;
		}

	}

	public void setHidden(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.hidden = true;
		}

	}

	public void setNopadding(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.nopadding = true;
		}

	}

	public int doAfterBody() throws JspException {
		return 0;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		this.styleClassname = new StringBuffer("button");
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		this.owner = (Buttonbar) findAncestorWithClass(this, Buttonbar.class);
		if (this.owner != null) {
			this.owner.startNewButton(this);
		}

		if (this.small) {
			this.styleClassname.append("Small");
		}

		if (this.disabled) {
			this.styleClassname.append("Disabled");
		} else if (this.emphasized) {
			this.styleClassname.append("Emphasized");
		} else {
			this.styleClassname.append("Regular");
		}

		result.append("<TABLE ");
		if (this.id != null) {
			result.append("id=\"" + this.id + "\" ");
		}

		if (this.hidden) {
			result.append("style=\"display:none\" ");
		}

		result.append("cellspacing=\"0px\" cellpadding=\"0px\" class=\"buttonBorder" + this.styleExtra + "\">" + "<TR>"
				+ "<TD id=\"" + this.styleClassname + "\" nowrap=\"yes\" class=\"buttonText" + this.styleExtra + "\">");
		if (!this.disabled) {
			result.append("<A ");
			if (this.htmlonclick != null) {
				result.append(" onclick=\"" + this.htmlonclick + "\"");
			}

			result.append(" href=\"" + this.onclick + "\"");
			if (this.target != null) {
				result.append(" target=\"" + this.target + "\"");
			}

			result.append(">");
		}

		if (!this.nopadding) {
			result.append("&nbsp;&nbsp;");
		}

		try {
			this.pageContext.getOut().write(result.toString());
			return 2;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer();

		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}

			if (!this.nopadding) {
				result.append("&nbsp;&nbsp;");
			}

			if (!this.disabled) {
				result.append("</A>");
			}

			result.append("</TD></TR></TABLE>");
			this.pageContext.getOut().write(result.toString());
			if (this.owner != null) {
				this.owner.endNewButton();
			}
		} catch (IOException var6) {
			throw new JspException("IO Error: " + var6.getMessage());
		} finally {
			this.reset();
		}

		return 6;
	}

	public void release() {
		super.release();
		this.reset();
	}

	private void reset() {
		this.onclick = null;
		this.htmlonclick = null;
		this.id = null;
		this.target = null;
		this.disabled = false;
		this.emphasized = false;
		this.regular = true;
		this.small = false;
		this.nopadding = false;
		this.hidden = false;
		this.owner = null;
		this.styleExtra = "";
		this.styleClassname = null;
	}
}