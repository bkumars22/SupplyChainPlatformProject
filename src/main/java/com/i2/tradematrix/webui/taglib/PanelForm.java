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

public class PanelForm extends BodyTagSupport {
	Settings settings;

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		result.append("<table border=\"0px\" height=\"100%\" width=\"100%\" cellpadding=\"0px\" cellspacing=\"0px\">");
		result.append("<tr>");
		result.append("<td width=\"134px\" style=\"background-repeat:y-report;background-image:url("
				+ this.settings.getImageDirectory() + "/login_panel_filler.jpg)\" valign=\"top\">");
		result.append("<img hspace=\"0px\" vspace=\"0px\" src=\"" + this.settings.getImageDirectory()
				+ "/login_panel_top.jpg\">");
		result.append("</td>");
		result.append("<td rowspan=\"2\" width=\"20px\">&nbsp;</td>");
		result.append("<td valign=\"top\" rowspan=\"2\" width=\"100%\">");

		try {
			this.pageContext.getOut().write(result.toString());
			return 2;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer();
		result.append("</td>");
		result.append("</tr>");
		result.append("<tr>");
		result.append("<td width=\"134px\" height=\"100px\" style=\"background-repeat:y-report;background-image:url("
				+ this.settings.getImageDirectory() + "/login_panel_filler.jpg)\" valign=\"bottom\">");
		result.append("<img hspace=\"0px\" vspace=\"0px\" src=\"" + this.settings.getImageDirectory()
				+ "/login_panel_bottom.jpg\">");
		result.append("</td>");
		result.append("</tr>");
		result.append("</table>");

		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
				this.pageContext.getOut().write(result.toString());
			}

			return 6;
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}
	}
}