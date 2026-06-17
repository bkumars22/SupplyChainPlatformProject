/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;

import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.MessageResources;

public class OptionsCollectionTag extends TagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected boolean filter = true;
    protected String label = "label";
    protected String name = "com.scplatform.common.web.taglib.html.BEAN";
    protected String property = null;
    private String style = null;
    private String styleClass = null;
    protected String value = "value";

    public boolean getFilter() {
	return this.filter;
    }

    public void setFilter(boolean filter) {
	this.filter = filter;
    }

    public String getLabel() {
	return this.label;
    }

    public void setLabel(String label) {
	this.label = label;
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

    public String getStyle() {
	return this.style;
    }

    public void setStyle(String style) {
	this.style = style;
    }

    public String getStyleClass() {
	return this.styleClass;
    }

    public void setStyleClass(String styleClass) {
	this.styleClass = styleClass;
    }

    public String getValue() {
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public int doStartTag() throws JspException {
	SelectTag selectTag = (SelectTag) this.pageContext.getAttribute("com.scplatform.common.web.taglib.html.SELECT");
	if (selectTag == null) {
	    JspException collection1 = new JspException(messages.getMessage("optionsCollectionTag.select"));
	    TagUtils.getInstance().saveException(this.pageContext, collection1);
	    throw collection1;
	} else {
	    Object collection = TagUtils.getInstance().lookup(this.pageContext, this.name, this.property,
		    (String) null);
	    if (collection == null) {
		JspException iter1 = new JspException(messages.getMessage("optionsCollectionTag.collection"));
		TagUtils.getInstance().saveException(this.pageContext, iter1);
		throw iter1;
	    } else {
		Iterator iter = this.getIterator(collection);
		StringBuffer sb = new StringBuffer();

		while (iter.hasNext()) {
		    Object bean = iter.next();
		    Object beanLabel = null;
		    Object beanValue = null;

		    JspException stringValue;
		    JspException jspe;
		    Throwable stringValue2;
		    try {
			beanLabel = PropertyUtils.getProperty(bean, this.label);
			if (beanLabel == null) {
			    beanLabel = "";
			}
		    } catch (IllegalAccessException arg10) {
			stringValue = new JspException(messages.getMessage("getter.access", this.label, bean));
			TagUtils.getInstance().saveException(this.pageContext, stringValue);
			throw stringValue;
		    } catch (InvocationTargetException arg11) {
			stringValue2 = arg11.getTargetException();
			jspe = new JspException(
				messages.getMessage("getter.result", this.label, stringValue2.toString()));
			TagUtils.getInstance().saveException(this.pageContext, jspe);
			throw jspe;
		    } catch (NoSuchMethodException arg12) {
			stringValue = new JspException(messages.getMessage("getter.method", this.label, bean));
			TagUtils.getInstance().saveException(this.pageContext, stringValue);
			throw stringValue;
		    }

		    try {
			beanValue = PropertyUtils.getProperty(bean, this.value);
			if (beanValue == null) {
			    beanValue = "";
			}
		    } catch (IllegalAccessException arg13) {
			stringValue = new JspException(messages.getMessage("getter.access", this.value, bean));
			TagUtils.getInstance().saveException(this.pageContext, stringValue);
			throw stringValue;
		    } catch (InvocationTargetException arg14) {
			stringValue2 = arg14.getTargetException();
			jspe = new JspException(
				messages.getMessage("getter.result", this.value, stringValue2.toString()));
			TagUtils.getInstance().saveException(this.pageContext, jspe);
			throw jspe;
		    } catch (NoSuchMethodException arg15) {
			stringValue = new JspException(messages.getMessage("getter.method", this.value, bean));
			TagUtils.getInstance().saveException(this.pageContext, stringValue);
			throw stringValue;
		    }

		    String stringLabel = beanLabel.toString();
		    String stringValue1 = beanValue.toString();
		    this.addOption(sb, stringLabel, stringValue1, selectTag.isMatched(stringValue1));
		}

		TagUtils.getInstance().write(this.pageContext, sb.toString());
		return 0;
	    }
	}
    }

    public void release() {
	super.release();
	this.filter = true;
	this.label = "label";
	this.name = "com.scplatform.common.web.taglib.html.BEAN";
	this.property = null;
	this.style = null;
	this.styleClass = null;
	this.value = "value";
    }

    protected void addOption(StringBuffer sb, String label, String value, boolean matched) {
	sb.append("<option value=\"");
	if (this.filter) {
	    sb.append(TagUtils.getInstance().filter(value));
	} else {
	    sb.append(value);
	}

	sb.append("\"");
	if (matched) {
	    sb.append(" selected=\"selected\"");
	}

	if (this.style != null) {
	    sb.append(" style=\"");
	    sb.append(this.style);
	    sb.append("\"");
	}

	if (this.styleClass != null) {
	    sb.append(" class=\"");
	    sb.append(this.styleClass);
	    sb.append("\"");
	}

	sb.append(">");
	if (this.filter) {
	    sb.append(TagUtils.getInstance().filter(label));
	} else {
	    sb.append(label);
	}

	sb.append("</option>\r\n");
    }

    protected Iterator getIterator(Object collection) throws JspException {
	if (collection.getClass().isArray()) {
	    collection = Arrays.asList((Object[]) ((Object[]) collection));
	}

	if (collection instanceof Collection) {
	    return ((Collection) collection).iterator();
	} else if (collection instanceof Iterator) {
	    return (Iterator) collection;
	} else if (collection instanceof Map) {
	    return ((Map) collection).entrySet().iterator();
	} else if (collection instanceof Enumeration) {
	    return this.toIterator((Enumeration) collection);
	} else {
	    throw new JspException(messages.getMessage("optionsCollectionTag.iterator", collection.toString()));
	}
    }

    private Iterator toIterator(Enumeration enumeration) {
	return new Iterator() {
	    public boolean hasNext() {
		return enumeration.hasMoreElements();
	    }

	    public Object next() {
		if (!enumeration.hasMoreElements()) {
		    throw new NoSuchElementException("Enumeration has no more elements");
		}

		return enumeration.nextElement();
	    }

	    public void remove() {
		throw new UnsupportedOperationException("remove");
	    }
	};
    }
}
