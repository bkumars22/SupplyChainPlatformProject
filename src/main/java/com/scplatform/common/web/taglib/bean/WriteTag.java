/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import com.scplatform.common.web.taglib.MessageResources;
import com.scplatform.common.web.taglib.TagUtils;

public class WriteTag extends TagSupport {
    public static final String SQL_TIMESTAMP_FORMAT_KEY = "com.scplatform.common.web.taglib.bean.format.sql.timestamp";
    public static final String SQL_DATE_FORMAT_KEY = "com.scplatform.common.web.taglib.bean.format.sql.date";
    public static final String SQL_TIME_FORMAT_KEY = "com.scplatform.common.web.taglib.bean.format.sql.time";
    public static final String DATE_FORMAT_KEY = "com.scplatform.common.web.taglib.bean.format.date";
    public static final String INT_FORMAT_KEY = "com.scplatform.common.web.taglib.bean.format.int";
    public static final String FLOAT_FORMAT_KEY = "com.scplatform.common.web.taglib.bean.format.float";
    protected static MessageResources messages = MessageResources.getMessageResources("com.scplatform.common.web.taglib.bean.LocalStrings");
    protected boolean filter = true;
    protected boolean ignore = false;
    protected String name = null;
    protected String property = null;
    protected String scope = null;
    protected String formatStr = null;
    protected String formatKey = null;
    protected String localeKey = null;
    protected String bundle = null;

    public boolean getFilter() {
        return this.filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean getIgnore() {
        return this.ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getFormat() {
        return this.formatStr;
    }

    public void setFormat(String formatStr) {
        this.formatStr = formatStr;
    }

    public String getFormatKey() {
        return this.formatKey;
    }

    public void setFormatKey(String formatKey) {
        this.formatKey = formatKey;
    }

    public String getLocale() {
        return this.localeKey;
    }

    public void setLocale(String localeKey) {
        this.localeKey = localeKey;
    }

    public String getBundle() {
        return this.bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public int doStartTag() throws JspException {
        if (this.ignore && TagUtils.getInstance().lookup(this.pageContext, this.name, this.scope) == null) {
            return 0;
        } else {
            Object value = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property, this.scope);
            if (value == null) {
                return 0;
            } else {
                String output = this.formatValue(value);
                if (this.filter) {
                    TagUtils.getInstance().write(this.pageContext, TagUtils.getInstance().filter(output));
                } else {
                    TagUtils.getInstance().write(this.pageContext, output);
                }
                return 0;
            }
        }
    }

    protected String retrieveFormatString(String formatKey) throws JspException {
        String result = TagUtils.getInstance().message(this.pageContext, this.bundle, this.localeKey, formatKey);
        return result == null || result.startsWith("???") && result.endsWith("???") ? null : result;
    }

    protected String formatValue(Object valueToFormat) throws JspException {
        Object format = null;
        Locale locale = TagUtils.getInstance().getUserLocale(this.pageContext, this.localeKey);
        boolean formatStrFromResources = false;
        String formatString = this.formatStr;
        if (valueToFormat instanceof String) {
            return (String) valueToFormat;
        } else {
            if (formatString == null && this.formatKey != null) {
                formatString = this.retrieveFormatString(this.formatKey);
                if (formatString != null) {
                    formatStrFromResources = true;
                }
            }
            if (valueToFormat instanceof Number) {
                if (formatString == null) {
                    if (!(valueToFormat instanceof Byte) && !(valueToFormat instanceof Short) && !(valueToFormat instanceof Integer) && !(valueToFormat instanceof Long) && !(valueToFormat instanceof BigInteger)) {
                        if (valueToFormat instanceof Float || valueToFormat instanceof Double || valueToFormat instanceof BigDecimal) {
                            formatString = this.retrieveFormatString("com.scplatform.common.web.taglib.bean.format.float");
                        }
                    } else {
                        formatString = this.retrieveFormatString("com.scplatform.common.web.taglib.bean.format.int");
                    }
                    if (formatString != null) {
                        formatStrFromResources = true;
                    }
                }
                if (formatString != null) {
                    try {
                        format = NumberFormat.getNumberInstance(locale);
                        if (formatStrFromResources) {
                            ((DecimalFormat) format).applyLocalizedPattern(formatString);
                        } else {
                            ((DecimalFormat) format).applyPattern(formatString);
                        }
                    } catch (IllegalArgumentException arg8) {
                        JspException ex = new JspException(messages.getMessage("write.format", formatString));
                        TagUtils.getInstance().saveException(this.pageContext, ex);
                        throw ex;
                    }
                }
            } else if (valueToFormat instanceof Date) {
                if (formatString == null) {
                    if (valueToFormat instanceof Timestamp) {
                        formatString = this.retrieveFormatString("com.scplatform.common.web.taglib.bean.format.sql.timestamp");
                    } else if (valueToFormat instanceof java.sql.Date) {
                        formatString = this.retrieveFormatString("com.scplatform.common.web.taglib.bean.format.sql.date");
                    } else if (valueToFormat instanceof Time) {
                        formatString = this.retrieveFormatString("com.scplatform.common.web.taglib.bean.format.sql.time");
                    } else if (valueToFormat instanceof Date) {
                        formatString = this.retrieveFormatString("com.scplatform.common.web.taglib.bean.format.date");
                    }
                }
                if (formatString != null) {
                    format = new SimpleDateFormat(formatString, locale);
                }
            }
            return format != null ? ((Format) format).format(valueToFormat) : valueToFormat.toString();
        }
    }

    public void release() {
        super.release();
        this.filter = true;
        this.ignore = false;
        this.name = null;
        this.property = null;
        this.scope = null;
        this.formatStr = null;
        this.formatKey = null;
        this.localeKey = null;
        this.bundle = null;
    }
}