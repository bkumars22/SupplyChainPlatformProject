/**
 *      ItemAdditionalAttributeManager.java
 *      Created on Aug 13, 2013
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.config.util.PcmConfigUtil;

/**
 * @author sgupta
 */
public enum AdditionalAttributeManager {
    ITEM("item"), COST("cost"),BUSINESS_ENTITY("businessEntity"),ITEM_AVL("itemAvl");

    private static final String PCM_ADDITIONAL_ATTRIBUTES_CONFIG = "pcm.additional.attributes.config";

    private String attributesDefnBeanName;
    
    private AdditionalAttributeManager(String beanName) {
        this.attributesDefnBeanName = beanName;
    }

    /**
     * Get the AttributesDefinition object
     * 
     * @return
     */
    public AttributesDefn getAdditionalAttributesDefinition() {
        // Get the configuration file path from PcmConfigUtil via SpringContextHolder
        PcmConfigUtil configUtil = SpringContextHolder.getBean(PcmConfigUtil.class);
        String configFilePath = configUtil.getString(PCM_ADDITIONAL_ATTRIBUTES_CONFIG);
        
        if (configFilePath == null || configFilePath.trim().isEmpty()) {
            return null;
        }
        
        // Load ApplicationContext from the specified config file
        ApplicationContext ctx = new ClassPathXmlApplicationContext(configFilePath);
        
        AttributesDefn defns = null;
        if(ctx.containsBean(this.attributesDefnBeanName)) {
            defns = ctx.getBean(this.attributesDefnBeanName, AttributesDefn.class);
        }
        return defns;
    }

    /**
     * Get attribute definitions organized by group name
     * 
     * @return
     */
    public Map<String,List<AttributeDefn>> getAdditionalAttributesDefinitionsByGroupName() {
        AttributesDefn defns = getAdditionalAttributesDefinition();
        if (defns == null) {
            return Collections.emptyMap();
        }
        return defns.getAdditionalAttributesDefinitionsByGroup();
    }
    
    /**
     * Get attribute definitions as a list
     * 
     * @return
     */
    public List<AttributeDefn> getAdditionalAttributeDefinitionList() {
        AttributesDefn defns = getAdditionalAttributesDefinition();
        if (defns == null) {
            return Collections.emptyList();
        }
        return defns.getAttributeDefinitionList();
    }
    
    /**
     * Find an attribute definition by group name and attribute name
     * 
     * @param groupName
     * @param attrName
     * @return the attribute definition or null is not found
     */
    public AttributeDefn getAttributeDefn(String groupName, String attrName) {
        return getAttributeDefn(groupName, attrName, null);
    }
    
    /**
     * Find an attribute definition by group name and attribute name or associate attribute
     * 
     * @param groupName
     * @param attrName
     * @param associatedAttribute
     * @return the attribute definition or null is not found
     */
    public AttributeDefn getAttributeDefn(String groupName, String attrName, String associatedAttribute) {
        AttributesDefn defns = getAdditionalAttributesDefinition();
        if (defns == null) return null;
        return defns.getAttributeDefn(groupName, attrName,  associatedAttribute);
    }

    /**
     * Build an attribute. If attributes definition do not exist for item then this method will create an attribute
     * without validating against the definition
     * 
     * @param attrName
     * @param type
     * @param value
     * @param description
     * @param attrGrp
     * @return
     */
    public Attribute buildAttribute(String attrName, String type, String value, String description,
            AttributeGroup attrGrp) {
        Validate.notNull(attrGrp, "Attribute group cannot be null");
        AttributesDefn defn = getAdditionalAttributesDefinition();
        if (defn == null) {
            return createAttribute(attrName, AttributeType.valueOf(type.toUpperCase()), value, description, attrGrp);
        } else {
            AttributeDefn attrDefn = getAttributeDefn(attrGrp.getAttributeGroupName(), attrName);
            if (attrDefn == null) {
                throw new IllegalArgumentException("Cannot find attribute definition with name " + attrName + " in "
                        + attrGrp.toString());
            }
            return createAttribute(attrName, attrDefn.getAttributeType(),value, description, attrGrp);
        }
    }
    
    /**
     * Build an attribute. If attributes definition do not exist for item then this method will return null.
     * 
     * @param attrName
     * @param value
     * @param associatedAttribute
     * @return
     */
    public Attribute buildAttribute(String attrName, String value, String associatedAttribute) {
        AttributesDefn defn = getAdditionalAttributesDefinition();
        if (defn == null) {
            return null;
        } else {
            AttributeDefn attrDefn = getAttributeDefn(null, attrName, associatedAttribute);
            if (attrDefn == null) {
                if(attrName != null) {
                    throw new IllegalArgumentException("Cannot find attribute definition with name " + attrName);
                } else if (associatedAttribute != null) {
                    throw new IllegalArgumentException("Cannot find attribute definition with associated attribute " + associatedAttribute);
                } else {
                    throw new IllegalArgumentException("Cannot find attribute definition.  No name or associated attribute provided");
                }
            }
            return createAttribute(attrDefn.getName(), attrDefn.getAttributeType(),value, attrDefn.getAssociatedAttribute());
        }
    }
    
    /**
     * Build an attribute from a known definition
     * 
     * @param attrDefn
     * @param value
     * @return
     */
    public Attribute buildAttributeFromDefn(AttributeDefn attrDefn, String value) {
        if (attrDefn == null) {
            throw new IllegalArgumentException("Cannot find attribute definition");
        }
        return createAttribute(attrDefn.getName(), attrDefn.getAttributeType(), value,
                attrDefn.getAssociatedAttribute());
    }

    /**
     * Create an attribute
     * 
     * @param attrNameg
     * @param attributeType
     * @param value
     * @param attrGrp
     * @return
     */
    private Attribute createAttribute(String attrName, AttributeType attributeType, String value, String description,
            AttributeGroup attrGrp) {
        Attribute attr = new Attribute();
        attr.setAttrGroup(attrGrp);
        attr.setAttrType(attributeType);
        attr.setAttrName(attrName);
        attr.setAttrValue(value);
        attr.setDescription(description);
        return attr;
    }
    
    /**
     * Create an attribute
     * 
     * @param attrNameg
     * @param attributeType
     * @param value
     * @param associatedAttribute
     * @return
     */
    private Attribute createAttribute(String attrName, AttributeType attributeType, String value, String associatedAttribute) {
        Attribute attr = new Attribute();
        attr.setAttrType(attributeType);
        attr.setAttrName(attrName);
        attr.setAttrValue(value);
        attr.setAssociatedAttribute(associatedAttribute);
        return attr;
    }

}
