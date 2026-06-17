/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class RowSelector extends BodyTagSupport {
	boolean global = false;
	boolean multiSelect = true;
	String treecell = null;

	public void setGlobal(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.global = true;
		}

	}

	public void setSelect(String value) {
		if (value.toLowerCase().equals("single")) {
			this.multiSelect = false;
		}

	}

	public void setTreecell(String value) {
		this.treecell = value;
	}

	public int doStartTag() throws JspException {
		Table table = (Table) findAncestorWithClass(this, Table.class);
		Row row = (Row) findAncestorWithClass(this, Row.class);
		if (table != null && row != null) {
			String rowClass = row.rowClass;

			try {
				if (this.global) {
					this.pageContext.getOut().write("<TH><input id=\"" + table.getId()
							+ "_globalrowselector\" type=\"checkbox\" onclick=\"i2uiToggleAllRowsSelectionState(this,'"
							+ table.getId() + "')\"></TH>");
				} else if (this.multiSelect) {
					this.pageContext.getOut()
							.write("<TH class=\"tableColumnHeadings\"><input id=\"" + table.getId()
									+ "_rowselector\" type=\"checkbox\" onclick=\"i2uiToggleRowSelectionState(this,'"
									+ rowClass + "','" + table.getId() + "'," + this.treecell + ",true)\"></TH>");
				} else {
					this.pageContext.getOut()
							.write("<TH class=\"tableColumnHeadings\"><input id=\"" + table.getId()
									+ "_rowselector\" name=\"" + table.getId()
									+ "_rowselector\" type=\"radio\" onclick=\"i2uiToggleRowSelectionState(this,'"
									+ rowClass + "','" + table.getId() + "'," + this.treecell + ",false)\"></TH>");
				}
			} catch (IOException var5) {
				throw new JspException("IO Error: " + var5.getMessage());
			}
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}

			return 6;
		} catch (IOException var2) {
			throw new JspException(var2.getMessage());
		}
	}

	public void release() {
		super.release();
	}
}