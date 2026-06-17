/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import java.util.ListIterator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Table extends BodyTagSupport {
	String title = null;
	String id = null;
	String relatedTableIds = null;
	String scrollableRows = null;
	String scrollableColumns = null;
	String scrollableSyncedTable = null;
	String width = null;
	int treecellCount = 0;
	boolean inPad = false;
	boolean inHeader = false;
	boolean isScrollable = false;
	boolean isFixedLayout = false;
	int rowCount = 0;
	Settings settings;
	boolean isEditable = false;

	public Settings getSettings() {
		return this.settings;
	}

	public boolean getScrollable() {
		return this.isScrollable;
	}

	public void setTitle(String value) {
		this.title = value;
	}

	public void setId(String value) {
		this.id = value;
	}

	public void setWidth(String value) {
		this.width = value;
	}

	public String getId() {
		return this.id;
	}

	public void setRelatedtableids(String value) {
		this.relatedTableIds = value;
	}

	public String getRelatedtableids() {
		return this.relatedTableIds;
	}

	public void setScrollablerows(String value) {
		this.scrollableRows = value;
		this.isScrollable = true;
	}

	public void setScrollablecolumns(String value) {
		this.scrollableColumns = value;
		this.isScrollable = true;
	}

	public void setScrollablesyncedtable(String value) {
		this.scrollableSyncedTable = value;
	}

	public void setEditable(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.isEditable = true;
		}

	}

	public void setFixedlayout(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.isFixedLayout = true;
		}

	}

	public StringBuffer generateTreeKey(int depth, boolean spanning) {
		int mykey = depth * 10;
		if (spanning) {
			mykey += 5;
		} else {
			++this.treecellCount;
		}

		return new StringBuffer("" + mykey + "." + this.treecellCount);
	}

	public int doStartTag() throws JspException {
		StringBuffer result = new StringBuffer();
		if (this.getParent() != null && this.getParent() == findAncestorWithClass(this, Container.class)) {
			((Container) this.getParent()).setHasTable();
		}

		if (this.getParent() != null && this.getParent() == findAncestorWithClass(this, Pad.class)) {
			this.inPad = true;
		}

		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		if (this.isScrollable && this.id == null) {
			this.id = "i2table";
		}

		if (this.width == null) {
			this.width = "100%";
		}

		if (this.title != null) {
			result.append(
					"<TABLE width=\"" + this.width + "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\">");
			result.append("<TR><TD class=\"tableHeader\">&nbsp;" + this.title + "</TD></TR>");
			result.append("<TR><TD>");
		}

		if (this.isScrollable) {
			result.append(
					"<TABLE width=\"100%\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" class=\"tableBorder\"");
		} else if (this.title != null) {
			result.append(
					"<TABLE width=\"100%\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" class=\"tableBorder\"");
		} else if (this.id != null && this.inPad) {
			result.append("<TABLE width=\"" + this.width
					+ "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" class=\"tableBorder\"");
		} else {
			result.append("<TABLE width=\"" + this.width
					+ "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" class=\"tableBorder\"");
		}

		if (this.id != null) {
			result.append(" id=\"" + this.id + "\"");
		}

		result.append(">");

		try {
			this.pageContext.getOut().write(result.toString());
		} catch (IOException var4) {
			throw new JspException("IO Error: " + var4.getMessage());
		}

		this.rowCount = 0;
		this.treecellCount = 0;
		return 2;
	}

	public int incrCount() throws JspException {
		++this.rowCount;
		return this.inPad ? 0 : this.rowCount;
	}

	public int doEndTag() throws JspException {
		StringBuffer buffer = new StringBuffer();
		if (this.bodyContent != null) {
			String generatedHtml = this.bodyContent.getString();
			this.bodyContent.clearBody();
			buffer.append(generatedHtml);
		}

		try {
			if (this.title != null) {
				buffer.append("</TABLE></TD></TR>");
			} else if (this.isScrollable) {
				buffer.append("</TABLE></DIV></TD></TR>");
			}

			Document doc = Jsoup.parseBodyFragment("<table id=\"TempWrapper\">" + buffer.toString() + "</table>");
			Element body = doc.body();
			Element table;
			Elements headerRows;
			if (this.isScrollable) {
				table = body.getElementById(this.id + "_header");
				headerRows = table != null ? table.select("tr.tableColumnHeadings") : null;
				if (headerRows != null) {
					ListIterator<Element> rowIter = headerRows.listIterator();

					label100 : for (int rowIndex = 0; rowIter.hasNext(); ++rowIndex) {
						Element row = (Element) rowIter.next();
						Elements children = row.children();
						if (children != null) {
							ListIterator<Element> iter = children.listIterator();
							int index = 0;

							while (true) {
								Element col;
								do {
									if (!iter.hasNext()) {
										continue label100;
									}

									col = (Element) iter.next();
								} while (!"th".equalsIgnoreCase(col.tagName())
										&& !"td".equalsIgnoreCase(col.tagName()));

								String cellId = this.id + "_headerdiv_" + rowIndex + "_" + index;
								col.html("<div id=\"" + cellId + "\" class=\"cellWrapper\">" + col.html() + "</div>");
								++index;
							}
						}
					}
				}

				Element dataTable = body.getElementById(this.id + "_data");
				Elements dataRows = dataTable != null ? dataTable.select("tr[class^=tableRow]") : null;
				if (dataRows != null) {
					ListIterator<Element> rowIter = dataRows.listIterator();

					label77 : for (int rowIndex = 0; rowIter.hasNext(); ++rowIndex) {
						Element row = (Element) rowIter.next();
						Elements children = row.children();
						if (children != null) {
							ListIterator<Element> iter = children.listIterator();
							int index = 0;

							while (true) {
								Element col;
								do {
									if (!iter.hasNext()) {
										continue label77;
									}

									col = (Element) iter.next();
								} while (!"th".equalsIgnoreCase(col.tagName())
										&& !"td".equalsIgnoreCase(col.tagName()));

								String cellId = this.id + "_datadiv_" + rowIndex + "_" + index;
								col.html("<div id=\"" + cellId + "\" class=\"cellWrapper\">" + col.html() + "</div>");
								if (rowIndex > 0) {
									break;
								}

								++index;
							}
						}
					}
				}
			}

			table = body.getElementById("TempWrapper");
			headerRows = table.children().first().children();
			this.pageContext.getOut().write(headerRows.outerHtml());
			this.pageContext.getOut().write("</TABLE>");
			if (this.isEditable) {
				this.pageContext.getOut().write(" <object id=\"" + this.id + "Spreadsheet\" ");
				this.pageContext.getOut().write("         codebase=\"file:\\\\\\\\download\\office2000\\msowc.cab\"");
				this.pageContext.getOut().write("         style=\"display:none\"");
				this.pageContext.getOut().write("         height=\"90px\"");
				this.pageContext.getOut().write("         width=\"100px\"");
				this.pageContext.getOut().write("         classid=\"CLSID:0002E510-0000-0000-C000-000000000046\">");
				this.pageContext.getOut().write(" </object>");
				this.pageContext.getOut()
						.write(" <TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"1px\" width=\"100%\">");
				this.pageContext.getOut().write(" <TR>");
				this.pageContext.getOut().write(" <TD width=\"100%\" nowrap=\"yes\">&#160;</TD>");
				this.pageContext.getOut().write(" <TD><TABLE id=\"" + this.id
						+ "ExportAction\" cellspacing=\"1px\" cellpadding=\"0px\" class=\"buttonBorder\"><TR><TD id=\"buttonRegular\" nowrap=\"yes\" class=\"buttonText\"><A href=\"javascript:i2uiExportTable('"
						+ this.id + "')\">&nbsp;&nbsp;Export&nbsp;&nbsp;</A></TD></TR></TABLE></TD>");
				this.pageContext.getOut().write(" <TD style=\"padding-left:4px\"><TABLE id=\"" + this.id
						+ "SortAction\" cellspacing=\"1px\" cellpadding=\"0px\" class=\"buttonBorder\"><TR><TD id=\"buttonRegular\" nowrap=\"yes\" class=\"buttonText\"><A href=\"javascript:i2uiSortTable('"
						+ this.id + "')\">&nbsp;&nbsp;Sort&nbsp;&nbsp;</A></TD></TR></TABLE></TD>");
				this.pageContext.getOut().write(" <TD style=\"padding-left:4px\"><TABLE id=\"" + this.id
						+ "CancelAction\" cellspacing=\"1px\" cellpadding=\"0px\" class=\"buttonBorder\"><TR><TD id=\"buttonRegular\" nowrap=\"yes\" class=\"buttonText\"><A href=\"javascript:i2uiCancelTableEdit('"
						+ this.id + "')\">&nbsp;&nbsp;Cancel Edit&nbsp;&nbsp;</A></TD></TR></TABLE></TD>");
				this.pageContext.getOut().write(" <TD style=\"padding-left:4px\"><TABLE id=\"" + this.id
						+ "SaveAction\" cellspacing=\"1px\" cellpadding=\"0px\" class=\"buttonBorder\"><TR><TD id=\"buttonRegular\" nowrap=\"yes\" class=\"buttonText\"><A href=\"javascript:i2uiSaveTableEdit('"
						+ this.id + "')\">&nbsp;&nbsp;Save Edit&nbsp;&nbsp;</A></TD></TR></TABLE></TD>");
				this.pageContext.getOut().write(" <TD style=\"padding-right:3px\"><TABLE id=\"" + this.id
						+ "EditAction\" cellspacing=\"1px\" cellpadding=\"0px\" class=\"buttonBorder\"><TR><TD id=\"buttonRegular\" nowrap=\"yes\" class=\"buttonText\"><A href=\"javascript:i2uiStartTableEdit('"
						+ this.id + "')\">&nbsp;&nbsp;Edit table&nbsp;&nbsp;</A></TD></TR></TABLE></TD>");
				this.pageContext.getOut().write(" </TR>");
				this.pageContext.getOut().write(" </TABLE>");
			}

			if (this.inPad) {
				this.pageContext.getOut().write("<SCRIPT>i2uiCollapsePadTree('" + this.id + "',10);</SCRIPT>");
			}

			return 6;
		} catch (IOException var16) {
			throw new JspException("IO Error: " + var16.getMessage());
		}
	}

	public void endOfHeader() throws JspException {
		if (this.inHeader) {
			this.inHeader = false;
			StringBuffer result = new StringBuffer();
			if (this.isScrollable) {
				result.append("</TABLE>");
				if (this.scrollableColumns != null && !this.scrollableColumns.toLowerCase().equals("no")) {
					result.append("</DIV>");
				}

				result.append("</TD></TR><TR><TD>");
				result.append("<DIV id=\"" + this.id + "_scroller\" ");
				if (this.scrollableColumns != null && !this.scrollableColumns.toLowerCase().equals("no")) {
					result.append("onscroll=\"i2uiSyncdScroll('" + this.id + "');");
					if (this.scrollableSyncedTable != null) {
						result.append("i2uiSyncdScroll('" + this.id + "','" + this.scrollableSyncedTable + "');");
						result.append("i2uiSyncdScroll('" + this.id + "','" + this.scrollableSyncedTable + "2');");
					}

					result.append("i2uiExternalSyncdScroll();");
					result.append("\" ");
				}

				result.append("style=\"");
				if (this.scrollableRows != null && this.scrollableRows.toLowerCase().equals("yes")) {
					result.append("overflow-y:scroll;height:100px;");
				} else if (this.scrollableRows != null && this.scrollableRows.toLowerCase().equals("auto")) {
					result.append("overflow-y:auto;height:100px;");
				} else {
					result.append("overflow-y:hidden;height:100px;");
				}

				if (this.scrollableColumns != null && this.scrollableColumns.toLowerCase().equals("yes")) {
					result.append("overflow-x:scroll;width:100px;");
				} else if (this.scrollableColumns != null && this.scrollableColumns.toLowerCase().equals("hidden")) {
					result.append("overflow-x:hidden;width:100px;");
				} else if (this.scrollableColumns != null && this.scrollableColumns.toLowerCase().equals("auto")) {
					result.append("overflow-x:auto;width:100px;");
				} else {
					result.append("overflow-x:hidden;");
				}

				result.append("\"><TABLE");
				if (this.scrollableColumns != null && !this.scrollableColumns.toLowerCase().equals("no")) {
					if (this.scrollableColumns != null && !this.scrollableColumns.toLowerCase().equals("no")
							|| this.scrollableRows != null && !this.scrollableRows.toLowerCase().equals("no")) {
						if (this.isFixedLayout) {
							result.append(" style=\"table-layout:fixed;width:" + this.width + ";\"");
						} else {
							result.append(" width=\"100px\"");
						}
					} else if (this.isFixedLayout) {
						result.append(" style=\"table-layout:fixed;width:" + this.width + ";\"");
					} else {
						result.append(" width=\"100%\"");
					}
				} else {
					result.append(" width=\"100%\"");
				}

				result.append(" cellspacing=\"0px\" cellpadding=\"0px\" id=\"" + this.id + "_data\">");

				try {
					this.pageContext.getOut().write(result.toString());
				} catch (IOException var3) {
					throw new JspException("IO Error: " + var3.getMessage());
				}
			}

		}
	}

	public void startOfHeader() throws JspException {
		if (!this.inHeader) {
			this.inHeader = true;
			StringBuffer result = new StringBuffer();
			if (this.isScrollable) {
				result.append("<TR><TD>");
				if (this.scrollableColumns != null && !this.scrollableColumns.toLowerCase().equals("no")) {
					result.append("<DIV id=\"" + this.id
							+ "_header_scroller\" style=\"overflow-y:hidden;overflow-x:hidden;width:100px;\">");
				}

				if (this.isFixedLayout) {
					result.append("<TABLE style=\"table-layout:fixed;width:" + this.width
							+ ";\" cellspacing=\"0px\" cellpadding=\"0px\" id=\"" + this.id + "_header\">");
				} else {
					result.append("<TABLE width=\"100%\" cellspacing=\"0px\" cellpadding=\"0px\" id=\"" + this.id
							+ "_header\">");
				}

				try {
					this.pageContext.getOut().write(result.toString());
				} catch (IOException var3) {
					throw new JspException("IO Error: " + var3.getMessage());
				}
			}

		}
	}

	public void release() {
		super.release();
		this.title = null;
		this.id = null;
		this.rowCount = 0;
		this.treecellCount = 0;
		this.settings = null;
		this.scrollableRows = null;
		this.scrollableColumns = null;
		this.isScrollable = false;
		this.inHeader = false;
		this.inPad = false;
		this.relatedTableIds = null;
		this.width = null;
	}
}