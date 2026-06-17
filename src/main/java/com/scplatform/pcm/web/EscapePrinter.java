/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.web;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

public class EscapePrinter extends SimpleTagSupport {

	String charcter = "'";
	String escapeCharcter = "\\\\'";
	String character1 = "\"";
	String escapeCharcter1 = "\\\\\"";

	private String value;
	private boolean removeColon;

	public boolean isRemoveColon() {
		return removeColon;
	}

	public void setRemoveColon(boolean removeColon) {
		this.removeColon = removeColon;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		try {
			if (value.indexOf(charcter) != -1) {
				value = value.replaceAll(charcter, escapeCharcter);
			}
			if(value.indexOf(character1) != -1) {
				value = value.replaceAll(character1, escapeCharcter1);
			}
			if(value.indexOf(System.getProperty("line.separator")) != -1 || value.indexOf("\n") != -1) {
				if(value.indexOf(System.getProperty("line.separator")) != -1) {
					value = value.replaceAll(System.getProperty("line.separator"), "/n");
				}else if(value.indexOf("\n") != -1) {
					value = value.replaceAll("\n", "/n");
				}
			}
			if(removeColon == false) {
				out.print(charcter + value + charcter);
			}else {
				out.print(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}