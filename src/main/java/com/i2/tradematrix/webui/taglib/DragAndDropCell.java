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

public class DragAndDropCell extends BodyTagSupport {
	String id = null;
	DragAndDropRow row;
	Settings settings;

	public Settings getSettings() {
		return this.settings;
	}

	public void setId(String value) {
		this.id = value;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		this.row = (DragAndDropRow) findAncestorWithClass(this, DragAndDropRow.class);
		if (this.row != null) {
			if (this.row.header) {
				result.append("<td width=\"1%\"");
				if (this.id != null) {
					result.append(" id=\"" + this.id + "\"");
				}

				result.append(">&nbsp;</td>");
			} else {
				result.append("<td");
				if (this.id != null) {
					result.append(" id=\"" + this.id + "\"");
				}

				if (!this.row.header && this.row.draggable) {
					result.append("><a href=\"#\" ondragstart=\"i2uiStartedRowDragging();\"><img src=\""
							+ this.settings.getImageDirectory() + "/drag.png" + "\" border=\"0\"></a></td>");
				} else {
					result.append(">&nbsp;</td>");
				}
			}

			try {
				this.pageContext.getOut().write(result.toString());
			} catch (IOException var3) {
				throw new JspException("IO Error: " + var3.getMessage());
			}
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		return 6;
	}

	public void release() {
		super.release();
		this.row = null;
	}
}