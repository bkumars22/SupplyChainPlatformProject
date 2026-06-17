/**
 *      FlexAttributesDefn.java
 *      Created on Aug 12, 2013
 *     
 *      Copyright (c) 2010 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code. 
 *      
 *      Author: sgupta
 */
package com.scplatform.pcm.common.entity;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.scplatform.pcm.util.validator.Errors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Defines attributes for an entity type
 * 
 * @author sgupta
 */
public class FlexAttributesDefn {

    public AttributeEntityType entityType;
    public List<FlexAttributeDefn> attributeDefinitionList = new ArrayList<FlexAttributeDefn>();

    /**
     * @param entityType
     */
    public FlexAttributesDefn(AttributeEntityType entityType) {
        super();
        this.entityType = entityType;
    }

    /**
     * Validate attributes against definition
     * 
     * @param attributes
     * @param errors
     */
    public void validateAttributes(Collection<Attribute> attributes, Errors errors) {
        // TODO
    }
    
    /**
     * Find a flex attribute definition by name or associated attribute. If an attribute defn is not found, null is
     * returned.
     * 
     * @param attrName
     * @param associatedAttribute
     * @return
     */
    public FlexAttributeDefn getFlexAttributeDefn(final String attrName, final String associatedAttribute) {
        if (StringUtils.isBlank(attrName) && StringUtils.isBlank(associatedAttribute)) {
            return null;
        }
        Predicate finder = new Predicate() {

            @Override
            public boolean evaluate(Object arg0) {
                FlexAttributeDefn defn = (FlexAttributeDefn) arg0;
                EqualsBuilder eb = new EqualsBuilder();
                if (attrName != null) {
                    eb.append(attrName, defn.getName());
                } else if (associatedAttribute != null) {
                    eb.append(associatedAttribute, defn.getAssociatedAttribute());
                }
                return eb.isEquals();
            }
        };
        return (FlexAttributeDefn) CollectionUtils.find(attributeDefinitionList, finder);
    }

    /**
     * @return the entityType
     */
    public AttributeEntityType getEntityType() {
        return this.entityType;
    }

    /**
     * @return the attributeDefinitionList
     */
    public List<FlexAttributeDefn> getAttributeDefinitionList() {
        return this.attributeDefinitionList;
    }

    /**
     * @param attributeDefinitionList
     *            the attributeDefinitionList to set
     */
    public void setAttributeDefinitionList(List<FlexAttributeDefn> attributeDefinitionList) {
        this.attributeDefinitionList = attributeDefinitionList;
    }
}
