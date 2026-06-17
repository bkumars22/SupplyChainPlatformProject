/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageBusinessEntities;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class ManageBusinessEntitiesResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Business Name")
    private String businessName;

    @DisplayName("Id")
    private String id;

    @DisplayName("Type")
    private String type;

    @DisplayName("Description")
    private String description;

    @DisplayName("Primary Contact")
    private String primaryContact;

    @DisplayName("Primary Contact Email")
    private String primaryContactEmail;

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
     * @return the primaryContact
     */
    public String getPrimaryContact() {
        return primaryContact;
    }

    /**
     * @param primaryContact
     *            the primaryContact to set
     */
    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    /**
     * @return the primaryContactEmail
     */
    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    /**
     * @param primaryContactEmail
     *            the primaryContactEmail to set
     */
    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

}
