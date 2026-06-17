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

public class TabbedContainer extends BodyTagSupport implements FooterSupporter {
	boolean collapsable = false;
	String width = null;
	String height = null;
	String id = null;
	boolean scrollable = false;
	boolean indented = false;
	boolean hasTabs = false;
	Settings settings = null;
	String tabType = "tab";
	String complexFooter = null;
	String complexHeader = null;
	boolean horizontal = true;
	String colspan = "";
	boolean hasTable = false;

	public void setIndentcontent(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.indented = true;
		}

	}

	public void setCollapsable(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.collapsable = true;
		}

	}

	public void setScrollable(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.scrollable = true;
		}

	}

	public void setWidth(String value) {
		this.width = value;
	}

	public void setId(String value) {
		this.id = value;
	}

	public void setHeight(String value) {
		this.height = value;
	}

	public void setTabType(String value) {
		this.tabType = value;
	}

	public void setHorizontal(boolean value) {
	}

	public void setComplexFooter(String value) {
		this.complexFooter = value;
	}

	public void setComplexHeader(String value) {
		this.complexHeader = value;
	}

	public void setHasTable() {
		this.hasTable = true;
	}

	public void doTabsetEnd(String altText, String tabsetid, boolean bAltText) throws JspException {
		String classname = "tabContainerHeader";
		String classname2 = "tabContainerHeaderRight";
		String indentedclassname = "";
		StringBuffer result = new StringBuffer();
		int nest = 1;
		if (this.indented) {
			indentedclassname = "Indent";
		}

		if (!bAltText) {
			classname = classname + "Thin";
		}

		if (this.complexHeader != null) {
			nest = 2;
			classname = "tabContainerHeaderLeft";
			if (!this.horizontal) {
				classname = classname + "Vert";
				classname2 = classname2 + "Vert";
			}
		}

		if (this.horizontal) {
			result.append("</TD></TR><TR><TD id=\"" + this.tabType + "Selected\">"
					+ "<TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" width=\"100%\" class=\"shadow\">"
					+ "<TR>");
			if (this.complexHeader != null) {
				result.append("<TD><TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" width=\"100%\"><TR>");
			}

			result.append("<TD ");
			if (this.collapsable) {
				result.append(" style=\"padding:2px;\"");
			}

			result.append(" width=\"100%\" nowrap=\"yes\" class=\"" + classname + "\">&nbsp;");
			if (this.collapsable) {
				if (this.settings.isIE()) {
					result.append("<IMG onclick=\"javascript:i2uiToggleContent(this," + nest
							+ ")\" onMouseOver=\"javascript:this.style.cursor='hand'\" src=\""
							+ this.settings.getImageDirectory() + "/container_collapse.png\">&nbsp;");
				} else {
					result.append("<A href=\"javascript:void i2uiToggleTabNoop();\" onclick=\"i2uiToggleContent(this,"
							+ nest + ")\"><IMG border=\"0\" src=\"" + this.settings.getImageDirectory()
							+ "/container_collapse.png\"></A>&nbsp;");
				}
			}
		} else {
			result.append(
					"</TD><TD valign=\"top\" width=\"100%\"><TABLE cellspacing=\"0px\" cellpadding=\"0px\" width=\"100%\" height=\"100%\"><TR><TD id=\""
							+ this.tabType + "Selected\">"
							+ "<TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" width=\"100%\" class=\"shadow\">"
							+ "<TR>" + "<TD width=\"100%\" nowrap=\"yes\" class=\"" + classname + "\">&nbsp;");
		}

		result.append("<SPAN id=\"" + tabsetid + "\" >");
		if (altText != null) {
			result.append(altText);
		}

		result.append("</SPAN></TD>");
		if (this.complexHeader != null) {
			result.append("<TD align=\"right\" class=\"" + classname2 + "\" id=\"tabSelected\" nowrap=\"yes\">"
					+ this.complexHeader + "</TD>");
			result.append("</TR></TABLE></TD>");
		}

		result.append("</TR><TBODY id=\"_containerbody\"><TR>");
		if (this.horizontal) {
			result.append("<TD" + this.colspan + " class=\"containerBody" + indentedclassname + "\">");
		} else {
			result.append("<TD" + this.colspan + " class=\"containerBodyVert" + indentedclassname + "\">");
		}

		if (this.scrollable) {
			result.append("<DIV ");
			if (this.id != null) {
				result.append("id=\"" + this.id + "_scroller\" ");
			}

			result.append("style=\"height:" + this.height + ";width:" + this.width + ";overflow:scroll\">");
		}

		try {
			this.pageContext.getOut().write(result.toString());
		} catch (IOException var10) {
			throw new JspException("IO Error: " + var10.getMessage());
		}

		this.hasTabs = true;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		if (this.width == null) {
			this.width = "100%";
		}

		if (this.height == null) {
			this.height = "100%";
		}

		result.append("<TABLE ");
		if (this.id != null) {
			result.append("id=\"" + this.id + "\" ");
		}

		result.append("width=\"" + this.width
				+ "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\"><TR width=\"100%\"><TD valign=\"top\">");

		try {
			this.pageContext.getOut().write(result.toString());
		} catch (IOException var3) {
			throw new JspException("IO Error: " + var3.getMessage());
		}

		this.hasTabs = false;
		return 2;
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer();
		if (this.scrollable) {
			result.append("</DIV>");
		}

		result.append("</TD></TR>");
		String classname = "containerFooter";
		if (this.complexFooter == null) {
			classname = classname + "Thin";
			this.complexFooter = "&nbsp;";
		}

		result.append("<TR><TD" + this.colspan + " nowrap=\"yes\" class=\"" + classname + "\" id=\"tabSelected\">"
				+ this.complexFooter + "</TD></TR>");
		result.append("</TBODY>");
		result.append("</TABLE></TD></TR>");
		result.append("</TABLE>");
		if (!this.horizontal) {
			result.append("</TD></TR></TABLE>");
		}

		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}

			this.pageContext.getOut().write(result.toString());
			return 6;
		} catch (IOException var4) {
			throw new JspException("IO Error: " + var4.getMessage());
		}
	}

	public void release() {
		super.release();
		this.width = null;
		this.id = null;
		this.hasTabs = false;
		this.settings = null;
		this.collapsable = false;
		this.indented = false;
		this.tabType = "tab";
		this.complexFooter = null;
		this.complexHeader = null;
		this.horizontal = true;
		this.scrollable = false;
		this.height = null;
		this.colspan = "";
		this.hasTable = false;
	}
}