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

public class Shell extends TagSupport {
	String logo = null;
	String frontLogo = null;
	String background = null;
	String username = null;
	String actions = null;
	String contenturl = null;
	String onresize = "";
	String onresize2 = "";
	String onload = "";
	String onload2 = null;
	boolean framed = false;
	Settings settings;

	public void setLogo(String value) {
		this.logo = value;
	}

	public void setFrontlogo(String value) {
		this.frontLogo = value;
	}

	public void setBackground(String value) {
		this.background = value;
	}

	public void setUsername(String value) {
		this.username = value;
	}

	public void setActions(String value) {
		this.actions = value;
	}

	public void setOnresize(String value) {
		this.onresize = "onresize=\"" + value + "\" ";
		this.onresize2 = value + ";";
	}

	public void setOnload(String value) {
		this.onload = "onload=\"" + value + "\" ";
		this.onload2 = value + ";";
	}

	public void setContenturl(String value) {
		this.contenturl = value;
	}

	public void setFramed(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.framed = true;
		}

	}

	public int doStartTag() throws JspException {
		StringBuffer result1 = new StringBuffer();
		StringBuffer result2 = new StringBuffer();
		if (this.actions == null) {
			this.actions = "";
		}

		if (this.username == null) {
			this.username = "";
		}

		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		String css = this.settings.getDefaultCSSStyleSheet();
		if (this.framed) {
			this.onload = "";
			this.onresize = "";
			result1.append("<script language=\"javascript\">");
			result1.append(" function i2ui_shell_init(action){");
			result1.append(" var content;");
			result1.append(" content='");
			if (css != null) {
				result1.append("<HEAD><LINK rel=\"STYLESHEET\" type=\"text/css\" href=\"" + css + "\"></HEAD>");
			}
		}

		result1.append("<BODY " + this.onload + " " + this.onresize
				+ " topmargin=\"0px\" leftmargin=\"0px\" marginwidth=\"0px\" marginheight=\"0px\" class=\"shellBody\"");
		if (this.background != null) {
			result1.append(" background=\"" + this.background + "\"");
		}

		result1.append(">");
		String tdText = "&nbsp;";
		if (this.frontLogo != null) {
			tdText = "<img src=\"" + this.frontLogo + "\" border=\"0px\">";
		}

		result1.append(
				"<TABLE width=\"100%\" height=\"100%\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" class=\"shellBackground\"><TR height=\"40px\"><TD nowrap=\"yes\" colspan=\"3\"><TABLE id=\"masthead\" width=\"100%\" height=\"100%\" ondblclick=\"javascript:parent.changeBgImg()\" cellpadding=\"0px\" cellspacing=\"0px\" style=\"background-image:url("
						+ this.logo + ");background-repeat:no-repeat;\">" + "<TR height=\"40px\">"
						+ "<TD width=\"100%\">" + tdText + "</TD>"
						+ "<TD id=\"shellUsername\" class=\"shellBannerText\" nowrap=\"yes\">" + this.username + "</TD>"
						+ "<TD>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</TD>"
						+ "<TD id=\"shellActions\"  class=\"shellBannerText\" nowrap=\"yes\"><b>" + this.actions
						+ "</b></TD>" + "<TD>&nbsp;&nbsp;</TD>" + "</TR>" + "</TABLE>" + "</TD>" + "</TR>");
		if (this.framed) {
			result1.append("</TABLE></BODY>';");
			result1.append("i2ui_shell_top.document.open();");
			result1.append("i2ui_shell_top.document.write(content);");
			result1.append("i2ui_shell_top.document.close();");
		}

		try {
			this.pageContext.getOut().write(result1.toString());
			this.pageContext.getOut().write(result2.toString());
			return 1;
		} catch (IOException var6) {
			throw new JspException("IO Error: " + var6.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		StringBuffer result1 = new StringBuffer();
		StringBuffer result2 = new StringBuffer();
		if (this.framed) {
			if (this.onload2 != null) {
				result2.append("if (action=='load') {" + this.onload2 + "}");
			}

			if (this.onresize2 != null) {
				result2.append("if (action=='resize') {" + this.onresize2 + "}");
			}

			result2.append("}");
			result2.append("</script>");
			result2.append(
					"<frameset rows=\"40,*\" marginwidth=\"0\" border=\"0\" frameborder=\"0px\" framespacing=\"0px\" marginheight=\"0px\" onload=\"i2ui_shell_init('load')\" onresize=\"i2ui_shell_init('resize')\">");
			result2.append("<frame src=\"" + this.settings.getJavascriptDirectory()
					+ "/i2uiblank.html\" name=\"i2ui_shell_top\" scrolling=\"no\" frameborder=\"no\" noresize=\"yes\">");
			if (this.contenturl == null) {
				result2.append("<frame src=\"" + this.settings.getJavascriptDirectory()
						+ "/i2uiblank.html\"  name=\"i2ui_shell_content\" scrolling=\"auto\" frameborder=\"no\" noresize=\"yes\">");
			} else {
				result2.append("<frame src=\"" + this.contenturl
						+ "\" name=\"i2ui_shell_content\" scrolling=\"auto\" frameborder=\"no\" noresize=\"yes\">");
			}

			result2.append("</frameset>");
		}

		try {
			this.pageContext.getOut().write(result1.toString());
			this.pageContext.getOut().write(result2.toString());
			return 6;
		} catch (IOException var4) {
			throw new JspException("IO Error: " + var4.getMessage());
		}
	}

	public void release() {
		super.release();
		this.logo = null;
		this.frontLogo = null;
		this.background = null;
		this.username = null;
		this.actions = null;
		this.settings = null;
		this.onresize = "";
		this.onresize2 = "";
		this.onload = "";
		this.onload2 = null;
		this.framed = false;
	}
}