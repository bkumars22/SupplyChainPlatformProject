/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.renderers.puncto;

import com.scplatform.testing.webui.jtags.TagModel;
import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.button.Button;
import com.scplatform.testing.webui.taglib.Settings;
import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;

public class ButtonTagRenderer implements TagRenderer {
	public void render(TagModel model, BodyContent tagBody, JspWriter out, Settings settings) throws JspException {
		if (model.isVisible()) {
			try {
				Button button = (Button) model;
				StringBuffer styleClassname = new StringBuffer("button");
				String styleExtra = "";
				if (button.isSmall()) {
					styleClassname.append("Small");
				}

				if (button.isDisabled()) {
					styleClassname.append("Disabled");
					styleExtra = "Disabled";
				} else if (button.isEmphasized()) {
					styleClassname.append("Emphasized");
					styleExtra = "Emphasized";
				} else {
					styleClassname.append("Regular");
				}

				out.write("<table ");
				if (button.getId() != null) {
					out.write("id=\"");
					out.write(button.getId());
					out.write("\" ");
				}

				out.write("cellspacing=\"1\" cellpadding=\"0\" class=\"buttonBorder");
				out.write(styleExtra);
				out.write("\"><tr><td id=\"");
				out.write(styleClassname.toString());
				out.write("\" nowrap=\"yes\" class=\"buttonText");
				out.write(styleExtra);
				out.write("\">");
				if (!button.isDisabled()) {
					out.write("<a href=\"");
					out.write(button.getOnClick());
					out.write("\"");
					if (button.getTarget() != null) {
						out.write(" target=\"");
						out.write(button.getTarget());
						out.write("\"");
					}

					if (button.getOnMouseOver() != null) {
						out.write(" onmouseover=\"");
						out.write(button.getOnMouseOver());
						out.write("\"");
					}

					out.write(">");
				}

				if (button.isPadded()) {
					out.write("&nbsp;&nbsp;");
				}

				if (button.getLabel() != null) {
					out.write(button.getLabel());
				} else if (tagBody != null) {
					tagBody.writeOut(out);
				}

				if (button.isPadded()) {
					out.write("&nbsp;&nbsp;");
				}

				if (!button.isDisabled()) {
					out.write("</a>");
				}

				out.write("</td></tr></table>");
			} catch (IOException var8) {
				throw new JspException(var8.getMessage());
			}
		}
	}

	public String toString() {
		return "Puncto button tag renderer";
	}
}