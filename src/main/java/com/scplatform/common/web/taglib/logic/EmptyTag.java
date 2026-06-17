/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import jakarta.servlet.jsp.JspException;

import com.scplatform.common.web.taglib.TagUtils;

public class EmptyTag extends ConditionalTagBase {
    protected boolean condition() throws JspException {
	return this.condition(true);
    }

    protected boolean condition(boolean desired) throws JspException {
	if (this.name == null) {
	    JspException value1 = new JspException(messages.getMessage("empty.noNameAttribute"));
	    TagUtils.getInstance().saveException(this.pageContext, value1);
	    throw value1;
	} else {
	    Object value = null;
	    if (this.property == null) {
		value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.scope);
	    } else {
		value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, this.scope);
	    }

	    boolean empty = true;
	    if (value == null) {
		empty = true;
	    } else if (value instanceof String) {
		String mapValue = (String) value;
		empty = mapValue.length() < 1;
	    } else if (value instanceof Collection) {
		Collection mapValue1 = (Collection) value;
		empty = mapValue1.isEmpty();
	    } else if (value instanceof Map) {
		Map mapValue2 = (Map) value;
		empty = mapValue2.isEmpty();
	    } else if (value.getClass().isArray()) {
		empty = Array.getLength(value) == 0;
	    } else {
		empty = false;
	    }

	    return empty == desired;
	}
    }
}