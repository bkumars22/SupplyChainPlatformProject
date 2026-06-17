/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.renderers.puncto;

import com.scplatform.testing.webui.jtags.BaseClickableTagModel;
import com.scplatform.testing.webui.jtags.TagModel;
import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.breadcrumbs.Breadcrumb;
import com.scplatform.testing.webui.jtags.breadcrumbs.BreadcrumbList;
import com.scplatform.testing.webui.taglib.Settings;
import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;

public class BreadcrumbsTagRenderer implements TagRenderer {
	public void render(TagModel model, BodyContent tagBody, JspWriter out, Settings settings) throws JspException {
		try {
			BreadcrumbList crumbs = (BreadcrumbList) model;
			int clipWidth = 400;
			int contentWidth = 1000;
			int height = 20;
			out.write("<script language=\"JavaScript1.2\">breadcrumbs=new i2uiBreadcrumbs();");
			if (crumbs.getAutoinit()) {
				out.write("window.onload=i2uiSetBreadcrumbsWidth;");
			}

			out.write("</script>");
			out.write("<table width=\"50%\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\"><tbody><tr>");
			out.write("<td nowrap align=\"left\" class=\"applicationHeader\" valign=\"top\">");
			out.write(crumbs.getApplicationName());
			out.write(":&nbsp;");
			out.write("<td valign=\"top\" class=\"breadcrumbsScrollers\">");
			out.write("<a href=\"javascript:breadcrumbs.scrollHorizontal(0);\">&lt;&lt;&nbsp;</a></td>");
			out.write("<td valign=\"top\" class=\"breadcrumbs\" nowrap>");
			out.write("<div id=\"breadcrumbsContainer\" style=\"position:relative;width:");
			out.print(clipWidth);
			out.write("px;height:");
			out.print(height);
			out.write("px;overflow:hidden;border:0px\">");
			out.write("<div id=\"breadcrumbsContent\" style=\"position:relative;width:");
			out.print(contentWidth);
			out.write("px;left:0px;top:0px\">");
			Breadcrumb crumb = null;
			int initialOffset = 0;

			for (int i = 0; i < crumbs.size() - 1; ++i) {
				crumb = (Breadcrumb) crumbs.get(i);
				out.write(BaseClickableTagModel.getTextLink(crumb, true));
				out.write("&nbsp;&gt;&nbsp;");
				initialOffset += crumb.getLabel().length() + 3;
			}

			crumb = (Breadcrumb) crumbs.get(crumbs.size() - 1);
			out.write("<b>");
			out.write(crumb.getLabel());
			out.write("</b>");
			initialOffset += crumb.getLabel().length();
			initialOffset *= 7;
			if (initialOffset > clipWidth) {
				initialOffset -= clipWidth;
			} else {
				initialOffset = 0;
			}

			out.write("</div></div>");
			out.write("<script language=\"JavaScript1.2\">breadcrumbs.init();initialBreadcrumbsOffset=");
			out.print(initialOffset);
			out.write(";</script>");
			out.write("</td><td class=\"breadcrumbsScrollers\" valign=\"top\">");
			out.write(
					"<a href=\"javascript:breadcrumbs.scrollHorizontal(1)\">&nbsp;&gt;&gt;</a></td></tr></tbody></table>");
		} catch (IOException var12) {
			throw new JspException(var12.getMessage());
		}
	}

	public String toString() {
		return "Puncto breadcrumbs tag renderer";
	}
}