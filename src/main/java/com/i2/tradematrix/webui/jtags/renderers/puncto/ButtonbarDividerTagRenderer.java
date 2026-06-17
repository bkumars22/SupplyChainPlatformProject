/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.renderers.puncto;

import com.scplatform.testing.webui.jtags.TagModel;
import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.taglib.Settings;
import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;

public class ButtonbarDividerTagRenderer implements TagRenderer {
	public void render(TagModel model, BodyContent tagBody, JspWriter out, Settings settings) throws JspException {
		if (model.isVisible()) {
			try {
				out.write("<td width=\"2px\" style=\"font-size:1px\" nowrap=\"yes\">&nbsp;</td>");
				out.write("<td nowrap=\"yes\">");
				out.write("<img src=\"");
				out.write(settings.getImageDirectory());
				out.write("/blue_divider.png\">");
				out.write("</td>");
				out.write("<td width=\"2px\" style=\"font-size:1px\" nowrap=\"yes\">&nbsp;</td>");
			} catch (IOException var6) {
				throw new JspException(var6.getMessage());
			}
		}
	}

	public String toString() {
		return "Puncto buttonbar divider tag renderer";
	}
}