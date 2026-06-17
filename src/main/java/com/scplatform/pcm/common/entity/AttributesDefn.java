/**
 *      AdditionalAttributesDefn.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.scplatform.pcm.util.common.MultiMapUtils;
import com.scplatform.pcm.util.validator.Errors;

/**
 * Defines attributes for an entity type
 * 
 * @author sgupta
 */
public class AttributesDefn {

    public AttributeEntityType entityType;
    public List<AttributeDefn> attributeDefinitionList = new ArrayList<AttributeDefn>();

    /**
     * @param entityType
     */
    public AttributesDefn(AttributeEntityType entityType) {
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
     * Find an attribute definition by groupName and name or associated attribute.
     * If an attribute defn is not found, null is returned.
     * 
     * @param groupName
     * @param attrName
     * @param associatedAttribute
     * @return
     */
    public AttributeDefn getAttributeDefn(final String groupName, final String attrName, final String associatedAttribute) {
        if (StringUtils.isBlank(attrName) && StringUtils.isBlank(associatedAttribute)) {
            return null;
        }
        Predicate finder = new Predicate() {

            @Override
            public boolean evaluate(Object arg0) {
                AttributeDefn defn = (AttributeDefn) arg0;
                EqualsBuilder eb = new EqualsBuilder();
                eb.append(groupName, defn.getGroupName());
                if(attrName != null) {
                    eb.append(attrName, defn.getName());
                } else if (associatedAttribute != null) {
                    eb.append(associatedAttribute, defn.getAssociatedAttribute());
                }
                return eb.isEquals();
            }
        };
        return (AttributeDefn) CollectionUtils.find(attributeDefinitionList, finder);
    }

    public Map<String, List<AttributeDefn>> getAdditionalAttributesDefinitionsByGroup() {
        MultiMapUtils.MapKeyFunctor<String, AttributeDefn> functor = new MultiMapUtils.MapKeyFunctor<String, AttributeDefn>() {

            @Override
            public String getKey(AttributeDefn ase) {
                return ase.getGroupName();
            }
        };
        return MultiMapUtils.organizeIntoMultiValueMap(this.getAttributeDefinitionList(), functor);
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
    public List<AttributeDefn> getAttributeDefinitionList() {
        return this.attributeDefinitionList;
    }

    /**
     * @param attributeDefinitionList the attributeDefinitionList to set
     */
    public void setAttributeDefinitionList(List<AttributeDefn> attributeDefinitionList) {
        this.attributeDefinitionList = attributeDefinitionList;
    }
}
