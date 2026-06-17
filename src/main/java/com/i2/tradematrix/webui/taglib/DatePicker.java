/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class DatePicker extends TagSupport {
	private static final String TODAY_KEY = "Today";
	private static final String CANCEL_KEY = "Cancel";
	private static final String OK_KEY = "OK";
	private static final String DAYLETTERS_KEY = "SMTWTFS";
	private static final int NOT_NS4_INDEX = 0;
	private static final int[] SELECTION_CALLBACK_INDEX = new int[]{0, 0};
	private static final int[] ID_INDEX = new int[]{2, 2};
	private static final int[] DIV_STYLE_INDEX = new int[]{3, -1};
	private static final int[] PREV_YEAR_A_ATTRIB_INDEX = new int[]{14, -1};
	private static final int[] PREV_YEAR_INDEX = new int[]{16, 14};
	private static final int[] NEXT_YEAR_A_ATTRIB_INDEX = new int[]{28, -1};
	private static final int[] NEXT_YEAR_INDEX = new int[]{30, 27};
	private static final int[] PREV_MONTH_A_ATTRIB_INDEX = new int[]{49, -1};
	private static final int[] PREV_MONTH_INDEX = new int[]{51, 47};
	private static final int[] NEXT_MONTH_A_ATTRIB_INDEX = new int[]{63, -1};
	private static final int[] NEXT_MONTH_INDEX = new int[]{65, 60};
	private static final int[] GEN_CALENDAR_HDR_INDEX = new int[]{81, 79};
	private static final int[] GEN_CALENDAR_INDEX = new int[]{87, 85};
	private static final int[] TODAY_A_ATTRIB_INDEX = new int[]{102, -1};
	private static final int[] TODAY_INDEX = new int[]{103, 103};
	private static final int[] EMBEDDED_STOP = new int[]{108, 108};
	private static final int[] DIVIDER_IMAGE_INDEX = new int[]{109, 109};
	private static final int[] CANCEL_A_ATTRIB_INDEX = new int[]{115, -1};
	private static final int[] CANCEL_CALLBACK_INDEX = new int[]{116, 115};
	private static final int[] CANCEL_INDEX = new int[]{117, 116};
	private static final int[] OK_A_ATTRIB_INDEX = new int[]{127, -1};
	private static final int[] OK_CALLBACK_INDEX = new int[]{128, 126};
	private static final int[] OK_INDEX = new int[]{129, 127};
	private static final int[] EMBEDDED_START = new int[]{134, 132};
	private static final String PREV_YEAR_ICON = "/previous_solid_double.png";
	private static final String NEXT_YEAR_ICON = "/forward_double_arrow.png";
	private static final String PREV_MONTH_ICON = "/previous_solid.png";
	private static final String NEXT_MONTH_ICON = "/forward_single_arrow.png";
	private static final String IE_A_ATTRIBUTE = " ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ";
	private static final String POPUP_DIV_STYLE_ATTRIBUTE = " STYLE=\"position:absolute;left:1;top:1;visibility:hidden;\" ";
	private static final String DIALOG_DIV_STYLE_ATTRIBUTE = "";
	private static final boolean DEBUG = false;
	private static final String[] boilerPlateNotNS4_ = new String[]{
			"<SCRIPT LANGUAGE=\"JavaScript\">var mySelectionCallback = \"", "\";</SCRIPT>", "<DIV ID=\"", "\"", ">",
			"  <TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\" CLASS=\"tableBorder\" ID=\"topLevelTable\">",
			"    <TR>", "      <TD CLASS=\"datePickerHeaderYr\">",
			"        <TABLE WIDTH=\"100%\" BORDER=\"0px\" CELLSPACING=\"0px\" CELLPADDING=\"1px\">", "          <TR>",
			"            <TD NOWRAP=\"yes\" WIDTH=\"18\">",
			"              <TABLE BORDER=\"0px\" CELLSPACING=\"1px\" CELLPADDING=\"0px\" CLASS=\"buttonBorder\" WIDTH=\"100%\">",
			"                <TR>",
			"                  <TD NOWRAP=\"yes\" CLASS=\"datePickerHeaderButton\" HEIGHT=\"14\">",
			"                    <A", "ONCLICK=\"javascript:void i2uiDatePickerPrevYear()\">",
			"                      <IMG BORDER=\"0px\" SRC=\"", "\">", "                    </A>",
			"                  </TD>", "                </TR>", "              </TABLE>", "            </TD>",
			"            <TD NOWRAP=\"yes\" CLASS=\"datePickerHeaderLabel\" ID=\"datePickerYearHeaderLabel\">2001</TD>",
			"            <TD NOWRAP=\"yes\" WIDTH=\"18px\">",
			"              <TABLE BORDER=\"0px\" CELLSPACING=\"1px\" CELLPADDING=\"0px\" CLASS=\"buttonBorder\" WIDTH=\"100%\">",
			"                <TR>",
			"                  <TD NOWRAP=\"yes\" CLASS=\"datePickerHeaderButton\" HEIGHT=\"14px\">",
			"                    <A", "ONCLICK=\"javascript:void i2uiDatePickerNextYear()\">",
			"                      <IMG BORDER=\"0px\" src=\"", "\">", "                    </A>",
			"                  </TD>", "                </TR>", "              </TABLE>", "            </TD>",
			"          </TR>", "        </TABLE>", "      </TD>", "    </TR>", "    <TR>",
			"      <TD CLASS=\"datePickerHeaderMo\">",
			"        <TABLE WIDTH=\"100%\" BORDER=\"0px\" CELLSPACING=\"0px\" CELLPADDING=\"1px\">", "          <TR>",
			"            <TD NOWRAP=\"yes\" WIDTH=\"18\">",
			"              <TABLE BORDER=\"0px\" CELLSPACING=\"1px\" CELLPADDING=\"0px\" CLASS=\"buttonBorder\" WIDTH=\"100%\">",
			"                <TR>",
			"                  <TD NOWRAP=\"yes\" CLASS=\"datePickerHeaderButton\" HEIGHT=\"14px\">",
			"                    <A", "ONCLICK=\"javascript:void i2uiDatePickerPrevMonth()\">",
			"                      <IMG BORDER=\"0px\" src=\"", "\">", "                    </A>",
			"                  </TD>", "                </TR>", "              </TABLE>", "            </TD>",
			"            <TD NOWRAP=\"yes\" CLASS=\"datePickerHeaderLabel\" ID=\"datePickerMonthHeaderLabel\">August</TD>",
			"            <TD NOWRAP=\"yes\" WIDTH=\"18px\">",
			"              <TABLE BORDER=\"0px\" CELLSPACING=\"1px\" CELLPADDING=\"0px\" CLASS=\"buttonBorder\" WIDTH=\"100%\">",
			"                <TR>",
			"                  <TD NOWRAP=\"yes\" CLASS=\"datePickerHeaderButton\" HEIGHT=\"14\">",
			"                    <A", "ONCLICK=\"javascript:void i2uiDatePickerNextMonth()\">",
			"                      <IMG BORDER=\"0px\" src=\"", "\">", "                    </A>",
			"                  </TD>", "                </TR>", "              </TABLE>", "            </TD>",
			"          </TR>", "        </TABLE>", "      </TD>", "    </TR>", "    <TR>",
			"      <TD CLASS=\"datePickerCalendar\">",
			"        <TABLE WIDTH=\"100%\" BORDER=\"0px\" CELLSPACING=\"0px\" CELLPADDING=\"0\">", "          <TR>",
			"            <TD ALIGN=\"center\">",
			"              <TABLE BORDER=\"0px\" CELLSPACING=\"1px\" CELLPADDING=\"0px\">", "              </TABLE>",
			"            </TD>", "          </TR>", "          <TR>", "            <TD ALIGN=\"center\">",
			"              <TABLE BORDER=\"0px\" CELLSPACING=\"1px\" CELLPADDING=\"0px\" CLASS=\"tableBorder\">",
			"              </TABLE>", "            </TD>", "          </TR>", "        </TABLE>", "      </TD>",
			"    </TR>", "    <TR>", "      <TD CLASS=\"datePickerFooter\">",
			"        <TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"1px\" width=\"100%\">", "          <TR>",
			"            <TD nowrap=\"yes\">",
			"              <TABLE BORDER=\"0px\" cellspacing=\"1px\" cellpadding=\"0px\" class=\"buttonBorder\">",
			"                <TR>", "                  <TD id=\"buttonRegular\" nowrap=\"yes\" class=\"buttonText\">",
			"                    <A", "ONCLICK=\"javascript:void i2uiSetDatePickerToday()\">&nbsp;", "&nbsp;</A>",
			"                  </TD>", "                </TR>", "              </TABLE>", "            </TD>",
			"            <TD nowrap=\"yes\" WIDTH=\"10px\" STYLE=\"text-align:center\"><IMG src=\"",
			"/blue_divider.png\"></TD>", "            <TD nowrap=\"yes\">",
			"              <TABLE BORDER=\"0px\" cellspacing=\"1px\" cellpadding=\"0px\" class=\"buttonBorder\">",
			"                <TR>", "                  <TD id=\"buttonRegular\" nowrap=\"yes\" class=\"buttonText\">",
			"                    <A", "ONCLICK=\"javascript:void i2uiDatePickerCancel('", "')\">&nbsp;", "&nbsp;</A>",
			"                  </TD>", "                </TR>", "              </TABLE>", "            </TD>",
			"            <TD nowrap=\"yes\">",
			"              <TABLE BORDER=\"0px\" cellspacing=\"1px\" cellpadding=\"0px\" class=\"buttonBorderEmphasized\">",
			"                <TR>",
			"                  <TD id=\"buttonEmphasized\" nowrap=\"yes\" class=\"buttonTextEmphasized\">",
			"                    <A", "ONCLICK=\"javascript:void i2uiDatePickerOk('", "')\">&nbsp;", "&nbsp;</A>",
			"                  </TD>", "                </TR>", "              </TABLE>", "            </TD>",
			"          </TR>", "        </TABLE>", "      </TD>", "    </TR>", "  </TABLE>", "</DIV>"};
	String id_ = null;
	String okCallback_ = null;
	String cancelCallback_ = null;
	String localeString_ = null;
	boolean isEmbedded_ = false;
	boolean isDialog_ = false;

	public void setId(String value) {
		this.id_ = value;
	}

	public void setOkcallback(String value) {
		this.okCallback_ = value;
	}

	public void setCancelcallback(String value) {
		this.cancelCallback_ = value;
	}

	public void setLocale(String value) {
		this.localeString_ = value;
	}

	public void setEmbedded(String value) {
		this.isEmbedded_ = value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
	}

	public void setDialog(String value) {
		this.isDialog_ = value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
	}

	public int doEndTag() throws JspException {
		StringBuffer result = new StringBuffer(16384);
		Settings settings = Settings.getSessionSettings((HttpServletRequest) this.pageContext.getRequest());
		String imageDir = settings.getImageDirectory();
		String[] boilerPlate = boilerPlateNotNS4_;
		int browserIndex = 0;
		boolean emit = true;
		if (this.localeString_ == null) {
			this.localeString_ = settings.getLocale().toString();
		}

		String dayletters_key = Utils.translate(this.localeString_, "SMTWTFS");

		for (int i = 0; i < boilerPlate.length; ++i) {
			if (emit) {
				result.append(boilerPlate[i].trim());
			}

			if (i == SELECTION_CALLBACK_INDEX[browserIndex]) {
				if (emit) {
					result.append(this.isEmbedded_ ? this.okCallback_ : "null");
				}
			} else if (i == ID_INDEX[browserIndex]) {
				if (emit) {
					result.append(this.id_);
				}
			} else if (i == DIV_STYLE_INDEX[browserIndex]) {
				if (emit) {
					result.append(
							this.isDialog_ ? "" : " STYLE=\"position:absolute;left:1;top:1;visibility:hidden;\" ");
				}
			} else if (i == PREV_YEAR_A_ATTRIB_INDEX[browserIndex]) {
				if (emit) {
					result.append(" ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ");
				}
			} else if (i == PREV_YEAR_INDEX[browserIndex]) {
				if (emit) {
					result.append(imageDir + "/previous_solid_double.png");
				}
			} else if (i == NEXT_YEAR_A_ATTRIB_INDEX[browserIndex]) {
				if (emit) {
					result.append(" ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ");
				}
			} else if (i == NEXT_YEAR_INDEX[browserIndex]) {
				if (emit) {
					result.append(imageDir + "/forward_double_arrow.png");
				}
			} else if (i == PREV_MONTH_A_ATTRIB_INDEX[browserIndex]) {
				if (emit) {
					result.append(" ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ");
				}
			} else if (i == PREV_MONTH_INDEX[browserIndex]) {
				if (emit) {
					result.append(imageDir + "/previous_solid.png");
				}
			} else if (i == NEXT_MONTH_A_ATTRIB_INDEX[browserIndex]) {
				if (emit) {
					result.append(" ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ");
				}
			} else if (i == NEXT_MONTH_INDEX[browserIndex]) {
				if (emit) {
					result.append(imageDir + "/forward_single_arrow.png");
				}
			} else if (i == TODAY_A_ATTRIB_INDEX[browserIndex]) {
				if (emit) {
					result.append(" ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ");
				}
			} else if (i == TODAY_INDEX[browserIndex]) {
				if (emit) {
					result.append(Utils.translate(this.localeString_, "Today"));
				}
			} else if (i == DIVIDER_IMAGE_INDEX[browserIndex]) {
				if (emit) {
					result.append(imageDir);
				}
			} else if (i == CANCEL_A_ATTRIB_INDEX[browserIndex]) {
				if (emit) {
					result.append(" ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ");
				}
			} else if (i == CANCEL_INDEX[browserIndex]) {
				if (emit) {
					result.append(Utils.translate(this.localeString_, "Cancel"));
				}
			} else if (i == CANCEL_CALLBACK_INDEX[browserIndex]) {
				if (emit) {
					result.append(this.cancelCallback_);
				}
			} else if (i == OK_A_ATTRIB_INDEX[browserIndex]) {
				if (emit) {
					result.append(" ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ");
				}
			} else if (i == OK_INDEX[browserIndex]) {
				if (emit) {
					result.append(Utils.translate(this.localeString_, "OK"));
				}
			} else if (i == OK_CALLBACK_INDEX[browserIndex]) {
				if (emit) {
					result.append(this.okCallback_);
				}
			} else if (i == GEN_CALENDAR_HDR_INDEX[browserIndex]) {
				if (emit) {
					result.append("\n<TR>\n");
				}

				for (int dow = 0; dow < 7; ++dow) {
					if (emit) {
						result.append("<TD WIDTH='18' HEIGHT='16' CLASS='datePickerDayLetter'>");
					}

					if (emit) {
						result.append(dayletters_key.charAt(dow) + "</TD>\n");
					}
				}

				if (emit) {
					result.append("</TR>\n");
				}
			} else if (i != GEN_CALENDAR_INDEX[browserIndex]) {
				if (this.isEmbedded_ && i == EMBEDDED_STOP[browserIndex]) {
					emit = false;
				} else if (this.isEmbedded_ && i == EMBEDDED_START[browserIndex]) {
					emit = true;
				} else if (emit) {
					result.append("\n");
				}
			} else {
				String selectionCallback = this.isEmbedded_ ? this.okCallback_ : "null";
				boolean currentMonth = false;
				int currentDay = 29;
				String dayClass = "datePickerDay";

				for (int w = 0; w < 6; ++w) {
					if (emit) {
						result.append("\n<TR>\n");
					}

					for (int dow = 0; dow < 7; ++dow) {
						if (currentMonth) {
							dayClass = "datePickerDayThisMonth";
						} else {
							dayClass = "datePickerDay";
						}

						if (currentDay == 15) {
							dayClass = "datePickerDayToday";
						} else if (currentDay == 20) {
							dayClass = "datePickerDaySelected";
						}

						String id = "cell" + w + dow;
						if (emit) {
							result.append("<TD ID='" + id + "' WIDTH='18px' HEIGHT='18px' CLASS='" + dayClass + "'>\n");
						}

						if (emit) {
							result.append(
									"<A ONMOUSEOVER=\"javascript:this.style.cursor='pointer'\" ONCLICK='javascript:i2uiDatePickerSelect(\""
											+ id + "\",\"" + selectionCallback + "\")'>" + currentDay + "</A>\n");
						}

						if (emit) {
							result.append("</TD>\n");
						}

						++currentDay;
						if (currentDay > 31) {
							currentDay = 1;
							currentMonth = !currentMonth;
						}
					}

					if (emit) {
						result.append("</TR>\n");
					}
				}
			}
		}

		try {
			this.pageContext.getOut().write(result.toString());
			return 6;
		} catch (IOException var16) {
			throw new JspException("IO Error: " + var16.getMessage());
		}
	}

	public void release() {
		super.release();
		this.id_ = null;
		this.okCallback_ = null;
		this.cancelCallback_ = null;
		this.localeString_ = null;
		this.isEmbedded_ = false;
	}
}