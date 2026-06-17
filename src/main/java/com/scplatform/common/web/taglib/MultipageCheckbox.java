/*
 * Checkbox tag that will support paging
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.util.Collection;

import jakarta.servlet.jsp.JspException;

/**
 * Tag for input fields of type "checkbox". This differs from CheckboxTag
 * because it assumes that the underlying property is an array getter (of any
 * supported primitive type, or String), and the checkbox is initialized to
 * "checked" if the value listed for the "value" attribute is present in the
 * values returned by the property getter.
 * 
 * @version $Revision: 1.25 $ $Date: 2004/03/14 06:23:46 $
 */
public class MultipageCheckbox extends BaseHandlerTag {
    // ----------------------------------------------------- Instance Variables
    /**
     * The constant String value to be returned when this checkbox is selected
     * and the form is submitted.
     */
    protected String constant = null;

    /**
     * The name of the tag.
     */
    protected String name = null;

    /**
     * The value which will mark this checkbox as "checked" if present in the
     * array returned by our collection
     */
    protected String value = null;

    protected Collection currentSet;
    protected Collection checkSet;

    public String getName() {
	return (this.name);
    }

    public void setName(String name) {
	this.name = name;
    }

    /**
     * Return the server value.
     */
    public String getValue() {
	return (this.value);
    }

    /**
     * Set the server value.
     * 
     * @param value
     *            The new server value
     */
    public void setValue(String value) {
	this.value = value;
    }

    public Collection getCheckSet() {
	return checkSet;
    }

    public void setCheckSet(Collection c) {
	checkSet = c;
    }

    public void setCurrentSet(Collection c) {
	currentSet = c;
    }

    // --------------------------------------------------------- Public Methods
    /**
     * Process the beginning of this tag.
     * 
     * @exception JspException
     *                if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {
	// Defer processing until the end of this tag is encountered
	this.constant = null;
	return EVAL_BODY_BUFFERED;
    }

    /**
     * Save the body contents of this tag as the constant that we will be
     * returning.
     * 
     * @exception JspException
     *                if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {
	if (bodyContent != null) {
	    this.constant = bodyContent.getString().trim();
	}
	if ("".equals(this.constant)) {
	    this.constant = null;
	}
	return SKIP_BODY;
    }

    /**
     * Render an input element for this tag.
     * 
     * @exception JspException
     *                if a JSP exception has occurred
     */
    public int doEndTag() throws JspException {
	// Create an appropriate "input" element based on our parameters
	StringBuffer results = new StringBuffer("<input type=\"checkbox\"");
	results.append(" name=\"");
	results.append(this.name);
	results.append("\"");
	if (accesskey != null) {
	    results.append(" accesskey=\"");
	    results.append(accesskey);
	    results.append("\"");
	}
	if (tabindex != null) {
	    results.append(" tabindex=\"");
	    results.append(tabindex);
	    results.append("\"");
	}
	results.append(" value=\"");
	String value = (this.value == null) ? this.constant : this.value;
	if (value == null) {
	    JspException e = new JspException("multiboxTag.value is null");
	    throw e;
	}
	results.append(TagUtils.getInstance().filter(value));
	results.append("\"");
	if (checkSet.contains(value)) {
	    results.append(" checked=\"checked\"");
	    if (currentSet != null) {
		currentSet.add(value);
	    }
	}
	results.append(prepareEventHandlers());
	results.append(prepareStyles());
	results.append(getElementClose());
	TagUtils.getInstance().write(pageContext, results.toString());
	return EVAL_PAGE;
    }

    /**
     * Release any acquired resources.
     */
    public void release() {
	super.release();
	constant = null;
	name = null;
	checkSet = null;
	currentSet = null;
	value = null;
    }
}
