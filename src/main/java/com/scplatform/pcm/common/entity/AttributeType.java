/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.common.entity;

import org.apache.commons.lang3.StringUtils;

import com.scplatform.pcm.util.datetime.ISO8601;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Models an attribute type
 */
@SuppressWarnings("unchecked")
public enum AttributeType {
    STRING {
        @Override
        protected String getObjectFromString(String val) {
            return val;
        }

        @Override
        protected void setAttributeValueFromString(Attribute attr, String value) {
            attr.setAttrStrValue(value);
        }

        @Override
        protected String getValuefromAttribute(Attribute attr) {
            return attr.getAttrStrValue();
        }
    },
    DATE {
        @Override
        protected Date getObjectFromString(String val) {
        	Date d = null;
        	try {
        		d = ISO8601.parse(val).getTime();
        		if(d == null) {
            		val = val+"T00:00:00";
            		d = ISO8601.parse(val).getTime();
            	}
        	}catch (NullPointerException e) {
        		throw new NullPointerException("Incorrect Date format, please provide date in yyyy-MM-dd'T'HH:mm:ss format");
			}
        	return d;
        }

        @Override
        protected void setAttributeValueFromString(Attribute attr, String value) {
            attr.setAttrDateValue(StringUtils.stripToNull(value) == null ? null : getObjectFromString(value));
        }

        @Override
        protected Date getValuefromAttribute(Attribute attr) {
            return attr.getAttrDateValue();
        } 
        
        /* (non-Javadoc)
         * @see com.scplatform.repository.bom.domain.common.AttributeType#getAttributeValueAsString(com.scplatform.repository.bom.domain.common.Attribute)
         */
        @Override
        public String getAttributeValueAsString(Attribute attr) {
            Date dt = getAttributeValue(attr);
            return ISO8601.safeFormat(dt);
        }
    },
    INTEGER {
        @Override
        protected Integer getObjectFromString(String val) {
            return new Integer(val);
        }

        @Override
        protected void setAttributeValueFromString(Attribute attr, String value) {
            attr.setAttrNumValue(StringUtils.stripToNull(value) == null ? null : new BigDecimal(getObjectFromString(value)));
        }

        @Override
        protected Integer getValuefromAttribute(Attribute attr) {
            BigDecimal attrNumValue = attr.getAttrNumValue();
            return attrNumValue == null ? null : attrNumValue.intValue();
        }
    },
    FLOAT {
        @Override
        protected BigDecimal getObjectFromString(String val) {
            return new BigDecimal(val);
        }

        @Override
        protected void setAttributeValueFromString(Attribute attr, String value) {
            attr.setAttrNumValue(StringUtils.stripToNull(value) == null ? null : getObjectFromString(value));
        }

        @Override
        protected BigDecimal getValuefromAttribute(Attribute attr) {
            return attr.getAttrNumValue();
        }
    },
    BOOLEAN {
        
        @Override
        protected Boolean getObjectFromString(String val) {
            return Boolean.valueOf(val);
        }

        @Override
        protected void setAttributeValueFromString(Attribute attr, String value) {
            attr.setAttrStrValue(value == null ? null : getObjectFromString(value).toString());
        }


        @Override
        protected Boolean getValuefromAttribute(Attribute attr) {
            return Boolean.parseBoolean(attr.getAttrStrValue());
        }
    };

    protected abstract <T> T getObjectFromString(String val);
    
    protected abstract void setAttributeValueFromString(Attribute attr, String value);

    public void updateAttributeUsingStringValue(Attribute attr,String value) {
        attr.setAttrType(this); // For consistency
        attr.resetInternalValues();
        if (StringUtils.isNotBlank(value)) {
            setAttributeValueFromString(attr, value);
        }
    }
    
    public <T> T getAttributeValue(Attribute attr) {
        if (attr == null ) return null;
        return getValuefromAttribute(attr);
    }
    
    protected abstract <T> T getValuefromAttribute(Attribute attr);
    
    /**
     * Get Attribute value as string
     * 
     * @param attr
     * @return
     */
    public String getAttributeValueAsString(Attribute attr) {
        return this.getAttributeValue(attr).toString();
    }
}