/**
 *      CompareManager.java
 *      Created on Apr 10, 2014
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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.config.util.PcmConfigUtil;

/**
 * @author manderson
 */
public enum CompareManager {
    BOM("bom");

    private static final String COMPARE_CONFIG = "pcm.compare.config";

    private String beanName;

    private CompareManager(String beanName) {
        this.beanName = beanName;
    }

    /**
     * Get the CompareDefinition object
     * 
     * @return
     */
    public CompareDefn getCompareDefinition() {
        // Get the configuration file path from PcmConfigUtil via SpringContextHolder
        PcmConfigUtil configUtil = SpringContextHolder.getBean(PcmConfigUtil.class);
        String configFilePath = configUtil.getString(COMPARE_CONFIG);
        
        if (configFilePath == null || configFilePath.trim().isEmpty()) {
            return null;
        }
        
        // Load ApplicationContext from the specified config file
        ApplicationContext ctx = new ClassPathXmlApplicationContext(configFilePath);
        
        CompareDefn defn = null;
        if (ctx.containsBean(this.beanName)) {
            defn = ctx.getBean(this.beanName, CompareDefn.class);
        }
        return defn;
    }

}
