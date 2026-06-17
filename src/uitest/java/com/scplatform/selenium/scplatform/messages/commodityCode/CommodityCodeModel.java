/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.commodityCode;

import com.test.selenium.common.modelViewController.model.Model;

public class CommodityCodeModel extends Model {

    private static final long serialVersionUID = 1L;

    private String commodityCode;
    private String commodityCodeDescription;
    private String parentCommodityCode;
    private String managedBy;
    private String operationCode;

    /**
     * @return the commodityCode
     */
    public String getCommodityCode() {
        return commodityCode;
    }

    /**
     * @param commodityCode
     *            the commodityCode to set
     */
    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    /**
     * @return the commodityCodeDescription
     */
    public String getCommodityCodeDescription() {
        return commodityCodeDescription;
    }

    /**
     * @param commodityCodeDescription
     *            the commodityCodeDescription to set
     */
    public void setCommodityCodeDescription(String commodityCodeDescription) {
        this.commodityCodeDescription = commodityCodeDescription;
    }

    /**
     * @return the parentCommodityCode
     */
    public String getParentCommodityCode() {
        return parentCommodityCode;
    }

    /**
     * @param parentCommodityCode
     *            the parentCommodityCode to set
     */
    public void setParentCommodityCode(String parentCommodityCode) {
        this.parentCommodityCode = parentCommodityCode;
    }

    /**
     * @return the managedBy
     */
    public String getManagedBy() {
        return managedBy;
    }

    /**
     * @param managedBy
     *            the managedBy to set
     */
    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }

    /**
     * @return the operationCode
     */
    public String getOperationCode() {
        return operationCode;
    }

    /**
     * @param operationCode
     *            the operationCode to set
     */
    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

}
