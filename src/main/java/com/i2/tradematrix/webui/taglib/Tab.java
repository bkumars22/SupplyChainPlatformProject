/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Tab extends BodyTagSupport {
	String alttext = null;
	String onclick = null;
	String target = null;
	String selected = new String("UnSelected");
	String name = null;
	boolean isselected = false;
	String tabclass = null;
	String tabclass2 = null;
	String tabsetonclick = null;
	String tabform = null;
	Tabset tabset;

	public void setAlttext(String value) {
		this.alttext = new String(value);
	}

	public void setName(String value) {
		this.name = new String(value);
	}

	public void setOnclick(String value) {
		this.onclick = new String(value);
	}

	public void setTarget(String value) {
		this.target = new String(value);
	}

	public void setSelected(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.selected = new String("Selected");
			this.isselected = true;
		}

	}

	public void setForm(String value) {
		if (this.tabset != null && this.tabset.getType().equals("powerTab")) {
			this.tabform = value;
		}

	}

	public int doAfterBody() throws JspException {
		return 0;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		this.tabset = (Tabset) findAncestorWithClass(this, Tabset.class);
		if (this.tabset != null) {
			this.tabset.startTab();
			this.tabclass = this.tabset.getType() + this.selected;
			this.tabclass2 = this.tabset.getType() + this.selected;
			if (!this.tabset.isHorizontal()) {
				this.tabclass2 = this.tabclass2 + "Vert";
				if (this.selected.equals("Selected")) {
					this.tabclass = this.tabclass + "Vert";
				}
			}

			this.tabsetonclick = this.tabset.getOnclick();
			if (this.tabset.ifContainerTabset() && this.alttext != null) {
				this.tabset.hasAltText();
				if (this.selected.equals("Selected")) {
					this.tabset.setAltText(this.alttext);
				}
			}

			if (!this.tabset.isHorizontal()) {
				result.append("<TR>");
			}

			if (this.tabset.isHorizontal()) {
				result.append("<TD id=\"" + this.tabclass + "\" width=\"" + this.tabset.getTabWidth() + "\"");
			} else {
				result.append("<TD id=\"" + this.tabclass2 + "2\" rowspan=\"2\"");
			}

			result.append(" class=\"tabText\" nowrap=\"yes\" valign=\"middle\">");
			if (this.tabsetonclick == null) {
				result.append("<A id=\"" + this.tabclass + "\" class=\"tabText\"" + " href=\"" + this.onclick + "\""
						+ " onclick=\"javascript:i2uiToggle");
				if (!this.tabset.isHorizontal()) {
					result.append("Vertical");
				}

				result.append("Tab('" + this.tabset.getId() + "',&quot;" + this.alttext + "&quot;,this)\"");
			} else {
				result.append("<A id=\"" + this.tabclass + "\" class=\"tabText\" "
						+ " onmouseover=\"javascript:this.style.cursor='pointer';\"" + " onclick=\"javascript:if ("
						+ this.tabsetonclick + ") {i2uiToggle");
				if (!this.tabset.isHorizontal()) {
					result.append("Vertical");
				}

				result.append("Tab('" + this.tabset.getId() + "',&quot;" + this.alttext + "&quot;,this); "
						+ this.onclick + "}\"");
			}

			if (this.name != null) {
				result.append(" name=\"" + this.name + "\"");
			}

			if (this.target != null) {
				result.append(" target=\"" + this.target + "\"");
			}

			result.append(">&nbsp;");

			try {
				this.pageContext.getOut().write(result.toString());
			} catch (IOException var3) {
				throw new JspException("IO Error: " + var3.getMessage());
			}
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer();

		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}
		} catch (IOException var4) {
			throw new JspException(var4.getMessage());
		}

		result.append("&nbsp;</A>");
		if (this.tabform != null) {
			result.append(this.tabform);
		}

		result.append("</TD>");
		if (!this.tabset.isHorizontal()) {
			result.append("</TR>");
		}

		try {
			this.pageContext.getOut().write(result.toString());
		} catch (IOException var3) {
			throw new JspException("IO Error: " + var3.getMessage());
		}

		this.reset();
		return 6;
	}

	public void release() {
		super.release();
		this.reset();
	}

	public void reset() {
		this.alttext = null;
		this.onclick = null;
		this.tabsetonclick = null;
		this.target = null;
		this.selected = new String("UnSelected");
		this.tabclass = null;
		this.tabform = null;
		this.name = null;
		this.isselected = false;
		this.tabset = null;
	}
}