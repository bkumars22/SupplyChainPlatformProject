/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.renderers.puncto;

import com.scplatform.testing.webui.jtags.TagModel;
import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.navpad.PadItem;
import com.scplatform.testing.webui.jtags.navpad.PadItemTree;
import com.scplatform.testing.webui.jtags.navpad.SimplePadItemTree;
import com.scplatform.testing.webui.taglib.Settings;
import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PadItemTagRenderer implements TagRenderer {
	private static final Logger log = LoggerFactory.getLogger(PadItemTagRenderer.class);

	public void render(TagModel model, BodyContent tagBody, JspWriter out, Settings settings) throws JspException {
		if (model != null && model.isVisible()) {
			PadItemTree padItemTree = (PadItemTree) model;

			for (int i = 0; i < padItemTree.size(); ++i) {
				int indent = 0;
				PadItem padItem = (PadItem) padItemTree.get(i);
				this.renderPadItem(padItemTree, padItem, out, settings, indent, i + 1, true);
			}

		}
	}

	public void renderPadItem(PadItemTree padItemTree, PadItem padItem, JspWriter out, Settings settings, int indent,
			int childCount, boolean topLevel) throws JspException {
		try {
			StringBuffer styleInfo = new StringBuffer();
			String name = padItem.getId();
			boolean isSimplePadItemTree = padItemTree instanceof SimplePadItemTree;
			boolean removeOnClick = isSimplePadItemTree && ((SimplePadItemTree) padItemTree).isRemoveOnClick();
			if (topLevel) {
				styleInfo.append(" class=\"");
				styleInfo.append(this.getPadType(padItemTree));
				styleInfo.append("PadContent0 ");
				padItem.setId(padItemTree.getId() + "_" + childCount);
			} else {
				styleInfo.append(" class=\"");
				styleInfo.append(this.getPadType(padItemTree));
				styleInfo.append("PadContent1 ");
				padItem.setId(padItem.getParent().getId() + "_" + childCount);
			}

			int depth = padItem.getId().length() - padItem.getId().replace("_", "").length() - 1;
			styleInfo.append("PadTRDepth" + depth + "\"");
			String tooltip = padItem.getTooltip();
			String target = padItem.getTarget();
			if (target == null) {
				target = padItemTree.getTarget();
			}

			if (padItem.isSelected()) {
				styleInfo.append(" id=\"");
				styleInfo.append(this.getPadType(padItemTree));
				styleInfo.append("HighlightedPadContent\" ");
			}

			out.write("<tr ");
			out.write(styleInfo.toString());
			out.write(">");
			out.write("<td nowrap=\"yes\" id=\"TREECELL_");
			out.write(padItem.getId());
			if (removeOnClick) {
				out.write("\"");
			} else {
				out.write("\" onclick=\"");
				out.write("javascript:i2uiManagePadTree('");
				out.write(padItemTree.getId());
				out.write("','");
				out.write(padItem.getId());
				out.write("',0,null,null,null,'i2uiTilePads()')\"");
			}

			if (!padItem.isLeaf()) {
				out.write(" class=\"nonLeafLink PadTDDepth" + depth + "\"");
			} else {
				out.write(" class=\"leafLink PadTDDepth" + depth + "\"");
			}

			if (!padItem.isLeaf()) {
				out.write(" onMouseOver=\"this.style.cursor='pointer';\" ");
			}

			out.write(" onMouseOut=\"this.style.cursor='auto';\" >");
			this.indentPadItem(out, indent);
			out.write("<img id=\"TREECELLIMAGE_");
			out.write(padItemTree.getId());
			out.write("_");
			out.write(padItem.getId());
			out.write("\" border=\"0px\" style=\"display:none;\" src=\"");
			out.write(settings.getImageDirectory());
			if (padItem.isLeaf()) {
				out.write("/tree_bullet.png\">");
			} else {
				out.write("/plus_norgie.png\">");
			}

			if (!padItem.isDisabled() && padItem.getOnClick() != null) {
				out.write("<a href=\"");
				out.write(padItem.getOnClick());
				out.write("\"");
				if (target != null) {
					out.write(" target=\"");
					out.write(target);
					out.write("\"");
				}

				if (tooltip != null) {
					out.write(" title=\"");
					out.write(tooltip);
					out.write("\"");
				}

				if (name != null) {
					out.write(" name=\"");
					out.write(name);
					out.write("\"");
				}

				if (padItem.isLeaf()) {
					out.write(" class=\"leafLink\" ");
				}

				if (removeOnClick) {
					out.write(">");
				} else {
					out.write(" onclick=\"javascript:i2uiHighlightPadItem('TREECELL_");
					out.write(padItem.getId());
					out.write("')\">");
				}
			}

			if (padItem.isDisabled()) {
				out.write("<span class=\"linkDisabled\">");
			}

			out.write("&nbsp;");
			out.write(padItem.getLabel());
			if (padItem.isDisabled()) {
				out.write("</span>");
			}

			if (!padItem.isDisabled() && padItem.getOnClick() != null) {
				out.write("</a>");
			}

			out.write("</td></tr>");
			out.println();
			if (!padItem.isLeaf()) {
				++indent;

				for (int i = 0; i < padItem.size(); ++i) {
					PadItem childPadItem = (PadItem) padItem.get(i);
					this.renderPadItem(padItemTree, childPadItem, out, settings, indent, i + 1, false);
				}
			}

		} catch (IOException var17) {
			throw new JspException(var17.getMessage());
		}
	}

	public String getItemColor(PadItemTree padItemTree, PadItem padItem, boolean topLevel) {
		if (padItem.isSelected()) {
			return padItemTree.isApplication() ? "#fff274" : "#e6e6e6";
		} else if (padItemTree.isApplication()) {
			return topLevel ? "#e4e6f5" : "#f7f8fd";
		} else {
			return "#ffffff";
		}
	}

	public String getPadType(PadItemTree padItemTree) {
		return padItemTree.isApplication() ? "application" : "solution";
	}

	public void indentPadItem(JspWriter out, int indent) throws IOException {
		out.write("&nbsp;");

		for (int i = 0; i < indent; ++i) {
			out.write("&nbsp;&nbsp;");
		}

	}

	public String toString() {
		return "Puncto navpaditem tag renderer";
	}
}