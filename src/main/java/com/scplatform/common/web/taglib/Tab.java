/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import com.scplatform.testing.webui.taglib.Tabset;
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

	public void setAlttext(String arg0) {
		this.alttext = new String(arg0);
	}

	public void setName(String arg0) {
		this.name = new String(arg0);
	}

	public void setOnclick(String arg0) {
		this.onclick = new String(arg0);
	}

	public void setTarget(String arg0) {
		this.target = new String(arg0);
	}

	public void setSelected(String arg0) {
		if (arg0.toLowerCase().equals("yes")) {
			this.selected = new String("Selected");
			this.isselected = true;
		}

	}

	public void setForm(String arg0) {
		if (this.tabset != null && this.tabset.getType().equals("powerTab")) {
			this.tabform = arg0;
		}

	}

	public int doAfterBody() throws JspException {
		return 0;
	}

	public int doStartTag() throws JspException {
		StringBuffer arg0 = new StringBuffer();
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
				arg0.append("<TR>");
			}

			if (this.tabset.isHorizontal()) {
				arg0.append("<TD id=\"" + this.tabclass + "\" width=\""
						+ this.tabset.getTabWidth() + "\"");
			} else {
				arg0.append("<TD id=\"" + this.tabclass2 + "2\" rowspan=\"2\"");
			}

			arg0.append(" class=\"tabText\" nowrap=\"yes\" valign=\"middle\">");
			if (this.tabsetonclick == null) {
				arg0.append("<A id=\"" + this.tabclass + "\" class=\"tabText\""
						+ " href=\"" + this.onclick + "\""
						+ " onclick=\"javascript:i2uiToggle");
				if (!this.tabset.isHorizontal()) {
					arg0.append("Vertical");
				}

				arg0.append("Tab(\'" + this.tabset.getId() + "\',&quot;"
						+ this.alttext + "&quot;,this)\"");
			} else {
				arg0.append("<A id=\""
						+ this.tabclass
						+ "\" class=\"tabText\" "
						+ " onmouseover=\"javascript:this.style.cursor=\'pointer\';\""
						+ " onclick=\"javascript:if (" + this.tabsetonclick
						+ ") {i2uiToggle");
				if (!this.tabset.isHorizontal()) {
					arg0.append("Vertical");
				}

				arg0.append("Tab(\'" + this.tabset.getId() + "\',&quot;"
						+ this.alttext + "&quot;,this); " + this.onclick
						+ "}\"");
			}

			if (this.name != null) {
				arg0.append(" name=\"" + this.name + "\"");
			}

			if (this.target != null) {
				arg0.append(" target=\"" + this.target + "\"");
			}

			arg0.append(">&nbsp;");

			try {
				this.pageContext.getOut().write(arg0.toString());
			} catch (IOException arg2) {
				throw new JspException("IO Error: " + arg2.getMessage());
			}
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		StringBuffer arg0 = new StringBuffer();

		try {
			if (this.bodyContent != null) {
				this.bodyContent
						.writeOut(this.bodyContent.getEnclosingWriter());
			}
		} catch (IOException arg3) {
			throw new JspException(arg3.getMessage());
		}

		arg0.append("&nbsp;</A>");
		if (this.tabform != null) {
			arg0.append(this.tabform);
		}

		arg0.append("</TD>");
		if (!this.tabset.isHorizontal()) {
			arg0.append("</TR>");
		}

		try {
			this.pageContext.getOut().write(arg0.toString());
		} catch (IOException arg2) {
			throw new JspException("IO Error: " + arg2.getMessage());
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