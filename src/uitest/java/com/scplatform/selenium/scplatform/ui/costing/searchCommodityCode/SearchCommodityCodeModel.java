/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchCommodityCode;

import com.test.selenium.common.modelViewController.model.Model;

public class SearchCommodityCodeModel extends Model {

    private static final long serialVersionUID = 1L;

    private String commodityName;
    private String multipleCommodityNames;
    private String contextType;
    private String contextName;

    /**
     * @return the commodityName
     */
    public String getCommodityName() {
        return commodityName;
    }

    /**
     * @param commodityName
     *            the commodityName to set
     */
    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    /**
     * @return the multipleCommodityNames
     */
    public String getMultipleCommodityNames() {
        return multipleCommodityNames;
    }

    /**
     * @param multipleCommodityNames
     *            the multipleCommodityNames to set
     */
    public void setMultipleCommodityNames(String multipleCommodityNames) {
        this.multipleCommodityNames = multipleCommodityNames;
    }

    /**
     * @return the contextType
     */
    public String getContextType() {
        return contextType;
    }

    /**
     * @param contextType
     *            the contextType to set
     */
    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    /**
     * @return the contextName
     */
    public String getContextName() {
        return contextName;
    }

    /**
     * @param contextName
     *            the contextName to set
     */
    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

}
