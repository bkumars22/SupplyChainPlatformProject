/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Row extends BodyTagSupport {
	boolean header = false;
	String id = null;
	String classOverride = null;
	String rowClass;
	Table table;

	public void setHeader(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.header = true;
		}

	}

	public void setId(String value) {
		this.id = value;
	}

	public void setClass(String value) {
		this.classOverride = value;
	}

	public void setCssclass(String value) {
		this.classOverride = value;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		String rowClassExtra = null;
		this.table = (Table) findAncestorWithClass(this, Table.class);
		if (this.table != null) {
			if (this.header) {
				this.rowClass = "tableColumnHeadings";
				this.table.startOfHeader();
			} else {
				this.table.endOfHeader();
				if (this.table.incrCount() % 2 == 0) {
					this.rowClass = "tableRow0";
				} else {
					this.rowClass = "tableRow1";
				}
			}

			if (this.classOverride != null) {
				this.rowClass = this.classOverride;
			}

			rowClassExtra = this.rowClass + "Extra";
			result.append("<TR class=\"" + this.rowClass + " " + rowClassExtra + "\"");
			if (this.id != null) {
				result.append(" ID=\"" + this.id + "\"");
			}

			result.append(">");

			try {
				this.pageContext.getOut().write(result.toString());
			} catch (IOException var4) {
				throw new JspException("IO Error: " + var4.getMessage());
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
				this.pageContext.getOut().write("</TR>");
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
		this.classOverride = null;
	}
}