/**
 *	PcmCostRecordValueType.java
 *	Created on Nov 22, 2011
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
package com.scplatform.pcm.cost.enums;

/**
 * Enum for Cost Record Value types
 * 
 * @author sgupta
 */
public enum PcmCostRecordValueType {
    /**
     * Simple cost record values
     */
    S,
    /**
     * Blended cost record values 
     */ 
    B,
    /** Percentage of all the total of all
     * {@link PcmCostElementType#MATERIAL} type cost elements. Values are treated as percentages
     * between 0 and 100
     */
    PM,
    /**
     * Percentage of all the total of all {@link PcmCostElementType#TRANSFORMATION} type cost
     * elements.Values are treated as percentages between 0 and 100.
     */
    PT,
    /**
     * Percentage of all the total of all {@link PcmCostElementType#FIXED} type cost
     * elements.Values are treated as percentages between 0 and 100.
     */
    PF,
    /**
     * Percentage of all the total of all {@link PcmCostElementType#REBATE} type cost
     * elements.Values are treated as percentages between 0 and 100.
     */
    PR,
    /** 1) If Assembly, Percentage of all the total of all
     * MATERIAL and TRANSFORMATION type cost elements of the child cost records.
     * 2) If Component, Percentage of all the total of all
     * {@link PcmCostElementType#MATERIAL} type cost elements.
     * Values are treated as percentages between 0 and 100
     */
    P,
    /**
     * Custom value type. See {@link CustomCostElementEvaluator}
     */
    C;
}
