/**
 *	FlexAttributeDefn.java
 *	Created on Aug 12, 2013
 *     
 *	Copyright (c) 2010 E2open, Inc.
 *	All Rights Reserved.
 *
 *	THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *	The copyright notice above does not evidence any
 *	actual or intended publication of such source code. 
 *	
 *	Author: sgupta
 */
package com.scplatform.pcm.common.entity;

import com.scplatform.pcm.util.validator.Validator;

/**
 * Defines a flex attribute
 * 
 * @author sgupta
 */
public class FlexAttributeDefn {

    private final String name;
    private AttributeType attributeType;
    private Validator attributeValueValidator;
    private boolean isRequired = false;
    private String associatedAttribute;

    /**
     * @param name
     */
    public FlexAttributeDefn(String name) {
        super();
        this.name = name;
    }

    // GETTERS AND SETTERS
    public String getName() {
        return this.name;
    }

    /**
     * @return the attributeType
     */
    public AttributeType getAttributeType() {
        return this.attributeType;
    }

    /**
     * @param attributeType
     *            the attributeType to set
     */
    public void setAttributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    /**
     * @return the attributeValueValidator
     */
    public Validator getAttributeValueValidator() {
        return this.attributeValueValidator;
    }

    /**
     * @param attributeValueValidator
     *            the attributeValueValidator to set
     */
    public void setAttributeValueValidator(Validator attributeValueValidator) {
        this.attributeValueValidator = attributeValueValidator;
    }

    /**
     * @return the isRequired
     */
    public boolean isRequired() {
        return this.isRequired;
    }

    /**
     * @param isRequired
     *            the isRequired to set
     */
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    /**
     * @return the associatedAttribute
     */
    public String getAssociatedAttribute() {
        return associatedAttribute;
    }

    /**
     * @param associatedAttribute
     *            the associatedAttribute to set
     */
    public void setAssociatedAttribute(String associatedAttribute) {
        this.associatedAttribute = associatedAttribute;
    }

}
