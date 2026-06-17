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

public class PadItem extends BodyTagSupport {
	String onclick = null;
	String text = null;
	String target = null;
	String tooltip = null;
	boolean disabled = false;
	boolean bSelected = false;
	Settings settings;
	String name;
	int nameCount = 1;
	String indent;

	public void setOnclick(String value) {
		this.onclick = value;
	}

	public void setTarget(String value) {
		this.target = value;
	}

	public void setText(String value) {
		this.text = value;
	}

	public void setTooltip(String value) {
		this.tooltip = value;
	}

	public void setSelected(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.bSelected = true;
		}

	}

	public void setDisabled(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.disabled = true;
		}

	}

	public String buildName() {
		String itemName = this.name + "_" + this.nameCount;
		++this.nameCount;
		return itemName;
	}

	public String getIndent() {
		return this.indent;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		Pad pad = (Pad) findAncestorWithClass(this, Pad.class);
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		String styleinfo;
		if (this.getParent() == pad) {
			this.indent = "&nbsp;";
			this.name = pad.buildName();
			styleinfo = " class=\"" + pad.getType() + "PadContent0\"";
		} else {
			this.indent = ((PadItem) this.getParent()).getIndent() + "&nbsp;&nbsp;";
			this.name = ((PadItem) this.getParent()).buildName();
			styleinfo = " class=\"" + pad.getType() + "PadContent1\"";
		}

		if (this.bSelected) {
			styleinfo = styleinfo + " id=\"" + pad.getType() + "HighlightedPadContent\" ";
		}

		result.append("<TR " + styleinfo + ">");
		result.append("<TD nowrap=\"yes\" id=\"TREECELL_" + this.name + "\">");
		result.append(this.indent + "<a href=\"javascript:i2uiManagePadTree('" + pad.getName() + "','" + this.name
				+ "',0,null,null,null,'i2uiTilePads()')\">");
		result.append("<img id=\"TREECELLIMAGE_" + pad.getName() + "_" + this.name + "\" border=\"0px\" src=\""
				+ this.settings.getImageDirectory() + "/plus_norgie.png\"></a>");
		if (this.onclick != null && !this.disabled) {
			result.append("<a href=\"" + this.onclick + "\"");
			if (this.target != null) {
				result.append(" target=\"" + this.target + "\"");
			}

			if (this.tooltip != null) {
				result.append(" title=\"" + this.tooltip + "\"");
			}

			result.append(" onclick=\"javascript:i2uiHighlightPadItem('TREECELL_" + this.name + "','" + pad.getType()
					+ "')\"");
			result.append(">");
		}

		if (this.disabled) {
			result.append("<SPAN class=\"linkDisabled\">");
		}

		result.append("&nbsp;" + this.text);
		if (this.disabled) {
			result.append("</SPAN>");
		}

		if (this.onclick != null && !this.disabled) {
			result.append("</a>");
		}

		result.append("</td></tr>");

		try {
			this.pageContext.getOut().write(result.toString());
			return 2;
		} catch (IOException var5) {
			throw new JspException("IO Error: " + var5.getMessage());
		}
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

		try {
			if (this.bSelected) {
				Pad pad = (Pad) findAncestorWithClass(this, Pad.class);
				if (pad != null && !pad.getType().equals("solution")) {
					result.append("<script>i2uiHighlightPadItem('TREECELL_" + this.name + "','" + pad.getType()
							+ "');</script>");
				}
			}

			this.pageContext.getOut().write(result.toString());
			return 6;
		} catch (IOException var3) {
			throw new JspException("IO Error: " + var3.getMessage());
		}
	}

	public void release() {
		super.release();
		this.onclick = null;
		this.text = null;
		this.target = null;
		this.indent = null;
		this.bSelected = false;
		this.disabled = false;
		this.nameCount = 1;
	}
}