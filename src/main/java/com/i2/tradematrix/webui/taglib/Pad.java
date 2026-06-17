/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

public class Pad extends Container {
	Settings settings;
	String editaction = "";
	String name = null;
	String title = null;
	String padType = "application";
	Container container;
	int nameCount = 1;
	String collapsable = "yes";

	public void setCollapsable(String value) {
		this.collapsable = value;
	}

	public void setTitle(String value) {
		this.title = value;
	}

	public void setName(String value) {
		this.name = value;
	}

	public void setOnedit(String value) {
		this.editaction = value;
	}

	public String getName() {
		return this.name;
	}

	public String buildName() {
		String itemName = this.name + "_" + this.nameCount;
		++this.nameCount;
		return itemName;
	}

	public void setType(String value) {
		this.padType = value;
	}

	public String getType() {
		return this.padType;
	}

	public String getItemColor(boolean bTopLevel, boolean bSelected) {
		if (bSelected) {
			return this.padType.equals("solution") ? "#e6e6e6" : "#fff274";
		} else if (this.padType.equals("solution")) {
			return "#ffffff";
		} else {
			return bTopLevel ? "#e4e6f5" : "#f7f8fd";
		}
	}

	public int doStartTag() throws JspException {
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		super.setTitle(this.title);
		super.setWidth("100%");
		if (this.padType.equals("doclib")) {
			super.setCollapsable("no");
		} else {
			super.setCollapsable(this.collapsable);
		}

		super.setScrollable("yes");
		super.setPadUse(this.padType);
		super.setId(this.name);
		if (this.editaction.length() > 0) {
			super.setComplexHeader("<a href=\"" + this.editaction + "\"><IMG border=\"0px\" src=\""
					+ this.settings.getImageDirectory() + "/cstmz_actv.png\"></a>");
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		super.doEndTag();
		return 6;
	}

	public void release() {
		super.release();
		this.settings = null;
		this.container = null;
		this.title = null;
		this.editaction = "";
		this.padType = "application";
		this.nameCount = 1;
	}
}