/**
 *      FlexAttributeManager.java
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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.config.util.PcmConfigUtil;

/**
 * @author sgupta
 */
public enum FlexAttributeManager {
    ITEM("item"), COST("cost"), BOM("bom"), BOMLINE("bomline"), COSTFORECAST("costforecast");

    private static final String PCM_FLEX_ATTRIBUTES_CONFIG = "pcm.flex.attributes.config";

    private String attributesDefnBeanName;

    private FlexAttributeManager(String beanName) {
        this.attributesDefnBeanName = beanName;
    }

    /**
     * Get the AttributesDefinition object
     * 
     * @return
     */
    public FlexAttributesDefn getFlexAttributesDefinition() {
        // Get the configuration file path from PcmConfigUtil via SpringContextHolder
        PcmConfigUtil configUtil = SpringContextHolder.getBean(PcmConfigUtil.class);
        String configFilePath = configUtil.getString(PCM_FLEX_ATTRIBUTES_CONFIG);
        
        if (configFilePath == null || configFilePath.trim().isEmpty()) {
            return null;
        }
        
        // Load ApplicationContext from the specified config file
        ApplicationContext ctx = new ClassPathXmlApplicationContext(configFilePath);
        
        FlexAttributesDefn defns = null;
        if (ctx.containsBean(this.attributesDefnBeanName)) {
            defns = ctx.getBean(this.attributesDefnBeanName, FlexAttributesDefn.class);
        }
        return defns;
    }

    /**
     * Get attribute definitions as a list
     * 
     * @return
     */
    public List<FlexAttributeDefn> getFlexAttributeDefinitionList() {
        FlexAttributesDefn defns = getFlexAttributesDefinition();
        if (defns == null) {
            return Collections.emptyList();
        }
        return defns.getAttributeDefinitionList();
    }

    /**
     * Find an attribute definition by group name and attribute name
     * 
     * @param attrName
     * @return the attribute definition or null is not found
     */
    public FlexAttributeDefn getAttributeDefn(String attrName) {
        return getFlexAttributeDefn(attrName, null);
    }

    /**
     * Find an attribute definition by group name and attribute name or associate attribute
     * 
     * @param attrName
     * @param associatedAttribute
     * @return the attribute definition or null is not found
     */
    public FlexAttributeDefn getFlexAttributeDefn(String attrName, String associatedAttribute) {
        FlexAttributesDefn defns = getFlexAttributesDefinition();
        if (defns == null)
            return null;
        return defns.getFlexAttributeDefn(attrName, associatedAttribute);
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
        FlexAttributesDefn defn = getFlexAttributesDefinition();
        if (defn == null) {
            return null;
        } else {
            FlexAttributeDefn attrDefn = getFlexAttributeDefn(attrName, associatedAttribute);
            if (attrDefn == null) {
                if (attrName != null) {
                    throw new IllegalArgumentException("Cannot find attribute definition with name " + attrName);
                } else if (associatedAttribute != null) {
                    throw new IllegalArgumentException("Cannot find attribute definition with associated attribute "
                            + associatedAttribute);
                } else {
                    throw new IllegalArgumentException(
                            "Cannot find attribute definition.  No name or associated attribute provided");
                }
            }
            return createAttribute(attrDefn.getName(), attrDefn.getAttributeType(), value,
                    attrDefn.getAssociatedAttribute());
        }
    }

    /**
     * Build an attribute from a known definition
     * 
     * @param attrDefn
     * @param value
     * @return
     */
    public Attribute buildAttributeFromDefn(FlexAttributeDefn attrDefn, String value) {
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
     * @param associatedAttribute
     * @return
     */
    private Attribute createAttribute(String attrName, AttributeType attributeType, String value,
            String associatedAttribute) {
        Attribute attr = new Attribute();
        attr.setAttrType(attributeType);
        attr.setAttrName(attrName);
        attr.setAttrValue(value);
        attr.setAssociatedAttribute(associatedAttribute);
        return attr;
    }

}
