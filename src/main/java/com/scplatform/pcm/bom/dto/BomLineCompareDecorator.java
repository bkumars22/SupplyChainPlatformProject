/**
 *      BomLineCompareDecorator.java
 *      Created on Apr 24, 2014
 *     
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code. 
 *      
 *      Author: manderson
 */

package com.scplatform.pcm.bom.dto;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.common.entity.CompareDefn;
import com.scplatform.pcm.common.entity.CompareManager;

/**
 * Bom Line Compare Decorator for configurable Bom comparison
 * 
 * @author manderson
 */
@SuppressWarnings("serial")
public class BomLineCompareDecorator extends BomLine {
	protected final static Logger logger = LogManager.getLogger(BomLineCompareDecorator.class);
    private CompareDefn defn = new CompareDefn();
    private BomLine bomLine;

    public BomLineCompareDecorator() {
        initDefn();
    }

    public BomLineCompareDecorator(BomLine bomLine, CompareDefn defn) {
        this.bomLine = bomLine;

        this.defn = defn;
    }

    private void initDefn() {
        defn = CompareManager.BOM.getCompareDefinition();
    }

    /**
     * @return the bomLine
     */
    public BomLine getBomLine() {
        return bomLine;
    }

    /**
     * @param bomLine
     *            the bomLine to set
     */
    public void setBomLine(BomLine bomLine) {
        this.bomLine = bomLine;
    }
    
    @Override
    public int hashCode() {
        HashCodeBuilder hc = new HashCodeBuilder(17, 37);
        if (this.bomLine != null) {
            try {
                for (String criteria : defn.getUniqueCriteria()) {
                    Object thisObj = PropertyUtils.getProperty(this.bomLine, criteria);
                    hc.append(thisObj);
                }
            } catch (Exception e) {
                logger.error("Bom Line Compare failed ", e);
            }
        }
        return hc.toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if ((other == null))
            return false;
        if (!(other instanceof BomLineCompareDecorator))
            return false;
        
        BomLineCompareDecorator castOther = (BomLineCompareDecorator) other;
        
        if(this.bomLine == null)
            return false;
        if(castOther.getBomLine() == null)
            return false;
        
        EqualsBuilder eb = new EqualsBuilder();

        try {
            for (String criteria : defn.getUniqueCriteria()) {
                Object thisObj = PropertyUtils.getProperty(this.bomLine, criteria);
                Object otherObj = PropertyUtils.getProperty(castOther.getBomLine(), criteria);
                eb.append(thisObj, otherObj);
            }
        } catch (Exception e) {
            logger.error("Bom Line Compare failed ", e);
            return false;
        }
        return eb.isEquals();
    }
}
