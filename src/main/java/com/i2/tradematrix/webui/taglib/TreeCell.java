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

public class TreeCell extends BodyTagSupport {
	int depth = 0;
	int column = 0;
	String name = null;
	String onclick = null;
	String ondblclick = null;
	boolean loadondemand = false;
	boolean spanning = false;
	Settings settings;
	Table table;
	Tree tree;

	public void setDepth(String value) {
		this.depth = new Integer(value);
	}

	public void setDepth(int value) {
		this.depth = value;
	}

	public void setDepth(Integer value) {
		this.depth = value;
	}

	public void setColumn(String value) {
		this.column = new Integer(value);
	}

	public void setName(String value) {
		this.name = value;
	}

	public void setOnclick(String value) {
		this.onclick = value;
	}

	public void setLoadondemand(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.loadondemand = true;
		}

	}

	public void setSpanning(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.spanning = true;
		}

	}

	public String getOndblclick() {
		return this.ondblclick;
	}

	public void setOndblclick(String ondblclick) {
		this.ondblclick = ondblclick;
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		this.table = (Table) findAncestorWithClass(this, Table.class);
		int loop;
		String tableid;
		StringBuffer mykey;
		if (this.table != null) {
			tableid = this.table.getId();
			mykey = this.table.generateTreeKey(this.depth, this.spanning);
			String relatedtableids = this.table.getRelatedtableids();
			result.append("<TD nowrap=\"yes\" id=\"TREECELL_").append(mykey).append("\"");
			if (this.ondblclick != null && this.ondblclick.length() > 0) {
				result.append(" ondblclick=\"").append(this.ondblclick).append("\"");
			}

			result.append("><nobr>");

			for (loop = 0; loop < this.depth; ++loop) {
				result.append("&nbsp;&nbsp;&nbsp;");
			}

			if (!this.spanning) {
				result.append(
						"<a href=\"javascript:i2uiManageTreeTable('" + tableid + "','" + mykey + "'," + this.column);
				if (relatedtableids != null) {
					result.append(",'" + relatedtableids + "'");
				} else {
					result.append(",null");
				}

				if (this.name != null) {
					result.append(",'" + this.name + "'");
				}

				if (this.loadondemand) {
					result.append(")\"><img id=\"TREECELLIMAGE_" + tableid + "_" + mykey + "\" border=\"0px\" src=\""
							+ this.settings.getImageDirectory() + "/plus_loadondemand.png\"></a>&nbsp;");
				} else {
					result.append(")\"><img id=\"TREECELLIMAGE_" + tableid + "_" + mykey + "\" border=\"0px\" src=\""
							+ this.settings.getImageDirectory() + "/minus_norgie.png\"></a>&nbsp;");
				}

				if (this.onclick != null) {
					result.append("<SPAN ");
					result.append("id=\"" + tableid + "_" + mykey + "\">");
					result.append("<a href='javascript:i2uiTreeTableAction(\"" + tableid + "_" + mykey + "\",\""
							+ this.onclick + "\")'>");
				}
			}

			try {
				this.pageContext.getOut().write(result.toString());
			} catch (IOException var8) {
				throw new JspException("IO Error: " + var8.getMessage());
			}
		} else {
			this.tree = (Tree) findAncestorWithClass(this, Tree.class);
			if (this.tree != null) {
				tableid = this.tree.getId();
				mykey = this.tree.generateTreeKey(this.depth, this.spanning);
				if (this.depth == 0) {
					result.append("<TR class=\"tableRow1\">");
				} else {
					result.append("<TR class=\"tableRow0\">");
				}

				result.append("<TD nowrap=\"yes\" id=\"TREECELL_" + mykey + "\"><nobr>");

				for (loop = 0; loop < this.depth; ++loop) {
					result.append("&nbsp;&nbsp;&nbsp;");
				}

				if (!this.spanning) {
					result.append("<a href=\"javascript:i2uiManageTreeTable('" + tableid + "','" + mykey + "',"
							+ this.column);
					if (this.loadondemand) {
						result.append(
								")\"><img id=\"TREECELLIMAGE_" + tableid + "_" + mykey + "\" border=\"0px\" src=\""
										+ this.settings.getImageDirectory() + "/plus_loadondemand.png\"></a>&nbsp;");
					} else {
						result.append(
								")\"><img id=\"TREECELLIMAGE_" + tableid + "_" + mykey + "\" border=\"0px\" src=\""
										+ this.settings.getImageDirectory() + "/minus_norgie.png\"></a>&nbsp;");
					}

					if (this.onclick != null) {
						result.append("<SPAN ");
						result.append("id=\"" + tableid + "_" + mykey + "\">");
						result.append("<a href='javascript:i2uiTreeTableAction(\"" + tableid + "_" + mykey + "\",\""
								+ this.onclick + "\")'>");
					}
				}

				try {
					this.pageContext.getOut().write(result.toString());
				} catch (IOException var7) {
					throw new JspException("IO Error: " + var7.getMessage());
				}
			}
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer();
		if (!this.spanning) {
			try {
				if (this.bodyContent != null) {
					this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
				}
			} catch (IOException var4) {
				throw new JspException(var4.getMessage());
			}

			if (this.onclick != null) {
				result.append("</A>");
				result.append("</SPAN>");
			}
		}

		result.append("</nobr></TD>");
		if (this.tree != null) {
			result.append("</TR>");
		}

		if (this.table != null || this.tree != null) {
			try {
				this.pageContext.getOut().write(result.toString());
			} catch (IOException var3) {
				throw new JspException("IO Error: " + var3.getMessage());
			}
		}

		return 6;
	}

	public void release() {
		super.release();
		this.depth = 0;
		this.column = 0;
		this.name = null;
		this.table = null;
		this.onclick = null;
		this.ondblclick = null;
		this.loadondemand = false;
		this.spanning = false;
	}
}