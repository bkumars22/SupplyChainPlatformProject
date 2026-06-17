/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchCostRecords;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.scplatform.utilities.DatabaseUtils;

public class SearchCostRecordsResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Status")
    private String status;

    @DisplayName("Commodity Name")
    private String commodityName;

    @DisplayName("Item")
    private String item;

    @DisplayName("Item Description")
    private String itemDescription;

    @DisplayName("Item Business")
    private String itemBusiness;

    @DisplayName("Supplier")
    private String supplier;

    @DisplayName("Source Site")
    private String sourceSite;

    @DisplayName("Destination Site")
    private String destinationSite;

    @DisplayName("Cost Type")
    private String costType;

    @DisplayName("Pricing Scenario")
    private String pricingScenario;

    @DisplayName("Start Date")
    private String startDate;

    @DisplayName("End Date")
    private String endDate;

    @DisplayName("Currency")
    private String currency;

    @DisplayName("Product State")
    private String productState;

    @DisplayName("Non-Managed Cost Adjustment")
    private String nonManagedCostAdjustment;

    @DisplayName("Lane Name")
    private String laneName;

    @DisplayName("Responsibility")
    private String responsibility;

    @DisplayName("Comment")
    private String comment;

    @DisplayName("Reason Code")
    private String reasonCode;

    private List<Range> range;

    public class Range {
        private float costRecordRange_from;
        private float costRecordRange_to;
        private float costTotal;
        private Map<String, Float> costElementList = null;

        public void setCostElementListToDefaultValues() {
            if (costElementList == null) {
                costElementList = getCostElementListFromDatabase();
            }
        }

        public void setCostElementList(String headerLabel, float value) {
            setCostElementListToDefaultValues();
            costElementList.put(headerLabel, value);
        }

        /**
         * @return the costRecordRange_from
         */
        public float getCostRecordRange_from() {
            return costRecordRange_from;
        }

        /**
         * @param costRecordRange_from
         *            the costRecordRange_from to set
         */
        public void setCostRecordRange_from(float costRecordRange_from) {
            this.costRecordRange_from = costRecordRange_from;
        }

        /**
         * @return the costRecordRange_to
         */
        public float getCostRecordRange_to() {
            return costRecordRange_to;
        }

        /**
         * @param costRecordRange_to
         *            the costRecordRange_to to set
         */
        public void setCostRecordRange_to(float costRecordRange_to) {
            this.costRecordRange_to = costRecordRange_to;
        }

        /**
         * @return the costTotal
         */
        public float getCostTotal() {
            return costTotal;
        }

        /**
         * @param costTotal
         *            the costTotal to set
         */
        public void setCostTotal(float costTotal) {
            this.costTotal = costTotal;
        }

        /**
         * @return the costElementList
         */
        public Map<String, Float> getCostElementList() {
            return costElementList;
        }

        /**
         * @param costElementList
         *            the costElementList to set
         */
        public void setCostElementList(Map<String, Float> costElementList) {
            this.costElementList = costElementList;
        }

    }

    public Map<String, Float> getCostElementListFromDatabase() {
        Map<String, Float> costElementTypes = new HashMap<String, Float>();

        List<String> costElements = DatabaseUtils.getCostElements();

        for (String key : costElements) {
            costElementTypes.put(key, NullValue.FLOAT);
        }

        return costElementTypes;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

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
     * @return the item
     */
    public String getItem() {
        return item;
    }

    /**
     * @param item
     *            the item to set
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * @return the itemDescription
     */
    public String getItemDescription() {
        return itemDescription;
    }

    /**
     * @param itemDescription
     *            the itemDescription to set
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    /**
     * @return the itemBusiness
     */
    public String getItemBusiness() {
        return itemBusiness;
    }

    /**
     * @param itemBusiness
     *            the itemBusiness to set
     */
    public void setItemBusiness(String itemBusiness) {
        this.itemBusiness = itemBusiness;
    }

    /**
     * @return the supplier
     */
    public String getSupplier() {
        return supplier;
    }

    /**
     * @param supplier
     *            the supplier to set
     */
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    /**
     * @return the sourceSite
     */
    public String getSourceSite() {
        return sourceSite;
    }

    /**
     * @param sourceSite
     *            the sourceSite to set
     */
    public void setSourceSite(String sourceSite) {
        this.sourceSite = sourceSite;
    }

    /**
     * @return the destinationSite
     */
    public String getDestinationSite() {
        return destinationSite;
    }

    /**
     * @param destinationSite
     *            the destinationSite to set
     */
    public void setDestinationSite(String destinationSite) {
        this.destinationSite = destinationSite;
    }

    /**
     * @return the costType
     */
    public String getCostType() {
        return costType;
    }

    /**
     * @param costType
     *            the costType to set
     */
    public void setCostType(String costType) {
        this.costType = costType;
    }

    /**
     * @return the pricingScenario
     */
    public String getPricingScenario() {
        return pricingScenario;
    }

    /**
     * @param pricingScenario
     *            the pricingScenario to set
     */
    public void setPricingScenario(String pricingScenario) {
        this.pricingScenario = pricingScenario;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency
     *            the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the productState
     */
    public String getProductState() {
        return productState;
    }

    /**
     * @param productState
     *            the productState to set
     */
    public void setProductState(String productState) {
        this.productState = productState;
    }

    /**
     * @return the nonManagedCostAdjustment
     */
    public String getNonManagedCostAdjustment() {
        return nonManagedCostAdjustment;
    }

    /**
     * @param nonManagedCostAdjustment
     *            the nonManagedCostAdjustment to set
     */
    public void setNonManagedCostAdjustment(String nonManagedCostAdjustment) {
        this.nonManagedCostAdjustment = nonManagedCostAdjustment;
    }

    /**
     * @return the laneName
     */
    public String getLaneName() {
        return laneName;
    }

    /**
     * @param laneName
     *            the laneName to set
     */
    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    /**
     * @return the responsibility
     */
    public String getResponsibility() {
        return responsibility;
    }

    /**
     * @param responsibility
     *            the responsibility to set
     */
    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the reasonCode
     */
    public String getReasonCode() {
        return reasonCode;
    }

    /**
     * @param reasonCode
     *            the reasonCode to set
     */
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * @return the range
     */
    public List<Range> getRange() {
        return range;
    }

    /**
     * @param range
     *            the range to set
     */
    public void setRange(List<Range> range) {
        this.range = range;
    }

}
