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

public class DragAndDropRow extends BodyTagSupport {
	boolean header = false;
	String id = null;
	boolean draggable = true;
	String ondrop = null;
	String rowClass;
	DragAndDropTable table;
	Settings settings;

	public Settings getSettings() {
		return this.settings;
	}

	public void setHeader(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.header = true;
		}

	}

	public void setId(String value) {
		this.id = value;
	}

	public void setDraggable(String value) {
		if (value.toLowerCase().equals("no")) {
			this.draggable = false;
		}

	}

	public void setOndrop(String value) {
		this.ondrop = value;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		this.table = (DragAndDropTable) findAncestorWithClass(this, DragAndDropTable.class);
		if (this.table != null) {
			if (this.header) {
				this.rowClass = "tableColumnHeadings";
			} else if (this.table.incrCount() % 2 == 0) {
				this.rowClass = "tableRow0";
			} else {
				this.rowClass = "tableRow1";
			}

			result.append("<tr class=\"" + this.rowClass + "\"");
			if (!this.header && this.draggable) {
				result.append(
						" ondragenter=\"i2uiRowDragEnter();\" ondragover=\"i2uiRowDragOver();\" ondragleave=\"i2uiRowDragLeave();\" ondrop=\"i2uiRowDropped();");
				if (this.ondrop != null && !"".equals(this.ondrop)) {
					result.append(this.ondrop);
				}

				result.append("\"");
			}

			if (this.id != null) {
				result.append(" id=\"" + this.id + "\"");
			}

			result.append(">");

			try {
				this.pageContext.getOut().write(result.toString());
			} catch (IOException var3) {
				throw new JspException("IO Error: " + var3.getMessage());
			}
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}
		} catch (IOException var3) {
			throw new JspException(var3.getMessage());
		}

		if (this.table != null) {
			try {
				this.pageContext.getOut().write("</tr>");
			} catch (IOException var2) {
				throw new JspException("IO Error: " + var2.getMessage());
			}
		}

		return 6;
	}

	public void release() {
		super.release();
		this.header = false;
		this.id = null;
		this.table = null;
	}
}