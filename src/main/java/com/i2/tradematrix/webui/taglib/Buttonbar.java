/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Buttonbar extends BodyTagSupport {
	boolean hasPadding = true;
	String alignment = null;
	int newrowcount = 6;
	int count = 0;
	int textCount = 0;
	boolean standalone = true;
	boolean previousIsDivider = false;

	public void setNopadding(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.hasPadding = false;
		}

	}

	public void setAligncontents(String value) {
		this.alignment = value.toLowerCase();
	}

	public void setNewrowcount(String value) {
		if (value != null && value.length() > 0) {
			try {
				this.newrowcount = Integer.parseInt(value);
			} catch (Exception var3) {
				this.newrowcount = 6;
			}
		}

	}

	public void setPreviousIsDivider() {
		this.previousIsDivider = true;
	}

	public void startNewButton(Button theBtn) throws JspException {
		try {
			if (!theBtn.disabled) {
				if (this.count > 0 && this.count % this.newrowcount == 0) {
					this.endOneRow();
					this.startOneRow();
					this.textCount = 0;
				}

				++this.count;
			}

			if (this.count > 1 && !this.previousIsDivider && this.standalone) {
				this.pageContext.getOut().write("<TD width=\"6px\" style=\"font-size:1px\" nowrap=\"yes\">&nbsp;</TD>");
			}

			this.previousIsDivider = false;
			this.pageContext.getOut().write("<TD nowrap=\"yes\">");
		} catch (IOException var3) {
			throw new JspException("IO Error: " + var3.getMessage());
		}
	}

	public void endNewButton() throws JspException {
		this.endNewButton(0);
	}

	public void endNewButton(int size) throws JspException {
		this.textCount += size;

		try {
			this.pageContext.getOut().write("</TD>");
		} catch (IOException var3) {
			throw new JspException("IO Error: " + var3.getMessage());
		}
	}

	public int doStartTag() throws JspException {
		this.startOneRow();
		return 2;
	}

	private void startOneRow() throws JspException {
		StringBuffer result = new StringBuffer();
		Footer footer = (Footer) findAncestorWithClass(this, Footer.class);
		if (footer != null) {
			this.standalone = false;
		}

		Header header = (Header) findAncestorWithClass(this, Header.class);
		if (header != null) {
			this.standalone = false;
		}

		try {
			result.append("<TABLE border=\"0px\" cellspacing=\"0px\" cellpadding=\"0px\"");
			result.append(" width=\"100%\"");
			result.append("><TR>");
			if (this.alignment != null && this.alignment.equals("right")) {
				result.append("<TD width=\"100%\" nowrap=\"yes\">&#160;</TD>");
			}

			this.pageContext.getOut().write(result.toString());
		} catch (IOException var5) {
			throw new JspException("IO Error: " + var5.getMessage());
		}
	}

	private void endOneRow() throws JspException {
		try {
			if ((this.alignment != null && this.alignment.equals("left") || this.hasPadding) && this.standalone) {
				this.pageContext.getOut().write("<TD width=\"100%\" nowrap=\"yes\">&#160;</TD>");
			}

			if (this.alignment != null && this.alignment.equals("right") && this.hasPadding) {
				this.pageContext.getOut().write("<TD width=\"1px\" style=\"font-size:1px\" nowrap=\"yes\">&#160;</TD>");
			}

			this.pageContext.getOut().write("</TR></TABLE>");
		} catch (IOException var2) {
			throw new JspException("IO Error: " + var2.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		try {
			if (this.bodyContent != null) {
				this.bodyContent.writeOut(this.bodyContent.getEnclosingWriter());
			}

			this.endOneRow();
		} catch (IOException var5) {
			throw new JspException(var5.getMessage());
		} finally {
			this.reset();
		}

		return 6;
	}

	public void release() {
		super.release();
		this.reset();
	}

	private void reset() {
		this.hasPadding = true;
		this.alignment = null;
		this.count = 0;
		this.textCount = 0;
		this.standalone = true;
		this.previousIsDivider = false;
	}
}