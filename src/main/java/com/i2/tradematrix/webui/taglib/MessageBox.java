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

public class MessageBox extends BodyTagSupport {
	String icontype = null;
	String interaction = null;

	public void setIcontype(String value) {
		if (value.toUpperCase().equals("WARN")) {
			this.icontype = "/alert_static.png";
		} else {
			this.icontype = "/alert_green_static.png";
		}

	}

	public void setInteraction(String value) {
		this.interaction = value.toUpperCase();
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		result.append("<table class=\"messageBoxBackground\" width=\"100%\" height=\"100%\">");
		result.append("<tr height=\"100%\"><td valign=\"top\" style=\"padding:8px 16px\">");
		result.append("<img src=\"" + settings.getImageDirectory() + this.icontype + "\" border=\"0px\">");
		result.append("</td><td width=\"100%\" valign=\"top\" style=\"padding:8px 8px 8px 0px\">");

		try {
			this.pageContext.getOut().write(result.toString());
			return 2;
		} catch (IOException var4) {
			throw new JspException(var4.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		StringBuffer result = new StringBuffer();
		result.append("</td></tr>");
		result.append("<tr><td colspan=\"2px\" style=\"padding:4px\">");
		result.append("<table cellpadding=\"4px\">");
		result.append("<tr>");
		result.append("<td width=\"100%\">&nbsp;</td>");
		if (this.interaction.indexOf("CANCEL") != -1) {
			result.append("<td>");
			result.append("<div style=\"border:1px solid #505050\">");
			result.append("<button id=\"buttonRegular\" onclick=\"javascript:i2uiCloseMessageBox('cancel')\">&nbsp;"
					+ Utils.translate(settings.getLocale().toString(), "Cancel") + "&nbsp;</button>");
			result.append("</div>");
			result.append("</td>");
			if (this.interaction.indexOf("NO") != -1) {
				result.append("<TD nowrap=\"yes\">");
				result.append("<IMG src=\"" + settings.getImageDirectory() + "/blue_divider.png\">");
				result.append("</TD>");
			}
		}

		if (this.interaction.indexOf("NO") != -1) {
			result.append("<td>");
			result.append("<div style=\"border:1px solid #505050\">");
			result.append("<button id=\"buttonRegular\" onclick=\"javascript:i2uiCloseMessageBox('no')\">&nbsp;"
					+ Utils.translate(settings.getLocale().toString(), "No") + "&nbsp;</button>");
			result.append("</div>");
			result.append("</td>");
		}

		if (this.interaction.indexOf("YES") != -1) {
			result.append("<td>");
			result.append("<div style=\"border:1px solid #505050\">");
			result.append("<button id=\"buttonRegular\" onclick=\"javascript:i2uiCloseMessageBox('yes')\">&nbsp;"
					+ Utils.translate(settings.getLocale().toString(), "Yes") + "&nbsp;</button>");
			result.append("</div>");
			result.append("</td>");
		}

		if (this.interaction.indexOf("OK") != -1) {
			result.append("<td>");
			result.append("<div style=\"border:1px solid #505050\">");
			result.append("<button id=\"buttonRegular\" onclick=\"javascript:i2uiCloseMessageBox('ok')\">&nbsp;"
					+ Utils.translate(settings.getLocale().toString(), "OK") + "&nbsp;</button>");
			result.append("</div>");
			result.append("</td>");
		}

		result.append("</tr>");
		result.append("</table>");
		result.append("</td></tr></table>");

		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}

			this.pageContext.getOut().write(result.toString());
			return 6;
		} catch (IOException var4) {
			throw new JspException(var4.getMessage());
		}
	}

	public void release() {
		super.release();
		this.interaction = null;
		this.icontype = null;
	}
}