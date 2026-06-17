/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class PopupCell extends BodyTagSupport {
	private static final String TD_START = "<TD NOWRAP=\"yes\" CLASS=\"popupCell\">\n";
	private static final String SPAN_START = "<SPAN";
	private static final String SPAN_END = "</SPAN>\n";
	private static final String A_START = "<A HREF=\"javascript:";
	private static final String IMG_START = "<IMG BORDER=\"0px\" ALIGN=\"right\" SRC=\"";
	private static final String A_END = "</A>\n";
	private static final String TD_END = "</TD>\n";
	private static final String POPUPLINK_CLASS = " CLASS=\"popupLink\"";
	private static final String ONMOUSEOVER = " ONMOUSEOVER=\"i2uiSetMenuCoords(this, event)\"";
	private static final String NS4_SPAN_STYLE = " STYLE=\"float: left\"";
	private static final String TAG_CLOSE = ">\n";
	private static final String POPUP_HEADER_ICON = "/dropdown.png";
	private static final String POPUP_CELL_ICON = "/table_cell_pop_indi.png";
	String popupName_ = null;
	String onClick_ = null;

	public void setPopupname(String value) {
		this.popupName_ = value;
	}

	public void setOnclick(String value) {
		this.onClick_ = value;
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer(255);
		Table table = (Table) findAncestorWithClass(this, Table.class);
		Row row = (Row) findAncestorWithClass(this, Row.class);
		if (table != null && row != null) {
			BodyContent bc = this.getBodyContent();
			Settings settings = table.getSettings();
			result.append("<TD NOWRAP=\"yes\" CLASS=\"popupCell\">\n");
			result.append("<SPAN CLASS=\"popupLink\">\n");
			result.append(bc.getString());
			result.append("</SPAN>\n");
			String imgSrc = settings.getImageDirectory() + (row.header ? "/dropdown.png" : "/table_cell_pop_indi.png");
			String aStart = "<A HREF=\"javascript:";
			if (this.onClick_ != null) {
				aStart = aStart + this.onClick_;
				if (aStart.lastIndexOf(59) != aStart.length() - 1) {
					aStart = aStart + ";";
				}
			}

			aStart = aStart + "i2uiShowMenu('";
			result.append(aStart + this.popupName_ + "')\"");
			result.append(" ONMOUSEOVER=\"i2uiSetMenuCoords(this, event)\">\n");
			result.append("<IMG BORDER=\"0px\" ALIGN=\"right\" SRC=\"" + imgSrc + "\"" + ">\n");
			result.append("</A>\n");
			result.append("</TD>\n");

			try {
				this.pageContext.getOut().write(result.toString());
			} catch (IOException var9) {
				throw new JspException("IO Error: " + var9.getMessage());
			}
		}

		return 6;
	}

	public void release() {
		super.release();
		this.popupName_ = null;
		this.onClick_ = null;
	}
}