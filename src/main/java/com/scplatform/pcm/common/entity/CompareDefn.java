/**
 *      CompareDefn.java
 *      Created on April 14, 2014
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
package com.scplatform.pcm.common.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines criteria to compare
 * 
 * @author manderson
 */
public class CompareDefn {

    public List<String> uniqueCriteria = new ArrayList<String>();
    public List<String> compareCriteria = new ArrayList<String>();
    public List<String> sortCriteria = new ArrayList<String>();
    
    /**
     * @return the uniqueCriteria
     */
    public List<String> getUniqueCriteria() {
        return uniqueCriteria;
    }
    
    /**
     * @param uniqueCriteria the uniqueCriteria to set
     */
    public void setUniqueCriteria(List<String> uniqueCriteria) {
        this.uniqueCriteria = uniqueCriteria;
    }
    
    /**
     * @return the compareCriteria
     */
    public List<String> getCompareCriteria() {
        return compareCriteria;
    }
    
    /**
     * @param compareCriteria the compareCriteria to set
     */
    public void setCompareCriteria(List<String> compareCriteria) {
        this.compareCriteria = compareCriteria;
    }

    /**
     * @return the sortCriteria
     */
    public List<String> getSortCriteria() {
        return sortCriteria;
    }

    /**
     * @param sortCriteria the sortCriteria to set
     */
    public void setSortCriteria(List<String> sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

}
