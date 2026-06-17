/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.service;


import org.apache.commons.lang3.StringUtils;

public class SearchParameterMultiText extends SearchParameterText {
	protected int numRows = 1;
	protected String delimiter = ",";

	public SearchParameterMultiText(String name, String labelKey, String delimiter) {
		super(name, labelKey);
		delimiter = delimiter;
	}

	public String getType() {
		return "MULTITEXT";
	}

	public void setRows(int numRows) {
		this.numRows = numRows;
	}

	public int getRows() {
		return numRows;
	}

	public void setDelimiter(String delimiter) {
		if (delimiter != null) {
			this.delimiter = delimiter;
		}
	}

	public String getDelimiter() {
		return delimiter;
	}

	public boolean isValueArray() {
		return false;
	}

	public Object getValueForSQL() {
		if (value instanceof Object[]) {
			StringBuilder sb = new StringBuilder();
			for (Object o : (Object[]) value) {
				if (!o.toString().trim().isEmpty()) {
					sb.append(o.toString());
					sb.append(delimiter);
				}
			}
			value = sb.substring(0, sb.length() - 1);
		}
		String work = StringUtils.strip((String) value);
		if (work != null) {
			if (getMatchType() == MatchType.IEXACT) {
				work = work.toLowerCase();
			}
			return trimArray(work.split(delimiter));
		} else {
			return work;
		}
	}

	public String[] trimArray(String[] input) {
		for (int i = 0; i < input.length; i++) {
			input[i] = input[i].trim();
		}
		return input;
	}

}
