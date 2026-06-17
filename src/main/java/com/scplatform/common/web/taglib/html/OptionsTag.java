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

public class OptionsTag extends TagSupport {
    protected static MessageResources messages = MessageResources
	    .getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    protected String collection = null;
    protected boolean filter = true;
    protected String labelName = null;
    protected String labelProperty = null;
    protected String name = null;
    protected String property = null;
    private String style = null;
    private String styleClass = null;

    public String getCollection() {
	return this.collection;
    }

    public void setCollection(String collection) {
	this.collection = collection;
    }

    public boolean getFilter() {
	return this.filter;
    }

    public void setFilter(boolean filter) {
	this.filter = filter;
    }

    public String getLabelName() {
	return this.labelName;
    }

    public void setLabelName(String labelName) {
	this.labelName = labelName;
    }

    public String getLabelProperty() {
	return this.labelProperty;
    }

    public void setLabelProperty(String labelProperty) {
	this.labelProperty = labelProperty;
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

    public int doStartTag() throws JspException {
	return 0;
    }

    public int doEndTag() throws JspException {
	SelectTag selectTag = (SelectTag) this.pageContext.getAttribute("com.scplatform.common.web.taglib.html.SELECT");
	if (selectTag == null) {
	    throw new JspException(messages.getMessage("optionsTag.select"));
	} else {
	    StringBuffer sb = new StringBuffer();
	    Iterator valuesIterator;
	    Object valueObject;
	    String label;
	    if (this.collection != null) {
		valuesIterator = this.getIterator(this.collection, (String) null);

		while (valuesIterator.hasNext()) {
		    Object labelsIterator = valuesIterator.next();
		    valueObject = null;
		    Object value = null;

		    Throwable labelObject;
		    try {
			valueObject = PropertyUtils.getProperty(labelsIterator, this.property);
			if (valueObject == null) {
			    valueObject = "";
			}
		    } catch (IllegalAccessException arg11) {
			throw new JspException(messages.getMessage("getter.access", this.property, this.collection));
		    } catch (InvocationTargetException arg12) {
			labelObject = arg12.getTargetException();
			throw new JspException(
				messages.getMessage("getter.result", this.property, labelObject.toString()));
		    } catch (NoSuchMethodException arg13) {
			throw new JspException(messages.getMessage("getter.method", this.property, this.collection));
		    }

		    try {
			if (this.labelProperty != null) {
			    value = PropertyUtils.getProperty(labelsIterator, this.labelProperty);
			} else {
			    value = valueObject;
			}

			if (value == null) {
			    value = "";
			}
		    } catch (IllegalAccessException arg8) {
			throw new JspException(
				messages.getMessage("getter.access", this.labelProperty, this.collection));
		    } catch (InvocationTargetException arg9) {
			labelObject = arg9.getTargetException();
			throw new JspException(
				messages.getMessage("getter.result", this.labelProperty, labelObject.toString()));
		    } catch (NoSuchMethodException arg10) {
			throw new JspException(
				messages.getMessage("getter.method", this.labelProperty, this.collection));
		    }

		    label = valueObject.toString();
		    this.addOption(sb, label, value.toString(), selectTag.isMatched(label));
		}
	    } else {
		valuesIterator = this.getIterator(this.name, this.property);
		Iterator labelsIterator1 = null;
		if (this.labelName != null || this.labelProperty != null) {
		    labelsIterator1 = this.getIterator(this.labelName, this.labelProperty);
		}

		String value1;
		for (; valuesIterator.hasNext(); this.addOption(sb, value1, label, selectTag.isMatched(value1))) {
		    valueObject = valuesIterator.next();
		    if (valueObject == null) {
			valueObject = "";
		    }

		    value1 = valueObject.toString();
		    label = value1;
		    if (labelsIterator1 != null && labelsIterator1.hasNext()) {
			Object labelObject1 = labelsIterator1.next();
			if (labelObject1 == null) {
			    labelObject1 = "";
			}

			label = labelObject1.toString();
		    }
		}
	    }

	    TagUtils.getInstance().write(this.pageContext, sb.toString());
	    return 6;
	}
    }

    public void release() {
	super.release();
	this.collection = null;
	this.filter = true;
	this.labelName = null;
	this.labelProperty = null;
	this.name = null;
	this.property = null;
	this.style = null;
	this.styleClass = null;
    }

    protected void addOption(StringBuffer sb, String value, String label, boolean matched) {
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

    protected Iterator getIterator(String name, String property) throws JspException {
	String beanName = name;
	if (name == null) {
	    beanName = "com.scplatform.common.web.taglib.html.BEAN";
	}

	Object bean = TagUtils.getInstance().lookup(this.pageContext, beanName, (String) null);
	if (bean == null) {
	    throw new JspException(messages.getMessage("getter.bean", beanName));
	} else {
	    Object collection = bean;
	    if (property != null) {
		try {
		    collection = PropertyUtils.getProperty(bean, property);
		    if (collection == null) {
			throw new JspException(messages.getMessage("getter.property", property));
		    }
		} catch (IllegalAccessException arg7) {
		    throw new JspException(messages.getMessage("getter.access", property, name));
		} catch (InvocationTargetException arg8) {
		    Throwable t = arg8.getTargetException();
		    throw new JspException(messages.getMessage("getter.result", property, t.toString()));
		} catch (NoSuchMethodException arg9) {
		    throw new JspException(messages.getMessage("getter.method", property, name));
		}
	    }

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
		throw new JspException(messages.getMessage("optionsTag.iterator", collection.toString()));
	    }
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
