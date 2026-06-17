/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.common.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * Generalized class for attributes of other class instances. 
 * The general notion of an attribute contains a name and value. In addition
 * Attribute provides a group and type.
 * 
 */
@Embeddable
@SuppressWarnings("serial")
public class Attribute implements Serializable
{
        // Fields
        @ManyToOne
        @JoinColumn(name = "ATTRIBUTE_GROUP_KEY", nullable = false)
        private AttributeGroup attrGroup;

        @Enumerated(EnumType.STRING)
        @Column(name = "ATTRIBUTE_TYPE", nullable = false, length = 50)
        private AttributeType attrType = AttributeType.STRING;

        @Column(name = "ATTRIBUTE_NAME", length = 50, nullable = false)
        private String attrName;

        @Column(name = "DESCRIPTION")
        private String description;

        @Column(name = "ATTRIBUTE_VALUE")
        private String attrStrValue;

        @Column(name = "ATTRIBUTE_VALUE_NUM", scale = 4)
        private BigDecimal attrNumValue;

        @Column(name = "ATTRIBUTE_VALUE_DT")
        private Date attrDateValue;

        @Transient
        private String associatedAttribute;

        @Transient
        private boolean isRequired = false;

        // Constructors
        /** default constructor */
        public Attribute()
        {
        }

        /**
         * 
         * Return the attributes group name
         * 
         */
        public AttributeGroup getAttrGroup()
        {
                return this.attrGroup;
        }

        /**
         * 
         * Set the attribute group name
         * 
         * @param AttrGroup
         *            name of the group
         * 
         */
        public void setAttrGroup(AttributeGroup AttrGroup)
        {
                this.attrGroup = AttrGroup;
        }

        /**
         * @return the attrType
         */
        public AttributeType getAttrType() {
            return this.attrType;
        }
        
        /**
         * @param attrType the attrType to set
         */
        public void setAttrType(AttributeType attrType) {
            this.attrType = attrType;
        }

        /**
         * 
         * Returns the attributes value
         * 
         */
    public Object getAttrValue() {
        if (this.attrType == null) {
            throw new IllegalStateException("Attribute type is not set. Cannot get attribute value");
        }
        return attrType.getAttributeValue(this);
    }
    
    public String getAttributeValueAsString() {
        if (this.attrType == null) {
            throw new IllegalStateException("Attribute type is not set. Cannot get attribute value");
        }
        return attrType.getAttributeValueAsString(this);
    }

    /**
     * 
     * Set the attribute value.
     * 
     * @param attrValue
     * 
     */
    public void setAttrValue(String attrValue) {
        if (this.attrType == null) {
            throw new IllegalStateException("Attribute type is not set. Cannot set attribute value");
        }
        this.attrType.updateAttributeUsingStringValue(this, attrValue);
    }

        /**
         * Set the attribute's named
         * @param attrName The attrName to set.
         * 
         */
        public void setAttrName(String attrName)
        {
                this.attrName = attrName;
        }

        /**
         * Gets the attribute's name
         * @return Returns the name.
         * 
         */
        public String getAttrName()
        {
                return attrName;
        }

        /**
         * Set the attribute's description
         * @param description
         *            The description to set.
         * 
         */
        public void setDescription(String description)
        {
                this.description = description;
        }

        /**
         * Get the attribute's description
         * @return Returns the description.
         * 
         */
        public String getDescription()
        {
                return description;
        }
        
        
    public String getAttrStrValue() {
        return this.attrStrValue;
    }

    protected void setAttrStrValue(String attrStrValue) {
        this.attrStrValue = attrStrValue;
    }

    protected BigDecimal getAttrNumValue() {
        return this.attrNumValue;
    }

    protected void setAttrNumValue(BigDecimal attrNumValue) {
        this.attrNumValue = attrNumValue;
    }
     /**
     * @return the attrDateValue
     */
    protected Date getAttrDateValue() {
        return this.attrDateValue;
    }
    
    /**
     * @param attrDateValue the attrDateValue to set
     */
    protected void setAttrDateValue(Date attrDateValue) {
        this.attrDateValue = attrDateValue;
    }
    
    /**
     * @return the associatedAttribute
     */
    public String getAssociatedAttribute() {
        return associatedAttribute;
    }

    /**
     * @param associatedAttribute the associatedAttribute to set
     */
    public void setAssociatedAttribute(String associatedAttribute) {
        this.associatedAttribute = associatedAttribute;
    }

    /**
     * @return the isRequired
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * @param isRequired the isRequired to set
     */
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    void resetInternalValues() {
        this.attrNumValue = null;
        this.attrStrValue = null;
        this.attrDateValue = null;
    }
    
    public ObjectNode getAttributesNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("attributeName",this.attrName);
        o.put("attributeValue",om.getNodeFactory().pojoNode(this.getAttrValue()));
		ObjectNode attributeGroupAsJSON = this.attrGroup.getAttributeGroupNaturalKeyAsJSON();
		o.put("attributeGroupName", attributeGroupAsJSON);
        return o;
    }
    
    /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         * 
         */
        @Override
    public boolean equals(Object other)
        {
                if ((this == other))
                        return true;
                if ((other == null))
                        return false;
                if (!(other instanceof Attribute))
                        return false;
                Attribute castOther = (Attribute) other;
                EqualsBuilder eb = new EqualsBuilder();
                eb.append(getAttrGroup(), castOther.getAttrGroup());            
                eb.append(getAttrName(), castOther.getAttrName());              
                eb.append(getAttrType(), castOther.getAttrType());
                return eb.isEquals();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         * 
         */
        @Override
    public int hashCode()
        {
                return new HashCodeBuilder(17, 37)
                        .append(getAttrGroup())
                        .append(getAttrName()).toHashCode();
        }

}