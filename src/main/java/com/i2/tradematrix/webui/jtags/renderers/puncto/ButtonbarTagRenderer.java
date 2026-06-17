/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.renderers.puncto;

import com.scplatform.testing.webui.jtags.TagModel;
import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.TagRendererFactory;
import com.scplatform.testing.webui.jtags.button.Buttonbar;
import com.scplatform.testing.webui.jtags.button.ButtonbarDivider;
import com.scplatform.testing.webui.taglib.Settings;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;

public class ButtonbarTagRenderer implements TagRenderer {
	public void render(TagModel model, BodyContent tagBody, JspWriter out, Settings settings) throws JspException {
		if (model.isVisible()) {
			Buttonbar buttonbar = (Buttonbar) model;
			String theAlignment = buttonbar.getAlignment();

			try {
				out.write("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\"");
				if (buttonbar.isStandalone()) {
					out.write(" width=\"100%\"");
				}

				out.write("><tr>");
				if ("right".equals(theAlignment)) {
					out.write("<td width=\"100%\" nowrap=\"yes\">&#160;</td>");
				}

				boolean previousIsDivider = false;
				TagRenderer dividerRenderer = TagRendererFactory.getRenderer("buttonbardivider");
				TagRenderer buttonRenderer = TagRendererFactory.getRenderer("button");

				for (int i = 0; i < buttonbar.size(); ++i) {
					if (i > 1 && !previousIsDivider && buttonbar.isStandalone()) {
						out.write("<td width=\"6px\" style=\"font-size:1px\" nowrap=\"yes\">&nbsp;</td>");
					}

					previousIsDivider = false;
					TagModel tagModel = buttonbar.get(i);
					if (tagModel instanceof ButtonbarDivider) {
						previousIsDivider = true;
						dividerRenderer.render(tagModel, (BodyContent) null, out, settings);
					} else {
						out.write("<td nowrap=\"yes\">");
						buttonRenderer.render(tagModel, (BodyContent) null, out, settings);
						out.write("</td>");
					}
				}

				if (("left".equals(theAlignment) || buttonbar.isPadded()) && buttonbar.isStandalone()) {
					out.write("<td width=\"100%\" nowrap=\"yes\">&#160;</td>");
				}

				if ("right".equals(theAlignment) && buttonbar.isPadded()) {
					out.write("<td width=\"1px\" style=\"font-size:1px\" nowrap=\"yes\">&#160;</td>");
				}

				out.write("</tr></table>");
			} catch (Exception var12) {
				throw new JspException(var12.getMessage());
			}
		}
	}

	public String toString() {
		return "Puncto buttonbar tag renderer";
	}
}