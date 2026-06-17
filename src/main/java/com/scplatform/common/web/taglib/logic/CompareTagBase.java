/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import java.lang.reflect.InvocationTargetException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import org.apache.commons.beanutils.PropertyUtils;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public abstract class CompareTagBase extends ConditionalTagBase {
    protected static final int DOUBLE_COMPARE = 0;
    protected static final int LONG_COMPARE = 1;
    protected static final int STRING_COMPARE = 2;
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.logic.LocalStrings");
    public String value = null;

    public String getValue() {
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public void release() {
	super.release();
	this.value = null;
    }

    protected abstract boolean condition() throws JspException;

    protected boolean condition(int desired1, int desired2) throws JspException {
	byte type = -1;
	double doubleValue = 0.0D;
	long longValue = 0L;
	if (type < 0 && this.value.length() > 0) {
	    try {
		doubleValue = Double.parseDouble(this.value);
		type = 0;
	    } catch (NumberFormatException arg14) {
		;
	    }
	}

	if (type < 0 && this.value.length() > 0) {
	    try {
		longValue = Long.parseLong(this.value);
		type = 1;
	    } catch (NumberFormatException arg13) {
		;
	    }
	}

	if (type < 0) {
	    type = 2;
	}

	Object variable = null;
	if (this.cookie != null) {
	    Cookie[] result = ((HttpServletRequest) this.pageContext.getRequest()).getCookies();
	    if (result == null) {
		result = new Cookie[0];
	    }

	    for (int e = 0; e < result.length; ++e) {
		if (this.cookie.equals(result[e].getName())) {
		    variable = result[e].getValue();
		    break;
		}
	    }
	} else if (this.header != null) {
	    variable = ((HttpServletRequest) this.pageContext.getRequest()).getHeader(this.header);
	} else if (this.name != null) {
	    Object arg17 = TagUtils.getInstance().lookup(this.pageContext, this.name, this.scope);
	    if (this.property != null) {
		if (arg17 == null) {
		    JspException arg19 = new JspException(messages.getMessage("logic.bean", this.name));
		    TagUtils.getInstance().saveException(this.pageContext, arg19);
		    throw arg19;
		}

		try {
		    variable = PropertyUtils.getProperty(arg17, this.property);
		} catch (InvocationTargetException arg15) {
		    Object t = arg15.getTargetException();
		    if (t == null) {
			t = arg15;
		    }

		    TagUtils.getInstance().saveException(this.pageContext, (Throwable) t);
		    throw new JspException(messages.getMessage("logic.property", this.name, this.property,
			    ((Throwable) t).toString()));
		} catch (Throwable arg16) {
		    TagUtils.getInstance().saveException(this.pageContext, arg16);
		    throw new JspException(
			    messages.getMessage("logic.property", this.name, this.property, arg16.toString()));
		}
	    } else {
		variable = arg17;
	    }
	} else {
	    if (this.parameter == null) {
		JspException arg22 = new JspException(messages.getMessage("logic.selector"));
		TagUtils.getInstance().saveException(this.pageContext, arg22);
		throw arg22;
	    }

	    variable = this.pageContext.getRequest().getParameter(this.parameter);
	}

	if (variable == null) {
	    variable = "";
	}

	int arg18 = 0;
	if (type == 0) {
	    try {
		double arg20 = Double.parseDouble(variable.toString());
		if (arg20 < doubleValue) {
		    arg18 = -1;
		} else if (arg20 > doubleValue) {
		    arg18 = 1;
		}
	    } catch (NumberFormatException arg12) {
		arg18 = variable.toString().compareTo(this.value);
	    }
	} else if (type == 1) {
	    try {
		long arg21 = Long.parseLong(variable.toString());
		if (arg21 < longValue) {
		    arg18 = -1;
		} else if (arg21 > longValue) {
		    arg18 = 1;
		}
	    } catch (NumberFormatException arg11) {
		arg18 = variable.toString().compareTo(this.value);
	    }
	} else {
	    arg18 = variable.toString().compareTo(this.value);
	}

	if (arg18 < 0) {
	    arg18 = -1;
	} else if (arg18 > 0) {
	    arg18 = 1;
	}

	return arg18 == desired1 || arg18 == desired2;
    }
}
