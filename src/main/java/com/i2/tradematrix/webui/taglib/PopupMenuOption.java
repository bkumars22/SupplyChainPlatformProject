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

public class PopupMenuOption extends TagSupport {
	String text = null;
	String url = null;
	boolean disabled = false;

	public void setText(String value) {
		this.text = value;
	}

	public void setUrl(String value) {
		this.url = value;
	}

	public void setDisabled(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.disabled = true;
		}

	}

	public int doStartTag() throws JspException {
		PopupMenu owner = (PopupMenu) findAncestorWithClass(this, PopupMenu.class);
		String ownerid = null;
		String id = null;
		StringBuffer result = new StringBuffer();
		int at = this.url.indexOf("i2uiShowSubMenu(");
		int colspan = 2;
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		boolean isRemoveOnClick = false;
		if (owner != null) {
			ownerid = owner.getName();
			id = owner.getChildName();
			isRemoveOnClick = owner.isRemoveOnClick();
		}

		result.append("<TR class=\"menuUnhighlighted\">");
		if (at != -1) {
			colspan = 1;
		}

		if (isRemoveOnClick) {
			result.append("<TD colspan=\"" + colspan + "\" nowrap=\"yes\" class=\"menuText\" id=\"" + id + "\">");
		} else {
			result.append("<TD colspan=\"" + colspan + "\" nowrap=\"yes\" class=\"menuText\">");
		}

		if (!this.disabled) {
			result.append("<A");
			if (!isRemoveOnClick) {
				if (at == -1) {
					result.append(" onmouseover=\"i2uiHighlightMenuOption(this,'Highlighted','" + id + "','" + ownerid
							+ "')\"");
					result.append(" onclick=\"i2uiHideMenu()\"");
				} else {
					result.append(" onmouseover=\"i2uiHighlightMenuOption(this,'Highlighted','" + id + "','" + ownerid
							+ "');\"");
					result.append(" onclick=\"i2uiSetSubMenuCoords(this, event); \"");
				}

				result.append(
						" onmouseout=\"i2uiHighlightMenuOption(this,'Unhighlighted','" + id + "','" + ownerid + "')\"");
			}

			result.append(" href=\"" + this.url + "\">");
		}

		result.append("&nbsp;&nbsp;" + this.text + "&nbsp;&nbsp;");
		if (!this.disabled) {
			result.append("</A>");
		}

		result.append("</TD>");
		if (at != -1) {
			result.append("<TD><IMG src=\"" + settings.getImageDirectory() + "/nested_menu.png\"></TD>");
		}

		result.append("</TR>");

		try {
			this.pageContext.getOut().write(result.toString());
			return 1;
		} catch (IOException var10) {
			throw new JspException("IO Error: " + var10.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		return 6;
	}

	public void release() {
		super.release();
		this.text = null;
		this.url = null;
		this.disabled = false;
	}
}