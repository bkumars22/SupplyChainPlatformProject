/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageBusinessEntities.details;

import java.util.List;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class BusinessDetailsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Id")
    private String id;

    @DisplayName("Business Name")
    private String businessName;

    @DisplayName("External Id")
    private String externalId;

    @DisplayName("Type")
    private String type;

    @DisplayName("Description")
    private String description;

    @DisplayName("Alternate Names")
    private List<String> alternateNames;

    @DisplayName("Currencies")
    private List<String> currencies;

    @DisplayName("Sites")
    private List<String> sites;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the businessName
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * @param businessName
     *            the businessName to set
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /**
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId
     *            the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the alternateNames
     */
    public List<String> getAlternateNames() {
        return alternateNames;
    }

    /**
     * @param alternateNames
     *            the alternateNames to set
     */
    public void setAlternateNames(List<String> alternateNames) {
        this.alternateNames = alternateNames;
    }

    /**
     * @return the currencies
     */
    public List<String> getCurrencies() {
        return currencies;
    }

    /**
     * @param currencies
     *            the currencies to set
     */
    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }

    /**
     * @return the sites
     */
    public List<String> getSites() {
        return sites;
    }

    /**
     * @param sites
     *            the sites to set
     */
    public void setSites(List<String> sites) {
        this.sites = sites;
    }

}
