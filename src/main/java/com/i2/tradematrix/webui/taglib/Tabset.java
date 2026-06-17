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

public class Tabset extends BodyTagSupport {
	TabbedContainer tabbedcontainer;
	String altText = null;
	boolean bAltText = false;
	String id = null;
	String tabWidth = "6%";
	String onclick = null;
	String tabType = "tab";
	String field = "White";
	Settings settings;
	int tabCount = 0;
	boolean horizontal = true;

	public void setMintabwidth(String value) {
		this.tabWidth = new String(value);
	}

	public boolean ifContainerTabset() {
		return this.tabbedcontainer != null;
	}

	public void setType(String value) {
	}

	public String getType() {
		return this.tabType;
	}

	public void setField(String value) {
		this.field = new String(value);
	}

	public String getField() {
		return this.field.toLowerCase();
	}

	public void setDirection(String value) {
		if (value.toLowerCase().equals("vertical")) {
			this.horizontal = false;
		}

	}

	public boolean isHorizontal() {
		return this.horizontal;
	}

	public void setAltText(String value) {
		this.altText = new String(value);
	}

	public void hasAltText() {
		this.bAltText = true;
	}

	public String getTabWidth() {
		return this.tabWidth;
	}

	public void setId(String value) {
		this.id = new String(value);
	}

	public String getId() {
		return this.id;
	}

	public String getAltText() {
		return this.altText;
	}

	public boolean haveAltText() {
		return this.bAltText;
	}

	public void setOnclick(String value) {
		this.onclick = new String(value);
	}

	public String getOnclick() {
		return this.onclick;
	}

	public Settings getSettings() {
		return this.settings;
	}

	public void startTab() throws JspException {
		if (this.tabCount > 0) {
			try {
				if (this.horizontal) {
					this.pageContext.getOut()
							.write("<TD width=\"3px\" class=\"tabGap\" id=\"tabPane" + this.field + "\">&nbsp;</TD>");
				} else {
					this.pageContext.getOut().write(
							"<TR><TD class=\"tabCorner\">&nbsp;</TD><TD align=\"right\" class=\"tabGapVert\" id=\"tabPane"
									+ this.field + "\">&nbsp;</TD></TR>");
				}
			} catch (IOException var2) {
				throw new JspException("IO Error: " + var2.getMessage());
			}
		}

		++this.tabCount;
	}

	public int doStartTag() throws JspException {
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		this.tabbedcontainer = (TabbedContainer) findAncestorWithClass(this, TabbedContainer.class);

		try {
			if (this.horizontal) {
				this.pageContext.getOut().write("<TABLE class=\"horizontalTabSet\" id=\"" + this.id
						+ "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" width=\"100%\"><TR>");
				this.pageContext.getOut().write("<TD width=\"3px\" class=\"tabScroller\" id=\"tabPane" + this.field
						+ "\"><A href=\"javascript:i2uiScrollTabsRight('" + this.getId()
						+ "')\"><IMG border=\"0px\" id=\"" + this.getId() + "_tabscrollerleft\" src=\""
						+ this.settings.getImageDirectory() + "/arrow_tab_left.png\" style=\"display:none\"></A></TD>");
			} else if (this.tabbedcontainer == null) {
				this.pageContext.getOut().write("<TABLE class=\"verticalTabSetNoContainer\" id=\"" + this.id
						+ "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\">");
			} else {
				this.pageContext.getOut().write("<TABLE class=\"verticalTabSet\" id=\"" + this.id
						+ "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" height=\"100%\"><TR><TD colspan=\"2px\" style=\"line-height:24px;border-right:1px solid #999999;\">&nbsp;</TD></TR>");
			}

			return 2;
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}

		try {
			if (this.horizontal) {
				this.pageContext.getOut()
						.write("<TD width=\"100%\" class=\"tabScroller\" id=\"tabPane" + this.field + "\">&nbsp;</TD>");
				this.pageContext.getOut()
						.write("<TD width=\"3px\" class=\"tabFiller" + this.field
								+ "\"><A href=\"javascript:i2uiScrollTabsLeft('" + this.getId()
								+ "')\"><IMG border=\"0px\" id=\"" + this.getId() + "_tabscrollerright\" src=\""
								+ this.settings.getImageDirectory()
								+ "/arrow_tab_right.png\" style=\"display:none\"></A></TD></TR></TABLE>");
				this.pageContext.getOut().write("<script>var " + this.getId() + "_allowed_width = 0;</script>");
			} else {
				this.pageContext.getOut().write(
						"<TR height=\"100%\"><TD class=\"tabCorner\">&nbsp;</TD><TD align=\"right\" class=\"tabFillerVert"
								+ this.field + "\">&nbsp;</TD></TR></TABLE>");
			}

			if (this.tabbedcontainer != null) {
				this.tabbedcontainer.setHorizontal(this.horizontal);
				this.tabbedcontainer.setTabType(this.tabType);
				this.tabbedcontainer.doTabsetEnd(this.altText, this.id + "_description", this.bAltText);
			}

			return 6;
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public void release() {
		super.release();
		this.tabbedcontainer = null;
		this.altText = null;
		this.id = null;
		this.tabWidth = "8%";
		this.onclick = null;
		this.tabType = "tab";
		this.field = "White";
		this.settings = null;
		this.tabCount = 0;
		this.horizontal = true;
		this.bAltText = false;
	}
}