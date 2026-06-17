/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.renderers.puncto;

import com.scplatform.testing.webui.jtags.TagModel;
import com.scplatform.testing.webui.jtags.TagModelList;
import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.toolbar.ToolbarButton;
import com.scplatform.testing.webui.taglib.Settings;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;

public class ToolbarTagRenderer implements TagRenderer {
	public void render(TagModel model, BodyContent tagBody, JspWriter out, Settings settings) throws JspException {
		TagModelList buttons = (TagModelList) model;
		String imgDir = settings.getImageDirectory();

		try {
			out.write("<table><tr>");

			for (int i = 0; i < buttons.size(); ++i) {
				ToolbarButton button = (ToolbarButton) buttons.get(i);
				out.write("<td width=\"16px\" height=\"16px\" align=\"right\">");
				if (!button.isDisabled()) {
					out.write("<a href=\"");
					out.write(button.getOnClick());
					out.write("\"");
					if (button.getTarget() != null) {
						out.write(" target=\"");
						out.write(button.getTarget());
						out.write("\"");
					}

					if (button.getTooltip() != null) {
						out.write(" onmouseover=\"self.status='");
						out.write(button.getTooltip());
						out.write("';return true;\"");
					}

					out.write(">");
				}

				out.write("<img src=\"");
				out.write(imgDir);
				out.write("/");
				out.write(button.getImage());
				if (button.getTooltip() != null) {
					out.write("\" alt=\"");
					out.write(button.getTooltip());
				}

				out.write("\" border=\"0px\" width=\"16px\" height=\"16px\">");
				if (button.getTarget() != null) {
					out.write("</a>");
				}

				out.write("</td>");
			}

			out.write("</tr></table>");
		} catch (Exception var9) {
			throw new JspException(var9.getMessage());
		}
	}

	public String toString() {
		return "Puncto toolbar tag renderer";
	}
}