/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import com.scplatform.testing.webui.jtags.navpad.PadItem;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Container extends BodyTagSupport implements FooterSupporter {
	public static final String SELECTED_PADITEM = "Container.SelectedPadItem";
	String title = null;
	String titlesuffix = null;
	String id = null;
	String footer = null;
	String width = null;
	String height = null;
	String tabindex = null;
	String onclick = null;
	boolean collapsable = false;
	boolean scrollable = false;
	boolean indented = false;
	boolean inneruse = false;
	String paduse = null;
	boolean hasTable = false;
	boolean isHeaderRightAlign = true;
	boolean hasHeaderBorder = false;
	Settings settings = null;
	String complexFooter = null;
	String complexHeader = null;

	public void setTitle(String value) {
		this.title = value;
	}

	public void setTitlesuffix(String value) {
		this.titlesuffix = value;
	}

	public void setId(String value) {
		this.id = value;
	}

	public void setWidth(String value) {
		this.width = value;
	}

	public void setHeight(String value) {
		this.height = value;
	}

	public void setTabindex(String value) {
		this.tabindex = value;
	}

	public void setFooter(String value) {
		this.footer = value;
	}

	public void setCollapsable(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.collapsable = true;
		}

	}

	public void setIndentcontent(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.indented = true;
		}

	}

	public void setInner(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.inneruse = true;
		}

	}

	public void setPadUse(String value) {
		this.paduse = value;
	}

	public void setScrollable(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.scrollable = true;
		}

	}

	public void setComplexFooter(String value) {
		this.complexFooter = value;
	}

	public void setComplexHeader(String value) {
		this.complexHeader = value;
	}

	public void setHasTable() {
		this.hasTable = true;
	}

	public void setOnclick(String value) {
		this.onclick = value;
	}

	public void setIsHeaderRightAlign(boolean isHeaderRightAlign) {
		this.isHeaderRightAlign = isHeaderRightAlign;
	}

	public void setHeaderborder(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.hasHeaderBorder = true;
		}

	}

	public int doStartTag() throws JspException {
		if (this.width == null) {
			this.width = "100%";
		}

		if (this.height == null) {
			this.height = "100%";
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		this.settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		int nest = 1;
		String colspan = "";
		String classname = "containerHeader";
		String classmodifier = "container";
		StringBuffer result = new StringBuffer();
		String contentclassname = "containerBody";
		String contentclassname2 = "containerBody";
		if (this.indented) {
			if (this.scrollable) {
				contentclassname2 = contentclassname2 + "Indent";
				contentclassname2 = contentclassname2 + "Scrolling";
			} else {
				contentclassname = contentclassname + "Indent";
			}
		}

		if (this.inneruse) {
			classmodifier = classmodifier + "Inner";
		} else {
			classmodifier = classmodifier + "Outer";
		}

		if (this.complexHeader != null) {
			colspan = " colspan=\"2\"";
			if (this.paduse == null) {
				classname = "containerHeaderLeft";
				if (this.hasHeaderBorder) {
					classname = classname + "Border";
				}
			} else {
				classname = this.paduse + "PadTitle";
			}

			nest = 2;
		} else if (this.title == null && !this.collapsable) {
			classname = "containerHeaderless";
		} else if (this.paduse != null) {
			classname = this.paduse + "PadTitle";
		} else if (this.hasHeaderBorder) {
			classname = "containerHeaderBorder";
		}

		result.append("<TABLE ");
		if (this.id != null) {
			if (this.paduse == null) {
				result.append("id=\"" + this.id + "\" ");
			} else {
				result.append("id=\"PAD_" + this.id + "\" ");
			}
		}

		result.append(
				"width=\"" + this.width + "\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" class=\"shadow\">"
						+ "<TR valign=\"top\">");
		if (this.complexHeader != null) {
			colspan = "";
			if (this.paduse != null) {
				result.append("<TD class=\"" + classname + "\" nowrap=\"yes\"><DIV class=\"" + this.paduse
						+ "PadTitleBorder0\"><DIV class=\"" + this.paduse + "PadTitleBorder1\">");
			} else {
				result.append("<TD>");
			}

			result.append("<TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\" width=\"100%\"><TR>");
		}

		String headerClassName;
		String padItemId;
		if (!"containerHeaderless".equals(classname) || this.paduse == null) {
			result.append("<TD ");
			if (this.collapsable && this.paduse == null) {
				result.append(" style=\"padding-top:4px;padding-bottom:4px;\"");
			}

			if ((!this.scrollable || this.complexHeader != null) && this.isHeaderRightAlign) {
				result.append(" width=\"100%\"");
			}

			if (this.paduse == null) {
				result.append(" class=\"" + classname + "\" id=\"" + classmodifier + "\" nowrap=\"yes\">&nbsp;");
			} else {
				if (this.complexHeader == null) {
					result.append(" class=\"" + classname + "\" nowrap=\"yes\"");
				}

				result.append(">");
				if (this.complexHeader == null) {
					result.append("<DIV class=\"" + this.paduse + "PadTitleBorder0\"><DIV class=\"" + this.paduse
							+ "PadTitleBorder1\">");
				}
			}

			if (this.collapsable) {
				headerClassName = "";
				if (this.paduse != null) {
					headerClassName = ",'i2uiTilePads()'";
				}

				result.append("&nbsp;<IMG  onclick=\"javascript:i2uiToggleContent(this," + nest + headerClassName + ");"
						+ this.onclick + ";\" onMouseOver=\"javascript:this.style.cursor='pointer'\" src=\""
						+ this.settings.getImageDirectory() + "/container_collapse.png\">&nbsp;");
			}

			if (this.title != null) {
				if (this.paduse == null) {
					result.append(this.title);
					if (this.titlesuffix != null) {
						result.append("&nbsp;<span style=\"font-weight:normal\">" + this.titlesuffix + "</span>");
					}
				} else {
					result.append("<b>" + this.title + "</b>");
				}
			}

			if (this.complexHeader != null) {
				if (this.paduse == null) {
					headerClassName = "containerHeaderRight";
					if (this.hasHeaderBorder) {
						headerClassName = headerClassName + "Border";
					}

					padItemId = this.isHeaderRightAlign
							? "align=\"right\"  class=\"" + headerClassName + "\""
							: " width=\"100%\" class=\"containerHeaderLeftNoSideBorder\"";
					result.append("</TD><TD " + padItemId + " id=\"" + classmodifier + "\" nowrap=\"yes\">"
							+ this.complexHeader);
				} else {
					headerClassName = this.isHeaderRightAlign ? "align=\"right\"" : "width=\"100%\"";
					result.append("</TD><TD " + headerClassName + " nowrap=\"yes\">" + this.complexHeader);
				}

				result.append("</TD></TR></TABLE>");
			}

			if (this.paduse != null) {
				result.append("</DIV></DIV>");
			}

			result.append("</TD></TR>");
		}

		result.append("<TBODY id=\"_containerBody\">");
		result.append("<TR valign=\"top\"><TD" + colspan + " class=\"" + contentclassname + "\">");
		if (this.scrollable) {
			result.append("<DIV ");
			if (this.indented) {
				result.append("class=\"" + contentclassname2 + "\" ");
			}

			if (this.id != null) {
				result.append("id=\"" + this.id + "_scroller\" ");
			}

			result.append("style=\"");
			if (this.height != null) {
				result.append("height:" + this.height + ";");
			}

			result.append("width:" + this.width + ";overflow:auto\"");
			if (this.tabindex != null) {
				result.append(" tabindex=\"" + this.tabindex + "\"");
			}

			result.append(">");
		}

		if (this.paduse != null) {
			result.append("<table ");
			if (this.id != null) {
				result.append("id=\"" + this.id + "\" ");
			}

			result.append("width=\"100%\" border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\">");
		}

		try {
			this.pageContext.getOut().write(result.toString());
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}
		} catch (IOException var17) {
			throw new JspException("IO Error: " + var17.getMessage());
		}

		result = new StringBuffer();
		if (this.paduse != null) {
			result.append("</table>");
			if (this.id != null) {
				result.append("<script>");
				result.append("i2uiCollapsePadTree('" + this.id + "',1);");
				result.append("i2uiPad.instances[i2uiPad.count]='" + this.id + "';");
				result.append("i2uiPad.count++;");
				result.append("i2uiManagePadScroller('" + this.id + "');");
				PadItem selectedPadItem = (PadItem) this.pageContext.getAttribute("Container.SelectedPadItem");
				if (selectedPadItem != null) {
					padItemId = selectedPadItem.getId();
					int pos = padItemId.indexOf("_");

					while (pos > 0) {
						pos = padItemId.indexOf("_", pos + 1);
						if (pos > 0) {
							result.append("i2uiManagePadTree('" + this.id + "','" + padItemId.substring(0, pos)
									+ "',0,null,null,null,'i2uiTilePads()');");
						}
					}

					result.append("i2uiHighlightPadItem('TREECELL_" + padItemId + "');");
				}

				result.append("</script>");
			}
		}

		if (this.scrollable) {
			result.append("</DIV>");
		}

		result.append("</TD></TR>");
		headerClassName = "containerFooter";
		if (this.complexFooter != null) {
			result.append("<TR><TD" + colspan + " nowrap=\"yes\" class=\"" + headerClassName + "\" id=\""
					+ classmodifier + "\">" + this.complexFooter + "</TD></TR>");
		} else if (this.footer != null) {
			result.append("<TR><TD" + colspan + " class=\"" + headerClassName + "\" id=\"" + classmodifier + "\">&nbsp;"
					+ this.footer + "</TD></TR>");
		} else if (this.paduse == null) {
			result.append("<TR height=\"1px\"><TD height=\"1px\" class=\"containerBorder\"></TD></TR>");
		}

		result.append("</TBODY>");
		result.append("</TABLE>");

		try {
			this.pageContext.getOut().write(result.toString());
		} catch (IOException var15) {
			throw new JspException("IO Error: " + var15.getMessage());
		} finally {
			this.reset();
		}

		return 6;
	}

	public int doAfterBody() throws JspException {
		return 0;
	}

	public void release() {
		super.release();
		this.reset();
	}

	private void reset() {
		this.title = null;
		this.titlesuffix = null;
		this.id = null;
		this.width = null;
		this.tabindex = null;
		this.footer = null;
		this.collapsable = false;
		this.scrollable = false;
		this.indented = false;
		this.inneruse = false;
		this.paduse = null;
		this.hasTable = false;
		this.settings = null;
		this.complexFooter = null;
		this.complexHeader = null;
	}
}